package gui_v1.mainWindows;
import gui_v1.action_processors.GUI_MainiWindowActionProcessor;
import gui_v1.action_processors.MenuActionProgrammableHandle;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

public interface GUI_MainWidowsSharedBehaviors {


    WindowListener mainW =  new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            MenuActionProgrammableHandle ac = new MenuActionProgrammableHandle();
            ac.dologOutProcessing();
            /*
            int answr = JOptionPane.showOptionDialog(null, "Do you want to terminate run of Consultant", "Cation System termination",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if(answr == JOptionPane.YES_OPTION){
                new GUI_MainiWindowActionProcessor();
//                System.exit(JFrame.EXIT_ON_CLOSE);
            }
             */
        }
    };
    WindowListener w = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            /*
            int answr = JOptionPane.showOptionDialog(null, "Do you Really want to close this window and return to Manual Entry?", "This window will be close",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if(answr == JOptionPane.YES_OPTION){
                GUI_NewAccountWindow.getInstance().disposeNewAccntWindow();
                GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
            }
             */
            Request r = Request.instance();
            r.getNewAccountWindowHolder().clearFields();
            GUI_NewAccountWindow.getInstance().disposeNewAccntWindow();
            GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
        }
    };

   WindowListener w2 = new WindowAdapter() {
       @Override
       public void windowClosing(WindowEvent e) {
           /*
           int answr = JOptionPane.showOptionDialog(null, "Do you Really want to close this window, and return to main?", "This window will be close.",
                   JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
           if(answr == JOptionPane.YES_OPTION){
               GUI_ManualEntryWindow.getInstance().disposeManualEntryWindow();
               GUI_RecordsWindow.getInstance().showRecordsWindow();
           }
            */
           int answr = JOptionPane.showOptionDialog(null, "Do you want to cancel Manual Entry\n and discard all input entries?", "Warning",
                   JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
           if (answr==JOptionPane.YES_OPTION) {
               PEC.instance().clearManualEntries();
               Request r = Request.instance();
               r.getManualEntryWindowHolder().clearFields();
               GUI_ManualEntryWindow.getInstance().disposeManualEntryWindow();
               GUI_RecordsWindow.getInstance().showRecordsWindow();
           }
       }
   };
    WindowListener w3 = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            /*
            int answr = JOptionPane.showOptionDialog(null, "Do you Really want to close this window, and return to New Account Window?", "This window will be close.",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if(answr == JOptionPane.YES_OPTION){
                GUI_NewBankWindow.getInstance().disposeNewBankWindow();
                GUI_NewAccountWindow.getInstance().showNewAccntWindow();
            }
             */
            Request r = Request.instance();
            r.getNewBankWindowHolder().clearField();
            GUI_NewBankWindow.getInstance().disposeNewBankWindow();
            GUI_NewAccountWindow.getInstance().showNewAccntWindow();
        }
    };
    WindowListener w4 = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            /*
            int answr = JOptionPane.showOptionDialog(null, "Do you Really want to close this window, and return to Manual Entry?", "This window will be close.",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if(answr == JOptionPane.YES_OPTION){
                GUI_NewCategoryWindow.getInstance().disposeNewCategoryWindow();
                GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
            }
             */
            Request r = Request.instance();
            r.getNewCategoryWindowHolder().clearField();
            GUI_NewCategoryWindow.getInstance().disposeNewCategoryWindow();
            GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
        }
    };
    WindowListener w5 = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            /*
            int answr = JOptionPane.showOptionDialog(null, "Do you Really want to close this window, and return to main?", "This window will be close.",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if(answr == JOptionPane.YES_OPTION){
                GUI_NewCategoryWindow.getInstance().disposeNewCategoryWindow();
                GUI_RecordsWindow.getInstance().showRecordsWindow();
            }
             */
            Request r = Request.instance();
            r.getNewCategoryWindowHolder().clearField();
            GUI_NewCategoryWindow.getInstance().disposeNewCategoryWindow();
            GUI_RecordsWindow.getInstance().showRecordsWindow();
        }
    };
    WindowListener w6 = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            GUI_PasswordRetrievalWindow.getInstance().disposePasswordRetrievalWindow();
            GUI_LogInWindow.getInstance().showLogInWindow();
        }
    };
}


