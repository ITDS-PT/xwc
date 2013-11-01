package netgest.bo.xwc.components.connectors.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.utils.StringUtils;


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
	private ConcatAdapter adapter;
	/**
	 * Taken from another XEO class
	 */
	Pattern p = Pattern.compile( "\\[(([A-Za-z0-9_\\.])*[^\\]*])\\]" );
	
	public CardIDParser(String cardId, boDefHandler definition, ConcatAdapter adapter){
		this.cardId = cardId;
		this.definition = definition;
		this.adapter = adapter;
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
			addAttribute( attributeInCardId , result );
		}
		return result; 
	}

	private void addAttribute(String attributeInCardId, List< String > attributes
			) {
		if (isNonOjectAttribute(attributeInCardId) ) {
				attributes.add( definition.getBoMasterTable()+"."+attributeInCardId ); 
		} else {
			addObjectAttribute(attributeInCardId, attributes);
		}
	}

	private boolean isNonOjectAttribute(String attributeInCardId) {
		return !isAttributeOfObjectRelation( attributeInCardId ) && !isAttributeObject( attributeInCardId );
	}

	private void addObjectAttribute(String attributeInCardId,
			List<String> attributes) {
		String[] parts = attributeInCardId.split("\\."); 
		String attributeNameOfObjectType = parts[0];
		String attributeNameOfTargetObject = "";
		String targetObjectName = definition.getAttributeRef( attributeNameOfObjectType ).getReferencedObjectName();
		boDefHandler handler = boDefHandler.getBoDefinition(targetObjectName);
		if (cardIdContainsDot(parts)){
			attributeNameOfTargetObject = parts[1];
		} else {
			String cardId = handler.getCARDID();
			Matcher m = p.matcher( cardId );
			List<String> concatedAttributes = new LinkedList<String>();
			while (m.find()){
				String newAttribute = m.group( 1 );
				if (handler.getAttributeRef(newAttribute).getAtributeDeclaredType().equals(boDefAttribute.ATTRIBUTE_OBJECT))
					concatedAttributes.add( newAttribute + "$" );
				else
					concatedAttributes.add( newAttribute );
			}
			attributeNameOfTargetObject = adapter.concatColumnsWithSeparator(concatedAttributes, "','");
		}
		
		if (StringUtils.hasValue(attributeNameOfTargetObject) && StringUtils.hasValue(attributeNameOfObjectType)){
			attributes.add( "(select " + attributeNameOfTargetObject+ " from " +
					handler.getBoMasterTable() 
					+ " where BOUI = "
					+ definition.getBoMasterTable() + "."
					+ attributeNameOfObjectType + "$)"
					);
		}
		
	}

	private boolean cardIdContainsDot(String[] parts) {
		return parts.length == 2;
	}

	private boolean isAttributeObject(String attributeInCardId) {
		return boDefAttribute.ATTRIBUTE_OBJECT.equals( 
				definition.getAttributeRef( attributeInCardId ).getAtributeDeclaredType());
	}

	private boolean isAttributeOfObjectRelation(String attributeInCardId) {
		return attributeInCardId.contains( "." );
	}
	
	
}
