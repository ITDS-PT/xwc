package netgest.bo.xwc.framework.localization;

import java.util.regex.Pattern;

import netgest.utils.StringUtils;

public class JavaToJavascriptPatternConverter {

	public static String convertDatePatternToJavascript(String javaPattern) {

		String js = convertYears( javaPattern );
		
		js = convertMonths( js );

		js = convertDaysInMonth( js );

		js = convertDaysInWeek( js );

		return js;
	}

	public static String convertTimePatternToJavascript(String javaPattern) {
		String js = convertHours( javaPattern );
		js = convertMinutes( js );
		return js;
	}

	private static String convertMinutes(String js) {
		if ( js.contains( "m" ) )
			return js = js.replaceAll( "[m]+" , "i" );
		return js;
	}

	private static String convertHours(String js) {		
		if ( js.contains( "H" ) )
			js = js.replaceAll( "[H]+" , "H" );
		else if ( js.contains( "k" ) )
			js = js.replaceAll( "[k]+" , "H" );
		
		else if ( js.contains( "K" ) )
			js = js.replaceAll( "[K]+" , "h" );
		else if ( js.contains( "h" ) )
			js = js.replaceAll( "[h]+" , "h" );
		
		return js;
	}

	private static String convertDaysInWeek(String js) {
		js = js.replaceAll( "[E]+" , "D" );
		return js;
	}

	private static String convertDaysInMonth(String js) {
		js = js.replace( "dd" , "d" );
		return js;
	}

	private static String convertMonths(String js) {
		if ( js.contains( "MMM" ) )
			js = js.replace( "MMM" , "M" );
		else if ( js.contains( "MM" ) )
			js = js.replace( "MM" , "m" );
		else if ( js.contains( "M" ) )
			js = js.replace( "M" , "m" );
		return js;
	}

	private static String convertYears(String javaPattern) {
		String js = javaPattern;
		if ( js.contains( "yyyy" ) )
			js = js.replace( "yyyy" , "Y" );
		else if ( js.contains( "yy" ) )
			js = js.replace( "yy" , "Y" );
		return js;
	}

	private static final String[] separators = new String[] { "/", ".", "-" };

	public static String getAlternativeDateFormats(String jsPattern) {
		
		int dayIndex = jsPattern.indexOf( "d" );
		int monthIndex = jsPattern.indexOf( "m" );
		
		boolean isDayFirst = dayIndex < monthIndex;
		
		StringBuilder b = new StringBuilder();
		String append = "";
		String separatorInPattern = "";
		for ( String separator : separators ) {
			if ( jsPattern.contains( separator ) ) {
				separatorInPattern = separator;
			}
		}
		if (isDayFirst){
			b.append("d-m|dm|d.m|d/m|d|");
		} else {
			b.append("m-d|md|m.d|m/d|d|");
		}
		if ( StringUtils.hasValue( separatorInPattern ) ) {
			for ( String separator : separators ) {
				if ( !jsPattern.contains( separator ) ) {
					b.append( append );
					append = "|";
					b.append( jsPattern.replaceAll(
							Pattern.quote( separatorInPattern ) , separator ) );
				}
			}
			
			
			return b.toString();
		} else
			return "";

	}

}
