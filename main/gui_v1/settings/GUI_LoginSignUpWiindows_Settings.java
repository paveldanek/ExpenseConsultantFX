package gui_v1.settings;

import java.awt.*;

public interface GUI_LoginSignUpWiindows_Settings {
    String strCopyRigts = "P.E.C., Copyright \u00a9 SPAM Team 2023";

    String strLogInWindowTitle = "Welcome to Personal Expense Consultant - Login";
    String strLogInHeadTitle = "Please Enter Your Login Info";

    String strSignUpWindowTitle = "Welcome to Personal Expense Consultant - Create an account";
    String StrSignUpHeadTilte = "Please Enter Your Info";

    int gui_width = 550;
    int gui_height = 350;
    Dimension logInFFrameSize = new Dimension(gui_width, gui_height);
    Dimension signUpWindowSize = new Dimension(gui_width, gui_height);



}