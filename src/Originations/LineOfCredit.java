package Originations;

import java.text.*;
import java.util.*;

public class LineOfCredit {

	private int id;
	private String appNumber;
	private int userId;
	private String email;
	
	private double creditLine;
	private Date openDate;
	private Ledger ledger = new Ledger();
	private Calendar interestCalendar;
	
	public LineOfCredit(Date date, int id, String appNumber, int userId, String email, double creditLine) {
		this.id = id;
		this.appNumber = appNumber;
		this.userId = userId;
		this.email = email;
		openDate = date;
		this.creditLine = creditLine;
		interestCalendar = Calendar.getInstance();
		interestCalendar.setTime(date);
	}
	
	/* getters */
	public int getId() { return id; }
	public int getUserId() { return userId; }
	public String getAppNumber() { return appNumber; }
	public String getEmail() { return email; }
	public double getCreditLine() { return creditLine; }
	public Date getOpenDate() {return openDate; }

	/* ledger amounts */
	public double getOutstanding(Date date) { return ledger.getPrincipal(date); }

	public double getFees(Date date) { return ledger.getFees(date); }	
	
	public double getAccruedInterest(Date date) {
		accrueInterest(date);
		return ledger.getInterest(date);
	}	
	
	public double getPayoff(Date date) {
		accrueInterest(date);
		return ledger.getBalance(date);
	}	
	
	/* draws */
	public double getDrawFee(double amount) {
		return Math.max(10, amount * 0.03);
	}
	
	public void draw(Date date, double amount) {
		accrueInterest(date);
		ledger.addEntry("Principal", date, amount, true);
		ledger.addEntry("Fee", date, getDrawFee(amount), true);
	}

	/* interest accrual */
	public double getDailyInterest(Date date) {
		return 0.35/365*ledger.getPrincipal(date);
	}	
	
	public void accrueInterest(Date date) {
		while (interestCalendar.getTime().before(date)) {
			Date intDate = interestCalendar.getTime();
			ledger.addEntry("Interest", intDate, getDailyInterest(intDate), true);
			interestCalendar.add(Calendar.DATE, 1);
		}
	}

	/* payments */
	public void pay(Date date, double amount) {
		accrueInterest(date);
		double fees = Math.min(ledger.getFees(date), amount);
		ledger.addEntry("Fee", date, fees, false);
		amount -= fees;
		double interest = Math.min(ledger.getInterest(date), amount);
		ledger.addEntry("Interest", date, interest, false);
		amount -= interest;
		ledger.addEntry("Principal", date, amount, false);
	}	
	
	/* diagnostic info */
	public String toString() {
		NumberFormat twoFixed = new DecimalFormat("0.00");
		//Formatter formatter = new Formatter();
		return
			Integer.toString(id) +
			"\t" + appNumber +
			"\t" + Integer.toString(userId) +
			"\t" + String.format("%-22s", email) +
			"\t" + twoFixed.format(creditLine) +
			"\t" + twoFixed.format(ledger.getPrincipal()) +
			"\t" + twoFixed.format(ledger.getFees()) +
			"\t" + twoFixed.format(ledger.getInterest()) +
			"\t" + twoFixed.format(ledger.getBalance());
	}

	public String getHeaderString() {
		return
			"ID" +
			"\t" + "AppNumber" +
			"\t" + "UserID" +
			"\t" + String.format("%-22s", "Email") +
			"\t" + "Line" +
			"\t" + "Out" +
			"\t" + "Fees" +
			"\t" + "Int" +
			"\t" + "Payoff";
	}
	
	public void printLedger() {
		ledger.print();
	}
	
}
