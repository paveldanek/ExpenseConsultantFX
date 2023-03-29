package gui_v1.mainWindows.recordsWElements;

import gui_v1.action_processors.RecordsNavigation_Actions;
import gui_v1.automation.GUI_ElementCreator;

import javax.swing.*;
import java.awt.*;

public class RecordsNavigationButtonsP extends JPanel{
    private JButton jbtnPrevious = GUI_ElementCreator.newRecordNavigationJButton("Previous");;
    private JButton  jbtnNext = GUI_ElementCreator.newRecordNavigationJButton("Next");
    private JButton  jbtnFirst = GUI_ElementCreator.newRecordNavigationJButton("First");
    private JButton jbtnLast = GUI_ElementCreator.newRecordNavigationJButton("Last");
//    private JButton jbtnNext;
//    private JButton jbtnFirst;
//    private JButton jbtnLast;
//    private JButton jbtnBack;
    RecordsNavigation_Actions a;

    public RecordsNavigationButtonsP(){
        setLayout(new GridLayout(1, 4));

//        jbtnPrevious = GUI_ElementCreator.newRecordNavigationJButton("Previous");
//        jbtnNext = GUI_ElementCreator.newRecordNavigationJButton("Next");
//        jbtnFirst = GUI_ElementCreator.newRecordNavigationJButton("First");
//        jbtnLast = GUI_ElementCreator.newRecordNavigationJButton("Last");

        a = new RecordsNavigation_Actions();
        jbtnPrevious.addActionListener(a);
        jbtnNext.addActionListener(a);
        jbtnFirst.addActionListener(a);
        jbtnLast.addActionListener(a);


        add(jbtnFirst);
        add(jbtnPrevious);
        add(jbtnNext);
        add(jbtnLast);

    }
}
