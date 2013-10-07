package netgest.bo.xwc.components.connectors.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.system.Logger;

public class CardIDSearchQueryCreator {

	private static final Logger logger = Logger.getLogger( CardIDSearchQueryCreator.class );
	
	private DriverUtils databaseUtils;
	private boDefAttribute attributeDefinition;
	private CardIDSearch cardIdSearchResult = new CardIDSearch( "" , new LinkedList< Object >() );
	
	public CardIDSearchQueryCreator(DriverUtils driver,
			boDefAttribute attributeDefinition) {
		this.databaseUtils = driver;
		this.attributeDefinition = attributeDefinition;
	}

	public CardIDSearch formatCardIdSearch(Object parameter)  {
		
		if (attributeDefinition != null){
			boDefHandler handler = attributeDefinition.getReferencedObjectDef();
			if ( isRelationWithSingleModel( handler ) ){
				createSingleQuerySearch( parameter , handler );
			} else {
				createMultipleQuerySearch( parameter );
			}
		}
		return cardIdSearchResult;
	}

	private void createMultipleQuerySearch(Object parameter) {
		boDefHandler[] relations = attributeDefinition.getObjects();
		if (relations != null){
			StringBuilder b = new StringBuilder("([");
			List<Object> parameters = new ArrayList< Object >();
			for (int k = 0 ; k < relations.length ; k++){
				boDefHandler current = relations[k];
				List< String > parts = getCardIdComponents( current );
				if (!parts.isEmpty()){
					String concat = databaseUtils.concatColumnsWithSeparator( parts , "', '" );
					b.append("SELECT BOUI FROM " + current.getBoMasterTable() + " WHERE " + concat + " LIKE UPPER(?) "); 
					if ( hasMoreObjects( relations , k ) )
						b.append ( " UNION ");
					parameters.add( parameter );
				}
			}
			b.append("])");
			cardIdSearchResult = new CardIDSearch( b.toString() , parameters );
		} else {
			//Can't find proper situation, return a value that will not make any harm
			boDefHandler handler = attributeDefinition.getBoDefHandler();
			cardIdSearchResult = new CardIDSearch( "([select BOUI from "+handler.getBoMasterTable()+" where 0=1])" , new LinkedList< Object >() );
		}
		
	}

	private boolean hasMoreObjects(boDefHandler[] relations, int k) {
		return k < relations.length - 1;
	}

	private List< String > getCardIdComponents(boDefHandler current) {
		String cardId = current.getCARDID();
		CardIDParser parser = new CardIDParser( cardId, current, new ConcatAdapter(databaseUtils) );
		List<String> parts = parser.getParts();
		return parts;
	}

	private void createSingleQuerySearch(Object parameter, boDefHandler handler) {
		List< String > parts = getCardIdComponents( handler );
		if (!parts.isEmpty()){
			String concat = databaseUtils.concatColumnsWithSeparator( parts , "', '" );
			String expression = "([SELECT BOUI FROM " + handler.getBoMasterTable() + " WHERE " + concat + " LIKE UPPER(?)])"; ;
			List<Object> params = new ArrayList<Object>();
			params.add( parameter );
			cardIdSearchResult = new CardIDSearch( expression , params );
		} else {
			logger.fine( "Handler of %s did not return parts for its CardID (%s)", handler.getName(), handler.getCARDID() );
		}
	}

	private boolean isRelationWithSingleModel(boDefHandler handler) {
		return handler != null && !"boObject".equalsIgnoreCase( handler.getName() );
	}
	

}
