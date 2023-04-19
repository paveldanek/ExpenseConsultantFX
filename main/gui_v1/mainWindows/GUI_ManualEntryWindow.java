package gui_v1.mainWindows;

import gui_v1.settings.GUI_Settings_Variables;
import gui_v1.mainWindows.manualEntryWElements.GUI_ManualTransactionsEntryP;
import main_logic.PEC;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class GUI_ManualEntryWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {

    @Serial
    private static final long serialVersionUID = 1L;
    private static GUI_ManualEntryWindow instance = null;
    private GUI_ManualEntryWindow() {
        setTitle("Manual Transaction Entry");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setSize(manualEntryGUIWindowFrameSize);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new GUI_ManualTransactionsEntryP(), BorderLayout.CENTER);

        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w2);

    }
    @Override
    public Component getComponent() {
        return this;
    }
    public static GUI_ManualEntryWindow getInstance(){
        if(instance==null){
            instance = new GUI_ManualEntryWindow();
        }
        return instance;
    }
    public void showManualEntryWindow(){
//        createManualEntryWindow();
        instance.setVisible(true);
    }
    public void hideManualEntryWindow(){
//        createManualEntryWindow();
        instance.setVisible(false);
    }
    public void disposeManualEntryWindow(){
        instance.dispose();
        instance = null;
    }

}
