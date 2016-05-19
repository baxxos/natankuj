package controllers;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import main.Main;

public class LanguageChanger {

	private ResourceBundle rb;
	private MainWindowController mwc;
	
	public LanguageChanger(MainWindowController mwc){
		try {
			rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
		}
		catch (MissingResourceException e) {
			Logger.getLogger(LoginWindowController.class)
				.fatal("Missing language resource(s) - check src/resource/properties file paths", e);
		}
		this.mwc = mwc;
	}
	
	public void changeWholeScreen(){
		this.changeButtons();
		this.changeDynamicLabels();
		this.changeStaticLabels();
		this.changeMisc();
	}
	
	private void changeStaticLabels(){
		// Load required phrases - static labels
		mwc.labelSearch.setText(rb.getString("search"));
		mwc.labelSearchBrand.setText(rb.getString("stationBrand"));
		mwc.labelSearchCity.setText(rb.getString("city"));
		mwc.labelSearchFuel.setText(rb.getString("fuelType"));
		mwc.labelSearchRating.setText(rb.getString("rating"));
		mwc.labelUserArea.setText(rb.getString("userArea")+":");
		// If the users tab is active change it as well
		if(mwc.getTabPaneMain().getTabs().size() > 2){
			mwc.getTabPaneMain().getTabs().get(2).setText(rb.getString("users"));
		}
	}
	
	private void changeDynamicLabels(){
		// Load required phrases - dynamic labels
		try {
			mwc.labelTotalRatingsUser.setText(
					rb.getString("numberOfRatings") + ": " + 
					mwc.labelTotalRatingsUser.getText().substring(
							mwc.labelTotalRatingsUser.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelTotalRatingsUser.setText(rb.getString("numberOfRatings") + ": 0");
		}
		try {
			mwc.labelAvgUser.setText(
					rb.getString("avgRatings") + ": " +
					mwc.labelAvgUser.getText().substring(
							mwc.labelAvgUser.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelAvgUser.setText(rb.getString("avgRatings") + ": 0" );
		}
		try {
			mwc.labelStationsInDb.setText(
					rb.getString("totalStations") + ": " +
					mwc.labelStationsInDb.getText().substring(
							mwc.labelStationsInDb.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelStationsInDb.setText(rb.getString("totalStations") + ": 0" );
		}
		try {
			mwc.labelLastUpdate.setText(
					rb.getString("lastUpdate") + ": " +
					mwc.labelLastUpdate.getText().substring(
							mwc.labelLastUpdate.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelLastUpdate.setText(rb.getString("lastUpdate") + ": -" );
		}
		try {
			mwc.labelTotalRatings.setText(
					rb.getString("totalRatings") + ": " + 
					mwc.labelTotalRatings.getText().substring(
							mwc.labelTotalRatings.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelTotalRatings.setText(rb.getString("totalRatings") + ": -" );
		}
		try {
			mwc.labelLowestDiesel.setText(
					rb.getString("historicMin") + ": " + 
					mwc.labelLowestDiesel.getText().substring(
							mwc.labelLowestDiesel.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelLowestDiesel.setText(rb.getString("historicMin") + ": -");
		}
		try {
			mwc.labelLowestGasoline.setText(
					rb.getString("historicMin") + ": " + 
					mwc.labelLowestGasoline.getText().substring(
							mwc.labelLowestGasoline.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelLowestGasoline.setText(rb.getString("historicMin") + ": -");
		}
		try {
			mwc.labelAvgDiesel.setText(
					rb.getString("avgPrice") + ": " + 
					mwc.labelAvgDiesel.getText().substring(
							mwc.labelAvgDiesel.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelAvgDiesel.setText(rb.getString("avgPrice") + ": -");
		}
		try {
			mwc.labelAvgGasoline.setText(
					rb.getString("avgPrice") + ": " + 
					mwc.labelAvgGasoline.getText().substring(
							mwc.labelAvgGasoline.getText().indexOf(":") + 2)
			);
		} catch (StringIndexOutOfBoundsException e) {
			mwc.labelAvgGasoline.setText(rb.getString("avgPrice") + ": -");
		}
		mwc.userNameLabel.setText(rb.getString("loggedInAs") + ": " + 
				(mwc.getActiveUser() == null ? "-" : mwc.getActiveUser().getUsername()));
	}
	
	private void changeButtons(){
		// Load required phrases - buttons
		mwc.filterButton.setText(rb.getString("filter"));
		mwc.dropCreateButton.setText(rb.getString("reset"));
		mwc.updateButton.setText(rb.getString("update"));
		mwc.fireUserActionButton.setText(rb.getString("continue"));
	}
	
	private void changeMisc(){
		// Load required phrases - table
		mwc.colBrand.setText(rb.getString("stationBrand"));
		mwc.colCity.setText(rb.getString("city"));
		mwc.colLocation.setText(rb.getString("location"));
		mwc.colFuelName.setText(rb.getString("fuelType"));
		mwc.colFuelBrand.setText(rb.getString("fuelBrand"));
		mwc.colRating.setText(rb.getString("rating"));
		mwc.colPrice.setText(rb.getString("price")+" (€)");
		mwc.colDate.setText(rb.getString("date"));
	}
}
