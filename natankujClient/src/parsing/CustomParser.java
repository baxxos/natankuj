package parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import dao.CityDaoAPI;
import dao.FuelBrandDaoAPI;
import dao.FuelDaoAPI;
import dao.FuelTypeDaoAPI;
import dao.OfferDaoAPI;
import dao.StationBrandsDaoAPI;
import dao.StationDaoAPI;
import dao.UserDaoAPI;
import entities.City;
import entities.Fuel;
import entities.FuelBrand;
import entities.FuelType;
import entities.Offer;
import entities.Price;
import entities.Station;
import entities.StationBrand;
import main.Main;
import misc.PropLoader;

public class CustomParser {

	// Almost all DAO objects are required for a full database access
	private FuelTypeDaoAPI fuelTypeDao;
	private FuelBrandDaoAPI fuelBrandDao;
	private FuelDaoAPI fuelDao;
	private CityDaoAPI cityDao;
	private StationDaoAPI stationDao;
	private StationBrandsDaoAPI stationBrandsDao;
	private OfferDaoAPI offerDao;
	private UserDaoAPI userDao;
	// Lists for storing parsing results
	private List<Station> stations = new ArrayList<>();;
	private List<Offer> offers = new ArrayList<>();;
	private List<Price> prices = new ArrayList<>();;

	/* Custom parsing class providing all HTML code parsing functionality
	 * TODO: too many duplicate methods with similar functionality */
	public CustomParser() {
		try {
			this.fuelTypeDao = (FuelTypeDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelTypeDao"));
			this.fuelBrandDao = (FuelBrandDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelBrandDao"));
			this.fuelDao = (FuelDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelDao"));
			this.cityDao = (CityDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("CityDao"));
			this.stationDao = (StationDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationDao"));
			this.stationBrandsDao = (StationBrandsDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationBrandsDao"));
			this.offerDao = (OfferDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("OfferDao"));
			this.userDao = (UserDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("UserDao"));
		} catch (NamingException e) {
			Logger.getLogger(CustomParser.class).fatal("Failed to load a custom DAO instance(s)", e);
		} catch (NullPointerException npe) {
			Logger.getLogger(CustomParser.class)
					.fatal("Failed to load a custom DAO instance(s)"
							+ " - missing file path in config.properties", npe);
		}
	}
	// Parse (non) unique list of cities
	public List<City> parseCities(String url, boolean unique) throws IOException {
		// Initialize result and temporary unique lists
		List<City> cities = new ArrayList<>();
		List<String> brands = new ArrayList<>();
		List<String> tempList = new ArrayList<>();
		// Load the specified document by url connection
		Document doc = Jsoup.connect(url).get();
		Elements td0 = doc.select("tr > td:eq(0) > a");
		Elements td1 = doc.select("tr > td:eq(1) > a");

		for (Element element : td0) {
			brands.add(element.text());
		}

		String city;
		int i = 0;
		for (Element element : td1) {
			// Eliminacia chybne parsnutych nazvov
			if (!brands.get(i).contains("...")) {
				city = element.attr("title").replace("Èerpacia stanica ", "");
				city = city.replace(brands.get(i) + " ", "");
				city = city.substring(0, city.indexOf(",")).trim();
				// Add or skip current city based on the unique parameter
				if (unique && !tempList.contains(city)) {
					cities.add(new City(city));
					tempList.add(city);
				} else if (!unique)
					cities.add(new City(city));
			}
			i++;
		}
		return cities;
	}
	// Parse (non) unique list of station brands
	public List<StationBrand> parseStationBrands(String url, boolean unique) throws IOException {
		// Initialize result and temporary unique lists
		List<StationBrand> brands = new ArrayList<>();
		List<String> tempList = new ArrayList<>();
		// Load the required document by Jsoup url connection
		Document doc = Jsoup.connect(url).get();
		Elements td0 = doc.select("tr > td:eq(0) > a");

		for (Element element : td0) {
			// Eliminacia chybne parsnutych nazvov
			if (!element.text().contains("...")) {
				// Add or skip current station brand based on unique parameter
				if (unique && !tempList.contains(element.text())) {
					brands.add(new StationBrand(element.text()));
					tempList.add(element.text());
				} else if (!unique)
					brands.add(new StationBrand(element.text()));
			}
		}
		return brands;
	}
	// Parse (non) unique list of fuel brands
	public List<FuelBrand> parseFuelBrands(String url, boolean unique) throws IOException {
		// Initialize result and temporary unique lists
		List<FuelBrand> fuelBrands = new ArrayList<>();
		List<String> tempList = new ArrayList<>();
		// Load the required document by Jsoup url connection
		Document doc = Jsoup.connect(url).get();
		Elements td3 = doc.select("tr > td:eq(3)");

		for (Element element : td3) {
			if (unique && !tempList.contains(element.text())) {
				fuelBrands.add(new FuelBrand(element.text()));
				tempList.add(element.text());
			} else if (!unique)
				fuelBrands.add(new FuelBrand(element.text()));
		}
		return fuelBrands;
	}
	// Parse fuels
	public List<Fuel> parseFuels(String url) throws IOException {

		List<String> brands = new ArrayList<String>();
		List<String> addressFromTitle = new ArrayList<String>();
		List<String> addressFromText = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		List<String> fuelBrands = new ArrayList<String>();
		List<Fuel> fuels = new ArrayList<>();
		List<String> tempList = new ArrayList<>();

		Document doc = Jsoup.connect(url).get();

		Elements td0 = doc.select("tr > td:eq(0) > a");
		Elements td1 = doc.select("tr > td:eq(1) > a");
		Elements td2 = doc.select("tr > td:eq(2) > strong");
		Elements td3 = doc.select("tr > td:eq(3)");

		for (Element element : td0) {
			brands.add(element.text());
		}

		for (Element element : td1) {
			addressFromText.add(element.text());
			addressFromTitle.add(element.attr("title").replace("Èerpacia stanica ", ""));
		}

		for (Element element : td2) {
			prices.add(element.text());
		}

		for (Element element : td3) {
			fuelBrands.add(element.text());
		}

		for (int i = 0; i < brands.size(); i++) {
			prices.set(i, prices.get(i).replace(",", "."));
			addressFromTitle.set(i, addressFromTitle.get(i).replace(brands.get(i) + " ", ""));

			// Odfiltrujeme chybne zapisane znacky - napr. "Benzinol S..."
			if (!brands.get(i).contains("...")) {

				String city = addressFromTitle.get(i).substring(0, addressFromTitle.get(i).indexOf(",")).trim();
				String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i)
						: addressFromTitle.get(i));
				address = address.replace(city + ", ", "").trim();
				String fuelType;
				if (fuelBrands.get(i).contains("95"))
					fuelType = "NAT 95";
				else if (fuelBrands.get(i).contains("98"))
					fuelType = "NAT 98";
				else
					fuelType = "Diesel";

				if (!tempList.contains(fuelBrands.get(i))) {
					fuels.add(
							new Fuel(fuelBrandDao.getFuelBrand(fuelBrands.get(i)), fuelTypeDao.getFuelType(fuelType)));
					tempList.add(fuelBrands.get(i));
				}
			}
		}
		return fuels;
	}

	public List<Offer> parseOffers(String url) throws IOException {

		List<String> brands = new ArrayList<String>();
		List<String> addressFromTitle = new ArrayList<String>();
		List<String> addressFromText = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		List<String> fuelBrands = new ArrayList<String>();

		Document doc = Jsoup.connect(url).get();

		Elements td0 = doc.select("tr > td:eq(0) > a");
		Elements td1 = doc.select("tr > td:eq(1) > a");
		Elements td2 = doc.select("tr > td:eq(2) > strong");
		Elements td3 = doc.select("tr > td:eq(3)");

		for (Element element : td0) {
			brands.add(element.text());
		}

		for (Element element : td1) {
			addressFromText.add(element.text());
			addressFromTitle.add(element.attr("title").replace("Èerpacia stanica ", ""));
		}

		for (Element element : td2) {
			prices.add(element.text());
		}

		for (Element element : td3) {
			fuelBrands.add(element.text());
		}

		for (int i = 0; i < brands.size(); i++) {
			prices.set(i, prices.get(i).replace(",", "."));
			addressFromTitle.set(i, addressFromTitle.get(i).replace(brands.get(i) + " ", ""));

			// Odfiltrujeme chybne zapisane znacky - napr. "Benzinol S..."
			if (!brands.get(i).contains("...")) {

				String city = addressFromTitle.get(i).substring(0, addressFromTitle.get(i).indexOf(",")).trim();
				String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i)
						: addressFromTitle.get(i));
				address = address.replace(city + ", ", "").trim();

				if (fuelTypeDao.getFuelType(fuelBrands.get(i)) != null
						&& cityDao.getCity(city) != null
						&& stationBrandsDao.getStationBrand(brands.get(i)) != null
						&& fuelBrandDao.getFuelBrand(fuelBrands.get(i)) != null
						&& fuelTypeDao.getFuelType(fuelBrands.get(i)) != null) {

					Station actStation = stationDao.getByAttributes(
							cityDao.getCity(city),
							(address.length() <= 0 ? city : address),
							stationBrandsDao.getStationBrand(brands.get(i))
					);
					Fuel actFuel = fuelDao.getByAttributes(
							fuelBrandDao.getFuelBrand(fuelBrands.get(i)),
							fuelTypeDao.getFuelType(fuelBrands.get(i))
					);
					if (actStation != null && actFuel != null) {
						this.offers.add(new Offer(actStation, actFuel));
					}
				}
			}
		}
		return this.offers;
	}
	
	public List<Price> parsePrices(String url) throws IOException {
		// Utility/result lists
		List<String> brands = new ArrayList<String>();
		List<String> addressFromTitle = new ArrayList<String>();
		List<String> addressFromText = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		List<String> fuelBrands = new ArrayList<String>();
		this.prices = new ArrayList<Price>();
		// Load the required HTML document via Jsoup
		Document doc = Jsoup.connect(url).get();
		Elements td0 = doc.select("tr > td:eq(0) > a");
		Elements td1 = doc.select("tr > td:eq(1) > a");
		Elements td2 = doc.select("tr > td:eq(2) > strong");
		Elements td3 = doc.select("tr > td:eq(3)");
		// Split the loaded page table into rows/columns
		for (Element element : td0) {
			brands.add(element.text());
		}

		for (Element element : td1) {
			addressFromText.add(element.text());
			addressFromTitle.add(element.attr("title").replace("Èerpacia stanica ", ""));
		}

		for (Element element : td2) {
			prices.add(element.text());
		}

		for (Element element : td3) {
			fuelBrands.add(element.text());
		}
		
		List<City> cityList = cityDao.getAllAsObjects();
		List<StationBrand> sbList = stationBrandsDao.getAllAsObjects();
		List<FuelBrand> fbList = fuelBrandDao.getAllAsObjects();
		List<Offer> offerList = offerDao.getAllAsObjects();
		
		for (int i = 0; i < brands.size(); i++) {
			// Format the price from 1,20 -> 1.20 due to Java double data type (numberFormatException)
			prices.set(i, prices.get(i).replace(",", "."));
			addressFromTitle.set(i, addressFromTitle.get(i).replace(brands.get(i) + " ", ""));

			// Exclude gas station with corrupted names - e.g. "Benzinol S..."
			if (!brands.get(i).contains("...")) {
				// Format the city/address attributes accordingly (Bratislava, ... -> Bratislava)
				String city = addressFromTitle.get(i).substring(0, addressFromTitle.get(i).indexOf(",")).trim();
				String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i)
						: addressFromTitle.get(i));
				address = address.replace(city + ", ", "").trim();
				// Prepare real entity objects
				City actCity = null;
				StationBrand actSb = null;
				FuelBrand actFb = null;
				/* Iterate through previously loaded lists from database and look for matches. 
				 * Might seem slow but it's actually ~4 times faster than constantly repeated 
				 * database requests */
				for(City c : cityList){
					if(c.getName().equals(city)){
						actCity = c;
						break;
					}
				}
				// Look for required station brand
				for(StationBrand sb : sbList){
					if(sb.getBrand().equals(brands.get(i))){
						actSb = sb;
						break;
					}
				}
				// Look for required fuel brand
				for(FuelBrand fb : fbList){
					if(fb.getBrand().equals(fuelBrands.get(i))){
						actFb = fb;
						break;
					}
				}
				/* Fuel type cannot be matched from list because fuel brand
				 * doesn't have direct relation to fuel type - DB request required */
				FuelType actFt = fuelTypeDao.getFuelType(fuelBrands.get(i));
				// The offer yet to be matched
				Offer actOffer = null;
				if (actFt != null && actCity != null && actSb != null && actFb != null) {
					for(Offer offer : offerList){
						if(offer.getStation().getBrand().getBrand().equals(actSb.getBrand()) && 
						   offer.getStation().getCity().getName().equals(actCity.getName()) &&
						   offer.getStation().getLocation().equals(address.isEmpty() ? actCity.getName() : address) &&
						   offer.getFuelType().equals(actFt.getType())
						   ){
							// Offer has been found - break and persist the price <-> offer pair 
							actOffer = offer;
							break;
						}
					}
					
					if(actOffer != null && actOffer.getId() != 0) {
						this.prices.add(new Price(
							Double.parseDouble(prices.get(i)),
							Calendar.getInstance().getTime(),
							actOffer,
							userDao.getById(1)
						));
					}
				}
			}
		}
		return this.prices;
	}

	public List<Station> parse(String url) throws IOException {
		// Initialise utility lists
		List<String> brands = new ArrayList<String>();
		List<String> addressFromTitle = new ArrayList<String>();
		List<String> addressFromText = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		List<String> fuelTypes = new ArrayList<String>();
		// Try to load HTML document via Jsoup
		Document doc = Jsoup.connect(url).get();
		Elements td0 = doc.select("tr > td:eq(0) > a");
		Elements td1 = doc.select("tr > td:eq(1) > a");
		Elements td2 = doc.select("tr > td:eq(2) > strong");
		Elements td3 = doc.select("tr > td:eq(3)");
		// Split the loaded page table into rows/columns
		for (Element element : td0) {
			brands.add(element.text());
		}

		for (Element element : td1) {
			addressFromText.add(element.text());
			addressFromTitle.add(element.attr("title").replace("Èerpacia stanica ", ""));
		}

		for (Element element : td2) {
			prices.add(element.text());
		}

		for (Element element : td3) {
			fuelTypes.add(element.text());
		}

		for (int i = 0; i < brands.size(); i++) {
			prices.set(i, prices.get(i).replace(",", "."));
			addressFromTitle.set(i, addressFromTitle.get(i).replace(brands.get(i) + " ", ""));

			// Odfiltrujeme chybne zapisane znacky - napr. "Benzinol S..."
			if (!brands.get(i).contains("...")) {

				String city = addressFromTitle.get(i).substring(0, addressFromTitle.get(i).indexOf(",")).trim();
				String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i)
						: addressFromTitle.get(i));
				address = address.replace(city + ", ", "").trim();

				this.stations.add(
					new Station(
						cityDao.getCity(city),
						(address.length() <= 0 ? city : address),
						stationBrandsDao.getStationBrand(brands.get(i)
					)
				));
			}
		}
		return this.stations;
	}
}
