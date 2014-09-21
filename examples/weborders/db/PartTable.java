package examples.weborders.db;

import java.util.*;

import org.mercurydb.queryutils.*;

import com.google.common.collect.MapMaker;

import examples.weborders.source.Part;

public class PartTable {
    public static final List<Part> table = new ArrayList<>();
    
    public static final Class<Part> containedClass = Part.class;

    // Maps for indexed fields
    public static void insert(Part val) {
        // Populate standard table if T(val) == part
        if (Part.class.equals(val.getClass()))
            table.add(val);
        // Populate super table indices
        examples.weborders.db.CommonTable.insert(val);
    }
    
    public static void remove(Part val) {
    	// Remove from table
    	if (Part.class.equals(val.getClass()))
    	    table.remove(val);
    	// Remove from outdb.Common indices (superclass)
    	examples.weborders.db.CommonTable.remove(val);
    	// Remove from subclass indices
    }

    // Set methods - make sure you use these on indexed fields for consistency!
    public static void setPno(Part instance, int val) {
        instance.pno = val;
    }

    public static void setPname(Part instance, java.lang.String val) {
        instance.pname = val;
    }

    public static void setQoh(Part instance, int val) {
        instance.qoh = val;
    }

    public static void setPrice(Part instance, double val) {
        instance.price = val;
    }

    public static void setOlevel(Part instance, int val) {
        instance.olevel = val;
    }


    // Get methods -- these are retrievals for attribute = value queries
    public static Stream<Part> queryPno(int val) {
        return scan().filter(fieldPno(),val);
    }

    public static Stream<Part> queryPname(java.lang.String val) {
        return scan().filter(fieldPname(),val);
    }

    public static Stream<Part> queryQoh(int val) {
        return scan().filter(fieldQoh(),val);
    }

    public static Stream<Part> queryPrice(double val) {
        return scan().filter(fieldPrice(),val);
    }

    public static Stream<Part> queryOlevel(int val) {
        return scan().filter(fieldOlevel(),val);
    }

    public static Stream<Part>
    queryOlevelQoh(Integer olevel, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryPriceQoh(Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPrice(Integer olevel, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryOlevelPriceQoh(Integer olevel, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryPnoQoh(Integer pno, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPno(Integer olevel, Integer pno) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnoQoh(Integer olevel, Integer pno, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryPnoPrice(Integer pno, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryPnoPriceQoh(Integer pno, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnoPrice(Integer olevel, Integer pno, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnoPriceQoh(Integer olevel, Integer pno, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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

    public static Stream<Part>
    queryPnameQoh(java.lang.String pname, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPname(Integer olevel, java.lang.String pname) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnameQoh(Integer olevel, java.lang.String pname, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryPnamePrice(java.lang.String pname, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryPnamePriceQoh(java.lang.String pname, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnamePrice(Integer olevel, java.lang.String pname, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnamePriceQoh(Integer olevel, java.lang.String pname, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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

    public static Stream<Part>
    queryPnamePno(java.lang.String pname, Integer pno) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<Part>
    queryPnamePnoQoh(java.lang.String pname, Integer pno, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter qoh
        result = result.filter(fieldQoh(),qoh);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnamePno(Integer olevel, java.lang.String pname, Integer pno) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter olevel
        result = result.filter(fieldOlevel(),olevel);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        return result;
    }

    public static Stream<Part>
    queryOlevelPnamePnoQoh(Integer olevel, java.lang.String pname, Integer pno, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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

    public static Stream<Part>
    queryPnamePnoPrice(java.lang.String pname, Integer pno, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

        // Filter pname
        result = result.filter(fieldPname(),pname);

        // Filter pno
        result = result.filter(fieldPno(),pno);

        // Filter price
        result = result.filter(fieldPrice(),price);

        return result;
    }

    public static Stream<Part>
    queryPnamePnoPriceQoh(java.lang.String pname, Integer pno, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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

    public static Stream<Part>
    queryOlevelPnamePnoPrice(Integer olevel, java.lang.String pname, Integer pno, Double price) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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

    public static Stream<Part>
    queryOlevelPnamePnoPriceQoh(Integer olevel, java.lang.String pname, Integer pno, Double price, Integer qoh) {
        Iterable<Part> seed = table;
        int size = table.size();

        Stream<Part> result = new Retrieval<Part>(seed, size);

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
                return ((Part)instance).pno;
            }

            @Override
            public Class<?> getContainerClass() {
                return Part.class;
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
                return ((Part)instance).pname;
            }

            @Override
            public Class<?> getContainerClass() {
                return Part.class;
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
                return ((Part)instance).qoh;
            }

            @Override
            public Class<?> getContainerClass() {
                return Part.class;
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
                return ((Part)instance).price;
            }

            @Override
            public Class<?> getContainerClass() {
                return Part.class;
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
                return ((Part)instance).olevel;
            }

            @Override
            public Class<?> getContainerClass() {
                return Part.class;
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
                return Part.class;
            }

            @Override
            public boolean isIndexed() {
                return false;
            }
        };
    }

    public static JoinStream<Part> joinOn(FieldExtractable jf) {
        return scan().joinOn(jf);
    }

    public static JoinStream<Part> joinOnFieldPno() {
        return scan().joinOn(fieldPno());
    }

    public static JoinStream<Part> joinOnFieldPname() {
        return scan().joinOn(fieldPname());
    }

    public static JoinStream<Part> joinOnFieldQoh() {
        return scan().joinOn(fieldQoh());
    }

    public static JoinStream<Part> joinOnFieldPrice() {
        return scan().joinOn(fieldPrice());
    }

    public static JoinStream<Part> joinOnFieldOlevel() {
        return scan().joinOn(fieldOlevel());
    }


    public static JoinStream<Part> joinOnItself() {
        return scan().joinOn(itself());
    }

    public static Retrieval<Part> scan() {
        /*
         * Here we need to do a tree traversal such that
         * every possible subclass table is joined with
         * this classes table in the scan
         */
         
        Retrieval<Part> result = new Retrieval<Part>(table, table.size());
        return result;
    }
}
