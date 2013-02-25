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

		Calendar cal = new GregorianCalendar(2013, 2-1, 24);
		lines.moveForwardTo(cal.getTime());
		lines.print();
//		lines.get(27).printLedger();

	}

}
