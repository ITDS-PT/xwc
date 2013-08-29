package netgest.bo.xwc.components.viewers.beans.localization;

import netgest.bo.system.Logger;
import netgest.bo.system.locale.LocaleFormatter.CurrencyPosition;
import netgest.bo.system.locale.LocaleSettings;
import netgest.bo.system.login.LocalePreferenceSerialization;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.localization.BeansMessages;

import netgest.utils.StringUtils;

import java.text.Collator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class LocaleSettingsBean extends XEOBaseBean {
	
	
	private static final Logger logger = Logger
			.getLogger( LocaleSettingsBean.class );

	public static final String YEAR = "yyyy";
	public static final String MONTH = "MM";
	public static final String DAY = "dd";
	
	public static final String HOUR_24 = "H";
	public static final String HOUR_12 = "h";

	public static final String SEPARATOR_SLASH = "/";
	public static final String SEPARATOR_DASH = "-";
	public static final String SEPARATOR_DOT = ".";
	
	public static final String CURRENCY_RIGHT = CurrencyPosition.RIGHT.name();
	public static final String CURRENCY_LEFT = CurrencyPosition.LEFT.name();
	
	public static final String DEFAULT_GROUP_SEPARATOR = ".";
	public static final String DEFAULT_DECIMAL_SEPARATOR = ",";
	public static final String DEFAULT_CURRENCY_SYMBOL = "€";
	public static final String DEFAULT_DATE_TIME_SEPARATOR = " ";
	
	public static final String DEFAULT_TIMEZONE = "Europe/Lisbon";
	

	public Map< String , String > getDateParts() {
		Map< String , String > parts = new HashMap< String , String >(3);
		parts.put( YEAR , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_DAY.toString() );
		parts.put( MONTH , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_MONTH.toString() );
		parts.put( DAY , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_YEAR.toString() );
		
		return parts;
	}
	
	
	
	
	public Map< String , String > getTimeParts() {
		Map< String , String > parts = new HashMap< String , String >();
		parts.put( HOUR_24 , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_24_HOURS.toString() );
		parts.put( HOUR_12 , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_AM_PM_HOURS.toString()  );
		return parts;
	}

	public Map< String , String > getSeparatorParts() {
		Map< String , String > parts = new HashMap< String , String >();
		parts.put( SEPARATOR_SLASH , "/" );
		parts.put( SEPARATOR_DASH , "-" );
		parts.put( SEPARATOR_DOT , "." );
		return parts;
	}
	
	public Map< String , String > getLocaleList() {
		
		Map< Locale , String > result = new HashMap< Locale , String >();
		Map< String , Locale > inverseQuery = new HashMap< String , Locale >();
		
		Locale[] localesArray = Locale.getAvailableLocales();
		List<String> sortedLocales = new LinkedList< String >(); 
		Locale currentLocale = getCurrentLocale();
		for ( Locale locale : localesArray ) {
			if (StringUtils.hasValue( locale.getDisplayCountry( currentLocale ))) {
				String localeDisplayName = locale.getDisplayLanguage( currentLocale ) 
						+ " ( " +locale.getDisplayCountry( currentLocale ) + " ) ";
				sortedLocales.add( localeDisplayName );
				result.put( locale , localeDisplayName );
				inverseQuery.put( localeDisplayName , locale );
			}
		}
		
		Collections.sort( sortedLocales, Collator.getInstance( currentLocale ) );
		
		Map< String , String > newResult = new LinkedHashMap< String , String >();
		
		for ( String sorted : sortedLocales ) {
			newResult.put( inverseQuery.get(  sorted ).toString() , sorted );
		}
		
		return newResult;
	}
	
	public Locale getCurrentLocale(){
		return this.locale;
	}
	
	public Map< String , String > getTimeZoneList(){
		
		String[] timeZoneIds = TimeZone.getAvailableIDs();
		
		Map< String , String > result = new LinkedHashMap< String , String >();
		
		List< String > timeZoneList = new LinkedList< String >();
		
		for ( String timeZoneId : timeZoneIds ) {
			timeZoneList.add( timeZoneId  );
		}
		
		Collections.sort( timeZoneList  );
		
		for ( String timeZoneId : timeZoneList ) {
			result.put( timeZoneId, timeZoneId + " ( "+ TimeZone.getTimeZone( timeZoneId ).getDisplayName(getCurrentLocale()) +" ) "  );
		}
		
		return result;
		
	}
	
	private String timeZone = DEFAULT_TIMEZONE;
	

	public Map< String , String > getCurrencyPositionParts() {
		Map< String , String > parts = new HashMap< String , String >();
		parts.put( CURRENCY_RIGHT , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_CURRENCY_RIGHT.toString() );
		parts.put( CURRENCY_LEFT , BeansMessages.LOCALE_SETTINGS_LOV_ENTRY_CURRENCY_LEFT.toString());
		return parts;
	}
	
	public void changeLocale() {
		
	}
	
	public LocaleSettingsBean() {
		try {
			LocaleSettings settings = getEboContext().getBoSession().getUser().getLocaleSettings();
			decimalSeparator = String.valueOf(settings.getDecimalSeparator());
			groupSeparator = String.valueOf(settings.getGroupSeparator());
			currencySymbol = settings.getCurrencySymbol();
			currencyPosition = settings.getCurrencyPosition().name();
			locale = settings.getLocale();
			timeZone = settings.getTimezone().getID();
			
			String datePattern = settings.getDatePattern();
			DatePatternTokenizer tokenizer = new DatePatternTokenizer( datePattern ,
					new String[] {SEPARATOR_SLASH, SEPARATOR_DASH, SEPARATOR_DOT} );
			
			part1 = tokenizer.getPart1();
			part2 = tokenizer.getSeparator1();
			part3 = tokenizer.getPart2();
			part4 = tokenizer.getSeparator2();
			part5 = tokenizer.getPart3();
			
			//FIXME Set Time Pattern and 
			
		} catch (Exception e) {
			logger.warn( e );
		}
	}

	private String decimalSeparator = DEFAULT_DECIMAL_SEPARATOR;
	private String groupSeparator = DEFAULT_GROUP_SEPARATOR;
	private String currencySymbol = DEFAULT_CURRENCY_SYMBOL;
	private String currencyPosition = CURRENCY_RIGHT;
	private String dateTimeSeparator = DEFAULT_DATE_TIME_SEPARATOR;
	private Locale locale = new Locale( "pt" , "PT" );

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getCurrencyPosition() {
		return currencyPosition;
	}

	public void setCurrencyPosition(String currencyPosition) {
		this.currencyPosition = currencyPosition;
	}

	public String getGroupSeparator() {
		return groupSeparator;
	}

	public void setGroupSeparator(String groupSeparator) {
		this.groupSeparator = groupSeparator;
	}

	public void previewDateFormat() {

	}
	
	public void previewTimeFormat() {
		
	}
	
	public void previewDateTimeFormat() {
		
	}
	
	public void previewNumberFormat() {

	}
	
	public void previewCurrencyFormat() {

	}
	
	private String timePattern = HOUR_24;

	private String part1 = DAY;
	private String part2 = SEPARATOR_SLASH;
	private String part3 = MONTH;
	private String part4 = SEPARATOR_SLASH;
	private String part5 = YEAR;

	public String getDatePreview() {
		SimpleDateFormat format = new SimpleDateFormat( part1 + part2 + part3
				+ part4 + part5 );
		return format.format( new Date() );
	}
	
	private String getDatePattern() { 
		return part1 + part2 + part3
				+ part4 + part5;
	}
	
	public String getNumberPreview() {
		
		DecimalFormatSymbols decimalymbols = new DecimalFormatSymbols( );
		decimalymbols.setDecimalSeparator( getDecimalSeparator().charAt( 0 ) );
		decimalymbols.setGroupingSeparator( getGroupSeparator().charAt( 0 ) );
		decimalymbols.setCurrencySymbol( getCurrencySymbol() );	
		DecimalFormat format = new DecimalFormat("###,###.###",decimalymbols);
		return format.format( 123456789.123 );
	}  
	
	public String getTimePreview() {
		SimpleDateFormat format = new SimpleDateFormat( getTimePatternLocalized() );
		return format.format( new Date() );
	}  
	
	public String getDateTimePreview() {
		SimpleDateFormat format = new SimpleDateFormat( part1 + part2 + part3
				+ part4 + part5 + getDateTimeSeparator() + getTimePatternLocalized() );
		return format.format( new Date() );
	}  
	
	public String getCurrencyPreview() {
		DecimalFormatSymbols decimalymbols = new DecimalFormatSymbols( );
		decimalymbols.setDecimalSeparator( getDecimalSeparator().charAt( 0 ) );
		decimalymbols.setGroupingSeparator( getGroupSeparator().charAt( 0 ) );
		decimalymbols.setCurrencySymbol( getCurrencySymbol() );	
		String pattern = "###,###.###";
		if (getCurrencyPosition().equals( "left" ))
			pattern = '¤' + pattern;
		else 
			pattern = pattern + '¤';
		DecimalFormat format = new DecimalFormat( pattern , decimalymbols );
		return format.format( 123456789.123 );
	}

	public String getPart1() {
		return part1;
	}

	public void setPart1(String part1) {
		this.part1 = part1;
	}

	public String getPart2() {
		return part2;
	}

	public void setPart2(String part2) {
		this.part2 = part2;
	}

	public String getPart3() {
		return part3;
	}

	public void setPart3(String part3) {
		this.part3 = part3;
	}

	public String getPart4() {
		return part4;
	}

	public void setPart4(String part4) {
		this.part4 = part4;
	}

	public String getPart5() {
		return part5;
	}

	public void setPart5(String part5) {
		this.part5 = part5;
	}
	
	public void save() {
		
		LocaleSettings settings = createLocaleSettings();
		
		LocalePreferenceSerialization.save( settings , getEboContext() );
		
	}
	
	public LocaleSettings createLocaleSettings() {
		
		CurrencyPosition position = CurrencyPosition.LEFT;
		if (getCurrencyPosition().equals( CURRENCY_RIGHT ))
			position = CurrencyPosition.RIGHT;
		
		LocaleSettings settings = new LocaleSettings(
				this.locale,
				getTimeZoneInstance() ,
				getDatePattern() , 
				getTimePatternLocalized() , 
				getDateTimeSeparator() , 
				getGroupSeparator() , 
				getDecimalSeparator() , 
				getCurrencySymbol() , 
				position );
		return settings;
		
	}

	public String getDateTimeSeparator() {
		return dateTimeSeparator;
	}

	public void setDateTimeSeparator(String dateTimeSeparator) {
		this.dateTimeSeparator = dateTimeSeparator;
	}
	
	private String getTimePatternLocalized() {
		if (HOUR_12.equals( timePattern ))
			return timePattern + ":mm:ss a";
		else 
			return timePattern + ":mm:ss";
	}

	public String getTimePattern() {
		return timePattern;
					
	}

	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}


	public String getLocale() {
		return locale.toString();
	}


	public void setLocale(String locale) {
		String[] localeParts = locale.split( "_" );
		if (localeParts.length == 1) {
			this.locale = new Locale( localeParts[0] );
		} else if (localeParts.length == 2) {
			this.locale = new Locale( localeParts[0], localeParts[1] );
		} else if (localeParts.length == 3) {
			this.locale = new Locale( localeParts[0], localeParts[1], localeParts[2]  );
		}
	}


	public String getTimeZone() {
		return timeZone;
	}

	private TimeZone getTimeZoneInstance() {
		return TimeZone.getTimeZone( timeZone );
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	

}
