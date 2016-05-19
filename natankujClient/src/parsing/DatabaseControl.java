package parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import controllers.MainWindowController;
import dao.CityDaoAPI;
import dao.FuelBrandDaoAPI;
import dao.FuelDaoAPI;
import dao.FuelTypeDaoAPI;
import dao.OfferDaoAPI;
import dao.PriceDaoAPI;
import dao.RatingDaoAPI;
import dao.StationBrandsDaoAPI;
import dao.StationDaoAPI;
import entities.City;
import entities.Fuel;
import entities.FuelBrand;
import entities.Offer;
import entities.Price;
import entities.Station;
import entities.StationBrand;
import main.Main;
import misc.PropLoader;
import gui.CustomAlert;
import javafx.scene.control.Alert;

public class DatabaseControl {

	// Almost all DAO objects are required for a full database access
	private RatingDaoAPI ratingDao;
	private StationDaoAPI stationDao;
	private StationBrandsDaoAPI stationBrandsDao;
	private FuelTypeDaoAPI fuelTypeDao;
	private FuelBrandDaoAPI fuelBrandDao;
	private FuelDaoAPI fuelDao;
	private OfferDaoAPI offerDao;
	private CityDaoAPI cityDao;
	private PriceDaoAPI priceDao;
	
	public DatabaseControl(){
		// Attempt to load remote objects needed for class logic
		try {
			this.ratingDao = (RatingDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("RatingDao"));
			this.stationDao = (StationDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationDao"));
			this.stationBrandsDao = (StationBrandsDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationBrandsDao"));
			this.fuelTypeDao = (FuelTypeDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelTypeDao"));
			this.fuelBrandDao = (FuelBrandDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelBrandDao"));
			this.fuelDao = (FuelDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelDao"));
			this.offerDao = (OfferDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("OfferDao"));
			this.cityDao = (CityDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("CityDao"));
			this.priceDao = (PriceDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("PriceDao"));
		} catch (NamingException e) {
			Logger.getLogger(DatabaseControl.class)
				.fatal("Failed to load a custom DAO instance(s)", e);
		} catch (NullPointerException npe) {
			Logger.getLogger(DatabaseControl.class)
				.fatal("Failed to load a custom DAO instance(s) - missing file path in config.properties", npe);
		}
	}
	
	public void recreateTables() {
		// Load external data sources URL
		Properties prop = PropLoader.load();
		String gasolineUrl = prop.getProperty("gasolineDataSrc");
		String dieselUrl = prop.getProperty("dieselDataSrc");
		try {
			// Drops whole station and price database and re-creates it
			CustomParser parser = new CustomParser();
			
			ratingDao.eraseTable();
			fuelTypeDao.eraseTable();
			fuelTypeDao.insertDefault();

			ArrayList<FuelBrand> fuelBrands = new ArrayList<>(parser.parseFuelBrands(gasolineUrl, true));
			fuelBrandDao.eraseTable();
			fuelBrandDao.insert(fuelBrands);
			fuelBrands = new ArrayList<>(parser.parseFuelBrands(dieselUrl, true));
			fuelBrandDao.insert(fuelBrands);

			ArrayList<Fuel> fuels = new ArrayList<>(parser.parseFuels(gasolineUrl));
			fuelDao.eraseTable();
			fuelDao.insert(fuels);
			fuels = new ArrayList<>(parser.parseFuels(dieselUrl));
			fuelDao.insert(fuels);

			ArrayList<City> cities = new ArrayList<>(parser.parseCities(gasolineUrl, true));
			cityDao.eraseTable();
			cityDao.insert(cities);

			ArrayList<StationBrand> brands = new ArrayList<>(parser.parseStationBrands(gasolineUrl, true));
			stationBrandsDao.eraseTable();
			stationBrandsDao.insert(brands);

			ArrayList<Station> stations = new ArrayList<>(parser.parse(gasolineUrl));
			stationDao.eraseTable();
			stationDao.insert(stations);

			ArrayList<Offer> offers = new ArrayList<>(parser.parseOffers(gasolineUrl));
			offerDao.eraseTable();
			offerDao.insert(offers);
			offers = new ArrayList<>(parser.parseOffers(dieselUrl));
			offerDao.insert(offers);
		} catch (IOException ex) {
			new CustomAlert(Alert.AlertType.ERROR, "Error", "Connection to external data source unavailable",
					"Cannot estabilish connection to " + gasolineUrl).showAndWait();
			Logger.getLogger(MainWindowController.class)
				.fatal("Failed to parse data from the Internet - "
						+ "connection to external data source unavailable", ex);
		}
	}
	
	public void updateTables() {
		// Load external data sources URL
		Properties prop = PropLoader.load();
		String gasolineUrl = prop.getProperty("gasolineDataSrc");
		String dieselUrl = prop.getProperty("dieselDataSrc");
		// Get parser object instance
		CustomParser parser = new CustomParser();
		try {
			List<Price> gasolinePrices = parser.parsePrices(gasolineUrl);
			List<Price> dieselPrices = parser.parsePrices(dieselUrl);
			// Insert the parsed gasoline prices into the database
			for(Price price : gasolinePrices){
				priceDao.insert(price);
			}
			// Insert the parsed diesel prices into the database
			for(Price price : dieselPrices){
				priceDao.insert(price);
			}
		} catch (IOException ex) {
			new CustomAlert(Alert.AlertType.ERROR, "Error", "Connection to external data source unavailable",
					"Cannot estabilish connection to " + gasolineUrl).showAndWait();
			Logger.getLogger(MainWindowController.class).fatal(
					"Failed to parse data from the Internet - " + "connection to external data source unavailable", ex);
		}
	}
}
