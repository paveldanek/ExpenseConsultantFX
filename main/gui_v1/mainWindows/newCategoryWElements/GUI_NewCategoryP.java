package gui_v1.mainWindows.newCategoryWElements;
import gui_v1.action_processors.NewCategoryProgrammableHandler;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.data_loaders.GUI_ElementsDataLoader;
import gui_v1.data_loaders.GUI_ElementsOptionLists;
import gui_v1.gui_logic.GUI_ManualEntryTemporaialHolder;
import gui_v1.mainWindows.*;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.Request;
import parsers.OFXParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static gui_v1.mainWindows.GUI_NewCategoryWindow.ACCESS_FROM_MANUAL_ENTRY;

public class GUI_NewCategoryP extends JPanel implements GUI_Settings_Variables, ActionListener, GUI_ShowingPupUpMsgAAbility {
    private JTextField jtfCategoryName;
    private JButton jbtnAdd;
    private static JComboBox<String>  jcmbCategory;

    private void init(){
        jcmbCategory = GUI_ElementCreator.newJComboBox(GUI_ElementsOptionLists.getInstance().getTransCategoryist());
        //jtfCategoryName= GUI_ElementCreator.newTextFieldWithHelp(GUI_ElementsDataLoader.getNCHelpMsgs().newCategoryNameInputHelpMsg());
        jtfCategoryName= GUI_ElementCreator.newTextFieldWithHelp("");
        jbtnAdd = GUI_ElementCreator.newJButton("Add This Category");

        jbtnAdd.addActionListener(this);
    }
    public GUI_NewCategoryP(){

        init();
        setLayout(new BorderLayout());
        add(GUI_ElementCreator.newTitle("Enter New Category Info"), BorderLayout.NORTH);

        JPanel pBox = new JPanel(new GridLayout(3,2));
        pBox.add(new JLabel());
        pBox.add(new JLabel());
        pBox.requestFocusInWindow();
        pBox.setRequestFocusEnabled(true);
        pBox.setFocusable(true);
        JLabel jlblInputTitles = GUI_ElementCreator.newTextLabel("New Category Name:");
        jlblInputTitles.setFocusTraversalPolicyProvider(true);
        pBox.add(jlblInputTitles);
        pBox.add(jtfCategoryName);
        pBox.add(new JLabel());
        pBox.add(new JLabel());
        add(pBox, BorderLayout.CENTER);
        add(jbtnAdd, BorderLayout.SOUTH);

        jbtnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                jbtnAdd.requestFocusInWindow();
            }
        });
        Request r = Request.instance();
        r.setNewCategoryWindowHolder(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==jbtnAdd){
            processAddCategoryBtnClick();
        }
    }

    AbstractAction a = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearField();
            GUI_NewCategoryWindow.getInstance().disposeNewCategoryWindow();
            if (GUI_NewCategoryWindow.getInstance().getPointOfEntry()==ACCESS_FROM_MANUAL_ENTRY) {
                GUI_ManualEntryWindow.getInstance().showManualEntryWindow();
            } else {
                GUI_RecordsWindow.getInstance().showRecordsWindow();
            }
        }
    };

    public void clearField() {
        jtfCategoryName.setText("");
    }

    private void processAddCategoryBtnClick() {
        String categoryName = jtfCategoryName.getText().trim() ;
        if( categoryName.compareToIgnoreCase(GUI_ElementsDataLoader.getNCHelpMsgs().
                newCategoryNameInputHelpMsg())==0 || categoryName.trim().compareToIgnoreCase("")==0 ) {
            showCategoryNotEnteredMsg();
            /*
        }else if(GUI_ManualEntryTemporaialHolder.getInstance().isCategoryInUnsavedList(categoryName)){
            showAlreadyEnteredCategoryMsg();
        }else  if(GUI_ElementsOptionLists.getInstance().isCategoryExist(categoryName)){
            showCategoryExistsMMsg();
            */
        }else {
            new NewCategoryProgrammableHandler(categoryName);
            GUI_ElementsOptionLists.getInstance().addTransactionCategoryToList(categoryName);
            showSuccessMsgStoreCategoryAndReturnToAddAcctWindow();
        }


    }

    private void showSuccessMsgStoreCategoryAndReturnToAddAcctWindow() {

        showConfirmationMessge("You Successfully Added new Category To Your Records",a);
    }


    private void  showCategoryNotEnteredMsg() {
        showErrMessageAndAskWhaatToDo("Category Name Not Entered, Do you want close this window, and return to Adding Accounts", "Category Adding Error", a) ;
    }
    private void showCategoryExistsMMsg() {
        showErrMessageAndAskWhaatToDo("This Category is already in your record. Do you want close this window, and return to Adding Accounts",  "Category Adding Error", a);
    }
    private void showAlreadyEnteredCategoryMsg() {
        showErrMessageAndAskWhaatToDo("This Category Already Been Entered and will be saved.Do you want close this window, and return to Adding Accounts",  "Category Adding Error", a);
    }



    @Override
    public Component getComponent() {
        return null;
    }

}
