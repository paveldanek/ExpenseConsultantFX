package gui_v1.mainWindows.manualEntryWElements;

import entities.Transaction;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsDataLoader;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.gui_logic.DateFormatter;
import gui_v1.mainWindows.GUI_ManualEntryWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.mainWindows.GUI_NewAccountWindow;
import gui_v1.mainWindows.GUI_NewCategoryWindow;
import gui_v1.mainWindows.recordsWElements.RecordsTable;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import main_logic.ManualEntry;
import main_logic.Request;
import main_logic.Result;
import net.sourceforge.jdatepicker.impl.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.ListIterator;

import static gui_v1.automation.GUI_ElementCreator.newFont;
//import java.util.Calendar;

public class GUI_ManualTransactionsEntryP extends JPanel implements GUI_Settings_Variables, ActionListener,
        FocusListener, WindowStateListener {
    private final static String[] ERRORS_IN_USER_INPUT = null;
    private final static int INPUT_ELEMENTS_ON_VIEW_COUNTER = GUI_ElementsDataLoader.getMEntHelpMsgs().
            numOfInputElementsManualEntryHas();
    private int[] listsSelectedItems;
    private JTextField jtfDate;
    private JTextField jtfOutputDate;
    private JTextField jtfRefNum;
    private JTextField jtfTransName;
    private JTextField jtfMemo;
    private JTextField jtfAmount;

    private static JComboBox<String> jcmbAccount = new JComboBox<>();
    private static JComboBox<String> jcmbCategory = new JComboBox<>();
    //    private String previousAcctSelection = infoTxtOfInputs[0];

    private JPanel userInputElementsBox;

    private JButton jbtnCancel;
    private JButton jbtnAnother;
    private JButton jbtnDone;

    private JButton jbtnFirst;
    private JButton jbtnPrev;
    private JButton jbtnNext;
    private JButton jbtnLast;

    private int position = 0;

    private UtilCalendarModel model;
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;

    private static Request req = Request.instance();

    private void init(){
        jcmbAccount = GUI_ElementCreator.newJComboBoxWithHidenHelp(GUI_ElementsOptionLists.getInstance().getAccountNicksList());
        jcmbAccount.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().acctNicksSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);
        jcmbCategory = GUI_ElementCreator.newJComboBoxWithHidenHelp(GUI_ElementsOptionLists.getInstance().getTransCategoryist());
        jcmbCategory.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().categoryOfAccntSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);
        listsSelectedItems = new int[2];
        if (PEC.instance().getActiveAccount().length()>0) {
            listsSelectedItems[0] = GUI_ElementsOptionLists.getInstance().
                    getAccountListPosition(PEC.instance().getActiveAccount());
        } else listsSelectedItems[0]=DEFAULT_SELECTED_ITEM;
        if (PEC.instance().getActiveCategory().length()>0) {
            listsSelectedItems[1] = GUI_ElementsOptionLists.getInstance().
                    getCategoryListPosition(PEC.instance().getActiveCategory());
        } else listsSelectedItems[1]=DEFAULT_SELECTED_ITEM;

        jtfDate = GUI_ElementCreator.newTextField(GUI_ElementsDataLoader.getMEntHelpMsgs().dateInputHelpMsg());
        jtfOutputDate = GUI_ElementCreator.newTextField(GUI_ElementsDataLoader.getMEntHelpMsgs().dateOutputHelpMsg());
        jtfRefNum = GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getMEntHelpMsgs().referenceInputHelpMsg());
        jtfTransName = GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getMEntHelpMsgs().transNameInputHelpMsg());
        jtfMemo = GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getMEntHelpMsgs().transMemoInputHelpMsg());
        jtfAmount = GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getMEntHelpMsgs().transAmountInputHelpMsg());

        jbtnFirst = GUI_ElementCreator.newJButton("<--First");
        jbtnFirst.setActionCommand("Nav");
        jbtnPrev = GUI_ElementCreator.newJButton("<-Prev");
        jbtnPrev.setActionCommand("Nav");
        jbtnNext = GUI_ElementCreator.newJButton("Next->");
        jbtnNext.setActionCommand("Nav");
        jbtnLast = GUI_ElementCreator.newJButton("Last-->");
        jbtnLast.setActionCommand("Nav");

        jbtnCancel = GUI_ElementCreator.newJButton("Cancel");
        jbtnAnother = GUI_ElementCreator.newJButton("Another");
        jbtnDone = GUI_ElementCreator.newJButton("Done");
    }

    public GUI_ManualTransactionsEntryP() {
        init();

        setLayout(new BorderLayout());
        String headingTitle = "Transaction Manual Entry";
        add(GUI_ElementCreator.newSubHead(headingTitle), BorderLayout.NORTH);
        JPanel mainBoxP = new JPanel(new BorderLayout());
        String manualEntryTitleMessage = "Enter Transaction Information";
        mainBoxP.add(GUI_ElementCreator.newTitle(manualEntryTitleMessage), BorderLayout.NORTH);
        userInputElementsBox = new JPanel(new GridLayout(7, 2));

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Account:"));
        jcmbAccount.addActionListener(this);
        userInputElementsBox.add(jcmbAccount);
        jcmbAccount.setSelectedItem(PEC.instance().getActiveAccount());

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel
                ("Pick a Date of Transaction (click \"...\"):"), BorderLayout.WEST);
        model = new UtilCalendarModel();
        datePanel = new JDatePanelImpl(model);
        datePicker = new JDatePickerImpl(datePanel, new DateFormatter());
        datePicker.setFont(newFont(datePicker.getFont(), txtSize_JTextField+1));
        datePicker.setForeground(clrF_JTextField);
        datePicker.setBackground(clrB_JTextField);
        model.setValue(Calendar.getInstance());
        model.setSelected(true);
        jtfOutputDate.setEditable(false);
        userInputElementsBox.add(datePicker, BorderLayout.EAST);
        datePanel.addActionListener(this);

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Reference Number:"));
        userInputElementsBox.add(jtfRefNum);
        jtfRefNum.addFocusListener(this);
        jtfRefNum.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Transaction Name (mandatory):"));
        userInputElementsBox.add(jtfTransName);
        jtfTransName.addFocusListener(this);
        jtfTransName.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));


        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Memo:"));
        userInputElementsBox.add(jtfMemo);
        jtfMemo.addFocusListener(this);
        jtfMemo.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Amount (mandatory):"));
        userInputElementsBox.add(jtfAmount);
        jtfAmount.addFocusListener(this);
        jtfAmount.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        userInputElementsBox.add(GUI_ElementCreator.newTextLabel("Category:"));
        jcmbCategory.setSelectedItem(PEC.instance().getActiveCategory());
        jcmbCategory.addActionListener(this);
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

        JPanel buttonsBox = new JPanel(new GridLayout(2, 4));
        buttonsBox.add(jbtnFirst);
        buttonsBox.add(jbtnPrev);
        buttonsBox.add(jbtnNext);
        buttonsBox.add(jbtnLast);
        buttonsBox.add(jbtnCancel);
        buttonsBox.add(jbtnAnother);
        buttonsBox.add(jbtnDone);
        add(buttonsBox, BorderLayout.SOUTH);
        req.setManualEntryWindowHolder(this);

        ManualEntry.instance().clearManualEntries();
        ManualEntry.instance().addManualEntry((String) jcmbAccount.getSelectedItem(),
                (Calendar) datePicker.getModel().getValue(), (String) jcmbCategory.getSelectedItem());
    }

    public void addAccountNickToComboBox(String acctNick) {
        JComboBox<String> oldAccount = jcmbAccount;
        if (!GUI_ElementsOptionLists.getInstance().isAccountExist(acctNick))
            GUI_ElementsOptionLists.getInstance().addAccntNickToList(acctNick);
        jcmbAccount = GUI_ElementCreator.newJComboBoxWithHidenHelp(GUI_ElementsOptionLists.getInstance().getAccountNicksList());
        if (jcmbAccount.getItemAt(DEFAULT_SELECTED_ITEM).
                compareToIgnoreCase(GUI_ElementsDataLoader.getMEntHelpMsgs().acctNicksSelectionHelpMsg())!=0)
            jcmbAccount.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().acctNicksSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);
        jcmbAccount.addActionListener(this);
        jcmbAccount.setSelectedItem(acctNick);
        userInputElementsBox.remove(oldAccount);
        userInputElementsBox.add(jcmbAccount, 1);
        //change all manual entry accounts to acctNick; if necessary, the whole new acct info is in Request
    }

    public void setSelectedAccount(String acctNick) {
        jcmbAccount.setSelectedItem(acctNick);
    }

    public void addCategoryToComboBox(String category) {
        JComboBox<String> oldCategory = jcmbCategory;
        if (!GUI_ElementsOptionLists.getInstance().isCategoryExist(category))
            GUI_ElementsOptionLists.getInstance().addTransactionCategoryToList(category);
        jcmbCategory = GUI_ElementCreator.newJComboBoxWithHidenHelp(GUI_ElementsOptionLists.getInstance().getTransCategoryist());
        if (jcmbCategory.getItemAt(DEFAULT_SELECTED_ITEM).
                compareToIgnoreCase(GUI_ElementsDataLoader.getMEntHelpMsgs().categoryOfAccntSelectionHelpMsg())!=0)
            jcmbCategory.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().categoryOfAccntSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);
        jcmbCategory.addActionListener(this);
        jcmbCategory.setSelectedItem(category);
        userInputElementsBox.remove(oldCategory);
        userInputElementsBox.add(jcmbCategory, 11);
        //add category to category list in logic permanently, do NOT change active category
    }

    public void setSelectedCategory(String category) {
        jcmbCategory.setSelectedItem(category);
    }

    private String[] getAllInputElementsData() {
        String account = (jcmbAccount.getSelectedItem() + "").trim();
        String custom_category = (jcmbCategory.getSelectedItem() + "").trim();
        String date = Transaction.returnYYYYMMDDFromCalendar(((Calendar) datePicker.getModel().getValue()));
        String refN = jtfRefNum.getText().trim();
        String descr = jtfTransName.getText().trim();
        String memo = jtfMemo.getText().trim();
        String amount = jtfAmount.getText().trim();
        return new String[]{account, date, refN, descr, memo, amount, custom_category};
    }

    private void processCategorySelection() {
        if (jcmbCategory.getSelectedIndex()==0) jcmbCategory.setSelectedIndex(listsSelectedItems[1]);
        else if ((jcmbCategory.getSelectedItem() + "").trim().compareToIgnoreCase
                (jcmbCategory.getItemAt(jcmbCategory.getItemCount()-1)) == 0) {
            jcmbCategory.setSelectedIndex(listsSelectedItems[1]);
            GUI_NewCategoryWindow.getInstance().showNewCategoryFromManualEntryWindow();
            GUI_ManualEntryWindow.getInstance().hideManualEntryWindow();
        } else listsSelectedItems[1] = jcmbCategory.getSelectedIndex();
    }

    private void processAccountSelection() {
        if (jcmbAccount.getSelectedIndex()==0) jcmbAccount.setSelectedIndex(listsSelectedItems[0]);
        else if ((jcmbAccount.getSelectedItem() + "").trim().compareToIgnoreCase
                (jcmbAccount.getItemAt(jcmbAccount.getItemCount()-1)) == 0) {
            jcmbAccount.setSelectedIndex(listsSelectedItems[0]);
            GUI_NewAccountWindow.getInstance().showNewAccntWindow();
            GUI_ManualEntryWindow.getInstance().hideManualEntryWindow();
        } else {
            listsSelectedItems[0] = jcmbAccount.getSelectedIndex();
            ManualEntry.instance().changeManualEntryAccount((String) jcmbAccount.getSelectedItem());
        }
    }

    private void processDateSelection() {
        Calendar selectedValue = (Calendar) datePicker.getModel().getValue();
        Calendar today = Calendar.getInstance();
        if (selectedValue==null || selectedValue.compareTo(today)>0) {
            model.setValue(Calendar.getInstance());
            model.setSelected(true);
        }
    }

    private void setNameErrorBorder() { jtfTransName.setBorder(new LineBorder(Color.RED, 1)); }

    private void setAmountErrorBorder() {
        jtfAmount.setBorder(new LineBorder(Color.RED, 1));
    }

    private void clearNameErrorBorder() {
        jtfTransName.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
    }

    private void clearAmountErrorBorder() {
        jtfAmount.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
    }

    private boolean isFormEmpty() {
        return (jtfRefNum.getText().trim().length()==0 && jtfTransName.getText().trim().length()==0 &&
                jtfMemo.getText().trim().length()==0 && jtfAmount.getText().trim().length()==0);
    }

    private boolean areFieldsValid() {
        boolean output = true;
        boolean amountError = false;
        double amount = 0.0;
        try {
            amount = Double.parseDouble(jtfAmount.getText().trim());
        } catch (NumberFormatException ex) {
            amountError = true;
        }
        if (jtfTransName.getText().trim().length()==0) {
            setNameErrorBorder();
            output = false;
        }
        else clearNameErrorBorder();
        if (jtfAmount.getText().trim().length()==0 || amountError) {
            setAmountErrorBorder();
            output = false;
        }
        else clearAmountErrorBorder();
        return output;
    }

    private boolean isManualEntryADouble() {
        int i = 0;
        String[] newEntry = new String[7];
        Result r = new Result();
        newEntry = getAllInputElementsData();
        if (position==0) i++;
        while (i < ManualEntry.instance().getManualEntrySize()) {
            r.reset();
            r = ManualEntry.instance().getManualEntry(i);
            if (newEntry[0].compareToIgnoreCase(r.getAccountNick())==0 &&
                    newEntry[1].compareToIgnoreCase(r.getTDate())==0 &&
                    newEntry[2].compareToIgnoreCase(r.getTRef())==0 &&
                    newEntry[3].compareToIgnoreCase(r.getTDesc())==0 &&
                    newEntry[4].compareToIgnoreCase(r.getTMemo())==0 &&
                    Double.parseDouble(newEntry[5])==r.getTAmount() &&
                    newEntry[6].compareToIgnoreCase(r.getTCat())==0)
                return true;
            i++; if (position==i) i++;
        }
        return false;
    }

    private boolean doesManualEntryExist(String[] entry) {
        Result r = new Result();
        for (int i=0; i<ManualEntry.instance().getManualEntrySize(); i++) {
            r.reset();
            r = ManualEntry.instance().getManualEntry(i);
            if (entry[0].compareToIgnoreCase(r.getAccountNick())==0 &&
                    entry[1].compareToIgnoreCase(r.getTDate())==0 &&
                    entry[2].compareToIgnoreCase(r.getTRef())==0 &&
                    entry[3].compareToIgnoreCase(r.getTDesc())==0 &&
                    entry[4].compareToIgnoreCase(r.getTMemo())==0 &&
                    Double.parseDouble(entry[5])==r.getTAmount() &&
                    entry[6].compareToIgnoreCase(r.getTCat())==0)
                return true;
        }
        return false;
    }

    private void setFieldsTo(Result r) {
        jcmbAccount.setSelectedItem(r.getAccountNick());
        model.setValue(Transaction.returnCalendarFromYYYYMMDD(r.getTDate()));
        jtfRefNum.setText(r.getTRef());
        jtfTransName.setText(r.getTDesc());
        jtfMemo.setText(r.getTMemo());
        if (r.getTAmount()==0.0) jtfAmount.setText("");
        else jtfAmount.setText(String.valueOf(r.getTAmount()));
        jcmbCategory.setSelectedItem(r.getTCat());
    }

    public void clearFields() {
        jtfRefNum.setText("");
        jtfTransName.setText("");
        jtfMemo.setText("");
        jtfAmount.setText("");
    }

    private boolean exitEntry() {
        // returns false if there are invalid fields and process can't move on
        if (isFormEmpty() && ManualEntry.instance().getManualEntrySize()>1) {
            ManualEntry.instance().deleteManualEntry(position);
            if (position>=ManualEntry.instance().getManualEntrySize())
                    position = ManualEntry.instance().getManualEntrySize()-1;
        } else {
            if (!areFieldsValid()) return false;
            if (isManualEntryADouble()) {
                JOptionPane.showMessageDialog(null, "You've already entered this transaction.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        if (!isFormEmpty()) {
            Request req = Request.instance();
            req.reset();
            String[] fields = new String[7];
            fields = getAllInputElementsData();
            req.setAccountNick(fields[0]);
            req.setTDate(fields[1]);
            req.setTRef(fields[2]);
            req.setTDesc(fields[3]);
            req.setTMemo(fields[4]);
            req.setTAmount(Double.parseDouble(fields[5]));
            req.setTCat(fields[6]);
            ManualEntry.instance().editManualEntry(position, req);
        }
        return true;
    }

    private void processAnotherClick() {
        if (!exitEntry()) return;
        clearFields();
        ManualEntry.instance().addManualEntry((String) jcmbAccount.getSelectedItem(),
                (Calendar) datePicker.getModel().getValue(), (String) jcmbCategory.getSelectedItem());
        position = ManualEntry.instance().getManualEntrySize()-1;
    }

    private void processDoneBtnClick(){
        if (!exitEntry()) return;
        boolean success = PEC.instance().processManualEntries();
        if (success) {
            ListIterator<Result> it = PEC.instance().returnRListIterator();
            Result result = new Result();
            RecordsTable.clearTable();
            while (it.hasNext()) {
                result = it.next();
                RecordsTable.addRowToTable(result.getTDate(),
                        result.getTRef(), result.getTDesc(),
                        result.getTMemo(), result.getTAmount(), result.getTCat());
            }
        } else {
            JOptionPane.showMessageDialog(null, "No transactions could be added.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        PEC.instance().setActiveCategory((String) jcmbCategory.getSelectedItem());
        Request.instance().getMainWindowHolder().setSelectedCategory((String) jcmbCategory.getSelectedItem());
        Request.instance().getMainWindowHolder().updateRecordWindowAcctMenu(PEC.instance().getActiveAccount());
        position = 0;
        clearFields();
        ManualEntry.instance().clearManualEntries();
        GUI_ManualEntryWindow.getInstance().disposeManualEntryWindow();
        GUI_RecordsWindow.getInstance().showRecordsWindow();
    }

    private void processFirstBtnClick() {
        if (position==0) return;
        if (!exitEntry()) return;
        position = 0;
        Result r = ManualEntry.instance().getManualEntry(position);
        setFieldsTo(r);
    }

    private void processPrevBtnClick(){
        if (position==0) return;
        boolean dontMove = false;
        if (position==ManualEntry.instance().getManualEntrySize()-1 && isFormEmpty()) dontMove = true;
        if (!exitEntry()) return;
        if (!dontMove) position--;
        Result r = ManualEntry.instance().getManualEntry(position);
        setFieldsTo(r);
    }

    private void processNextBtnClick(){
        if (position==ManualEntry.instance().getManualEntrySize()-1) return;
        boolean dontMove = false;
        if (isFormEmpty()) dontMove = true;
        if (!exitEntry()) return;
        if (!dontMove) position++;
        if (position>=ManualEntry.instance().getManualEntrySize()) position = ManualEntry.instance().getManualEntrySize()-1;
        Result r = ManualEntry.instance().getManualEntry(position);
        setFieldsTo(r);
    }

    private void processLastBtnClick(){
        if (position==ManualEntry.instance().getManualEntrySize()-1) return;
        if (!exitEntry()) return;
        position = ManualEntry.instance().getManualEntrySize()-1;
        Result r = ManualEntry.instance().getManualEntry(position);
        setFieldsTo(r);
    }

    private void processCancelBtnClick() {
        int answr = JOptionPane.showOptionDialog(null, "Do you want to cancel manual entry"+
                        "\n and discard all input entries?", "Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
        if (answr==JOptionPane.YES_OPTION) {
            ManualEntry.instance().clearManualEntries();
            GUI_ManualEntryWindow.getInstance().disposeManualEntryWindow();
            GUI_RecordsWindow.getInstance().showRecordsWindow();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //testingOutput(e);
        if (e.getSource() == jbtnDone) {
            processDoneBtnClick();
        }else if (e.getSource() == jbtnAnother) {
            processAnotherClick();
        }  else if (e.getSource() == jbtnFirst) {
            processFirstBtnClick();
        } else if (e.getSource() == jbtnPrev) {
            processPrevBtnClick();
        } else if (e.getSource() == jbtnNext) {
            processNextBtnClick();
        } else if (e.getSource() == jbtnLast) {
            processLastBtnClick();
        } else if (e.getSource() == jbtnCancel) {
            processCancelBtnClick();
        } else if (e.getSource() == jcmbAccount) {
            processAccountSelection();
        } else if (e.getSource() == jcmbCategory) {
            processCategorySelection();
        } else if (e.getSource() == datePanel) {
            processDateSelection();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        ((JTextField) e.getSource()).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        jcmbAccount.setSelectedIndex(listsSelectedItems[0]);
        jcmbAccount.setSelectedIndex(listsSelectedItems[1]);

    }
}
