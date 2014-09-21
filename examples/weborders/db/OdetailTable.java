package examples.weborders.db;

import java.util.*;

import org.mercurydb.queryutils.*;

import com.google.common.collect.MapMaker;

import examples.weborders.source.Odetail;

public class OdetailTable {
    public static final List<Odetail> table = new ArrayList<>();
    
    public static final Class<Odetail> containedClass = Odetail.class;

    // Maps for indexed fields
    private static Map<Integer, Set<Odetail>> onoIndex = new HashMap<>();

    public static void insert(Odetail val) {
        // Populate ono index
        Set<Odetail> onoSet = onoIndex.get(val.ono);
        if (onoSet == null) {
            onoSet = new HashSet<Odetail>();//Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());;
        }
        onoSet.add(val);
        onoIndex.put(val.ono, onoSet);
        // Populate standard table if T(val) == odetail
        if (Odetail.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        examples.weborders.db.CommonTable.insert(val);
    }
    
    public static void remove(Odetail val) {
    	// Remove from table
    	if (Odetail.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from ono index
    	onoIndex.values().removeAll(Collections.singleton(val));
    	// Remove from outdb.Common indices (superclass)
    	examples.weborders.db.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setOno(Odetail instance, int val) {
        onoIndex.get(instance.ono).remove(instance);
        instance.ono = val;
        Set<Odetail> onoSet = onoIndex.get(instance.ono);
        if (onoSet == null) {
            onoSet = new HashSet<Odetail>();//Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());;
            onoIndex.put(instance.ono, onoSet);
        }
        onoSet.add(instance);
    }

    public static void setPno(Odetail instance, examples.weborders.source.Part val) {
        instance.pno = val;
    }

    public static void setQty(Odetail instance, int val) {
        instance.qty = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Odetail> queryOno(int val) {
        Set<Odetail> result = onoIndex.get(val);
        return result == null
                ? new Retrieval<Odetail>(Collections.EMPTY_LIST, 0)
                : new Retrieval<Odetail>(result, result.size()); 
    }

    public static Stream<Odetail> queryPno(examples.weborders.source.Part val) {
        return scan().filter(fieldPno(),val);
    }

    public static Stream<Odetail> queryQty(int val) {
        return scan().filter(fieldQty(),val);
    }

    public static Stream<Odetail>
    queryPnoQty(examples.weborders.source.Part pno, Integer qty) {
        Iterable<Odetail> seed = table;
        int size = table.size();

        Stream<Odetail> result = new Retrieval<Odetail>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qty
        result = result.filter(fieldQty(),qty);

        return result;
    }

    public static Stream<Odetail>
    queryOnoPno(Integer ono, examples.weborders.source.Part pno) {
        Iterable<Odetail> seed = table;
        int size = table.size();
        Set<Odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Odetail> result = new Retrieval<Odetail>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<Odetail>
    queryOnoQty(Integer ono, Integer qty) {
        Iterable<Odetail> seed = table;
        int size = table.size();
        Set<Odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Odetail> result = new Retrieval<Odetail>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter qty
        result = result.filter(fieldQty(),qty);

        return result;
    }

    public static Stream<Odetail>
    queryOnoPnoQty(Integer ono, examples.weborders.source.Part pno, Integer qty) {
        Iterable<Odetail> seed = table;
        int size = table.size();
        Set<Odetail> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Odetail> result = new Retrieval<Odetail>(seed, size);

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
                return ((Odetail)instance).ono;
            }

            @Override
            public Class<?> getContainerClass() {
                return Odetail.class;
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
            public examples.weborders.source.Part extractField(Object instance) {
                return ((Odetail)instance).pno;
            }

            @Override
            public Class<?> getContainerClass() {
                return Odetail.class;
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
                return ((Odetail)instance).qty;
            }

            @Override
            public Class<?> getContainerClass() {
                return Odetail.class;
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
                return Odetail.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Odetail> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<Odetail> joinOnFieldOno() {
        return new IndexRetrieval<Odetail>(onoIndex).joinOn(fieldOno()); 
    }

    public static JoinStream<Odetail> joinOnFieldPno() {
        return scan().joinOn(fieldPno());
    }

    public static JoinStream<Odetail> joinOnFieldQty() {
        return scan().joinOn(fieldQty());
    }


    public static JoinStream<Odetail> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Odetail> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Odetail> result = new Retrieval<Odetail>(table, table.size());
        return result;
    }
}
