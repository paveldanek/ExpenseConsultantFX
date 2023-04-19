package gui_v1.mainWindows;

import gui_v1.mainWindows.summaryWElements.GUI_SummaryP;
import gui_v1.settings.GUI_Settings_Variables;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class GUI_SummaryWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {

    @Serial
    private static final long serialVersionUID = 1L;
    public GUI_SummaryWindow(String acctNick, String from, String to) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Summary");
        setSize(summaryWindowFrameSize);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new GUI_SummaryP(acctNick, from, to), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        setVisible(true);
    }
    @Override
    public Component getComponent() {
        return this;
    }

}
