package gui_v1.mainWindows.advisingWElements;

import advising.Advising;
import entities.Transaction;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.mainWindows.GUI_AdvisingWindow;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

public class GUI_AdvisingP extends JPanel implements GUI_Settings_Variables, ActionListener {

    private JPanel jpAdvisingActionControlBoxP;
    private JPanel jpPredictionScopeSelectionBoxP;
    private JPanel jpAdvisingDisplayBoxP;
    private GUI_AdvisingInstanceP advisingInstance;
    private static JComboBox<String> jcmbAccount;
    private static JComboBox<String> jcmbDate1;
    private static JComboBox<String> jcmbDate2;
    private static JComboBox<String> jcmbScope;
    private static int selectedDate1, selectedDate2;
    private JButton jbtnOK;
    private static String[] predictionOptions = {"3 months", "6 months", "1 year", "2 years", "5 years", "10 years"};
    private static String[] emptyPredisctionOptions = {"               "};

    public GUI_AdvisingP() {
        setLayout(new BorderLayout());

        JPanel jpAdvisingTitleBoxP = new JPanel();
        jpAdvisingDisplayBoxP = new JPanel();
        jpAdvisingActionControlBoxP = new JPanel();
        jpPredictionScopeSelectionBoxP = new JPanel();

        String[] nicksListTemp = GUI_ElementsOptionLists.getInstance().getAccountNicksList();
        String[] nicksList = new String[nicksListTemp.length-1];
        System.arraycopy(nicksListTemp, 0, nicksList, 0, nicksListTemp.length-1);
        jcmbAccount = GUI_ElementCreator.newJComboBox(nicksList);
        String[] dateList = returnDateOptions(PEC.instance().getActiveAccount());
        jcmbDate1 = GUI_ElementCreator.newJComboBox(dateList);
        jcmbDate2 = GUI_ElementCreator.newJComboBox(dateList);
        if (dateList.length>=2) {
            selectedDate1 = dateList.length - 2;
            selectedDate2 = dateList.length - 1;
            String[] d1Array = dateList[selectedDate1].split("-");
            String[] d2Array = dateList[selectedDate2].split("-");
            advisingInstance = new GUI_AdvisingInstanceP(
                    PEC.instance().getActiveAccount(), d1Array[0], d2Array[0], 0);
            jcmbScope = GUI_ElementCreator.newJComboBox(predictionOptions);
            jcmbScope.setSelectedIndex(0);
            jcmbScope.addActionListener(this);
        } else {
            selectedDate1 = 0;
            selectedDate2 = 0;
            advisingInstance = new GUI_AdvisingInstanceP(null, null, null, 0);
            jcmbScope = GUI_ElementCreator.newJComboBox(emptyPredisctionOptions);
        }

        jpAdvisingDisplayBoxP = advisingInstance;
        jpAdvisingTitleBoxP.setLayout(new GridLayout(4,1));
        jpAdvisingActionControlBoxP.setLayout(new GridBagLayout());
        JLabel acctLabel = GUI_ElementCreator.newTextLabel("Account:");
        jpAdvisingActionControlBoxP.add(acctLabel);
        jcmbAccount.setSelectedItem(PEC.instance().getActiveAccount());
        jcmbAccount.addActionListener(this);
        jpAdvisingActionControlBoxP.add(jcmbAccount);

        JLabel date1Label = GUI_ElementCreator.newTextLabel("Period 1:");
        jpAdvisingActionControlBoxP.add(date1Label);
        jcmbDate1.setSelectedIndex(selectedDate1);
        jpAdvisingActionControlBoxP.add(jcmbDate1);
        JLabel date2Label = GUI_ElementCreator.newTextLabel("Period 2:");
        jpAdvisingActionControlBoxP.add(date2Label);
        jcmbDate2.setSelectedIndex(selectedDate2);
        jpAdvisingActionControlBoxP.add(jcmbDate2);

        jbtnOK = GUI_ElementCreator.newJButton("OK");
        jbtnOK.addActionListener(this);
        jpAdvisingActionControlBoxP.add(jbtnOK);

        JLabel initialInstruct = GUI_ElementCreator.newFieldNameLabel(
                "The analysis is based on comparison of EXPENSES"+
                " (and their relative change) between Period 1 and Period 2. "+
                        "Select & click OK button.");
        jpAdvisingTitleBoxP.add(initialInstruct);
        jpAdvisingTitleBoxP.add(jpAdvisingActionControlBoxP);
        JLabel categExplenation = GUI_ElementCreator.newFieldNameLabel(
                "<html><u><b>Listed categories are ones that appear in "+
                        "<font color='#00'>BOTH<font color='#ff'> "+
                        "Period 1 and 2 EXPENSE Summaries.</b></u></html>");
        jpAdvisingTitleBoxP.add(categExplenation);
        JLabel predictionInstruct = GUI_ElementCreator.newFieldNameLabel(
                "For different scope of future expense prediction, please select "+
                        "from the drop-down menu below.");
        jpAdvisingTitleBoxP.add(predictionInstruct);
        add(jpAdvisingTitleBoxP, BorderLayout.NORTH);
        add(jpAdvisingDisplayBoxP, BorderLayout.CENTER);

        jpPredictionScopeSelectionBoxP.setLayout(new GridBagLayout());
        JLabel predictionLabel = GUI_ElementCreator.newTextLabel("Scope of Prediction:");
        jpPredictionScopeSelectionBoxP.add(predictionLabel);
        jpPredictionScopeSelectionBoxP.add(jcmbScope);
        add(jpPredictionScopeSelectionBoxP, BorderLayout.SOUTH);
    }

    private String[] returnDateOptions(String acctNick) {
        ArrayList<Calendar[]> calList = Advising.getAllAvailablePeriodsForAdvising(acctNick);
        // the first dimension will indicate the 3-month-chunk in database,
        // the second dimension will be 0 for beginning, 1 for ending date
        if (calList==null) {
            String[] result = new String[1];
            result[0] = "";
            return result;
        }
        String[] result = new String[calList.size()];
        for (int i = 0; i < calList.size(); i++) {
            result[i] = Transaction.returnYYYYMMDDFromCalendar(calList.get(i)[0])+"-"+
                    Transaction.returnYYYYMMDDFromCalendar(calList.get(i)[1]);
        }
        return result;
    }

    private int getDateListPosition(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].startsWith(item)) return i;
        }
        return -1;
    }

    private void processOKBtnClick() {
        selectedDate1 = jcmbDate1.getSelectedIndex();
        selectedDate2 = jcmbDate2.getSelectedIndex();
        if (selectedDate1>selectedDate2) {
            int temp = selectedDate2;
            selectedDate2 = selectedDate1;
            selectedDate1 = temp;
            jcmbDate1.setSelectedIndex(selectedDate1);
            jcmbDate2.setSelectedIndex(selectedDate2);
        }
        String temp1 = (String) jcmbDate1.getSelectedItem();
        String temp2 = (String) jcmbDate2.getSelectedItem();
        temp1 = temp1.trim();
        temp2 = temp2.trim();
        String[] d1Array = temp1.split("-");
        String[] d2Array = temp2.split("-");
        JPanel oldPanel = jpAdvisingDisplayBoxP;
        if (temp1.compareToIgnoreCase("")==0 & temp2.compareToIgnoreCase("")==0){
            advisingInstance = new GUI_AdvisingInstanceP(null, null, null, 0);
        } else {
            advisingInstance = new GUI_AdvisingInstanceP(
                    (String) jcmbAccount.getSelectedItem(), d1Array[0], d2Array[0], jcmbScope.getSelectedIndex());
        }
        jpAdvisingDisplayBoxP = advisingInstance;
        this.remove(oldPanel);
        this.add(jpAdvisingDisplayBoxP, BorderLayout.CENTER);
        GUI_AdvisingWindow.getInstance().showAdvisingWindow();
    }

    private void processAccountSelection() {
        String[] dateList = returnDateOptions((String) jcmbAccount.getSelectedItem());
        JComboBox<String> jcmbNewDate1 = GUI_ElementCreator.newJComboBox(dateList);
        JComboBox<String> jcmbNewDate2 = GUI_ElementCreator.newJComboBox(dateList);
        JComboBox<String> jcmbNewScope;
        if (dateList.length>=2) {
            selectedDate1 = dateList.length - 2;
            selectedDate2 = dateList.length - 1;
            jcmbNewScope = GUI_ElementCreator.newJComboBox(predictionOptions);
            jcmbNewScope.setSelectedIndex(0);
            jcmbNewScope.addActionListener(this);
        } else {
            selectedDate1 = 0;
            selectedDate2 = 0;
            jcmbNewScope = GUI_ElementCreator.newJComboBox(emptyPredisctionOptions);
        }
        jpAdvisingActionControlBoxP.remove(jcmbDate1);
        jcmbDate1 = jcmbNewDate1;
        jcmbDate1.setSelectedIndex(selectedDate1);
        jpAdvisingActionControlBoxP.add(jcmbDate1, 3);
        jpAdvisingActionControlBoxP.remove(jcmbDate2);
        jcmbDate2 = jcmbNewDate2;
        jcmbDate2.setSelectedIndex(selectedDate2);
        jpAdvisingActionControlBoxP.add(jcmbDate2, 5);
        jpPredictionScopeSelectionBoxP.remove(jcmbScope);
        jcmbScope = jcmbNewScope;
        jpPredictionScopeSelectionBoxP.add(jcmbScope);
        GUI_AdvisingWindow.getInstance().showAdvisingWindow();
    }

    private void processScopeChange() {
        if (!advisingInstance.isEmpty()) advisingInstance.switchScope(jcmbScope.getSelectedIndex());
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbtnOK) {
            processOKBtnClick();
        }
        if (e.getSource() == jcmbAccount) {
            processAccountSelection();
        }
        if (e.getSource() == jcmbScope) {
            processScopeChange();
        }
    }
}
