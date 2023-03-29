package gui_v1.action_processors;

import javax.swing.*;

public class GUI_MainiWindowActionProcessor {

    public GUI_MainiWindowActionProcessor(){
        startAppTerminationProcess();
    }
    public void startAppTerminationProcess(){
        System.exit(JFrame.EXIT_ON_CLOSE);
    }
}
