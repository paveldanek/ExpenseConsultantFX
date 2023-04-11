package gui_v1.mainWindows.loginSigninWElements;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.settings.GUI_Settings_Variables;
import gui_v1.mainWindows.GUI_PasswordRetrievalWindow;
import authentication.Authentication;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class GUI_LogInP extends JPanel implements GUI_LoginSignUpWiindows_Settings, ActionListener {

    private final JTextField jtfLogInName;
    private final JPasswordField jtfPass;


    public GUI_LogInP(){
        setLayout(new BorderLayout());
//        add(new Label(strLogInHeadTitle), BorderLayout.NORTH);
        add(GUI_ElementCreator.newTitle(strLogInHeadTitle), BorderLayout.NORTH);

        JPanel inputBoxP = new JPanel();
        inputBoxP.setLayout(new GridLayout(3,2));

        jtfLogInName =  GUI_ElementCreator.newTextField();
        jtfLogInName.setText("");
        jtfLogInName.selectAll();
        jtfPass =  GUI_ElementCreator.newPasswordField();
        jtfPass.setText("");
        JButton jbtOk = GUI_ElementCreator.newJButton("OK");
        jbtOk.addActionListener(this);

        inputBoxP.add(GUI_ElementCreator.newTextLabel("Login Email:"));
        inputBoxP.add(jtfLogInName);
        inputBoxP.add(GUI_ElementCreator.newTextLabel("Password:"));
        inputBoxP.add(jtfPass);

        String noAcct = "Don't have an account yet? Click HERE.";
        JLabel lbl1 = new JLabel(noAcct, JLabel.LEFT);
        inputBoxP.add(lbl1);
        String forgotPass = "Forgot your password? Click HERE.";
        JLabel lbl2 = new JLabel(forgotPass, JLabel.LEFT);
        inputBoxP.add(lbl2);

        MouseListener m = null;
        lbl1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                GUI_LogInWindow.getInstance().hideLogInWindow();
                GUI_SignUPWindow.getInstance().showSignUpWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e)
            {
                lbl1.setForeground(GUI_Settings_Variables.linkSelected);
                lbl1.setText("<html><a href=''>" + noAcct + "</a></html>");
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                lbl1.setForeground(GUI_Settings_Variables.linkDeselected);
                lbl1.setText(noAcct);
                setCursor(Cursor.getDefaultCursor());
            }
        });

        lbl2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                GUI_LogInWindow.getInstance().hideLogInWindow();
                GUI_PasswordRetrievalWindow.getInstance().showPasswordRetrievalWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e)
            {
                lbl2.setForeground(GUI_Settings_Variables.linkSelected);
                lbl2.setText("<html><a href=''>" + forgotPass + "</a></html>");
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                lbl2.setForeground(GUI_Settings_Variables.linkDeselected);
                lbl2.setText(forgotPass);
                setCursor(Cursor.getDefaultCursor());
            }
        });

        add(inputBoxP, BorderLayout.CENTER);
        add(jbtOk, BorderLayout.SOUTH);

    }


    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getActionCommand().compareToIgnoreCase("OK")==0) {
            Request req = Request.instance();
            int userID = -1;
            req.setEmail(String.valueOf(jtfLogInName.getText()));
            req.setPass1(String.valueOf(jtfPass.getPassword()));
            try {
                userID = Authentication.instance().login(req);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (userID!=-1) {
                PEC.instance().finishLogin(userID);
                GUI_LogInWindow.getInstance().hideLogInWindow();
                GUI_RecordsWindow.getInstance().showRecordsWindow();
            } else {
                JOptionPane.showMessageDialog(null,"Wrong Email or Password!");
            }

        }
    }

}

