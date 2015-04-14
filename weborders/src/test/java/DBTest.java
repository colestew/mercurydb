import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mercurydb.queryutils.*;
import org.mercurydb.queryutils.HgTupleStream.HgTuple;
import org.mercurydb.queryutils.joiners.JoinNestedLoops;
import weborders.db.OdetailTable;
import weborders.db.OrderTable;
import weborders.db.PartTable;
import weborders.db.ZipcodeTable;
import weborders.source.*;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

@SuppressWarnings("unused")
public class DBTest {
    public static final int TEST_SIZE = 50;

    static Zipcode[] zips;
    static Employee[] emps;
    static Customer[] customers;
    static Part[] parts;
    static Order[] orders;
    static Odetail[] odetails;

    static HgStream<HgTupleStream.HgTuple> correctResult;
    static long correctCount;

    private static <T> void checkCorrectQueryResult(HgStream<T> test, HgStream<T> src, HgPredicate<T> pred) {
        List<T> correct = Lists.newArrayList();
        for (T e : src) {
            if (pred.test(e)) {
                correct.add(e);
            }
        }
        for (T e : test) {
            if (!correct.remove(e)) fail();
        }

        if (!correct.isEmpty()) fail();
    }

    private static <T1, T2> void compareIterators(Iterator<T1> a, Iterator<T2> b) {
        while (a.hasNext() && b.hasNext()) {
            T1 t1 = a.next();
            T2 t2 = b.next();
            if (!t1.equals(t2)) {
                fail("Wrong iterator comparison: " + t1 + " \\ " + t2);
            }
        }
        if (a.hasNext() || b.hasNext()) fail();
    }

    @Test
    public void testStreamFilter() {
        HgStream<Order> test = OrderTable
                .stream()
                .filter(OrderTable.eq.ono(1020));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono == 1020);
    }

    @Test
    public void testQueryLt() {
        HgStream<Order> test = HgDB.query(OrderTable.lt.ono(1020));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono < 1020);
    }

    @Test
    public void testQueryLe() {
        HgStream<Order> test = HgDB.query(OrderTable.le.ono(1020));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono <= 1020);
    }

    @Test
    public void testQueryGt() {
        HgStream<Order> test = HgDB.query(OrderTable.gt.ono(1020));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono > 1020);
    }


    @Test
    public void testQueryGe() {
        HgStream<Order> test = HgDB.query(OrderTable.ge.ono(1020));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono >= 1020);
    }

    @Test
    public void testQueryCustom() {
        Collection<Integer> set = Lists.newArrayList(1020, 1021, 1025);
        HgStream<Order> test = HgDB.query(OrderTable.predicates.ono(set::contains));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> set.contains(o.ono));
    }

    @Test
    public void testQueryMulti() {
        HgStream<Part> test = HgDB.query(
                PartTable.lt.pname("L"),
                PartTable.le.price(19.99));
        checkCorrectQueryResult(
                test,
                PartTable.stream(),
                p -> p.pname.compareTo("L") < 0 && p.price <= 19.99);
    }

    @Test
    public void testStreamConcat() {
        HgStream<Order> test = HgDB.query(OrderTable.eq.ono(1020))
                .concat(HgDB.query(OrderTable.eq.ono(1021)));
        checkCorrectQueryResult(
                test,
                OrderTable.stream(),
                o -> o.ono == 1020 || o.ono == 1021);
    }

    @Test
    public void testFilterJoin() {
        HgTupleStream stream = HgDB.join(
                HgDB.query(OrderTable.eq.ono(1023)).joinOn(OrderTable.on.ono()),
                OdetailTable.on.ono());
        for (HgTuple t : stream) {
            if (t.get(OrderTable.ID).ono != 1023 || t.get(OdetailTable.ID).ono != 1023) fail();
        }
    }

    @Test
    public void testJoinPredicate() {
        HgTupleStream stream = HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                new HgBiPredicate<Integer, Integer>() {
                    @Override
                    public boolean test(Integer o1, Integer o2) {
                        return o1 == 1020 && o2 == 1020;
                    }
                }
        );

        for (HgTuple t : stream) {
            if (t.get(OrderTable.ID).ono != t.get(OdetailTable.ID).ono &&
                    t.get(OrderTable.ID).ono != 1020) fail();
        }
    }

    @Test
    public void testJoinCollection() {
        HgTupleStream stream = HgDB.join(
                PartTable.stream().joinOn(PartTable.self(PartTable.ID)),
                OdetailTable.on.pnos(),
                HgRelation.IN
        );

        for (HgTuple t : stream) {
            if (!(t.get(OdetailTable.ID).pnos.contains(t.get(PartTable.ID)))) fail();
        }
    }

    @Test
    public void testTemporaryIndexJoinEq() {
        // Hash Join
        long count = 0;
        HgTupleStream result = HgDB.join(
                noIndexStream(OrderTable.on.ono()),
                noIndexStream(OdetailTable.on.ono()));

        for (HgTupleStream.HgTuple t : result) ++count;

        if (count != correctCount) fail();
    }

    @Test
    public void testNestedLoops() {
        long count = 0;
        long startTime = System.currentTimeMillis();
        for (HgTuple jr : new JoinNestedLoops(new JoinPredicate(
                noIndexStream(OrderTable.on.ono()),
                noIndexStream(OdetailTable.on.ono())))) {
            ++count;
        }

        long stopTime = System.currentTimeMillis();
        System.out.println("Nested loops time for " + count + " elements: " + (stopTime - startTime) / 1000.0);

        if (count != correctCount) fail();
    }

    @Test
    public void testIndexScan() {
        long startTime = System.currentTimeMillis();
        // Index Scan
        long count = 0;
        for (HgTuple jr : HgDB.join(
                noIndexStream(OrderTable.on.ono()),
                OdetailTable.on.ono())) {
            ++count;
        }

        long stopTime = System.currentTimeMillis();

        System.out.println("Index Scan Time for " + count + " elements: " + (stopTime - startTime) / 1000.0);

        if (count != correctCount) fail();
    }

    @Test
    public void testIndexScanLt() {
        long startTime = System.currentTimeMillis();
        // Index Scan
        long count = 0;
        for (HgTuple jr : HgDB.join(
                noIndexStream(OrderTable.on.ono()),
                OdetailTable.on.ono(),
                HgRelation.LT)) {
            ++count;
            if (jr.get(OrderTable.ID).ono >= jr.get(OdetailTable.ID).ono) fail();
        }
        long stopTime = System.currentTimeMillis();

        System.out.println("Index Scan Lt Time for " + count + " elements: " + (stopTime - startTime) / 1000.0);

    }

    @Test
    public void testTempIndexLt() {
        long startTime = System.currentTimeMillis();
        // Index Scan
        long count = 0;
        for (HgTuple jr : HgDB.join(
                noIndexStream(OrderTable.on.ono()),
                noIndexStream(OdetailTable.on.ono()),
                HgRelation.LT)) {
            ++count;
            if (jr.get(OrderTable.ID).ono >= jr.get(OdetailTable.ID).ono) fail();
        }
        long stopTime = System.currentTimeMillis();

        System.out.println("Temp Index Lt Time for " + count + " elements: " + (stopTime - startTime) / 1000.0);

    }

    @Test
    public void testIndexScan3() {
        long count = 0;
        for (HgTuple jr : HgDB.join(
                OrderTable.on.ono(),
                noIndexStream(OdetailTable.on.ono()))) {
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testIndexIntersection() {
        // Index Intersection
        long count = 0;
        HgTupleStream order = OrderTable.on.ono();
        HgTupleStream odetail = OdetailTable.on.ono();
        for (HgTuple jr : HgDB.join(
                order,
                odetail)) {
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testReset() {
        HgTupleStream a = OrderTable.on.ono();
        HgTupleStream b = OdetailTable.on.ono();
        HgPolyTupleStream c = HgDB.join(a, b);

        long count = 0;
        for (HgTuple jr : c) ++count;
        if (count != correctCount) fail();
        c.reset();
        count = 0;
        for (HgTuple jr : c) ++count;
        if (count != correctCount) fail();
        c.reset();
        count = 0;
        for (HgTuple jr : c) ++count;
        if (count != correctCount) fail();
    }

    @Test
    public void testSelfJoin() {
        TableID<Order> oid = OrderTable.createAlias();
        TableID<Odetail> odid = OdetailTable.createAlias();
        HgTupleStream a = OrderTable.as(oid).on.ono();
        HgTupleStream b = OdetailTable.as(odid).on.ono();

        int count = 0;
        for (HgTuple t : HgDB.join(a, b)) {
            if (t.get(oid).ono != t.get(odid).ono) fail();
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testSelfJoin2() {
        TableID<Part> partAlias = PartTable.createAlias();

        for (HgTuple t : HgDB.join(PartTable.on.price(), PartTable.as(partAlias).on.price(), HgRelation.LT)) {
            if (t.get(PartTable.ID).price >= t.get(partAlias).price) fail();
        }
    }

    @Test
    public void testJoinNe() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                HgRelation.NE)) {
            if (t.get(OrderTable.ID).ono == t.get(OdetailTable.ID).ono)
                fail();
        }
    }

    @Test
    public void testJoinLtIntersection() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                HgRelation.LT)) {
            if (t.get(OrderTable.ID).ono >= t.get(OdetailTable.ID).ono)
                fail();
        }
    }

    @Test
    public void testJoinLtScan1() {
        for (HgTuple t : HgDB.join(
                noIndexStream(OrderTable.on.ono()),
                OdetailTable.on.ono(),
                HgRelation.LT)) {
            if (t.get(OrderTable.ID).ono >= t.get(OdetailTable.ID).ono)
                fail();
        }
    }

    @Test
    public void testJoinLtScan2() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                noIndexStream(OdetailTable.on.ono()),
                HgRelation.LT)) {
            if (t.get(OrderTable.ID).ono >= t.get(OdetailTable.ID).ono)
                fail();
        }
    }

    @Test
    public void testJoinLe() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                HgRelation.LE)) {
            if (t.get(OrderTable.ID).ono > t.get(OdetailTable.ID).ono)
                fail();
        }
    }

    @Test
    public void testJoinGt() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                HgRelation.GT)) {
            if (t.get(OrderTable.ID).ono <= t.get(OdetailTable.ID).ono)
                fail();
        }
    }


    @Test
    public void testJoinGe() {
        for (HgTuple t : HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono(),
                HgRelation.GE)) {
            if (t.get(OrderTable.ID).ono < t.get(OdetailTable.ID).ono)
                fail();
        }
    }


    @Test
    public void testMultiJoin() {
        HgTupleStream result = HgDB.join(
                new JoinPredicate(OrderTable.on.ono(), OdetailTable.on.ono()),
                new JoinPredicate(OdetailTable.on.qty(), ZipcodeTable.on.zip(), HgRelation.LT));

        boolean hasData = false;

        for (HgTuple t : result) {
            hasData = true;
            Order o = t.get(OrderTable.ID);
            Odetail od = t.get(OdetailTable.ID);
            Zipcode z = t.get(ZipcodeTable.ID);

            if (o.ono != od.ono || od.qty >= z.zip) fail();
        }

        if (!hasData) fail();

    }

    @Test
    public void testMultiJoinManual() {
        HgTupleStream result = HgDB.join(
                OrderTable.on.ono(), OdetailTable.on.ono()
        );
        result = result.joinOn(OdetailTable.on.qty());
        result = HgDB.join(result, ZipcodeTable.on.zip(), HgRelation.LT);
        boolean hasData = false;

        for (HgTuple t : result) {
            hasData = true;
            Order o = t.get(OrderTable.ID);
            Odetail od = t.get(OdetailTable.ID);
            Zipcode z = t.get(ZipcodeTable.ID);

            if (o.ono != od.ono || od.qty >= z.zip) fail();
        }

        if (!hasData) fail();

    }

    @Test
    public void testMultiJoinReset() {
        HgTupleStream result = HgDB.join(
                new JoinPredicate(OrderTable.on.ono(), OdetailTable.on.ono()),
                new JoinPredicate(OdetailTable.on.qty(), ZipcodeTable.on.zip(), HgRelation.LT));

        int count = 0;
        for (HgTuple t : result) {
            ++count;
            Order o = t.get(OrderTable.ID);
            Odetail od = t.get(OdetailTable.ID);
            Zipcode z = t.get(ZipcodeTable.ID);

            if (o.ono != od.ono || od.qty >= z.zip) fail();
        }

        if (count == 0) fail();

        result.reset();
        count = 0;
        for (HgTuple t : result) {
            ++count;
        }
        if (count == 0) fail();
    }

    public static HgTupleStream noIndexStream(HgTupleStream src) {
        return new HgWrappedTupleStream(src) {
            public boolean isIndexed() {
                return false;
            }
        };
    }

    @BeforeClass
    public static void setup() {
        zips = new Zipcode[]{
                new Zipcode(67226, "Wichita"),
                new Zipcode(60606, "Fort Dodge"),
                new Zipcode(50302, "Kansas City"),
                new Zipcode(54444, "Columbia"),
                new Zipcode(66002, "Liberal"),
                new Zipcode(61111, "Fort Hays")
        };

        /*
         * NOTE: The for loops inserting into the tables are only necessary
         * when the bytecode hooks are not used to insert into the tables. They
         * simply serve the purpose of adding more data into the database when hooks
         * are present.
         */

        //for (Zipcode z : zips) ZipcodeTable.insert(z);

        emps = new Employee[]{
                new Employee(1000, "Jones", zips[0], "12-DEC-95"),
                new Employee(1001, "Smith", zips[1], "01-JAN-92"),
                new Employee(1002, "Brown", zips[2], "01-SEP-94"),
        };
        //for (Employee e : emps) EmployeeTable.insert(e);

        customers = new Customer[]{
                new Customer(1111, "Charles", "123 Main St.", zips[0], "316-636-5555"),
                new Customer(2222, "Bertram", "237 Ash Avenue", zips[0], "316-689-5555"),
                new Customer(3333, "Barbara", "111 Inwood St.", zips[1], "316-111-1234")
        };
        //for (Customer c : customers) CustomerTable.insert(c);

        parts = new Part[]{
                new Part(10506, "Land Before Time I", 200, 19.99, 20),
                new Part(10507, "Land Before Time II", 156, 19.99, 20),
                new Part(10508, "Land Before Time III", 190, 19.99, 20),
                new Part(10509, "Land Before Time IV", 60, 19.99, 20),
                new Part(10601, "Sleeping Beauty", 300, 24.99, 20),
                new Part(10701, "When Harry Met Sally", 120, 19.99, 30),
                new Part(10800, "Dirty Harry", 140, 14.99, 30),
                new Part(10900, "Dr. Zhivago", 100, 24.99, 30),
        };
        //for (Part p : parts) PartTable.insert(p);

        Random rn = new Random();
        orders = new Order[TEST_SIZE];
        for (int i = 0; i < orders.length; ++i) {
            orders[i] = new Order(
                    (rn.nextInt(4)) + 1020,
                    customers[rn.nextInt(customers.length)],
                    emps[rn.nextInt(emps.length)],
                    new Date(rn.nextInt(Integer.MAX_VALUE)).toString(),
                    new Date(rn.nextInt(Integer.MAX_VALUE)).toString());
        }
        //for (Order o : orders) OrderTable.insert(o);

        odetails = new Odetail[orders.length];
        for (int i = 0; i < odetails.length; ++i) {
            odetails[i] = new Odetail(
                    orders[rn.nextInt(orders.length)],
                    parts[rn.nextInt(parts.length)],
                    rn.nextInt(10));
        }
        //for (Odetail o : odetails) OdetailTable.insert(o);

        correctResult = HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono());

        long count = 0;

        for (HgTuple t : correctResult) {
            ++count;
        }

        correctCount = count;

        if (count == 0) {
            throw new IllegalStateException("Database has no elements!");
        }
    }
}
