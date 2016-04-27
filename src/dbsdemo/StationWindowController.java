/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbsdemo;

import dbsdemo.elastic.ElasticClient;
import dbsdemo.entities.Offer;
import dbsdemo.entities.Station;
import dbsdemo.sql.DatabaseControl;
import dbsdemo.sql.HibernateUtil;
import dbsdemo.sql.custom.CityDao;
import dbsdemo.sql.custom.FuelBrandDao;
import dbsdemo.sql.custom.FuelDao;
import dbsdemo.sql.custom.FuelTypeDao;
import dbsdemo.sql.custom.OfferDao;
import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.sql.custom.StationDao;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Baxos
 */
public class StationWindowController implements Initializable {
   
    private final Station station = new Station();
    private ObservableList<String> cities;
    private ObservableList<String> brands;
    private ObservableList<String> fuelBrands;
    private final ElasticClient client = new ElasticClient();
    @FXML
    private ComboBox<String> brandsComboBox;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private ComboBox<String> fuelBrandsComboBox;
    @FXML
    private Button buttonAddFuelBrand;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonAddStation;
    @FXML
    private ListView<String> listFuelBrands;
    @FXML
    private ToggleGroup fuelTypeGroup;
    @FXML
    private RadioButton radioButtonN95;
    @FXML
    private RadioButton radioButtonN98;
    @FXML
    private RadioButton radioButtonDiesel;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ComboBox<String> locationComboBox;
    
    public void populateComboBoxes(){
        
        this.brands = FXCollections.observableArrayList(new StationBrandsDao().getStationBrandsString());
        this.cities = FXCollections.observableArrayList(new CityDao().getCitiesAsString());
        this.fuelBrands = FXCollections.observableArrayList(
                new FuelBrandDao().getFuelBrandsAsString(
                        fuelTypeGroup.getSelectedToggle().getUserData().toString()
        ));
        
        this.cityComboBox.setItems(this.cities);
        this.brandsComboBox.setItems(this.brands);
        this.fuelBrandsComboBox.setItems(this.fuelBrands);
    }
    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.radioButtonN95.setUserData(this.radioButtonN95.getText());
        this.radioButtonN98.setUserData(this.radioButtonN98.getText());
        this.radioButtonDiesel.setUserData(this.radioButtonDiesel.getText());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StationWindowController.this.populateComboBoxes();
            }
        });

        this.fuelTypeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (fuelTypeGroup.getSelectedToggle() != null) {
                    StationWindowController.this.fuelBrands = FXCollections.observableArrayList(
                             new FuelBrandDao().getFuelBrandsAsString(
                                    fuelTypeGroup.getSelectedToggle().getUserData().toString()
                    ));
                    StationWindowController.this.fuelBrandsComboBox.setItems(StationWindowController.this.fuelBrands);
                }
            }
        });

        FxUtil.autoCompleteComboBox(locationComboBox, this.client);     
    }    

    @FXML
    private void actionAddToList(ActionEvent event) {
        String selection = this.fuelBrandsComboBox.getSelectionModel().getSelectedItem();
        
        if(selection != null && !this.listFuelBrands.getItems().contains(selection)){
            this.listFuelBrands.getItems().add(selection);
        }
    }

    @FXML
    private void actionCancel(ActionEvent event) {
        ((Stage) this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    private void actionSave(ActionEvent event) {
        String city = this.cityComboBox.getSelectionModel().getSelectedItem();
        String stationBrand = this.brandsComboBox.getSelectionModel().getSelectedItem();
        String location = (FxUtil.getComboBoxValue(locationComboBox).length() > 255 ?
                FxUtil.getComboBoxValue(locationComboBox).substring(0, 256) : 
                FxUtil.getComboBoxValue(locationComboBox)
        );

        if(city != null && stationBrand != null){
            this.station.setCity(new CityDao().getCity(city));
            this.station.setBrand(new StationBrandsDao().getStationBrand(stationBrand));
            this.station.setLocation(location.length() > 0 ? location : city);
            new StationDao().insert(station);
            
            client.insert(station);
            
            FuelDao fuelDao = new FuelDao();
            FuelBrandDao fuelBrandDao = new FuelBrandDao();
            FuelTypeDao fuelTypeDao = new FuelTypeDao();
            OfferDao offerDao = new OfferDao();

            for(String fuelName : this.listFuelBrands.getItems()){
                Offer offer = new Offer();
                offer.setFuel(fuelDao.getByAttributes(fuelBrandDao.getFuelBrand(fuelName), fuelTypeDao.getFuelType(fuelName)));
                offer.setStation(station);
                offerDao.insert(offer);
            }
        }
        ((Stage) this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    private void actionCancelFilter(ActionEvent event) {
        this.listFuelBrands.getItems().clear();
    }
}
