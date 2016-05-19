package controllers;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import dao.UserDaoAPI;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import main.Main;
import misc.PropLoader;

// Users tab controller class - handles opening/closing/editing actions etc.
public class UsersTabController implements Initializable {
    @FXML
    private Tab userListTab;
    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colSurname;
    @FXML
    private TableColumn<User, Integer> colUserLevel;
    // Private attributes for database access and main logic
    private UserDaoAPI userDao;
    private ObservableList<User> users;
    
    public UsersTabController(){
    	try {
    		// Attempt to load remote objects needed for class default logic
    		this.userDao = (UserDaoAPI)
    				Main.ctx.lookup(PropLoader.load().getProperty("UserDao"));
    	} catch (NamingException e) {
    		Logger.getLogger(UsersTabController.class)
    			.fatal("Failed to load UserDao instance", e);
    	} catch (NullPointerException npe) {
    		Logger.getLogger(UsersTabController.class)
    			.fatal("Failed to load a custom DAO instance - missing file path in config.properties", npe);
    	}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	// Set content
        this.populateUserList();
        // Set language
		try {
			// Initialize resource bundle with properties file
			ResourceBundle rbLang = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
	        this.userListTab.setText(rbLang.getString("users"));
	        this.tableViewUsers.getColumns().get(0).setText(rbLang.getString("username"));
	        this.colName.setText(rbLang.getString("name"));
	        this.colSurname.setText(rbLang.getString("surname"));
	        this.colUserLevel.setText(rbLang.getString("userLevel"));
		} catch (MissingResourceException e) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Missing language resource(s) - check src/resource/properties file paths", e);
		}
    }
    
    public void enableEditing(){
        // Add context menu for record deletion
        addContextMenu(tableViewUsers);
        // Make selected table fields editable
        this.tableViewUsers.setEditable(true);
        this.colName.setEditable(true);
        this.colSurname.setEditable(true);
        // Configure editing actions and handlers
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(
                new EventHandler<CellEditEvent<User, String>>() {
                    @Override
                    public void handle(CellEditEvent<User, String> t) {
                        User user = tableViewUsers.getSelectionModel().getSelectedItem();
                        user.setName(t.getNewValue());
                        userDao.updateUser(user);
                    }
                }
        );
        
        colSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSurname.setOnEditCommit(
                new EventHandler<CellEditEvent<User, String>>() {
                    @Override
                    public void handle(CellEditEvent<User, String> t) {
                        User user = tableViewUsers.getSelectionModel().getSelectedItem();
                        user.setSurname(t.getNewValue());
                        userDao.updateUser(user);
                    }
                }
        );
        // TODO catch numberFormatException
        colUserLevel.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerToString()));
        colUserLevel.setOnEditCommit(
                new EventHandler<CellEditEvent<User, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<User, Integer> t) {
	                        User user = tableViewUsers.getSelectionModel().getSelectedItem();
	                        try {
	                        	user.setUserLevel(t.getNewValue());
	                        	userDao.updateUser(user);
	                        }
	                        catch (NullPointerException npe){
	                        	populateUserList();
	                        	Logger.getLogger(UsersTabController.class)
	                        		.error("Failed to update userlevel to " +t.getNewValue()+ 
	                        		       " - either wrong number format or userDao object is not initialized");
	                        }
                    }
                }
        );
    }
    
    public void populateUserList(){
        
        users = FXCollections.observableArrayList(this.userDao.getAllAsObjects());

        this.tableViewUsers.getColumns().get(0).setCellValueFactory(
            new PropertyValueFactory<>("username")
        );
        this.tableViewUsers.getColumns().get(1).setCellValueFactory(
            new PropertyValueFactory<>("name")
        );
        this.tableViewUsers.getColumns().get(2).setCellValueFactory(
            new PropertyValueFactory<>("surname")
        );
        this.tableViewUsers.getColumns().get(3).setCellValueFactory(
            new PropertyValueFactory<>("userLevel")
        );
        this.tableViewUsers.setItems(users);
    }
    
    private void addContextMenu(Region region){
        
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("Vymazaù pouûÌvateæa")); 
        
        region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY){
                    contextMenu.show(tableViewUsers , t.getScreenX() , t.getScreenY());
                }
                else {
                    contextMenu.hide();
                }
            }
        });
        
        contextMenu.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                User user = tableViewUsers.getSelectionModel().getSelectedItem();
                UsersTabController.this.users.remove(user);
                userDao.deleteRecord(user.getId());
            }
        });
    }
    
    public Tab getUserListTab() {
        return userListTab;
    }
    
    // Class for handling user inputs and catching number format exceptions
    private class IntegerToString extends StringConverter<Integer> {
        @Override public Integer fromString(String value) {
            // If the specified value is null or zero-length, return null
            if (value == null) 
                return null;

            value = value.trim();

            if (value.length() < 1)
                return null;
            try {
            	return Integer.valueOf(value);
	        } catch (NumberFormatException nfe) {
	    		Logger.getLogger(UsersTabController.class)
	    			.error("Wrong input entered while editing userlevel (expected integer)", nfe);
	    		return null;
	    	}
        }

        @Override public String toString(Integer value) {
            // If the specified value is null, return a zero-length String
            if (value == null) 
                return "";
            
            return (Integer.toString(((Integer)value).intValue()));
        }
    }
}