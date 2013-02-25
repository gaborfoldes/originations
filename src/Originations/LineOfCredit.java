package Originations;

import java.text.*;
import java.util.*;

public class LineOfCredit {

	private final static double MONTHLY_FEE = 6.25;
	private final static double INTEREST_RATE = 0.35;
	private final static double DRAW_FEE_PCT = 0.03;
	private final static double DRAW_FEE_MIN = 10.00;
	
	private int id;
	private String appNumber;
	private int userId;
	private String email;
	private double creditLine;
	private Date openDate;
	private Date firstDueDate;
	
	private Ledger ledger = new Ledger();

	private Calendar interestCalendar;
	private Calendar billingCalendar;
	private boolean hadDraw = false;
	
	public LineOfCredit(Date date, int id, String appNumber, int userId, String email, double creditLine, Date firstDueDate) {
		this.id = id;
		this.appNumber = appNumber;
		this.userId = userId;
		this.email = email;
		this.openDate = date;
		this.creditLine = creditLine;
		this.firstDueDate = firstDueDate;
		interestCalendar = Calendar.getInstance();
		interestCalendar.setTime(date);
		billingCalendar = Calendar.getInstance();
		billingCalendar.setTime(date);
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
		moveForwardTo(date);
		return ledger.getInterest(date);
	}	
	
	public double getPayoff(Date date) {
		moveForwardTo(date);
		return ledger.getBalance(date);
	}	
	
	/* draws */
	public double getDrawFee(double amount) {
		return Math.max(DRAW_FEE_MIN, amount * DRAW_FEE_PCT);
	}
	
	public void draw(Date date, double amount) {
		moveForwardTo(date);
		if (!hadDraw) {
			ledger.addEntry("Fee", billingCalendar.getTime(), getMonthlyFee(), true);
			hadDraw = true;
		}
		ledger.addEntry("Principal", date, amount, true);
		ledger.addEntry("Fee", date, getDrawFee(amount), true);
	}

	/* interest accrual */
	public double getDailyInterest(Date date) {
		return INTEREST_RATE/365*ledger.getPrincipal(date);
	}	
	
	public void accrueInterest(Date date) {
		while (interestCalendar.getTime().before(date)) {
			double interest = getDailyInterest(interestCalendar.getTime());
			interestCalendar.add(Calendar.DATE, 1);
			ledger.addEntry("Interest", interestCalendar.getTime(), interest, true);
		}
	}

	/* payments */
	public void pay(Date date, double amount) {
		moveForwardTo(date);
		double fees = Math.min(ledger.getFees(date), amount);
		ledger.addEntry("Fee", date, fees, false);
		amount -= fees;
		double interest = Math.min(ledger.getInterest(date), amount);
		ledger.addEntry("Interest", date, interest, false);
		amount -= interest;
		ledger.addEntry("Principal", date, amount, false);
	}	
	
	/* billing cycles */
	public Date skipWeekEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SATURDAY: cal.add(Calendar.DATE, 2); break;
			case Calendar.SUNDAY: cal.add(Calendar.DATE, 1); break;
		}
		return cal.getTime();
	}
	
	public Date getNextDueDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDueDate);
		while (!skipWeekEnd(cal.getTime()).after(date)) {
			cal.add(Calendar.MONTH, 1);
		}
		return skipWeekEnd(cal.getTime());
	}

	public Date getNextStatementDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getNextDueDate(date));
		cal.add(Calendar.DATE, -14);
		if (!cal.getTime().after(date)) {
			cal.setTime(getNextDueDate(getNextDueDate(date)));
			cal.add(Calendar.DATE, -14);
		}
		return cal.getTime();
	}
	
	public double getMonthlyFee() { return MONTHLY_FEE; }
	
	public void addBillingCycles(Date date) {
		while (!getNextStatementDate(billingCalendar.getTime()).after(date)) {
			billingCalendar.setTime(getNextStatementDate(billingCalendar.getTime()));
			if (hadDraw) ledger.addEntry("Fee", billingCalendar.getTime(), getMonthlyFee(), true);
		}
	}
	
	public void moveForwardTo(Date date) {
		accrueInterest(date);
		addBillingCycles(date);
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
