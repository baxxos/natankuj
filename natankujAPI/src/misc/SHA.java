package misc;

import javax.ejb.Remote;

@Remote
public interface SHA {

	public String getHash(String string);
}
