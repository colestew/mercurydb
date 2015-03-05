import org.junit.BeforeClass;
import org.junit.Test;
import org.mercurydb.queryutils.HgPredicate;
import org.mercurydb.queryutils.HgQuery;
import org.mercurydb.queryutils.HgStream;
import org.mercurydb.queryutils.HgTuple;
import weborders.db.OdetailTable;
import weborders.db.OrderTable;
import weborders.db.ZipcodeTable;
import weborders.source.*;

import java.sql.Date;
import java.util.Random;

import static org.junit.Assert.fail;

public class DBTest {
    public static final int TEST_SIZE = 5000;

    static Zipcode[] zips;
    static Employee[] emps;
    static Customer[] customers;
    static Part[] parts;
    static Order[] orders;
    static Odetail[] odetails;

    static HgStream correctResult;
    static long correctCount;

    @Test
    public void testFilter1() {
        OrderTable
                .scan() // don't use index
                .filter(OrderTable.equal.ono(1020));

        // ==

        OrderTable.queryOno(1020);

        // TODO this syntax (make it efficient too by selecting index)
        // OrderTable.query(OrderTable.on.ono, 1020);

        // static class solution with FieldExtractableValue
        HgQuery.query(OrderTable.eq.ono(5), OrderTable.eq.cno(null));

        HgQuery.query(OrderTable.lt.ono(5));

        HgQuery.query(OrderTable.predicate.ono(value -> value < 5));

        HgQuery.query(OrderTable.predicate.ono(new HgPredicate<Integer>() {
            public boolean test(Integer value) {
                return value < 5;
            }
        }));

        OrderTable.query(OrderTable.equal.ono(5), OrderTable.equal.cno(null));
    }

    @Test
    public void testHashJoin() {
        // Hash Join
        long count = 0;
        HgPolyStream result = JoinDriver.joinHash(
                OrderTable.joinOnOno(),
                OdetailTable.scan().joinOn(OdetailTable.on.ono));

        count = result.getCardinality();

        if (count != correctCount) fail();
    }

    @Test
    public void testNestedLoops() {
        long count = 0;
        for (HgTuple jr : JoinDriver.joinNestedLoops(
                OrderTable.scan().joinOn(OrderTable.on.ono),
                OdetailTable.scan().joinOn(OdetailTable.on.ono)).elements()) {
            ++count;
        }
        if (count != correctCount) fail();
    }

    @Test
    public void testIndexScan() {
        // Index Scan
        long count = 0;
        for (HgTuple jr : JoinDriver.join(
                OrderTable.scan().joinOn(OrderTable.on.ono),
                OdetailTable.joinOnOno()).elements()) {
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testIndexScan2() {

        // Index Scan
        long count = 0;
        for (HgTuple jr : JoinDriver.join(
                OrderTable.joinOnOno(),
                OdetailTable.scan().joinOn(OdetailTable.on.ono)).elements()) {
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testIndexIntersection() {

        // Index Intersection
        long count = 0;
        for (HgTuple jr : JoinDriver.join(
                OrderTable.joinOnOno(),
                OdetailTable.joinOnOno()).elements()) {
            ++count;
        }

        if (count != correctCount) fail();
    }

    @Test
    public void testReset() {
        HgMonoStream<?> a = OrderTable.scan().joinOn(OrderTable.on.ono);
        HgMonoStream<?> b = OdetailTable.scan().joinOn(OdetailTable.on.ono);
        HgPolyStream c = JoinDriver.join(a, b);

        // TODO make this syntax happen
        // HgPolyStream d = JoinDriver.join(OrderTable.on.ono, OdetailTable.on.ono);

        for (HgTuple jr : c.elements()) ;
        c.reset();
        for (HgTuple jr : c.elements()) ;
        c.reset();

        long count = 0;
        for (HgTuple jr : c.elements()) {
            ++count;
        }
        if (count != correctCount) fail();
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

        emps = new Employee[]{
                new Employee(1000, "Jones", zips[0], "12-DEC-95"),
                new Employee(1001, "Smith", zips[1], "01-JAN-92"),
                new Employee(1002, "Brown", zips[2], "01-SEP-94"),
        };

        customers = new Customer[]{
                new Customer(1111, "Charles", "123 Main St.", zips[0], "316-636-5555"),
                new Customer(2222, "Bertram", "237 Ash Avenue", zips[0], "316-689-5555"),
                new Customer(3333, "Barbara", "111 Inwood St.", zips[1], "316-111-1234")
        };

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

        odetails = new Odetail[orders.length];
        for (int i = 0; i < odetails.length; ++i) {
            odetails[i] = new Odetail(
                    orders[rn.nextInt(orders.length)],
                    parts[rn.nextInt(parts.length)],
                    rn.nextInt(10));
        }

        correctResult = JoinDriver.joinHash(
                OrderTable.scan().joinOn(OrderTable.on.ono),
                OdetailTable.scan().joinOn(OdetailTable.on.ono));

        long count = 0;
        for (HgTuple jr : correctResult.elements()) {
            ++count;
        }
        System.out.println("DB Size: " + ZipcodeTable.table.size());
        correctCount = count;
    }

}
