package netgest.bo.xwc.components.util;

public class JavaScriptUtils {

//    public static final String safeJavaScriptWrite( CharSequence sString, final char escapeChar ) {
//    	if( sString != null ) {
//    		
//        	StringBuilder sb = new StringBuilder( sString.length() );
//    		safeJavaScriptWrite( sb, sString.toCharArray() , escapeChar );
//    		return sb.toString();
//    	}
//    	return "";
//    }

//	public static final String safeJavaScriptWrite( char[] chars, final char escapeChar ) {
//    	StringBuilder sb = new StringBuilder();
//    	safeJavaScriptWrite( sb, chars , escapeChar );
//        return sb.toString();
//    }
	
	public static final String safeJavaScriptWrite( CharSequence chars, final char escapeChar ) {
    	StringBuilder sb = new StringBuilder();
    	safeJavaScriptWrite( sb, chars , escapeChar );
        return sb.toString();
	}
	
	public static final void safeJavaScriptWrite( StringBuilder appendTo, CharSequence chars, final char escapeChar ) {
		
        for (int i = 0; i < chars.length(); i++) {
        	char c = chars.charAt( i );
            switch( c ) {
                case '\'':
                    if( escapeChar == '\'' )  
                        appendTo.append( '\\' );
                
                    appendTo.append( '\'' );
                    break;
                case '"':
                    if( escapeChar == '"' )  
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
                default:
                    appendTo.append( c );
            }
        }
        
    }
}
