package main_logic;

//import java.util.ArrayList;
//import java.util.ListIterator;

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
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

import static main_logic.Result.Code.*;
import static parsers.OFXParser.ofxParser;

public class PEC {

	public static String NEW_BANK = "<NEW BANK>";
	public static String NEW_ACCOUNT = "<NEW ACCOUNT>";
	public static String NEW_CATEGORY = "<NEW CATEGORY>";
	public static String OTHER_CATEGORY = "<OTHER>";

	// presetCategories must contain OTHER_CATEGORY as last element
	private static String[] presetCategories = new String[]
			{"Groceries", "Clothing", "Rent/Mortgage", "Phone", "Car/Gas",
					"Home", "Restaurants", "Entertainment", "Traveling", "Health/Fitness",
					"Beauty", "Fee", "Wage/Salary", "Interest", "Money Transfer", OTHER_CATEGORY};

	// private main structure housing active Transaction data
	// (no more than 3 months worth)
	private TransactionList tList;
	private Summary currentSummary;

	private String[][] manualEntries = new String[0][7];

	private Calendar[] acctBeginDate = new Calendar[0];
	private Calendar[] acctEndDate = new Calendar[0];
	private String[] allBanks = {};
	private String[] allAccounts = {};
	private String[] allCategories = {};
	private String activeAccount = "";
	private String activeCategory = OTHER_CATEGORY;
	// array of booleans to remember if a particular column is sorted
	// in a descending (or ascending) direction

	private int currentUserID = 0;
	private String currentUserPass = "";

	private static PEC singleton = null;

	/**
	 * Private constructor.
	 */
	private PEC() {
		tList = new TransactionList();
		// code for reaching out to database, and if there are records, load
		// the last "batch" (last 3 months or less--last time index
		// (transaction_date)--whatever is included in the last encrypted
		// String of Transactions) into tList and display; IF NOTHING FOUND,
		// DISPLAY EMPTY TABLE AND A WINDOW: "To start, choose IMPORT ACCOUNT
		// ACTIVITY, MANUAL ENTRY, or HOW TO START from the Menu. <OK>".

		// VERY IMPORTANT:
		// beginDate = <the first time index (transaction_date) in the database>;
		// endDate = <the date of the last Transaction in tList>;
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

//	an example of "loading" any logic bearing method with Request parameter
//	and returning a Result or a ListIterator to a list of Results.
//
//	public Result XXX(Request request) {
//		Result result = new Result();
//		return result;
//	}
//
//	public ListIterator<Result> YYY(Request request) {
//		ArrayList<Result> list = new ArrayList<Result>();
//		// ... code ...
//		for (... for loop ...) {
//			Result result = new Result();
//			result = ...;
//			list.add(result);
//		}
//		return list.listIterator();
//	}
// ...

	public int getCurrentUserID() { return currentUserID; }

	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

	public String getCurrentUserPass() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
			InvalidKeyException { return AESUtil.decryptItem(currentUserPass); }

	public void setCurrentUserPass(String cipheredPass) { currentUserPass = cipheredPass; }

	public Calendar getAcctBeginDate(int position) {
		if (position>=acctBeginDate.length || position<0)
			return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		return acctBeginDate[position];
	}

	public Calendar getAcctBeginDate(String account) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) return acctBeginDate[count];
		}
		return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
	}

	public void setAcctBeginDate(int position, Calendar acctBegin) {
		if (position>=0 && position<acctBeginDate.length)
			acctBeginDate[position] = acctBegin;
	}

	public void setAcctBeginDate(String account, Calendar acctBegin) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) acctBeginDate[count] = acctBegin;
		}
	}

	public Calendar getAcctEndDate(int position) {
		if (position>=acctEndDate.length || position<0)
			return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
		return acctEndDate[position];
	}

	public Calendar getAcctEndDate(String account) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) return acctEndDate[count];
		}
		return Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
	}

	public void setAcctEndDate(int position, Calendar acctEnd) {
		if (position>=0 && position<acctEndDate.length)
			acctEndDate[position] = acctEnd;
	}

	public void setAcctEndDate(String account, Calendar acctEnd) {
		int count = 0;
		for (count = 0; count < allAccounts.length; count++) {
			if (allAccounts[count].startsWith(account)) acctEndDate[count] = acctEnd;
		}
	}

	public String getActiveAccount() {
		return activeAccount;
	}

	public void setActiveAccount(String activeAccount) {
		this.activeAccount = activeAccount;
	}

	public String getActiveCategory() { return activeCategory; }

	public void setActiveCategory(String activeCategory) { this.activeCategory = activeCategory; }

	public static boolean isTextInList(String text, LinkedList<String> list) {
		for (String item : list) {
			if (item.compareToIgnoreCase(text)==0) return true;
		}
		return false;
	}

	public static boolean isTextInList(String text, String[] list) {
		for (String item : list) {
			if (item.compareToIgnoreCase(text)==0) return true;
		}
		return false;
	}

	public static int whereIsTextInList(String text, LinkedList<String> list) {
		for (int i=0; i<list.size(); i++) {
			if (list.get(i).compareToIgnoreCase(text)==0) return i;
		}
		return -1;
	}

	public static int whereIsTextInList(String text, String[] list) {
		for (int i=0; i<list.length; i++) {
			if (list[i].compareToIgnoreCase(text)==0) return i;
		}
		return -1;
	}

	public static String createAcctIdentifier(String acctNick, String acctNum, String bankName) {
		String output = "";
		if (acctNick.length()>0) output += acctNick;
		if (acctNick.length()>0 && (acctNum.length()>4 || bankName.length()>0)) output += " ";
		if (acctNum.length()>4) output += "…" + acctNum.substring(acctNum.length()-4, acctNum.length());
		if (acctNum.length()>4 && bankName.length()>0) output += " ";
		if (bankName.length()>0) output += "(" + bankName +")";
		return output;
	}

	private String getBankNameFromCurrAcctIdentifier() {
		String[] strArray = getActiveAccount().split("[( )]");
		for (String str : strArray) {
			for (String bank : allBanks) {
				if (str.compareToIgnoreCase(bank)==0) return bank;
			}
		}
		return "";
	}

	public void addBankToList(String bankName) {
		String[] tempBanks = new String[allBanks.length+1];
		System.arraycopy(allBanks, 0, tempBanks, 0, allBanks.length);
		tempBanks[tempBanks.length-1] = bankName;
		allBanks = tempBanks;
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
				String[] tempStr = allAccounts[count].split(" …");
				if (tempStr.length>1 && tempStr[1].startsWith(accountNumber)) return count;
			}
		}
		return -1;
	}

	public void clearManualEntries() {
		manualEntries = new String[0][7];
	}

	public void addManualEntry(String acctNick, Calendar date, String category) {
		String [][] temp = new String[manualEntries.length+1][7];
		System.arraycopy(manualEntries, 0, temp, 0, manualEntries.length);
		manualEntries = temp;
		manualEntries[manualEntries.length-1][0] = acctNick;
		manualEntries[manualEntries.length-1][1] = Transaction.returnYYYYMMDDFromCalendar(date);
		manualEntries[manualEntries.length-1][2] = "";
		manualEntries[manualEntries.length-1][3] = "";
		manualEntries[manualEntries.length-1][4] = "";
		manualEntries[manualEntries.length-1][5] = "";
		manualEntries[manualEntries.length-1][6] = category;
	}

	public void editManualEntry(int position, Request r) {
		if (position<0 || position>=manualEntries.length) return;
		manualEntries[position][0] = r.getAccountNick();
		manualEntries[position][1] = r.getTDate();
		manualEntries[position][2] = r.getTRef();
		manualEntries[position][3] = r.getTDesc();
		manualEntries[position][4] = r.getTMemo();
		manualEntries[position][5] = String.valueOf(r.getTAmount());
		manualEntries[position][6] = r.getTCat();
	}

	public void deleteManualEntry(int position) {
		if (position<0 || position>=manualEntries.length) return;
		if (manualEntries.length==0) return;
		String [][] temp = new String[manualEntries.length-1][7];
		System.arraycopy(manualEntries, 0, temp, 0, position);
		System.arraycopy(manualEntries, position+1, temp, position, manualEntries.length-position-1);
		manualEntries = temp;
	}

	public Result getManualEntry(int position) {
		Result r = new Result();
		if (manualEntries.length==0) return r;
		r.setAccountNick(manualEntries[position][0]);
		r.setTDate(manualEntries[position][1]);
		r.setTRef(manualEntries[position][2]);
		r.setTDesc(manualEntries[position][3]);
		r.setTMemo(manualEntries[position][4]);
		if (manualEntries[position][5].length()>0)
			r.setTAmount(Double.parseDouble(manualEntries[position][5]));
		r.setTCat(manualEntries[position][6]);
		return r;
	}

	public int getManualEntrySize() {
		return manualEntries.length;
	}

	public void changeManualEntryAccount(String accountNick) {
		for (int i = 0; i < manualEntries.length; i++) {
			manualEntries[i][0] = accountNick;
		}
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
			//GUI_ElementsOptionLists.getInstance().addAccntNickToList(activeAccount);
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
			uploadCurrentList();
			return returnRListIterator();
		}
		else {
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
		if (currentSummary!=null) System.out.println(currentSummary.toString());
		return rList.listIterator();
	}

	public boolean processManualEntries() {
		if (manualEntries.length==0) return false;
		if (isTextInList(manualEntries[0][0], allAccounts)) {
			switchActiveAccount(manualEntries[0][0]);
		} else {
			if (tList.size()>0) {
				uploadCurrentList();
				tList.clearTransactionList();
			}
			String[] tempAccounts = new String[(allAccounts.length+1)];
			System.arraycopy(allAccounts, 0, tempAccounts, 0, allAccounts.length);
			allAccounts = tempAccounts;
			allAccounts[allAccounts.length-1] = manualEntries[0][0];
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
		for (int j=0; j<manualEntries.length; j++) {
			Transaction newT = new Transaction(Transaction.returnCalendarFromYYYYMMDD(manualEntries[j][1]),
					manualEntries[j][2], manualEntries[j][3], manualEntries[j][4],
					Double.parseDouble(manualEntries[j][5]), manualEntries[j][6]);
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

	public ListIterator<Result> initialDBaseDownload() {

		// for each account fetch begin date and end date and store in the
		// arrays acctBeginDate and acctEndDate, in the order of accounts

		// fetch the user's first account's last 3-month-block
		// set activeAccount = ...;
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

	public void uploadCurrentList() {
		if (tList==null || tList.size()==0) return;
		// if there's no record in database and the current list is smaller than 3 months,
		// upload the current list, its start date, end date, check its categories against
		// their list and update it, banks, account nicks, and account numbers

		// if there's no record in database and the current list is longer than 3 months,
		// SPLIT THE LIST, upload first 3 months + everything, keep going, 3-month increments
		// until the whole list is uploaded, keep the last portion active

		// if there IS record in database and the current list is goes BEFORE the record, download
		// the first record below the current list, count backwards 3 months, SPLIT and upload INSTEAD
		// the first record, keep splitting and uploading BEFORE the first record until done. Each
		// time you upload do the begin-end date check, category check, bank, nick, and acct number check

		// if there IS record in database and the current list is goes AFTER the record, download
		// the last record above the current list, count onwards 3 months, SPLIT and upload INSTEAD
		// the last record, keep splitting and uploading AFTER the last record until done. Each
		// time you upload do the begin-end date check, category check, bank, nick, and acct number check

		Calendar startPlus3Months = Transaction.returnCalendarFromOFX(Transaction.returnOFXFromCalendar
				(tList.getStartDate()));
		startPlus3Months.add(Calendar.MONTH, 3);
		Calendar[] dbAcctFirstLast = firstAndLastEntryOfAccount(activeAccount);
		boolean isCurrentAcctInDB = currentUserHasAccount(activeAccount);
		boolean isCurrentListLessThan3Months = tList.getEndDate().compareTo(startPlus3Months)<=0;
		boolean currentListStartGoesBeforeDB = tList.getStartDate().compareTo(dbAcctFirstLast[0])<=0;
		boolean currentListEndGoesAfterDB = tList.getStartDate().compareTo(dbAcctFirstLast[1])>=0;
		if (!isCurrentAcctInDB && isCurrentListLessThan3Months) {
			upload3monthPortion(tList);
			setAcctBeginDate(activeAccount, tList.getStartDate());
			setAcctEndDate(activeAccount, tList.getEndDate());
		}
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

	public ListIterator<Result> goFirst() {
		uploadCurrentList();
		Calendar first = firstAndLastEntryOfAccount(activeAccount)[0];
		tList = download3monthPortion(first);
		currentSummary = Summary.downloadSummary(activeAccount, first, first);
		return returnRListIterator();
	}

	public ListIterator<Result> goPrevious() {
		uploadCurrentList();
		Calendar previous = previousAndNextEntryOfAccount(activeAccount, tList.getStartDate())[0];
		tList = download3monthPortion(previous);
		currentSummary = Summary.downloadSummary(activeAccount, previous, previous);
		return returnRListIterator();
	}

	public ListIterator<Result> goNext() {
		uploadCurrentList();
		Calendar next = previousAndNextEntryOfAccount(activeAccount, tList.getStartDate())[1];
		tList = download3monthPortion(next);
		currentSummary = Summary.downloadSummary(activeAccount, next, next);
		return returnRListIterator();
	}

	public ListIterator<Result> goLast() {
		uploadCurrentList();
		Calendar last = firstAndLastEntryOfAccount(activeAccount)[1];
		tList = download3monthPortion(last);
		currentSummary = Summary.downloadSummary(activeAccount, last, last);
		return returnRListIterator();
	}

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

	public void changeCategoryToActive(String refNum) {
		if (tList==null || tList.size()==0) return;
		tList.searchByRefNumber(refNum).setCategory(activeCategory);
	}

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
}