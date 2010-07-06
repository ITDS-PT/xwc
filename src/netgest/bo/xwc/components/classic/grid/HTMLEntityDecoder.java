/*
 * ITDS - Copyright 2009
 * 
 * Classe para converter HTML entities
 * 
 */
package netgest.bo.xwc.components.classic.grid;

public class HTMLEntityDecoder {

	/** The Constant htmlEntities. */
	public final static String htmlEntities[] = { "&quot;", "&apos;", "&amp;",
			"&lt;", "&gt;", "&nbsp;", "&iexcl;", "&cent;", "&pound;",
			"&curren;", "&yen;", "&brvbar;", "&sect;", "&uml;", "&copy;",
			"&ordf;", "&laquo;", "&not;", "&reg;", "&macr;", "&deg;",
			"&plusmn;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;",
			"&middot;", "&cedil;", "&sup1;", "&ordm;", "&raquo;", "&frac14;",
			"&frac12;", "&frac34;", "&iquest;", "&times;", "&divide;",
			"&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;",
			"&AElig;", "&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;",
			"&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;", "&Ntilde;",
			"&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;",
			"&Oslash;", "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;",
			"&Yacute;", "&THORN;", "&szlig;", "&agrave;", "&aacute;",
			"&acirc;", "&atilde;", "&auml;", "&aring;", "&aelig;", "&ccedil;",
			"&egrave;", "&eacute;", "&ecirc;", "&euml;", "&igrave;",
			"&iacute;", "&icirc;", "&iuml;", "&eth;", "&ntilde;", "&ograve;",
			"&oacute;", "&ocirc;", "&otilde;", "&ouml;", "&oslash;",
			"&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;", "&thorn;",
			"&yuml;" };
	
	/** The Constant htmlCodes. */
	public final static String htmlCodes[] = { "&#34;", "&#39;", "&#38;",
			"&#60;", "&#62;", "&#160;", "&#161;", "&#162;", "&#163;", "&#164;",
			"&#165;", "&#166;", "&#167;", "&#168;", "&#169;", "&#170;",
			"&#171;", "&#172;", "&#174;", "&#175;", "&#176;", "&#177;",
			"&#178;", "&#179;", "&#180;", "&#181;", "&#182;", "&#183;",
			"&#184;", "&#185;", "&#186;", "&#187;", "&#188;", "&#189;",
			"&#190;", "&#191;", "&#215;", "&#247;", "&#192;", "&#193;",
			"&#194;", "&#195;", "&#196;", "&#197;", "&#198;", "&#199;",
			"&#200;", "&#201;", "&#202;", "&#203;", "&#204;", "&#205;",
			"&#206;", "&#207;", "&#208;", "&#209;", "&#210;", "&#211;",
			"&#212;", "&#213;", "&#214;", "&#216;", "&#217;", "&#218;",
			"&#219;", "&#220;", "&#221;", "&#222;", "&#223;", "&#224;",
			"&#225;", "&#226;", "&#227;", "&#228;", "&#229;", "&#230;",
			"&#231;", "&#232;", "&#233;", "&#234;", "&#235;", "&#236;",
			"&#237;", "&#238;", "&#239;", "&#240;", "&#241;", "&#242;",
			"&#243;", "&#244;", "&#245;", "&#246;", "&#248;", "&#249;",
			"&#250;", "&#251;", "&#252;", "&#253;", "&#254;", "&#255;" };

	/** The Constant chars. */
	public final static char[] chars = { '"', '\'', '&', '<', '>', ' ', '¡',
			'¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '®', '¯',
			'°', '±', '²', '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼',
			'½', '¾', '¿', '×', '÷', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç',
			'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ð', 'Ñ', 'Ò', 'Ó', 'Ô',
			'Õ', 'Ö', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Þ', 'ß', 'à', 'á', 'â',
			'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï',
			'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'ù', 'ú', 'û', 'ü', 'ý',
			'þ', 'ÿ' };

	/**
	 * Convert a CharSequente to HTML Entities
	 * 
	 * @param s the CharSequence to convert
	 * 
	 * @return the converted CharSequence
	 */
	public static final String charsToHtmlEntity(CharSequence s) {
		StringBuilder ret;
		char c;
		int i;
		boolean bfound;
		ret = new StringBuilder( s.length() );
		for (i = 0; i < s.length(); i++) {
			bfound = false;
			c = s.charAt(i);
			if( c == ' ' ) {
               ret.append( c );
               continue;
			}
			for( int k=0; k < chars.length; k++ ) {
				if( chars[k] == c ) {
					ret.append( htmlEntities[k] );
					bfound = true;
					break;
				}
			}
			if( !bfound ) {
	           if ( c>='a' && c<='z' || c>='A' && c<='Z' || c>='0' && c<='9' )
	           {
	               ret.append( c );
	           }
	           else
	           {
	               ret.append("&#").append((int)c).append(";");
	           }
			}
		}
		return ret.toString();
		
	}
	
	/**
	 * Convert a CharSequente with HTML Entities to normal characters
	 * 
	 * @param s the CharSequence to convert
	 * 
	 * @return the converted CharSequence
	 */
	public static final String htmlEntityToChar(CharSequence s) {

		boolean entityStart = false;
		boolean codeStart = false;

		StringBuilder ret = new StringBuilder();
		StringBuilder buffer = new StringBuilder();
		int i;
		char c;
		for (i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			switch (c) {
			case '&':
				// Malformed.. 
				if( entityStart ) {
					ret.append( buffer );
				}
				entityStart = true;
				break;
			case '#':
				if (i > 0 && s.charAt(i - 1) == '&')
					codeStart = true;
				
				break;
			case ';':
				if (entityStart) {
					buffer.append(c);
					if (codeStart)
						ret.append(
								convert(buffer.toString(), htmlCodes,
										chars));
					else
						ret.append(
								convert(buffer.toString(), htmlEntities,
										chars));
					buffer.delete(0, buffer.length());
					entityStart = false;
					continue;
				}
				break;
			}
			if (!entityStart)
				ret.append(c);
			else {
				buffer.append(c);
				if( buffer.length() > 15 ) {
					ret.append( buffer );
					buffer.delete(0, buffer.length() );
					entityStart = false;
				}
			}
		}
		ret.append(buffer);
		return ret.toString();
	}

	private static final String convert(String buffer, String[] entities,
			char[] chars) {
		String ret = null;
		int i;
		for (i = 0; i < entities.length; i++) {
			if (entities[i].equals(buffer)) {
				ret = String.valueOf(chars[i]);
			}
		}
		if (ret == null) {
			ret = buffer;
		}
		return ret;
	}

}
