package Originations;

import java.text.*;
import java.util.*;

public class LineOfCredit {

	private final static double MONTHLY_FEE = 6.25;
	private final static double INTEREST_RATE = 0.35;
	private final static double DRAW_FEE_PCT = 0.03;
	private final static double DRAW_FEE_MIN = 10.00;
	private final static int DUE_AFTER = 14;
	
	private int id;
	private String appNumber;
	private int userId;
	private String email;
	private double creditLine;
	private Date openDate;
	private Date firstDueDate;
	
	private Ledger ledger = new Ledger();
	private Map<Date,Double> payments = new HashMap<Date,Double>();

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


	/* fees */
	public double getMonthlyFee() { return MONTHLY_FEE; }
	
	public double getDrawFee(double amount) {
		return Math.max(DRAW_FEE_MIN, amount * DRAW_FEE_PCT);
	}
	
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
		payments.put(date, Double.valueOf(amount));
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

	public Date getNextDueDate() {
		return skipWeekEnd(getNextDueDate(billingCalendar.getTime()));
	}
	
	public Date getPreviousDueDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDueDate);
		Date previousDueDate = cal.getTime();
		while (!skipWeekEnd(cal.getTime()).after(date)) {
			previousDueDate = cal.getTime();
			cal.add(Calendar.MONTH, 1);
		}
		return skipWeekEnd(previousDueDate);
	}

	public Date getPreviousDueDate() {
		return skipWeekEnd(getNextDueDate(billingCalendar.getTime()));
	}
	
	public Date getNextStatementDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getNextDueDate(date));
		cal.add(Calendar.DATE, -DUE_AFTER);
		if (!cal.getTime().after(date)) {
			cal.setTime(getNextDueDate(getNextDueDate(date)));
			cal.add(Calendar.DATE, -DUE_AFTER);
		}
		return cal.getTime();
	}

	public Date getNextStatementDate() {
		return getNextStatementDate(billingCalendar.getTime());
	}
	
	public Date getPreviousStatementDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getNextDueDate(date));
		cal.add(Calendar.DATE, -DUE_AFTER);
		if (cal.getTime().after(date)) {
			cal.setTime(getPreviousDueDate(date));
			cal.add(Calendar.DATE, -DUE_AFTER);
		}
		return cal.getTime();
	}

	public Date getPreviousStatementDate() {
		return getPreviousStatementDate(billingCalendar.getTime());
	}
	
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
	
	/* minimum payment */
	public double getLastDrawOutstanding(Date date) {
		return ledger.getLastDrawPrincipal(date);
	}

	public double getDenominator(double line) {
		double denominator;
		if (this.creditLine <= 350) denominator = 5;
		else if (this.creditLine <= 500) denominator = 6;
		else if (this.creditLine <= 750) denominator = 8;
		else denominator = 10;
		return denominator;
	}
	
	public double getMinPayment(Date date) {
		return getLastDrawOutstanding(date) / getDenominator(this.creditLine);
	}
	
	public double getPayments(Date start, Date end) {
		double paid = 0;
		for (Date day : payments.keySet()) {
			if (!day.before(start) && !day.after(end)) paid += payments.get(day).doubleValue();
		}
		return paid;
	}
	
	public double getPaymentDue(Date date) {
		Calendar cal = Calendar.getInstance();
		Date lastStatementDate = getPreviousStatementDate(date);
		cal.setTime(lastStatementDate);
		cal.add(Calendar.DATE, DUE_AFTER);
		double paid = getPayments(lastStatementDate, cal.getTime()); 
		double minPayment = Math.max(0, getMinPayment(date) - paid);
		return Math.min(minPayment, getOutstanding(date));
	}
	
	/* diagnostic info */
	public String toString(Date date) {
		NumberFormat twoFixed = new DecimalFormat("0.00");
		SimpleDateFormat mdyyyy = new SimpleDateFormat("M/d/yyyy");
		return
			Integer.toString(id) +
			"\t" + appNumber +
			"\t" + Integer.toString(userId) +
			"\t" + String.format("%-30s", email) +
			"\t" + twoFixed.format(creditLine) +
			"\t" + twoFixed.format(getOutstanding(date)) +
			"\t" + twoFixed.format(getFees(date)) +
			"\t" + twoFixed.format(getAccruedInterest(date)) +
			"\t" + twoFixed.format(getPayoff(date)) +
			"\t" + mdyyyy.format(getPreviousDueDate(date)) +
			"\t" + mdyyyy.format(getNextDueDate(date)) +
			"\t" + twoFixed.format(getPaymentDue(date));
	}

	public String toString() {
		return toString(Calendar.getInstance().getTime());
	}
	
	public String getHeaderString() {
		return
			"ID" +
			"\t" + "AppNumber" +
			"\t" + "UserID" +
			"\t" + String.format("%-30s", "Email") +
			"\t" + "Line" +
			"\t" + "Out" +
			"\t" + "Fees" +
			"\t" + "Int" +
			"\t" + "Payoff" +
			"\t" + "PrevDate" +
			"\t" + "NextDate" +
			"\t" + "Due";
	}
	
	public void printLedger() {
		ledger.print();
	}
	
}
