package dbsdemo;

import dbsdemo.entities.User;
import dbsdemo.sql.custom.UserDao;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
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
    
    private ObservableList<User> users;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        populateUserList();
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
                        new UserDao().updateUser(user);
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
                        new UserDao().updateUser(user);
                    }
                }
        );
        // TODO catch numberFormatException
        colUserLevel.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colUserLevel.setOnEditCommit(
                new EventHandler<CellEditEvent<User, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<User, Integer> t) {
                        User user = tableViewUsers.getSelectionModel().getSelectedItem();
                        user.setUserLevel(t.getNewValue());
                        new UserDao().updateUser(user);
                    }
                }
        );
    }
    
    public void populateUserList(){
        
        users = FXCollections.observableArrayList(new UserDao().getAllAsObjects());

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
        contextMenu.getItems().add(new MenuItem("Vymazať používateľa")); 
        
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
                new UserDao().deleteRecord(user.getId());
            }
        });
    }

    public Tab getUserListTab() {
        return userListTab;
    }

    public TableView<User> getTableViewUsers() {
        return tableViewUsers;
    }
}
