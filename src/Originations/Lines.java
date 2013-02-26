package Originations;

import java.util.*;
import java.io.*;

public class Lines {

	private Map<Integer,LineOfCredit> lines = new HashMap<Integer,LineOfCredit>();
	private static final String SEPARATOR = "\t";

	public Lines() {}
	
	public void put(LineOfCredit line) {
		lines.put(new Integer(line.getId()), line);
	}

	public void put(Date date, int id, String appNumber, int userId, String email, double creditLine, Date firstDueDate) {
		lines.put(new Integer(id), new LineOfCredit(date, id, appNumber, userId, email, creditLine, firstDueDate));
	}

	public LineOfCredit get(int id) { return lines.get(Integer.valueOf(id)); }
	
	public Collection<LineOfCredit> values() { return lines.values(); }
	
	public void putFromString(String data) {
		String[] items = data.split(SEPARATOR);
		Date date = java.sql.Date.valueOf(items[0]);
		int id = Integer.valueOf(items[1]);
		String appNumber = items[2];
		int userId = Integer.valueOf(items[3]);
		String email = items[4];
		double creditLine = Double.valueOf(items[5]);
		Date firstDueDate = java.sql.Date.valueOf(items[6]);
		put(date, id, appNumber, userId, email, creditLine, firstDueDate);
	}
	
	public void drawFromString(String data) {
		String[] items = data.split(SEPARATOR);
		int id = Integer.valueOf(items[0]);
		Date date = java.sql.Date.valueOf(items[1]);
		double amount = Double.valueOf(items[2]);
		LineOfCredit loc = get(id);
		if (loc != null) { loc.draw(date, amount); }
		else System.out.println(id);
	}
	
	public void payFromString(String data) {
		String[] items = data.split(SEPARATOR);
		int id = Integer.valueOf(items[0]);
		Date date = java.sql.Date.valueOf(items[1]);
		double amount = Double.valueOf(items[2]);
		LineOfCredit loc = get(id);
		if (loc != null) { loc.pay(date, amount); }
		else System.out.println(id);
	}
	
	/* diagnostic info */
	public void print() {
		System.out.println(lines.values().iterator().next().getHeaderString());
		for(LineOfCredit line : lines.values()) {
			System.out.println(line.toString());
		}
	}
	
	public void print(int id) {
		System.out.println(get(id).getHeaderString());
		System.out.println(get(id).toString());
	}
	
	/* load in data */
	public void loadLines(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		try {
			while (scanner.hasNextLine()){
				putFromString(scanner.nextLine());
			}
		}
		finally {
			scanner.close();
		}
	}
			
	public void loadDraws(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		try {
			while (scanner.hasNextLine()){
				drawFromString(scanner.nextLine());
			}
		}
		finally {
			scanner.close();
		}
	}

	public void loadPayments(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		try {
			while (scanner.hasNextLine()){
				payFromString(scanner.nextLine());
			}
		}
		finally {
			scanner.close();
		}
	}
	
	/* get all lines in sync */
	public void moveForwardTo(Date date) {
		for(LineOfCredit line : lines.values()) {
			line.moveForwardTo(date);
		}
	}

	/* queries */
	public List<LineOfCredit> getLinesDue(Date date) {
		List<LineOfCredit> linesDue = new ArrayList<LineOfCredit>();
		for(LineOfCredit line : lines.values()) {
			if (line.getNextDueDate().equals(date)) linesDue.add(line);
		}
		return linesDue;
	}
	
	
	
}
