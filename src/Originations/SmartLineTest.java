package Originations;

import java.util.*;
import java.io.*;

public class SmartLineTest {

	private static Lines lines = new Lines();

	private static Date dt(int year, int month, int date) {
		return new GregorianCalendar(year, month-1, date).getTime();
	}
	
	private static Date today() {
		return Calendar.getInstance().getTime();
	}

	private static void loadData() throws FileNotFoundException {
		lines.loadLines("/Users/Gabor/Code/lines.tsv");
		lines.loadDraws("/Users/Gabor/Code/draws.tsv");
		lines.loadPayments("/Users/Gabor/Code/payments.tsv");
	}
	
	private static void printDue(Date date) {
		lines.moveForwardTo(date);
		List<LineOfCredit> linesDue = lines.getLinesDue(date);
		Collections.sort(linesDue, new Comparator<LineOfCredit>() {
			public int compare(LineOfCredit a, LineOfCredit b) {
				return Integer.signum(a.getId() - b.getId());
			}
		});
		System.out.println(lines.values().iterator().next().getHeaderString());
		for(LineOfCredit line : linesDue) {
			System.out.println(line.toString());
		}
	}
	
	private static void printByAppNumber(String appNumber) {
		LineOfCredit loc = lines.getByAppNumber(appNumber);
		if (loc != null) {
			loc.printHeader();
			loc.print();
			System.out.println();
			loc.printLedger();
		}
	}
	
	private static void printByEmail(String email) {
		LineOfCredit loc = lines.getByEmail(email);
		if (loc != null) {
			loc.printHeader();
			loc.print();
			System.out.println();
			loc.printLedger();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
//		loadData();
//		printDue(dt(2013, 3, 1));

//		lines.moveForwardTo(today());
//		printByEmail("gabor@billfloat.com");

		LineOfCredit loc = new LineOfCredit(dt(2013,1,11), 1, "", 0, "", 750, dt(2013,2,15));
		loc.printHeader();
		loc.print();
		loc.draw(dt(2013,1,11), 200);
		loc.print();
		loc.pay(dt(2013,2,15), 25);
		loc.print();
		loc.moveForwardTo(dt(2013,2,26));
		loc.print();
		System.out.println();
		loc.printLedger();
		
	}

}
