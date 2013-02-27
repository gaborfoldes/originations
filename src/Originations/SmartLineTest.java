package Originations;

import java.util.*;
import java.io.*;

public class SmartLineTest {

	static Lines lines = new Lines();

	public static void printDue(Date date) {
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
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		lines.loadLines("/Users/Gabor/Code/lines.tsv");
		lines.loadDraws("/Users/Gabor/Code/draws.tsv");
		lines.loadPayments("/Users/Gabor/Code/payments.tsv");

		//Calendar cal = new GregorianCalendar(2013, Calendar.MARCH, 1);
		//printDue(new GregorianCalendar(2013, Calendar.MARCH, 1).getTime());
		LineOfCredit loc = lines.getByAppNumber("LG24LZ9J4");
		if (loc != null) {
			loc.printLedger();
			loc.print();
		}

	}

}
