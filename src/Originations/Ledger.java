package Originations;

import java.util.*;

public class Ledger {

	private List<LedgerEntry> ledgerEntries;
	
	public Ledger() {
		ledgerEntries = new ArrayList<LedgerEntry>();
	}
	
	public void addEntry(String type, Date date, double amount, boolean credit) {
		if (amount != 0) ledgerEntries.add(new LedgerEntry(type, date, amount, credit));
	}
	
	public int size() { return ledgerEntries.size(); }
	
	public double getPrincipal(Date date) {
		double principal = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				principal += (entry.getType() == "Principal") ? entry.getAmount() : 0;
		}
		return principal;
	}
	
	public double getPrincipal() {
		return getPrincipal(Calendar.getInstance().getTime());
	}
	
	public double getFees(Date date) {
		double fees = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				fees += (entry.getType() == "Fee") ? entry.getAmount() : 0;
		}
		return fees;
	}

	public double getFees() {
		return getFees(Calendar.getInstance().getTime());
	}
	
	public double getInterest(Date date) {
		double interest = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				interest += (entry.getType() == "Interest") ? entry.getAmount() : 0;
		}
		return interest;
	}	
	
	public double getInterest() {
		return getInterest(Calendar.getInstance().getTime());
	}

	public double getBalance(Date date) {
		double balance = 0;
		for (LedgerEntry entry : ledgerEntries) {
			if (entry.getPostDate().compareTo(date) <= 0)
				balance += entry.getAmount();
		}
		return balance;
	}	
	
	public double getBalance() {
		return getBalance(Calendar.getInstance().getTime());
	}

	public void print() {
		Collections.sort(ledgerEntries, new Comparator<LedgerEntry>() {
		    public int compare(LedgerEntry a, LedgerEntry b) {
		        return a.getPostDate().compareTo(b.getPostDate());
		    }
		});
		for (LedgerEntry entry : ledgerEntries) {
			System.out.println(entry.toString());
		}
	}	
	
	
}
