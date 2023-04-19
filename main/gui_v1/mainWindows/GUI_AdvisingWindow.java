package gui_v1.mainWindows;

import gui_v1.mainWindows.advisingWElements.GUI_AdvisingP;
import gui_v1.settings.GUI_Settings_Variables;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class GUI_AdvisingWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {

    @Serial
    private static final long serialVersionUID = 1L;
    private static GUI_AdvisingWindow instance = null;

    private GUI_AdvisingWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Analysis");
        setSize(advisingWindowFrameSize);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new GUI_AdvisingP(), BorderLayout.CENTER);
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w9);
        setVisible(true);
    }

    public static GUI_AdvisingWindow getInstance(){
        if(instance==null){
            instance = new GUI_AdvisingWindow();
        }
        return instance;
    }

    public void showAdvisingWindow(){
        instance.setVisible(true);
    }

    public void hideAdvisingWindow(){
        instance.setVisible(false);
    }

    public void disposeAdvisingWindow(){
        instance.dispose();
        instance = null;
    }

    @Override
    public Component getComponent() {
        return this;
    }

}
