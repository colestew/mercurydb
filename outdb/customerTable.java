package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import javadb.tests.target.customer;

public class customerTable {
    public static Set<customer> table = new HashSet<>();
        //Collections.newSetFromMap(new WeakHashMap<customer, Boolean>());

    // Maps for indexed fields
    private static Map<java.lang.String, Set<customer>> cnameIndex = new HashMap<>();
        // new MapMaker().weakKeys().makeMap();

    private static Map<java.lang.String, Set<customer>> streetIndex = new HashMap<>();
        // new MapMaker().weakKeys().makeMap();

    public static void insert(customer val) {
        // Populate cname index
        Set<customer> cnameSet = cnameIndex.get(val.cname);
        if (cnameSet == null) {
            cnameSet = new HashSet<customer>();//Collections.newSetFromMap(new WeakHashMap<customer, Boolean>());;
        }
        cnameSet.add(val);
        cnameIndex.put(val.cname, cnameSet);
        // Populate street index
        Set<customer> streetSet = streetIndex.get(val.street);
        if (streetSet == null) {
            streetSet = new HashSet<customer>();//Collections.newSetFromMap(new WeakHashMap<customer, Boolean>());;
        }
        streetSet.add(val);
        streetIndex.put(val.street, streetSet);
        // Populate standard table
        table.add(val);
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setCno(customer instance, int val) {
        instance.cno = val;
    }

    public static void setCname(customer instance, java.lang.String val) {
        cnameIndex.get(instance.cname).remove(instance);
        instance.cname = val;
        Set<customer> cnameSet = cnameIndex.get(instance.cname);
        if (cnameSet == null) {
            cnameSet = new HashSet<customer>();//Collections.newSetFromMap(new WeakHashMap<customer, Boolean>());;
            cnameIndex.put(instance.cname, cnameSet);
        }
        cnameSet.add(instance);
    }

    public static void setStreet(customer instance, java.lang.String val) {
        streetIndex.get(instance.street).remove(instance);
        instance.street = val;
        Set<customer> streetSet = streetIndex.get(instance.street);
        if (streetSet == null) {
            streetSet = new HashSet<customer>();//Collections.newSetFromMap(new WeakHashMap<customer, Boolean>());;
            streetIndex.put(instance.street, streetSet);
        }
        streetSet.add(instance);
    }

    public static void setZip(customer instance, javadb.tests.target.zipcode val) {
        instance.zip = val;
    }

    public static void setPhone(customer instance, java.lang.String val) {
        instance.phone = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<customer> queryCno(int val) {
        return scan().filter(fieldCno(),val);
    }

    public static Stream<customer> queryCname(java.lang.String val) {
        Set<customer> result = cnameIndex.get(val);
        return result == null
                ? new Retrieval<customer>(Collections.EMPTY_LIST, 0)
                : new Retrieval<customer>(result, result.size()); 
    }

    public static Stream<customer> queryStreet(java.lang.String val) {
        Set<customer> result = streetIndex.get(val);
        return result == null
                ? new Retrieval<customer>(Collections.EMPTY_LIST, 0)
                : new Retrieval<customer>(result, result.size()); 
    }

    public static Stream<customer> queryZip(javadb.tests.target.zipcode val) {
        return scan().filter(fieldZip(),val);
    }

    public static Stream<customer> queryPhone(java.lang.String val) {
        return scan().filter(fieldPhone(),val);
    }

    public static Stream<customer>
    queryCnameStreet(java.lang.String cname, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryPhoneStreet(java.lang.String phone, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryCnamePhone(java.lang.String cname, java.lang.String phone) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        return result;
    }

    public static Stream<customer>
    queryCnamePhoneStreet(java.lang.String cname, java.lang.String phone, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryCnoStreet(Integer cno, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryCnameCno(java.lang.String cname, Integer cno) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoStreet(java.lang.String cname, Integer cno, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryCnoPhone(Integer cno, java.lang.String phone) {
        Iterable<customer> seed = table;
        int size = table.size();

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        return result;
    }

    public static Stream<customer>
    queryCnoPhoneStreet(Integer cno, java.lang.String phone, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoPhone(java.lang.String cname, Integer cno, java.lang.String phone) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoPhoneStreet(java.lang.String cname, Integer cno, java.lang.String phone, java.lang.String street) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        return result;
    }

    public static Stream<customer>
    queryStreetZip(java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameZip(java.lang.String cname, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameStreetZip(java.lang.String cname, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryPhoneZip(java.lang.String phone, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryPhoneStreetZip(java.lang.String phone, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnamePhoneZip(java.lang.String cname, java.lang.String phone, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnamePhoneStreetZip(java.lang.String cname, java.lang.String phone, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnoZip(Integer cno, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnoStreetZip(Integer cno, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoZip(java.lang.String cname, Integer cno, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoStreetZip(java.lang.String cname, Integer cno, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnoPhoneZip(Integer cno, java.lang.String phone, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnoPhoneStreetZip(Integer cno, java.lang.String phone, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoPhoneZip(java.lang.String cname, Integer cno, java.lang.String phone, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }

    public static Stream<customer>
    queryCnameCnoPhoneStreetZip(java.lang.String cname, Integer cno, java.lang.String phone, java.lang.String street, javadb.tests.target.zipcode zip) {
        Iterable<customer> seed = table;
        int size = table.size();
        Set<customer> l;
        Object usedIndex = null;

        // Check cname index
        l = cnameIndex.get(cname);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = cname;
        }
        // Check street index
        l = streetIndex.get(street);
        if (l != null && l.size() <= size) {
            size = l.size();
            seed = l;
            usedIndex = street;
        }

        Stream<customer> result = new Retrieval<customer>(seed, size);

        // Filter cname
        if (cname != usedIndex)
            result = result.filter(fieldCname(),cname);

        // Filter cno
        result = result.filter(fieldCno(),cno);

        // Filter phone
        result = result.filter(fieldPhone(),phone);

        // Filter street
        if (street != usedIndex)
            result = result.filter(fieldStreet(),street);

        // Filter zip
        result = result.filter(fieldZip(),zip);

        return result;
    }


    public static FieldExtractable fieldCno() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((customer)instance).cno;
            }

            @Override
            public Class<?> getContainerClass() {
                return customer.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldCname() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((customer)instance).cname;
            }

            @Override
            public Class<?> getContainerClass() {
                return customer.class;
            }

            @Override
            public boolean isIndexed() {
                return true;
            }
        };
    }

    public static FieldExtractable fieldStreet() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((customer)instance).street;
            }

            @Override
            public Class<?> getContainerClass() {
                return customer.class;
            }

            @Override
            public boolean isIndexed() {
                return true;
            }
        };
    }

    public static FieldExtractable fieldZip() {
        return new FieldExtractable() {
            @Override
            public javadb.tests.target.zipcode extractField(Object instance) {
                return ((customer)instance).zip;
            }

            @Override
            public Class<?> getContainerClass() {
                return customer.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldPhone() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((customer)instance).phone;
            }

            @Override
            public Class<?> getContainerClass() {
                return customer.class;
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
                return customer.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<customer> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<customer> joinOnFieldCno() {
        return scan().joinOn(fieldCno());
    }

    public static JoinStream<customer> joinOnFieldCname() {
        return new IndexRetrieval<customer>(cnameIndex).joinOn(fieldCname()); 
    }

    public static JoinStream<customer> joinOnFieldStreet() {
        return new IndexRetrieval<customer>(streetIndex).joinOn(fieldStreet()); 
    }

    public static JoinStream<customer> joinOnFieldZip() {
        return scan().joinOn(fieldZip());
    }

    public static JoinStream<customer> joinOnFieldPhone() {
        return scan().joinOn(fieldPhone());
    }


    public static JoinStream<customer> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Stream<customer> scan() {
        return new Retrieval<customer>(table, table.size());
    }
}
