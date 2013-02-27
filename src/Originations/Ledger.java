package Originations;

import java.util.*;

/** Class to represent the ledger for a single loan instrument.
 *  The ledger contains entries which fall into three major buckets:
 *  principal, interest and fees.
 *
 * @see LedgerEntry
 * @see LineOfCredit
 */
public class Ledger {

	private List<LedgerEntry> ledgerEntries;
	
	/** Initializes the ledger by creating an array of ledger entries.
	 */
	public Ledger() {
		ledgerEntries = new ArrayList<LedgerEntry>();
	}
	
	/**
	 * Books a new ledger entry. 
	 *
	 * @param  type   denotes the entry type (only LedgerEntry.PRINCIPAL, LedgerEntry.INTEREST and LedgerEntry.FEE are used)
	 * @param  desc   description of entry (e.g. "Monthly fee")
	 * @param  date   post date for an entry
	 * @param  amount dollar amount of the entry (unsigned)
	 * @param  credit specifies if the entry is a credit or a debit
	 */
	public void addEntry(int type, String desc, Date date, double amount, boolean credit) {
		if (amount != 0) ledgerEntries.add(new LedgerEntry(type, desc, date, amount, credit));
	}
	
	/**
	 * Returns the number of entries in the ledger.
	 * 
	 * @return  number of ledger entries
	 */
	public int size() { return ledgerEntries.size(); }
	
	/**
	 * Computes the principal balance on the given date by adding up
	 * all entries with a principal type until the given date
	 * 
	 * @param  date  last post date to consider
	 * @return  principal balance
	 */
	public double getPrincipal(Date date) {
		double principal = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				principal += (entry.getType() == LedgerEntry.PRINCIPAL) ? entry.getAmount() : 0;
		}
		return principal;
	}
	
	/**
	 * Computes the principal balance today
	 * 
	 * @return  principal balance
	 */
	public double getPrincipal() {
		return getPrincipal(Calendar.getInstance().getTime());
	}
	
	/**
	 * Computes the outstanding fees on the given date by adding up
	 * all entries with a fee type until the given date
	 * 
	 * @param  date  last post date to consider
	 * @return  fees outstanding
	 */
	public double getFees(Date date) {
		double fees = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				fees += (entry.getType() == LedgerEntry.FEE) ? entry.getAmount() : 0;
		}
		return fees;
	}

	/**
	 * Computes outstanding fees today
	 * 
	 * @return  fees outstanding
	 */
	public double getFees() {
		return getFees(Calendar.getInstance().getTime());
	}
	
	/**
	 * Computes the accrued interest on the given date by adding up
	 * all entries with an interest type until the given date
	 * 
	 * @param  date  last post date to consider
	 * @return  accrued interest
	 */
	public double getInterest(Date date) {
		double interest = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				interest += (entry.getType() == LedgerEntry.INTEREST) ? entry.getAmount() : 0;
		}
		return interest;
	}	
	
	/**
	 * Computes the accrued interest today
	 * 
	 * @return  accrued interest
	 */
	public double getInterest() {
		return getInterest(Calendar.getInstance().getTime());
	}

	/**
	 * Computes the total ledger balance on the given date by adding up
	 * all entries until the given date
	 * 
	 * @param  date  last post date to consider
	 * @return  total ledger balance
	 */
	public double getBalance(Date date) {
		double balance = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				balance += entry.getAmount();
		}
		return balance;
	}	
	
	/**
	 * Computes the total ledger balance for today
	 * 
	 * @return  total ledger balance
	 */
	public double getBalance() {
		return getBalance(Calendar.getInstance().getTime());
	}

	/**
	 * Sorts the entries by date.  Helper function used in {@link #getLastDrawPrincipal(Date)}.
	 */
	public void sort() {
		Collections.sort(ledgerEntries, new Comparator<LedgerEntry>() {
		    public int compare(LedgerEntry a, LedgerEntry b) {
		        return a.getPostDate().compareTo(b.getPostDate());
		    }
		});
	}	
	
	/**
	 * Computes the principal balance at the time of the last draw
	 * (i.e. when the last positive principal entry was booked)
	 * before the specified date.  This is used to compute minimum
	 * monthly payment.
	 * 
	 * @param  date  last post date to consider
	 * @return  principal at the time of the last draw
	 */
	public double getLastDrawPrincipal(Date date) {
		this.sort();
		double lastDraw = 0;
		double principal = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				if (entry.getType() == LedgerEntry.PRINCIPAL) {
					principal += entry.getAmount();
					if (entry.getAmount() > 0) lastDraw = principal;
				}
		}
		return lastDraw;
	}
	
	/**
	 * Prints the ledger to the screen.
	 */
	public void print() {
		this.sort();
		System.out.println(ledgerEntries.get(0).getHeaderString());
		for (LedgerEntry entry : ledgerEntries) {
			System.out.println(entry.toString());
		}
	}	
	
}
