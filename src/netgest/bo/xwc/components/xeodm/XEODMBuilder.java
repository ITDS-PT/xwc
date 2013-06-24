package netgest.bo.xwc.components.xeodm;

import java.util.LinkedHashMap;

public class XEODMBuilder {
	LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
	public void put( String paramName, String paramValue ) {
		paramsMap.put( paramName, paramValue);
	}
	
	public String toUrlString() {
		boolean first = true;
		StringBuilder sb = new StringBuilder("xeodm:");
		for( String key : paramsMap.keySet() ) {
			if( !first ) {
				sb.append( '|' );
			}
			
			sb.append( encodeSpecialChars( key ) ).append('!').append( encodeSpecialChars( paramsMap.get( key ) ) );
			
			first = false;
		}
		return sb.toString();
	}
	
	public String encodeSpecialChars( String param ) {
		StringBuilder sb = new StringBuilder();
		for( char c : param.toCharArray() ) {
			switch( c ) {
				case '|':
					sb.append( '\\' );
					break;
				case '!':
					sb.append( '\\' );
					break;
			}
			sb.append( c );
		}
		return sb.toString();
	}
	
}
