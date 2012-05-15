package netgest.bo.xwc.xeo.advancedSearch;

import netgest.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A wrapper around the JSON object that keeps information about an object used in the attribute choose
 * viewer
 *
 */
public class ModelInformation{
	
	private String modelName;
	private String attributeName;
	private String parentAttribute;
	private String targetObject;
	
	public ModelInformation( String modelName, String attributeName, String parentAttribute, String targetObject ) {
		this.modelName = modelName;
		this.attributeName = attributeName;
		this.parentAttribute = parentAttribute;
		this.targetObject = targetObject;
	}

	public static ModelInformation fromJSON(String json){
		try {
			JSONObject jsonObj = new JSONObject( json );
			return new ModelInformation( 
					jsonObj.getString( "modelName" ), 
					jsonObj.getString( "attributeName" ), 
					jsonObj.getString( "parentAttribute" ), 
					jsonObj.getString( "targetObject" ) );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getModelName() {
		return modelName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getParentAttribute() {
		return parentAttribute;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public String toString(){
		JSONObject json = new JSONObject();
		try {
			json.put( "modelName", modelName );
			json.put( "attributeName", attributeName );
			if (StringUtils.hasValue( parentAttribute ))
				json.put( "parentAttribute", parentAttribute );
			else
				json.put( "parentAttribute", "" );
			if (StringUtils.hasValue( targetObject ))
				json.put( "targetObject", targetObject );
			else
				json.put( "targetObject", "" );
			
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	
}
