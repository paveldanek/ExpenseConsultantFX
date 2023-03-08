package gui_v1.transaction_records;

import gui_v1.action_processors.ManualEntryProgrammableHandler;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.guiHelp_utils.GUI_AY_Calendar;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import gui_v1.mainWindows.GUI_ManualEntryWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.settings.GUI_Settings_Variables;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;

public class GUI_ManualTransactionsEntryP extends JPanel implements GUI_Settings_Variables, ActionListener, FocusListener {

    private static String[] manyalEntryDefaultJTextFieldText =new String[]{"Select Nick","Enter Date in Format: 02/22/2023","Enter Reference number",
            "Enter Transaction Name","Enter Memo","Enter Amount", "Select Category"};


    private JTextField jtfDate = GUI_ElementCreator.newTextField(manyalEntryDefaultJTextFieldText[1]);
    private JTextField jtfOutputDate= GUI_ElementCreator.newOutputTextField("2023/04/22");
    private JTextField jtfRefNum= GUI_ElementCreator.newTextField(manyalEntryDefaultJTextFieldText[2]);
    private JTextField jtfTransName = GUI_ElementCreator.newTextField(manyalEntryDefaultJTextFieldText[3]);
    private JTextField jtfMemo= GUI_ElementCreator.newTextField(manyalEntryDefaultJTextFieldText[4]);
    private JTextField jtfAmount= GUI_ElementCreator.newTextField(manyalEntryDefaultJTextFieldText[5]);

    private static JComboBox<String> jcmbAccount  = GUI_ElementCreator.newJComboBox(TransactionBankAccountInitialLists.getInstance().getAccntNicksList());
    private static JComboBox<String> jcmbCategory = GUI_ElementCreator.newJComboBox(TransactionBankAccountInitialLists.getInstance().getTransCategoryist());

    private JButton jbtnCancel = GUI_ElementCreator.newJButton("Cancel");
    private JButton jbtnAnother = GUI_ElementCreator.newJButton("Another");
    private JButton jbtnDone = GUI_ElementCreator.newJButton("Done");

    private JButton jbtnFirst = GUI_ElementCreator.newJButton("<--First");
    private JButton jbtnPrev = GUI_ElementCreator.newJButton("<-Prev");
    private JButton jbtnNext = GUI_ElementCreator.newJButton("Next->");
    private JButton jbtnLast = GUI_ElementCreator.newJButton("Last-->");

    private JComboBox<String> jcmbYears;
    private JComboBox<String> jcmbMonths;
    private JComboBox<String> jcmbDays;


    public GUI_ManualTransactionsEntryP(){
        setLayout(new BorderLayout());
        String  headingTitle  = "Transaction Manual Entry";
        add(GUI_ElementCreator.newSubHead(headingTitle), BorderLayout.NORTH);

        JPanel mainBoxP = new JPanel(new BorderLayout());
        String  manualEntryTitleMessage  = "Enter Transaction Information";
        mainBoxP.add(GUI_ElementCreator.newTitle(manualEntryTitleMessage), BorderLayout.NORTH);

        JPanel userInputElementsBox = new JPanel(new GridLayout(7,2));
        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Account:"));
        jcmbAccount.addActionListener(this);
        jcmbAccount.insertItemAt(manyalEntryDefaultJTextFieldText[0], 0);
        jcmbAccount.setSelectedIndex(0);

        userInputElementsBox.add(jcmbAccount);

        JPanel dateBoxP = new JPanel(new BorderLayout());
        dateBoxP.add(GUI_ElementCreator.newTextLabel("Date:"), BorderLayout.WEST);

        jtfOutputDate.setEditable(false);
        jtfOutputDate.setBackground(null);
        JPanel datOutBoxP = new JPanel();
        jtfOutputDate.setPreferredSize(new Dimension(120,txtSize_JTextField+4));
        datOutBoxP.add(GUI_ElementCreator.newOutputTextFieldLabel("Selected Date:"));
        datOutBoxP.add(jtfOutputDate);
        datOutBoxP.add(GUI_ElementCreator.newFieldNameLabel(" "));
        dateBoxP.add(datOutBoxP, BorderLayout.EAST);
        userInputElementsBox.add(dateBoxP);
        userInputElementsBox.add(dateSelectionsP());


        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Reference #"));
        userInputElementsBox.add(jtfRefNum);
        jtfRefNum.addFocusListener(this);
        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Transaction Name:"));
        userInputElementsBox.add(jtfTransName);
        jtfTransName.addFocusListener(this);
        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Memo:"));
        userInputElementsBox.add(jtfMemo);
        jtfMemo.addFocusListener(this);
        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Amount:"));
        userInputElementsBox.add(jtfAmount);
        jtfAmount.addFocusListener(this);

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Category:"));
        jcmbCategory.insertItemAt(manyalEntryDefaultJTextFieldText[manyalEntryDefaultJTextFieldText.length-1], 0);
        jcmbCategory.setSelectedIndex(0);
        userInputElementsBox.add(jcmbCategory);

        mainBoxP.add(userInputElementsBox, BorderLayout.CENTER);
        add(mainBoxP, BorderLayout.CENTER);


        jbtnFirst.addActionListener(this);
        jbtnPrev.addActionListener(this);
        jbtnNext.addActionListener(this);
        jbtnLast.addActionListener(this);


        jbtnDone.addActionListener(this);
        jbtnCancel.addActionListener(this);
        jbtnAnother.addActionListener(this);

        JPanel buttonsBox = new JPanel(new GridLayout(2,4));
        buttonsBox.add(jbtnFirst);
        buttonsBox.add(jbtnPrev);
        buttonsBox.add(jbtnNext);
        buttonsBox.add(jbtnLast);
        buttonsBox.add(jbtnCancel);
        buttonsBox.add(jbtnAnother);
        buttonsBox.add(jbtnDone);
        add(buttonsBox, BorderLayout.SOUTH);

    }

    private JPanel dateSelectionsP(){

        JPanel dateBoxP = new JPanel(new GridLayout(1,6));

        jcmbYears = GUI_ElementCreator.newJComboBox(GUI_AY_Calendar.getYearsDesendingArr());
        jcmbYears.setActionCommand("YEAR");
        jcmbYears.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR));
        jcmbYears.addActionListener(actionListenerForDatesJCMboxes());

        jcmbMonths = GUI_ElementCreator.newJComboBox(GUI_AY_Calendar.monthsArr);
        jcmbMonths.setSelectedItem((Calendar.getInstance().get(Calendar.MONTH)+1)+"");
        jcmbMonths.addActionListener(actionListenerForDatesJCMboxes());
        jcmbMonths.setActionCommand("MONTH");

        jcmbDays = GUI_ElementCreator.newJComboBox(GUI_AY_Calendar.getDaysAsStrArrForMountOFYear(Integer.parseInt(jcmbMonths.getSelectedItem()+""),
                Integer.parseInt(jcmbYears.getSelectedItem()+"")));
        jcmbDays.setSelectedItem(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"");
        jcmbDays.addActionListener(actionListenerForDatesJCMboxes());
        jcmbDays.setActionCommand("DAY");

        dateBoxP.add(GUI_ElementCreator.newTextLabel("Year"));
        dateBoxP.add(jcmbYears);
        dateBoxP.add(GUI_ElementCreator.newFieldNameLabel("Month"));
        dateBoxP.add(jcmbMonths);
        dateBoxP.add(GUI_ElementCreator.newFieldNameLabel("Day"));
        dateBoxP.add(jcmbDays);

        setDateOutputTextFieldWithDateFromComboBoxes();

        return dateBoxP;
    }
    private void setDateOutputTextFieldWithDateFromComboBoxes(){
        jtfOutputDate.setText( jcmbMonths.getSelectedItem() +"/"+jcmbDays.getSelectedItem()+ "/"+jcmbYears.getSelectedItem());
    }
    private ActionListener actionListenerForDatesJCMboxes(){
        ActionListener a=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                o(e);
                if(e.getActionCommand().compareToIgnoreCase("YEAR")==0){
                    jcmbMonths.setSelectedIndex(0);
//                    jcmbDays.setSelectedIndex(0);
//                    o("YEAR");
                }else if(e.getActionCommand().compareToIgnoreCase("MONTH")==0){
                    replaceItemsAtJCMBWieth(jcmbDays, GUI_AY_Calendar.getDaysAsStrArrForMountOFYear(Integer.parseInt(jcmbMonths.getSelectedItem()+""), Integer.parseInt(jcmbYears.getSelectedItem()+"")));
//                    o("MONTH");
                }else if(e.getActionCommand().compareToIgnoreCase("DAY")==0){
//                    o("DAY");
                }
                setDateOutputTextFieldWithDateFromComboBoxes();
            }
        };
        return a;
    }
    private void o(Object o){
        System.out.println(o+"");
    }
    private void replaceItemsAtJCMBWieth(JComboBox<String> jcb, String[] items){
        jcb.removeAllItems();
        for(String i: items){
            jcb.addItem(i);
        }
    }

    public static void addAccountNickToComboBox(String acctNick){
        jcmbAccount.insertItemAt(acctNick, jcmbAccount.getItemCount()-1);
    }
    public void setALLDefault_UserHelpTexts(){
        setALLCustom_UserHelpTexts(manyalEntryDefaultJTextFieldText[0],  manyalEntryDefaultJTextFieldText[1],
                manyalEntryDefaultJTextFieldText[2], manyalEntryDefaultJTextFieldText[3], manyalEntryDefaultJTextFieldText[4],
                manyalEntryDefaultJTextFieldText[5],manyalEntryDefaultJTextFieldText[6]);

    }
    public void setALLCustom_UserHelpTexts(String accountCombo_helpText, String date_helpText, String refnum_helpText, String transName_helpText,
                                           String memo_helpText, String amount_helpText, String categoryCombo_helpText){
        setJTFCustom_Texts(date_helpText, refnum_helpText, transName_helpText, memo_helpText, amount_helpText);
        setJCMBs_Custom_UserHelpTexts(accountCombo_helpText, categoryCombo_helpText);
    }

    private void setJCMBs_Custom_UserHelpTexts(String accountComboHelpText, String categoryComboHelpText) {
        jcmbAccount.setSelectedItem(accountComboHelpText);
        jcmbCategory.setSelectedItem(categoryComboHelpText);

    }

    public void setJTFCustom_Texts( String date_helpText, String refnum_helpText, String TransName_helpText,
                                                  String memo_helpText, String amount_helpText){

        jtfDate.setText(date_helpText);
        jtfRefNum.setText(refnum_helpText);
        jtfTransName.setText(TransName_helpText);
        jtfMemo.setText(memo_helpText);
        jtfAmount.setText(amount_helpText);
    }
    public void setManualEntriesValues(String[] tmpManualEntries){
        String acctNick = tmpManualEntries[0];
        String date = tmpManualEntries[1];
        String refNum = tmpManualEntries[2];
        String transName = tmpManualEntries[3];
        String memo = tmpManualEntries[4];
        String amount = tmpManualEntries[5];
        String category = tmpManualEntries[6];
        setALLCustom_UserHelpTexts(acctNick, date, refNum, transName, memo, amount, category);
        setManualEntriesCalendarValues(date.split("/"));
    }
    public void setManualEntriesCalendarValues(String[] calArrYMD){
        jcmbYears.setSelectedItem(calArrYMD[2]);
        jcmbMonths.setSelectedItem(calArrYMD[0]);
        jcmbDays.setSelectedItem(calArrYMD[1]);
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == jbtnDone){
//           String accoutn = jcmbAccount.getSelectedItem().toString().trim();
//           String date= jtfDate.getText().trim();
//           String refN = jtfRefNum.getText().trim();
//           String descr = jtfTransName.getText().trim();
//           String memo = jtfMemo.getText().trim();
//           String amount = jtfAmount.getText().trim();
//           String  custom_category= jcmbCategory.getSelectedItem().toString().trim();
//
//            new ManualEntryProgrammableHandler(accoutn, date, refN,descr, memo, amount,custom_category);

            new ManualEntryProgrammableHandler(GUI_ManualEntryTemporaialHolder.getManuallYEnterredAccounts());
            GUI_ManualEntryTemporaialHolder.clearTmporalManulEntrylist();
            GUI_ManualEntryWindow.disposeManualEntryWindow();
            GUI_RecordsWindow.showRecordsWindow();

        }else  if(e.getSource() == jcmbAccount){
            if((jcmbAccount.getSelectedItem()+"").trim().compareToIgnoreCase("NEW")==0){
                new NewAccountWindow();
            }

        }else  if(e.getSource() == jbtnAnother){
            processAnotherClick();

        }else  if(e.getSource() == jbtnFirst){
            String[] firstManualEntryArr = GUI_ManualEntryTemporaialHolder.getFirst();
            setManualEntriesValues(firstManualEntryArr);
        }else  if(e.getSource() == jbtnPrev){
            String[] previousManualEntryArr =  GUI_ManualEntryTemporaialHolder.getPrev();
            setManualEntriesValues(previousManualEntryArr);
        }else  if(e.getSource() == jbtnNext){
            String[] nextManualEntryArr = GUI_ManualEntryTemporaialHolder.getNext();
            setManualEntriesValues(nextManualEntryArr);
        }else  if(e.getSource() == jbtnLast){
            String[] lastManualEntryArr =  GUI_ManualEntryTemporaialHolder.getLast();
            setManualEntriesValues(lastManualEntryArr);
        }
    }

    private void clearErrorBorders(){
        jcmbAccount.setBorder(new LineBorder(null, 0));
        jcmbCategory.setBorder(new LineBorder(null, 0));
    }
    private void processAnotherClick(){
        clearErrorBorders();
        String account = (jcmbAccount.getSelectedItem()+"").trim();
        String custom_category= (jcmbCategory.getSelectedItem()+"").trim();
        boolean err=false;
        if(account.compareToIgnoreCase(manyalEntryDefaultJTextFieldText[0])==0){
            jcmbAccount.setBorder(new LineBorder(Color.RED, 1));
            err = true;

        }
        if(custom_category.compareToIgnoreCase(manyalEntryDefaultJTextFieldText[manyalEntryDefaultJTextFieldText.length-1])==0){
            jcmbCategory.setBorder(new LineBorder(Color.RED, 1));
            err = true;
        }
        if(err){
            return;
        }
        String date= jtfOutputDate.getText().trim();
        String refN = jtfRefNum.getText().trim();
        String descr = jtfTransName.getText().trim();
        String memo = jtfMemo.getText().trim();
        String amount = jtfAmount.getText().trim();



        GUI_ManualEntryTemporaialHolder.addTempUserManualEntry(account, date, refN,descr, memo, amount,custom_category);
        setALLDefault_UserHelpTexts();
    }

    @Override
    public void focusGained(FocusEvent e) {
        ((JTextField)e.getSource()).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}


