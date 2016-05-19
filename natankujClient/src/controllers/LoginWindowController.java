package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import dao.UserDaoAPI;
import entities.User;
import gui.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;
import misc.PropLoader;
import misc.SHA;

public class LoginWindowController implements Initializable {
	// Reference to the main window
	private Stage actStage;
	// Reference to user dao object
	private UserDaoAPI userDao;
	// Reference to SHA object
	private SHA sha;
	// Language resource bundle
	private ResourceBundle rb;
	// Misc GUI components
	@FXML
	private Button loginButton;
	@FXML
	private TextField userNameTextField;
	@FXML
	private PasswordField passwdTextField;
	@FXML
	private Label labelLoginPrompt;
	@FXML
	private Label labelUsername;
	@FXML
	private Label labelPassword;
	@FXML
	private Label labelGuestAccount;
	@FXML
	private Hyperlink linkCreateAccount;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Logger.getLogger(LoginWindowController.class)
				.debug("Login window controller initialized");
		// Set the user environment based on locale
		this.updateLanguage();
	}
	
	public LoginWindowController(){
		try {
			// Attempt to load remote objects needed for class logic
			this.userDao = (UserDaoAPI) Main.ctx.lookup(PropLoader.load().getProperty("UserDao"));
			this.sha = (SHA) Main.ctx.lookup(PropLoader.load().getProperty("SHA"));
		} catch (NamingException ne) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Failed to load UserDao instance", ne);
		} catch (NullPointerException npe) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Failed to load a custom DAO instance - missing file path in config.properties", npe);
		}
	}

	public void updateLanguage(){
		try {
			// Initialize resource bundle with properties file
			this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
			// Load required phrases
			this.labelLoginPrompt.setText(rb.getString("loginPrompt"));
			this.labelUsername.setText(rb.getString("username"));
			this.labelPassword.setText(rb.getString("password"));
			this.linkCreateAccount.setText(rb.getString("createAccount"));
			this.labelGuestAccount.setText(rb.getString("guestAccount"));
			this.loginButton.setText(rb.getString("login"));
		} catch (MissingResourceException e) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Missing language resource(s) - check src/resource/properties file paths", e);
		}
	}
	
	public void goToMainWindowScene(User user){
		// Load properties file -
		Properties prop = PropLoader.load(PropLoader.configProps);
		// Continue to main window screen 
		try { 
			FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("MainWindowPath")));
			Parent root = (Parent) loader.load();
	  
			MainWindowController mainWindowController = loader.getController();
			mainWindowController.setActiveUser(user);
	  
			Stage mainWindowStage = new Stage(); mainWindowStage.setTitle(
					"Fuel database");
			mainWindowStage.setMinHeight(635);
			mainWindowStage.setMinWidth(1160);
			mainWindowStage.setScene(this.actStage.getScene());
			mainWindowStage.getScene().setRoot(root);
			mainWindowStage.show();
		}
		catch (IOException ex) {
			Logger.getLogger(LoginWindowController.class.getName())
					.fatal("Invalid FXML file path (" + prop.getProperty("MainWindowPath") + ")", ex);
			ex.printStackTrace();
		}
	}
	 

	@FXML
	public void goToRegWindowScene() {
		// Load config properties
		Properties prop = PropLoader.load(PropLoader.configProps);
		// Continue to user registration screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("RegWindowPath")));
			Parent root = (Parent) loader.load();

			Stage regWindowStage = new Stage();
			regWindowStage.setTitle("New user registration");
			regWindowStage.setMinHeight(regWindowStage.getHeight());
			regWindowStage.setMinWidth(regWindowStage.getWidth());
			regWindowStage.setScene(new Scene(root));

			RegistrationWindowController regWindowController = loader.getController();
			regWindowController.userLevelAvailable(false);
			regWindowController.setActStage(regWindowStage);
			
			regWindowStage.setResizable(false);
			regWindowStage.show();
			Logger.getLogger(LoginWindowController.class.getName())
				.info("User registration window opened");
			if (this.actStage != null) {
				this.actStage.close();
			} else {
				regWindowController.setTriggerMainWindow(false);
			}
		} catch (IOException ex) {
			Logger.getLogger(LoginWindowController.class.getName())
					.fatal("Invalid FXML file path (" + prop.getProperty("RegWindowPath") + ")", ex);
		}
	}

	public void setActStage(Stage stage) {
		this.actStage = stage;
	}

	@FXML
	private void loginAction(ActionEvent event) {

		try {
			User activeUser = userDao.getUser(
					userNameTextField.getText(), 
					sha.getHash(passwdTextField.getText())
			);
			goToMainWindowScene(activeUser);
			this.actStage.close();
		} catch (NullPointerException e) {
			// Inform the user that he entered wrong credentials
			new CustomAlert(Alert.AlertType.ERROR, 
					"Error", 
					"Login failed", 
					"Wrong username or password. Try again.")
					.showAndWait();
			// Log the event
			Logger.getLogger(LoginWindowController.class)
				.error("Wrong credentials entered while logging in: "
						+ userNameTextField.getText(), e);
		} catch (IllegalStateException ex) {
			// Inform the user that the server is down
			new CustomAlert(Alert.AlertType.ERROR, 
					"Error", 
					"Login failed", 
					"The application server (WildFly 10) is not available.")
					.showAndWait();
			// Log the event
			Logger.getLogger(LoginWindowController.class)
				.error("Attempted login while the application server was down", ex);
		}
	}
}
