package gui_v1.mainWindows.loginSigninWElements;
import db_connectors.Connectivity;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_MainWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;
import gui_v1.settings.GUI_LoginSignUpWiindows_Settings;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        String txt = "Don't have an account yet? Click HERE.";
        JLabel lbl = new JLabel(txt, JLabel.LEFT);
        inputBoxP.add(lbl);
        MouseListener m = null;
        lbl.addMouseListener(new MouseAdapter()
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
                lbl.setForeground(GUI_Settings_Variables.linkSelected);
                lbl.setText("<html><a href=''>" + txt + "</a></html>");
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                lbl.setForeground(GUI_Settings_Variables.linkDeselected);
                lbl.setText(txt);
                setCursor(Cursor.getDefaultCursor());
            }
        });
        JLabel padding = new JLabel("", JLabel.LEFT);
        inputBoxP.add(padding);

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
                userID = PEC.instance().login(req);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (userID!=-1) {
                GUI_LogInWindow.getInstance().hideLogInWindow();
                GUI_RecordsWindow.getInstance().showRecordsWindow();
            } else {
                JOptionPane.showMessageDialog(null,"Wrong Email or Password!");
            }

        }
    }

}

