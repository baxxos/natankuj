package dbsdemo.sql;

import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.sql.custom.StationDao;
import dbsdemo.MainWindowController;
import dbsdemo.entities.City;
import dbsdemo.misc.CustomAlert;
import dbsdemo.misc.PropLoader;
import dbsdemo.parsing.StationParser;
import dbsdemo.entities.Station;
import dbsdemo.entities.StationBrand;
import dbsdemo.sql.custom.CityDao;
import dbsdemo.sql.custom.FuelTypeDao;
import dbsdemo.sql.custom.RatingDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

/**
 *
 * @author Baxos
 */
public class DatabaseControl {
    
    public static void recreateTables(){
        // Load external data source URL
        Properties prop = PropLoader.load("etc/config.properties");
        String url = prop.getProperty("mainDataSrc");
        try {
            // Drops whole station and price database and re-creates it
            StationParser parser = new StationParser();
            CityDao cityDao = new CityDao();
            StationDao stationDao = new StationDao();
            StationBrandsDao brandsDao = new StationBrandsDao();
            FuelTypeDao fuelTypeDao = new FuelTypeDao();
            
            new RatingDao().eraseTable();
            fuelTypeDao.eraseTable();
            fuelTypeDao.insertDefault();
            
            ArrayList<City> cities = new ArrayList<>(parser.parseCities(url, true));
            cityDao.eraseTable();
            cityDao.insert(cities);
            
            ArrayList<StationBrand> brands = new ArrayList<>(parser.parseStationBrands(url, true));
            brandsDao.eraseTable();
            brandsDao.insert(brands);
            
            ArrayList<Station> stations = new ArrayList<>(parser.parse(url));
            stationDao.eraseTable();
            stationDao.insert(stations);
        } catch (IOException ex) {
            new CustomAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Connection to external data source unavailable",
                    "Cannot estabilish connection to "+url
            ).showAndWait();
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
