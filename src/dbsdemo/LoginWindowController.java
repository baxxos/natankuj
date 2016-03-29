
package dbsdemo;

import dbsdemo.misc.CustomAlert;
import dbsdemo.misc.PropLoader;
import dbsdemo.misc.SHA;
import dbsdemo.entities.User;
import dbsdemo.sql.custom.UserDao;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class LoginWindowController implements Initializable {
    @FXML
    private Font x1;
    @FXML
    private Button loginButton;

    private Stage actStage;
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField passwdTextField;
    @FXML
    private Hyperlink goToRegWindowScene;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void goToMainWindowScene(User user){
        // Load properties file - TODO catch exception
        Properties prop = PropLoader.load("etc/config.properties");
        // Continue to main window screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("MainWindowPath")));
            Parent root = (Parent) loader.load();
            
            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setActiveUser(user);
            
            Stage mainWindowStage = new Stage();
            mainWindowStage.setTitle("Fuel database");
            mainWindowStage.setMinHeight(mainWindowStage.getHeight());
            mainWindowStage.setMinWidth(mainWindowStage.getWidth());
            mainWindowStage.setScene(this.actStage.getScene());
            mainWindowStage.getScene().setRoot(root);
            
            mainWindowStage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void goToRegWindowScene(){
        // Load properties file - TODO catch exception
        Properties prop = PropLoader.load("etc/config.properties");
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
            
            regWindowStage.show();
            if(this.actStage != null){
                this.actStage.close();
            }
            else {
                regWindowController.setTriggerMainWindow(false);
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setActStage(Stage stage){
        this.actStage = stage;
    }

    @FXML
    private void loginAction(ActionEvent event) {
        
        try {
            User activeUser = new UserDao().getUser(
                    userNameTextField.getText(),
                    new SHA().getHash(passwdTextField.getText())
            ).get(0);
            
            goToMainWindowScene(activeUser);
            this.actStage.close();
        }
        catch(IndexOutOfBoundsException e) {
            new CustomAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Login failed",
                    "Wrong username or password. Try again."
            ).showAndWait();
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
