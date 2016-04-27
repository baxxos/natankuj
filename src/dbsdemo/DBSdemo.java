package dbsdemo;

import dbsdemo.misc.PropLoader;
import dbsdemo.sql.HibernateUtil;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

/**
 *
 * @author Baxos
 */
public class DBSdemo extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        //Load a property file
        Properties prop = PropLoader.load("etc/config.properties");
            
        FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("LoginWindowPath")));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        LoginWindowController loginController = loader.getController();
        loginController.setActStage(primaryStage);
        
        primaryStage.setTitle("Login");
        primaryStage.setMinHeight(Double.parseDouble(prop.getProperty("LoginWindowMinHeight")));
        primaryStage.setMinWidth(Double.parseDouble(prop.getProperty("LoginWindowMinWidth")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        launch(args);
        HibernateUtil.close();
    }
    
}
