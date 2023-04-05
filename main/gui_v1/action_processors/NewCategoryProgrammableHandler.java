package gui_v1.action_processors;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import main_logic.PEC;

import javax.swing.*;

import main_logic.Request;


public class NewCategoryProgrammableHandler {
    private String strCategory;
    public NewCategoryProgrammableHandler( String _strCategory){
        strCategory = _strCategory;
        Request r = Request.instance();
        if (PEC.instance().isTextInList(strCategory,
                GUI_ElementsOptionLists.getInstance().getTransCategoryist())) {
            JOptionPane.showMessageDialog(null, "This category alredy exists.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            r.getManualEntryWindowHolder().setSelectedAccount(strCategory);
        } else {
            if (r.getManualEntryWindowHolder() != null)
                r.getManualEntryWindowHolder().addCategoryToComboBox(strCategory);
            GUI_ManualEntryTemporaialHolder.getInstance().addCategoryAsUnstored(strCategory);
            PEC.instance().addCategoryLocally(strCategory);
            r.getMainWindowHolder().updateRecordWindowCatMenu(PEC.instance().getActiveCategory());
            //showNewCategoryEntryInfo();
        }
    }
    private void showNewCategoryEntryInfo(){
        String regInfo = "User New Category Info:\nCategory name --> " + strCategory;
        JOptionPane.showMessageDialog(null, regInfo,  "New Category Data", JOptionPane.INFORMATION_MESSAGE);

    }
}
