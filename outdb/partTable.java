package outdb;

import javadb.queryutils.*;
import java.util.*;

import com.google.common.collect.MapMaker;

import tests.target.part;

public class partTable {
    public static final List<part> table = new ArrayList<>();
    
    public static final Class<part> containedClass = part.class;

    // Maps for indexed fields
    public static void insert(part val) {
        // Populate standard table if T(val) == part
        if (part.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        outdb.CommonTable.insert(val);
    }
    
    public static void remove(part val) {
    	// Remove from table
    	if (part.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	outdb.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setPno(part instance, int val) {
        instance.pno = val;
    }

    public static void setPname(part instance, java.lang.String val) {
        instance.pname = val;
    }

    public static void setQoh(part instance, int val) {
        instance.qoh = val;
    }

    public static void setPrice(part instance, double val) {
        instance.price = val;
    }

    public static void setOlevel(part instance, int val) {
        instance.olevel = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<part> queryPno(int val) {
        return scan().filter(fieldPno(),val);
    }

    public static Stream<part> queryPname(java.lang.String val) {
        return scan().filter(fieldPname(),val);
    }

    public static Stream<part> queryQoh(int val) {
        return scan().filter(fieldQoh(),val);
    }

    public static Stream<part> queryPrice(double val) {
        return scan().filter(fieldPrice(),val);
    }

    public static Stream<part> queryOlevel(int val) {
        return scan().filter(fieldOlevel(),val);
    }

    public static Stream<part>
    queryOlevelQoh(Integer olevel, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPriceQoh(Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPrice(Integer olevel, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryOlevelPriceQoh(Integer olevel, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnoQoh(Integer pno, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPno(Integer olevel, Integer pno) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<part>
    queryOlevelPnoQoh(Integer olevel, Integer pno, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnoPrice(Integer pno, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryPnoPriceQoh(Integer pno, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPnoPrice(Integer olevel, Integer pno, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryOlevelPnoPriceQoh(Integer olevel, Integer pno, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnameQoh(java.lang.String pname, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPname(Integer olevel, java.lang.String pname) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        return result;
    }

    public static Stream<part>
    queryOlevelPnameQoh(Integer olevel, java.lang.String pname, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnamePrice(java.lang.String pname, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryPnamePriceQoh(java.lang.String pname, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePrice(Integer olevel, java.lang.String pname, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePriceQoh(Integer olevel, java.lang.String pname, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnamePno(java.lang.String pname, Integer pno) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<part>
    queryPnamePnoQoh(java.lang.String pname, Integer pno, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePno(Integer olevel, java.lang.String pname, Integer pno) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePnoQoh(Integer olevel, java.lang.String pname, Integer pno, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryPnamePnoPrice(java.lang.String pname, Integer pno, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryPnamePnoPriceQoh(java.lang.String pname, Integer pno, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePnoPrice(Integer olevel, java.lang.String pname, Integer pno, Double price) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<part>
    queryOlevelPnamePnoPriceQoh(Integer olevel, java.lang.String pname, Integer pno, Double price, Integer qoh) {
        Iterable<part> seed = table;
        int size = table.size();

        Stream<part> result = new Retrieval<part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }


    public static FieldExtractable fieldPno() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((part)instance).pno;
            }

            @Override
            public Class<?> getContainerClass() {
                return part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldPname() {
        return new FieldExtractable() {
            @Override
            public java.lang.String extractField(Object instance) {
                return ((part)instance).pname;
            }

            @Override
            public Class<?> getContainerClass() {
                return part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldQoh() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((part)instance).qoh;
            }

            @Override
            public Class<?> getContainerClass() {
                return part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldPrice() {
        return new FieldExtractable() {
            @Override
            public Double extractField(Object instance) {
                return ((part)instance).price;
            }

            @Override
            public Class<?> getContainerClass() {
                return part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static FieldExtractable fieldOlevel() {
        return new FieldExtractable() {
            @Override
            public Integer extractField(Object instance) {
                return ((part)instance).olevel;
            }

            @Override
            public Class<?> getContainerClass() {
                return part.class;
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
                return part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<part> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<part> joinOnFieldPno() {
        return scan().joinOn(fieldPno());
    }

    public static JoinStream<part> joinOnFieldPname() {
        return scan().joinOn(fieldPname());
    }

    public static JoinStream<part> joinOnFieldQoh() {
        return scan().joinOn(fieldQoh());
    }

    public static JoinStream<part> joinOnFieldPrice() {
        return scan().joinOn(fieldPrice());
    }

    public static JoinStream<part> joinOnFieldOlevel() {
        return scan().joinOn(fieldOlevel());
    }


    public static JoinStream<part> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<part> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<part> result = new Retrieval<part>(table, table.size());
        return result;
    }
}
