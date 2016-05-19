package controllers;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import dao.CityDaoAPI;
import dao.FuelBrandDaoAPI;
import dao.FuelDaoAPI;
import dao.FuelTypeDaoAPI;
import dao.OfferDaoAPI;
import dao.StationBrandsDaoAPI;
import dao.StationDaoAPI;
import entities.Offer;
import entities.Station;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import main.Main;
import misc.PropLoader;

// Controller for the station addition window
public class StationWindowController implements Initializable {
    // Private controller attributes and remote objects
    private Station station = new Station();
    private FuelDaoAPI fuelDao;
    private FuelBrandDaoAPI fuelBrandDao;
    private FuelTypeDaoAPI fuelTypeDao;
    private CityDaoAPI cityDao;
    private OfferDaoAPI offerDao;
    private StationDaoAPI stationDao;
    private StationBrandsDaoAPI stationBrandsDao;
    // Language resource bundle
    private ResourceBundle rb;
    // Controller data sets
    private ObservableList<String> cities;
    private ObservableList<String> brands;
    private ObservableList<String> fuelBrands;
    // GUI fxml attributes
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
    private TextField locationTextField;
    @FXML
    private Label labelStationBrand;
    @FXML
    private Label labelFuels;
    @FXML
    private Label labelCity;
    @FXML
    private Label labelLocation;
    
    public StationWindowController(){
		try {
			// Attempt to load remote objects needed for class logic
			this.fuelDao = (FuelDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelDao"));
			this.fuelTypeDao = (FuelTypeDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("FuelTypeDao"));
			this.fuelBrandDao = (FuelBrandDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("FuelBrandDao"));
			this.cityDao = (CityDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("CityDao"));
			this.offerDao = (OfferDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("OfferDao"));
			this.stationDao = (StationDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("StationDao"));
			this.stationBrandsDao = (StationBrandsDaoAPI) 
					Main.ctx.lookup(PropLoader.load().getProperty("StationBrandsDao"));
		} catch (NamingException e) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Failed to load Dao instance(s)", e);
		} catch (NullPointerException npe) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Failed to load custom DAO instance(s) - missing file path in config.properties", npe);
		}
    }
    
    private void populateComboBoxes(){
        this.brands = FXCollections.observableArrayList(stationBrandsDao.getStationBrandsString());
        this.cities = FXCollections.observableArrayList(cityDao.getCitiesAsString());
        this.fuelBrands = FXCollections.observableArrayList(
                fuelBrandDao.getFuelBrandsAsString(
                        fuelTypeGroup.getSelectedToggle().getUserData().toString()
        ));
        
        this.cityComboBox.setItems(this.cities);
        this.brandsComboBox.setItems(this.brands);
        this.fuelBrandsComboBox.setItems(this.fuelBrands);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.radioButtonN95.setUserData(this.radioButtonN95.getText());
        this.radioButtonN98.setUserData(this.radioButtonN98.getText());
        this.radioButtonDiesel.setUserData(this.radioButtonDiesel.getText());
        this.populateComboBoxes();
        
        this.fuelTypeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (fuelTypeGroup.getSelectedToggle() != null) {
                    StationWindowController.this.fuelBrands = FXCollections.observableArrayList(
                             fuelBrandDao.getFuelBrandsAsString(
                                     fuelTypeGroup.getSelectedToggle().getUserData().toString()
                    ));
                    StationWindowController.this.fuelBrandsComboBox.setItems(StationWindowController.this.fuelBrands);
                }
            }
        });
        
		try {
			this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
			// Set the user environment based on locale
			this.updateLanguage();
		}
		catch (MissingResourceException e) {
			Logger.getLogger(StationWindowController.class)
				.fatal("Missing language resource(s) - check src/resource/properties file paths", e);
		}
    }    
    
    private void updateLanguage(){
    	// Update labels
    	this.labelStationBrand.setText(rb.getString("stationBrand"));
    	this.labelFuels.setText(rb.getString("offeredFuels"));
    	this.labelLocation.setText(rb.getString("location"));
    	this.labelCity.setText(rb.getString("city"));
    	
    	// Update buttons
    	this.buttonAddFuelBrand.setText(rb.getString("addFuel"));
    	this.buttonAddStation.setText(rb.getString("addStation"));
    	this.buttonCancel.setText(rb.getString("cancel"));
    	
    	// Update misc components
    	this.locationTextField.setPromptText(rb.getString("locationExact"));
    	this.fuelBrandsComboBox.setPromptText(rb.getString("chooseFuel"));
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
    	// Close the current window without saving any objects
        ((Stage) this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    private void actionSave(ActionEvent event) {
    	// Get all required attributes for saving new offers/stations
        String city = this.cityComboBox.getSelectionModel().getSelectedItem();
        String stationBrand = this.brandsComboBox.getSelectionModel().getSelectedItem();
        String location = (this.locationTextField.getText().length() > 255 ?
                this.locationTextField.getText().substring(0, 256) : 
                this.locationTextField.getText()
        );
        // If all inputs are provided continue to offer addition
        if(city != null && stationBrand != null){
            this.station.setCity(cityDao.getCity(city));
            this.station.setBrand(stationBrandsDao.getStationBrand(stationBrand));
            this.station.setLocation(location.length() > 0 ? location : city);
            
            station = stationDao.insert(station);
            
            for(String fuelName : this.listFuelBrands.getItems()){
                Offer offer = new Offer();
                offer.setFuel(fuelDao.getByAttributes(
                		fuelBrandDao.getFuelBrand(fuelName), 
                		fuelTypeDao.getFuelType(fuelName)));
                offer.setStation(station);
                offerDao.insert(offer);
            }
        }
        // Close the window after saving new offers
        ((Stage) this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    private void actionCancelFilter(ActionEvent event) {
    	// Clears all the items previously added to the fuel brands list
        this.listFuelBrands.getItems().clear();
    }
}