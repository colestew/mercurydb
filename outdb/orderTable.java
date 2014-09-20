package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.order;

public class orderTable {
    public static final List<order> table = new ArrayList<>();
    
    public static final Class<order> containedClass = order.class;

    // Maps for indexed fields
    private static Map<Integer, Set<order>> onoIndex = new HashMap<>();

    public static void insert(order val) {
        // Populate ono index
        Set<order> onoSet = onoIndex.get(val.ono);
        if (onoSet == null) {
            onoSet = new HashSet<order>();//Collections.newSetFromMap(new WeakHashMap<order, Boolean>());;
        }
        onoSet.add(val);
        onoIndex.put(val.ono, onoSet);
        // Populate standard table if T(val) == order
        if (order.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(order val) {
    	// Remove from table
    	if (order.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from ono index
    	onoIndex.values().removeAll(Collections.singleton(val));
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setOno(order instance, int val) {
        onoIndex.get(instance.ono).remove(instance);
        instance.ono = val;
        Set<order> onoSet = onoIndex.get(instance.ono);
        if (onoSet == null) {
            onoSet = new HashSet<order>();//Collections.newSetFromMap(new WeakHashMap<order, Boolean>());;
            onoIndex.put(instance.ono, onoSet);
        }
        onoSet.add(instance);
    }

    public static void setCno(order instance, tests.target.nextlevel.customer val) {
        instance.cno = val;
    }

    public static void setEno(order instance, tests.target.employee val) {
        instance.eno = val;
    }

    public static void setReceived(order instance, java.lang.String val) {
        instance.received = val;
    }

    public static void setShipped(order instance, java.lang.String val) {
        instance.shipped = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<order> queryOno(int val) {
        Set<order> result = onoIndex.get(val);
        return result == null
                ? new Retrieval<order>(Collections.EMPTY_LIST, 0)
                : new Retrieval<order>(result, result.size()); 
    }

    public static Stream<order> queryCno(tests.target.nextlevel.customer val) {
        return scan().filter(fieldCno(),val);
    }

    public static Stream<order> queryEno(tests.target.employee val) {
        return scan().filter(fieldEno(),val);
    }

    public static Stream<order> queryReceived(java.lang.String val) {
        return scan().filter(fieldReceived(),val);
    }

    public static Stream<order> queryShipped(java.lang.String val) {
        return scan().filter(fieldShipped(),val);
    }

    public static Stream<order>
    queryReceivedShipped(java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryOnoReceived(Integer ono, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryOnoShipped(Integer ono, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryOnoReceivedShipped(Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoReceived(tests.target.nextlevel.customer cno, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryCnoShipped(tests.target.nextlevel.customer cno, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoReceivedShipped(tests.target.nextlevel.customer cno, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoOno(tests.target.nextlevel.customer cno, Integer ono) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<order>
    queryCnoOnoReceived(tests.target.nextlevel.customer cno, Integer ono, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryCnoOnoShipped(tests.target.nextlevel.customer cno, Integer ono, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoOnoReceivedShipped(tests.target.nextlevel.customer cno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryEnoReceived(tests.target.employee eno, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryEnoShipped(tests.target.employee eno, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryEnoReceivedShipped(tests.target.employee eno, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryEnoOno(tests.target.employee eno, Integer ono) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<order>
    queryEnoOnoReceived(tests.target.employee eno, Integer ono, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryEnoOnoShipped(tests.target.employee eno, Integer ono, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryEnoOnoReceivedShipped(tests.target.employee eno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoEno(tests.target.nextlevel.customer cno, tests.target.employee eno) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<order>
    queryCnoEnoReceived(tests.target.nextlevel.customer cno, tests.target.employee eno, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryCnoEnoShipped(tests.target.nextlevel.customer cno, tests.target.employee eno, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoEnoReceivedShipped(tests.target.nextlevel.customer cno, tests.target.employee eno, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoEnoOno(tests.target.nextlevel.customer cno, tests.target.employee eno, Integer ono) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<order>
    queryCnoEnoOnoReceived(tests.target.nextlevel.customer cno, tests.target.employee eno, Integer ono, java.lang.String received) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<order>
    queryCnoEnoOnoShipped(tests.target.nextlevel.customer cno, tests.target.employee eno, Integer ono, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<order>
    queryCnoEnoOnoReceivedShipped(tests.target.nextlevel.customer cno, tests.target.employee eno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<order> seed = table;
        int size = table.size();
        Set<order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<order> result = new Retrieval<order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }


    public static FieldExtractable fieldOno() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((order)instance).ono;
            }

            @Override
            public Class<?> getContainerClass() {
                return order.class;
            }

            @Override
            public boolean isIndexed() {
                return true;
            }
        };
    }

    public static FieldExtractable fieldCno() {
        return new FieldExtractable() {
            @Override
            public tests.target.nextlevel.customer extractField(Object instance) {
                return ((order)instance).cno;
            }

            @Override
            public Class<?> getContainerClass() {
                return order.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldEno() {
        return new FieldExtractable() {
            @Override
            public tests.target.employee extractField(Object instance) {
                return ((order)instance).eno;
            }

            @Override
            public Class<?> getContainerClass() {
                return order.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldReceived() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((order)instance).received;
            }

            @Override
            public Class<?> getContainerClass() {
                return order.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldShipped() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((order)instance).shipped;
            }

            @Override
            public Class<?> getContainerClass() {
                return order.class;
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
                return order.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<order> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<order> joinOnFieldOno() {
        return new IndexRetrieval<order>(onoIndex).joinOn(fieldOno()); 
    }

    public static JoinStream<order> joinOnFieldCno() {
        return scan().joinOn(fieldCno());
    }

    public static JoinStream<order> joinOnFieldEno() {
        return scan().joinOn(fieldEno());
    }

    public static JoinStream<order> joinOnFieldReceived() {
        return scan().joinOn(fieldReceived());
    }

    public static JoinStream<order> joinOnFieldShipped() {
        return scan().joinOn(fieldShipped());
    }


    public static JoinStream<order> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<order> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<order> result = new Retrieval<order>(table, table.size());
        return result;
    }
}
