package gui_v1.mainWindows.summaryWElements;

import entities.Transaction;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.mainWindows.GUI_SummaryPickerWindow;
import gui_v1.mainWindows.GUI_SummaryWindow;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import summary.Summary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

public class GUI_SummaryPickerP extends JPanel implements GUI_Settings_Variables, ActionListener{

    private static int selectedAccount;
    private static int selectedDate1;
    private static int selectedDate2;

    private static JComboBox<String> jcmAcctNick;
    private static JComboBox<String> jcmDate1;
    private static JComboBox<String> jcmDate2;

    private JButton jbtnOK;
    private JPanel pBox;
    private GridBagConstraints c;

    private void init(){
        String[] nicksListTemp = GUI_ElementsOptionLists.getInstance().getAccountNicksList();
        String[] nicksList = new String[nicksListTemp.length-1];
        System.arraycopy(nicksListTemp, 0, nicksList, 0, nicksListTemp.length-1);
        jcmAcctNick = GUI_ElementCreator.newJComboBox(nicksList);
        selectedAccount = GUI_ElementsOptionLists.getInstance().getAccountListPosition(PEC.instance().getActiveAccount());
        String[][] dateList = returnDateOptions(PEC.instance().getActiveAccount());
        jcmDate1 = GUI_ElementCreator.newJComboBox(dateList[0]);
        selectedDate1 = getDateListPosition(dateList[0],
                Transaction.returnYYYYMMDDFromCalendar(PEC.instance().getCurrentViewBeginDate()));
        if (selectedDate1==-1) selectedDate1=0;
        jcmDate2 = GUI_ElementCreator.newJComboBox(dateList[1]);
        selectedDate2 = getDateListPosition(dateList[1],
                Transaction.returnYYYYMMDDFromCalendar(PEC.instance().getCurrentViewEndDate()));
        if (selectedDate2==-1) selectedDate2=0;
        jbtnOK = GUI_ElementCreator.newJButton("OK");
    }

    public GUI_SummaryPickerP(){
        init();
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Select Summary Account and Span"), BorderLayout.NORTH);
        //pBox = new JPanel(new GridLayout(3,2));
        pBox = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        pBox.add(GUI_ElementCreator.newTextLabel("Account:"), c);
        jcmAcctNick.setSelectedIndex(selectedAccount);
        jcmAcctNick.addActionListener(this);
        c.gridwidth = 2;
        c.gridx = 1; c.gridy = 0;
        pBox.add(jcmAcctNick, c);
        c.gridx = 0; c.gridy = 1;
        pBox.add(new JLabel(" "), c);
        c.gridx = 0; c.gridy = 2;
        pBox.add(GUI_ElementCreator.newTextLabel("From:"), c);
        jcmDate1.setSelectedIndex(selectedDate1);
        c.gridx = 1; c.gridy = 2;
        pBox.add(jcmDate1, c);
        c.gridx = 0; c.gridy = 3;
        pBox.add(new JLabel(" "), c);
        c.gridx = 0; c.gridy = 4;
        pBox.add(GUI_ElementCreator.newTextLabel("To:"), c);
        jcmDate2.setSelectedIndex(selectedDate2);
        c.gridx = 1; c.gridy = 4;
        pBox.add(jcmDate2, c);
        add(pBox, BorderLayout.CENTER);
        jbtnOK.addActionListener(this);
        add(jbtnOK, BorderLayout.SOUTH);
        pBox.requestFocusInWindow();
        jbtnOK.setFocusTraversalPolicyProvider(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbtnOK) {
            processOKBtnClick();
        }
        if (e.getSource() == jcmAcctNick) {
            processAccountSelection();
        }
    }

    private String[][] returnDateOptions(String acctNick) {
        ArrayList<Calendar[]> calList = Summary.getAllAvailablePeriods(acctNick);
        // the first dimension will indicate the 3-month-chunk in database,
        // the second dimension will be 0 for beginning, 1 for ending date
        String[][] result = new String[2][calList.size()];
        for (int i = 0; i < calList.size(); i++) {
            result[0][i] = Transaction.returnYYYYMMDDFromCalendar(calList.get(i)[0]);
            result[1][i] = Transaction.returnYYYYMMDDFromCalendar(calList.get(i)[1]);
        }
        return result;
    }

    private int getDateListPosition(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].startsWith(item)) return i;
        }
        return -1;
    }

    private void processAccountSelection() {
        String[][] dateList = returnDateOptions((String) jcmAcctNick.getSelectedItem());
        JComboBox<String> jcmNewDate1 = GUI_ElementCreator.newJComboBox(dateList[0]);
        selectedDate1 = dateList[0].length-1;
        JComboBox<String> jcmNewDate2 = GUI_ElementCreator.newJComboBox(dateList[1]);
        selectedDate2 = dateList[1].length-1;
        pBox.remove(jcmDate1);
        jcmDate1 = jcmNewDate1;
        jcmDate1.setSelectedIndex(selectedDate1);
        c.gridx = 1; c.gridy = 2;
        pBox.add(jcmDate1, c);
        pBox.remove(jcmDate2);
        jcmDate2 = jcmNewDate2;
        jcmDate2.setSelectedIndex(selectedDate2);
        c.gridx = 1; c.gridy = 4;
        pBox.add(jcmDate2, c);
        GUI_SummaryPickerWindow.getInstance().showSummaryPickerWindow();
    }

    private void processOKBtnClick() {
        String account = (String) jcmAcctNick.getSelectedItem();
        String from = (String) jcmDate1.getSelectedItem();
        String to = (String) jcmDate2.getSelectedItem();
        if (from.compareToIgnoreCase(to)>0) {
            JOptionPane.showMessageDialog(null, "\"From\" date must be earlier than \"To\" date.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        GUI_SummaryPickerWindow.getInstance().disposeSummaryPickerWindow();
        new GUI_SummaryWindow(account, from, to);
    }

    @Override
    public Component getComponent() {
        return null;
    }

}
