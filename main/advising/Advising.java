package advising;

import db_connectors.Connectivity;
import entities.Transaction;
import entities.TransactionList;
import main_logic.PEC;
import summary.Summary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the statistics based on two discrete periods
 * (3-month portions) of a single account: PAST period and LAST period.
 * Any analysis and advising is based on the statistical information
 * produced by this engine, with the help of its subsystem, CatStats class.
 * @author SPAM team: Pavel Danek and Samuel Dinka
 */
public class Advising {

    // THIS CLASS FOCUSES ON EXPENSES (SPENDING MONEY), NOT INCOMES (MAKING MONEY)

    // ALL METRICS AND THEIR CHANGES ARE BASED ON TWO SUMMARIES OF AN ACCOUNT;
    // THE SUMMARIES (THEIR TIME PERIODS) ARE CHOSEN BY THE CUSTOMER: IT IS
    // A GOOD IDEA FOR THE FIRST TIME PERIOD TO BE AS FAR BACK AS POSSIBLE
    // FOR GETTING A BIGGER PICTURE, OR AS CLOSE TO THE SECOND TIME PERIOD
    // AS POSSIBLE TO CHATCH MORE CURRENT CHANGES; THE SECOND ONE SHOULD BE
    // AS CURRENT AS POSSIBLE. THEY BOTH NEED TO HAVE A SPAN MORE THAN 1 MONTH.

    // We call the first time period PAST period, and second, more current, LAST.

    private double pastSumOfCatTotals = 0.0;
    private double pastSumOfCatPercentages = 0.0;
    private double lastSumOfCatTotals = 0.0;
    private double lastSumOfCatPercentages = 0.0;
    private double netSumOfCatPercentageChange = 0.0;

    private double pastNetIncome = 0.0;
    private double lastNetIncome = 0.0;
    // ADVISING BASED OFF OF THIS NET MONEY FLOW INTO
    // THE CUSTOMER'S ACCOUNT CHANGE BETWEEN PAST AND LAST PERIOD
    private double netIncomeChangeInPercent = 0.0;

    private String accountNick = "";
    private Calendar pastPeriodBegin = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private Calendar pastPeriodEnd = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private Calendar lastPeriodBegin = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private Calendar lastPeriodEnd = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);

    private int numberOfDaysBetweenEndingsOfPastAndLast = 0;

    private ArrayList<CatStats> catStats = new ArrayList<CatStats>();

    private Summary s1, s2;

    /**
     * Creates the Advising instance based on two Summaries of an account.
     * @param acctNick the account identifier
     * @param pastPeriod a Calendar value of the beginning date of period 1 (PAST)
     * @param lastPeriod a Calendar value of the beginning date of period 2 (LAST)
     */
    public Advising(String acctNick, Calendar pastPeriod, Calendar lastPeriod) {
        s1 = Summary.downloadSingleSummary(acctNick, pastPeriod);
        s2 = Summary.downloadSingleSummary(acctNick, lastPeriod);
        accountNick = acctNick;
        pastPeriodBegin = s1.getPeriodBegin();
        pastPeriodEnd = s1.getPeriodEnd();
        lastPeriodBegin = s2.getPeriodBegin();
        lastPeriodEnd = s2.getPeriodEnd();
        long end1 = s1.getPeriodEnd().getTimeInMillis();
        long end2 = s2.getPeriodEnd().getTimeInMillis();
        numberOfDaysBetweenEndingsOfPastAndLast =
                (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end2 - end1)) + 1;
        int indexInTheOther = 0;
        // depending on which list of Categories is longer (P1 or P2) this compiles
        // a new list containing only Categories present in BOTH periods (P1 and P2)
        if (s1.getCatTotals().size() < s2.getCatTotals().size()) {
            for (int i = 0; i < s1.getCatTotals().size(); i++) {
                indexInTheOther = s2.returnCatIndex(s1.getCatTotals().get(i).getCategoryName());
                if (indexInTheOther != -1 && s1.getCatTotals().get(i).getTotalPerPeriod()<0.0 &&
                        s2.getCatTotals().get(indexInTheOther).getTotalPerPeriod()<0.0) {
                    CatStats c = new CatStats();
                    c.setCategoryName(s1.getCatTotals().get(i).getCategoryName());
                    c.setPastTotalPerPeriod(s1.getCatTotals().get(i).getTotalPerPeriod());
                    c.setLastTotalPerPeriod(s2.getCatTotals().get(indexInTheOther).getTotalPerPeriod());
                    catStats.add(c);
                }
            }
        } else {
            for (int i = 0; i < s2.getCatTotals().size(); i++) {
                indexInTheOther = s1.returnCatIndex(s2.getCatTotals().get(i).getCategoryName());
                if (indexInTheOther != -1 && s2.getCatTotals().get(i).getTotalPerPeriod()<0.0 &&
                        s1.getCatTotals().get(indexInTheOther).getTotalPerPeriod()<0.0) {
                    CatStats c = new CatStats();
                    c.setCategoryName(s2.getCatTotals().get(i).getCategoryName());
                    c.setPastTotalPerPeriod(s1.getCatTotals().get(indexInTheOther).getTotalPerPeriod());
                    c.setLastTotalPerPeriod(s2.getCatTotals().get(i).getTotalPerPeriod());
                    catStats.add(c);
                }
            }
        }
        // this calculates all other values, if there were some common Categories available
        if (catStats.size() > 0) {
            for (int i = 0; i < catStats.size(); i++) {
                catStats.get(i).calculateCategoryStats(s1.getTotalExpense(), s2.getTotalExpense(),
                        numberOfDaysBetweenEndingsOfPastAndLast);
                pastSumOfCatTotals += catStats.get(i).getPastTotalPerPeriod();
                lastSumOfCatTotals += catStats.get(i).getLastTotalPerPeriod();
                pastSumOfCatPercentages += catStats.get(i).getPastPercentagePerPeriod();
                lastSumOfCatPercentages += catStats.get(i).getLastPercentagePerPeriod();
            }
            netSumOfCatPercentageChange = lastSumOfCatPercentages - pastSumOfCatPercentages;
            catStats = mergeSortByNetChange(catStats);
        }
        pastNetIncome = s1.getTotalIncome() + s1.getTotalExpense(); //totalExpense is a negative number
        lastNetIncome = s2.getTotalIncome() + s2.getTotalExpense();
        if (lastNetIncome!=0.0) netIncomeChangeInPercent =
                ((lastNetIncome - pastNetIncome) / Math.abs(pastNetIncome)) * 100;
    }

    /**
     * Method returns an ArrayList of all start date/ end date pairs of periods in database
     * available for fetching, and suitable for creating an instance of Advising for a chosen
     * account (all 3-month chunks in the database for the particular account, IF their time
     * span is larger than 1 month). There have to be at least two such time periods, otherwise
     * the method returns NULL.
     * @param acctNick account of interest
     * @return an ArrayList of all start date/ end date pairs (in ascending order) spanning at least
     * 1 month. If the ArrayList's length is less than 2, NULL will be returned.
     */
    public static ArrayList<Calendar[]> getAllAvailablePeriodsForAdvising(String acctNick) {
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
                Calendar begin = Transaction.returnCalendarFromYYYYMMDD(rs.getString("begin_date"));
                Calendar end = Transaction.returnCalendarFromYYYYMMDD(rs.getString("end_date"));
                // this following gimmick is used to transfer the Calendar's value, but not the Object itself
                Calendar oneMonthFromBeginning = Transaction.
                        returnCalendarFromYYYYMMDD(Transaction.returnYYYYMMDDFromCalendar(begin));
                oneMonthFromBeginning.add(Calendar.MONTH, 1);
                if (end.compareTo(oneMonthFromBeginning) >= 0) {
                    resultFrom.add(begin);
                    resultTo.add(end);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // if there aren't at least two time periods to create an istance of Advising,
        // do not return any (return NULL)
        if (resultFrom.size() < 2) return null;
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

    // ----------------- helper sorting algorithm for CatStats (sorting by netPercentagePerPeriodChange) ----------

    private static ArrayList<CatStats> mergeByNetChange(ArrayList<CatStats> list1, ArrayList<CatStats> list2) {
        ArrayList<CatStats> result = new ArrayList<CatStats>();
        while (list1.size() > 0 && list2.size() > 0) {
            if (list1.get(0).getNetPercentagePerPeriodChange()>list2.get(0).getNetPercentagePerPeriodChange()) {
                result.add(list2.get(0));
                list2.remove(0);
            } else {
                result.add(list1.get(0));
                list1.remove(0);
            }
        }
        // at this point list1 or list2 is empty
        while (list1.size() > 0) {
            result.add(list1.get(0));
            list1.remove(0);
        }
        while (list2.size() > 0) {
            result.add(list2.get(0));
            list2.remove(0);
        }
        return result;
    }

    public static ArrayList<CatStats> mergeSortByNetChange(ArrayList<CatStats> list) {
        ArrayList<CatStats> list1 = new ArrayList<CatStats>();
        ArrayList<CatStats> list2 = new ArrayList<CatStats>();
        int n = list.size();
        if (n == 0)
            return null;
        if (n == 1)
            return list;
        n = n / 2;
        for (int i = 0; i < n; i++) {
            list1.add(list.get(i));
        }
        for (int i = n; i < list.size(); i++) {
            list2.add(list.get(i));
        }
        list1 = mergeSortByNetChange(list1);
        list2 = mergeSortByNetChange(list2);
        return mergeByNetChange(list1, list2);
    }

    // ------------------------------------------------------------------------------------------------------------

    public double getPastSumOfCatTotals() {
        return pastSumOfCatTotals;
    }

    public double getPastSumOfCatPercentages() {
        return pastSumOfCatPercentages;
    }

    public double getLastSumOfCatTotals() {
        return lastSumOfCatTotals;
    }

    public double getLastSumOfCatPercentages() {
        return lastSumOfCatPercentages;
    }

    public double getNetSumOfCatPercentageChange() {
        return netSumOfCatPercentageChange;
    }

    public double getPastNetIncome() {
        return pastNetIncome;
    }

    public double getLastNetIncome() {
        return lastNetIncome;
    }

    public double getNetIncomeChangeInPercent() {
        return netIncomeChangeInPercent;
    }

    public int getNumberOfDaysBetweenEndingsOfPastAndLast() {
        return numberOfDaysBetweenEndingsOfPastAndLast;
    }

    public String getAccountNick() {
        return accountNick;
    }

    public Calendar getPastPeriodBegin() {
        return pastPeriodBegin;
    }

    public Calendar getPastPeriodEnd() {
        return pastPeriodEnd;
    }

    public Calendar getLastPeriodBegin() {
        return lastPeriodBegin;
    }

    public Calendar getLastPeriodEnd() {
        return lastPeriodEnd;
    }

    public ArrayList<CatStats> getCatStats() {
        return catStats;
    }

    public String toString() {
        String output = "";
        output = "Analysis of " + getAccountNick() + " EXPENSES based on change between periods (1):" +
                Transaction.returnYYYYMMDDFromCalendar(getPastPeriodBegin()) + "-" +
                Transaction.returnYYYYMMDDFromCalendar(getPastPeriodEnd()) + " and (2):" +
                Transaction.returnYYYYMMDDFromCalendar(getLastPeriodBegin()) + "-" +
                Transaction.returnYYYYMMDDFromCalendar(getLastPeriodEnd());
        output += "\n------------------------------------------------------------------"+
                "------------------------------------------------------------------------\n";
        output += String.format("%-25s", "CATEGORY");
        output += String.format("%12s", "SUMS 1");
        output += String.format("%12s", "%ofSPEND 1");
        output += String.format("%12s", "SUMS 2");
        output += String.format("%12s", "%ofSPEND 2");
        output += String.format("%12s", "% CHANGE");
        output += "  |  ";
        output += String.format("%24s", "SUMS+% CHANGE 3mo.");
        output += String.format("%24s", "SUMS+% CHANGE 6mo.");
        output += String.format("%24s", "SUMS+% CHANGE 1yr.");
        output += String.format("%24s", "SUMS+% CHANGE 2yrs.");
        output += String.format("%24s", "SUMS+% CHANGE 5yrs.");
        output += String.format("%24s", "SUMS+% CHANGE 10yrs.");
        output += "\n------------------------------------------------------------------"+
                "------------------------------------------------------------------------\n";
        for (int i = 0; i < getCatStats().size(); i++) {
            output += String.format("%-25s", getCatStats().get(i).getCategoryName());
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getPastTotalPerPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getPastPercentagePerPeriod(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getLastTotalPerPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getLastPercentagePerPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getNetPercentagePerPeriodChange(), ""));
            output += "  |  ";
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getM3TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getM3NetPercentageChange(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getM6TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getM6NetPercentageChange(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getY1TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getY1NetPercentageChange(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getY2TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getY2NetPercentageChange(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getY5TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getY5NetPercentageChange(), ""));
            output += String.format("%12s", String.format("$%.2f", getCatStats().get(i).getY10TotalperPeriod(), ""));
            output += String.format("%12s", String.format("%.2f%%", getCatStats().get(i).getY10NetPercentageChange(), ""));
            output += "\n";
        }
        if (getCatStats().size()==0) output += "No EXPENSE categories appearing in BOTH periods to display...\n";
        output += "------------------------------------------------------------------"+
                "------------------------------------------------------------------------\n";
        output += String.format("%-25s", "TOTAL");
        output += String.format("%12s", String.format("$%.2f", getPastSumOfCatTotals(), ""));
        output += String.format("%12s", String.format("%.2f%%", getPastSumOfCatPercentages(), ""));
        output += String.format("%12s", String.format("$%.2f", getLastSumOfCatTotals(), ""));
        output += String.format("%12s", String.format("%.2f%%", getLastSumOfCatPercentages(), ""));
        output += String.format("%12s", String.format("%.2f%%", getNetSumOfCatPercentageChange(), ""));
        output += "\n------------------------------------------------------------------"+
                "------------------------------------------------------------------------\n";
        output += "\nYour net income in the period (1) was " + String.format("$%.2f", getPastNetIncome(), "") +
                ", and in the period (2) was " + String.format("$%.2f", getLastNetIncome(), "") + ",\nwhich is ";
        if (getNetIncomeChangeInPercent() < 0.0) output += "a decrease of ";
        else output += "an increase of ";
        output += String.format("%.2f%%", getNetIncomeChangeInPercent(), "") + ". ";
        if (getNetIncomeChangeInPercent() < 0.0) {
            output += "WATCH OUT!!! LOWER YOUR SPENDINGS!!!\n";
            if (getCatStats().size()>0 && getCatStats().get(getCatStats().size()-1).getNetPercentagePerPeriodChange()>=5.0)
                output += "You NEED to DECREASE your spending mainly in categories:\n";
        }
        else {
            output += "GOOD JOB!!! YOU'RE ABLE TO SAVE UP!!!\n";
            if (getCatStats().size()>0 && getCatStats().get(getCatStats().size()-1).getNetPercentagePerPeriodChange()>=5.0)
                output += "You could consider lowering your spending in categories like:\n";
        }
        for (int i = getCatStats().size()-1; i>=0; i--) {
            if (getCatStats().get(i).getNetPercentagePerPeriodChange()>=5.0)
                output += "- "+getCatStats().get(i).getCategoryName()+"\n";
            else break;
        }
        return output;
    }

}