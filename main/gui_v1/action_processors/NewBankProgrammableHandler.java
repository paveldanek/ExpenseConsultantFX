package gui_v1.action_processors;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import gui_v1.mainWindows.newAccountWElements.GUI_NewAccountP;

import javax.swing.*;



public class NewBankProgrammableHandler {
    private String strBank;
    public NewBankProgrammableHandler( String _strBank){
        strBank = _strBank;
        GUI_NewAccountP.addAccountNickToComboBox(strBank );
        GUI_ManualEntryTemporaialHolder.getInstance().addBankAsUnstored(strBank);

        //showNewBankEntryInfo();
    }
    private void showNewBankEntryInfo(){
        String regInfo = "User New Bank Info:\nBank name --> " + strBank;
        JOptionPane.showMessageDialog(null, regInfo,  "New Bank Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
