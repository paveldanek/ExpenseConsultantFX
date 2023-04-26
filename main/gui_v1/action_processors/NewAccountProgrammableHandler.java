package gui_v1.action_processors;

import javax.swing.*;

import gui_v1.data_loaders.GUI_ElementsOptionLists;
import main_logic.PEC;
import main_logic.ManualEntry;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import main_logic.Request;

public class NewAccountProgrammableHandler {
    private String strAcctNum;
    private String strAccntNick;
    private String strBank;
    public NewAccountProgrammableHandler(String _strAcctNum, String _strAccntNick, String _strBank){
        strAcctNum = _strAcctNum;
        strAccntNick = _strAccntNick;
        strBank = _strBank;
        String acctIdentifier = PEC.instance().createAcctIdentifier(_strAccntNick, _strAcctNum, _strBank);
        Request r = Request.instance();
        r.setAccountNumber(strAcctNum);
        r.setAccountNick(acctIdentifier);
        r.setBankName(strBank);
        if (PEC.instance().isTextInList(acctIdentifier,
                GUI_ElementsOptionLists.getInstance().getAccountNicksList())) {
            JOptionPane.showMessageDialog(null, "This account alredy exists.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            r.getManualEntryWindowHolder().setSelectedAccount(acctIdentifier);
        } else {
            //GUI_ManualEntryTemporaialHolder.getInstance().addAcctNickAsUnstored(acctIdentifier);
            GUI_ElementsOptionLists.getInstance().addAccntNickToList(acctIdentifier);
            PEC.instance().addBankToList(strBank);
            if (r.getManualEntryWindowHolder()!=null) {
                r.getManualEntryWindowHolder().addAccountNickToComboBox(acctIdentifier);
                ManualEntry.instance().changeManualEntryAccount(acctIdentifier);
            }
        }
    }
    private void showNewManualEntryInfo(){
        String regInfo = "User New Account Info:\nAccount # --> "+ strAcctNum+ "\nAccount NickName --> " + strAccntNick
                +"\nBank --> "+ strBank;


        JOptionPane.showMessageDialog(null, regInfo,  "New Account Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
