package netgest.bo.xwc.xeo.beans;

/**
 * 
 * Bean to Support Showing the logs of different versions of an object
 * 
 */
public class ShowLogsVersionBean extends XEOBaseBean {

	private String differenceResult = "";
	
	public void setDifferences(String result){
		this.differenceResult = result;
	}
	
	public String getDifferences(){
		return differenceResult;
	}
	
}
