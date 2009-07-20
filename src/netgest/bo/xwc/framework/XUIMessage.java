package netgest.bo.xwc.framework;

public class XUIMessage {
    
    public static final int TYPE_ALERT = 1;
    public static final int TYPE_MESSAGE = 2;
    public static final int TYPE_POPUP_MESSAGE = 3;
    
    public static final int SEVERITY_INFO = 11;
    public static final int SEVERITY_WARNING = 12;
    public static final int SEVERITY_ERROR = 13;
    public static final int SEVERITY_CRITICAL = 14;
    
    
    private int severity;
    private int type;
    
    private String title;
    private String message;
    private String detail;
    
    
    public XUIMessage( int iType, int iSeverity, String sTitle, String sMessage ) {
        this( iType, iSeverity, sTitle, sMessage, null );
    }

    public XUIMessage( int iType, int iSeverity, String sTitle, String sMessage, String sDetail ) {
        this.type = iType;
        this.severity = iSeverity;
        this.title = sTitle;
        this.message = sMessage;
        this.detail = sDetail;
    }

    public int getSeverity() {
        return severity;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }
}
