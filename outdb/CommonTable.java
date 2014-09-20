package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.Common;

public class CommonTable {
    public static final List<Common> table = new ArrayList<>();
    
    public static final Class<Common> containedClass = Common.class;

    // Maps for indexed fields
    private static Map<Integer, Set<Common>> commonIdIndex = new HashMap<>();

    public static void insert(Common val) {
        // Populate commonId index
        Set<Common> commonIdSet = commonIdIndex.get(val.commonId);
        if (commonIdSet == null) {
            commonIdSet = new HashSet<Common>();//Collections.newSetFromMap(new WeakHashMap<Common, Boolean>());;
        }
        commonIdSet.add(val);
        commonIdIndex.put(val.commonId, commonIdSet);
        // Populate standard table if T(val) == Common
        if (Common.class.equals(val.getClass()))
            table.add(val);
    }
    
    public static void remove(Common val) {
    	// Remove from table
    	if (Common.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from commonId index
    	commonIdIndex.values().removeAll(Collections.singleton(val));
    	// Remove from subclass indices
    	if (outdb.EmployeeTable.containedClass.isInstance(val)) {
    	    outdb.EmployeeTable.remove(outdb.EmployeeTable.containedClass.cast(val));
    	    return;
    	}
    	if (outdb.nextlevel.customerTable.containedClass.isInstance(val)) {
    	    outdb.nextlevel.customerTable.remove(outdb.nextlevel.customerTable.containedClass.cast(val));
    	    return;
    	}
    	if (outdb.odetailTable.containedClass.isInstance(val)) {
    	    outdb.odetailTable.remove(outdb.odetailTable.containedClass.cast(val));
    	    return;
    	}
    	if (outdb.orderTable.containedClass.isInstance(val)) {
    	    outdb.orderTable.remove(outdb.orderTable.containedClass.cast(val));
    	    return;
    	}
    	if (outdb.partTable.containedClass.isInstance(val)) {
    	    outdb.partTable.remove(outdb.partTable.containedClass.cast(val));
    	    return;
    	}
    	if (outdb.zipcodeTable.containedClass.isInstance(val)) {
    	    outdb.zipcodeTable.remove(outdb.zipcodeTable.containedClass.cast(val));
    	    return;
    	}
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setCommonId(Common instance, int val) {
        commonIdIndex.get(instance.commonId).remove(instance);
        instance.commonId = val;
        Set<Common> commonIdSet = commonIdIndex.get(instance.commonId);
        if (commonIdSet == null) {
            commonIdSet = new HashSet<Common>();//Collections.newSetFromMap(new WeakHashMap<Common, Boolean>());;
            commonIdIndex.put(instance.commonId, commonIdSet);
        }
        commonIdSet.add(instance);
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Common> queryCommonId(int val) {
        Set<Common> result = commonIdIndex.get(val);
        return result == null
                ? new Retrieval<Common>(Collections.EMPTY_LIST, 0)
                : new Retrieval<Common>(result, result.size()); 
    }


    public static FieldExtractable fieldCommonId() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((Common)instance).commonId;
            }

            @Override
            public Class<?> getContainerClass() {
                return Common.class;
            }

            @Override
            public boolean isIndexed() {
                return true;
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

    public static JoinStream<Common> joinOnFieldCommonId() {
        return new IndexRetrieval<Common>(commonIdIndex).joinOn(fieldCommonId()); 
    }


    public static JoinStream<Common> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Common> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Common> result = new Retrieval<Common>(table, table.size());
        result = result.join(outdb.EmployeeTable.scan());
        result = result.join(outdb.nextlevel.customerTable.scan());
        result = result.join(outdb.odetailTable.scan());
        result = result.join(outdb.orderTable.scan());
        result = result.join(outdb.partTable.scan());
        result = result.join(outdb.zipcodeTable.scan());
        return result;
    }
}
