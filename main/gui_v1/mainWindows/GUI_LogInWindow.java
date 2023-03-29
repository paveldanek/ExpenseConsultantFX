package gui_v1.mainWindows;

import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.loginSigninWElements.GUI_LogInP;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import javax.swing.*;
import java.awt.*;


public class GUI_LogInWindow extends JFrame  implements GUI_LoginSignUpWiindows_Settings {
    private static GUI_LogInWindow instance = null;
    private GUI_LogInWindow() {

        setTitle(strLogInWindowTitle);
        setSize(logInFFrameSize);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new GUI_LogInP(), BorderLayout.CENTER);
        //add(GUI_ElementCreator.newCopyRightsLabel(strCopyRigts), BorderLayout.SOUTH);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        setVisible(true);
    }
    public static GUI_LogInWindow getInstance(){
        if(instance==null){
            instance = new GUI_LogInWindow();
        }
        return instance;
    }
    public void showLogInWindow(){
        instance.setVisible(true);
    }
    public void hideLogInWindow(){
        instance.setVisible(false);
    }
    public void disposeLogInWindow(){
        instance.dispose();
        instance = null;
    }

}
