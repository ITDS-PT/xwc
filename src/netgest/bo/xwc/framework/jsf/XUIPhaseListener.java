package netgest.bo.xwc.framework.jsf;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIApplicationContext;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;

public class XUIPhaseListener implements PhaseListener {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	XUIApplicationContext  oXuiApplication;
    
    public XUIPhaseListener() {
    }

    public void afterPhase(PhaseEvent event) {
    }

    public void beforePhase(PhaseEvent event) {

        if( oXuiApplication == null ) {
            oXuiApplication = new XUIApplicationContext();
            PackageIAcessor.initApplicationContext( oXuiApplication );
        }
        // Check if the SessionContext exits, if not create a session context

        XUISessionContext oSessionContext;

        Map<String,Object> oExternalSessionMap = event.getFacesContext().getExternalContext().getSessionMap();        
        oSessionContext = (XUISessionContext)oExternalSessionMap.get(XUISessionContext.SESSION_ATTRIBUTE_ID );

        if( oSessionContext == null ) {
            oSessionContext = new XUISessionContext();
            oExternalSessionMap.put(XUISessionContext.SESSION_ATTRIBUTE_ID, oSessionContext );
        }
        
        if( XUIRequestContext.getCurrentContext() == null ) {
        	PackageIAcessor.createRequestContext( oXuiApplication );
        }
        
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }


}
