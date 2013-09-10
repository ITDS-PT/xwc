package netgest.bo.xwc.components.connectors.helper;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Utility class to parse CardID definitions
 * 
 * @author PedroRio
 *
 */
public class CardIDParser {
	
	private String cardId;
	private boDefHandler definition;
	/**
	 * Taken from another XEO class
	 */
	Pattern p = Pattern.compile( "\\[(([A-Za-z0-9_])*[^\\]*])\\]" );
	
	public CardIDParser(String cardId, boDefHandler definition){
		this.cardId = cardId;
		this.definition = definition;
	}
	
	/**
	 * 
	 * Return the attributes present in the cardId as a list of strings
	 * 
	 * @return the list of attributes in the cardid
	 */
	public List<String> getParts(){
		
		if (StringUtils.isEmpty( cardId ))
			return new ArrayList< String >();
		
		Matcher m = p.matcher( cardId );
		List<String> result = new ArrayList< String >();
		while (m.find()){
			String attributeInCardId = m.group( 1 );
			addNonObjectAttribute( attributeInCardId , result );
		}
		return result; 
	}

	private void addNonObjectAttribute(String attributeInCardId, List< String > attributes
			) {
		if (!isAttributeOfObjectRelation( attributeInCardId )) {
			if (!isAttributeObject( attributeInCardId ) ) {
				attributes.add( definition.getBoMasterTable()+"."+attributeInCardId ); 
			}
		}
	}

	private boolean isAttributeObject(String attributeInCardId) {
		return boDefAttribute.ATTRIBUTE_OBJECT.equals( 
				definition.getAttributeRef( attributeInCardId ).getAtributeDeclaredType());
	}

	private boolean isAttributeOfObjectRelation(String attributeInCardId) {
		return attributeInCardId.contains( "." );
	}
	
	
}
