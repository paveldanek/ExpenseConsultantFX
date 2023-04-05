package gui_v1.mainWindows.newAccountWElements;

import gui_v1.action_processors.NewAccountProgrammableHandler;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsDataLoader;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.mainWindows.GUI_ManualEntryWindow;
import gui_v1.mainWindows.GUI_NewAccountWindow;
import gui_v1.mainWindows.GUI_NewBankWindow;
import gui_v1.mainWindows.GUI_NewCategoryWindow;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

public class GUI_NewAccountP extends JPanel implements GUI_Settings_Variables, ActionListener, WindowStateListener {
    private final static String[] ERRORS_IN_USER_INPUT = null;
    private static int selectedItem;


    private static JComboBox<String> jcmbBank;
    private JTextField jtfAcctNum;
    private JTextField jtfAcctNick;
    private JButton jbtnAdd;
    private JPanel pBox;

    private void init(){
        jcmbBank = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getBanksList());
        jcmbBank.insertItemAt(GUI_ElementsDataLoader.getNAHelpMsgs().bankSelectionHelpMsg(),DEFAULT_SELECTED_ITEM);
        selectedItem = DEFAULT_SELECTED_ITEM;
        jtfAcctNum= GUI_ElementCreator.newTextFieldWithHelp("");
        jtfAcctNick= GUI_ElementCreator.newTextFieldWithHelp("");
        //jtfAcctNum= GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getNAHelpMsgs().accontInputHelpMsg());
        //jtfAcctNick= GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getNAHelpMsgs().nicknameInputHelpMsg());
        jbtnAdd = GUI_ElementCreator.newJButton("Add This Account");
    }

    public GUI_NewAccountP(){
        init();

        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Enter New Account Info"), BorderLayout.NORTH);
        pBox = new JPanel(new GridLayout(3,2));
        pBox.add(GUI_ElementCreator.newTextLabel("Account #:"));
        pBox.add(jtfAcctNum);
        pBox.add(GUI_ElementCreator.newTextLabel("Account Nick:"));
        pBox.add(jtfAcctNick);
        pBox.add(GUI_ElementCreator.newTextLabel("Bank:"));
        jcmbBank.addActionListener(this);
        jcmbBank.setSelectedIndex(DEFAULT_SELECTED_ITEM);
        pBox.add(jcmbBank);
        add(pBox, BorderLayout.CENTER);
        jbtnAdd.addActionListener(this);
        add(jbtnAdd, BorderLayout.SOUTH);
        pBox.requestFocusInWindow();
        jbtnAdd.setFocusTraversalPolicyProvider(true);
//        pBox.setRequestFocusEnabled(true);
        Request r = Request.instance();
        r.setNewAccountWindowHolder(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== jbtnAdd){
            processAddBtnClick();
        }else  if(e.getSource() == jcmbBank){
            processBankSelection();
        }
    }
    private void processBankSelection() {
        if (jcmbBank.getSelectedIndex()==0) jcmbBank.setSelectedIndex(selectedItem);
        else if((jcmbBank.getSelectedItem()+"").trim().
                compareToIgnoreCase(jcmbBank.getItemAt(jcmbBank.getItemCount()-1))==0) {
            jcmbBank.setSelectedIndex(selectedItem);
            GUI_NewBankWindow.getInstance().showNewBankWindow();
            GUI_NewAccountWindow.getInstance().hideNewAccntWindow();
        } else selectedItem = jcmbBank.getSelectedIndex();
    }

    private void processAddBtnClick() {
        /*
        String msg = "Do you really want to save this account:";
        msg+="\n";
        msg+="Account #: "+ jtfAcctNum.getText().trim();
        msg+="\n";
        msg+="Account Nick:"+ jtfAcctNick.getText().trim();
        msg+="\n";
        msg+="Bank of Account:"+ (jcmbBank.getSelectedItem()+"").trim();
        msg+="\n";
        msg+="After Clicking Yes button this account will be added to your accounts";
        int answr = JOptionPane.showOptionDialog(null, msg, "Adding and Storing Account!",
                JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
        if(answr == JOptionPane.OK_OPTION){
        */
            String bank = (jcmbBank.getSelectedItem()+"").trim();
            if (bank.compareToIgnoreCase(GUI_ElementsDataLoader.newAccountElements_HelpMessages[2])==0) { bank = ""; }
            new NewAccountProgrammableHandler(jtfAcctNum.getText().trim(), jtfAcctNick.getText().trim(), bank);
            clearFields();
            GUI_NewAccountWindow.getInstance().hideNewAccntWindow();
            GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
          /* } */
    }

    public void clearFields() {
        jtfAcctNum.setText("");
        jtfAcctNick.setText("");
    }

    public void addBankToComboBox(String bank) {
        JComboBox<String> oldBank = jcmbBank;
        if (!GUI_ElementsOptionLists.getInstance().isBankExist(bank))
            GUI_ElementsOptionLists.getInstance().addBankToList(bank);
        jcmbBank = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getBanksList());
        jcmbBank.addActionListener(this);
        jcmbBank.setSelectedItem(bank);
        pBox.remove(oldBank);
        pBox.add(jcmbBank, 5);
    }

    public void setSelectedBank(String bank) {
        jcmbBank.setSelectedItem(bank);
    }

    public void setDefaultSelectedBank() { jcmbBank.setSelectedIndex(DEFAULT_SELECTED_ITEM); }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
    }
}
