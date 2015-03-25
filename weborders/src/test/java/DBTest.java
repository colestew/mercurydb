import org.junit.BeforeClass;
import org.junit.Test;
import org.mercurydb.queryutils.*;
import org.mercurydb.queryutils.HgTupleStream.HgTuple;
import org.mercurydb.queryutils.joiners.JoinNestedLoops;
import weborders.db.*;
import weborders.source.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.fail;

/**
 * These tests are in limbo. They are not fully comprehensive. We will
 * continue to add to these tests to get full coverage of HgDB for
 * this example.
 */
@SuppressWarnings("unused") // loop variables
public class DBTest {
    public static final int TEST_SIZE = 1000;

    static Zipcode[] zips;
    static Employee[] emps;
    static Customer[] customers;
    static Part[] parts;
    static Order[] orders;
    static Odetail[] odetails;

    static HgStream<HgTupleStream.HgTuple> correctResult;
    static long correctCount;

    @Test
    public void testFilter1() {
        boolean hasData = false;
        for (Order o : OrderTable
                .stream() // don't use index
                .filter(OrderTable.eq.ono(1020))) {
            hasData = true;
            if (o.ono != 1020) fail();
        }

        if (!hasData) fail();
    }

    @Test
    public void testFilter() {
        for (Order o : HgDB.query(OrderTable.eq.ono(1025))) {
            if (o.ono != 1025) fail();
        }
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
    public void testQuery() {
        Set<Integer> seen = new HashSet<Integer>();
        for (Order o : HgDB.query(OrderTable.eq.ono(1020))) {
            seen.add(o.ono);
        }

        if (seen.size() != 1 || !seen.contains(1020)) fail();
    }

    @Test
    public void testQueryPredicate() {
        Set<Integer> seen = new HashSet<Integer>();

        for (Order o : HgDB.query(OrderTable.predicate(new HgPredicate<Order>() {
            @Override
            public boolean test(Order value) {
                return value.ono == 1020;
            }
        }))) {
            seen.add(o.ono);
        }

        if (seen.size() != 1 || !seen.contains(1020)) fail();
    }

    @Test
    public void testQueryPredicate2() {
        Set<Integer> seen = new HashSet<Integer>();

        for (Order o : HgDB.query(OrderTable.predicates.ono(new HgPredicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return value == 1020;
            }
        }))) {
            seen.add(o.ono);
        }

        if (seen.size() != 1 || !seen.contains(1020)) fail();
    }

    @Test
    public void testFilterOr() {
        Set<Integer> seen = new HashSet<Integer>();
        for (Order o : HgDB.query(OrderTable.eq.ono(1020))
                .concat(HgDB.query(OrderTable.eq.ono(1021)))) {
            seen.add(o.ono);
        }

        if (seen.size() != 2 || !seen.contains(1020) || !seen.contains(1021)) fail();
    }

    @Test
    public void testSelfPredicate() {
        boolean hasData = false;
        for (Order o : HgDB.query(
                OrderTable.predicate(new HgPredicate<Order>() {
                    @Override
                    public boolean test(Order value) {
                        return value.ono == 1020;
                    }
                }))) {
            if (o.ono != 1020) fail();
            hasData = true;
        }

        if (!hasData) fail();
    }

    @Test
    public void testJoinPredicate() {
        boolean hasData = false;

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
            hasData = true;
            if (t.get(OrderTable.ID).ono != t.get(OdetailTable.ID).ono &&
                    t.get(OrderTable.ID).ono != 1020) fail();
        }

        if (!hasData) fail();
    }

    @Test
    public void testJoinCollection() {
        boolean hasData = false;

        HgTupleStream stream = HgDB.join(
                PartTable.stream().joinOn(PartTable.self(PartTable.ID)),
                OdetailTable.on.pnos(),
                HgRelation.IN
        );

        for (HgTuple t : stream) {
            hasData = true;
            if (!(t.get(OdetailTable.ID).pnos.contains(t.get(PartTable.ID)))) fail();
        }
    }

    @Test
    public void testHashJoin() {
        // Hash Join
        long count = 0;
        HgTupleStream result = HgDB.joinHash(
                OrderTable.on.ono(),
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
    public void testNestedLoopsLt() {
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

        System.out.println("Nested Loops Lt Time for " + count + " elements: " + (stopTime - startTime) / 1000.0);

    }

    @Test
    public void testIndexScan3() {
        // Index Scan
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
        System.out.println("Test Index Intersection.");
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
        System.out.println("Testing Join Lt Intersection");
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
        System.out.println("Testing Join Lt Scan using index A");
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
        System.out.println("Test Join Lt Scan using index B");
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
    public void testMultiJoinReset() {
        HgTupleStream result = HgDB.join(
                new JoinPredicate(OrderTable.on.ono(), OdetailTable.on.ono()),
                new JoinPredicate(OdetailTable.on.qty(), ZipcodeTable.on.zip(), HgRelation.LT));

        long startTime = System.currentTimeMillis();
        int count = 0;
        for (HgTuple t : result) {
            ++count;
            Order o = t.get(OrderTable.ID);
            Odetail od = t.get(OdetailTable.ID);
            Zipcode z = t.get(ZipcodeTable.ID);

            if (o.ono != od.ono || od.qty >= z.zip) fail();
        }
        long stopTime = System.currentTimeMillis();
        long time1 = stopTime - startTime;
        System.out.println(time1 / 1000.0 + " seconds to iterate over all " + count + " elements");

        if (count == 0) fail();

        result.reset();
        count = 0;
        startTime = System.currentTimeMillis();
        for (HgTuple t : result) {
            ++count;
        }
        stopTime = System.currentTimeMillis();
        long time2 = stopTime - startTime;
        System.out.println(time1 / 1000.0 + " seconds to iterate over all " + count + " elements");
        System.out.println("Overhead of tuple retrievals: " + (time1 - time2) / 1000.0 + " seconds");
        System.out.println("Overhead % : " + (time1 - time2) / (double) time1);
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

        for (Zipcode z : zips) ZipcodeTable.insert(z);

        emps = new Employee[]{
                new Employee(1000, "Jones", zips[0], "12-DEC-95"),
                new Employee(1001, "Smith", zips[1], "01-JAN-92"),
                new Employee(1002, "Brown", zips[2], "01-SEP-94"),
        };
        for (Employee e : emps) EmployeeTable.insert(e);

        customers = new Customer[]{
                new Customer(1111, "Charles", "123 Main St.", zips[0], "316-636-5555"),
                new Customer(2222, "Bertram", "237 Ash Avenue", zips[0], "316-689-5555"),
                new Customer(3333, "Barbara", "111 Inwood St.", zips[1], "316-111-1234")
        };
        for (Customer c : customers) CustomerTable.insert(c);

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
        for (Part p : parts) PartTable.insert(p);

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
        for (Order o : orders) OrderTable.insert(o);

        odetails = new Odetail[orders.length];
        for (int i = 0; i < odetails.length; ++i) {
            odetails[i] = new Odetail(
                    orders[rn.nextInt(orders.length)],
                    parts[rn.nextInt(parts.length)],
                    rn.nextInt(10));
        }
        for (Odetail o : odetails) OdetailTable.insert(o);

        correctResult = HgDB.join(
                OrderTable.on.ono(),
                OdetailTable.on.ono());

        long count = 0;

        for (HgTuple t : correctResult) {
            ++count;
        }

        System.out.println("Count is: " + count);
        correctCount = count;

        if (count == 0) {
            throw new IllegalStateException("Database has no elements!");
        }
    }
}
