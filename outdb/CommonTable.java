package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import javadb.tests.target.Common;

public class CommonTable {
    public static Set<Common> table = new HashSet<>();
        //Collections.newSetFromMap(new WeakHashMap<Common, Boolean>());

    // Maps for indexed fields
    public static void insert(Common val) {
        // Populate standard table
        table.add(val);
    }

    // Set methods - make sure you use these on indexed fields for consistency!

    // Get methods -- these are retrievals for attribute = value queries


    public static FieldExtractable itself() {
        return new FieldExtractable() {
            @Override
            public Object extractField(Object instance) {
                return instance;
            }

            @Override
            public Class<?> getContainerClass() {
                return Common.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Common> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }


    public static JoinStream<Common> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Stream<Common> scan() {
        return new Retrieval<Common>(table, table.size());
    }
}
