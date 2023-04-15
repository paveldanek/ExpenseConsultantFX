package gui_v1.action_processors;
import entities.Transaction;
import entities.TransactionList;
import gui_v1.mainWindows.*;
import gui_v1.mainWindows.recordsWElements.RecordsTable;
import main_logic.PEC;
import main_logic.Request;
import main_logic.Result;
import javax.swing.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ListIterator;

//import static gui_v1.settings.GUI_Static_Settings.pathToFile;


public class MenuActionProgrammableHandle {


    void doHowToStartProcessing(){
        GUI_HowToWindow.getInstance().showHowToWindow();
    }

    /**
     *  this method is
     *  getting file of user choose and
     *  stronger it in File named chosenFile
     *  for future processing..
     *  Code in method is just example of use, how to get file from user, and
     *  can be used anywhere.
     */
    public  void doParsOFXFileProcessing(){
      //  GUI_RecordsFrame records = new GUI_RecordsFrame();
        GUI_RecordsWindow.getInstance().showRecordsWindow();
        Request request = Request.instance();
        File chosenFile= GUI_FileChooser.getFileOrDirectory();
        if(chosenFile == null ){
            JOptionPane.showMessageDialog(null, "File not selected.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        } else{
            request.reset();
            ListIterator<Result> it;
            request.setFileWithPath(chosenFile.getAbsolutePath());
            //For limiting the time window for parsed Transactions.
            //It should be set to: min.date - min.date
            request.setFrom(Transaction.returnCalendarFromOFX(TransactionList.STR_DATE_MIN));
            request.setTo(Transaction.returnCalendarFromOFX(TransactionList.STR_DATE_MIN));
            it = PEC.instance().parseOFX(request);
            Result result = new Result();
            if (it.hasNext()) result = it.next();
            if (result.getCode()==Result.Code.WRONG_FILE ||
                    result.getCode()==Result.Code.IO_ERROR) {
                JOptionPane.showMessageDialog(null,
                        "The file is not OFX/QFX\nfile or could NOT be read.",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
            } else if (result.getCode()==Result.Code.NO_ITEMS_TO_READ) {
                JOptionPane.showMessageDialog(null,
                        "The file doesn't contain\nany new account activity\nthat could be added.",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
            } else {
                RecordsTable.clearTable();
                RecordsTable.addRowToTable(result.getTDate(),
                        result.getTRef(), result.getTDesc(),
                        result.getTMemo(), result.getTAmount(), result.getTCat());
            }
            while(it.hasNext()){
                result = it.next();
                RecordsTable.addRowToTable(result.getTDate(),
                        result.getTRef(), result.getTDesc(),
                        result.getTMemo(), result.getTAmount(), result.getTCat());
               }
        }
        //updateMenus(PEC.instance().getActiveAccount(), PEC.instance().getActiveCategory());
//        records.setVisible(true);
        request.getMainWindowHolder().updateRecordWindowAcctMenu(PEC.instance().getActiveAccount());
        GUI_RecordsWindow.getInstance().showRecordsWindow();
    }

    public void out(Object o){
        System.out.println(o+"");

    }

    void doAdvisingProcessing(){

    }
    void doManualEntryProcessing(){
        if (PEC.instance().getActiveAccount().length()==0) {
            GUI_NewAccountWindow.getInstance().showNewAccntWindow();
        } else {
            GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
        }
    }
    void doGenerateSummaryProcessing(){

    }
    void doChangePasswordProcessing(){
        GUI_PasswordChangeWindow.getInstance().showPasswordChangeWindow();
    }

    void doCloseAccountProcessing(){ GUI_CloseAccountWindow.getInstance().showCloseAccountWindow(); }

    public void dologOutProcessing() {
        JOptionPane.showMessageDialog(null, "You're about to log out and end" +
                        "\nthe program. When you do, your work\nwill be saved in the database.",
                "Warning", JOptionPane.INFORMATION_MESSAGE);
        try {
            PEC.instance().uploadCurrentList();
            PEC.instance().addCategoriesForUserToDB();
            System.exit(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

