package summary;

import crypto.AESUtil;
import entities.Transaction;
import entities.TransactionList;
import main_logic.PEC;
import db_connectors.Connectivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * This class creates and keeps a Summary of a single-account per-period activity for
 * Personal Expense Consultant.
 */
public class Summary {

    // essencial metrics and values the Summary holds
    private double totalExpense = 0.0;
    private double totalIncome = 0.0;
    private Calendar periodBegin = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private Calendar periodEnd = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private ArrayList<CatTotal> catTotals = new ArrayList<CatTotal>();
    private String accountNick = "";
    // helper variables: start and end are periodBegin and periodEnd millisecond representation
    // (typically for Java, milliseconds are measured from the beginning point, midnight, Jan 1, 1970 UTC).
    // periodDayAmount is the length of the specific period (up to 3 months) in days
    private long start = 0, end = 0;
    int periodDayAmount = 0;
    // a time stamp of when the summary was generated
    private Calendar timeStamp = null;

    /**
     * Creates a Summary from a TransactionList.
     * @param list the TransactionList supplied
     */
    public Summary(TransactionList list) {
        if (list==null) return;
        if (list.size()==0) return;
        periodBegin = list.getStartDate();
        periodEnd = list.getEndDate();
        accountNick = PEC.instance().getActiveAccount();
        int index = -1;
        double amount = 0.0;
        for (int i = 0; i < list.size(); i++) {
            index = returnCatIndex(list.get(i).getCategory());
            amount = list.get(i).getAmount();
            if (amount<=0.0) totalExpense += amount;
            else totalIncome += amount;
            if (index==-1) {
                catTotals.add(new CatTotal(list.get(i).getCategory(), amount));
            } else {
                catTotals.get(index).setTotalPerPeriod(catTotals.get(index).getTotalPerPeriod()+amount);
            }
        }
        start = periodBegin.getTimeInMillis();
        end = periodEnd.getTimeInMillis();
        periodDayAmount = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) + 1;
        for (int k = 0; k < catTotals.size(); k++) {
            catTotals.get(k).calculatePercentageAndAverage(totalExpense, totalIncome, periodDayAmount);
        }
        timeStamp = Calendar.getInstance();
    }

    /**
     * Creates a new, blank Summary.
     */
    public Summary() {
    }

    /**
     * Makes a Summary from downloaded information from the database. While this may seem
     * convenient it's only recomended for other than current TransactionList in use. The
     * current, most up-to-minute Summary should be always created from scratch, from a
     * TransactionList.
     * @param acctNick downloads the Summary from the specified account on file
     * @param from downloads the Summary from the specified time index
     * @return the downloaded Summary
     */
    public static Summary downloadSingleSummary(String acctNick, Calendar from) {
        Connection connection = Connectivity.getConnection();
        Summary summary = new Summary();
        String query = "SELECT end_date, total_out, total_in, category_totals FROM summary "
                + "WHERE user_id = ? AND begin_date = ? AND account_nick = ?";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, PEC.instance().getCurrentUserID());
            stmt.setString(2, Transaction.returnYYYYMMDDFromCalendar(from));
            stmt.setString(3, acctNick);
            ResultSet rs = stmt.executeQuery();
            String result = "";
            if (rs.next()) {
                summary.accountNick = acctNick;
                summary.periodBegin = from;
                summary.periodEnd = Transaction.returnCalendarFromYYYYMMDD(rs.getString("end_date"));
                summary.totalExpense = Double.parseDouble(AESUtil.decryptItem(rs.getString("total_out")));
                summary.totalIncome = Double.parseDouble(AESUtil.decryptItem(rs.getString("total_in")));
                result = rs.getString("category_totals");
                result = AESUtil.decryptStringTable(result, PEC.instance().getCurrentUserPass());
                summary.catTotals = AESUtil.stringIntoCatTotals(result);
                summary.start = summary.periodBegin.getTimeInMillis();
                summary.end = summary.periodEnd.getTimeInMillis();
                summary.periodDayAmount = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(summary.end - summary.start)) + 1;
                summary.timeStamp = Calendar.getInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return summary;
    }

    /**
     * Deletes a specified Summary from the database.
     * @param dateStarting a specific time index of a period to be deleted
     * @param acctNick a specific account
     */
    public static void deleteSummary(Calendar dateStarting, String acctNick) {
        Connection connection = Connectivity.getConnection();
        String sql = "DELETE FROM summary WHERE user_id = ? AND begin_date = ? AND account_nick = ?";
        try {
            PreparedStatement s = connection.prepareStatement(sql);
            s.setInt(1, PEC.instance().getCurrentUserID());
            s.setString(2, Transaction.returnYYYYMMDDFromCalendar(dateStarting));
            s.setString(3, acctNick);
            int rowsAffected = s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new Summary from scratch from a TransactionList and uploads it to database.
     * @param tList the TransactionList the Summary will be created from
     * @param acctNick the nickname of the account the Summary will be associated with
     */
    public static void uploadSummary(TransactionList tList, String acctNick) {
        Summary summary = new Summary(tList);
        Connection connection = Connectivity.getConnection();
        String sql = "INSERT INTO summary (begin_date, end_date, total_out, total_in, "+
                "category_totals, account_nick, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement s = connection.prepareStatement(sql);
            s.setString(1, Transaction.returnYYYYMMDDFromCalendar(summary.getPeriodBegin()));
            s.setString(2, Transaction.returnYYYYMMDDFromCalendar(summary.getPeriodEnd()));
            s.setString(3, AESUtil.encryptItem(String.valueOf(summary.getTotalExpense())));
            s.setString(4, AESUtil.encryptItem(String.valueOf(summary.getTotalIncome())));
            s.setString(5, AESUtil.encryptStringTable(AESUtil.catTotalsIntoString
                    (summary.getCatTotals()), PEC.instance().getCurrentUserPass()));
            s.setString(6, acctNick);
            s.setInt(7, PEC.instance().getCurrentUserID());
            int rowsAffected = s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and populates a new Summary for a specific account accross multiple time periods.
     * If just one period is desired, the from and to parameters should be the same.
     * @param acctNick the requested account
     * @param from the time index of the first period included
     * @param to the time index of the last period included
     * @return the aggregated Summary for the time period specified
     */
    public static Summary downloadSummary(String acctNick, Calendar from, Calendar to) {
        if (from.compareTo(to)>0) {
            Calendar temp = to;
            to = from;
            from = temp;
        }
        Summary totalSummary = new Summary();
        Connection connection = Connectivity.getConnection();
        String query = "SELECT begin_date, end_date, total_out, total_in, category_totals "+
                "FROM summary WHERE begin_date >= ? "+
                "AND begin_date <= ? AND user_id = ? AND account_nick = ?";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, Transaction.returnYYYYMMDDFromCalendar(from));
            stmt.setString(2, Transaction.returnYYYYMMDDFromCalendar(to));
            stmt.setInt(3, PEC.instance().getCurrentUserID());
            stmt.setString(4, acctNick);
            ResultSet rs = stmt.executeQuery();
            String result = "";
            while (rs.next()) {
                Calendar bd = Transaction.returnCalendarFromYYYYMMDD(rs.getString("begin_date"));
                Calendar ed = Transaction.returnCalendarFromYYYYMMDD(rs.getString("end_date"));
                ArrayList<CatTotal> catTotalList = new ArrayList<CatTotal>();
                if (totalSummary.getPeriodBegin().compareTo
                        (Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN))==0) {
                    totalSummary.setPeriodBegin(bd);
                    totalSummary.setPeriodEnd(ed);
                }
                else {
                    if (totalSummary.getPeriodBegin().compareTo(bd)>0) totalSummary.setPeriodBegin(bd);
                    if (totalSummary.getPeriodEnd().compareTo(ed)<0) totalSummary.setPeriodEnd(ed);
                }
                totalSummary.incrementTotalExpense(
                        Double.parseDouble(AESUtil.decryptItem(rs.getString("total_out"))));
                totalSummary.incrementTotalIncome(
                        Double.parseDouble(AESUtil.decryptItem(rs.getString("total_in"))));
                catTotalList = AESUtil.stringIntoCatTotals(AESUtil.decryptStringTable
                        (rs.getString("category_totals"), PEC.instance().getCurrentUserPass()));
                if (totalSummary.getCatTotals().size()==0) totalSummary.setCatTotals(catTotalList);
                else {
                    for (int i=0; i< catTotalList.size(); i++) {
                        int index = totalSummary.returnCatIndex(catTotalList.get(i).getCategoryName());
                        if (index>-1) {
                            totalSummary.getCatTotals().get(index).incrementTotalPerPeriod(
                                    catTotalList.get(i).getTotalPerPeriod());
                        } else {
                            totalSummary.getCatTotals().add(new CatTotal(catTotalList.get(i).getCategoryName(),
                                            catTotalList.get(i).getTotalPerPeriod()));
                        }
                    }
                }
            }
            if (totalSummary.getCatTotals().size()>0) {
                totalSummary.setAccountNick(acctNick);
                totalSummary.setStart(totalSummary.getPeriodBegin().getTimeInMillis());
                totalSummary.setEnd(totalSummary.getPeriodEnd().getTimeInMillis());
                totalSummary.setPeriodDayAmount((int) TimeUnit.MILLISECONDS.toDays
                        (Math.abs(totalSummary.end - totalSummary.start)) + 1);
                for (int i = 0; i < totalSummary.getCatTotals().size(); i++) {
                    totalSummary.getCatTotals().get(i).calculatePercentageAndAverage
                            (totalSummary.totalExpense, totalSummary.totalIncome, totalSummary.periodDayAmount);
                }
                totalSummary.setTimeStamp(Calendar.getInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return totalSummary;
    }

    /**
     * Method returns an ArrayList of all start date/ end date pairs of periods in database
     * available for fetching, aggregating, and rendering a Summary of a chosen account
     * (all 3-month chunks in the database for the particular account).
     * @param acctNick account of interest
     * @return an ArrayList of all start date/ end date pairs in ascending order
     */
    public static ArrayList<Calendar[]> getAllAvailablePeriods(String acctNick) {
        ArrayList<Calendar[]> result = new ArrayList<Calendar[]>();
        ArrayList<Calendar> resultFrom = new ArrayList<Calendar>();
        ArrayList<Calendar> resultTo = new ArrayList<Calendar>();
        Calendar[] calendarPair;
        Connection connection = Connectivity.getConnection();
        String query = "SELECT begin_date, end_date FROM summary "
                + "WHERE user_id = ? AND account_nick = ?";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, PEC.instance().getCurrentUserID());
            stmt.setString(2, acctNick);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultFrom.add(Transaction.returnCalendarFromYYYYMMDD(rs.getString("begin_date")));
                resultTo.add(Transaction.returnCalendarFromYYYYMMDD(rs.getString("end_date")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // sort the two independent lists
        Collections.sort(resultFrom);
        Collections.sort(resultTo);
        // merge the two lists into one ArrayList of Calendar pairs;
        // their order will match, because periods don't overlap
        for (int i = 0; i < resultFrom.size(); i++) {
            calendarPair = new Calendar[2];
            calendarPair[0] = resultFrom.get(i);
            calendarPair[1] = resultTo.get(i);
            result.add(calendarPair);
        }
        return result;
    }

    /**
     * Returns the index under which it finds a specified Category in the CatTotal list.
     * @param categoryName Category name searched for
     * @return the index of the list where the Category is; returns -1 if not found
     */
    public int returnCatIndex(String categoryName) {
        for (int j = 0; j < catTotals.size(); j++) {
            if (categoryName.compareToIgnoreCase(catTotals.get(j).getCategoryName())==0)
                return j;
        }
        return -1;
    }

    public String getAccountNick() {
        return accountNick;
    }

    public void setAccountNick(String accountNick) {
        this.accountNick = accountNick;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void incrementTotalExpense(double totalExpense) {
        this.totalExpense += totalExpense;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void incrementTotalIncome(double totalIncome) {
        this.totalIncome += totalIncome;
    }

    public Calendar getPeriodBegin() {
        return periodBegin;
    }

    public void setPeriodBegin(Calendar periodBegin) {
        this.periodBegin = periodBegin;
    }

    public Calendar getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Calendar periodEnd) {
        this.periodEnd = periodEnd;
    }

    public ArrayList<CatTotal> getCatTotals() {
        return catTotals;
    }

    public void setCatTotals(ArrayList<CatTotal> catTotals) {
        this.catTotals = catTotals;
    }

    public long getStart() { return start; }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() { return end; }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getPeriodDayAmount() { return periodDayAmount; }

    public void setPeriodDayAmount(int periodDayAmount) {
        this.periodDayAmount = periodDayAmount;
    }

    public Calendar getTimeStamp() { return timeStamp; }

    public String getTimeStampString() {
        String year, month, day, hour, minute, second;
        year = Integer.toString(timeStamp.get(Calendar.YEAR));
//		the twelve months in Calendar range from 0-11
        month = Integer.toString(timeStamp.get(Calendar.MONTH) + 1);
        day = Integer.toString(timeStamp.get(Calendar.DATE));
        hour = Integer.toString(timeStamp.get(Calendar.HOUR_OF_DAY));
        minute = Integer.toString(timeStamp.get(Calendar.MINUTE));
        second = Integer.toString(timeStamp.get(Calendar.SECOND));
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
        return year+"/"+month+"/"+day+" at "+hour+":"+minute+":"+second;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toString() {
        String output = "";
        output = "Summary for "+getAccountNick()+" from period starting "+
                Transaction.returnYYYYMMDDFromCalendar(getPeriodBegin())+" and ending "+
                Transaction.returnYYYYMMDDFromCalendar(getPeriodEnd())+
                "\n(created on "+getTimeStampString()+").\n";
        output+="------------------------------------------------------------------------------------------------\n";
        output+="Total expenses are "+String.format("$%.2f", getTotalExpense()*(-1), "")+
                ", total income is "+String.format("$%.2f", getTotalIncome(), "")+"\n";
        output+="\nExpense and income breakdown:\n"+
                "--------------------------------------------------------------------\n";
        for (int i=0; i<getCatTotals().size(); i++) {
            output+=String.format("%-25s", getCatTotals().get(i).getCategoryName());
            output+=String.format("%12s", String.format("$%.2f", getCatTotals().get(i).getTotalPerPeriod(), ""));
            output+=String.format("%12s", String.format("$%.2f", getCatTotals().get(i).getAveragePerMonth(), ""));
            output+=String.format("%12s", String.format("%.2f%%", getCatTotals().get(i).getPercentagePerPeriod(), ""));
            output+="\n";
        }
        return output;
    }

    /*
    public static void main(String[] args) {
        Calendar date1 = Transaction.returnCalendarFromYYYYMMDD("2023/01/01");
        Calendar date2 = Transaction.returnCalendarFromYYYYMMDD("2023/12/31");
        long end = date2.getTimeInMillis();
        long start = date1.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) + 1;
        System.out.println(days);
    }
    */
}
