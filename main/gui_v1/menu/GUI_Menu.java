package gui_v1.menu;


import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import gui_v1.action_processors.GUI_Menu_Actions;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.settings.GUI_Settings_Variables;

import java.io.Serial;

public class GUI_Menu extends JMenuBar implements GUI_Settings_Variables{
	@Serial
	private static final long serialVersionUID = 1L;


	public GUI_Menu(){
		if(gui_v1.settings.GUI_Static_Settings.workStage==0){
			setBackground(clrB_JMenuBar);
		}else{
			setForeground(clrF_JMenuBar);
		}
		final GUI_Menu_Actions a;

		setFont(GUI_ElementCreator.newFont(this.getFont(), txtSize_JMenuBar));
		a = new GUI_Menu_Actions();

		JMenu jmVMenu = GUI_ElementCreator.newJMenu("Menu");
		jmVMenu.setMnemonic('M');

		JMenuItem jmiHowStart = GUI_ElementCreator.newJMenuItem("How To Start");
		jmiHowStart.setMnemonic('S');
		jmiHowStart.addActionListener(a);
		jmVMenu.add(jmiHowStart);

		JMenuItem jmiParseOFX = GUI_ElementCreator.newJMenuItem("Import Account Activity");
		jmiParseOFX.setMnemonic('P');
		jmiParseOFX.addActionListener(a);
		jmVMenu.add(jmiParseOFX);

		JMenuItem jmiAddDataManually = GUI_ElementCreator.newJMenuItem("Manual Entry");
		jmiAddDataManually.setMnemonic('M');
		jmiAddDataManually.addActionListener(a);
		jmVMenu.add(jmiAddDataManually);

		JMenuItem jmiSummery = GUI_ElementCreator.newJMenuItem("Generate Summary");
		jmiSummery.setMnemonic('S');
		jmiSummery.addActionListener(a);
		jmVMenu.add(jmiSummery);

		JMenuItem jmiAdvising = GUI_ElementCreator.newJMenuItem("Get Advise");
		jmiAdvising.setMnemonic('G');
		jmiAdvising.addActionListener(a);
		jmVMenu.add(jmiAdvising);

		JMenu jmiSettings = GUI_ElementCreator.newJMenu("Settings");
		jmiSettings.setMnemonic('S');
		jmiSettings.addActionListener(a);
		JMenuItem jmiChangePassword = GUI_ElementCreator.newJMenuItem("Change Password");
		jmiChangePassword.setMnemonic('E');
		jmiChangePassword.addActionListener(a);
		jmiSettings.add(jmiChangePassword);
		JMenuItem jmiCloseAccount = GUI_ElementCreator.newJMenuItem("Close Account");
		jmiCloseAccount.setMnemonic('C');
		jmiCloseAccount.addActionListener(a);
		jmiSettings.add(jmiCloseAccount);
		jmVMenu.add(jmiSettings);

		JMenuItem jmiAbout = GUI_ElementCreator.newJMenuItem("About");
		jmiAbout.setMnemonic('A');
		jmiAbout.addActionListener(a);
		jmVMenu.add(jmiAbout);

		JMenuItem jmiLogOuut = GUI_ElementCreator.newJMenuItem("Log Out and Exit" );
		jmiLogOuut.addActionListener(a);
		jmiLogOuut.setMnemonic('O');
		jmVMenu.add(jmiLogOuut);

		add(jmVMenu);

	}
}
