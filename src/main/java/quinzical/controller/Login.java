package quinzical.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import quinzical.util.Modal;

public class Login {
    
    @FXML
    private TextField fxUsermameField;

    @FXML
    private PasswordField fxPasswordField;

    @FXML
    private Label fxMessage;

    @FXML
    public void handleSubmit(){
        
        String username = fxUsermameField.getText();
        String password = fxPasswordField.getText();

        //Validate input
        if(username.length() == 0){
            fxMessage.setText("Please enter a username");
        } else if(password.length() == 0){
            fxMessage.setText("Please enter a password");
        } else {
            //Input is valid
        }
    }

    @FXML
    public void redirectNewAccount(){
        Modal.redirect(View.MODAL_SIGNUP);
    }

    @FXML
    public void handleClose(){
        Modal.hide();
    }

}