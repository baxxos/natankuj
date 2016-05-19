package controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.ejb.EJBException;
import javax.naming.NamingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import dao.UserDaoAPI;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.Main;
import misc.PropLoader;
import misc.SHA;
import gui.CustomAlert;
import javafx.scene.control.Alert;

public class RegistrationWindowController implements Initializable {
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
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelName;
    @FXML
    private Label labelPassword;
    @FXML
    private Label labelPasswordAgain;
    @FXML
    private Label labelSurname;
    @FXML
    private Label labelUserLevel;
    @FXML
    private Label labelRegisterPrompt;
    // Private controller attributes
    private Stage actStage;
    private boolean triggerMainWindow = true;
    private UserDaoAPI userDao;
    private SHA sha;
    // Language resource bundle
    private ResourceBundle rb;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Logger.getLogger(RegistrationWindowController.class)
			.debug("Registration window controller initialized");
		// Set the user environment based on locale
		this.updateLanguage();
    }   
    
    public RegistrationWindowController(){
    	try {
			this.userDao = (UserDaoAPI) Main.ctx.lookup(PropLoader.load().getProperty("UserDao"));
			this.sha = (SHA) Main.ctx.lookup(PropLoader.load().getProperty("SHA"));
		} catch (NamingException e) {
			Logger.getLogger(RegistrationWindowController.class)
			.fatal("Failed to load UserDao/SHA instance", e);
		} catch (NullPointerException npe) {
			Logger.getLogger(RegistrationWindowController.class)
				.fatal("Failed to load a custom DAO instance - missing file path in config.properties", npe);
		} catch (Exception e){
			Logger.getLogger(LoginWindowController.class)
				.fatal("Unspecified exception while loading registration window - check resource files paths", e);
		}
    }
    
    public void updateLanguage(){
    	try {
			// Initialize resource bundle with properties file
			this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
			// Load required phrases
			this.labelRegisterPrompt.setText(rb.getString("registerPrompt"));
			this.labelName.setText(rb.getString("name"));
			this.labelUsername.setText(rb.getString("username"));
			this.labelSurname.setText(rb.getString("surname"));
			this.labelPassword.setText(rb.getString("password"));
			this.labelPasswordAgain.setText(rb.getString("passwordAgain"));
			this.registerButton.setText(rb.getString("register"));
		} catch (MissingResourceException e) {
			Logger.getLogger(RegistrationWindowController.class)
				.fatal("Missing language resource(s) - check src/resource/properties file paths", e);
		}
	}
    
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
    
    public void userLevelAvailable(boolean value){
        this.userLevelComboBox.setDisable(!value);
    }

    @FXML
    private void registerNewUser(ActionEvent event) {
        
        // Check fields for required lengh
        if(!verifyInput()){
            return;
        }
        // Perform DB connection and transaction
        try {
            User user = new User(
                    userNameTextField.getText(),
                    sha.getHash(passwdTextField.getText()),
                    nameTextField.getText(),
                    surnameTextField.getText(),
                    userLevelComboBox.getValue() == null ? 0 : userLevelComboBox.getValue()
            );

            this.userDao.insert(user);
            // Trigger the main application window if needed
            if(this.triggerMainWindow){
                goToMainWindowScene(user);
            }
            this.actStage.close();
        } catch (EJBException ex ) {
			// Username already taken - inform the user
        	System.out.println(ex.getClass().getName());
			new CustomAlert(Alert.AlertType.WARNING,
					"Error",
					"Registration unsuccessful",
					"Username is already taken. Please choose a different username and try again.")
			.showAndWait();
			// Log the event
			Logger.getLogger(RegistrationWindowController.class)
				.error("User attempted to register an already taken username: "
						+ userNameTextField.getText(), ex);
			return;
        }
	}

    @FXML
    private void keyInputDetected(KeyEvent event) {  
        checkForFilledFields();
    }
    
    public void goToMainWindowScene(User user){
    	Properties prop = PropLoader.load(PropLoader.configProps);
        // Continue to main window screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("MainWindowPath")));
            Parent root = (Parent) loader.load();
            
            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setActiveUser(user);
            
            //Scene scene = new Scene(root);
            Stage mainWindowStage = new Stage();
            mainWindowStage.setTitle("Fuel database");
            mainWindowStage.setMinHeight(mainWindowStage.getHeight());
            mainWindowStage.setMinWidth(mainWindowStage.getWidth());
            mainWindowStage.setScene(new Scene(root));
            
            mainWindowStage.show();
		} catch (IOException ex) {
			Logger.getLogger(LoginWindowController.class.getName())
					.fatal("Invalid FXML file path (" + prop.getProperty("MainWindowPath") + ")", ex);
			ex.printStackTrace();
		}
    }
    
    public void setActStage(Stage stage){
        this.actStage = stage;
    }

    public void setTriggerMainWindow(boolean triggerMainWindow) {
        this.triggerMainWindow = triggerMainWindow;
    }
}
