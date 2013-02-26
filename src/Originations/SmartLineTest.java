package Originations;

import java.util.*;
import java.io.*;

public class SmartLineTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		Lines lines = new Lines();
		lines.loadLines("/Users/Gabor/Code/lines.tsv");
		lines.loadDraws("/Users/Gabor/Code/draws.tsv");
		lines.loadPayments("/Users/Gabor/Code/payments.tsv");

		Calendar cal = new GregorianCalendar(2013, Calendar.MARCH, 1);
		lines.moveForwardTo(cal.getTime());
		List<LineOfCredit> linesDue = lines.getLinesDue(cal.getTime());
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

}
