package gui_v1.mainWindows.loginSigninWElements;

import authentication.Authentication;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_PasswordChangeWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.sql.SQLException;

public class GUI_CloseAccountP extends JPanel implements GUI_LoginSignUpWiindows_Settings, ActionListener {
    private JTextField jtfEmail;
    private JPasswordField jtfPass;

    public GUI_CloseAccountP() {
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Close Account"), BorderLayout.NORTH);

        JPanel inputBoxP = new JPanel();
        inputBoxP.setLayout(new GridLayout(3, 2));

        String warning1 = "You are about to delete your account.  ";
        JLabel lbl1 = new JLabel(warning1, JLabel.RIGHT);
        inputBoxP.add(lbl1);
        String warning2 = "This action cannot be undone!";
        JLabel lbl2 = new JLabel(warning2, JLabel.LEFT);
        inputBoxP.add(lbl2);

        jtfEmail = GUI_ElementCreator.newTextField("");
        jtfEmail.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Enter Your Email:"));
        inputBoxP.add(jtfEmail);

        jtfPass = GUI_ElementCreator.newPasswordField();
        jtfPass.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Enter Your Password:"));
        inputBoxP.add(jtfPass);
        add(inputBoxP, BorderLayout.CENTER);

        JButton jbtOk = GUI_ElementCreator.newJButton("Close and Delete This Account");
        jbtOk.addActionListener(this);
        add(jbtOk, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        int code = 0;
        if (a.getActionCommand().compareToIgnoreCase("Close and Delete This Account") == 0) {
            Request r = Request.instance();
            r.setEmail(jtfEmail.getText().trim());
            r.setPass1(String.valueOf(jtfPass.getPassword()));
            try {
                code = Authentication.instance().closeAccountDialog(r);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            switch (code)
            {
                case 1:
                    JOptionPane.showMessageDialog(null, "Unrecognized Email.");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "The entered Password does\nnot match our records.");
                    break;
                case 3:
                    String entry = JOptionPane.showInputDialog(null, "Please enter Your Email one more time:");
                    if (entry!=null && entry.compareToIgnoreCase(jtfEmail.getText().trim())==0) {
                        Object[] options = { "YES", "NO" };
                        int answer = JOptionPane.showOptionDialog(null,
                                "Your Account will now be closed and DELETED,\n"+
                                        "and this program terminated. Do you agree?", "Warning",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, options, options[1]);
                        if (answer==0) {
                            try {
                                Authentication.instance().closeAccount();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            JOptionPane.showMessageDialog(null, "Thank you for using\n"+
                                    "Personal Expense Consultant\n"+
                                    "by SPAM Team \u00a9 2023", "Thank you", JOptionPane.INFORMATION_MESSAGE);
                            System.exit(0);
                        }
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Sorry. Your Account could not be closed!");
                    break;
            }
            r.setPass1("");
        }
    }
}


