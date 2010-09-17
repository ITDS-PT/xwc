package netgest.bo.xwc.xeo.workplaces.admin;

public class Utils {

	public static String formatTimeMiliSeconds(double time) {
		return formatTimeSeconds(time/1000);
	}
	
	public static String formatTimeSeconds(double time) {
        String retval = "";

        int days = (int)time / (60*60*24);
        int minutes, hours;

        if (days != 0) {
            retval += days + " " + ((days > 1) ? "days" : "day") + ", ";
        }

        minutes = (int)time / 60;
        hours = minutes / 60;
        hours %= 24;
        minutes %= 60;

        if (hours != 0) {
            retval += hours + ":" + minutes;
        }
        else {
            retval += minutes + " min";
        }

        return retval;
    }
	
    public static Long formatBytesMB(long value) {
        return new Long(value / 1024 / 1024);
    }
}
