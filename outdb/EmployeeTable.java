package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.Employee;

public class EmployeeTable {
    public static final List<Employee> table = new ArrayList<>();
    
    public static final Class<Employee> containedClass = Employee.class;

    // Maps for indexed fields
    public static void insert(Employee val) {
        // Populate standard table if T(val) == Employee
        if (Employee.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(Employee val) {
    	// Remove from table
    	if (Employee.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setEno(Employee instance, int val) {
        instance.eno = val;
    }

    public static void setEname(Employee instance, java.lang.String val) {
        instance.ename = val;
    }

    public static void setZip(Employee instance, tests.target.zipcode val) {
        instance.zip = val;
    }

    public static void setDate(Employee instance, java.lang.String val) {
        instance.date = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Employee> queryEno(int val) {
        return scan().filter(fieldEno(),val);
    }

    public static Stream<Employee> queryEname(java.lang.String val) {
        return scan().filter(fieldEname(),val);
    }

    public static Stream<Employee> queryZip(tests.target.zipcode val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<Employee> queryDate(java.lang.String val) {
        return scan().filter(fieldDate(),val);
    }

    public static Stream<Employee>
    queryEnoZip(Integer eno, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryDateEno(java.lang.String date, Integer eno) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<Employee>
    queryDateZip(java.lang.String date, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryDateEnoZip(java.lang.String date, Integer eno, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryEnameEno(java.lang.String ename, Integer eno) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<Employee>
    queryEnameZip(java.lang.String ename, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryEnameEnoZip(java.lang.String ename, Integer eno, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryDateEname(java.lang.String date, java.lang.String ename) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        return result;
    }

    public static Stream<Employee>
    queryDateEnameEno(java.lang.String date, java.lang.String ename, Integer eno) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<Employee>
    queryDateEnameZip(java.lang.String date, java.lang.String ename, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

        // Filter date
        result = result.filter(fieldDate(),date);

        // Filter ename
        result = result.filter(fieldEname(),ename);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<Employee>
    queryDateEnameEnoZip(java.lang.String date, java.lang.String ename, Integer eno, tests.target.zipcode zip) {
        Iterable<Employee> seed = table;
        int size = table.size();

        Stream<Employee> result = new Retrieval<Employee>(seed, size);

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
                return ((Employee)instance).eno;
            }

            @Override
            public Class<?> getContainerClass() {
                return Employee.class;
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
                return ((Employee)instance).ename;
            }

            @Override
            public Class<?> getContainerClass() {
                return Employee.class;
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
                return ((Employee)instance).zip;
            }

            @Override
            public Class<?> getContainerClass() {
                return Employee.class;
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
                return ((Employee)instance).date;
            }

            @Override
            public Class<?> getContainerClass() {
                return Employee.class;
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
                return Employee.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Employee> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<Employee> joinOnFieldEno() {
        return scan().joinOn(fieldEno());
    }

    public static JoinStream<Employee> joinOnFieldEname() {
        return scan().joinOn(fieldEname());
    }

    public static JoinStream<Employee> joinOnFieldZip() {
        return scan().joinOn(fieldZip());
    }

    public static JoinStream<Employee> joinOnFieldDate() {
        return scan().joinOn(fieldDate());
    }


    public static JoinStream<Employee> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Employee> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Employee> result = new Retrieval<Employee>(table, table.size());
        return result;
    }
}
