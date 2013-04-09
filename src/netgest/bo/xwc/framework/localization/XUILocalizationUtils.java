package netgest.bo.xwc.framework.localization;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XUILocalizationUtils {
	
	private static final SimpleDateFormat sdfdt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	private static final SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final String dateToString( Date date ) {
		return sdfd.format( date );
	}

	public static final String dateTimeToString( Date date ) {
		return sdfdt.format( date );
	}

}
