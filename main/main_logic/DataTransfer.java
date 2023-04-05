package main_logic;

import entities.Transaction;
import gui_v1.mainWindows.manualEntryWElements.GUI_ManualTransactionsEntryP;
import gui_v1.mainWindows.newAccountWElements.GUI_NewAccountP;
import gui_v1.mainWindows.newBankWElements.GUI_NewBankP;
import gui_v1.mainWindows.newCategoryWElements.GUI_NewCategoryP;
import gui_v1.mainWindows.recordsWElements.GUI_RecordsBoxP;

import javax.swing.*;

public class DataTransfer {


	// Transaction fields
	private String tDate;
	private String tRef;
	private String tDesc;
	private String tMemo;
	private Double tAmount;
	private String tCat;
	// parse OFX file fields/ manual entry
	private String fileWithPath;
	private String accountNick;
	private String accountNumber;
	private String bankName;
	// drop down lists for the main logic
	private String[] acctList;
	private String[] bankList;
	private String[] categoryList;
	// fields for sign up
	private String email;
	private String password1;
	private String password2;
	private String question1;
	private String question2;
	private String answer1;
	private String answer2;
	// fields to hold references to instantiated GUI objects
	private GUI_RecordsBoxP mainWindowHolder = null;
	private JTable recordsTableHolder = null;
	private GUI_ManualTransactionsEntryP manualEntryWindowHolder = null;
	private GUI_NewAccountP newAccountWindowHolder = null;
	private GUI_NewCategoryP newCategoryWindowHolder = null;
	private GUI_NewBankP newBankWindowHolder = null;

	// ... Both GUI programmers and LOGIC programmers are allowed to add fields and
	// corresponding methods to convey functionality back and forth between LOGIC
	// and GUI.

	public DataTransfer() {
		reset();
	}

	/**
	 * Resets all fields to "", null, or zero.
	 */
	public void reset() {
		tDate = "";
		tRef = "";
		tDesc = "";
		tMemo = "";
		tAmount = 0.0;
		tCat = "";
		fileWithPath = "";
		accountNick = "";
		accountNumber = "";
		bankName = "";
		acctList = new String[0];
		bankList = new String[0];
		categoryList = new String[0];
		email = "";
		password1 = "";
		password2 = "";
		question1 = "";
		question2 = "";
		answer1 = "";
		answer2 = "";
		// ...
	}

	public GUI_RecordsBoxP getMainWindowHolder() { return mainWindowHolder;	}

	public void setMainWindowHolder(GUI_RecordsBoxP holder) { mainWindowHolder = holder; }

	public JTable getRecordsTableHolder() { return recordsTableHolder; }

	public void setRecordsTableHolder(JTable holder) { recordsTableHolder = holder; }

	public GUI_ManualTransactionsEntryP getManualEntryWindowHolder() { return manualEntryWindowHolder; }

	public void setManualEntryWindowHolder(GUI_ManualTransactionsEntryP manualEntryWindowHolder) {
		this.manualEntryWindowHolder = manualEntryWindowHolder;
	}

	public GUI_NewAccountP getNewAccountWindowHolder() { return newAccountWindowHolder; }

	public void setNewAccountWindowHolder(GUI_NewAccountP newAccountWindowHolder) {
		this.newAccountWindowHolder = newAccountWindowHolder;
	}

	public GUI_NewCategoryP getNewCategoryWindowHolder() { return newCategoryWindowHolder; }

	public void setNewCategoryWindowHolder(GUI_NewCategoryP newCategoryWindowHolder) {
		this.newCategoryWindowHolder = newCategoryWindowHolder;
	}

	public GUI_NewBankP getNewBankWindowHolder() { return newBankWindowHolder; }

	public void setNewBankWindowHolder(GUI_NewBankP newBankWindowHolder) {
		this.newBankWindowHolder = newBankWindowHolder;
	}

	public String getTDate() {
		return tDate;
	}

	public void setTDate(String tDate) {
		this.tDate = tDate;
	}

	public String getTRef() {
		return tRef;
	}

	public void setTRef(String tRef) {
		this.tRef = tRef;
	}

	public String getTDesc() {
		return tDesc;
	}

	public void setTDesc(String tDesc) {
		this.tDesc = tDesc;
	}

	public String getTMemo() {
		return tMemo;
	}

	public void setTMemo(String tMemo) {
		this.tMemo = tMemo;
	}

	public Double getTAmount() {
		return tAmount;
	}

	public void setTAmount(Double tAmount) {
		this.tAmount = tAmount;
	}

	public String getTCat() {
		return tCat;
	}

	public void setTCat(String tCat) {
		this.tCat = tCat;
	}

	public String getFileWithPath() {
		return fileWithPath;
	}

	public void setFileWithPath(String fileWithPath) {
		this.fileWithPath = fileWithPath;
	}

	public String getAccountNick() { return accountNick; }

	public void setAccountNick(String accountNick) { this.accountNick = accountNick; }

	public String getAccountNumber() { return accountNumber; }

	public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

	public String getBankName() { return bankName; }

	public void setBankName(String bankName) { this.bankName = bankName; }

	// more getters and setters: ...

	public void setTFields(Transaction transaction) {
		this.tDate = Transaction.returnYYYYMMDDFromCalendar(transaction.getPostedDate());
		this.tRef = transaction.getRefNumber();
		this.tDesc = transaction.getDescription();
		this.tMemo = transaction.getMemo();
//		if (transaction.getAmount()<0) { this.tAmount = String.format("$%.2f", transaction.getAmount()); }
//		else { this.tAmount = String.format("$ %.2f", transaction.getAmount()); }
		this.tAmount = transaction.getAmount();
		//this.tAmount = ((int)(transaction.getAmount() * 100))/100.0;
		this.tCat = transaction.getCategory();
	}

	public String[] getAcctList() { return acctList; }

	public void setAcctList(String[] acctList) { this.acctList = acctList; }

	public String[] getBankList() { return bankList; }

	public void setBankList(String[] bankList) { this.bankList = bankList; }

	public String[] getCategoryList() { return categoryList; }

	public void setCategoryList(String[] categoryList) { this.categoryList = categoryList; }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass1() {
		return password1;
	}

	public void setPass1(String password1) {
		this.password1 = password1;
	}

	public String getPass2() {
		return password2;
	}

	public void setPass2(String password2) {
		this.password2 = password2;
	}

	public String getQuestion1() {
		return question1;
	}

	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	public String getQuestion2() {
		return question2;
	}

	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
}
