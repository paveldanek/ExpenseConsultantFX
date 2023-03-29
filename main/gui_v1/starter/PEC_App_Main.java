package gui_v1.starter;

import db_connectors.Connectivity;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;

import java.sql.SQLException;

public class PEC_App_Main {
    public static void main(String[] args) {
        if (Connectivity.aUserExists()) GUI_LogInWindow.getInstance().showLogInWindow();
        else GUI_SignUPWindow.getInstance().showSignUpWindow();
    }
}
