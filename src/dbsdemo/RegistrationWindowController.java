/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbsdemo;

import dbsdemo.misc.CustomAlert;
import dbsdemo.entities.User;
import dbsdemo.sql.custom.UserDao;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Hex;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class RegistrationWindowController implements Initializable {
    @FXML
    private Font x1;
    @FXML
    private ComboBox<Integer> userLevelComboBox;
    @FXML
    private Button registerButton;
    @FXML
    private PasswordField passwdTextField;
    @FXML
    private PasswordField passwdAgainTextField;
    @FXML
    private TextField userNameTextField;
    
    private Stage actStage;
    private Stage prevStage;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    
    private void checkForFilledFields(){
        
        if(
            !this.nameTextField.getText().isEmpty() &&
            !this.surnameTextField.getText().isEmpty() &&
            !this.userNameTextField.getText().isEmpty() &&
            !this.passwdTextField.getText().isEmpty() &&
            !this.passwdAgainTextField.getText().isEmpty()){
            this.registerButton.setDisable(false);
        }
        else{
            this.registerButton.setDisable(true);
        }
    }
    
    private boolean verifyInput(){
        
        if(
            this.surnameTextField.getText().length() > 2 &&
            this.nameTextField.getText().length() > 2 &&
            this.passwdTextField.getText().length() > 2 &&
            this.passwdAgainTextField.getText().length() > 2 &&
            this.userNameTextField.getText().length() > 2){
            
            if(this.passwdTextField.getText().equals(this.passwdAgainTextField.getText())){
                return true;
            }
            else {
                new CustomAlert(
                       Alert.AlertType.WARNING,
                       "Warning",
                       "Registration unsuccessful",
                       "Password fields not matching."
               ).showAndWait();               
            }
        }
        else {
            new CustomAlert(
                    Alert.AlertType.WARNING,
                    "Warning",
                    "Registration unsuccessful",
                    "Please fill in all the displayed fields. Each input must be at least 3 characters long."
            ).showAndWait();
        }
        return false;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void userLevelAvailable(boolean value){
        
        this.userLevelComboBox.setDisable(!value);
    }

    @FXML
    private void registerNewUser(ActionEvent event) {
        
        // Chceck fields for required lengh
        if(!verifyInput()){
            return;
        }
        // Perform DB connection and transaction

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(passwdTextField.getText().getBytes(Charset.forName("UTF-8")));
        

        User user = new User(
                userNameTextField.getText(),
                new String(Hex.encodeHex(md.digest())),
                nameTextField.getText(),
                surnameTextField.getText(),
                userLevelComboBox.getValue() == null ? 0 : userLevelComboBox.getValue()
        );

        new UserDao().insert(user);
        
        //trigger the main application window
        goToMainWindowScene("gui/MainWindow.fxml");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RegistrationWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConstraintViolationException ex ) {
            new CustomAlert(
                    Alert.AlertType.WARNING,
                    "Error",
                    "Registration unsuccessful",
                    "Username is already taken. Please choose a different username and try again."
            ).showAndWait();
            
            Logger.getLogger(RegistrationWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void keyInputDetected(KeyEvent event) {
        
        checkForFilledFields();
    }
    
    public void goToMainWindowScene(String fxmlPath){
        // Try loading a fxml scene file
        // TODO create a static loader - more than one class triggers mainWindow
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = (Parent) loader.load();
            
            MainWindowController mainWindowController = loader.getController();
            mainWindowController.populateTable();
            
            //Scene scene = new Scene(root);
            Stage mainWindowStage = new Stage();
            mainWindowStage.setTitle("Fuel database");
            mainWindowStage.setMinHeight(mainWindowStage.getHeight());
            mainWindowStage.setMinWidth(mainWindowStage.getWidth());
            mainWindowStage.setScene(this.actStage.getScene());
            mainWindowStage.getScene().setRoot(root);
            
            mainWindowStage.show();
            this.actStage.close();
        } catch (IOException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setActStage(Stage stage){
        this.actStage = stage;
    }
}
