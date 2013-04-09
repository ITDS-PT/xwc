package netgest.bo.xwc.framework.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import netgest.bo.system.boApplication;
import netgest.bo.xwc.framework.XUISessionContext;

public class XUISessionListener implements HttpSessionListener, ServletContextListener {

	
	boApplication xeoApp;
	
	public void sessionCreated(HttpSessionEvent arg0) {
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent ) {
		HttpSession session = sessionEvent.getSession();
		if( session != null ) {
	        XUISessionContext oSessionContext = (XUISessionContext)session.getAttribute( XUISessionContext.SESSION_ATTRIBUTE_ID );
	        if( oSessionContext != null ) {
	        	oSessionContext.close();
	        }
		}
	}

	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// Initialize XEO
		xeoApp = boApplication.getApplicationFromStaticContext("XEO");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// Showdown XEO
		if( xeoApp != null ) {
			xeoApp.shutDown();
		}
	}
	
}
