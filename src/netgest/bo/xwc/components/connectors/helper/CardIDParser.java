package netgest.bo.xwc.components.connectors.helper;

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
	/**
	 * Taken from another XEO class
	 */
	Pattern p = Pattern.compile( "\\[(([A-Za-z0-9_])*[^\\]*])\\]" );
	
	public CardIDParser(String cardId){
		this.cardId = cardId;
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
		List<String> attributes = new ArrayList< String >();
		while (m.find()){
			String attribute = m.group( 1 );
			if (!attribute.contains( "." )) //For now don't add attributes of children
				attributes.add( attribute ); 
		}
		return attributes; 
	}
	
	
}
