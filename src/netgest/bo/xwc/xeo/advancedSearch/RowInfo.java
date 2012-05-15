package netgest.bo.xwc.xeo.advancedSearch;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Wrapper around a JSON object representing information about a row
 * used when a new row should be added and removed
 *
 */
public class RowInfo {
	
	private JSONObject value;
	

	public RowInfo() {
		value = new JSONObject();
	}
	
	public RowInfo( String source ){
		JSONObject object;
		try {
			object = new JSONObject( source );
			this.value = object;
		} catch ( JSONException e ) {
			this.value = new JSONObject();
		}
	}
	
	public RowInfo (int k){
		JSONObject object;
		object = new JSONObject( );
		this.value = object;
		this.setId( String.valueOf( k ) );
	}
	
	public String getId(){
		try {
			return value.getString( "id" );
		} catch ( JSONException e ) {
		}
		return "-1";
	}
	
	public void setId(String id){
		try {
			this.value.put( "id", id );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	public void setOrder(int order){
		try {
			this.value.put( "order", order );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	public int getOrder(){
		try {
			return value.getInt( "order" );
		} catch ( JSONException e ) {
			
		}
		return -1;
	}
	
	public String toString(){
		return value.toString();
	}
}