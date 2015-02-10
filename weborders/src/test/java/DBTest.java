import static org.junit.Assert.fail;

import java.sql.Date;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mercurydb.queryutils.JoinDriver;
import org.mercurydb.queryutils.JoinRecord;
import org.mercurydb.queryutils.JoinResult;
import org.mercurydb.queryutils.JoinStream;

import weborders.db.OdetailTable;
import weborders.db.OrderTable;
import weborders.db.PartTable;
import weborders.source.Employee;
import weborders.source.Odetail;
import weborders.source.Order;
import weborders.source.Part;
import weborders.source.Zipcode;
import weborders.source.Customer;

public class DBTest {
	public static final int TEST_SIZE = 5000;
	
	static Zipcode[] zips;
	static Employee[] emps;
	static Customer[] customers;
	static Part[] parts;
	static Order[] orders;
	static Odetail[] odetails;
	
	static JoinResult correctResult;
	static long correctCount;
	
	@Test
	public void testFilter1() {
		OrderTable
		.scan()
		.filter(OrderTable.fieldOno(), 1020);
	}
	
	@Test
	public void testHashJoin() {
		// Hash Join
		long count = 0;
		for (JoinRecord jr : JoinDriver.joinHash(
				OrderTable.scan().joinOn(OrderTable.fieldOno()), 
				OdetailTable.scan().joinOn(OdetailTable.fieldOno())).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testNestedLoops() {
		long count = 0;
		for (JoinRecord jr : JoinDriver.joinNestedLoops(
				OrderTable.scan().joinOn(OrderTable.fieldOno()), 
				OdetailTable.scan().joinOn(OdetailTable.fieldOno())).elements()) {
			++count;
		}
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexScan() {
		// Index Scan
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				OrderTable.scan().joinOn(OrderTable.fieldOno()), 
				OdetailTable.joinOnFieldOno()).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexScan2() {
		
		// Index Scan
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				OrderTable.joinOnFieldOno(), 
				OdetailTable.scan().joinOn(OdetailTable.fieldOno())).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexIntersection() {
		
		// Index Intersection
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				OrderTable.joinOnFieldOno(), 
				OdetailTable.joinOnFieldOno()).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testReset() {
		JoinStream a = OrderTable.scan().joinOn(OrderTable.fieldOno());
		JoinStream b = OdetailTable.scan().joinOn(OdetailTable.fieldOno());
		JoinResult c = JoinDriver.join(a, b);
		
		for (JoinRecord jr : c.elements());
		c.reset();
		for (JoinRecord jr : c.elements());
		c.reset();
		
		long count = 0;
		for (JoinRecord jr : c.elements()) {
			++count;
		}
		if (count != correctCount) fail();
	}
	
	@BeforeClass
	public static void setup() {
		zips = new Zipcode[] {
				new Zipcode(67226, "Wichita"),
				new Zipcode(60606, "Fort Dodge"),
				new Zipcode(50302, "Kansas City"),
				new Zipcode(54444, "Columbia"),
				new Zipcode(66002, "Liberal"),
				new Zipcode(61111, "Fort Hays")
		};

		emps = new Employee[] {
				new Employee(1000, "Jones", zips[0], "12-DEC-95"),
				new Employee(1001, "Smith", zips[1], "01-JAN-92"),
				new Employee(1002, "Brown", zips[2], "01-SEP-94"),
		};

		customers = new Customer[] {
				new Customer(1111, "Charles", "123 Main St.", zips[0], "316-636-5555"),
				new Customer(2222, "Bertram", "237 Ash Avenue", zips[0], "316-689-5555"),
				new Customer(3333, "Barbara", "111 Inwood St.", zips[1], "316-111-1234")
		};
		
		parts = new Part[] {
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
					(rn.nextInt(4))+1020, 
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
				OrderTable.scan().joinOn(OrderTable.fieldOno()), 
				OdetailTable.scan().joinOn(OdetailTable.fieldOno()));
		
		long count = 0;
		for (JoinRecord jr : correctResult.elements()) {
			++count;
		}
		correctCount = count;
	}

}
