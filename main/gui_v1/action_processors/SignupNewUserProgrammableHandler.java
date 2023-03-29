package gui_v1.action_processors;

import javax.swing.*;

public class SignupNewUserProgrammableHandler  {
    private String email;
    private String pass1;
    private String pass2;
    private String question1;
    private String question2;
    private String answer1;
    private String answer2;

    public SignupNewUserProgrammableHandler(String e, String p1, String p2, String q1,
                                            String q2, String a1, String a2){
        email = e;
        pass1 = p1;
        pass2 = p2;
        question1 = q1;
        question2 = q2;
        answer1 = a1;
        answer2 = a2;

        //showNewUserSignupInfo();


    }

    private void showNewUserSignupInfo(){
        String regInfo = "New User Information:\n" + "email --> " + email +"\npass1 --> "+ pass1
                +"\npass2 --> "+ pass2 +"\nquestion1 --> "+ question1 +"\nquestion2 --> "+ question2
                +"\nanswer1 --> "+ answer1 +"\nanswer2 --> "+ answer2;
        JOptionPane.showMessageDialog(null, regInfo,  "New User Signup Info!", JOptionPane.INFORMATION_MESSAGE);

    }
}