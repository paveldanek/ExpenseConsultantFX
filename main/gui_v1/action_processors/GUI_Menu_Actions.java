package gui_v1.action_processors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import gui_v1.mainWindows.GUI_LogInWindow;
import gui_v1.mainWindows.GUI_SignUPWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.settings.GUI_Settings_Variables;

public class GUI_Menu_Actions extends MenuActionProgrammableHandle implements ActionListener, GUI_Settings_Variables{
	@Override
	public void actionPerformed(ActionEvent e) {

		if(gui_v1.settings.GUI_Static_Settings.workStage==0){
			developmentActions(e);
		}else{
			productionActions(e);
		}

	}

	private void productionActions(ActionEvent e) {
		MenuActionProgrammableHandle ac = new MenuActionProgrammableHandle();

		if (e.getActionCommand().compareToIgnoreCase("About")==0) {
			JOptionPane.showMessageDialog(null, ""+strAboutApp,"About", JOptionPane.INFORMATION_MESSAGE);
		}else if (e.getActionCommand().compareToIgnoreCase("Manual Entry")==0) {
			ac.doManualEntryProcessing();

		}else if (e.getActionCommand().compareToIgnoreCase("Import Account Activity")==0) {
			ac.doParsOFXFileProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("Get Advise")==0) {
			ac.doAdvisingProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("Generate Summary")==0) {
			ac.doGenerateSummaryProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("How To Start")==0) {
			ac.doHowToStartProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("Log Out and Exit")==0) {
			ac.dologOutProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("Settings")==0){
			ac.doSettingsProcessing();

		}
	}



	private void developmentActions(ActionEvent e) {
		MenuActionProgrammableHandle ac = new MenuActionProgrammableHandle();

		if (e.getActionCommand().compareToIgnoreCase("Exit")==0) {
			System.exit(0);
		}else if (e.getActionCommand().compareToIgnoreCase("About")==0) {
			JOptionPane.showMessageDialog(null, ""+strAboutApp,"About", JOptionPane.INFORMATION_MESSAGE);
		}else if (e.getActionCommand().compareToIgnoreCase("Show Records Window")==0) {
//			new GUI_RecordsWindow();
			GUI_RecordsWindow.getInstance().showRecordsWindow();
		}else if (e.getActionCommand().compareToIgnoreCase("Increse GUI Text Size (+)")==0) {
			increaseAllElementsFont();
		}else if (e.getActionCommand().compareToIgnoreCase("Decrese GUI Text Size (-)")==0) {
			decreaseAllElementsFont();
		}else if (e.getActionCommand().compareToIgnoreCase("Manual Entry")==0) {
			JOptionPane.showMessageDialog(null, "Manual Entry Menu","About", JOptionPane.INFORMATION_MESSAGE);
		}else if (e.getActionCommand().compareToIgnoreCase("Import Account Activity")==0) {
			ac.doParsOFXFileProcessing();
		}else if (e.getActionCommand().compareToIgnoreCase("Get Advise")==0) {
			JOptionPane.showMessageDialog(null, "Get Advise Menu","About", JOptionPane.INFORMATION_MESSAGE);

		}else if (e.getActionCommand().compareToIgnoreCase("Generate Summary")==0) {
			JOptionPane.showMessageDialog(null, "Generate Summary Menu","About", JOptionPane.INFORMATION_MESSAGE);

		}else if (e.getActionCommand().compareToIgnoreCase("How To Start")==0) {
			JOptionPane.showMessageDialog(null, "How To Start","About", JOptionPane.INFORMATION_MESSAGE);

		}else if (e.getActionCommand().compareToIgnoreCase("LogOut")==0) {
			JOptionPane.showMessageDialog(null, "Please Select Exit from File Menu","About", JOptionPane.INFORMATION_MESSAGE);

		}else if (e.getActionCommand().compareToIgnoreCase("Show SignIn Window")==0) {

			GUI_SignUPWindow.getInstance().showSignUpWindow();

		}else if (e.getActionCommand().compareToIgnoreCase("Show LogIn Window")==0) {
			GUI_LogInWindow.getInstance().showLogInWindow();

		}
	}


	@Override
	public Component getComponent() {
		return null;
	}
}
