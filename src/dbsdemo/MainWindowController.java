package dbsdemo;

import dbsdemo.entities.User;
import dbsdemo.sql.DatabaseControl;
import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.sql.custom.StationDao;
import dbsdemo.entities.Station;
import dbsdemo.sql.custom.CityDao;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class MainWindowController implements Initializable {
    @FXML
    private TableView<Station> tableView;
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
    @FXML
    private Font x1;
    @FXML
    private Button filterButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button dropCreateButton;
    @FXML
    private ComboBox<String> brandsComboBox;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private Label userNameLabel;
    
    private User activeUser;
    private ObservableList<String> brands;
    private ObservableList<String> cities;
    
    public void populateComboBoxes(){
        
        this.brands = FXCollections.observableArrayList(new StationBrandsDao().getStationBrandsString());
        this.cities = FXCollections.observableArrayList(new CityDao().getCitiesAsString());
        cityComboBox.setItems(this.cities);
        brandsComboBox.setItems(this.brands);
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
        
        tableView.setItems(stations);
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.populateTable();
        this.populateComboBoxes();
        
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

    public TableView<?> getTableView() {
        return tableView;
    }

    public TableColumn<?, ?> getColBrand() {
        return colBrand;
    }

    public TableColumn<?, ?> getColCity() {
        return colCity;
    }

    public TableColumn<?, ?> getColLocation() {
        return colLocation;
    }

    public TableColumn<?, ?> getColRating() {
        return colRating;
    }

    public TableColumn<?, ?> getColFuelName() {
        return colFuelName;
    }

    public TableColumn<?, ?> getColPrice() {
        return colPrice;
    }

    public TableColumn<?, ?> getColDate() {
        return colDate;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        this.userNameLabel.setText("Logged in as: " + (this.activeUser == null ? "-" : this.activeUser.getUsername()));
    }
}
