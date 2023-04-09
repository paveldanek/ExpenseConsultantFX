package gui_v1.mainWindows;

import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.mainWindows.loginSigninWElements.GUI_PasswordRetrievalP;

import javax.swing.*;
import java.awt.*;

public class GUI_PasswordRetrievalWindow extends JFrame implements GUI_MainWidowsSharedBehaviors,
        GUI_LoginSignUpWiindows_Settings {
    private static GUI_PasswordRetrievalWindow instance=null;

    private GUI_PasswordRetrievalWindow(){
        setTitle(strSignUpWindowTitle);
        setSize(signUpWindowSize);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
//        add(new  GUI_SignUpP(), BorderLayout.CENTER);
        add(new GUI_PasswordRetrievalP(), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w6);
    }
    public static GUI_PasswordRetrievalWindow getInstance(){
        if(instance==null){
            instance = new GUI_PasswordRetrievalWindow();
        }
        return instance;
    }
    public void showPasswordRetrievalWindow(){
        instance.setVisible(true);
    }
    public void hidePasswordRetrievalWindow(){
        instance.setVisible(false);
    }
    public void disposePasswordRetrievalWindow(){
        instance.dispose();
        instance = null;
    }
}
