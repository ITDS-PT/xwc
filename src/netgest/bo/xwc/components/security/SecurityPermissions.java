package netgest.bo.xwc.components.security;

public class SecurityPermissions {
    public static final byte NONE			= 0;
    public static final byte READ     		= 1;
    public static final byte WRITE    		= 2;
    public static final byte ADD      		= 4;
    public static final byte DELETE   		= 8;
    public static final byte EXECUTE  		= 16;
    public static final byte FULL_CONTROL 	= 31;
}
