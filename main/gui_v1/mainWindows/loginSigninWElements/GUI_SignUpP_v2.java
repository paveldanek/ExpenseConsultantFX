package gui_v1.mainWindows.loginSigninWElements;

import gui_v1.action_processors.SignupNewUserProgrammableHandler;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class GUI_SignUpP_v2 extends JPanel implements GUI_LoginSignUpWiindows_Settings, ActionListener {
    private JFrame signUpFrame;
    private JTextField jtfSeqAnswer1;
    private JTextField jtfSeqAnswer2;
    private JTextField jtfNewLogInName;
    private JTextField jtfEmail;
    private JPasswordField jtfNewPass;
    private JPasswordField jtfNewPass2;
    private JComboBox jcmbQuestion1;
    private JComboBox jcmbQuestion2;

    public GUI_SignUpP_v2() {
//        this.signUpFrame = frame;
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle(StrSignUpHeadTilte), BorderLayout.NORTH);


        JPanel inputBoxP = new JPanel();
        inputBoxP.setLayout(new GridLayout(8, 2));

        String txt = "Already have an account? Click HERE.";
        JLabel lbl = new JLabel(txt, JLabel.LEFT);
        inputBoxP.add(lbl);
        MouseListener m = null;
        lbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                GUI_SignUPWindow.getInstance().hideSignUpWindow();
                GUI_LogInWindow.getInstance().showLogInWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e)
            {
                setForeground(GUI_Settings_Variables.linkSelected);
                lbl.setText("<html><a href=''>" + txt + "</a></html>");
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                setForeground(GUI_Settings_Variables.linkDeselected);
                lbl.setText(txt);
                setCursor(Cursor.getDefaultCursor());
            }
        });
        JLabel padding = new JLabel("", JLabel.LEFT);
        inputBoxP.add(padding);

        jtfEmail = GUI_ElementCreator.newTextField("");
        jtfEmail.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Email:"));
        inputBoxP.add(jtfEmail);

        jtfNewPass = new JPasswordField();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Password:"));
        inputBoxP.add(jtfNewPass);
        jtfNewPass2 = new JPasswordField();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Confirm password:"));
        inputBoxP.add(jtfNewPass2);
        add(inputBoxP, BorderLayout.CENTER);

        jcmbQuestion1 = GUI_ElementCreator.newJComboBox(new String[]{"What's your pet's name?", "Which city were you born in?", "What's your favorite car?"});
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Security Question 1:"));
        inputBoxP.add(jcmbQuestion1);
        jtfSeqAnswer1 = GUI_ElementCreator.newTextField("");
        jtfSeqAnswer1.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Answer To Question 1:"));
        inputBoxP.add(jtfSeqAnswer1);

        jcmbQuestion2 = GUI_ElementCreator.newJComboBox(new String[]{"What's your pet's name?", "Which city were you born in?", "What's your favorite car?"});
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Security Question 2:"));
        jcmbQuestion2.setSelectedIndex(1);
        // Get selected a different question
        inputBoxP.add(jcmbQuestion2);
        jtfSeqAnswer2 = GUI_ElementCreator.newTextField("");
        jtfSeqAnswer2.selectAll();
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Answer To Question 2:"));
        inputBoxP.add(jtfSeqAnswer2);

        /*
        lbl.setFont(newFont(lbl.getFont(), txtSize_Regular));
        lbl.setForeground(clrF_InfoMsgs);
        */

        JButton jbtOk = GUI_ElementCreator.newJButton("OK");
        jbtOk.addActionListener(this);
        add(jbtOk, BorderLayout.SOUTH);


    }

    @Override
    public void actionPerformed(ActionEvent a) {


        if (a.getActionCommand().compareToIgnoreCase("OK") == 0) {
            int code = 0;
            Request r = Request.instance();
            r.setEmail(jtfEmail.getText().trim());
            //System.out.println(jtfNewPass.getText());
            r.setPass1(String.valueOf(jtfNewPass.getText()));
            r.setPass2(String.valueOf(jtfNewPass2.getText()));
            r.setQuestion1(jcmbQuestion1.getSelectedItem().toString().trim());
            r.setQuestion2(jcmbQuestion2.getSelectedItem().toString().trim());
            r.setAnswer1(jtfSeqAnswer1.getText().trim());
            r.setAnswer2(jtfSeqAnswer2.getText().trim());
            try {
                code = PEC.instance().signup(r);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            switch (code)
            {
                case 1:
                    JOptionPane.showMessageDialog(null, "Incorrect email format!");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "This email has its account already!");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Password has to be between\n8 and 19 characters long!");
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "The two password entries don't match!");
                    break;
                case 5:
                    JOptionPane.showMessageDialog(null, "The user account\n"+r.getEmail()+"\nhas been successfully created.");
                    GUI_SignUPWindow.getInstance().hideSignUpWindow();
                    GUI_RecordsWindow.getInstance().showRecordsWindow();
                    break;
                case 6:
                default:
                    JOptionPane.showMessageDialog(null, "Account could not be created!");
                    break;
            }
        }
    }
}


