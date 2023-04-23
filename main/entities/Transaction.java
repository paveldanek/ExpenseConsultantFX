package entities;


import java.util.Calendar;

/**
 *
 * Transaction class represents a single bank transaction.
 *
 * @author SPAM (Sammy Dinka, Andrey Yefermov, Pavel Danek) © 2023
 *
 */
public class Transaction {

    // Category Name examples { "<OTHER>", "INCOME", "FEE", "ESSENTIALS", "TRANSPORT", "ENTERTAINMENT", "ALCOHOL" };

//	many more to be added

    private Calendar postedDate;
    private String refNumber;
    private String description;
    private String memo;
    private double amount;
    private String category;

    //	Constants representing different fields of Transaction; used in sorting.
    public static final int POSTED_DATE = 0, REF_NUMBER = 1, DESCRIPTION = 2, MEMO = 3, AMOUNT = 4, CATEGORY = 5;

    /**
     * Class constructor.
     */
    public Transaction(Calendar postedDate, String refNumber, String description, String memo, double amount,
                       String category) {
        this.postedDate = postedDate;
        this.refNumber = refNumber;
        this.description = description;
        this.memo = memo;
        this.amount = amount;
        this.category = category;
    }

// -------------- static Calendar conversion helpers -----------------------
//
    /**
     * Returns a Calendar type of date from a String format, used in exported OFX
     * files.
     *
     * @param stringDate - format YYYYMMDDHHMMSS
     * @return same date as a Calendar type
     */
    public static Calendar returnCalendarFromOFX(String stringDate) {
        Calendar date = Calendar.getInstance();
        int year, month, day, hour, minute, second;
//		if stringDate is not complete (no HHMMSS), add zeros to complete it and
//		preserve the value of date; if is even shorter than that (no apparent
//		YYYYMMDD) or otherwise compromised, then catch clause will get it and
//		change the date to 1900/01/01.
        if (stringDate.length() < 14) {
            stringDate += "000000";
        }
        try {
            year = Integer.parseInt(stringDate.substring(0, 4));
//			the twelve months in Calendar range from 0-11
            month = (Integer.parseInt(stringDate.substring(4, 6))) - 1;
            day = Integer.parseInt(stringDate.substring(6, 8));
            hour = Integer.parseInt(stringDate.substring(8, 10));
            minute = Integer.parseInt(stringDate.substring(10, 12));
            second = Integer.parseInt(stringDate.substring(12, 14));
        } catch (NumberFormatException ex) {
            year = 1900;
            month = 0;
            day = 1;
            hour = 0;
            minute = 0;
            second = 0;
        }
        date.set(year, month, day, hour, minute, second);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    /**
     * Returns a Calendar type of date from a String format, used in the table.
     *
     * @param stringDate - formats YYYY/MM/DD, MM/DD/YYYY
     * @return same date as a Calendar type
     */
    public static Calendar returnCalendarFromYYYYMMDD(String stringDate) {
        String[] ymd = stringDate.split("[/. :-]");
        if (ymd.length<3) stringDate = "19000101000000";
        else
        if (ymd[0].length()==4) {
            stringDate = ymd[0] + ymd[1] + ymd[2] + "000000";
        } else if (ymd[2].length()==4){
            stringDate = ymd[2] + ymd[0] + ymd[1] + "000000";
        }
        return returnCalendarFromOFX(stringDate);
    }

    /**
     * Returns a String format date, used in exported OFX files, from a Calendar
     * type of date.
     *
     * @param date - Calendar type
     * @return a String in format YYYYMMDDHHMMSS
     */
    public static String returnOFXFromCalendar(Calendar date) {
        String year, month, day, hour, minute, second;
        year = Integer.toString(date.get(Calendar.YEAR));
//		the twelve months in Calendar range from 0-11
        month = Integer.toString(date.get(Calendar.MONTH) + 1);
        day = Integer.toString(date.get(Calendar.DATE));
        hour = Integer.toString(date.get(Calendar.HOUR_OF_DAY));
        minute = Integer.toString(date.get(Calendar.MINUTE));
        second = Integer.toString(date.get(Calendar.SECOND));
        if (month.length() == 1)
            month = "0" + month;
        if (day.length() == 1)
            day = "0" + day;
        if (hour.length() == 1)
            hour = "0" + hour;
        if (minute.length() == 1)
            minute = "0" + minute;
        if (second.length() == 1)
            second = "0" + second;
        return year + month + day + hour + minute + second;
    }

    /**
     * Returns a String format date, used in paper statements, from a Calendar type
     * of date.
     *
     * @param date - Calendar type
     * @return a String in format YYYY/MM/DD
     */
    public static String returnYYYYMMDDFromCalendar(Calendar date) {
        String year, month, day;
        year = Integer.toString(date.get(Calendar.YEAR));
        month = Integer.toString(date.get(Calendar.MONTH) + 1);
        day = Integer.toString(date.get(Calendar.DATE));
        if (month.length() == 1)
            month = "0" + month;
        if (day.length() == 1)
            day = "0" + day;
        return year + "/" + month + "/" + day;
    }

// --------------- getters and setters ------------------------------------

    public Calendar getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Calendar postedDate) {
        this.postedDate = postedDate;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

// ------------------- compare-by-criteria methods -------------------------
//
//  ALWAYS just ONE property of the two Transactions is being compared;
//
//  if NEGATIVE number is returned, the calling Transaction
//	is "LESS THAN" the Transaction in parameters,
//
//  if 0 is returned, the two Transactions are "EQUAL",
//
//  if POSITIVE number is returned, the calling Transaction
//	is "GREATER THAN" the Transaction in parameters,

    public int comparePostedDates(Transaction transaction) {
        return this.postedDate.compareTo(transaction.getPostedDate());
    }

    public int compareRefNumbers(Transaction transaction) {
        return this.refNumber.compareTo(transaction.getRefNumber());
    }

    public int compareDescriptions(Transaction transaction) {
        return this.description.compareTo(transaction.getDescription());
    }

    public int compareMemos(Transaction transaction) {
        return this.memo.compareTo(transaction.getMemo());
    }

    public double compareAmounts(Transaction transaction) {
        return this.amount - transaction.getAmount();
    }

    public int compareCategories(Transaction transaction) {
        return this.category.compareTo(transaction.getCategory());
    }

// ------------------- other methods ---------------------------------------
//
    /**
     *
     * @param firstDate, secondDate - the boundaries, inside which the Transaction
     *                   should be
     * @return TRUE - the Transaction has been time-stamped between the boundary dates
     *                (boundary dates included)
     *         FALSE - the Transaction has been time-stamped outside the boundary dates
     */
    public boolean isBetweenDates(Calendar firstDate, Calendar secondDate) {
        return (!postedDate.before(firstDate) && !postedDate.after(secondDate));
    }

    @Override
    public String toString() {
        String output = returnYYYYMMDDFromCalendar(this.postedDate) + "\t";
        output += this.refNumber + "\t";
        output += this.description + "\t";
        output += this.memo + "\t";
        output += String.format("$%8.2f", amount) + "\t";
        output += this.category;
        return output;
    }

    /*
	@Override
	public String toString() {
		String output = "THIS TRANSACTION:\n------------------------------------\n";
		output += "Posted Date:      " + returnMMslashDDFromCalendar(this.postedDate) + "\n";
		output += "Reference Number: " + this.refNumber + "\n";
		output += "Description:      " + this.description + "\n";
		output += "Memo:             " + this.memo + "\n";
		output += "Amount:           " + String.format("$ %.2f", amount) + "\n";
		output += "Category:         " + this.category + "\n------------------------------------\n";
		return output;
	}
    */

    /*
	public static void main(String[] args) {
		System.out.println(returnOFXFromCalendar(returnCalendarFromOFX("20220415201405")));
		System.out.println("\n\n");
		Transaction t1 = new Transaction(returnCalendarFromMMslashDD("02/22", "2022"),
				"S254WD98422000A1", "Cub Foods #1224", "Quick groceries...", 126.74, getACategoryValue("ESSENTIALS"));
		Transaction t2 = new Transaction(returnCalendarFromMMslashDD("02/21", "2022"),
				"R254WD98422000A1", "Rainbow Foods", "Birthday present purchase", 122, getACategoryValue("ESSENTIALS"));
		System.out.println(t1);
		System.out.println(t2 + "\n\n");
		System.out.println("COMPARISONS:\n------------------");
		System.out.println("Posted Dates:   " + t1.comparePostedDates(t2) + "\n");
		System.out.println("Ref. Numbers:   " + t1.compareRefNumbers(t2) + "\n");
		System.out.println("Descriptions:   " + t1.compareDescriptions(t2) + "\n");
		System.out.println("Memos:          " + t1.compareMemos(t2) + "\n");
		System.out.println("Amounts:        " + t1.compareAmounts(t2) + "\n");
		System.out.println("Categories:     " + t1.compareCategories(t2) + "\n------------------");
	}
	*/
}
