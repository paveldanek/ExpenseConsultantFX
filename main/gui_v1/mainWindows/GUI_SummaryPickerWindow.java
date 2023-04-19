package gui_v1.mainWindows;

import gui_v1.mainWindows.summaryWElements.GUI_SummaryPickerP;
import gui_v1.settings.GUI_Settings_Variables;

import javax.swing.*;
import java.awt.*;


public class GUI_SummaryPickerWindow extends JFrame  implements GUI_Settings_Variables {
    private static GUI_SummaryPickerWindow instance = null;
    private GUI_SummaryPickerWindow() {
        setTitle("Summary");
        setSize(new Dimension(550, 350));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new GUI_SummaryPickerP(), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        setVisible(true);
    }
    public static GUI_SummaryPickerWindow getInstance(){
        if(instance==null){
            instance = new GUI_SummaryPickerWindow();
        }
        return instance;
    }
    public void showSummaryPickerWindow(){
        instance.setVisible(true);
    }
    public void hideSummaryPickerWindow(){
        instance.setVisible(false);
    }
    public void disposeSummaryPickerWindow(){
        instance.dispose();
        instance = null;
    }

    @Override
    public Component getComponent() {
        return null;
    }
}
