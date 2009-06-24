package netgest.bo.xwc.framework.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import netgest.bo.xwc.framework.XUISessionContext;

public class XUISessionListener implements HttpSessionListener {

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
}
