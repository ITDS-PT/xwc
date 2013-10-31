package netgest.bo.xwc.framework.localization;

import java.util.Date;

public class XUILocalizationUtils {
	
	/**
	 * 
	 * Formats a date to string
	 * 
	 * @deprecated See {@link XUILocalization#formatDate(Date)}
	 */
	@Deprecated
	public static final String dateToString( Date date ) {
		return XUILocalization.formatDate(date);
	}

	/**
	 * 
	 * Formats a date to string with time
	 * 
	 * @deprecated See {@link XUILocalization#formatDateTime(Date)}
	 */
	@Deprecated
	public static final String dateTimeToString( Date date ) {
		return XUILocalization.formatDateTime(date);
	}

}
