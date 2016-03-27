package dbsdemo.misc;

import dbsdemo.RegistrationWindowController;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Baxos
 */
public class SHA {
    
    public String getHash(String string){
        
        MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-1");
                    md.update(string.getBytes(Charset.forName("UTF-8")));
                    return new String(Hex.encodeHex(md.digest()));
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(RegistrationWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
    }
}
