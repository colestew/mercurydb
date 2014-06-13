package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import javadb.tests.target.odetail;

public class odetailTable {
    public static Set<odetail> table = new HashSet<>();
        //Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());

    // Maps for indexed fields
    private static Map<Integer, Set<odetail>> onoIndex = new HashMap<>();
        // new MapMaker().weakKeys().makeMap();

    public static void insert(odetail val) {
        // Populate ono index
        Set<odetail> onoSet = onoIndex.get(val.ono);
        if (onoSet == null) {
            onoSet = new HashSet<odetail>();//Collections.newSetFromMap(new WeakHashMap<odetail, Boolean>());;
        }
        onoSet.add(val);
        onoIndex.put(val.ono, onoSet);
        // Populate standard table
        table.add(val);
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

    public static void setPno(odetail instance, javadb.tests.target.part val) {
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

    public static Stream<odetail> queryPno(javadb.tests.target.part val) {
        return scan().filter(fieldPno(),val);
    }

    public static Stream<odetail> queryQty(int val) {
        return scan().filter(fieldQty(),val);
    }

    public static Stream<odetail>
    queryOnoPno(Integer ono, javadb.tests.target.part pno) {
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
    queryPnoQty(javadb.tests.target.part pno, Integer qty) {
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
    queryOnoPnoQty(Integer ono, javadb.tests.target.part pno, Integer qty) {
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
            public javadb.tests.target.part extractField(Object instance) {
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

    public static Stream<odetail> scan() {
        return new Retrieval<odetail>(table, table.size());
    }
}
