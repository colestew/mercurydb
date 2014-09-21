package examples.weborders.db;

import java.util.*;

import org.mercurydb.queryutils.*;

import com.google.common.collect.MapMaker;

import examples.weborders.source.Zipcode;

public class ZipcodeTable {
    public static final List<Zipcode> table = new ArrayList<>();
    
    public static final Class<Zipcode> containedClass = Zipcode.class;

    // Maps for indexed fields
    public static void insert(Zipcode val) {
        // Populate standard table if T(val) == zipcode
        if (Zipcode.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        examples.weborders.db.CommonTable.insert(val);
    }
    
    public static void remove(Zipcode val) {
    	// Remove from table
    	if (Zipcode.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	examples.weborders.db.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setZip(Zipcode instance, int val) {
        instance.zip = val;
    }

    public static void setCity(Zipcode instance, java.lang.String val) {
        instance.city = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Zipcode> queryZip(int val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<Zipcode> queryCity(java.lang.String val) {
        return scan().filter(fieldCity(),val);
    }

    public static Stream<Zipcode>
    queryCityZip(java.lang.String city, Integer zip) {
        Iterable<Zipcode> seed = table;
        int size = table.size();

        Stream<Zipcode> result = new Retrieval<Zipcode>(seed, size);

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
                return ((Zipcode)instance).zip;
            }

            @Override
            public Class<?> getContainerClass() {
                return Zipcode.class;
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
                return ((Zipcode)instance).city;
            }

            @Override
            public Class<?> getContainerClass() {
                return Zipcode.class;
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
                return Zipcode.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Zipcode> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<Zipcode> joinOnFieldZip() {
        return scan().joinOn(fieldZip());
    }

    public static JoinStream<Zipcode> joinOnFieldCity() {
        return scan().joinOn(fieldCity());
    }


    public static JoinStream<Zipcode> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Zipcode> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Zipcode> result = new Retrieval<Zipcode>(table, table.size());
        return result;
    }
}
