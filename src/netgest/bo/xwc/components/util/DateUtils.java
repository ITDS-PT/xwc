package netgest.bo.xwc.components.util;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

public class DateUtils {
    
    private static final SimpleDateFormat oDateFormater = new SimpleDateFormat( "dd/MM/yyyy" );
    private static final SimpleDateFormat oDateTimeFormater = new SimpleDateFormat( "dd/MM/yyyy hh:MM:ss" );
    
    public static final void formatTimestampToDate( StringBuilder appendTo, Timestamp oTimestamp ) {
        appendTo.append( 
            oDateFormater.format( oTimestamp )
        );
    }

    public static final void formatTimestampToDateTime( StringBuilder appendTo, Timestamp oTimestamp ) {
        appendTo.append( 
            oDateTimeFormater.format( oTimestamp )
        );
    }
}
