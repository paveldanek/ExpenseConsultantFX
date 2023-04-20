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
    private static JComboBox<String> jcmbAccount;
    private static JComboBox<String> jcmbDate1;
    private static JComboBox<String> jcmbDate2;
    private static int selectedDate1, selectedDate2;
    private JButton jbtnOK;

    public GUI_AdvisingP() {
        setLayout(new BorderLayout());

        JPanel jpAdvisingTitleBoxP = new JPanel();
        JPanel jpAdvisingDisplayBoxP = new JPanel();
        jpAdvisingActionControlBoxP = new JPanel();

        jpAdvisingTitleBoxP.setLayout(new GridLayout(2,1));

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
            jpAdvisingDisplayBoxP.add(new GUI_AdvisingInstanceP(null, null, null)
                    /* The main Advising object (param: PEC.instance().getActiveAccount(),
                    dateList[dateList.length - 2], dateList[dateList.length - 1]) instead of new...*/
                    , BorderLayout.CENTER);
        } else {
            selectedDate1 = 0;
            selectedDate2 = 0;
            jpAdvisingDisplayBoxP.add(new GUI_AdvisingInstanceP(null, null, null)
                    /* The main Advising object (no param ==> "Not enough data to create an instance of
                    advising...") instead of new...*/
                    , BorderLayout.CENTER);
        }

        jpAdvisingActionControlBoxP.setLayout(new GridBagLayout());
        JLabel acctLabel = GUI_ElementCreator.newTextLabel("Account:");
        jpAdvisingActionControlBoxP.add(acctLabel);
        jcmbAccount.setSelectedItem(PEC.instance().getActiveAccount());
        jcmbAccount.addActionListener(this);
        jpAdvisingActionControlBoxP.add(jcmbAccount/*, BorderLayout.WEST*/);

        JLabel date1Label = GUI_ElementCreator.newTextLabel("Period 1:");
        jpAdvisingActionControlBoxP.add(date1Label);
        jcmbDate1.setSelectedIndex(selectedDate1);
        jpAdvisingActionControlBoxP.add(jcmbDate1/*, BorderLayout.CENTER*/);
        JLabel date2Label = GUI_ElementCreator.newTextLabel("Period 2:");
        jpAdvisingActionControlBoxP.add(date2Label);
        jcmbDate2.setSelectedIndex(selectedDate2);
        jpAdvisingActionControlBoxP.add(jcmbDate2/*, BorderLayout.CENTER*/);

        jbtnOK = GUI_ElementCreator.newJButton("OK");
        jbtnOK.addActionListener(this);
        jpAdvisingActionControlBoxP.add(jbtnOK);

        JLabel initialInstruct = GUI_ElementCreator.newFieldNameLabel(
                "The analysis is based on the numbers' "+
                "comparison (and their relative change) between Period 1 and Period 2.");
        jpAdvisingTitleBoxP.add(initialInstruct);
        jpAdvisingTitleBoxP.add(jpAdvisingActionControlBoxP);
        add(jpAdvisingTitleBoxP, BorderLayout.NORTH);
        add(jpAdvisingDisplayBoxP, BorderLayout.CENTER);

        // new code goes HERE...
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
        // call AdvisingInstance()...
    }

    private void processAccountSelection() {

        String[] dateList = returnDateOptions((String) jcmbAccount.getSelectedItem());
        JComboBox<String> jcmNewDate1 = GUI_ElementCreator.newJComboBox(dateList);
        JComboBox<String> jcmNewDate2 = GUI_ElementCreator.newJComboBox(dateList);
        if (dateList.length>=2) {
            selectedDate1 = dateList.length - 2;
            selectedDate2 = dateList.length - 1;
        } else {
            selectedDate1 = 0;
            selectedDate2 = 0;
        }
        jpAdvisingActionControlBoxP.remove(jcmbDate1);
        jcmbDate1 = jcmNewDate1;
        jcmbDate1.setSelectedIndex(selectedDate1);
        /*
        Component[] c = jpAdvisingActionControlBoxP.getComponents();
        System.out.println("HELLO");
        int i;
        for (i = 0; i < c.length; i++) {if (c[i].equals(jcmbDate1)) break;}
        System.out.println(i);
        int j;
        for (j = 0; j < c.length; i++) {if (c[j].equals(jcmbDate2)) break;}
        System.out.println("Date1 = #"+i+", Date2 = #"+j+".");

         */
        jpAdvisingActionControlBoxP.add(jcmbDate1, 3);
        jpAdvisingActionControlBoxP.remove(jcmbDate2);
        jcmbDate2 = jcmNewDate2;
        jcmbDate2.setSelectedIndex(selectedDate2);
        jpAdvisingActionControlBoxP.add(jcmbDate2, 5);
        GUI_AdvisingWindow.getInstance().showAdvisingWindow();
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
    }
}
