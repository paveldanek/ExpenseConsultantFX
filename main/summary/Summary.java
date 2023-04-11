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
import java.util.concurrent.TimeUnit;

public class Summary {

    private double totalExpense = 0.0;
    private double totalIncome = 0.0;
    private Calendar periodBegin = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private Calendar periodEnd = Transaction.returnCalendarFromYYYYMMDD(TransactionList.STR_DATE_MIN);
    private ArrayList<CatTotal> catTotals = new ArrayList<CatTotal>();
    private String accountNick = "";

    private long start = 0, end = 0, periodDayAmount = 0;

    public Summary(TransactionList list) {
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
        periodDayAmount = TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) + 1;
        for (int k = 0; k < catTotals.size(); k++) {
            catTotals.get(k).calculatePercentageAndAverage(totalExpense, totalIncome, periodDayAmount);
        }
    }

    public Summary(String acctNick, Calendar from) {
        Connection connection = Connectivity.getConnection();
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
                accountNick = acctNick;
                periodBegin = from;
                periodEnd = Transaction.returnCalendarFromYYYYMMDD(rs.getString("end_date"));
                totalExpense = Double.parseDouble(AESUtil.decryptItem(rs.getString("total_out")));
                totalIncome = Double.parseDouble(AESUtil.decryptItem(rs.getString("total_in")));
                result = rs.getString("category_totals");
                result = AESUtil.decryptStringTable(result, PEC.instance().getCurrentUserPass());
                catTotals = AESUtil.stringIntoCatTotals(result);
                start = periodBegin.getTimeInMillis();
                end = periodEnd.getTimeInMillis();
                periodDayAmount = TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) + 1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Summary() {
    }

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
            //System.out.println(AESUtil.encryptItem(String.valueOf(summary.getTotalExpense())));
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

    public void uploadCurrentSummary() {
        Connection connection = Connectivity.getConnection();
        String sql = "INSERT INTO summary (begin_date, end_date, total_out, total_in, "+
        "category_totals, account_nick, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement s = connection.prepareStatement(sql);
            s.setString(1, Transaction.returnYYYYMMDDFromCalendar(periodBegin));
            s.setString(2, Transaction.returnYYYYMMDDFromCalendar(periodEnd));
            s.setString(3, AESUtil.encryptItem(String.valueOf(totalExpense)));
            s.setString(4, AESUtil.encryptItem(String.valueOf(totalIncome)));
            s.setString(5, AESUtil.encryptStringTable(AESUtil.catTotalsIntoString(catTotals),
                    PEC.instance().getCurrentUserPass()));
            s.setString(6, PEC.instance().getActiveAccount());
            s.setInt(7, PEC.instance().getCurrentUserID());
            int rowsAffected = s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
                else if (totalSummary.getPeriodBegin().compareTo(bd)>0) totalSummary.setPeriodBegin(bd);
                if (totalSummary.getPeriodEnd().compareTo(ed)<0) totalSummary.setPeriodEnd(ed);
                totalSummary.setTotalExpense(totalSummary.getTotalExpense()+
                        Double.parseDouble(AESUtil.decryptItem(rs.getString("total_out"))));
                totalSummary.setTotalIncome(totalSummary.getTotalIncome()+
                        Double.parseDouble(AESUtil.decryptItem(rs.getString("total_in"))));
                catTotalList = AESUtil.stringIntoCatTotals(AESUtil.decryptStringTable
                        (rs.getString("category_totals"), PEC.instance().getCurrentUserPass()));
                if (totalSummary.getCatTotals().size()==0) totalSummary.setCatTotals(catTotalList);
                else {
                    for (int i=0; i< catTotalList.size(); i++) {
                        int index = totalSummary.returnCatIndex(catTotalList.get(i).getCategoryName());
                        if (index>-1) {
                            totalSummary.getCatTotals().get(index).setTotalPerPeriod
                                    (totalSummary.getCatTotals().get(index).getTotalPerPeriod()+
                                            catTotalList.get(i).getTotalPerPeriod());
                        } else {
                            totalSummary.getCatTotals().add(new CatTotal(catTotalList.get(i).getCategoryName(),
                                            catTotalList.get(i).getTotalPerPeriod()));
                        }
                    }
                }
            }
            totalSummary.setAccountNick(acctNick);
            totalSummary.setStart(totalSummary.getPeriodBegin().getTimeInMillis());
            totalSummary.setEnd(totalSummary.getPeriodEnd().getTimeInMillis());
            totalSummary.setPeriodDayAmount(TimeUnit.MILLISECONDS.toDays
                    (Math.abs(totalSummary.end - totalSummary.start)) + 1);
            for (int i=0; i< totalSummary.getCatTotals().size(); i++) {
                totalSummary.getCatTotals().get(i).calculatePercentageAndAverage
                        (totalSummary.totalExpense, totalSummary.totalIncome, totalSummary.periodDayAmount);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return totalSummary;
    }

    private int returnCatIndex(String categoryName) {
        for (int j = 0; j < catTotals.size(); j++) {
            if (categoryName.compareToIgnoreCase(catTotals.get(j).getCategoryName())==0)
                return j;
        }
        return -1;
    }

    private boolean currentUserHasAnyAccountSummary() {
        Connection connection = Connectivity.getConnection();
        String query = "SELECT account_nick FROM summary WHERE user_id = ?";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, PEC.instance().getCurrentUserID());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean currentUserHasSummaryForAccount(String acctNick) {
        Connection connection = Connectivity.getConnection();
        String query = "SELECT account_nick FROM summary WHERE user_id = ? AND account_nick = ?";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, PEC.instance().getCurrentUserID());
            stmt.setString(2, acctNick);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public double getTotalIncome() {
        return totalIncome;
    }

    public Calendar getPeriodBegin() {
        return periodBegin;
    }

    public Calendar getPeriodEnd() {
        return periodEnd;
    }

    public ArrayList<CatTotal> getCatTotals() {
        return catTotals;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setPeriodBegin(Calendar periodBegin) {
        this.periodBegin = periodBegin;
    }

    public void setPeriodEnd(Calendar periodEnd) {
        this.periodEnd = periodEnd;
    }

    public void setCatTotals(ArrayList<CatTotal> catTotals) {
        this.catTotals = catTotals;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getPeriodDayAmount() { return periodDayAmount; }

    public void setPeriodDayAmount(long periodDayAmount) {
        this.periodDayAmount = periodDayAmount;
    }

    public String toString() {
        String output = "";
        output = "Summary for "+getAccountNick()+" from period starting "+
                Transaction.returnYYYYMMDDFromCalendar(getPeriodBegin())+" and ending "+
                Transaction.returnYYYYMMDDFromCalendar(getPeriodEnd())+".\n";
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
