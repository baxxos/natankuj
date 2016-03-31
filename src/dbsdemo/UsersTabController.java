package dbsdemo;

import dbsdemo.entities.User;
import dbsdemo.sql.custom.UserDao;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        // TODO
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

    public Tab getUserListTab() {
        return userListTab;
    }

    public TableView<User> getTableViewUsers() {
        return tableViewUsers;
    }
}
