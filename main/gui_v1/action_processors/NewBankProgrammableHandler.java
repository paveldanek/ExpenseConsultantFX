package gui_v1.action_processors;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import gui_v1.mainWindows.newAccountWElements.GUI_NewAccountP;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;



public class NewBankProgrammableHandler {
    private String strBank;
    public NewBankProgrammableHandler( String _strBank){
        strBank = _strBank;
        Request r = Request.instance();
        if (PEC.instance().isTextInList(strBank,
                GUI_ElementsOptionLists.getInstance().getBanksList())) {
            JOptionPane.showMessageDialog(null, "This bank alredy exists.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            r.getNewAccountWindowHolder().setSelectedBank(strBank);
        } else {
            GUI_ElementsOptionLists.getInstance().addBankToList(strBank);
            r.getNewAccountWindowHolder().addBankToComboBox(strBank);
            //GUI_ManualEntryTemporaialHolder.getInstance().addBankAsUnstored(strBank);
            PEC.instance().addNewBankToTempList(strBank);
        }
        //showNewBankEntryInfo();
    }
    private void showNewBankEntryInfo(){
        String regInfo = "User New Bank Info:\nBank name --> " + strBank;
        JOptionPane.showMessageDialog(null, regInfo,  "New Bank Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
