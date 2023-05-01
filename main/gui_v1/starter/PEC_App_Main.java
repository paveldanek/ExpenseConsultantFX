package gui_v1.starter;

import db_connectors.Connectivity;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;
import main_logic.PEC;

/**
 * The entry point of the Personal Expense Consultant application.
 * Run THIS to start the app.
 */
public class PEC_App_Main {
    public static void main(String[] args) {
        PEC.instance().processArgs(args);
        if (Connectivity.anyUserExists()) GUI_LogInWindow.getInstance().showLogInWindow();
        else GUI_SignUPWindow.getInstance().showSignUpWindow();
    }
}
