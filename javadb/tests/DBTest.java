package javadb.tests;

import static org.junit.Assert.fail;

import java.sql.Date;
import java.util.Random;

import javadb.queryutils.JoinDriver;
import javadb.queryutils.JoinRecord;
import javadb.queryutils.JoinResult;
import javadb.queryutils.JoinStream;
import javadb.tests.target.customer;
import javadb.tests.target.employee;
import javadb.tests.target.odetail;
import javadb.tests.target.order;
import javadb.tests.target.part;
import javadb.tests.target.zipcode;

import org.junit.BeforeClass;
import org.junit.Test;

import outdb.odetailTable;
import outdb.orderTable;

public class DBTest {
	public static final int TEST_SIZE = 5000;
	
	static zipcode[] zips;
	static employee[] emps;
	static customer[] customers;
	static part[] parts;
	static order[] orders;
	static odetail[] odetails;
	
	static JoinResult correctResult;
	static long correctCount;
	
	@Test
	public void testFilter1() {
		orderTable
		.scan()
		.filter(orderTable.fieldOno(), 1020);
	}
	
	@Test
	public void testHashJoin() {
		// Hash Join
		long count = 0;
		for (JoinRecord jr : JoinDriver.joinHash(
				orderTable.scan().joinOn(orderTable.fieldOno()), 
				odetailTable.scan().joinOn(odetailTable.fieldOno())).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testNestedLoops() {
		long count = 0;
		for (JoinRecord jr : JoinDriver.joinNestedLoops(
				orderTable.scan().joinOn(orderTable.fieldOno()), 
				odetailTable.scan().joinOn(odetailTable.fieldOno())).elements()) {
			++count;
		}
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexScan() {
		// Index Scan
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				orderTable.scan().joinOn(orderTable.fieldOno()), 
				odetailTable.joinOnFieldOno()).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexScan2() {
		
		// Index Scan
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				orderTable.joinOnFieldOno(), 
				odetailTable.scan().joinOn(odetailTable.fieldOno())).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testIndexIntersection() {
		
		// Index Intersection
		long count = 0;
		for (JoinRecord jr : JoinDriver.join(
				orderTable.joinOnFieldOno(), 
				odetailTable.joinOnFieldOno()).elements()) {
			++count;
		}
		
		if (count != correctCount) fail();
	}
	
	@Test
	public void testReset() {
		JoinStream a = orderTable.scan().joinOn(orderTable.fieldOno());
		JoinStream b = odetailTable.scan().joinOn(odetailTable.fieldOno());
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
		zips = new zipcode[] {
				new zipcode(67226, "Wichita"),
				new zipcode(60606, "Fort Dodge"),
				new zipcode(50302, "Kansas City"),
				new zipcode(54444, "Columbia"),
				new zipcode(66002, "Liberal"),
				new zipcode(61111, "Fort Hays")
		};

		emps = new employee[] {
				new employee(1000, "Jones", zips[0], "12-DEC-95"),
				new employee(1001, "Smith", zips[1], "01-JAN-92"),
				new employee(1002, "Brown", zips[2], "01-SEP-94"),
		};

		customers = new customer[] {
				new customer(1111, "Charles", "123 Main St.", zips[0], "316-636-5555"),
				new customer(2222, "Bertram", "237 Ash Avenue", zips[0], "316-689-5555"),
				new customer(3333, "Barbara", "111 Inwood St.", zips[1], "316-111-1234")
		};
		
		parts = new part[] {
				new part(10506, "Land Before Time I", 200, 19.99, 20),
				new part(10507, "Land Before Time II", 156, 19.99, 20),
				new part(10508, "Land Before Time III", 190, 19.99, 20),
				new part(10509, "Land Before Time IV", 60, 19.99, 20),
				new part(10601, "Sleeping Beauty", 300, 24.99, 20),
				new part(10701, "When Harry Met Sally", 120, 19.99, 30),
				new part(10800, "Dirty Harry", 140, 14.99, 30),
				new part(10900, "Dr. Zhivago", 100, 24.99, 30),
		};


		Random rn = new Random();
		orders = new order[TEST_SIZE];
		for (int i = 0; i < orders.length; ++i) {
			orders[i] = new order(
					(rn.nextInt(4))+1020, 
					customers[rn.nextInt(customers.length)], 
					emps[rn.nextInt(emps.length)], 
					new Date(rn.nextInt(Integer.MAX_VALUE)).toString(), 
					new Date(rn.nextInt(Integer.MAX_VALUE)).toString());
		}

		odetails = new odetail[orders.length];
		for (int i = 0; i < odetails.length; ++i) {
			odetails[i] = new odetail(
					orders[rn.nextInt(orders.length)],
					parts[rn.nextInt(parts.length)],
					rn.nextInt(10));
		}
		
		correctResult = JoinDriver.joinHash(
				orderTable.scan().joinOn(orderTable.fieldOno()), 
				odetailTable.scan().joinOn(odetailTable.fieldOno()));
		
		long count = 0;
		for (JoinRecord jr : correctResult.elements()) {
			++count;
		}
		correctCount = count;
		System.out.println("Correct #records: " + correctCount);
	}

}
