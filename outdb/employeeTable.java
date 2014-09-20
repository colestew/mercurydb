package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.employee;

public class employeeTable {
    public static final List<employee> table = new ArrayList<>();
    
    public static final Class<employee> containedClass = employee.class;

    // Maps for indexed fields
    public static void insert(employee val) {
        // Populate standard table if T(val) == employee
        if (employee.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(employee val) {
    	// Remove from table
    	if (employee.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setEno(employee instance, int val) {
        instance.eno = val;
    }

    public static void setEname(employee instance, java.lang.String val) {
        instance.ename = val;
    }

    public static void setZip(employee instance, tests.target.zipcode val) {
        instance.zip = val;
    }

    public static void setDate(employee instance, java.lang.String val) {
        instance.date = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<employee> queryEno(int val) {
        return scan().filter(fieldEno(),val);
    }

    public static Stream<employee> queryEname(java.lang.String val) {
        return scan().filter(fieldEname(),val);
    }

    public static Stream<employee> queryZip(tests.target.zipcode val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<employee> queryDate(java.lang.String val) {
        return scan().filter(fieldDate(),val);
    }

    public static Stream<employee>
    queryDateZip(java.lang.String date, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryEnoZip(Integer eno, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryDateEno(java.lang.String date, Integer eno) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<employee>
    queryDateEnoZip(java.lang.String date, Integer eno, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryEnameZip(java.lang.String ename, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryDateEname(java.lang.String date, java.lang.String ename) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        return result;
    }

    public static Stream<employee>
    queryDateEnameZip(java.lang.String date, java.lang.String ename, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryEnameEno(java.lang.String ename, Integer eno) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<employee>
    queryEnameEnoZip(java.lang.String ename, Integer eno, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<employee>
    queryDateEnameEno(java.lang.String date, java.lang.String ename, Integer eno) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<employee>
    queryDateEnameEnoZip(java.lang.String date, java.lang.String ename, Integer eno, tests.target.zipcode zip) {
        Iterable<employee> seed = table;
        int size = table.size();

        Stream<employee> result = new Retrieval<employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }


    public static FieldExtractable fieldEno() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((employee)instance).eno;
            }

            @Override
            public Class<?> getContainerClass() {
                return employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldEname() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((employee)instance).ename;
            }

            @Override
            public Class<?> getContainerClass() {
                return employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldZip() {
        return new FieldExtractable() {
            @Override
            public tests.target.zipcode extractField(Object instance) {
                return ((employee)instance).zip;
            }

            @Override
            public Class<?> getContainerClass() {
                return employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldDate() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((employee)instance).date;
            }

            @Override
            public Class<?> getContainerClass() {
                return employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }


    public static FieldExtractable itself() {
        return new FieldExtractable() {
            @Override
            public Object extractField(Object instance) {
                return instance;
            }

            @Override
            public Class<?> getContainerClass() {
                return employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<employee> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<employee> joinOnFieldEno() {
        return scan().joinOn(fieldEno());
    }

    public static JoinStream<employee> joinOnFieldEname() {
        return scan().joinOn(fieldEname());
    }

    public static JoinStream<employee> joinOnFieldZip() {
        return scan().joinOn(fieldZip());
    }

    public static JoinStream<employee> joinOnFieldDate() {
        return scan().joinOn(fieldDate());
    }


    public static JoinStream<employee> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<employee> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<employee> result = new Retrieval<employee>(table, table.size());
        return result;
    }
}
