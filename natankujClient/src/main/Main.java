package main;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import controllers.LoginWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import misc.PropLoader;

public class Main extends Application {
	
	public static Context ctx;
	private static Locale prefLang;
	
	@Override
    public void start(Stage primaryStage) throws IOException {
        //Load FXML file from path specified in config.properties
        Properties prop = PropLoader.load(PropLoader.configProps);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("LoginWindowPath")));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        // Get controller from FXML file
        LoginWindowController loginController = loader.getController();
        loginController.setActStage(primaryStage);
        // Initialise the displayed stage
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

	public static void main(String[] args) throws NamingException {
		// EJB modules initialisation
		Properties prop = new Properties();
        prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        Main.ctx = new InitialContext(prop);
        // Look for preferred language in config properties file
        Main.prefLang = PropLoader.load().getProperty("prefLang").toLowerCase().equals("sk") ? 
        				new Locale("sk") : Locale.getDefault();
        
        Logger.getLogger(Main.class.getName()).info("Client application started");
        launch(args);
	}

	public static Locale getPrefLang() {
		return prefLang;
	}

	public static void setPrefLang(Locale prefLang) {
		// Update language in property file as well - persist language changes
		PropLoader.save("prefLang", prefLang.getLanguage());
		Main.prefLang = prefLang;
	}
}
