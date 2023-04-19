package gui_v1.settings;
import java.awt.Color;
import java.awt.Dimension;
import gui_v1.automation.GUI_FontSizeAdjustable;
import gui_v1.data_loaders.GUI_Constants;
import gui_v1.help_utils.GUI_Routines;

/**
 * @author owners SPAM Team, ICS499, Personal Expenses Manager App.
 * @author owner Andrey Y
 * @author owner Pavel
 * @author owner Sam
 * @
 *
 */
public interface GUI_Settings_Variables extends GUI_Constants, GUI_FontSizeAdjustable, GUI_Routines {

	
	/**
	 * This is Main Settings Interface Class for this App.
	 * GUI Elements visual appearance: sizes, colors, locations,
	 * are here. Global Headings, Titles, Subtitles, Messages, with
	 * all other static Text Strings are here.
	 *
	 * *************************************************************8
	 * Technical Information Section of this info
	 * *************************
	 *
	 * ***********
	 * 1. All Color variables naming -->
	 * *****
	 * A.  All BackgroundColor variables have prefix --> clrB_
	 * B.  All ForegroundColor variables have prefix --> clrF_
	 *
	 * ***********
	 * 1. All Fonts Sizing are related to -->
	 * *****
	 * A. Basic Font Size
	 * B. Head Font Size
	 * C. SubHead Font Size
	 * D, Title Font Size
	 * E. Important Font Size --> Basic Font Size + fontSizeFactor
	 * F. Smaller Font Size --> Basic Font Size - fontSizeFactor
	 *
	 *
	 * */
//	String strDefaultString  = "This is Default NO-ANY Message is Set Sttring";
	String strDefaultString  = "SPAM Team \nSam, Pavel, Andrey, and not Musab";
	String strDefaultActionString  = "Action Title NOT SET";

	String strDefaultSubTitleString  = "";


	String strAppOfficialName = "Personal Expense Consultant by SPAM Team";
	String strCopyRigts = "Copyright \u00a9 SPAM Team 2023";
	// the following one will be fitted into a JLabel
	String strAboutApp = "<html><center>This App is the Capstone project of SPAM Team,<br>Class ICS 499-01, Spring 2023,"+
			"<br>Metropolitan State University,<br>Saint Paul, Minnesota, United States."+
			"<br><br>Copyright \u00a9 SPAM Team 2023.";
	String strHeadTitle_GUIMainWindow = "Welcome to Personal Expenses Manager ";
	String strHeadTitle_GUIRecordsWindow = "Personal Expense Consultant";



	int gui_width = 950;
	int gui_height = 800;
	Dimension mainGUIFrameSize = new Dimension(gui_width, gui_height);
	Dimension recordsGUIWindowFrameSize = new Dimension(gui_width, gui_height);
	Dimension mainWindowMessageAreaSize = new Dimension(0,200);

	Dimension manualEntryGUIWindowFrameSize = new Dimension(gui_width, 400);
	Dimension howToWindowFrameSize  = new Dimension(gui_width-100, gui_height-50);
	Dimension summaryWindowFrameSize = new Dimension(1050, 700);
	Dimension advisingWindowFrameSize = new Dimension(1250, 700);


	String mainGUIFrameWindowTitle = strAppOfficialName;
	String recordsGUIWindowTitle = strAppOfficialName;
	String manulEntryWindowWindowTitle = strAppOfficialName;

	/////////////////////////////////////////
	//    main colors
	/////////////////////////////////////////
	Color clr_Input = Color.BLUE;
	Color clr_Action_Option = Color.RED;
	Color clr_Selection = Color.RED;
	
	/////////////////////////////////////////
	
	int lineSize = 1;

	int txtSizeFactor = 3;
	int txtSize_Regular = 18;
	int txtSize_Important = txtSize_Regular + txtSizeFactor;
	int txtSize_Smaller = txtSize_Regular - txtSizeFactor;

	int txtSize_Heading = txtSize_Regular + 4 * txtSizeFactor;
	int txtSize_subHead = txtSize_Regular +  4 * txtSizeFactor;
	int txtSize_Title  = txtSize_Regular +  1 * txtSizeFactor;
	

	int txtSize_NamesOfFields = txtSize_Regular;
	Color clr_NamesOfField =clr_Input;
	
	
	
	////////////////////////////////////////
	/// Java Elements Font size settings here

	//// Menu Items Sizes:
	int txtSize_JMenuBar = txtSize_Regular + 2 *txtSizeFactor;
	int txtSize_JMenu = txtSize_Regular +  2 * txtSizeFactor;
	int txtSize_JMenuItem = txtSize_Regular + 2 * txtSizeFactor;
	int txtSize_JLabel = txtSize_Regular +  txtSizeFactor;

	int txtSize_JTextArea = txtSize_Regular;
	int txtSize_JTextField = txtSize_Regular;
	int txtSize_JTextFieldOutput = txtSize_Regular;
	int txtSize_JCheckBox = txtSize_Smaller;
	int txtSize_JRadioButton = txtSize_Smaller;
	int txtSize_JButton = txtSize_Regular;
	int txtSize_JTable = txtSize_Regular;
	int txtSize_JTableHeader = txtSize_Regular;
	int txtSize_JTableCell = txtSize_Regular;


		
    /////////////////////////////////////////
///// COLOR SETTINGS START HERE
	Color guiFramesBackgroundColor = Color.CYAN;
	Color guiFramesForegroundColor = Color.BLACK;

	Color allGuiBoxesBackgroundCColor = Color.BLUE;
	Color allGuiBoxesForegroundCColor = Color.BLACK;

//	Color allSelectionHoldersPanelsBackgroundColor = Color.ORANGE;
//	Color allSelectionHoldersPanelsForegroundColor = Color.BLUE;

	Color availableActionsPanelBackgroundColor = Color.RED;
	Color availableActionsPanelForegroundColor = Color.GREEN;


	Color InsertDataPBackgroundColor = Color.ORANGE;
	Color InsertDataPForegroundColor = Color.RED;
	
	
	

	Color clrF_HeadTitle = Color.BLUE;
	Color clrB_HeadTitle = Color.LIGHT_GRAY;


	Color clrF_SubTitle = Color.BLUE;
	Color clrB_SubTitle = Color.LIGHT_GRAY;


	Color clrF_Title = Color.BLUE;
	Color clrB_Title = Color.LIGHT_GRAY;


	Color clrF_InfoMsgs = Color.BLUE;
	Color clrB_InfoMsgs = Color.LIGHT_GRAY;

	Color clrF_PurposeMsgs = Color.BLUE;
	Color clrB_PurposeMsgs = Color.LIGHT_GRAY;


	Color clrF_SelectionMsgs = Color.BLUE;
	Color clrB_SelectionMsgs = Color.LIGHT_GRAY;

	Color clrF_ErrorMsgs = Color.BLUE;
	Color clrB_ErrorMsg = Color.LIGHT_GRAY;

	Color clrF_WarningMsgs = Color.BLUE;
	Color clrB_WarningMsg = Color.LIGHT_GRAY;

	Color clrF_Action_Selectors= Color.RED;
	Color clrB_Action_Selectors = Color.YELLOW;


	Color clrF_Type_Selections = Color.BLUE;
	Color clrB_Type_Selections = Color.LIGHT_GRAY;


	Color clrF_DataInput = Color.BLUE;
	Color clrB_DataInput = Color.LIGHT_GRAY;

	Color clrF_DataOutput = Color.BLUE;
	Color clrB_DataOutput = Color.LIGHT_GRAY;

	Color linkSelected = Color.CYAN;
	Color linkDeselected = Color.BLACK;

	Color clrF_Btn_Insert = Color.BLUE;
	Color clrB_Btn_Insert = Color.LIGHT_GRAY;
	Color clrF_Btn_Agree = Color.BLUE;
	Color clrB_Btn_Agree = Color.LIGHT_GRAY;
	Color clrF_Btn_Deny = Color.BLUE;	
	Color clrB_Btn_Deny = Color.LIGHT_GRAY;
	Color clrF_Btn_Show = Color.BLUE;
	Color clrB_Btn_Show = Color.LIGHT_GRAY;
	Color clrF_Btn_Delete = Color.BLUE;
	Color clrB_Btn_Delete = Color.LIGHT_GRAY;
	Color clrF_Btn_Close = Color.BLUE;
	Color clrB_Btn_Close = Color.LIGHT_GRAY;
	Color clrF_Btn_Confirm = Color.BLUE;
	Color clrB_Btn_Confirm = Color.LIGHT_GRAY;
	Color clrF_Btn_Add = Color.BLUE;
	Color clrB_Btn_Add = Color.getHSBColor(255, 209, 229);
	Color clrF_Btn_Search = Color.BLUE;
	Color clrB_Btn_Search = Color.LIGHT_GRAY;
	Color clrF_Btn_Help = Color.BLUE;
	Color clrB_Btn_Help = Color.LIGHT_GRAY;



	// Same Types of GUI Elements settings


	Color clrF_JFrame = Color.BLUE;
	Color clrB_JFrame = Color.LIGHT_GRAY;

	Color clrF_JPanel = Color.BLUE;
	Color clrB_JPanel = Color.LIGHT_GRAY;

	Color clrF_JLabel = Color.BLUE;
	Color clrB_JLabel = Color.LIGHT_GRAY;

	Color clrF_JButton = Color.BLUE;
	Color clrB_JButton = Color.LIGHT_GRAY;

	Color clrF_JTextField = Color.BLUE;
	Color clrB_JTextField = Color.getHSBColor(74, 205, 242);

	Color clrF_JTextArea = Color.BLUE;
	Color clrB_JTextArea = Color.LIGHT_GRAY;

	Color clrF_JRadioButton = Color.BLUE;
	Color clrB_JRadioButton= Color.LIGHT_GRAY;

	
	Color clrF_JCheckBox = Color.BLUE;
	Color clrB_JCheckBox = Color.LIGHT_GRAY;

	Color clrF_JTable = Color.BLUE;
	Color clrB_JTable = Color.LIGHT_GRAY;

	Color clrF_JTableCellHead = Color.BLUE;
	Color clrB_JTableCellHead = Color.GREEN;
	Color clrF_JTable_cell = Color.BLUE;
	Color clrB_JTable_cell = Color.LIGHT_GRAY;



	Color clrF_JMenuBar = Color.BLUE;
	Color clrB_JMenuBar = Color.getHSBColor(74, 205, 242);
	Color clrF_JMenu = Color.BLACK;
	Color clrB_JMenu = Color.LIGHT_GRAY;
	Color clrF_JMenuItem = Color.BLUE;
	Color clrB_JMenuItem = Color.getHSBColor(74, 205, 242);;

	/// end of Java GUI  elements settings



	////////////////////////////////////////////
	////// FONTS Settings area
	
	
	
	
///////////////////////////////////////////////////////////
	
	
	
	/////////////////////////////////////////////////////////

}



















