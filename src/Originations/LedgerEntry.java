package Originations;

import java.util.*;
import java.text.*;

/** Class containing a single ledger entry.
 *
 * @see Ledger
 */
public class LedgerEntry {

	public static final int PRINCIPAL = 1;
	public static final int INTEREST = 2;
	public static final int FEE = 3;
	
	private int type;
	private String desc;
	private Date postDate;
	private double amount;
	private int credit;
	
	/**
	 * Creates a new ledger entry. 
	 *
	 * @param  type   denotes the entry type (LedgerEntry.PRINCIPAL, LedgerEntry.INTEREST or LedgerEntry.FEE)
	 * @param  desc   description of entry (e.g. "Monthly fee")
	 * @param  date   post date for an entry
	 * @param  amount dollar amount of the entry (unsigned)
	 * @param  credit specifies if the entry is a credit or a debit
	 */
	public LedgerEntry(int type, String desc, Date date, double amount, boolean credit) {
		this.type = type;
		this.desc = desc;
		this.postDate = date;
		this.amount = amount;
		this.credit = credit ? 1 : -1;
	}
	
	/**
	 * Returns the entry type.
	 * 
	 * @return type of the entry (LedgerEntry.PRINCIPAL, LedgerEntry.INTEREST or LedgerEntry.FEE)
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the entry type.
	 * 
	 * @return type of the entry (LedgerEntry.PRINCIPAL, LedgerEntry.INTEREST or LedgerEntry.FEE)
	 */
	public String getTypeString() {
		String st = "";
		switch (type) {
			case PRINCIPAL: st = "Principal"; break;
			case INTEREST: st = "Interest "; break;
			case FEE: st = "Fee      "; break;
		}
		return st;
	}

	/**
	 * Returns the entry description.
	 * 
	 * @return type of the entry (LedgerEntry.PRINCIPAL, LedgerEntry.INTEREST or LedgerEntry.FEE)
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * Returns the post date for the entry.
	 * 
	 * @return post date
	 */
	public Date getPostDate() {
		return postDate;
	}

	/**
	 * Returns the <strong>signed</strong> amount of the entry.
	 * Positive is entry is credit and negative if entry is a debit.
	 * This allows a sum of the entries to determine the current
	 * balance.
	 * 
	 * @return signed amount
	 */
	public double getAmount() {
		return amount * credit;
	}
	
	/**
	 * Printout of the entry in a simple tab-delimited format.
	 * 
	 * @return returns tab-delimited string containing post date, entry type and signed amount
	 */
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
		return
			formatter.format(getPostDate()) + "\t" +
			getTypeString() + " \t" +
			getDescription() + " \t" +
			new DecimalFormat("0.00").format(getAmount());
	}

	/**
	 * Create a header for {@link #toString()}.
	 * 
	 * @return returns tab-delimited header
	 */
	public String getHeaderString() {
		return "PostDate\tType     \tDescription\tAmount";
	}

	
}
