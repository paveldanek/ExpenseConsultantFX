package gui_v1.mainWindows;

import gui_v1.mainWindows.loginSigninWElements.GUI_CloseAccountP;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;

import javax.swing.*;
import java.awt.*;

public class GUI_CloseAccountWindow extends JFrame implements GUI_MainWidowsSharedBehaviors,
        GUI_LoginSignUpWiindows_Settings {
    private static GUI_CloseAccountWindow instance=null;

    private GUI_CloseAccountWindow(){
        setTitle(strSignUpWindowTitle);
        setSize(signUpWindowSize);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
//        add(new  GUI_SignUpP(), BorderLayout.CENTER);
        add(new GUI_CloseAccountP(), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w8);
    }
    public static GUI_CloseAccountWindow getInstance(){
        if(instance==null){
            instance = new GUI_CloseAccountWindow();
        }
        return instance;
    }
    public void showCloseAccountWindow(){
        instance.setVisible(true);
    }
    public void hideCloseAccountWindow(){
        instance.setVisible(false);
    }
    public void disposeCloseAccountWindow(){
        instance.dispose();
        instance = null;
    }
}
