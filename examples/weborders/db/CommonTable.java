package examples.weborders.db;

import java.util.*;

import org.mercurydb.queryutils.*;

import com.google.common.collect.MapMaker;

import examples.weborders.source.Common;

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
    	if (examples.weborders.db.EmployeeTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.EmployeeTable.remove(examples.weborders.db.EmployeeTable.containedClass.cast(val));
    	    return;
    	}
    	if (examples.weborders.db.CustomerTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.CustomerTable.remove(examples.weborders.db.CustomerTable.containedClass.cast(val));
    	    return;
    	}
    	if (examples.weborders.db.OdetailTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.OdetailTable.remove(examples.weborders.db.OdetailTable.containedClass.cast(val));
    	    return;
    	}
    	if (examples.weborders.db.OrderTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.OrderTable.remove(examples.weborders.db.OrderTable.containedClass.cast(val));
    	    return;
    	}
    	if (examples.weborders.db.PartTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.PartTable.remove(examples.weborders.db.PartTable.containedClass.cast(val));
    	    return;
    	}
    	if (examples.weborders.db.ZipcodeTable.containedClass.isInstance(val)) {
    	    examples.weborders.db.ZipcodeTable.remove(examples.weborders.db.ZipcodeTable.containedClass.cast(val));
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
        result = result.join(examples.weborders.db.EmployeeTable.scan());
        result = result.join(examples.weborders.db.CustomerTable.scan());
        result = result.join(examples.weborders.db.OdetailTable.scan());
        result = result.join(examples.weborders.db.OrderTable.scan());
        result = result.join(examples.weborders.db.PartTable.scan());
        result = result.join(examples.weborders.db.ZipcodeTable.scan());
        return result;
    }
}
