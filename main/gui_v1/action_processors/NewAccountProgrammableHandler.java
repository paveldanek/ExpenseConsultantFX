package gui_v1.action_processors;

import javax.swing.*;
import main_logic.PEC;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;

import static gui_v1.mainWindows.manualEntryWElements.GUI_ManualTransactionsEntryP.addAccountNickToComboBox;

public class NewAccountProgrammableHandler {
    private String strAcctNum;
    private String strAccntNick;
    private String strBank;
    public NewAccountProgrammableHandler(String _strAcctNum, String _strAccntNick, String _strBank){
        strAcctNum = _strAcctNum;
        strAccntNick = _strAccntNick;
        strBank = _strBank;
        GUI_ManualEntryTemporaialHolder.getInstance().
                addAcctNickAsUnstored(PEC.instance().createAcctIdentifier(_strAccntNick, _strAcctNum, _strBank));
        addAccountNickToComboBox(PEC.instance().createAcctIdentifier(_strAccntNick, _strAcctNum, _strBank));
        //showNewManualEntryInfo();
    }
    private void showNewManualEntryInfo(){
        String regInfo = "User New Account Info:\nAccount # --> "+ strAcctNum+ "\nAccount NickName --> " + strAccntNick
                +"\nBank --> "+ strBank;


        JOptionPane.showMessageDialog(null, regInfo,  "New Account Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
