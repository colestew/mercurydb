package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.zipcode;

public class zipcodeTable {
    public static final List<zipcode> table = new ArrayList<>();
    
    public static final Class<zipcode> containedClass = zipcode.class;

    // Maps for indexed fields
    public static void insert(zipcode val) {
        // Populate standard table if T(val) == zipcode
        if (zipcode.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(zipcode val) {
    	// Remove from table
    	if (zipcode.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setZip(zipcode instance, int val) {
        instance.zip = val;
    }

    public static void setCity(zipcode instance, java.lang.String val) {
        instance.city = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<zipcode> queryZip(int val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<zipcode> queryCity(java.lang.String val) {
        return scan().filter(fieldCity(),val);
    }

    public static Stream<zipcode>
    queryCityZip(java.lang.String city, Integer zip) {
        Iterable<zipcode> seed = table;
        int size = table.size();

        Stream<zipcode> result = new Retrieval<zipcode>(seed, size);

        // Filter city
        result = result.filter(fieldCity(),city);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }


    public static FieldExtractable fieldZip() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((zipcode)instance).zip;
            }

            @Override
            public Class<?> getContainerClass() {
                return zipcode.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldCity() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((zipcode)instance).city;
            }

            @Override
            public Class<?> getContainerClass() {
                return zipcode.class;
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
                return zipcode.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<zipcode> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<zipcode> joinOnFieldZip() {
        return scan().joinOn(fieldZip());
    }

    public static JoinStream<zipcode> joinOnFieldCity() {
        return scan().joinOn(fieldCity());
    }


    public static JoinStream<zipcode> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<zipcode> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<zipcode> result = new Retrieval<zipcode>(table, table.size());
        return result;
    }
}
