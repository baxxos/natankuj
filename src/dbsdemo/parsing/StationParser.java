package dbsdemo.parsing;

import dbsdemo.entities.City;
import dbsdemo.entities.Fuel;
import dbsdemo.sql.custom.StationBrandsDao;
import dbsdemo.entities.FuelBrand;
import dbsdemo.entities.Offer;
import dbsdemo.entities.Station;
import dbsdemo.entities.StationBrand;
import dbsdemo.sql.custom.CityDao;
import dbsdemo.sql.custom.FuelBrandDao;
import dbsdemo.sql.custom.FuelDao;
import dbsdemo.sql.custom.FuelTypeDao;
import dbsdemo.sql.custom.StationDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Baxos
 */
public class StationParser {

    private List<Station> stations;
    private List<Offer> offers;

    public StationParser() {
        this.stations = new ArrayList<>();
        this.offers = new ArrayList<>();
    }
    
    public List<City> parseCities(String url, boolean unique) throws IOException {
        
        Document doc = Jsoup.connect(url).get();
        List<City> cities = new ArrayList<>();
        List<String> brands = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        Elements td0 = doc.select("tr > td:eq(0) > a");
        Elements td1 = doc.select("tr > td:eq(1) > a");
        
        for (Element element : td0) {
            brands.add(element.text());
        }
        
        String city;
        int i = 0;
        for (Element element : td1) {
            // Eliminacia chybne parsnutych nazvov
            if(!brands.get(i).contains("...")){
                city = element.attr("title").replace("Čerpacia stanica ", "");
                city = city.replace(brands.get(i) + " ", "");
                city = city.substring(0, city.indexOf(",")).trim();

                if(unique && !tempList.contains(city)){
                    cities.add(new City(city));
                    tempList.add(city);
                }
                else if(!unique)
                    cities.add(new City(city));
            }
            i++;
        }
        return cities;
    }
    
    public List<StationBrand> parseStationBrands(String url, boolean unique) throws IOException {
        
        List<StationBrand> brands = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        
        Document doc = Jsoup.connect(url).get();
        Elements td0 = doc.select("tr > td:eq(0) > a");

        for (Element element : td0) {
            // Eliminacia chybne parsnutych nazvov
            if(!element.text().contains("...")){
                if(unique && !tempList.contains(element.text())){
                    brands.add(new StationBrand(element.text()));
                    tempList.add(element.text());
                }
                else if(!unique)
                    brands.add(new StationBrand(element.text()));
            }
        }
        
        return brands;
    }
    
    public List<FuelBrand> parseFuelBrands(String url, boolean unique) throws IOException{
        
        List<FuelBrand> fuelBrands = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        
        Document doc = Jsoup.connect(url).get();
        Elements td3 = doc.select("tr > td:eq(3)");

        for (Element element : td3) {
            if(unique && !tempList.contains(element.text())){
                fuelBrands.add(new FuelBrand(element.text()));
                tempList.add(element.text());
            }
            else if(!unique)
                fuelBrands.add(new FuelBrand(element.text()));
        }
        return fuelBrands;
    }
    
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
            addressFromTitle.add(element.attr("title").replace("Čerpacia stanica ", ""));
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
                String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i) : addressFromTitle.get(i));
                address = address.replace(city + ", ", "").trim();
                String fuelType;
                if(fuelBrands.get(i).contains("95"))
                    fuelType = "NAT 95";
                else if(fuelBrands.get(i).contains("98"))
                    fuelType = "NAT 98";
                else
                    fuelType = "Diesel";
                
                if(!tempList.contains(fuelBrands.get(i))){
                    fuels.add(new Fuel(
                            new FuelBrandDao().getFuelBrand(fuelBrands.get(i)),
                            new FuelTypeDao().getFuelType(fuelType)
                    ));
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
            addressFromTitle.add(element.attr("title").replace("Čerpacia stanica ", ""));
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
                String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i) : addressFromTitle.get(i));
                address = address.replace(city + ", ", "").trim();
                
                
                if(new FuelTypeDao().getFuelType(fuelBrands.get(i)) != null &&
                   new CityDao().getCity(city) != null &&
                   new StationBrandsDao().getStationBrand(brands.get(i)) != null &&
                   new FuelBrandDao().getFuelBrand(fuelBrands.get(i)) != null &&
                   new FuelTypeDao().getFuelType(fuelBrands.get(i)) != null){
                    
                    Station actStation = new StationDao().getByAttributes(
                                    new CityDao().getCity(city),
                                    (address.length() <= 0 ? city : address),
                                    new StationBrandsDao().getStationBrand(brands.get(i))
                            );
                    Fuel actFuel = new FuelDao().getByAttributes(
                                    new FuelBrandDao().getFuelBrand(fuelBrands.get(i)),
                                    new FuelTypeDao().getFuelType(fuelBrands.get(i))
                            );
                    if(actStation != null && actFuel != null){
                    this.offers.add(new Offer(
                            actStation, actFuel
                    ));
                    }
                }
            }
        }
        return this.offers;
    }

    public List<Station> parse(String url) throws IOException {
        
        List<String> brands = new ArrayList<String>();
        List<String> addressFromTitle = new ArrayList<String>();
        List<String> addressFromText = new ArrayList<String>();
        List<String> prices = new ArrayList<String>();
        List<String> fuelTypes = new ArrayList<String>();

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
            addressFromTitle.add(element.attr("title").replace("Čerpacia stanica ", ""));
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
                String address = (addressFromTitle.get(i).contains("...") ? addressFromText.get(i) : addressFromTitle.get(i));
                address = address.replace(city + ", ", "").trim();
                
                this.stations.add(new Station(
                        new CityDao().getCity(city),
                        (address.length() <= 0 ? city : address),
                        new StationBrandsDao().getStationBrand(brands.get(i))
                ));
            }
        }
        return this.stations;
    }
}
