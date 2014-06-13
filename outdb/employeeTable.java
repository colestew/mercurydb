package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import javadb.tests.target.employee;

public class employeeTable {
    public static Set<employee> table = new HashSet<>();
        //Collections.newSetFromMap(new WeakHashMap<employee, Boolean>());

    // Maps for indexed fields
    public static void insert(employee val) {
        // Populate standard table
        table.add(val);
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setEno(employee instance, int val) {
        instance.eno = val;
    }

    public static void setEname(employee instance, java.lang.String val) {
        instance.ename = val;
    }

    public static void setZip(employee instance, javadb.tests.target.zipcode val) {
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

    public static Stream<employee> queryZip(javadb.tests.target.zipcode val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<employee> queryDate(java.lang.String val) {
        return scan().filter(fieldDate(),val);
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
    queryEnoZip(Integer eno, javadb.tests.target.zipcode zip) {
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
    queryDateZip(java.lang.String date, javadb.tests.target.zipcode zip) {
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
    queryDateEnoZip(java.lang.String date, Integer eno, javadb.tests.target.zipcode zip) {
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
    queryEnameZip(java.lang.String ename, javadb.tests.target.zipcode zip) {
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
    queryEnameEnoZip(java.lang.String ename, Integer eno, javadb.tests.target.zipcode zip) {
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
    queryDateEnameZip(java.lang.String date, java.lang.String ename, javadb.tests.target.zipcode zip) {
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
    queryDateEnameEnoZip(java.lang.String date, java.lang.String ename, Integer eno, javadb.tests.target.zipcode zip) {
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
            public javadb.tests.target.zipcode extractField(Object instance) {
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

    public static Stream<employee> scan() {
        return new Retrieval<employee>(table, table.size());
    }
}
