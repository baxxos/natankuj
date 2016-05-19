package test;


import javax.ejb.Remote;

@Remote
public interface TestBeanRemote {

	public String strPlus(String prvy, String druhy);
}
