package Originations;

import java.util.*;
import java.io.*;

public class SmartLineTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		/* setup */
		Lines lines = new Lines();
		lines.loadLines("/Users/Gabor/Code/lines.tsv");
		lines.loadDraws("/Users/Gabor/Code/draws.tsv");
		lines.loadPayments("/Users/Gabor/Code/payments.tsv");

		Calendar cal = new GregorianCalendar(2013, 1, 24);

		
/*
		LineOfCredit loc = lines.get(2911);
		System.out.println(loc.getOpenDate());

		Calendar cal = new GregorianCalendar(2013, 0, 11);
		loc.draw(cal.getTime(), 200);
		cal.set(2013,1,24);
		loc.pay(cal.getTime(), 25);
		lines.print(2911);
		loc.printLedger();
*/
		lines.get(2911).accrueInterest(cal.getTime());
		lines.get(2911).printLedger();
		lines.print(2911);

		cal.set(2013,0,25);
		System.out.println(lines.get(2911).getOutstanding(cal.getTime()));

	}

}


/*
Calendar cal = new GregorianCalendar(2013, 1, 1);
lines.put(cal.getTime(), 1, "TEST_APP", 100, "gabor@billfloat.com", 500);
lines.putFromString("2012-12-20	3	L2043MMTK	614280	plb2002@msn.com	350.00");
LineOfCredit loc = lines.get(1);
lines.get(1).draw(cal.getTime(), 200);
cal.set(2013, 1, 4);
loc.draw(cal.getTime(), 550);
cal.set(2013, 1, 7);
double payoff = loc.getPayoff(cal.getTime());
loc.pay(cal.getTime(), 155);
loc.printLedger();
*/
