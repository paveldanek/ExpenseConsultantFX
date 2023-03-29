package gui_v1.mainWindows;
import gui_v1.mainWindows.newBankWElements.GUI_NewBankP;
import gui_v1.settings.GUI_Settings_Variables;
import javax.swing.*;
import java.awt.*;

public class GUI_NewBankWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {
    private static GUI_NewBankWindow instance = null;

    private GUI_NewBankWindow() {
        int width = 500;
        int height = 200;
        setTitle("New Bank to Add");
        setSize(new Dimension(width, height));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        addWindowListener(w3);
        add(new GUI_NewBankP(), BorderLayout.CENTER);
    }
    public static GUI_NewBankWindow getInstance() {
        if (instance == null) {
            instance = new GUI_NewBankWindow();
        }
        return instance;
    }
    public void showNewBankWindow() {
        instance.setVisible(true);
    }
    public void hideNewBankWindow() {
        instance.setVisible(false);
    }
    public void disposeNewBankWindow() {
        instance.dispose();
//        instance =null;
    }
    @Override
    public Component getComponent() {
        return null;
    }
}