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
		lines.loadLines("/Users/gaborfoldes/Code/lines.tsv");
		lines.loadDraws("/Users/gaborfoldes/Code/draws.tsv");
		lines.loadPayments("/Users/gaborfoldes/Code/payments.tsv");
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
			if (line.getPaymentDue() > 0) System.out.println(line.toString());
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
		
		loadData();
		lines.moveForwardTo(today());
		//printDue(dt(2013, 3, 1));

		//printByEmail("apachewriter@gmail.com");
		printByAppNumber("LG05KY420");
		//lines.get(1961).pay(dt(2013,3,15), 70);
		//lines.print(1961);
		//lines.get(1961).printLedger();

/*		
		LineOfCredit loc = new LineOfCredit(dt(2013,2,21), 1, "", 0, "", 750, dt(2013,2,22));
		loc.printHeader();
		loc.print();
		loc.draw(dt(2013,2,21), 350);
		loc.print();
		loc.pay(dt(2013,2,21), 43.75);
		loc.print();
		loc.moveForwardTo(dt(2013,2,21));
		loc.print();
		System.out.println();
		loc.printLedger();
*/		
/*

		List<LineOfCredit> linesDue = lines.getLinesDue(dt(2013,3,1));
		int sum = 0;
		int count = 0;
		for (LineOfCredit line : linesDue) {
			if ((line.payments.size() > 0) && (line.getPaymentDue(dt(2013,3,1)) > 0.01 )) {
				line.print();
				count++;
				sum += line.getPaymentDue(dt(2013,3,1));
			}
		}
		System.out.println(sum);
		System.out.println(count);
*/
		
	}

}
