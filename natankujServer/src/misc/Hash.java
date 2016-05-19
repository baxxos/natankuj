package misc;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Hex;

import misc.SHA;
import test.TestBeanRemote;

@Stateless
public class Hash implements TestBeanRemote, SHA {

	@Override
	public String strPlus(String prvy, String druhy) {
		return prvy + druhy;
	}

	@Override
	public String getHash(String string) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(string.getBytes(Charset.forName("UTF-8")));
			return new String(Hex.encodeHex(md.digest()));
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(SHA.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
