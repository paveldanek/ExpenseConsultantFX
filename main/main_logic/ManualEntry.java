package main_logic;

import entities.Transaction;

import java.util.Calendar;

/**
 * A class that represents a temporary Manual Entry Array for storing Transactions
 * entered by hand, before they are saved and merged into the main Transaction List.
 * @author SPAM team: Pavel Danek and Samuel Dinka
 */
public class ManualEntry {

    private static String[][] manualEntries;
    private static ManualEntry singleton = null;

    // this class is a singleton
    private ManualEntry() {
        manualEntries = new String[0][7];
    }

    public static ManualEntry instance() {
        if (singleton==null) singleton = new ManualEntry();
        return singleton;
    }

    /**
     * Clears all previous temporary Transactions by creating a new temporary
     * storage array.
     */
    public static void clearManualEntries() {
        manualEntries = new String[0][7];
    }

    /**
     * Adds a new, "empty" entry at the end of the array.
     * @param acctNick pre-filled account nickname
     * @param date pre-filled date (as Calendar value)
     * @param category pre-filled Category
     */
    public static void addManualEntry(String acctNick, Calendar date, String category) {
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

    /**
     * Fills out an existing manual entry using Request variable for field details.
     * @param position the position of the entry in the array
     * @param r holds all fields to be used
     */
    public static void editManualEntry(int position, Request r) {
        if (position<0 || position>=manualEntries.length) return;
        manualEntries[position][0] = r.getAccountNick();
        manualEntries[position][1] = r.getTDate();
        manualEntries[position][2] = r.getTRef();
        manualEntries[position][3] = r.getTDesc();
        manualEntries[position][4] = r.getTMemo();
        manualEntries[position][5] = String.valueOf(r.getTAmount());
        manualEntries[position][6] = r.getTCat();
    }

    /**
     * Deletes an existing manual entry from the array.
     * @param position the position of the entry in the array
     */
    public static void deleteManualEntry(int position) {
        if (position<0 || position>=manualEntries.length) return;
        if (manualEntries.length==0) return;
        String [][] temp = new String[manualEntries.length-1][7];
        System.arraycopy(manualEntries, 0, temp, 0, position);
        System.arraycopy(manualEntries, position+1, temp, position, manualEntries.length-position-1);
        manualEntries = temp;
    }

    /**
     * Reads and gets a specific manual entry from the array.
     * @param position the position of the entry in the array
     * @return Result variable containing all fields of the entry
     */
    public static Result getManualEntry(int position) {
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

    /**
     * Returns the size of the temporary manual entry array.
     */
    public static int getManualEntrySize() {
        return manualEntries.length;
    }

    /**
     * Switches the account all manual entries in this session will be placed under.
     * @param accountNick the desired account identifier
     */
    public void changeManualEntryAccount(String accountNick) {
        for (int i = 0; i < manualEntries.length; i++) {
            manualEntries[i][0] = accountNick;
        }
    }

    // ------------for communication with main PEC Class ---------------------

    /**
     * Returns the entire temporary manual entry array for further manipulation.
     */
    public static String[][] getArray() {
        return manualEntries;
    }

}
