package netgest.bo.xwc.framework.localization;

import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.locale.LocaleFormatter;
import netgest.bo.system.locale.LocaleFormatter.CurrencyPosition;
import netgest.bo.system.locale.LocaleFormatter.DateTimeLengh;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XUILocalization  {
	
	public static void setCurrentLocale(Locale locale) {
		boApplication.currentContext().setLocale( locale );
	}
	

	public static Locale getCurrentLocale() {
		return XEO.getCurrentLocale();
	}
	
	public static Locale getUserLocale() {
		return XEO.getUserLocale();
	}
	

	static LocaleFormatter getLocaleProvider() {
		return XEO.getLocaleFormatter();
	}

	
	public static String formatDate(Date toFormat) {
		return getLocaleProvider().formatDate( toFormat );
	}
	
	public static String formatDateDefaultTimeZone(Date toFormat) {
		return getLocaleProvider().formatDateDefaultTimezone(toFormat);
	}

	
	public static String formatDate(Date toFormat, DateTimeLengh length) {
		return getLocaleProvider().formatDate( toFormat, length );
	}

	public static String formatHourMinute(Date date) {
		return getLocaleProvider().formatHourMinute( date );
	}
	
	public static String formatTime(Date toFormat) {
		return getLocaleProvider().formatTime( toFormat );
	}

	
	public static String formatTime(Date toFormat, DateTimeLengh length) {
		return getLocaleProvider().formatTime( toFormat, length );
	}

	
	public static String formatDateTime(Date toFormat) {
		return getLocaleProvider().formatDateTime( toFormat );
	}

	
	public static String formatDateTime(Date toFormat, DateTimeLengh dateLength,
			DateTimeLengh timeLength) {
		return getLocaleProvider().formatDateTime( toFormat, dateLength, timeLength );
	}

	
	public static Date parseDate(String date) throws ParseException{
		return getLocaleProvider().parseDate( date );
	}
	
	public static Date parseDateDefaultTimezone(String date) throws ParseException{
		return getLocaleProvider().parseDateDefaultTimezone(date);
	}
	
	public static Date parseDateHourMinute(String date) throws ParseException{
		return getLocaleProvider().parseDateHourMinute( date );
	}

	public static Date parseDateTime(String date) throws ParseException{
		return getLocaleProvider().parseDateWithCompleteTime( date );
	}
	
	public static String formatNumber(long toFormat) {
		return getLocaleProvider().formatNumber( toFormat );
	}

	
	public static String formatPercent(float toFormat) {
		return getLocaleProvider().formatPercent( toFormat );
	}

	
	public static String formatCurrency(long toFormat) {
		return getLocaleProvider().formatCurrency( toFormat );
	}

	
	public static String getDateFormat(DateTimeLengh length) {
		return getLocaleProvider().getDateFormat(length);
	}
	
	public static String getDateFormat() {
		return getLocaleProvider().getDateFormat(DateTimeLengh.DEFAULT);
	}
	
	public static String getTimeFormat() {
		return getLocaleProvider().getTimeFormat(DateTimeLengh.DEFAULT);
	}
	
	public static String getHourMinuteFormat() {
		return getLocaleProvider().getHourMinuteFormat(DateTimeLengh.DEFAULT);
	}
	
	public static String getCurrencySymbol() {
		return getLocaleProvider().getCurrencySymbol();
	}
	
	public static char getGroupSeparator() {
		return getLocaleProvider().getGroupSeparator();
	}
	
	public static char getDecimalSeparator() {
		return getLocaleProvider().getDecimalSeparator();
	}
	
	public static char getDateTimeSeparator(){
		return getLocaleProvider().getDateTimeSeparator();
	}
	
	
	public static DecimalFormat getNumberFormatter() {
		return getLocaleProvider().getNumberFormatter();
	}
	
	public static DecimalFormat getCurrencyFormatter() {
		return getLocaleProvider().getCurrencyFormatter();
	}
	
	public static CurrencyPosition getCurrencyPosition() {
		return getLocaleProvider().getCurrencyPosition();
	}
	
	public static List<Locale> getAvailableLocales(){
		return XEO.getAvailableLocales();
	}
	
	
}
