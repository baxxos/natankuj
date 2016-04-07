package dbsdemo;

import dbsdemo.entities.Offer;
import dbsdemo.entities.Rating;
import dbsdemo.entities.User;
import dbsdemo.sql.DatabaseControl;
import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.sql.custom.StationDao;
import dbsdemo.entities.Station;
import dbsdemo.misc.CustomAlert;
import dbsdemo.misc.PropLoader;
import dbsdemo.sql.HibernateUtil;
import dbsdemo.sql.custom.CityDao;
import dbsdemo.sql.custom.FuelTypeDao;
import dbsdemo.sql.custom.OfferDao;
import dbsdemo.sql.custom.RatingDao;
import dbsdemo.sql.custom.UserDao;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class MainWindowController implements Initializable {
    // Table related components
    @FXML
    private TableView<Offer> tableView;
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
    private TableColumn<Station, Double> colRating;
    @FXML
    private TableColumn<Station, String> colFuelName;
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
    @FXML
    private Button cancelFilterButton;
    // Combo Boxes
    @FXML
    private ComboBox<String> brandsComboBox;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private ComboBox<String> actionTargetComboBox;
    @FXML
    private ComboBox<String> ratingComboBox;
    @FXML
    private ComboBox<String> fuelTypeComboBox;
    // Misc GUI components
    @FXML
    private Label userNameLabel;
    @FXML
    private Font x1;
    // Controller private attributes
    private User activeUser;
    private ObservableList<String> brands;
    private ObservableList<String> cities;
    private ObservableList<Offer> offers;
    private final ObservableList<String> actions = FXCollections.observableArrayList();
    private final ObservableList<String> actionTargets = FXCollections.observableArrayList();
    @FXML
    private Font x2;
    
    public void populateComboBoxes(){
        
        this.brands = FXCollections.observableArrayList(new StationBrandsDao().getStationBrandsString());
        this.cities = FXCollections.observableArrayList(new CityDao().getCitiesAsString());
        // Station ratings from 0.0 to 5.0
        for(double i = 0.0; i<5.0; i++){
            this.ratingComboBox.getItems().add("<"+i+"-"+(i+1)+">");
        }
        // Fuel types
        this.fuelTypeComboBox.setItems(
                FXCollections.observableArrayList(new FuelTypeDao().getTypesAsString())
        );
        
        this.actionComboBox.setItems(this.actions);
        this.actionTargetComboBox.setItems(this.actionTargets);
        this.cityComboBox.setItems(this.cities);
        this.brandsComboBox.setItems(this.brands);
    }
    
    public void populateTable(){
        
        this.offers = FXCollections.observableArrayList(new OfferDao().getAllAsObjects());

        colBrand.setCellValueFactory(
            new PropertyValueFactory<>("brand")
        );
        colCity.setCellValueFactory(
            new PropertyValueFactory<>("city")
        );
        colLocation.setCellValueFactory(
            new PropertyValueFactory<>("location")
        );
        colRating.setCellValueFactory(
            new PropertyValueFactory<>("ratings")
        );
        colFuelName.setCellValueFactory(
            new PropertyValueFactory<>("fuelType")
        );
        this.tableView.setItems(offers);
    }
    
    private void addContextMenu(Region region){
        
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("Ohodnotiť stanicu"));
        contextMenu.getItems().add(new MenuItem("Aktualizovať cenu")); 
        // Restrict low level users from deleting records
        MenuItem recDeletion = new MenuItem("Vymazať stanicu a súvisiace záznamy");
        recDeletion.setDisable(this.activeUser.getUserLevel() < 2);
        contextMenu.getItems().add(recDeletion);

        region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY){
                    contextMenu.show(tableView , t.getScreenX() , t.getScreenY());
                }
                else {
                    contextMenu.hide();
                }
            }
        });
        
        contextMenu.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                Offer offer = tableView.getSelectionModel().getSelectedItem();
                
                Rating rating = new Rating();
                rating.setStation(offer.getStation());
                rating.setUser(activeUser);
                
                TextInputDialog dialog = new TextInputDialog("5.0");
                dialog.setContentText("Vaše hodnotenie:");
                dialog.setHeaderText("Prosím, zvoľte hodnotu medzi 0.0-5.0");
                dialog.setTitle("Hodnotenie čerpacej stanice");
                
                String response;
                CustomAlert wrongInputAlert = new CustomAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Wrong input format",
                    "Please enter a valid floating point value (double)"
                );
                try {
                    response = dialog.showAndWait().get();
                    double ratingValue = Double.parseDouble(response);
                    if(ratingValue >= 0.0 && ratingValue <= 5.0){
                        rating.setRate(ratingValue);
                    }
                    else {
                        wrongInputAlert.showAndWait();
                        return;
                    }
                }
                catch (NoSuchElementException nse){
                    // User did not provide the rating
                    return;
                }
                catch (NumberFormatException nfe) {
                    wrongInputAlert.showAndWait();
                    Logger.getLogger(MainWindowController.class.getName()).log(Level.WARNING, null, nfe);
                    return;
                }
                // Insert new user rating or update if already exists
                RatingDao ratingDao = new RatingDao();
                if(!ratingDao.updateRating(rating)){
                    ratingDao.insert(rating);
                }
                // Update affected station row                
                MainWindowController.this.offers.set(
                        MainWindowController.this.offers.indexOf(offer),
                        offer
                );
                MainWindowController.this.tableView.getSelectionModel().select(offer);
            }
        });
        
        contextMenu.getItems().get(2).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Station station = tableView.getSelectionModel().getSelectedItem().getStation();
                
                for(Offer offer : MainWindowController.this.offers){
                    if(offer.getStation().getId() == station.getId())
                        MainWindowController.this.offers.remove(offer);
                }
                new StationDao().deleteRecord(station.getId());
            }
        });
    }
    
    private void addUsersTab() throws IOException {
        
        Properties prop = PropLoader.load("etc/config.properties");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("UsersTabPath")));
        Parent root = (Parent) loader.load();
        UsersTabController usersTabController = loader.getController();  
        // Make tables editable if admin user is logged in 
        if(this.activeUser.getUserLevel() > 1){
            usersTabController.enableEditing();
        }
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
    }    
    
    public void enableEditing(){
        // Add context menu for record deletion
        addContextMenu(tableView);
        // Make selected table fields editable if needed
        this.tableView.setEditable(this.activeUser.getUserLevel() > 0);
        this.colLocation.setEditable(this.activeUser.getUserLevel() > 0);
        
        this.colLocation.setCellFactory(TextFieldTableCell.forTableColumn());
        this.colLocation.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Station, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Station, String> t) {
                        
                        Offer offer = tableView.getSelectionModel().getSelectedItem();
                        Station station = offer.getStation();
                        station.setLocation(t.getNewValue());
                        new StationDao().updateStation(station);
                        MainWindowController.this.offers.set(
                                MainWindowController.this.offers.indexOf(offer),
                                offer
                        );
                    }
                }
        );
    }

    @FXML
    private void filterButtonAction(ActionEvent event) throws IOException {
        
        String brandFilter = this.brandsComboBox.getValue();
        String cityFilter = this.cityComboBox.getValue();
        String fuelTypeFilter = this.fuelTypeComboBox.getValue();
        
        this.offers.setAll(new OfferDao().getByAttributes(
            brandFilter == null ? -1 : new StationBrandsDao().getStationBrand(brandFilter).get(0).getId(),
            cityFilter == null ? -1 : new CityDao().getCity(cityFilter).get(0).getId(),
            fuelTypeFilter == null ? -1 : new FuelTypeDao().getFuelType(fuelTypeFilter).getId(),
            this.ratingComboBox.getSelectionModel().getSelectedIndex()
        ));
        
        this.tabPaneMain.getSelectionModel().select(1);
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
                if(action.equals("Upraviť") ||
                    action.equals("Vymazať")){
                    this.tabPaneMain.getSelectionModel().select(1);
                }
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
        // Assign active user - needed for editing etc.
        this.activeUser = activeUser;
        this.userNameLabel.setText("Logged in as: " + (this.activeUser == null ? "-" : this.activeUser.getUsername()));
        // Enable editing - editing level is handled by method itself
        enableEditing();
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
    
    @FXML
    private void cancelFilter(ActionEvent event) {
        
        this.brandsComboBox.valueProperty().set(null);
        this.cityComboBox.valueProperty().set(null);
        this.ratingComboBox.valueProperty().set(null);
        this.fuelTypeComboBox.valueProperty().set(null);
        this.populateTable();
    }
    
    public User getActiveUser() {
        return activeUser;
    } 
}
