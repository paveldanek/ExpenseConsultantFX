package gui_v1.mainWindows;
import gui_v1.mainWindows.newCategoryWElements.GUI_NewCategoryP;
import gui_v1.settings.GUI_Settings_Variables;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;

public class GUI_NewCategoryWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables {
    private static GUI_NewCategoryWindow instance = null;
    public static int ACCESS_FROM_MANUAL_ENTRY = 0;
    public static int ACCESS_FROM_RECORDS_WINDOW = 1;
    private int pointOfEntry = ACCESS_FROM_RECORDS_WINDOW;
    private GUI_NewCategoryWindow() {
        int width = 550;
        int height = 200;
        setTitle("New Category to Add");
        setSize(new Dimension(width, height));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
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
        WindowListener[] wl = instance.getWindowListeners();
        if (wl.length>0) instance.removeWindowListener(wl[0]);
        instance.addWindowListener(w4);
        pointOfEntry = ACCESS_FROM_MANUAL_ENTRY;
        instance.setVisible(true);
    }

    public void showNewCategoryFromRecordsWindow() {
        WindowListener[] wl = instance.getWindowListeners();
        if (wl.length>0) instance.removeWindowListener(wl[0]);
        instance.addWindowListener(w5);
        pointOfEntry = ACCESS_FROM_RECORDS_WINDOW;
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