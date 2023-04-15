package gui_v1.action_processors;

import javax.swing.*;
import java.io.File;

/**
 *  THis is class for static method file chooser,
 *  to allow user ot choose OFX file for parsing
 */
public class GUI_FileChooser {
   static  public File getFileOrDirectory(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        //if user clicked Cancel button
        if(result == JFileChooser.CANCEL_OPTION) {
        }
        File file = fileChooser.getSelectedFile();
        if((file ==null)||(file.getName().equals(""))){
        }
        return file;
    }

}
