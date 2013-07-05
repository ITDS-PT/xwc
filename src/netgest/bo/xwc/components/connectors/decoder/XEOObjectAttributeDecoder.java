package netgest.bo.xwc.components.connectors.decoder;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;

public class XEOObjectAttributeDecoder {

	private boDefHandler base;
	public static final String DOT_SEPARATOR_REGEX = "\\.";
	public static final String DOT_SEPARATOR = ".";
	public static final String UNDERSCORE_SEPARATOR = "__";
	public static final String UNDERSCORE_SEPARATOR_REGEX = "__";

	public XEOObjectAttributeDecoder(boDefHandler parent) {
		this.base = parent;
	}

	public boDefAttribute decode(String attributeName) {
		boDefAttribute result = base.getAttributeRef( attributeName );
		if ( result != null )
			return result;
		result = findWithSeparator( attributeName , DOT_SEPARATOR , DOT_SEPARATOR_REGEX );
		if ( result != null )
			return result;
		else
			result = findWithSeparator( attributeName , UNDERSCORE_SEPARATOR 
					, UNDERSCORE_SEPARATOR_REGEX );

		return result;
	}

	private boDefAttribute findWithSeparator(String attributeName,
			String separator, String regex) {
		boDefHandler parent = base;
		if ( attributeName.contains( separator ) ) {
			String[] relationAttribute = attributeName.split( regex );
			int size = relationAttribute.length;
			boDefAttribute targetAttributeDefinition = null;
			for ( int i = 0 ; i < size ; i++ ) {
				String parentAtt = relationAttribute[i];
				if ( i + 1 < size ) {
					String childAtt = relationAttribute[i + 1];
					boDefAttribute defAttRel = parent.getAttributeRef( parentAtt );
					if ( defAttRel != null ) {
						boDefHandler defModelRel = defAttRel.getReferencedObjectDef();
						if ( defModelRel != null ) {
							targetAttributeDefinition = defModelRel.getAttributeRef( childAtt );
							parent = defModelRel;
						}
					}
				}
			}
			return targetAttributeDefinition;
		}
		return null;
	}
	
	

	public String toString() {
		return base.getName();
	}
}
