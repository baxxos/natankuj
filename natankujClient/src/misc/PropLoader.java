package misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import main.Main;

/* Utility class which sole purpose is to fetch
 * a property file and return it */
public class PropLoader {
	public static final String configProps = "src/resources/config.properties";
	// Method returning the default property file
	public static Properties load() {
		Properties prop = new Properties();
		FileInputStream input = null;

		try {
			input = new FileInputStream(PropLoader.configProps);
			// Load properties from a file into the prop instance
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName())
				.fatal("Failed to load property file (" + PropLoader.configProps + ")", ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.getLogger(Main.class.getName())
						.error("Failed to close Input stream (" + PropLoader.configProps + ")", e);
				}
			}
		}
		Logger.getLogger(Main.class.getName())
			.debug("Property file loaded successfully (" + PropLoader.configProps + ")");
		return prop;
	}
	// Method returning the loaded property file
	public static Properties load(String filePath) {
		Properties prop = new Properties();
		FileInputStream input = null;

		try {
			input = new FileInputStream(filePath);
			// Load properties from a file into the prop instance
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName())
				.fatal("Failed to load property file (" + filePath + ")", ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.getLogger(Main.class.getName())
						.error("Failed to close Input stream (" + filePath + ")", e);
				}
			}
		}
		Logger.getLogger(Main.class.getName())
			.debug("Property file loaded successfully (" + filePath + ")");
		return prop;
	}
	
	public static void save(String propName, String propValue){
		// Load config.properties
		Properties props = PropLoader.load();
		props.setProperty(propName, propValue);
		// Try to save new property
		FileOutputStream out;
		try {
			out = new FileOutputStream(PropLoader.configProps);
			props.store(out, null);
		} catch (FileNotFoundException e) {
			Logger.getLogger(PropLoader.class)
				.fatal("Failed to load config.properties file while saving new property", e);
		} catch (IOException e) {
			Logger.getLogger(PropLoader.class)
				.fatal("Failed to store new property value", e);
		}
	}
}
