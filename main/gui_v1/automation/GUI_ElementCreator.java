package gui_v1.automation;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import gui_v1.settings.GUI_Settings_Variables;
public final class GUI_ElementCreator implements GUI_Settings_Variables{



	public static Font newFont(Font f, int fSize) {
		return new Font(f.getName(),f.getStyle(),  fSize);
	}

	public static JRadioButton newRadioButton(String  msg) {
		JRadioButton  rbtn = new JRadioButton(msg);
		rbtn.setFont(newFont(rbtn.getFont(), txtSize_JRadioButton));
		rbtn.setForeground(clrF_JRadioButton);
		rbtn.setBackground(clrB_JRadioButton);
		return rbtn;
	}
	public static JTextField newTextField() {
		JTextField  jtf = new JTextField();
		jtf.setFont(newFont(jtf.getFont(), txtSize_JTextField+1));
		jtf.setForeground(clrF_JTextField);
		jtf.setBackground(clrB_JTextField);
		return jtf;
	}
	public static JPasswordField newPasswordField() {
		JPasswordField  jtf = new JPasswordField();
		jtf.setFont(newFont(jtf.getFont(), txtSize_JTextField+1));
		jtf.setForeground(clrF_JTextField);
		jtf.setBackground(clrB_JTextField);
		return jtf;
	}
	public static JComboBox<String> newJComboBox(String[]  s){
		JComboBox<String>  jcmmo = new JComboBox<String>(s);
		jcmmo.setFont(newFont(jcmmo.getFont(), txtSize_JTextField+1));
		return jcmmo;
	}
	public static JComboBox<String> newJComboBoxWithHidenHelp(String[]  s){
		JComboBox<String>  jcmmo = new JComboBox<String>(s);
		jcmmo.setFont(newFont(jcmmo.getFont(), txtSize_JTextField+1));
		if (s.length>0) jcmmo.setToolTipText(s[0]);
		jcmmo.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {

			}
			@Override
			public void focusLost(FocusEvent e) {

			}
		});
		return jcmmo;
	}
	public static JTextField newTextField(String s) {
		JTextField  jtf = new JTextField();
		jtf.setFont(newFont(jtf.getFont(), txtSize_JTextField+1));
		jtf.setForeground(clrF_JTextField);
		jtf.setBackground(clrB_JTextField);
		jtf.setText(s);
		jtf.selectAll();
		return jtf;
	}
	public static JTextField newTextFieldWithHelNpAndNoSelection(String s){
		return newTextFieldWithHelp(s, false);
	}
	public static JTextField newTextFieldWithHelp(String s) {
		return newTextFieldWithHelp(s, true);
	}
	public static JTextField newTextFieldWithHelp(String s, boolean withTextSelected) {
		JTextField  jtf = new JTextField();
		jtf.setFont(newFont(jtf.getFont(), txtSize_JTextField+1));
		jtf.setForeground(clrF_JTextField);
		jtf.setBackground(clrB_JTextField);
		jtf.setText(s);
		jtf.setToolTipText(s);
		jtf.selectAll();
		jtf.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(jtf.getText().trim().compareToIgnoreCase(s)==0){
					jtf.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(jtf.getText().trim().compareToIgnoreCase("")==0){
					jtf.setText(s);
				}
			}
		});
		return jtf;
	}

	public static JTextArea newJTextArea() {
		JTextArea  jta = new JTextArea();
		jta.setFont(newFont(jta.getFont(), txtSize_JTextArea));
		jta.setForeground(clrF_JTextArea);
		jta.setBackground(clrB_JTextArea);
		return jta;
	}
	public static JTextField newOutputTextField(String s) {
		JTextField  jtf = new JTextField();
		jtf.setFont(newFont(jtf.getFont(), txtSize_JTextFieldOutput));
		jtf.setForeground(clrF_JTextField);
//		jtf.setBackground(clrB_JTextField);
		jtf.setBorder(new LineBorder(Color.RED, 1));
		jtf.setHorizontalAlignment(JTextField.CENTER);
		jtf.setText(s);
		jtf.selectAll();
		return jtf;
	}
	public static JLabel newOutputTextFieldLabel(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.LEFT);
		lbl.setFont(newFont(lbl.getFont(), txtSize_JTextFieldOutput-2));
		lbl.setFont(new Font(lbl.getFont().getName(),Font.PLAIN, txtSize_JTextFieldOutput-2));
		lbl.setForeground(clrF_Action_Selectors);
//		lbl.setBackground(clrB_Title);
		return lbl;
	}
	private static String checkTitleMsg(String msg) {
		if(msg!=null) {
			return msg;
		}else {
			return strDefaultString;
		}
	}
	public static JLabel newCopyRightsLabel(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.CENTER);
		lbl.setFont(newFont(lbl.getFont(), txtSize_Regular));
		lbl.setForeground(clrF_HeadTitle);
		lbl.setBackground(clrB_HeadTitle);
		return lbl;
	}
	public static JLabel newHead(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.CENTER);
		lbl.setFont(newFont(lbl.getFont(), txtSize_Heading));
		lbl.setForeground(clrF_HeadTitle);
		lbl.setBackground(clrB_HeadTitle);
		return lbl;
	}
	public static JLabel newSubHead(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.CENTER);
		lbl.setFont(newFont(lbl.getFont(), txtSize_subHead));
		lbl.setForeground(clrF_SubTitle);
		lbl.setBackground(clrB_SubTitle);
		return lbl;
	}
	public static JLabel newTitle(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.CENTER);
		lbl.setFont(newFont(lbl.getFont(), txtSize_Title));
		lbl.setForeground(clrF_Title);
		lbl.setBackground(clrB_Title);
		return lbl;
	}
	public static JButton newJButton(String  msg) {
		JButton  jbtn = new JButton(checkTitleMsg(msg));
		jbtn.setFont(newFont(jbtn.getFont(), txtSize_JButton));
		jbtn.setForeground(clrF_Btn_Add);
		jbtn.setBackground(clrB_Btn_Add);
		return jbtn;
	}
	public static JButton newRecordNavigationJButton(String  msg) {
		JButton  jbtn = new JButton(checkTitleMsg(msg));
		jbtn.setFont(newFont(jbtn.getFont(), txtSize_JButton));
		jbtn.setForeground(clrF_Btn_Add);
		jbtn.setBackground(clrB_Btn_Add);
		return jbtn;
	}
	public static JMenu newJMenu(String  msg) {
		JMenu  jmenu = new JMenu(checkTitleMsg(msg));
		jmenu.setFont(newFont(jmenu.getFont(), txtSize_JMenuItem));
		jmenu.setForeground(clrF_JMenuItem);
		jmenu.setBackground(clrB_JMenuItem);
		return jmenu;
	}
	public static JMenuItem newJMenuItem(String  msg) {
		JMenuItem  jmenu = new JMenuItem(checkTitleMsg(msg));
		jmenu.setFont(newFont(jmenu.getFont(), txtSize_JMenuItem));
		jmenu.setForeground(clrF_JMenuItem);
		jmenu.setBackground(clrB_JMenuItem);
		return jmenu;
	}
	public static JLabel newFieldNameLabel(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.CENTER);
		lbl.setFont(newFont(lbl.getFont(), txtSize_NamesOfFields));
		lbl.setForeground(clr_NamesOfField);
//		lbl.setBackground(clrB_Title);
		return lbl;
	}
	public static JLabel newTextLabel(String  msg) {
		JLabel  lbl = new JLabel(checkTitleMsg(msg), JLabel.LEFT);
		lbl.setFont(newFont(lbl.getFont(), txtSize_Regular));
		lbl.setForeground(clrF_InfoMsgs);
//		lbl.setBackground(clrB_Title);
		return lbl;
	}
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public static JTable newJTable(String[][] testData, String[] columnNames) {
		JTable jtbl = new JTable(testData, columnNames);
		jtbl.getTableHeader().setBackground(clrB_JTableCellHead);
		jtbl.getTableHeader().setForeground(clrF_JTableCellHead);
		jtbl.getTableHeader().setFont(newFont(jtbl.getTableHeader().getFont(), txtSize_JTableHeader));
		jtbl.setFont(newFont(jtbl.getFont(), txtSize_JTableCell));
		jtbl.setRowHeight(jtbl.getFont().getSize()+2);
		return jtbl;
	}
	public static JTable newJTable() {
		JTable jtbl = new JTable();
		jtbl.getTableHeader().setBackground(clrB_JTableCellHead);
		jtbl.getTableHeader().setForeground(clrF_JTableCellHead);
		jtbl.getTableHeader().setFont(newFont(jtbl.getTableHeader().getFont(), txtSize_JTableHeader));
		jtbl.setFont(newFont(jtbl.getFont(), txtSize_JTableCell));
		jtbl.setRowHeight(jtbl.getFont().getSize()+2);
		return jtbl;
	}
	public static void setTableHeads(JTable table, String[] columnNames){
		TableColumn tc = table.getColumnModel().getColumn(0);
		for (String name : columnNames) {
			tc.setHeaderValue(name);
		}
	}
	public static JTable newJTableWithModel(String[] columnNames) {
		JTable jtbl = new JTable(new DefaultTableModel(columnNames, 0));
		jtbl.getTableHeader().setBackground(clrB_JTableCellHead);
		jtbl.getTableHeader().setForeground(clrF_JTableCellHead);
		jtbl.getTableHeader().setFont(newFont(jtbl.getTableHeader().getFont(), txtSize_JTableHeader));
		jtbl.setFont(newFont(jtbl.getFont(), txtSize_JTableCell));
		jtbl.setRowHeight(jtbl.getFont().getSize()+2);
		return jtbl;
	}

}

