package gui_v1.gui_logic;
import gui_v1.help_utils.GUI_Routines;
import java.util.*;

public class GUI_ManualEntryTemporaialHolder implements GUI_Routines {
    private static GUI_ManualEntryTemporaialHolder instance = null;
    public enum ENTRY_PREVIW {NO_CYCLES};
    private static LinkedList<String[]> accounts;
    private static int currEntryIndex;

    private static LinkedList<String>  unsavedBanksList;
    private static LinkedList<String>  unsavedCategoriesList;
    private static LinkedList<String>  unsavedAcctNickskList;

    public  boolean isBankInUnsavedList(String bName) {
        return isTextInList(bName,  unsavedBanksList);
    }

    public  void addBankAsUnstored(String bName) {
        if(!isBankInUnsavedList(bName)){
            unsavedBanksList.addLast(bName);
        }
    }
    public  boolean isNickInUnsavedList(String aNick) {
        return isTextInList(aNick,  unsavedAcctNickskList);
    }

    public  void addAcctNickAsUnstored(String aNick) {
        if(!isNickInUnsavedList(aNick)){
            unsavedAcctNickskList.addLast(aNick);
        }
    }
    public  boolean isCategoryInUnsavedList(String cName) {
        return isTextInList(cName,  unsavedCategoriesList);
    }

    public  void addCategoryAsUnstored(String cName) {
        if(!isCategoryInUnsavedList(cName)){
            unsavedCategoriesList.addLast(cName);
        }
    }


    private GUI_ManualEntryTemporaialHolder(){
        initiate();
    }
    private static void initiate(){
        accounts= new LinkedList<String[]>();
        currEntryIndex = 0;
        unsavedBanksList= new LinkedList<String>();
        unsavedAcctNickskList= new LinkedList<String>();
        unsavedCategoriesList= new LinkedList<String>();
    }

    public static GUI_ManualEntryTemporaialHolder getInstance() {
        if (instance == null) {
           instance = new GUI_ManualEntryTemporaialHolder();
        }
        return instance;
    }
    public int getNumOfItems(){
        return accounts.size();
    }
    public void addTempUserManualEntry(String[] currEntry){
        if(currEntry == null){
            return;
        }
        if(isThisEntryExists(currEntry)){
            return;
        }
        accounts.addLast(trimAllStr(new String[]{currEntry[0], currEntry[1], currEntry[2],
                currEntry[3], currEntry[4], currEntry[5], currEntry[6]}));
        currEntryIndex = accounts.size()-1;
    }

    public void addTempUserManualEntry(String account, String date, String refN,String descr, String memo,
                                       String amount, String category){
        addTempUserManualEntry(crateStrArr(account,  date,  refN, descr,  memo, amount,  category));
    }

    public void clearTemporalManualEntryList(){
        initiate();
    }

    public boolean isThisEntryExists(String[] itemToCheck){
        if(accounts.size()==0){
            return false;
        }
        itemToCheck = trimAllStr(itemToCheck);
        boolean check = false;
        for(String[] singleItm: accounts){
            if(singleItm[0].compareToIgnoreCase(itemToCheck[0])==0){
                check = false;
                for(int i=0; i< singleItm.length; i++){
                    if(singleItm[i].compareToIgnoreCase(itemToCheck[i])!=0){
                        check = true;
                    }
                }
                if(!check){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isStrArrsIdentical(String[] sArr1, String[] sAr2){
        if(sArr1.length != sAr2.length){
            return false;
        }
        for(int i=0; i< sArr1.length; i++){
            System.out.println(sArr1[i] + " ==  "+sAr2[i]);
            if(!(sArr1[i].compareTo(sAr2[i])==0)){
                return false;
            }
        }
        return true;
    }

    public boolean isThisEntryExists(String account, String date, String refN,String descr, String memo,
                                     String amount, String category){
        return isThisEntryExists(crateStrArr(account,  date,  refN, descr,  memo, amount,  category));
    }

    private String[] crateStrArr(String account, String date, String refN, String descr, String memo,
                                 String amount, String category){

        return trimAllStr(new String[]{ account,  date,  refN, descr,  memo, amount,  category});
    }


    public String[] getFirst() {
        if(accounts!=null && accounts.size()>0){
            currEntryIndex = 0;
            return accounts.get(currEntryIndex);
        }
        return null;
    }
    public String[] getPrevNoCycles() {
        return getPrev(ENTRY_PREVIW.NO_CYCLES);
    }
    public String[] getNextNoCycles() {
        return getNext(ENTRY_PREVIW.NO_CYCLES);
    }
    public String[] getPrev(ENTRY_PREVIW cyclic) {
        if(accounts==null || accounts.size() == 0 ||(  cyclic==ENTRY_PREVIW.NO_CYCLES && currEntryIndex==0 )) {
            return null;
        }else{
            if(currEntryIndex==0){
                currEntryIndex = accounts.size()-1;
            }else{
                currEntryIndex--;
            }
            return accounts.get(currEntryIndex);
        }
    }


    public String[] getNext(ENTRY_PREVIW cyclic) {
        if((accounts==null || accounts.size() == 0) || ((cyclic==ENTRY_PREVIW.NO_CYCLES) && (currEntryIndex == accounts.size() -1))) {
            return null;
        }else{
            if (currEntryIndex == (accounts.size() - 1)) {
                currEntryIndex = 0;
            } else {
                currEntryIndex++;
            }

            return accounts.get(currEntryIndex);
        }
    }
    public String[] getLast() {
        if(accounts!=null && accounts.size()>0){
            currEntryIndex = accounts.size()-1;
            return accounts.get(currEntryIndex);
        }
        return null;
    }

    public LinkedList<String[]> getManuallYEnterredAccounts() {
        return accounts;
    }
}
