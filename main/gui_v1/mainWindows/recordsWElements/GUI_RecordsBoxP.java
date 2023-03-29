package gui_v1.mainWindows.recordsWElements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ListIterator;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsDataLoader;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.mainWindows.GUI_ManualEntryWindow;
import gui_v1.mainWindows.GUI_NewCategoryWindow;
import gui_v1.mainWindows.GUI_RecordsWindow;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import main_logic.Request;
import main_logic.Result;

import static gui_v1.mainWindows.recordsWElements.RecordsTable.addRowToTable;
import static gui_v1.mainWindows.recordsWElements.RecordsTable.clearTable;

public class GUI_RecordsBoxP extends JPanel implements GUI_Settings_Variables, ActionListener {

	private static final long serialVersionUID = 1L;

//	private JLabel jlblRecordsMainTitle = new JLabel(strHeadTitle_GUIRecordsWindow, JLabel.CENTER);
//	private JLabel jlblRecordsSubTitle = new JLabel("Records Sub Title", JLabel.CENTER);
	private JLabel jlblRecordsMainTitle= GUI_ElementCreator.newHead(strHeadTitle_GUIRecordsWindow);
	private JLabel jlblRecordsSubTitle =  GUI_ElementCreator.newSubHead(strDefaultSubTitleString);
	private static JComboBox<String> jcmbCategory;
	private static JComboBox<String> jcmbAccount;
	private static JPanel jpRecordsActionControlsBoxP;
	private static JLabel acctLabel;
	private static JLabel catLabel;
	private static String[] columnNames = {"Date", "Ref", "Name", "Memo", "Amount", "OTHER"};
	private static String[][] testData = {{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"},
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"},
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"} ,
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"},
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"},
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"},
			{"Rec Num", "Explain", "Bank", "Acct #", "Amount", "OTHER"}};
	private static ActionListener a;
	private JTable table;

//	private static final JTable jtRecordsTable = GUI_ElementCreator.newJTable(testData, columnNames);
////	private static JTable jtRecordsTable = GUI_ElementCreator.newJTable();
//	public static JTable getTableView(){
//		return jtRecordsTable;
//	}
	public GUI_RecordsBoxP() {
		setLayout(new BorderLayout());

		JPanel jpRecordsTitleBoxP = new JPanel();
		JPanel jpRecordsDisplayBoxP = new JPanel();
		jpRecordsActionControlsBoxP = new JPanel();
		JPanel jpRecordsDisplayAndActionBoxP = new JPanel();

		jpRecordsTitleBoxP.setLayout(new GridLayout(2,1));
		jpRecordsTitleBoxP.add(jlblRecordsMainTitle);
		jpRecordsTitleBoxP.add(jpRecordsActionControlsBoxP);

		jpRecordsDisplayBoxP.setLayout(new BorderLayout());
		String[] nicksListTemp = GUI_ElementsOptionLists.getInstance().getAccountNicksList();
		String[] nicksList = new String[nicksListTemp.length-1];
		System.arraycopy(nicksListTemp, 0, nicksList, 0, nicksListTemp.length-1);
		jcmbAccount = GUI_ElementCreator.newJComboBox(nicksList);
		//jcmbAccount.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().acctNicksSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);
		jcmbCategory = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getTransCategoryist());
		jcmbCategory.insertItemAt(GUI_ElementsDataLoader.getMEntHelpMsgs().categoryOfAccntSelectionHelpMsg(), DEFAULT_SELECTED_ITEM);

		jpRecordsDisplayBoxP.add(new RecordsTable(), BorderLayout.CENTER);
		jpRecordsActionControlsBoxP.setLayout(new GridLayout(1,2));

		acctLabel = GUI_ElementCreator.newTextLabel("Account:");
		jpRecordsActionControlsBoxP.add(acctLabel);
		jcmbAccount.addActionListener(this);
		jcmbAccount.setSelectedItem(PEC.instance().getActiveAccount());
		jpRecordsActionControlsBoxP.add(jcmbAccount);

		catLabel = GUI_ElementCreator.newTextLabel("Category:");
		jpRecordsActionControlsBoxP.add(catLabel);
		jcmbCategory.setSelectedItem(PEC.OTHER_CATEGORY);
		jcmbCategory.addActionListener(this);
		jpRecordsActionControlsBoxP.add(jcmbCategory);
		jpRecordsTitleBoxP.add(jpRecordsActionControlsBoxP, BorderLayout.EAST);
		add(jpRecordsTitleBoxP, BorderLayout.EAST);

		add(jpRecordsTitleBoxP, BorderLayout.NORTH);
		add(jpRecordsDisplayBoxP, BorderLayout.CENTER);
		add(new RecordsNavigationButtonsP(), BorderLayout.SOUTH);
		Request r = Request.instance();
		table = r.getTableHolder();
		DefaultTableModel dm = (DefaultTableModel) table.getModel();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					PEC.instance().changeCategoryToActive((String) dm.getValueAt(row, 1));
					dm.setValueAt(PEC.instance().getActiveCategory(), row, 5);
				}
			}
		});
	}

	public void updateRecordWindowAcctMenu(String acctNick) {
		jpRecordsActionControlsBoxP.remove(jcmbCategory);
		jpRecordsActionControlsBoxP.remove(catLabel);
		jpRecordsActionControlsBoxP.remove(jcmbAccount);
		GUI_ElementsOptionLists.getInstance().addAccntNickToList(acctNick);
		jcmbAccount = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getAccountNicksList());
		jcmbAccount.removeItemAt(jcmbAccount.getItemCount()-1);
		jcmbAccount.setSelectedItem(acctNick);
		jpRecordsActionControlsBoxP.add(jcmbAccount);
		jcmbAccount.addActionListener(this);
		jpRecordsActionControlsBoxP.add(catLabel);
		jpRecordsActionControlsBoxP.add(jcmbCategory);
		jcmbCategory.addActionListener(this);
	}

	public void updateRecordWindowCatMenu(String category) {
		jpRecordsActionControlsBoxP.remove(jcmbCategory);
		GUI_ElementsOptionLists.getInstance().addTransactionCategoryToList(category);
		jcmbCategory = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getTransCategoryist());
		jcmbCategory.setSelectedItem(category);
		jpRecordsActionControlsBoxP.add(jcmbCategory);
		jcmbCategory.addActionListener(this);
	}

	private void processCategorySelection() {
		int selectedIndex =  jcmbCategory.getSelectedIndex();
		if ((jcmbCategory.getSelectedItem() + "").trim().
				compareToIgnoreCase(jcmbCategory.getItemAt(jcmbCategory.getItemCount()-1)) == 0) {
			GUI_NewCategoryWindow.getInstance().showNewCategoryFromRecordsWindow();
			GUI_RecordsWindow.getInstance().hideRecordsWindoww();
		} //else if ((jcmbCategory.getSelectedItem() + "").trim().
		//		compareToIgnoreCase(jcmbCategory.getItemAt(0)) == 0) {
		//	jcmbCategory.setSelectedItem(PEC.instance().getActiveCategory());}
		else {
			PEC.instance().setActiveCategory((String) jcmbCategory.getSelectedItem());
		}
	}

	private void processAccountSelection() {
		String selectedItem = (String) jcmbAccount.getSelectedItem();
		if (selectedItem.compareToIgnoreCase(PEC.instance().getActiveAccount())!=0) {
			ListIterator<Result> resIt = PEC.instance().switchActiveAccount(selectedItem);
			if (resIt.hasNext()) {
				clearTable();
				while (resIt.hasNext()) {
					Result res = resIt.next();
					addRowToTable(res.getTDate(), res.getTRef(), res.getTDesc(), res.getTMemo(), res.getTAmount(),
							res.getTCat());
				}
			}
		}
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jcmbCategory) {
			processCategorySelection();
		}
		if (e.getSource() == jcmbAccount) {
			processAccountSelection();
		}
	}
}
