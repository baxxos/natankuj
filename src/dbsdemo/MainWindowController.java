package dbsdemo;

import dbsdemo.entities.User;
import dbsdemo.sql.DatabaseControl;
import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.sql.custom.StationDao;
import dbsdemo.entities.Station;
import dbsdemo.misc.CustomAlert;
import dbsdemo.misc.PropLoader;
import dbsdemo.sql.custom.CityDao;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class MainWindowController implements Initializable {
    // Table related components
    @FXML
    private TableView<Station> tableView;
    private TableView<User> tableViewUsers;
    @FXML
    private TabPane tabPaneMain;
    @FXML
    private TableColumn<Station, String> colBrand;
    @FXML
    private TableColumn<Station, String> colCity;
    @FXML
    private TableColumn<Station, String> colLocation;
    @FXML
    private TableColumn<?, ?> colRating;
    @FXML
    private TableColumn<?, ?> colFuelName;
    @FXML
    private TableColumn<?, ?> colPrice;
    @FXML
    private TableColumn<?, ?> colDate;
    // Button GUI components
    @FXML
    private Button fireUserActionButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button dropCreateButton;
    // Combo Boxes
    @FXML
    private ComboBox<String> brandsComboBox;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private ComboBox<String> actionTargetComboBox;
    // Misc GUI components
    @FXML
    private Label userNameLabel;
    @FXML
    private Font x1;
    // Controller private attributes
    private User activeUser;
    private ObservableList<String> brands;
    private ObservableList<String> cities;
    private final ObservableList<String> actions = FXCollections.observableArrayList();
    private final ObservableList<String> actionTargets = FXCollections.observableArrayList();
    
    public void populateComboBoxes(){
        
        this.brands = FXCollections.observableArrayList(new StationBrandsDao().getStationBrandsString());
        this.cities = FXCollections.observableArrayList(new CityDao().getCitiesAsString());
        
        this.actionComboBox.setItems(this.actions);
        this.actionTargetComboBox.setItems(this.actionTargets);
        this.cityComboBox.setItems(this.cities);
        this.brandsComboBox.setItems(this.brands);
    }
    
    public void populateTable(){
        ObservableList<Station> stations = FXCollections.observableArrayList(new StationDao().getAllAsObjects());

        colBrand.setCellValueFactory(
            new PropertyValueFactory<>("brand")
        );
        colCity.setCellValueFactory(
            new PropertyValueFactory<>("city")
        );
        colLocation.setCellValueFactory(
            new PropertyValueFactory<>("location")
        );
        this.tableView.setItems(stations);
    }
    
    private void addContextMenu(Region region, ContextMenu menu){
        
        region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY){
                    menu.show(tableView , t.getScreenX() , t.getScreenY());
                }
            }
        });
    }
    
    private void addUsersTab() throws IOException {
        
        Properties prop = PropLoader.load("etc/config.properties");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("UsersTabPath")));
        Parent root = (Parent) loader.load();
        UsersTabController usersTabController = loader.getController();  
        // Add user list tab to current view and make it active
        this.tabPaneMain.getTabs().add(usersTabController.getUserListTab());
        this.tabPaneMain.getSelectionModel().select(this.tabPaneMain.getTabs().size()-1);
        this.tableViewUsers = usersTabController.getTableViewUsers();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate content in comboBoxes and tableViews
        this.populateTable();
        this.populateComboBoxes();
        this.tabPaneMain.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        // Add context menu on tableView items
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("Ohodnotiť stanicu"));
        contextMenu.getItems().add(new MenuItem("Aktualizovať cenu"));        
        addContextMenu(tableView, contextMenu);
    }    

    @FXML
    private void filterButtonAction(ActionEvent event) throws IOException {
        
    }
    
    @FXML
    private void updateDatabase(ActionEvent event) {
    }

    @FXML
    private void dropCreateDatabase(ActionEvent event) {
        
        DatabaseControl.recreateTables();
        this.populateComboBoxes();
        this.populateTable();
    }
    
    @FXML
    private void fireUserAction(ActionEvent event) throws IOException {
        
        try {
            String action = this.actionComboBox.getValue();
            String actionTarget = this.actionTargetComboBox.getValue();
            
            if(actionTarget.equals("Používateľ")){
                if(action.equals("Pridať")){
                    goToRegWindowScene();
                }
                else if((action.equals("Upraviť") ||
                        action.equals("Vymazať")) &&
                        this.tabPaneMain.getTabs().size() < 3){
                        addUsersTab();
                }
            }
            else if(actionTarget.equals("Čerpacia stanica")){
                ;
            }
        } catch(NullPointerException e){
            //Nothing selected in combo boxes
            new CustomAlert(
                    Alert.AlertType.WARNING,
                    "Warning",
                    "Action unsuccessful",
                    "Please select a valid action and action target"
            ).showAndWait();
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

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
            regWindowController.userLevelAvailable(this.activeUser.getUserLevel() > 1);
            regWindowController.setTriggerMainWindow(false);
            regWindowController.setActStage(regWindowStage);
            
            regWindowStage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        this.userNameLabel.setText("Logged in as: " + (this.activeUser == null ? "-" : this.activeUser.getUsername()));
        // Control the user actions based on userLevel
        if(this.activeUser.getUserLevel() > 0){
            this.actions.add("Pridať");
            this.actions.add("Upraviť");
            this.actionTargets.add("Čerpacia stanica");
            this.actionTargets.add("Používateľ");
        }
        else {
            this.fireUserActionButton.setDisable(true);
            this.actionComboBox.setDisable(true);
            this.actionTargetComboBox.setDisable(true);
        }
        // superUser (admin) actions
        if(this.activeUser.getUserLevel() > 1){
            this.actions.add("Vymazať");
        }
    }
    
    public User getActiveUser() {
        return activeUser;
    }
}
