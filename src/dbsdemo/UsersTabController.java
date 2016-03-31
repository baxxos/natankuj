package dbsdemo;

import dbsdemo.entities.User;
import dbsdemo.sql.custom.UserDao;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateUserList();
        // Add context menu on tableView items
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("Zvýšiť userLevel"));
        contextMenu.getItems().add(new MenuItem("Znížiť userLevel"));        
        addContextMenu(tableViewUsers, contextMenu);
    }    
    
    public void populateUserList(){
        
        ObservableList<User> users = FXCollections.observableArrayList(new UserDao().getAllAsObjects());

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
    
    private void addContextMenu(Region region, ContextMenu menu){
        
        region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY){
                    menu.show(tableViewUsers , t.getScreenX() , t.getScreenY());
                }
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
