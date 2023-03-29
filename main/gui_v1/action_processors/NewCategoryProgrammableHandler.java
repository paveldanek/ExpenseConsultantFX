package gui_v1.action_processors;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import gui_v1.mainWindows.newCategoryWElements.GUI_NewCategoryP;
import gui_v1.mainWindows.recordsWElements.GUI_RecordsBoxP;
import main_logic.PEC;

import javax.swing.*;

import gui_v1.mainWindows.manualEntryWElements.GUI_ManualTransactionsEntryP;
import main_logic.Request;


public class NewCategoryProgrammableHandler {
    private String strCategory;
    public NewCategoryProgrammableHandler( String _strCategory){
        strCategory = _strCategory;
        GUI_ManualTransactionsEntryP.addCategoryToComboBox(strCategory);
        //GUI_RecordsBoxP.addCategoryToComboBox(strCategory);
        GUI_ManualEntryTemporaialHolder.getInstance().addCategoryAsUnstored(strCategory);
        PEC.instance().addCategoryLocally(strCategory);
        Request r = Request.instance();
        r.getWindowHolder().updateRecordWindowCatMenu(PEC.instance().getActiveCategory());
        //showNewCategoryEntryInfo();
    }
    private void showNewCategoryEntryInfo(){
        String regInfo = "User New Category Info:\nCategory name --> " + strCategory;
        JOptionPane.showMessageDialog(null, regInfo,  "New Category Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
