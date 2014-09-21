package examples.weborders.db;

import java.util.*;

import org.mercurydb.queryutils.*;

import com.google.common.collect.MapMaker;

import examples.weborders.source.Order;

public class OrderTable {
    public static final List<Order> table = new ArrayList<>();
    
    public static final Class<Order> containedClass = Order.class;

    // Maps for indexed fields
    private static Map<Integer, Set<Order>> onoIndex = new HashMap<>();

    public static void insert(Order val) {
        // Populate ono index
        Set<Order> onoSet = onoIndex.get(val.ono);
        if (onoSet == null) {
            onoSet = new HashSet<Order>();//Collections.newSetFromMap(new WeakHashMap<order, Boolean>());;
        }
        onoSet.add(val);
        onoIndex.put(val.ono, onoSet);
        // Populate standard table if T(val) == order
        if (Order.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        examples.weborders.db.CommonTable.insert(val);
    }
    
    public static void remove(Order val) {
    	// Remove from table
    	if (Order.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from ono index
    	onoIndex.values().removeAll(Collections.singleton(val));
    	// Remove from outdb.Common indices (superclass)
    	examples.weborders.db.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setOno(Order instance, int val) {
        onoIndex.get(instance.ono).remove(instance);
        instance.ono = val;
        Set<Order> onoSet = onoIndex.get(instance.ono);
        if (onoSet == null) {
            onoSet = new HashSet<Order>();//Collections.newSetFromMap(new WeakHashMap<order, Boolean>());;
            onoIndex.put(instance.ono, onoSet);
        }
        onoSet.add(instance);
    }

    public static void setCno(Order instance, examples.weborders.source.customer val) {
        instance.cno = val;
    }

    public static void setEno(Order instance, examples.weborders.source.Employee val) {
        instance.eno = val;
    }

    public static void setReceived(Order instance, java.lang.String val) {
        instance.received = val;
    }

    public static void setShipped(Order instance, java.lang.String val) {
        instance.shipped = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Order> queryOno(int val) {
        Set<Order> result = onoIndex.get(val);
        return result == null
                ? new Retrieval<Order>(Collections.EMPTY_LIST, 0)
                : new Retrieval<Order>(result, result.size()); 
    }

    public static Stream<Order> queryCno(examples.weborders.source.customer val) {
        return scan().filter(fieldCno(),val);
    }

    public static Stream<Order> queryEno(examples.weborders.source.Employee val) {
        return scan().filter(fieldEno(),val);
    }

    public static Stream<Order> queryReceived(java.lang.String val) {
        return scan().filter(fieldReceived(),val);
    }

    public static Stream<Order> queryShipped(java.lang.String val) {
        return scan().filter(fieldShipped(),val);
    }

    public static Stream<Order>
    queryCnoEno(examples.weborders.source.customer cno, examples.weborders.source.Employee eno) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        return result;
    }

    public static Stream<Order>
    queryCnoReceived(examples.weborders.source.customer cno, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryEnoReceived(examples.weborders.source.Employee eno, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoReceived(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryCnoOno(examples.weborders.source.customer cno, Integer ono) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<Order>
    queryEnoOno(examples.weborders.source.Employee eno, Integer ono) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoOno(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, Integer ono) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        return result;
    }

    public static Stream<Order>
    queryOnoReceived(Integer ono, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryCnoOnoReceived(examples.weborders.source.customer cno, Integer ono, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryEnoOnoReceived(examples.weborders.source.Employee eno, Integer ono, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoOnoReceived(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, Integer ono, java.lang.String received) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

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

    public static Stream<Order>
    queryCnoShipped(examples.weborders.source.customer cno, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryEnoShipped(examples.weborders.source.Employee eno, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoShipped(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryReceivedShipped(java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoReceivedShipped(examples.weborders.source.customer cno, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryEnoReceivedShipped(examples.weborders.source.Employee eno, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoReceivedShipped(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();

        Stream<Order> result = new Retrieval<Order>(seed, size);

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

    public static Stream<Order>
    queryOnoShipped(Integer ono, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoOnoShipped(examples.weborders.source.customer cno, Integer ono, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryEnoOnoShipped(examples.weborders.source.Employee eno, Integer ono, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter eno
        result = result.filter(fieldEno(),eno);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoEnoOnoShipped(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, Integer ono, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

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

    public static Stream<Order>
    queryOnoReceivedShipped(Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

        // Filter ono
        if (ono != usedIndex)
            result = result.filter(fieldOno(),ono);

        // Filter received
        result = result.filter(fieldReceived(),received);

        // Filter shipped
        result = result.filter(fieldShipped(),shipped);

        return result;
    }

    public static Stream<Order>
    queryCnoOnoReceivedShipped(examples.weborders.source.customer cno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

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

    public static Stream<Order>
    queryEnoOnoReceivedShipped(examples.weborders.source.Employee eno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

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

    public static Stream<Order>
    queryCnoEnoOnoReceivedShipped(examples.weborders.source.customer cno, examples.weborders.source.Employee eno, Integer ono, java.lang.String received, java.lang.String shipped) {
        Iterable<Order> seed = table;
        int size = table.size();
        Set<Order> l;
        Object usedIndex = null;

        // Check ono index
        l = onoIndex.get(ono);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = ono;
        }

        Stream<Order> result = new Retrieval<Order>(seed, size);

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
                return ((Order)instance).ono;
            }

            @Override
            public Class<?> getContainerClass() {
                return Order.class;
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
            public examples.weborders.source.customer extractField(Object instance) {
                return ((Order)instance).cno;
            }

            @Override
            public Class<?> getContainerClass() {
                return Order.class;
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
            public examples.weborders.source.Employee extractField(Object instance) {
                return ((Order)instance).eno;
            }

            @Override
            public Class<?> getContainerClass() {
                return Order.class;
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
                return ((Order)instance).received;
            }

            @Override
            public Class<?> getContainerClass() {
                return Order.class;
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
                return ((Order)instance).shipped;
            }

            @Override
            public Class<?> getContainerClass() {
                return Order.class;
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
                return Order.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Order> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<Order> joinOnFieldOno() {
        return new IndexRetrieval<Order>(onoIndex).joinOn(fieldOno()); 
    }

    public static JoinStream<Order> joinOnFieldCno() {
        return scan().joinOn(fieldCno());
    }

    public static JoinStream<Order> joinOnFieldEno() {
        return scan().joinOn(fieldEno());
    }

    public static JoinStream<Order> joinOnFieldReceived() {
        return scan().joinOn(fieldReceived());
    }

    public static JoinStream<Order> joinOnFieldShipped() {
        return scan().joinOn(fieldShipped());
    }


    public static JoinStream<Order> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Order> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Order> result = new Retrieval<Order>(table, table.size());
        return result;
    }
}
