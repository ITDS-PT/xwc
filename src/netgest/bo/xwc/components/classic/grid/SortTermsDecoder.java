package netgest.bo.xwc.components.classic.grid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Parsed the JSON representation of the sort terms to be applied
 * to a GridPanel. The format is the following:
 * 
 * [{"field":"FIELD_NAME","direction":"ASC|DESC"},{"field":"OTHER_FIELD","direction":"ASC|DESC"}]
 * 
 * @author PedroRio
 *
 */
public class SortTermsDecoder {

	private String sortTerms;
	private boolean validTerms = false;
	
	private Map<String,Direction> decodedTerms = new HashMap<String, Direction>(5);
	
	public enum Direction{
		  ASCENDING("ASC")
		, DESCENDING("DESC")
		, NONE("");
		
		private String name;
		
		private Direction(String name){
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
		  
		public static Direction fromString(String setting){
			if ("ASC".equalsIgnoreCase( setting ))
				return ASCENDING;
			else if ("DESC".equalsIgnoreCase( setting ))
				return DESCENDING;
			else
				return NONE;
		}
	}
	
	public SortTermsDecoder(String terms) {
		this.sortTerms = terms;
		parseTerms();
	}
	
	public SortTermsDecoder(){
		
	}
	
	public void addTerm(String field, Direction direction){
		this.decodedTerms.put( field, direction );
	}
	
	private void parseTerms() {
		
		if (sortTerms == null){
			validTerms = false;
			return;
		}
			
		try {
			JSONArray array = new JSONArray( sortTerms );
			int objects = array.length();
			for (int k = 0 ; k < objects ; k++){
				try{
					JSONObject obj = array.getJSONObject( k );
					Direction direction = Direction.fromString( obj.getString( "direction" ) );
					decodedTerms.put( obj.getString( "field" ), direction );
				} catch (JSONException e){
					//Carry on to other elements
				}
			}
			validTerms = true;
		} catch ( JSONException e ) {
			throw new RuntimeException("JSON is not valid format " + sortTerms);
		}
	}

	public Iterator<String> getSortTerms(){
		return decodedTerms.keySet().iterator();
	}
	
	public int getSortTermsCount(){
		return decodedTerms.size();
	}
	
	public boolean areTermsValid(){
		return sortTerms != null && validTerms;
	}
	
	public Direction getSortDirection(String name){
		return decodedTerms.get( name );
	}
	
	public String toJSON(){
		JSONArray array = new JSONArray();
		for (Iterator<String> it = getSortTerms(); it.hasNext() ; ){
			String term = it.next();
			JSONObject currentSortTerm = new JSONObject();
			try {
				currentSortTerm.put( "field", term );
				currentSortTerm.put( "direction", getSortDirection( term ).getName() );
				
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
			array.put( currentSortTerm );
		}
		return array.toString();
	}
	
	public String toGridPanelInternalFormat(){
		
		StringBuilder b = new StringBuilder(100);
		
		Iterator<String> it = getSortTerms();
		while (it.hasNext()){
			String fieldName = it.next();
			b.append( fieldName.replaceAll("__", ".") );
			b.append("|");
			b.append(getSortDirection( fieldName ).getName());
			if (it.hasNext())
				b.append(",");
				
		}
		
		return b.toString();
	}
	
}
