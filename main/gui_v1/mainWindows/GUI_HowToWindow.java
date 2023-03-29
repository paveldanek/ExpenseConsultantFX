package gui_v1.mainWindows;

import gui_v1.automation.GUI_ElementCreator;
import gui_v1.settings.GUI_Settings_Variables;
import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class GUI_HowToWindow  extends JFrame implements GUI_Settings_Variables, GUI_MainWidowsSharedBehaviors {
    @Serial
    private static final long serialVersionUID = 1L;
    private static GUI_HowToWindow instance = null;
    private final ImageIcon howToImage = new ImageIcon("HowToPoster.jpg");

    private GUI_HowToWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(howToWindowFrameSize);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("To resize, hold down lower-right corner of window and move it around. "),
                BorderLayout.NORTH);
        add(new JScrollPane(new JImagePanel()), BorderLayout.CENTER);
        /*
        JButton jbtnClose = GUI_ElementCreator.newJButton("Click Here To Close This Window");
        jbtnClose.addActionListener(e -> disposeHowToWindow());
        add(jbtnClose, BorderLayout.SOUTH);
        addWindowListener(w2);
        */
    }
    public static GUI_HowToWindow getInstance() {
        if (instance == null) {
            instance = new GUI_HowToWindow();
        }
        return instance;
    }

    public void showHowToWindow() {
        instance.setVisible(true);
    }

    public void hideHowToWindow() {
        instance.setVisible(false);
    }

    public void disposeHowToWindow() {
        if(instance!=null){
            instance.dispose();
        }
    }
    private class JImagePanel extends JPanel {
        ImageIcon scaledHowToImage = getResizedPictureIconForFrame(howToImage);
        Image backGround;
        public JImagePanel() {
            backGround = scaledHowToImage.getImage();
        }
        public void paintComponent(Graphics g) {
            scaledHowToImage = getResizedPictureIconForFrame(howToImage);
            backGround = scaledHowToImage.getImage();
            g.drawImage(backGround, 0, 0, null);
            setPreferredSize(new Dimension(scaledHowToImage.getIconWidth(), scaledHowToImage.getIconHeight()));
        }
    }
    private ImageIcon getResizedPictureIconForFrame(ImageIcon _howToImage) {
        Image img = _howToImage.getImage();
        double proportionFactorW = (double) (getWidth() - 40) / (double) img.getWidth(_howToImage.getImageObserver());
//        double proportionFactorH = (double)getHeight()/ (double) img.getHeight(_howToImage.getImageObserver());
        int newWidth = (int) ((img.getWidth(_howToImage.getImageObserver())) * proportionFactorW);
        int newHeight = (int) ((img.getHeight(_howToImage.getImageObserver())) * proportionFactorW);
        return new ImageIcon(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
    }
    @Override
    public Component getComponent() {
        return null;
    }

}
