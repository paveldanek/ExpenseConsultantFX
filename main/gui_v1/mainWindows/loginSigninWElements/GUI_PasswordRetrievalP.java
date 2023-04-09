package gui_v1.mainWindows.loginSigninWElements;

import crypto.AESUtil;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.mainWindows.GUI_PasswordRetrievalWindow;
import login.Account;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class GUI_PasswordRetrievalP extends JPanel implements GUI_LoginSignUpWiindows_Settings, ActionListener {
    private JFrame passwordRetrievalFrame;
    private JTextField jtfAnswer1;
    private JTextField jtfAnswer2;
    private JTextField jtfEmail;
    private JComboBox jcmbQuestion1;
    private JComboBox jcmbQuestion2;

    public GUI_PasswordRetrievalP() {
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Password Retrieval"), BorderLayout.NORTH);

        JPanel inputBoxP = new JPanel();
        inputBoxP.setLayout(new GridLayout(5, 2));

        jtfEmail = GUI_ElementCreator.newTextField("");
        jtfEmail.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Enter Your Email:"));
        inputBoxP.add(jtfEmail);

        jcmbQuestion1 = GUI_ElementCreator.newJComboBox(new String[]{"What's your pet's name?", "Which city were you born in?", "What's your favorite car?"});
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Security Question 1:"));
        inputBoxP.add(jcmbQuestion1);
        jtfAnswer1 = GUI_ElementCreator.newTextField("");
        jtfAnswer1.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Answer To Question 1:"));
        inputBoxP.add(jtfAnswer1);

        jcmbQuestion2 = GUI_ElementCreator.newJComboBox(new String[]{"What's your pet's name?", "Which city were you born in?", "What's your favorite car?"});
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Security Question 2:"));
        inputBoxP.add(jcmbQuestion2);
        jtfAnswer2 = GUI_ElementCreator.newTextField("");
        jtfAnswer2.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Answer To Question 2:"));
        inputBoxP.add(jtfAnswer2);
        add(inputBoxP, BorderLayout.CENTER);

        JButton jbtOk = GUI_ElementCreator.newJButton("Retrieve My Password");
        jbtOk.addActionListener(this);
        add(jbtOk, BorderLayout.SOUTH);


    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getActionCommand().compareToIgnoreCase("Retrieve My Password") == 0) {
            int code = 0;
            Request r = Request.instance();
            r.setEmail(jtfEmail.getText().trim());
            r.setQuestion1(jcmbQuestion1.getSelectedItem().toString().trim());
            r.setQuestion2(jcmbQuestion2.getSelectedItem().toString().trim());
            r.setAnswer1(jtfAnswer1.getText().trim());
            r.setAnswer2(jtfAnswer2.getText().trim());
            try {
                code = Account.instance().retrievePassword(r);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            switch (code)
            {
                case 1:
                    JOptionPane.showMessageDialog(null, "Unrecognized email. You may\nhave to set up a new account.");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "The security Questions & Answers\ndo not match our record. Sorry.");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Your password is:\n"+ AESUtil.decryptItem(r.getPass1()));
                    r.setPass1("");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Sorry. Your password could not be retrieved!");
                    break;
            }
        }
        GUI_PasswordRetrievalWindow.getInstance().hidePasswordRetrievalWindow();
        jtfEmail.setText("");
        jcmbQuestion1.setSelectedIndex(0);
        jcmbQuestion2.setSelectedIndex(0);
        jtfAnswer1.setText("");
        jtfAnswer2.setText("");
        GUI_LogInWindow.getInstance().showLogInWindow();
    }
}


