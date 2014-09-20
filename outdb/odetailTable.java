package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.odetail;

public class odetailTable {
    public static final List<odetail> table = new ArrayList<>();
    
    public static final Class<odetail> containedClass = odetail.class;

    // Maps for indexed fields
    private static Map<Integer, Set<odetail>> onoIndex = new HashMap<>();

    public static void insert(odetail val) {
        // Populate ono index
        Set<odetail> onoSet = onoIndex.get(val.ono);
        if (onoSet == null) {
            onoSet = new HashSet<odetail>();//Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());;
        }
        onoSet.add(val);
        onoIndex.put(val.ono, onoSet);
        // Populate standard table if T(val) == odetail
        if (odetail.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(odetail val) {
    	// Remove from table
    	if (odetail.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from ono index
    	onoIndex.values().removeAll(Collections.singleton(val));
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setOno(odetail instance, int val) {
        onoIndex.get(instance.ono).remove(instance);
        instance.ono = val;
        Set<odetail> onoSet = onoIndex.get(instance.ono);
        if (onoSet == null) {
            onoSet = new HashSet<odetail>();//Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());;
            onoIndex.put(instance.ono, onoSet);
        }
        onoSet.add(instance);
    }

    public static void setPno(odetail instance, tests.target.part val) {
        instance.pno = val;
    }

    public static void setQty(odetail instance, int val) {
        instance.qty = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<odetail> queryOno(int val) {
        Set<odetail> result = onoIndex.get(val);
        return result == null
                ? new Retrieval<odetail>(Collections.EMPTY_LIST, 0)
                : new Retrieval<odetail>(result, result.size()); 
    }

    public static Stream<odetail> queryPno(tests.target.part val) {
        return scan().filter(fieldPno(),val);
    }

    public static Stream<odetail> queryQty(int val) {
        return scan().filter(fieldQty(),val);
    }

    public static Stream<odetail>
    queryPnoQty(tests.target.part pno, Integer qty) {
        Iterable<odetail> seed = table;
        int size = table.size();

        Stream<odetail> result = new Retrieval<odetail>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qty
        result = result.filter(fieldQty(),qty);

        return result;
    }

    public static Stream<odetail>
    queryOnoPno(Integer ono, tests.target.part pno) {
        Iterable<odetail> seed = table;
        int size = table.size();
        Set<odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<odetail> result = new Retrieval<odetail>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<odetail>
    queryOnoQty(Integer ono, Integer qty) {
        Iterable<odetail> seed = table;
        int size = table.size();
        Set<odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<odetail> result = new Retrieval<odetail>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter qty
        result = result.filter(fieldQty(),qty);

        return result;
    }

    public static Stream<odetail>
    queryOnoPnoQty(Integer ono, tests.target.part pno, Integer qty) {
        Iterable<odetail> seed = table;
        int size = table.size();
        Set<odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<odetail> result = new Retrieval<odetail>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qty
        result = result.filter(fieldQty(),qty);

        return result;
    }


    public static FieldExtractable fieldOno() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((odetail)instance).ono;
            }

            @Override
            public Class<?> getContainerClass() {
                return odetail.class;
            }

            @Override
            public boolean isIndexed() {
                return true;
            }
        };
    }

    public static FieldExtractable fieldPno() {
        return new FieldExtractable() {
            @Override
            public tests.target.part extractField(Object instance) {
                return ((odetail)instance).pno;
            }

            @Override
            public Class<?> getContainerClass() {
                return odetail.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldQty() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((odetail)instance).qty;
            }

            @Override
            public Class<?> getContainerClass() {
                return odetail.class;
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
                return odetail.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<odetail> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<odetail> joinOnFieldOno() {
        return new IndexRetrieval<odetail>(onoIndex).joinOn(fieldOno()); 
    }

    public static JoinStream<odetail> joinOnFieldPno() {
        return scan().joinOn(fieldPno());
    }

    public static JoinStream<odetail> joinOnFieldQty() {
        return scan().joinOn(fieldQty());
    }


    public static JoinStream<odetail> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<odetail> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<odetail> result = new Retrieval<odetail>(table, table.size());
        return result;
    }
}
