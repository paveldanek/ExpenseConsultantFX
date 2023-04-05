package gui_v1.mainWindows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serial;
import java.sql.SQLException;
import javax.swing.*;

import gui_v1.data_loaders.GUI_ElementsDataLoader;
import gui_v1.mainWindows.recordsWElements.GUI_RecordsBoxP;
import gui_v1.menu.GUI_Menu;
import gui_v1.menu.GUI_Menu_Technical;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.Request;

public class GUI_RecordsWindow extends JFrame implements GUI_MainWidowsSharedBehaviors, GUI_Settings_Variables{
	@Serial
	private static final long serialVersionUID = 1L;
	private static GUI_RecordsWindow instance = null;
	private GUI_RecordsBoxP rbp;
	private GUI_RecordsWindow() {
		try {
			GUI_ElementsDataLoader.loadDataInitializeGUI();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		setBackground(guiFramesBackgroundColor);
//		setForeground(guiFramesForegroundColor);
		setTitle(recordsGUIWindowTitle);

		if(gui_v1.settings.GUI_Static_Settings.workStage==1){
			setJMenuBar(new GUI_Menu());
		}else{
			setJMenuBar(new GUI_Menu_Technical());
		}
		setSize(recordsGUIWindowFrameSize);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		rbp = new GUI_RecordsBoxP();
		Request r = Request.instance();
		r.setMainWindowHolder(rbp);
		add(rbp, BorderLayout.CENTER);
		add(new JLabel(strCopyRigts, JLabel.CENTER), BorderLayout.SOUTH);
		addWindowListener(mainW);
	}
	public static GUI_RecordsWindow getInstance(){
		if(instance==null){
			instance = new GUI_RecordsWindow();
		}
		return instance;
	}

	public void showRecordsWindow(){
		instance.setVisible(true);
	}
	public void hideRecordsWindoww(){
		instance.setVisible(false);
	}
	@Override
	public Component getComponent() {
		return this;
	}

}
