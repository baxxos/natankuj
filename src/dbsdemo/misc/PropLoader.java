package dbsdemo.misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Baxos
 */
public class PropLoader {
    
    public static Properties load(String propPath){
        Properties prop = new Properties();
        FileInputStream input = null;
        
        try {
            input = new FileInputStream(propPath);
            
            // load a properties file
            prop.load(input);

	} catch (IOException ex) {
            ex.printStackTrace();
	} finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
        
        return prop;
    }
}
