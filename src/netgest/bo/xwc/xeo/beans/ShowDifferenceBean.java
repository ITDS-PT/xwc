package netgest.bo.xwc.xeo.beans;

/**
 * 
 * Bean to Support Showing differences bettwen an object and an unsaved  
 * version of the same object
 * 
 */
public class ShowDifferenceBean extends XEOBaseBean {

	private String differenceResult = "";
	
	public void setDifferences(String result){
		this.differenceResult = result;
	}
	
	public String getShowDifferences(){
		return differenceResult;
	}
	
}
