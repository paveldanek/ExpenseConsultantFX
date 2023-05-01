package main_logic;

import crypto.AESUtil;
import db_connectors.Connectivity;
import entities.Transaction;
import entities.TransactionList;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import parsers.OFXParser;
import summary.Summary;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

import static main_logic.Result.Code.*;
import static parsers.OFXParser.ofxParser;

// Please run this app from main.gui_v1.starter.PEC_App_Main.java!!!!!!!!!!!!!!!!!!!!!

/**
 * Main logic class of the Personal Expense Consultant. This is the core of the program.
 * @author SPAM team: Pavel Danek and Samuel Dinka, Capstone Project of Computer Science
 * cless of 2023, Metropolitan State University, Saint Paul, Minnesota.
 */
public class PEC {

	public static String NEW_BANK = "<NEW BANK>";
	public static String NEW_ACCOUNT = "<NEW ACCOUNT>";
	public static String NEW_CATEGORY = "<NEW CATEGORY>";
	public static String OTHER_CATEGORY = "<OTHER>";

	// presetCategories must contain OTHER_CATEGORY as last element
	private static String[] presetCategories = new String[]
			{"Groceries", "Clothing", "Rent/Mortgage", "Phone", "Car/Gas",
					"Home", "Restaurants", "Entertainment", "Traveling", "Health/Fitness",
					"Beauty", "Fee", "Wage/Salary", "Interest", "Money Transfer In",
					"Money Transfer Out", OTHER_CATEGORY};

	// private main structure housing active Transaction data
	// (no more than 3 months worth)
	private TransactionList tList;
	private Summary currentSummary;

	private Calendar[] acctBeginDate = new Calendar[0];
	private Calendar[] acctEndDate = new Calendar[0];
	private String[] allBanks = {};
	private String[] allAccounts = {};
	private String[] allCategories = {};
	private String activeAccount = "";
	// holds temporary list of bank names, created by Add New Bank Dialog in
	// Manual Entry feature; will get discarted when logged out
	private ArrayList<String> tempBankNames = new ArrayList<String>();
	private String activeCategory = OTHER_CATEGORY;
	private int currentUserID = 0;
	private String currentUserPass = "";

	// optional database user name and password stored here for accessing individual
	// MySQL database. These variables get populated from command line arguments listed
	// at the time of execution of the app.
	private String dbUserName = "", dbPassword = "";

	private static PEC singleton = null;

	/**
	 * Private constructor.
	 */
	private PEC() {
		tList = new TransactionList();
	}

	/**
	 * Instance creator.
	 *
	 * @return an instance of PEC
	 */
	public static PEC instance() {
		if (singleton == null) {
			singleton = new PEC();
		}
		return singleton;
	}

	/**
	 * Processes args in case the user ran the program from the Command Line and chose
	 * to specify local MySQL server user name (argument 0) and password (argument 1).
	 * @param args a String array, possibly containing the db Server credentials
	 */
	public void processArgs(String[] args) {
		if (args.length==2) {
			Connectivity.storeDBCredentials(args[0], args[1]);
		}
	}

	/**
	 * Returns the ID of the user currently logged in.
	 */
	public int getCurrentUserID() { return currentUserID; }

	/**
	 * Sets the ID of the user currently logged in.
	 * @param currentUserID user ID
	 */
	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

	/**
	 * Returns a password of the current user, used for some encryption. Stored encrypted,
	 * returned as plain text.
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 */
	public String getCurrentUserPass() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
			InvalidKeyException { return AESUtil.decryptItem(currentUserPass); }

	/**
	 * Stores a password of the current user. It's stored in its encrypted form.
	 * @param cipheredPass encrypted password
	 */
	public void setCurrentUserPass(String cipheredPass) { currentUserPass = cipheredPass; }

	/**
	 * Returns the beginning date of an account's entire Transaction history,
	 * stored in database.
	 * @param position the order number of the account in the list
	 * @return a date as a Calendar type
	 */
	public Calendar getAcctBeginDate(int position) {
		if (position>=acctBeginDate.length || position<0)
			return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		return acctBeginDate[position];
	}

	/**
	 * Returns the beginning date of an account's entire Transaction history,
	 * stored in database.
	 * @param account the String account identifier
	 * @return a date as a Calendar type
	 */
	public Calendar getAcctBeginDate(String account) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) return acctBeginDate[count];
		}
		return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
	}

	/**
	 * Stores the beginning date of an account's entire Transaction history,
	 * stored in database.
	 * @param position the order number of the account in the list
	 * @param acctBegin the date as a Calendar type
	 */
	public void setAcctBeginDate(int position, Calendar acctBegin) {
		if (position>=0 && position<acctBeginDate.length)
			acctBeginDate[position] = acctBegin;
	}

	/**
	 * Stores the beginning date of an account's entire Transaction history,
	 * stored in database.
	 * @param account the String account identifier
	 * @param acctBegin the date as a Calendar type
	 */
	public void setAcctBeginDate(String account, Calendar acctBegin) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) acctBeginDate[count] = acctBegin;
		}
	}

	/**
	 * Returns the end date of an account's entire Transaction history,
	 * stored in database.
	 * @param position the order number of the account in the list
	 * @return a date as a Calendar type
	 */
	public Calendar getAcctEndDate(int position) {
		if (position>=acctEndDate.length || position<0)
			return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		return acctEndDate[position];
	}

	/**
	 * Returns the end date of an account's entire Transaction history,
	 * stored in database.
	 * @param account the String account identifier
	 * @return a date as a Calendar type
	 */
	public Calendar getAcctEndDate(String account) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) return acctEndDate[count];
		}
		return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
	}

	/**
	 * Stores the end date of an account's entire Transaction history,
	 * stored in database.
	 * @param position the order number of the account in the list
	 * @param acctEnd the date as a Calendar type
	 */
	public void setAcctEndDate(int position, Calendar acctEnd) {
		if (position>=0 && position<acctEndDate.length)
			acctEndDate[position] = acctEnd;
	}

	/**
	 * Stores the end date of an account's entire Transaction history,
	 * stored in database.
	 * @param account the String account identifier
	 * @param acctEnd the date as a Calendar type
	 */
	public void setAcctEndDate(String account, Calendar acctEnd) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) acctEndDate[count] = acctEnd;
		}
	}

	/**
	 * Returns the account used to display Transactions.
	 */
	public String getActiveAccount() {
		return activeAccount;
	}

	/**
	 * Sets the account used to display Transactions.
	 * @param activeAccount the String account identifier
	 */
	public void setActiveAccount(String activeAccount) {
		this.activeAccount = activeAccount;
	}

	/**
	 * Returns the Category currently used for identifying Transactions.
	 */
	public String getActiveCategory() { return activeCategory; }

	/**
	 * Sets the Category used for identifying Transactions.
	 * @param activeCategory
	 */
	public void setActiveCategory(String activeCategory) { this.activeCategory = activeCategory; }

	/**
	 * Finds out, whether a String is one of the items of a String Linked List.
	 * @param text the String looked for
	 * @param list the List to look through
	 * @return
	 */
	public static boolean isTextInList(String text, LinkedList<String> list) {
		for (String item : list) {
			if (item.compareToIgnoreCase(text)==0) return true;
		}
		return false;
	}

	/**
	 * Finds out, whether a String is one of the items of a String Array.
	 * @param text the String looked for
	 * @param list the Array to look through
	 * @return
	 */
	public static boolean isTextInList(String text, String[] list) {
		for (String item : list) {
			if (item.compareToIgnoreCase(text)==0) return true;
		}
		return false;
	}

	/**
	 * Returns the position of the String text in a String Linked List.
	 * @param text the String looke for
	 * @param list the List to look through
	 * @return an integer representning the position (0-n),
	 *         -1 if not found
	 */
	public static int whereIsTextInList(String text, LinkedList<String> list) {
		for (int i=0; i<list.size(); i++) {
			if (list.get(i).compareToIgnoreCase(text)==0) return i;
		}
		return -1;
	}

	/**
	 * Returns the position of the String text in a String Array.
	 * @param text the String looke for
	 * @param list the Array to look through
	 * @return an integer representning the position (0-n),
	 *         -1 if not found
	 */
	public static int whereIsTextInList(String text, String[] list) {
		for (int i=0; i<list.length; i++) {
			if (list[i].compareToIgnoreCase(text)==0) return i;
		}
		return -1;
	}

	/**
	 * Creates an account identifier out of supplied ingredients, in the form:
	 * "<Account Nickname> …<last 4 digits of Acct.Num.> (<Bank Name>)". It skips
	 * an ingredient that's supplied as an empty String (a.k.a. missing).
	 * @param acctNick Account NickName
	 * @param acctNum Account Number
	 * @param bankName Bank Name
	 * @return an account identifier in a String form
	 */
	public static String createAcctIdentifier(String acctNick, String acctNum, String bankName) {
		String output = "";
		if (acctNick.length()>0) output += acctNick;
		if (acctNick.length()>0 && (acctNum.length()>4 || bankName.length()>0)) output += " ";
		if (acctNum.length()>4) output += "…" + acctNum.substring(acctNum.length()-4, acctNum.length());
		if (acctNum.length()>4 && bankName.length()>0) output += " ";
		if (bankName.length()>0) output += "(" + bankName +")";
		return output;
	}

	/**
	 * Adds a newly created bank name to a temporary list for later comparison
	 * with potential bank name candidates to be stored in a Transaction history
	 * in the database.
	 * @param bankName bank name to be temporarily stored
	 */
	public void addNewBankToTempList(String bankName) {
		tempBankNames.add(bankName);
	}

	/**
	 * Extracts a name of the account's bank from its identifier. To be sure the
	 * correct String name was recognized, the candidate is compared against bank names
	 * in the allBanks bank list, prepared from all bank names in the database, and
	 * in the temporary list of newly created bank names which will later get discarted.
	 * @return the bank name found,
	 *         empty String in not matched to any name (a.k.a. not found)
	 */
	private String getBankNameFromCurrAcctIdentifier() {
		String[] strArray = getActiveAccount().split("[()]");
		for (String str : strArray) {
			for (String bank : allBanks) {
				if (str.compareToIgnoreCase(bank)==0) return bank;
			}
			for (String bank : tempBankNames) {
				if (str.compareToIgnoreCase(bank)==0) return bank;
			}
		}
		return "";
	}

	/**
	 * Adds a bank name to a permanent list of all bank names in the database.
	 * @param bankName the bank name being stored
	 */
	public void addBankToList(String bankName) {
		String[] tempBanks = new String[allBanks.length+1];
		System.arraycopy(allBanks, 0, tempBanks, 0, allBanks.length);
		tempBanks[tempBanks.length-1] = bankName;
		allBanks = tempBanks;
	}

	/**
	 * Gets the beginning date of the current Transaction List showing in the
	 * main window table.
	 * @return a date as a Calendar type
	 */
	public Calendar getCurrentViewBeginDate() {
		return tList.getStartDate();
	}

	/**
	 * Gets the end date of the current Transaction List showing in the main
	 * window table.
	 * @return a date as a Calendar type
	 */
	public Calendar getCurrentViewEndDate() {
		return tList.getEndDate();
	}

	/**
	 * Returns the account position in the allAccounts array,
	 * searched either by accountNick (if accountNick!=""), or
	 * by accountNumber's last four digits (if accountNumber>three digits)
	 * @param accountNick identifying account nick (or "" for search by number)
	 * @param accountNumber identifying account number (or "" for search by nick)
	 * @return position of the account in the array if found, -1 if not found
	 */
	private int accountPosition(String accountNick, String accountNumber) {
		// if the array is empty, return -1 right away
		if (allAccounts.length < 1) return -1;
		if (accountNick.length()>0) {
			int count = 0;
			for (count = 0; count < allAccounts.length; count++) {
				if (allAccounts[count].startsWith(accountNick)) return count;
			}
		} else if (accountNumber.length()>=4) {
			int count = 0;
			accountNumber = accountNumber.substring(accountNumber.length()-4, accountNumber.length());
			for (count = 0; count < allAccounts.length; count++) {
				String[] tempStr = allAccounts[count].split("…");
				if (tempStr.length>1 && tempStr[1].startsWith(accountNumber)) return count;
			}
		}
		return -1;
	}

	/**
	 * Gets the name + abs. path from the Request object, checks the file for
	 * readability, hands the job over to OFX parser and collects and expedites
	 * the Result as a ListIterator. The newly populated list is no more than
	 * request.from - request.to long.
	 * @param request - Request object;
	 *                  request.fileWithPath, request.from, request.to filled out
	 * @return - list of Result objects with Transaction fields filled out
	 */
	public ListIterator<Result> parseOFX(Request request) {
		File file = null;
		Result result = new Result();
		TransactionList parsedTlist = new TransactionList();
		ArrayList<Result> rList = new ArrayList<Result>();
		try {
			file = new File(request.getFileWithPath());
			parsedTlist = OFXParser.ofxParser(file, request.getFrom(), request.getTo());
			if (parsedTlist ==null) {
				result.setCode(WRONG_FILE);
			} else if (parsedTlist.size()==0){
				result.setCode(NO_ITEMS_TO_READ);
			} else {
				result.setCode(SUCCESS);
			}
		} catch (Exception e) {
			result.setCode(IO_ERROR);
		}
		if (result.getCode()!=SUCCESS) {
			rList.add(result);
			return rList.listIterator();
		}
		//SUCCESS: parsedTList is merged into the beginning or ending of the database
		String acctNum = OFXParser.getAcctNumber();
		int acctPos = accountPosition("", acctNum);
		if (acctPos==-1) {
			// if the newly parsed data belongs to account that's not yet on file, do:
			if (tList.size()>0) {
				// if there's anything in the current tList
				uploadCurrentList();
				tList.clearTransactionList();
			}
			// set up a new account, clear the table; the following is temporary
			String[] tempAccounts = new String[(allAccounts.length+1)];
			System.arraycopy(allAccounts, 0, tempAccounts, 0, allAccounts.length);
			allAccounts = tempAccounts;
			String newParsedNick = "My "+OFXParser.getAcctType();
			allAccounts[allAccounts.length-1] = createAcctIdentifier(newParsedNick,
					OFXParser.getAcctNumber(), OFXParser.getBankName());
			addBankToList(OFXParser.getBankName());
			activeAccount = allAccounts[allAccounts.length-1];
			GUI_ElementsOptionLists.getInstance().addBankToList(OFXParser.getBankName());
			Calendar[] tempBegin = new Calendar[acctBeginDate.length+1];
			Calendar[] tempEnd = new Calendar[acctEndDate.length+1];
			System.arraycopy(acctBeginDate, 0, tempBegin, 0, acctBeginDate.length);
			System.arraycopy(acctEndDate, 0, tempEnd, 0, acctEndDate.length);
			tempBegin[tempBegin.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			tempEnd[tempEnd.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			acctBeginDate = tempBegin;
			acctEndDate = tempEnd;
		} else {
			// if the parsed account is not the active account, fetch it and make it active
			if (activeAccount.compareToIgnoreCase(allAccounts[acctPos])!=0)
				switchActiveAccount(allAccounts[acctPos]);
		}
		if (mergeNewTList(parsedTlist)) {
			// if the new list has been merged successully (at least 1 item added to main Transaction List),
			// upload it to database
			uploadCurrentList();
			return returnRListIterator();
		}
		else {
			// if no item has been added to the main Transaction List
			result.setCode(NO_ITEMS_TO_READ);
			rList.add(result);
			return rList.listIterator();
		}
	}

	/**
	 * Merges a new list (from parsing or manual entry) into the main tList.
	 * @param list - list to be merged
	 * @return - TRUE if succeeded, FALSE if nothing got merged
	 */
	private boolean mergeNewTList(TransactionList list) {
		boolean change = false;
		if (list==null) return change;
		TransactionList resultTList = new TransactionList();
		if (list.getStartDate().compareTo(getAcctBeginDate(activeAccount))<=0) {
			// fetch the first 3-or-less-month chunk of the db and load it in tList, if not there already
			if (tList.getStartDate().compareTo(getAcctBeginDate(activeAccount))>0) {
				tList = download3monthPortion(firstAndLastEntryOfAccount(activeAccount)[0]);
			}
			for (int j=0;j<tList.size();j++) resultTList.add(tList.get(j));
			int i = list.size()-1;
			// do this until the end of the parsedTList or beginning of the db
			while (i>=0 &&
					list.get(i).getPostedDate().compareTo(getAcctBeginDate(activeAccount))<=0) {
				if (resultTList.add(list.get(i))) change = true;
				i--;
			}
			setAcctBeginDate(activeAccount, resultTList.getStartDate());
			tList = resultTList;
			currentSummary = new Summary(tList);
			return change;
		} else if (list.getEndDate().compareTo(getAcctEndDate(activeAccount))>=0) {
			// fetch the last 3-or-less-month chunk of db and load it in tList, if not there already
			if (tList.getEndDate().compareTo(getAcctEndDate(activeAccount))<0) {
				tList = download3monthPortion(firstAndLastEntryOfAccount(activeAccount)[1]);
			}
			for (int i=0;i<tList.size();i++) resultTList.add(tList.get(i));
			int i = 0;
			while (i< list.size() &&
					list.get(i).getPostedDate().compareTo(getAcctEndDate(activeAccount))<0) i++;
			for (int j = i; j< list.size(); j++) {
				if (resultTList.add(list.get(j))) change = true;
			}
			setAcctEndDate(activeAccount, resultTList.getEndDate());
			// If database is empty, do:
			if (tList.size()==0) setAcctBeginDate(activeAccount, list.getStartDate());
			tList = resultTList;
			currentSummary = new Summary(tList);
			return change;
		}
		return change;
	}

	/**
	 * Returns an ListIterator of Results with fields filled out with all Transactions
	 * from Transaction list.
	 */
	public ListIterator<Result> returnRListIterator() {
		ListIterator<Transaction> it = tList.listIterator();
		Result result = new Result();
		ArrayList<Result> rList = new ArrayList<Result>();
		result.setCode(SUCCESS);
		while (it.hasNext()) {
			result.setTFields(it.next());
			rList.add(result);
			result = new Result();
		}
		return rList.listIterator();
	}

	/**
	 * Just like parseOFX, this methods creates a list of new candidates to be merged to the
	 * existing (or new if one does not exist) Transaction List, attempts to merge it and returns
	 * the result
	 * @return TRUE if at least 1 item got merged,
	 *         FALSE if nothing got merged
	 */
	public boolean processManualEntries() {
		String[][] manEntryArray = ManualEntry.instance().getArray();
		if (manEntryArray.length==0) return false;
		if (isTextInList(manEntryArray[0][0], allAccounts)) {
			switchActiveAccount(manEntryArray[0][0]);
		} else {
			if (tList.size()>0) {
				uploadCurrentList();
				tList.clearTransactionList();
			}
			String[] tempAccounts = new String[(allAccounts.length+1)];
			System.arraycopy(allAccounts, 0, tempAccounts, 0, allAccounts.length);
			allAccounts = tempAccounts;
			allAccounts[allAccounts.length-1] = manEntryArray[0][0];
			activeAccount = allAccounts[allAccounts.length-1];
			if (getBankNameFromCurrAcctIdentifier().length()>0) {
				GUI_ElementsOptionLists.getInstance().addBankToList(getBankNameFromCurrAcctIdentifier());
			}
			Calendar[] tempBegin = new Calendar[acctBeginDate.length+1];
			Calendar[] tempEnd = new Calendar[acctEndDate.length+1];
			System.arraycopy(acctBeginDate, 0, tempBegin, 0, acctBeginDate.length);
			System.arraycopy(acctEndDate, 0, tempEnd, 0, acctEndDate.length);
			tempBegin[tempBegin.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			tempEnd[tempEnd.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			acctBeginDate = tempBegin;
			acctEndDate = tempEnd;
		}
		// sort transactions by date, merge them in.
		ArrayList<Transaction> list = new ArrayList<Transaction>();
		ArrayList<Transaction> sortedList = new ArrayList<Transaction>();
		for (int j=0; j<manEntryArray.length; j++) {
			Transaction newT = new Transaction(Transaction.returnCalendarFromYYYYMMDD(manEntryArray[j][1]),
					manEntryArray[j][2], manEntryArray[j][3], manEntryArray[j][4],
					Double.parseDouble(manEntryArray[j][5]), manEntryArray[j][6]);
			list.add(newT);
		}
		sortedList = TransactionList.mergeSortByDate(list);
		TransactionList temp = new TransactionList();
		for (Transaction t : sortedList) { temp.add(t); }
		if (mergeNewTList(temp)) {
			uploadCurrentList();
			return true;
		}
		else return false;
	}

	/**
	 * Fetches items for three distinct drop-down menus in the app: Bank List Menu,
	 * Account List Menu, and Category List Menu. Plus it fetches first and last date
	 * of each account in database stored under the current user ID.
	 * @return Rusult type of variable, containing all three lists; in addition all the
	 * lists, plus the account first/last dates are stored in their appropriate variables
	 * in this (PEC) class.
	 * @throws SQLException
	 */
	public Result downloadDropDownMenuEntries() throws SQLException {
		Result result = new Result();
		List<String> banks = new ArrayList<String>();
		List<String> accounts = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		// downloading bank list
		Connection connection = Connectivity.getConnection();
		String query = "SELECT DISTINCT bank_name FROM transaction WHERE user_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, getCurrentUserID());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			banks.add(rs.getString("bank_name"));
		}
		allBanks = new String[banks.size()];
		for (int i = 0; i < banks.size(); i++) {
			allBanks[i] = banks.get(i);
		}
		result.setBankList(allBanks);
		// downloading account name/ nick list
		query = "SELECT DISTINCT account_nick FROM transaction WHERE user_id = ?";
		stmt = connection.prepareStatement(query);
		stmt.setInt(1, getCurrentUserID());
		rs = stmt.executeQuery();
		while (rs.next()) {
			accounts.add(rs.getString("account_nick"));
		}
		allAccounts = new String[accounts.size()];
		for (int i = 0; i < accounts.size(); i++) {
			allAccounts[i] = accounts.get(i);
		}
		result.setAcctList(allAccounts);
		// downloading category list
		connection = Connectivity.getConnection();
		query = "SELECT category_name FROM category WHERE user_id = ?";
		stmt = connection.prepareStatement(query);
		stmt.setInt(1, getCurrentUserID());
		rs = stmt.executeQuery();
		while (rs.next()) {
			categories.add(rs.getString("category_name"));
		}
		// if the category list was empty, use a preset
		if (categories.size()==0) {
			allCategories = new String[presetCategories.length];
			for (int i = 0; i < presetCategories.length; i++) {
				allCategories[i] = presetCategories[i];
			}
		} else {
			// otherwise use what's been downloaded
			allCategories = new String[categories.size()];
			for (int i = 0; i < categories.size(); i++) {
				allCategories[i] = categories.get(i);
			}
		}
		result.setCategoryList(allCategories);
		// downloading all first/last entry dates for all user's accounts
		acctBeginDate = new Calendar[allAccounts.length];
		acctEndDate = new Calendar[allAccounts.length];
		for (int i = 0; i < allAccounts.length; i++) {
			Calendar[] calRange = new Calendar[2];
			calRange = firstAndLastEntryOfAccount(allAccounts[i]);
			acctBeginDate[i] = calRange[0];
			connection = Connectivity.getConnection();
			query = "SELECT end_date FROM summary WHERE user_id = ? AND account_nick = ?"+
					" AND begin_date = ?";
			stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			stmt.setString(2, allAccounts[i]);
			stmt.setString(3, Transaction.returnYYYYMMDDFromCalendar(acctBeginDate[i]));
			rs = stmt.executeQuery();
			if (rs.next()) acctEndDate[i] = Transaction.returnCalendarFromYYYYMMDD
					(rs.getString("end_date"));
			else acctEndDate[i] = calRange[1];
		}
		return result;
	}

	/**
	 * Adds a newly created Category in the List of all Categories, which is stored
	 * in database upon the program's quitting.
	 * @param category new Category to be added
	 * @return the new, updated Category list
	 */
	public String[] addCategoryLocally(String category) {
		String last = allCategories[allCategories.length-1];
		String[] newArray = new String[allCategories.length+1];
		System.arraycopy(allCategories, 0, newArray, 0, allCategories.length-1);
		newArray[newArray.length-2] = category;
		newArray[newArray.length-1] = last;
		allCategories = newArray;
		setActiveCategory(category);
		return allCategories;
	}

	/**
	 * Finds a Category in Transaction List by its reference number/ name and
	 * sets its Category field to the active (current) Category selected. The
	 * name used as a fallback, in case the reference number is empty.
	 * @param refNum the Transaction reference number
	 * @param name the Transaction name (a.k.a. description)
	 */
	public void changeCategoryToActive(String refNum, String name) {
		if (tList==null || tList.size()==0) return;
		Transaction t = tList.searchByRefNumberAndName(refNum, name);
		if (t!=null) t.setCategory(activeCategory);
	}

	/**
	 * Updates a Category table in the database with the up-to-date list stored
	 * locally.
	 * @throws SQLException
	 */
	public void addCategoriesForUserToDB() throws SQLException {
		Connection connection = Connectivity.getConnection();
		String sql = "DELETE FROM category WHERE user_id = ?";
		PreparedStatement s = connection.prepareStatement(sql);
		s.setInt(1, getCurrentUserID());
		int rowsAffected = s.executeUpdate();
		String query = "INSERT INTO category (category_name, user_id) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			for (String categoryName : allCategories) {
				stmt.setString(1, categoryName);
				stmt.setInt(2, getCurrentUserID());
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Switches the Active Account to the requsted one. Before doing so, it updates
	 * the old account and its Summary in the database. Then it fetches the first
	 * 3-month-portion of the new Active Account from the db and replaces it in the
	 * Transaction List.
	 * @param acctToSwitchTo account to switch to
	 * @return an iterator over the newly fetched list
	 */
	public ListIterator<Result> switchActiveAccount(String acctToSwitchTo) {
		if (accountPosition(acctToSwitchTo, "")==-1)
			return returnRListIterator();
		uploadCurrentList();
		setActiveAccount(acctToSwitchTo);
		tList = download3monthPortion(firstAndLastEntryOfAccount(activeAccount)[0]);
		currentSummary = Summary.downloadSummary(activeAccount, firstAndLastEntryOfAccount(activeAccount)[0],
				firstAndLastEntryOfAccount(activeAccount)[0]);
		return returnRListIterator();
	}

	// ---------------------------------------------------------------------------------------------------------
	// -------------- navigation around TransactionList table + Transaction upload and download ----------------

	/**
	 * It fetches the last record made and stored for the current user. That way,
	 * whichever view the current user logged out with the last time, they will
	 * log in with this time.
	 * @return an iterator over the last stored list
	 */
	public ListIterator<Result> initialDBaseDownload() {
		String lastNickFound="";
		String lastRecordMade="";
		Calendar[] firstEntry = new Calendar[2];
		if (currentUserHasAnyAccount()) {
			Connection connection = Connectivity.getConnection();
			String query = "SELECT * FROM transaction WHERE user_id = ?";
			PreparedStatement stmt = null;
			try {
				stmt = connection.prepareStatement(query);
				stmt.setInt(1, getCurrentUserID());
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					lastNickFound = rs.getString("account_nick");
					lastRecordMade = rs.getString("transaction_date");
				}
				setActiveAccount(lastNickFound);
				tList = download3monthPortion(Transaction.returnCalendarFromYYYYMMDD(lastRecordMade));
				currentSummary = Summary.downloadSummary(lastNickFound,
						Transaction.returnCalendarFromYYYYMMDD(lastRecordMade),
						Transaction.returnCalendarFromYYYYMMDD(lastRecordMade));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return returnRListIterator();
	}

	/**
	 * Checks if the current user has any accounts in the database.
	 * @return TRUE if at least 1 account is in the db,
	 *         FALSE if there's no account in the db for this user
	 */
	private boolean currentUserHasAnyAccount() {
		Connection connection = Connectivity.getConnection();
		String query = "SELECT account_nick FROM transaction WHERE user_id = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks if the current user has a specific account in the database.
	 * @param acctNick the account identifier looked for
	 * @return TRUE if the account is in the db,
	 *         FALSE if the account is NOT in the db for this user
	 */
	private boolean currentUserHasAccount(String acctNick) {
		Connection connection = Connectivity.getConnection();
		String query = "SELECT account_nick FROM transaction WHERE user_id = ? AND account_nick = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			stmt.setString(2, acctNick);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fetches both first and last dates of the db entries for a specific account.
	 * @param acctNick the account searched for
	 * @return an Array of two Calendar values
	 */
	private Calendar[] firstAndLastEntryOfAccount(String acctNick) {
		Connection connection = Connectivity.getConnection();
		Calendar[] result = new Calendar[2];
		result[0] = Transaction.returnCalendarFromOFX(TransactionList.STR_DATE_MAX);
		result[1] = Transaction.returnCalendarFromOFX(TransactionList.STR_DATE_MIN);
		String query = "SELECT * FROM transaction WHERE user_id = ? AND account_nick = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			stmt.setString(2, acctNick);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Calendar dbDate = Transaction.returnCalendarFromYYYYMMDD(rs.getString("transaction_date"));
				if (dbDate.compareTo(result[0])<0) result[0] = dbDate;
				if (dbDate.compareTo(result[1])>0) result[1] = dbDate;
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fetches both previous and next dates of the db entries for a specific account,
	 * relative to a specific date.
	 * @param acctNick the account searched for
	 * @return an Array of two Calendar values
	 */
	private Calendar[] previousAndNextEntryOfAccount(String acctNick, Calendar date) {
		Connection connection = Connectivity.getConnection();
		Calendar[] result = new Calendar[2];
		Calendar[] firstLast = firstAndLastEntryOfAccount(acctNick);
		Calendar biggestSmall = firstLast[0];
		Calendar smallestBig = firstLast[1];
		if (date==null) date=Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar(firstLast[1]));
		String query = "SELECT * FROM transaction WHERE user_id = ? AND account_nick = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			stmt.setString(2, acctNick);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Calendar dbDate = Transaction.returnCalendarFromYYYYMMDD(rs.getString("transaction_date"));
				if (dbDate.compareTo(date) < 0 && dbDate.compareTo(biggestSmall) > 0) biggestSmall =
						Transaction.returnCalendarFromYYYYMMDD(Transaction.returnYYYYMMDDFromCalendar(dbDate));
				if (dbDate.compareTo(date) > 0 && dbDate.compareTo(smallestBig) < 0) smallestBig =
						Transaction.returnCalendarFromYYYYMMDD(Transaction.returnYYYYMMDDFromCalendar(dbDate));
			}
			result[0] = biggestSmall;
			result[1] = smallestBig;
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uploads a Transaction List of max. length of 3 months into the database.
	 * @param load the Transaction List being uploaded
	 */
	private void upload3monthPortion(TransactionList load) {
		Connection connection = Connectivity.getConnection();
		String sql = "INSERT INTO transaction (transaction_date, transaction_history,"+
				" bank_name, account_nick, user_id) VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement s = connection.prepareStatement(sql);
			s.setString(1, Transaction.returnYYYYMMDDFromCalendar(load.getStartDate()));
			s.setString(2, AESUtil.encryptStringTable(AESUtil.tListIntoString(load), getCurrentUserPass()));
			s.setString(3, getBankNameFromCurrAcctIdentifier());
			s.setString(4, getActiveAccount());
			s.setInt(5, getCurrentUserID());
			int rowsAffected = s.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Summary.uploadSummary(load, getActiveAccount());
	}

	/**
	 * Dowloads a 3-month-long entry with a specific beginning date from the database.
	 * @param dateStarting Calendar value of the date in the database
	 * @return the requested Transaction List; will return an empty list if not found
	 */
	private TransactionList download3monthPortion(Calendar dateStarting) {
		Connection connection = Connectivity.getConnection();
		TransactionList list = new TransactionList();
		String query = "SELECT transaction_history FROM transaction "
				+ "WHERE user_id = ? AND transaction_date = ? AND account_nick = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(query);
			stmt.setInt(1, getCurrentUserID());
			stmt.setString(2, Transaction.returnYYYYMMDDFromCalendar(dateStarting));
			stmt.setString(3, getActiveAccount());
			ResultSet rs = stmt.executeQuery();
			String result = "";
			if (rs.next()) result = rs.getString("transaction_history");
			result = AESUtil.decryptStringTable(result, getCurrentUserPass());
			list = AESUtil.stringIntoTList(result);
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes a 3-month-long entry with a specific beginning date from the database.
	 * @param dateStarting Calendar value of the date in the database
	 */
	private void delete3monthPortion(Calendar dateStarting) {
		Connection connection = Connectivity.getConnection();
		String sql = "DELETE FROM transaction WHERE user_id = ? AND transaction_date = ? AND account_nick = ?";
		try {
			PreparedStatement s = connection.prepareStatement(sql);
			s.setInt(1, getCurrentUserID());
			s.setString(2, Transaction.returnYYYYMMDDFromCalendar(dateStarting));
			s.setString(3, getActiveAccount());
			int rowsAffected = s.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Summary.deleteSummary(dateStarting, getActiveAccount());
	}

	/**
	 * Uploads current Transaction List to database. If the tList is larger than 3 months,
	 * it will be split into 3-month-portions, with a smaller remainder if necessary, and
	 * everything gets uploaded, one by one.
	 */
	public void uploadCurrentList() {
		if (tList==null || tList.size()==0) return;
		Calendar startPlus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar
				(tList.getStartDate()));
		startPlus3Months.add(Calendar.MONTH, 3);
		Calendar[] dbAcctFirstLast = firstAndLastEntryOfAccount(activeAccount);
		boolean isCurrentAcctInDB = currentUserHasAccount(activeAccount);
		boolean isCurrentListLessThan3Months = tList.getEndDate().compareTo(startPlus3Months)<=0;
		boolean currentListStartGoesBeforeDB = tList.getStartDate().compareTo(dbAcctFirstLast[0])<=0;
		boolean currentListEndGoesAfterDB = tList.getStartDate().compareTo(dbAcctFirstLast[1])>=0;
		// if there's no record in database and the current list is smaller than 3 months,
		// upload the current list, its start date, end date
		if (!isCurrentAcctInDB && isCurrentListLessThan3Months) {
			upload3monthPortion(tList);
			setAcctBeginDate(activeAccount, tList.getStartDate());
			setAcctEndDate(activeAccount, tList.getEndDate());
		}
		// if there's no record in database and the current list is longer than 3 months,
		// SPLIT THE LIST, upload first 3 months + everything, keep going, 3-month increments
		// until the whole list is uploaded, keep the last portion active
		if (!isCurrentAcctInDB && !isCurrentListLessThan3Months) {
			setAcctBeginDate(activeAccount, tList.getStartDate());
			setAcctEndDate(activeAccount, tList.getEndDate());
			TransactionList tempList = new TransactionList();
			while (tList.size()>0) {
				startPlus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar
						(tList.getStartDate()));
				startPlus3Months.add(Calendar.MONTH, 3);
				tempList = new TransactionList();
				while (tList.size()>0 && tList.get(0).getPostedDate().compareTo(startPlus3Months)<=0) {
					tempList.add(tList.get(0));
					tList.remove(tList.get(0).getRefNumber());
				}
				upload3monthPortion(tempList);
			}
			tList = tempList;
		}
		// if there IS record in database and the current list is goes BEFORE the record, download
		// the first record below the current list, count backwards 3 months, SPLIT and upload INSTEAD
		// the first record, keep splitting and uploading BEFORE the first record until done
		if (isCurrentAcctInDB && currentListStartGoesBeforeDB) {
			TransactionList tempList = download3monthPortion(dbAcctFirstLast[0]);
			delete3monthPortion(dbAcctFirstLast[0]);
			mergeNewTList(tempList);
			while (tList.size()>0) {
				Calendar endMinus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar
						(tList.getEndDate()));
				endMinus3Months.add(Calendar.MONTH, -3);
				tempList = new TransactionList();
				while (tList.size()>0 && tList.get(tList.size()-1).getPostedDate().compareTo(endMinus3Months)>=0) {
					tempList.addToFrontFirst(tList.get(tList.size()-1));
					tList.remove(tList.get(tList.size()-1).getRefNumber());
				}
				upload3monthPortion(tempList);
			}
			tList = tempList;
			setAcctBeginDate(activeAccount, tList.getStartDate());
		} else if (isCurrentAcctInDB && currentListEndGoesAfterDB) {
			// if there IS record in database and the current list is goes AFTER the record, download
			// the last record above the current list, count forwards 3 months, SPLIT and upload INSTEAD
			// the last record, keep splitting and uploading AFTER the last record until done
			TransactionList tempList = download3monthPortion(dbAcctFirstLast[1]);
			delete3monthPortion(dbAcctFirstLast[1]);
			mergeNewTList(tempList);
			while (tList.size()>0) {
				startPlus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar
						(tList.getStartDate()));
				startPlus3Months.add(Calendar.MONTH, 3);
				tempList = new TransactionList();
				while (tList.size()>0 && tList.get(0).getPostedDate().compareTo(startPlus3Months)<=0) {
					tempList.add(tList.get(0));
					tList.remove(tList.get(0).getRefNumber());
				}
				upload3monthPortion(tempList);
			}
			tList = tempList;
			setAcctEndDate(activeAccount, tList.getEndDate());
		}
		// if there IS record in database and the current list is fits in the middle
		if (isCurrentAcctInDB && !currentListStartGoesBeforeDB && !currentListEndGoesAfterDB) {
			delete3monthPortion(tList.getStartDate());
			upload3monthPortion(tList);
		}
		try {
			addCategoriesForUserToDB();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fetches the first 3-month-portion of the active account from the database.
	 * @return an iterator over the new list
	 */
	public ListIterator<Result> goFirst() {
		uploadCurrentList();
		Calendar first = firstAndLastEntryOfAccount(activeAccount)[0];
		tList = download3monthPortion(first);
		currentSummary = Summary.downloadSummary(activeAccount, first, first);
		return returnRListIterator();
	}

	/**
	 * Fetches the previous 3-month-portion of the active account from the database.
	 * @return an iterator over the new list
	 */
	public ListIterator<Result> goPrevious() {
		uploadCurrentList();
		Calendar previous = previousAndNextEntryOfAccount(activeAccount, tList.getStartDate())[0];
		tList = download3monthPortion(previous);
		currentSummary = Summary.downloadSummary(activeAccount, previous, previous);
		return returnRListIterator();
	}

	/**
	 * Fetches the next 3-month-portion of the active account from the database.
	 * @return an iterator over the new list
	 */
	public ListIterator<Result> goNext() {
		uploadCurrentList();
		Calendar next = previousAndNextEntryOfAccount(activeAccount, tList.getStartDate())[1];
		tList = download3monthPortion(next);
		currentSummary = Summary.downloadSummary(activeAccount, next, next);
		return returnRListIterator();
	}

	/**
	 * Fetches the last 3-month-portion of the active account from the database.
	 * @return an iterator over the new list
	 */
	public ListIterator<Result> goLast() {
		uploadCurrentList();
		Calendar last = firstAndLastEntryOfAccount(activeAccount)[1];
		tList = download3monthPortion(last);
		currentSummary = Summary.downloadSummary(activeAccount, last, last);
		return returnRListIterator();
	}

	// ---------------------------------------------------------------------------------------------------------

}