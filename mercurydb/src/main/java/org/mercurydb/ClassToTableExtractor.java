package org.mercurydb;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mercurydb.annotations.HgIndexStyle;
import org.mercurydb.annotations.HgValue;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class ClassToTableExtractor {
    public final Class<?> c;

    public final boolean hasSuper;

    public final String cSuper;

    public Collection<String> subClasses;

    public List<ValueData> values;

    public String packageName;

    public String tableSuffix;

    public List<ConstructorData> constructors;

    public int joinId;

    public ClassToTableExtractor(
            Class<?> c, String superTable, Collection<String> subClassTables, String tableSuffix, int joinId)
            throws IOException {
        this.c = c;
        this.hasSuper = superTable != null;
        this.cSuper = superTable;
        this.tableSuffix = tableSuffix;
        this.joinId = joinId;

        this.values = new ArrayList<>();
        this.constructors = new ArrayList<>();

        this.subClasses = subClassTables;
        populateValuesList();
        //populateConstructorsList();
    }

    private void populateValuesList() {
        Set<String> seenValues = new HashSet<>();
        for (Method m : c.getMethods())  {
            HgValue value = getValueAnnotation(m);

            if (value != null) {
                if (seenValues.contains(value.value())) {
                    throw new IllegalStateException(
                            String.format("Cannot apply @HgValue(\"%s\") on more than one method.", value.value()));
                }

                if (m.getParameterCount() > 0) {
                    throw new IllegalStateException(
                            String.format("Cannot apply @HgValue(\"%s\") on method with non-zero number of parameters: %s",
                                    value.value(), m.getName()));
                }

                seenValues.add(value.value());
                values.add(new ValueData(value, m));
            }
        }
    }

    // TODO unused for now, but should be used later in conjunction with ConstructorData
    @SuppressWarnings("unused")
    private void populateConstructorsList() {
        for (Constructor<?> con : c.getConstructors()) {
            constructors.add(new ConstructorData(con));
        }
    }

    private static class ConstructorData {
        public ConstructorData(Constructor<?> con) {
            // TODO implement
            throw new NotImplementedException();
//            for (Parameter p : con.getParameters()) {
//                System.out.println(p.getParameterizedType().getTypeName() + " " + p.getName());
//            }
        }
    }

    // TODO if the `queries` field is unnecessary, then this class may be unnecessary as well
    private static class QueryData {
        TreeSet<ValueData> qFields;

        QueryData(Collection<ValueData> fields) {
            this.qFields = new TreeSet<>(fields);
        }

        @SuppressWarnings("unused") // used in template
        String prototype() {
            StringBuilder result = new StringBuilder();
            for (ValueData fd : qFields) {
                result.append(String.format("%s %s, ", fd.type(), fd.name));
            }
            return result.substring(0, result.length() - 2);
        }

        @SuppressWarnings("unused") // used in template
        boolean hasIndex() {
            for (ValueData fd : qFields) {
                if (fd.hasIndex) return true;
            }
            return false;
        }
    }

    private static class ValueData implements Comparable<ValueData> {
        Method valueMethod;
        String hgValueMethod;
        String rawType;
        String name;
        Type valueType;

        boolean hasIndex;
        boolean isOrdered;

        ValueData(HgValue value, Method valueMethod) {
            this.valueMethod = valueMethod;
            hgValueMethod = valueMethod.getName() + "()";
            valueType = valueMethod.getGenericReturnType();
            rawType = valueType.getTypeName();
            name = value.value();

            // fetch HgIndex annotation
            if (value.index() != HgIndexStyle.UNINDEXED) {
                hasIndex = true;
                isOrdered = value.index() == HgIndexStyle.ORDERED &&
                        Comparable.class.isAssignableFrom(valueMethod.getReturnType());
            }
        }

        String type() {
            switch (rawType) {
                case "boolean":
                    return Boolean.class.getName();
                case "byte":
                    return Byte.class.getName();
                case "char":
                    return Character.class.getName();
                case "short":
                    return Short.class.getName();
                case "int":
                    return Integer.class.getName();
                case "long":
                    return Long.class.getName();
                case "float":
                    return Float.class.getName();
                case "double":
                    return Double.class.getName();
                default:
                    return rawType;
            }
        }

        @SuppressWarnings("unused") // used in template.java.mustache
        String CCname() {
            return Utils.upperFirst(name);
        }

        @Override
        @SuppressWarnings("NullableProblems") // parameter "o" can be null or non-null
        public int compareTo(ValueData o) {
            if (o == null) {
                return 1; // sort nulls last
            }

            return name.compareTo(o.name);
        }
    }

    public String fullSourceClass() {
        return c.getName();
    }

    public String sourceClass() {
        return c.getSimpleName();
    }

    public void extract(String outPath, String packageName) throws IOException {
        File outFile = new File(outPath);

        //ignore return value because true or false are both success AFAIC
        //noinspection ResultOfMethodCallIgnored
        outFile.getParentFile().mkdirs();

        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
        this.packageName = packageName;
        extract(w);
        w.close();
    }

    private void extract(PrintWriter w) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        String templateName = "template.java.mustache";
        InputStream templateStream = getClass().getResourceAsStream('/' + templateName);
        Mustache template = mf.compile(new InputStreamReader(templateStream), templateName);
        Writer execute = template.execute(w, this);
        execute.flush();
    }

    private static HgValue getValueAnnotation(Method m) {
        return m.getAnnotation(HgValue.class);
    }
}
