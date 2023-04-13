package gui_v1.mainWindows.loginSigninWElements;

import authentication.Authentication;
import crypto.AESUtil;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_PasswordChangeWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class GUI_PasswordChangeP extends JPanel implements GUI_LoginSignUpWiindows_Settings, ActionListener {
    private JTextField jtfEmail;
    private JPasswordField jtfPassOld;
    private JPasswordField jtfPass1;
    private JPasswordField jtfPass2;

    public GUI_PasswordChangeP() {
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Password Change"), BorderLayout.NORTH);

        JPanel inputBoxP = new JPanel();
        inputBoxP.setLayout(new GridLayout(4, 2));

        jtfEmail = GUI_ElementCreator.newTextField("");
        jtfEmail.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Enter Your Email:"));
        inputBoxP.add(jtfEmail);

        jtfPassOld = GUI_ElementCreator.newPasswordField();
        jtfPassOld.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Current Password:"));
        inputBoxP.add(jtfPassOld);

        jtfPass1 = GUI_ElementCreator.newPasswordField();
        jtfPass1.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("New Password:"));
        inputBoxP.add(jtfPass1);

        jtfPass2 = GUI_ElementCreator.newPasswordField();
        jtfPass2.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Confirm New Password:"));
        inputBoxP.add(jtfPass2);
        add(inputBoxP, BorderLayout.CENTER);

        JButton jbtOk = GUI_ElementCreator.newJButton("Change My Password");
        jbtOk.addActionListener(this);
        add(jbtOk, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        int code = 0;
        if (a.getActionCommand().compareToIgnoreCase("Change My Password") == 0) {
            Request r = Request.instance();
            r.setEmail(jtfEmail.getText().trim());
            r.setOldPass(String.valueOf(jtfPassOld.getPassword()));
            r.setPass1(String.valueOf(jtfPass1.getPassword()));
            r.setPass2(String.valueOf(jtfPass2.getPassword()));
            try {
                code = Authentication.instance().passwordChange(r);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            switch (code)
            {
                case 1:
                    JOptionPane.showMessageDialog(null, "The New Password entries must match.");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "New Password has to be between\n8 and 19 characters long!");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Unrecognized email.");
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "The Current Password does\nnot match our records.");
                    break;
                case 5:
                    JOptionPane.showMessageDialog(null, "Your New Password is:\n"+ r.getPass1());
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Sorry. Your password could not be changed!");
                    break;
            }
            r.setOldPass(""); r.setPass1(""); r.setPass2("");
        }
        if (code==5) GUI_PasswordChangeWindow.getInstance().disposePasswordChangeWindow();
    }
}


