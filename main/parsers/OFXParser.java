package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import entities.Transaction;
import entities.TransactionList;
import main_logic.PEC;

/**
 *
 * This Parser class parses bank-generated transaction reports and turns them
 * into a list of transactions no longer than 3-months-worth.
 *
 * ( OFX = Open Financial Exchange )
 *
 * @author SPAM (Sammy Dinka, Andrey Yefermov, Pavel Danek) © 2023,
 *
 * @adapted from an Internet version of a OFX parser, written by Federico
 *          Tomassetti (http://tomassetti.me)
 *
 *          THANK YOU!
 *
 * @source https://semaphoreci.com/community/tutorials/a-tdd-approach-to-parsing-ofx-banking-data-in-java
 */
public class OFXParser {

	private static Calendar startNoParseDate;
	private static Calendar endNoParseDate;

	private static TransactionList output;
	private static Calendar startDate;
	private static Calendar endDate;
	private static boolean isCreditCard;
	private static String currency;
	private static String acctType;
	private static String acctNumber;
	private static String bankName;
	private static String bankID;
	//	OFXParser is a singleton class
	private static OFXParser singleton = null;


	/**
	 * Private constructor.
	 */
	private OFXParser() {
		clearParser();
	}

	/**
	 * Instance creator.
	 *
	 * @return an instance of OFXParser
	 */
	public static OFXParser instance() {
		if (singleton == null) {
			singleton = new OFXParser();
		}
		return singleton;
	}

	/**
	 * Clears all fields of the parser.
	 */
	public static void clearParser() {
		output = new TransactionList();
		startNoParseDate = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		endNoParseDate = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		startDate = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		endDate = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MAX);
		isCreditCard = false;
		currency = "";
		acctType = "";
		acctNumber = "";
		bankName = "";
		bankID = "";
	}

//	------------ getters and setters ----------------------	

	public static Calendar getStartDate() {
		return startDate;
	}

	private static void setStartDate(Calendar startDate) {
		OFXParser.startDate = startDate;
	}

	public static Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Sets the endDate to be no longer away from the startDate than 3 months
	 */
	/*
	private static void setEndDate(Calendar endDate) {
		// the date is copied without coping the whole object's reference
		Calendar startPlus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar(startDate));
		startPlus3Months.add(Calendar.MONTH, 3);
		System.out.println("Start+3="+Transaction.returnYYYYMMDDFromCalendar(startPlus3Months));
		System.out.println("End="+Transaction.returnYYYYMMDDFromCalendar(endDate));
		if (endDate.compareTo(startPlus3Months)>0) OFXParser.endDate = startPlus3Months;
		else OFXParser.endDate = endDate;
		System.out.println("Parsing from "+Transaction.returnYYYYMMDDFromCalendar(OFXParser.startDate)+
				" to "+Transaction.returnYYYYMMDDFromCalendar(OFXParser.endDate)+".\n");
	}
	*/

	private static void setEndDate(Calendar endDate) {
		OFXParser.endDate = endDate;
	}

	public static boolean isCreditCard() {
		return isCreditCard;
	}

	private static void setCreditCard(boolean isCreditCard) {
		OFXParser.isCreditCard = isCreditCard;
	}

	public static String getCurrency() {
		return currency;
	}

	private static void setCurrency(String currency) {
		OFXParser.currency = currency;
	}

	public static String getAcctType() {
		return acctType;
	}

	private static void setAcctType(String acctType) {
		OFXParser.acctType = acctType;
	}

	public static String getAcctNumber() {
		return acctNumber;
	}

	private static void setAcctNumber(String acctNumber) {
		OFXParser.acctNumber = acctNumber;
	}

	public static String getBankName() { return bankName; }

	private static void setBankName(String bankName) { OFXParser.bankName = bankName; }

	public static String getBankID() { return bankID; }

	private static void setBankID(String bankID) { OFXParser.bankID = bankID; }

	//	--------------------------------------------------------

//  

	/**
	 * Parses OFX file and returns a list of Transactions. It doesn't check the file
	 * for existence, readability, etc. It leaves this up to the calling method. It
	 * only worries about the file content.
	 *
	 * @param source - Open Financial Exchange (OFX) file
	 * @return - list of Transactions
	 * @throws IOException - in case of problems with the file
	 */
	public static TransactionList ofxParser(File source) throws IOException {
		clearParser();
		return ofxParser(new FileInputStream(source));
	}

	/**
	 * Overloaded method.
	 * Parses OFX file and returns a list of Transactions. It doesn't check the file
	 * for existence, readability, etc. It leaves this up to the calling method. It
	 * only worries about the file content. It adds Transactions, whose date is either
	 * <= startRestrictDate, or >= endRestrictDate. Nothing in between.
	 *
	 * @param source - Open Financial Exchange (OFX) file
	 * @param startRestrictDate - minimum date restricts Transactions from top
	 * @param endRestrictDate - maximum date restricts Transactions from bottom
	 * @return - list of Transactions
	 * @throws IOException - in case of problems with the file
	 */
	public static TransactionList ofxParser(File source, Calendar startRestrictDate,
											Calendar endRestrictDate) throws IOException {
		clearParser();
		startNoParseDate = startRestrictDate;
		endNoParseDate = endRestrictDate;
		return ofxParser(new FileInputStream(source));
	}

	/**
	 * Parses OFX input stream, similar to its overloaded counterpart, and returns a
	 * list of Transactions.
	 *
	 * @param is - InputStream to read from
	 * @return - list of Transactions
	 * @throws IOException - in case of problems with the file
	 */
	public static TransactionList ofxParser(InputStream is) throws IOException {
		String content = readAll(is);
		if (!content.startsWith("OFX")) {
			return null;
		}
		Collector collector = instance().new Collector();
		processInput(content, 0, collector);
		return output;
	}

	/**
	 * Reads the whole source into a String. String, technically, can be up to
	 * 2,147,483,647 bytes (2 GB) long.
	 *
	 * @param is - InputStream to read from
	 * @return - a String representing a whole text-based file
	 * @throws IOException - in case of problems with the file
	 */
	private static String readAll(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Processes the whole String, using a recursion. Sorts out tags and other text.
	 *
	 * @param content   - the source String
	 * @param position  - a pointer to a position in the String
	 * @param collector - this object keeps all sorted parts of the original String
	 */
	private static void processInput(String content, int position, Collector collector) {
		// we reached the end, time to stop
		if (content.length() == position) {
			collector.end();
			// we have a closed tag in front of us
		} else if (content.startsWith("</", position)) {
			int close = content.indexOf(">", position);
			collector.closeTag(content.substring(position + 2, close));
			processInput(content, close + 1, collector);
			// we have an open tag in front of us
		} else if (content.startsWith("<", position)) {
			int close = content.indexOf(">", position);
			collector.openTag(content.substring(position + 1, close));
			processInput(content, close + 1, collector);
			// we have text, eat it until we get a tag or the end of the file
		} else {
			int next = content.indexOf("<", position);
			// no more tag, read until the end of the file
			if (next == -1) {
				next = content.length();
			}
			// get the text and pass it to the collector
			String text = content.substring(position, next).trim();
			if (!text.isEmpty()) {
				collector.text(text);
			}
			// keep parsing after the text
			processInput(content, next, collector);
		}
	}

//  --------------- inner class of OFXParser, the Collector class --------------

	/**
	 * This inner class collects and stores all parts of the OFX file (like tags,
	 * and plain text containing banking info), and contains tools to get the job
	 * done.
	 */
	private class Collector {
		private List<String> openTags = new LinkedList<>();
		private Calendar date;
		private String ref;
		private String name;
		private String mem;
		private double amt;

		/**
		 * No functionality at the moment.
		 */
		public void end() {
		}

		/**
		 * Clears fields of the Collector class.
		 */
		private void clearFields() {
			// The date is set to Jan 1, 1900, 00:00:00. Sufficiently generic.
			date = Transaction.returnCalendarFromOFX("19000101000000");
			ref = "";
			name = "";
			mem = "";
			amt = 0.0;
		}

		/**
		 * Collects opening tags of the file. (Opening tag: <XXX>, closing tag: </XXX>)
		 *
		 * @param tag - String with the label of the tag
		 */
		public void openTag(String tag) {
			openTags.add(tag);
			if (tag.equals("STMTTRN")) {
				clearFields();
			}
			if (tag.startsWith("CREDITCARD")) {
				setCreditCard(true);
				setAcctType("CREDIT CARD");
			}
		}

		/**
		 * Closes open tags, when collects a matching closing tag, and all tags
		 * in-between. (Opening tag: <XXX>, closing tag: </XXX>)
		 *
		 * @param tag - String with the label of the tag
		 */
		public void closeTag(String tag) {
			// if the last close tag was of a Transaction, let's create that
			// Transaction object and check, if it's within the requested time window.
			// If so, add it!
			if (tag.equals("STMTTRN")) {
				Transaction t = new Transaction(date, ref, name, mem, amt, PEC.OTHER_CATEGORY); // Default
				// following if statement checks if the Transaction date is outside of
				// restricted time period; being a "border date" is allowed
				if (((!t.isBetweenDates(startNoParseDate, endNoParseDate)) ||
						date.compareTo(startNoParseDate)==0 ||
						date.compareTo(endNoParseDate)==0)) {
					output.add(t);
				}
			}
			// when we close a tag we could implicitly close a bunch of
			// tags in between. For example consider: <a><b><c><d></a>
			// when I encounter "</a>", it tells me that all the tags
			// inside "a" should be considered to be closed
			while (!lastOpenTag().equals(tag)) {
				closeTag(lastOpenTag());
			}
			// remove the last one
			openTags.remove(openTags.size() - 1);
		}

		/**
		 * Collects and uses all banking info from the Transactions inside all the tags.
		 *
		 * @param text - text being converted to a meaningful piece of information
		 */
		public void text(String text) {
			if (lastOpenTag().equals("DTPOSTED")) {
				date = Transaction.returnCalendarFromOFX(text);
				return;
			}
			if (lastOpenTag().equals("FITID")) {
				ref = text;
				return;
			}
			if (lastOpenTag().equals("NAME")) {
				name = text;
				return;
			}
			if (lastOpenTag().equals("MEMO")) {
				mem = text;
				return;
			}
			if (lastOpenTag().equals("TRNAMT")) {
				try {
					amt = Double.parseDouble(text);
				} catch (NumberFormatException ex) {
					name = " --- ERROR READING THE AMOUNT ---  ";
					mem = "CONSULT YOUR BANK";
					amt = 0.0;
				}
				return;
			}
			if (lastOpenTag().equals("TRNTYPE")) {
				// no functionality yet
				return;
			}
			if (lastOpenTag().equals("CURDEF")) {
				setCurrency(text);
				return;
			}
			if (lastOpenTag().equals("ACCTID")) {
				setAcctNumber(text);
				return;
			}
			if (lastOpenTag().equals("ACCTTYPE")) {
				setAcctType(text);
				return;
			}
			if (lastOpenTag().equals("ORG")) {
				setBankName(text);
				return;
			}
			if (lastOpenTag().equals("BANKID")) {
				setBankID(text);
				return;
			}
			if (lastOpenTag().equals("DTSTART")) {
				setStartDate(Transaction.returnCalendarFromOFX(text));
				return;
			}
			if (lastOpenTag().equals("DTEND")) {
				setEndDate(Transaction.returnCalendarFromOFX(text));
				return;
			}
		}

		/**
		 * A helper method that returns the last open tag label.
		 *
		 * @return String representing the tag label
		 */
		private String lastOpenTag() {
			if (openTags.size() == 0) {
				return "";
			} else {
				return openTags.get(openTags.size() - 1);
			}
		}
	}

	/*
	public static void main(String[] args) throws IOException {
		TransactionList t;
		File file = new File("/Users/starnet/CreditCardSAMPLE.qfx");
		instance();
		t = OFXParser.ofxParser(file);
		ListIterator<Transaction> i = t.listIterator(); // t.sort(Transaction.DESCRIPTION);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("Beginning date: " + Transaction.returnOFXFromCalendar(OFXParser.getStartDate()));
		System.out.println("Ending date: " + Transaction.returnOFXFromCalendar(OFXParser.getEndDate()));
		System.out.println("Is this a CC? " + OFXParser.isCreditCard());
		System.out.println("Currency: " + OFXParser.getCurrency());
		System.out.println("Account number: " + OFXParser.getAcctNumber());
		System.out.println("Account type: " + OFXParser.getAcctType());
		System.out.println("Bank name: " + OFXParser.getBankName());
		System.out.println("Bank ID: " + OFXParser.getBankID());
	}
	*/
}
