package main_logic;

//import java.util.ArrayList;
//import java.util.ListIterator;

import db_connectors.Connectivity;
import entities.Transaction;
import entities.TransactionList;
import parsers.OFXParser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static main_logic.Result.Code.*;
import static parsers.OFXParser.ofxParser;

public class PEC {

	public static String NEW_BANK = "<NEW BANK>";
	public static String NEW_ACCOUNT = "<NEW ACCOUNT>";
	public static String OTHER = "<OTHER>";

	private static String[] presetCategories = new String[]
			{"Food","Car Repair","Mortgage", "Car insurance", "Fun"};

	// private main structure housing active Transaction data
	// (no more than 3 months worth)
	private TransactionList tList;
	// these variables contain the date of the first and last transaction
	// in each account of the authenticated user in the database, both initialized
	// with STR_DATE_MIN of TransactionList.java (1900/01/01)--that way, if the
	// database is empty, the parsing or manual entries can start anytime after that
	// initial date
	private Calendar[] acctBeginDate = new Calendar[0];
	private Calendar[] acctEndDate = new Calendar[0];
	private String[] allBanks = {};
	private String[] allAccounts = {};
	private String[] allCategories = {};
	private String activeAccount;
	// array of booleans to remember if a particular column is sorted
	// in a descending (or ascending) direction
	private boolean[] descColumn = { true, true, true, true, true, true };
	// sortedColumn indicates which column is active and sorted on screen
	private int sortedColumn = Transaction.POSTED_DATE;
	// current user (after authentication) will have their own space in the database,
	// their own presets, and communicate with the system in their own, unique way

	private int currentUserID = 0;

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

	/**
	 * Sets all columns to be viewed in descending order, sets the sorted
	 * column to go by as the one with the "date posted".
	 */
	private void resetView() {
		for (int i = 0; i < descColumn.length; i++) {
			descColumn[i] = true;
		}
		sortedColumn = Transaction.POSTED_DATE;
	}

	public int getCurrentUserID() { return currentUserID; }

	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

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

	public static boolean isTextInList(String text, LinkedList<String> list) {
		for (String item : list) {
			if (item.compareToIgnoreCase(text)==0) return true;
		}
		return false;
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
				if (tempStr[1].startsWith(accountNumber)) return count;
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
			// set up a new account, clear the table; the following is temporary
			String[] tempAccounts = new String[(allAccounts.length+1)];
			System.arraycopy(allAccounts, 0, tempAccounts, 0, allAccounts.length);
			allAccounts = tempAccounts;
			String newParsedNick = "My "+OFXParser.getAcctType();
			allAccounts[allAccounts.length-1] = createAcctIdentifier(newParsedNick,
					OFXParser.getAcctNumber(), OFXParser.getBankName());
			activeAccount = newParsedNick;
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
			String[] tempStr = allAccounts[acctPos].split(" …");
			setActiveAccount(tempStr[0]);
		}
		if (mergeNewTList(parsedTlist)) return returnRListIterator();
		else {
			result.setCode(NO_ITEMS_TO_READ);
			rList.add(result);
			return rList.listIterator();
		}
	}

	/**
	 * Merges a new list (from parsing or manual entry) into the database.
	 * @param list - list to be merged
	 * @return - TRUE if succeeded, FALSE if nothing got merged
	 */
	private boolean mergeNewTList(TransactionList list) {
		boolean change = false;
		if (list==null) return change;
		System.out.println("POTENTIALLY ADDING "+list.size()+", active acct: "+activeAccount);
		for (int i = 0; i < allAccounts.length; i++) { System.out.println(allAccounts[i]); }
		System.out.println("\n");
		for (int i = 0; i < acctBeginDate.length; i++) { System.out.println(Transaction.returnYYYYMMDDFromCalendar
				(acctBeginDate[i])+"-"+Transaction.returnYYYYMMDDFromCalendar(acctEndDate[i])); }
		TransactionList resultTList = new TransactionList();
		if (list.getStartDate().compareTo(getAcctBeginDate(activeAccount))<=0) {
			// fetch the first 3-or-less-month chunk of the db and load it in tList
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
			return change;
		} else if (list.getEndDate().compareTo(getAcctEndDate(activeAccount))>=0) {
			// fetch the last 3-or-less-month chunk of db and load it in tList
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


	/*
	/**
	 * Fetches the Transaction list, sorts it by the active column criterion
	 * and distinguishes whether the data are in descending or ascending order.
	 * @return the Result object list with all Transaction fields filled out
	 */
	/*
	private ListIterator<Result> getNewView() {
		ListIterator<Transaction> it = tList.sort(sortedColumn);
		ArrayList<Result> resIt = new ArrayList<Result>();
		if (descColumn[sortedColumn]) {
			while (it.hasNext()) {
				Result result = new Result();
				result.setTFields(it.next());
				resIt.add(result);
			}
		} else {
			while (it.hasNext()) { it.next(); }
			while (it.hasPrevious()) {
				Result result = new Result();
				result.setTFields(it.previous());
				resIt.add(result);
			}
		}
		return resIt.listIterator();
	}

	/**
	 * Switches the view between descending and ascending order.
	 * @return new IteratorList to view
	 */
	/*
	public ListIterator<Result> sortingOrientationSwitched() {
		descColumn[sortedColumn] = !descColumn[sortedColumn];
		return getNewView();
	}
	*/

	public boolean processSingleManualEntry(Request request) {
		int acctPos = accountPosition(request.getAccountNick(), "");
		if (acctPos==-1) {
			// set up a new account, clear the table; the following is temporary
			String[] tempAccounts = new String[(allAccounts.length+1)];
			System.arraycopy(allAccounts, 0, tempAccounts, 0, allAccounts.length);
			allAccounts = tempAccounts;
			allAccounts[allAccounts.length-1] =createAcctIdentifier(request.getAccountNick(),
					request.getAccountNumber(), request.getBankName());
			activeAccount = request.getAccountNick();
			Calendar[] tempBegin = new Calendar[acctBeginDate.length+1];
			Calendar[] tempEnd = new Calendar[acctEndDate.length+1];
			System.arraycopy(acctBeginDate, 0, tempBegin, 0, acctBeginDate.length);
			System.arraycopy(acctEndDate, 0, tempEnd, 0, acctEndDate.length);
			tempBegin[tempBegin.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			tempEnd[tempEnd.length-1] = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
			acctBeginDate = tempBegin;
			acctEndDate = tempEnd;
		} else {
			// if the added account is not the active account, fetch it and make it active
			setActiveAccount(request.getAccountNick());
		}
		// if a new Category is present in request.tCat at time of syncing local<--->database, it will get
		// written in Category table.
		Transaction newT = new Transaction(Transaction.returnCalendarFromYYYYMMDD(request.getTDate()),
				request.getTRef(), request.getTDesc(), request.getTMemo(), request.getTAmount(), request.getTCat());
		TransactionList list = new TransactionList();
		list.add(newT);
		return mergeNewTList(list);
	}

	public ListIterator<Result> initialDBaseDownload() {
		// for each account fetch begin date and end date and store in the
		// arrays acctBeginDate and acctEndDate, in the order of accounts

		// fetch the user's first account's last 3-month-block
		// set activeAccount = ...;

		return returnRListIterator();
	}

	public void uploadCurrentList() {
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
	}

	public ListIterator<Result> goFirst() {
		// upload the current list
		// fetch the first 3-month block from user's database and download it to tList
		return returnRListIterator();
	}

	public ListIterator<Result> goPrevious() {
		// upload the current list
		// use tList.getStartDate() to figure out previous transaction_date,
		// fetch that block from user's database and download it to tList
		return returnRListIterator();
	}

	public ListIterator<Result> goNext() {
		// upload the current list
		// use tList.getStartDate() to figure out next transaction_date,
		// fetch that block from user's database and download it to tList
		return returnRListIterator();
	}

	public ListIterator<Result> goLast() {
		// upload the current list
		// fetch the last 3-month block from user's database and download it to tList
		return returnRListIterator();
	}

	public int login(Request r) throws SQLException {
		int userId = -1;
		boolean result = false;

		Connectivity connectivity = new Connectivity();
		Connection connection = connectivity.getConnection();
		String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, r.getEmail());
			statement.setString(2, r.getPass1());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				userId = resultSet.getInt("user_id");
				result = true;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		if (result) {
			currentUserID = userId;
			return userId;
		} else {
			return -1;
		}
	}

	public int signup(Request r) throws SQLException {

		int checkCode = 0;
		// Connect to the database
		Connectivity connectivity = new Connectivity();
		Connection conn = connectivity.getConnection();

		String email = r.getEmail();
		String pass1 = r.getPass1();
		String pass2 = r.getPass2();
		String question1 = r.getQuestion1();
		String question2 = r.getQuestion2();
		String answer1 = r.getAnswer1();
		String answer2 = r.getAnswer2();

		// Check if the email is in the right format
		if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			checkCode = 1;
		} else {
			String checkSql = "SELECT COUNT(*) FROM users WHERE email=?";
			PreparedStatement checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setString(1, email);

			// Execute the query to check if the user already exists
			ResultSet rs = checkStmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);

			if (count == 0) {
				if (pass1.length() >= 8 && pass1.length() < 20) {
					System.out.println(pass1 + "123");
					if (!pass1.equals(pass2)) {
						checkCode = 4;
					} else {


						// Create a PreparedStatement to insert a new user
						String sql = "INSERT INTO users (email,password,Question1,Question2,answer1,answer2,created_date) " +
								"VALUES ( ?,?,?,?,?,?,now())";
						PreparedStatement stmt = conn.prepareStatement(sql);
						stmt.setString(1, email);
						stmt.setString(2, pass1);
						stmt.setString(3, question1);
						stmt.setString(4, question2);
						stmt.setString(5, answer1);
						stmt.setString(6, answer2);


						// Execute the query and check the number of rows affected
						int rowsAffected = stmt.executeUpdate();
						if (rowsAffected > 0) {
							checkCode = 5;
						} else {
							checkCode = 6;
						}

						// Close the connection and statement
						stmt.close();
						conn.close();

					}
				} else {
					checkCode = 3;
				}

			} else {
				checkCode = 2;
			}


		}
		return checkCode;
	}

	public Result downloadDropDownMenuEntries() throws SQLException {
		Result result = new Result();
		List<String> banks = new ArrayList<String>();
		List<String> accounts = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		// downloading bank list
		Connectivity connectivity = new Connectivity();
		Connection connection = connectivity.getConnection();
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
		connectivity = new Connectivity();
		connection = connectivity.getConnection();
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
		connectivity = new Connectivity();
		connection = connectivity.getConnection();
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
		return result;
	}

	public void addCategoriesForUser() throws SQLException {
		Connectivity connectivity = new Connectivity();
		Connection connection = connectivity.getConnection();
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

	/*

	// --------------------------------------------------------------------------
	public List<String> getTransactionHistory1(Request r) throws SQLException {
		List<String> transactionHistories = new ArrayList<>();
		String query = "SELECT transaction_history FROM transaction "
				+ "WHERE user_id = ? AND transaction_date = ? AND account_nick = ?";
		Connectivity connectivity = new Connectivity();
		Connection connection = connectivity.getConnection();

		PreparedStatement stmt = connection.prepareStatement(query);


		stmt.setInt(1, getCurrentUserID());
		stmt.setString(2, r.getDate_range());
		stmt.setString(3, r.getAccountNick());
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			// Get the encrypted transaction history
			String encryptedTransactionHistory = rs.getString("transaction_history");

			// Decrypt the transaction history using the encryption algorithm
			String decryptedTransactionHistory = decryptTransactionHistory(encryptedTransactionHistory);

			// Add the decrypted transaction history to the list
			transactionHistories.add(decryptedTransactionHistory);
		}


		return transactionHistories;
	}
	private String decryptTransactionHistory(String encryptedTransactionHistory) {
		// Decrypt the transaction history using the encryption algorithm
		// ...
		String decryptedTransactionHistory="";
		return decryptedTransactionHistory;
	}





	public String getTransactionHistory(Request r) throws SQLException, ParseException {
		Connectivity connectivity = new Connectivity();
		Connection connection = connectivity.getConnection();
		String transactionHistory = "";
		int transactionId = Integer.parseInt(r.getParameter("transaction_id"));


		// Create a database connection


		// Get the date range and user_id for the specified transaction_id
		PreparedStatement ps = connection.prepareStatement("SELECT date_range, user_id FROM transaction WHERE transaction_id = ?");
		ps.setInt(1, transactionId);
		ResultSet rs = ps.executeQuery();
		rs.next();
		String dateRange = rs.getString("date_range");
		int userId = rs.getInt("user_id");

		// Parse the date range string into its start and end date components
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		String startDateStr = dateRange.substring(0, 8);
		String endDateStr = dateRange.substring(8);
		Date startDate = dateFormat.parse(startDateStr);
		Date endDate = dateFormat.parse(endDateStr);

		// Calculate the date 3 months prior to the start date
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, -3);
		Date prevStartDate = cal.getTime();

		// Retrieve the transaction history for the same user_id within the previous 3 months
		ps = connection.prepareStatement("SELECT transaction_history FROM transaction WHERE user_id = ? AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC");
		ps.setInt(1, userId);
		ps.setString(2, dateFormat.format(prevStartDate));
		ps.setString(3, endDateStr);
		rs = ps.executeQuery();

		// Concatenate the transaction histories into a single string
		while (rs.next()) {
			transactionHistory += rs.getString("transaction_history") + "\n";
		}

		// Close the database connection
		connection.close();


		return transactionHistory;
	}

	// ---------------------------------------------------------------------------------
	 */

	/*
	public static void main(String[] args) {
		Request request = Request.instance();
		ListIterator<Result> it;
		request.setFileWithPath("/Users/starnet/CreditCardSAMPLE.qfx");
		System.out.println("Now parsing.");
		it = PEC.instance().parseOFX(request);
		while (it.hasNext()) {
			System.out.println(it.next().getTDesc());
		}
	}
	*/
}
