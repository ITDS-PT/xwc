package netgest.bo.xwc.xeo.beans;

/**
 * 
 * Bean to Support Showing differences between an object and another 
 * version of the same object
 * 
 */
public class ShowVersionDifferenceBean extends XEOBaseBean {

	private String differenceResult = "";
	
	public void setDifferences(String result){
		this.differenceResult = result;
	}
	
	public String getDifferences(){
		return differenceResult;
	}
	
}
