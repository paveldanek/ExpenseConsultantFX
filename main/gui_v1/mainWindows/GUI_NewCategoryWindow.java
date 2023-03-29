package gui_v1.mainWindows;
import gui_v1.mainWindows.newCategoryWElements.GUI_NewCategoryP;
import gui_v1.settings.GUI_Settings_Variables;
import javax.swing.*;
import java.awt.*;

public class GUI_NewCategoryWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {
    public static final int ACCESS_FROM_MANUAL_ENTRY = 1;
    public static final int ACCESS_FROM_RECORDS_WINDOW = 2;
    private static GUI_NewCategoryWindow instance = null;
    private int pointOfEntry = 0;
    private GUI_NewCategoryWindow() {
        int width = 550;
        int height = 200;
        setTitle("New Category to Add");
        setSize(new Dimension(width, height));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
        if (pointOfEntry==ACCESS_FROM_MANUAL_ENTRY) addWindowListener(w4);
        else addWindowListener(w5);
        add(new GUI_NewCategoryP(), BorderLayout.CENTER);
    }
    public static GUI_NewCategoryWindow getInstance() {
        if (instance == null) {
            instance = new GUI_NewCategoryWindow();
        }
        return instance;
    }
    public int getPointOfEntry() {
        return pointOfEntry;
    }
    public void showNewCategoryFromManualEntryWindow() {
        pointOfEntry=ACCESS_FROM_MANUAL_ENTRY;
        instance.setVisible(true);
    }
    public void showNewCategoryFromRecordsWindow() {
        pointOfEntry=ACCESS_FROM_RECORDS_WINDOW;
        instance.setVisible(true);
    }
    public void hideNewCategoryWindow() {
        instance.setVisible(false);
    }
    public void disposeNewCategoryWindow() {
        instance.dispose();
//        instance =null;
    }
    @Override
    public Component getComponent() {
        return null;
    }
}