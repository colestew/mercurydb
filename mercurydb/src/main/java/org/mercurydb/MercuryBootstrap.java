package org.mercurydb;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.mercurydb.queryutils.HgPredicate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MercuryBootstrap {
    /**
     * Predicate for classes which can be properly
     * mapped to output tables.
     */
    private static HgPredicate<Class<?>> supportedClassCheck = new HgPredicate<Class<?>>() {
        @Override
        public boolean test(Class<?> cls) {
            return !cls.isMemberClass()
                    && !cls.isLocalClass()
                    && !cls.isAnonymousClass();
        }
    };
    // TODO remove use of lambdas above so we're compatible with 1.7

    /**
     * source directory for classes
     */
    private final String _srcPackage;

    /**
     * Output package for generated code
     */
    private final String _outPackage;

    /**
     * output directory for tables
     */
    private final String _srcJavaDir;

    /**
     * Output table class suffix
     */
    private String tableSuffix = "Table";

    /**
     * Primary constructor for MercuryBootstrap.
     *
     * @param srcPackage the input package for client code
     * @param outPackage the output root package for generated code
     * @param rootDir    the root directory for java files
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    public MercuryBootstrap(String srcPackage, String outPackage, String rootDir)
            throws NotFoundException, CannotCompileException {
        this._srcPackage = srcPackage;
        this._outPackage = outPackage;
        this._srcJavaDir = rootDir;
    }

    /**
     * Sets the table suffix for generated tables. i.e. Customer
     * maps to CustomerTable. Default suffix is "Table"
     *
     * @param suffix the new table suffix
     */
    public void setTableSuffix(String suffix) {
        this.tableSuffix = suffix;
    }

    /**
     * Retrieves all classes that can be converted into table classes
     * by this tool.
     *
     * @return supported classes
     */
    public Collection<Class<?>> getSupportedClasses() {
        // Fetch appropriate class files
        Collection<Class<?>> classes = Collections.emptyList();
        try {
            classes = Arrays.asList(Utils.getClasses(_srcPackage));
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException in getSupportedClasses()");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException in getSupportedClasses()");
            e.printStackTrace();
            System.exit(1);
        }

        // filter classes so we only have supported class files
        Collection<Class<?>> filteredClasses = new ArrayList<Class<?>>(classes.size());
        for (Class<?> c : classes) {
            if (c != null && supportedClassCheck.test(c)) {
                filteredClasses.add(c);
            }
        }

        return filteredClasses;
    }

    /**
     * Performs the bootstrap operation. This is everything. The
     * cat's meow. Namely, it fetches all class files in the
     * source directory and converts them into class objects. Then
     */
    public void generateTables() {
        // Fetch appropriate class files
        Collection<Class<?>> classes = getSupportedClasses();

        // if no classes are supported exit
        if (classes.isEmpty()) {
            System.out.println("No supported .class files found in " + _srcPackage);
            System.exit(1);
        }

        String basePath = _srcJavaDir + '/' + _outPackage.replace('.', '/');

        // startup a collection of table files we generate
        Collection<String> tableFiles = new ArrayList<String>(); // TODO warning: never queried - is this a mistake?
        // and create a map of input package classes to their subclasses
        Map<Class<?>, List<Class<?>>> subClassMap = getSubclasses(classes);

        // join id for index into streams
        int joinId = 0;

        // now iterate over each class and generate the tables
        for (Class<?> cls : classes) {

            // fetch the subclass table names
            Collection<String> subTables = new ArrayList<String>();
            if (subClassMap.containsKey(cls)) {
                for (Class<?> subclass : subClassMap.get(cls)) {
                    subTables.add(toOutPackage(subclass.getName()));
                }
            }

            // calculate required paths and packages for the new table
            String genTablePrefix = basePath + cls.getName().replace(_srcPackage, "").replace('.', '/');
            String tablePath = genTablePrefix + tableSuffix + ".java";
            String tablePackage = _outPackage + cls.getPackage().getName().replace(_srcPackage, "");
            tableFiles.add(genTablePrefix + tableSuffix + ".java");

            System.out.println("Extracting " + cls + " to " + tablePath + " in " + tablePackage);

            String superTable = subClassMap.containsKey(
                    cls.getSuperclass()) ? toOutPackage(cls.getSuperclass().getName()) : null;

            ClassToTableExtractor extractor;
            try {
                extractor = new ClassToTableExtractor(cls, superTable, subTables, tableSuffix, joinId++);
                extractor.extract(tablePath, tablePackage);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Inserts bytecode hooks in the classes found in the input package
     */
    public void insertBytecodeHooks() {
        Collection<Class<?>> classes = getSupportedClasses();
        ClassPool cp = ClassPool.getDefault();

        // Modify original bytecode with the insert hooks
        for (Class<?> cls : classes) {
            System.out.println("Adding insert hook to " + cls);
            try {
                CtClass ctCls = cp.get(cls.getName());
                BytecodeModifier modifier = new BytecodeModifier(ctCls, toOutPackage(cls.getName()) + tableSuffix);
                modifier.modify();
            } catch (NotFoundException e) {
                System.err.println("NotFoundException in MercuryBootstrap.insertBytecodeHooks()");
                e.printStackTrace();
            } catch (CannotCompileException e) {
                System.err.println("CannotCompileException in MercuryBootstrap.insertBytecodeHooks()");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IOException in MercuryBootstrap.insertBytecodeHooks()");
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts a class to an output package name. To be more specific,
     * this method converts the class to a filename, replaces the source
     * directory with the out directory, and converts that path to a package
     * name. It is up to the template engine to append "Table" or whatever
     * it wants to use for the table class names.
     *
     * @param c input String
     * @return output package name
     */
    private String toOutPackage(String c) {
        String filePrefix = _outPackage + c.replace(_srcPackage, "");
        return filePrefix;
    }

    /**
     * Method which returns a map of each class in the given collection
     * to immediate subclasses of that classes that are also in the given collection.
     *
     * @param classes the collection of classes as restriction of i/o
     * @return map of each class to its immediate subclasses
     */
    private Map<Class<?>, List<Class<?>>> getSubclasses(Collection<Class<?>> classes) {
        Map<Class<?>, List<Class<?>>> subclassMap = new HashMap<Class<?>, List<Class<?>>>();

        for (Class<?> c : classes) {
            // Determine if superclass has a mapped table class
            Class<?> currC = c;

            if (classes.contains(currC.getSuperclass())) {
                // Now if this subclass is supported put it in the map
                List<Class<?>> subClasses = subclassMap.get(currC.getSuperclass());
                if (subClasses == null) {
                    subClasses = new ArrayList<Class<?>>();
                    subclassMap.put(currC.getSuperclass(), subClasses);
                }

                subClasses.add(c);
            }
        }

        return subclassMap;
    }
}
