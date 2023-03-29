package gui_v1.action_processors;

import main_logic.PEC;
import main_logic.Result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;

import static gui_v1.mainWindows.recordsWElements.RecordsTable.addRowToTable;
import static gui_v1.mainWindows.recordsWElements.RecordsTable.clearTable;

public class RecordsNavigation_Actions implements ActionListener {

    ListIterator<Result> resIt;

    private void render() {
        if (resIt.hasNext()) {
            clearTable();
            while (resIt.hasNext()) {
                Result res = resIt.next();
                addRowToTable(res.getTDate(), res.getTRef(), res.getTDesc(), res.getTMemo(), res.getTAmount(),
                        res.getTCat());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getActionCommand().compareToIgnoreCase("First")==0) {
            resIt = PEC.instance().goFirst();
            render();
        }else if (a.getActionCommand().compareToIgnoreCase("Previous")==0) {
            resIt = PEC.instance().goPrevious();
            render();
        }else if (a.getActionCommand().compareToIgnoreCase("Next")==0) {
            resIt = PEC.instance().goNext();
            render();
        }else if (a.getActionCommand().compareToIgnoreCase("Last")==0) {
            resIt = PEC.instance().goLast();
            render();
        }
    }

}
