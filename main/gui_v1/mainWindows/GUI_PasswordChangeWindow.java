package gui_v1.mainWindows;

import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.mainWindows.loginSigninWElements.GUI_PasswordChangeP;

import javax.swing.*;
import java.awt.*;

public class GUI_PasswordChangeWindow extends JFrame implements GUI_MainWidowsSharedBehaviors,
        GUI_LoginSignUpWiindows_Settings {
    private static GUI_PasswordChangeWindow instance=null;

    private GUI_PasswordChangeWindow(){
        setTitle(strSignUpWindowTitle);
        setSize(signUpWindowSize);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
//        add(new  GUI_SignUpP(), BorderLayout.CENTER);
        add(new GUI_PasswordChangeP(), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w7);
    }
    public static GUI_PasswordChangeWindow getInstance(){
        if(instance==null){
            instance = new GUI_PasswordChangeWindow();
        }
        return instance;
    }
    public void showPasswordChangeWindow(){
        instance.setVisible(true);
    }
    public void hidePasswordChangeWindow(){
        instance.setVisible(false);
    }
    public void disposePasswordChangeWindow(){
        instance.dispose();
        instance = null;
    }
}
