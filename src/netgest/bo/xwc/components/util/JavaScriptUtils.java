package netgest.bo.xwc.components.util;

public class JavaScriptUtils {

	public static final char DEFAULT_CHAR_TO_ESCAPE = '\'';
	
	public static final String safeJavaScriptWrite( CharSequence chars ) {
		return safeJavaScriptWrite(chars, DEFAULT_CHAR_TO_ESCAPE );
	}
	
	public static final String safeJavaScriptWrite( CharSequence chars, final char escapeChar ) {
    	StringBuilder sb = new StringBuilder();
    	safeJavaScriptWrite( sb, chars , escapeChar );
        return sb.toString();
	}
	
	public static final void safeJavaScriptWrite( StringBuilder appendTo, CharSequence chars) {
		safeJavaScriptWrite( appendTo, chars, DEFAULT_CHAR_TO_ESCAPE );
	}
	
	public static final void safeJavaScriptWrite( StringBuilder appendTo, CharSequence chars, final char charToEscape ) {
		char c;
        for (int i = 0; i < chars.length(); i++) {
        	c = chars.charAt( i );
            switch( c ) {
                case '\'':
                    if( charToEscape == '\'' )  
                        appendTo.append( '\\' );
                
                    appendTo.append( '\'' );
                    break;
                case '"':
                    if( charToEscape == '"' )  
                        appendTo.append( '\\' );
                
                    appendTo.append( '"' );
                    break;
                case '\n':
                    appendTo.append( '\\' );
                    appendTo.append( 'n' );
                    break;
                case '\r':
                    //appendTo.append( '\\' );
                    //appendTo.append( 'r' );
                    break;
                case '\\':
                    appendTo.append( '\\' );
                    appendTo.append( '\\' );
                    break;
                case 0:
                	break;
                default:
                    appendTo.append( c );
            }
        }
        
    }
	
	public static final String writeValue( Object value ) {
		if( value == null ) {
			return "";
		}
		return safeJavaScriptWrite( String.valueOf( value ), DEFAULT_CHAR_TO_ESCAPE );
	}

	public static final String writeValue( Object value, char c ) {
		if( value == null ) {
			return "";
		}
		return safeJavaScriptWrite( String.valueOf( value ), c );
	}
	
}
