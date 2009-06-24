package netgest.bo.xwc.framework.jsf;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import netgest.bo.transaction.XTransactionManager;
import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIApplicationContext;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.http.XUIServlet;

public class XUIPhaseListener implements PhaseListener {


    XUIApplicationContext  oXuiApplication;
    
    public XUIPhaseListener() {
        
        // Initializing listener
        System.out.println("Listener Initialized");
        
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

        Map oExternalSessionMap = event.getFacesContext().getExternalContext().getSessionMap();        
        oSessionContext = (XUISessionContext)oExternalSessionMap.get(XUISessionContext.SESSION_ATTRIBUTE_ID );

        if( oSessionContext == null ) {
            oSessionContext = new XUISessionContext();
            oExternalSessionMap.put(XUISessionContext.SESSION_ATTRIBUTE_ID, oSessionContext );
        }
        
        if( XUIRequestContext.getCurrentContext() == null ) {
        	XUIRequestContext oRequestContext = PackageIAcessor.createRequestContext( oXuiApplication );
        }
        
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }


}
