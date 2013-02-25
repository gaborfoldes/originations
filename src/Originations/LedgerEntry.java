package Originations;

import java.util.*;
import java.text.*;

public class LedgerEntry {

	private String type;
	private Date postDate;
	private double amount;
	private int credit;
	
	public LedgerEntry(String type, Date date, double amount, boolean credit) {
		this.type = type;
		this.postDate = date;
		this.amount = amount;
		this.credit = credit ? 1 : -1;
	}
	
	public String getType() {
		return type;
	}

	public Date getPostDate() {
		return postDate;
	}

	public double getAmount() {
		return amount * credit;
	}
	
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
		return formatter.format(getPostDate()) + " " + getType() + " " + new DecimalFormat("0.00").format(getAmount());
	}
	
	
}
