package netgest.bo.xwc.components.viewers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.xwc.framework.XUIRequestContext;

public class XEOViewerOperations {

	public void xeodmToggler() {
		XUIRequestContext oRequestContext;
		
		oRequestContext = XUIRequestContext.getCurrentContext();
		HttpServletResponse oServeletResponse = (HttpServletResponse)oRequestContext.getResponse();
		boolean xeodmEnable = Boolean.valueOf( oRequestContext.getRequestParameterMap().get( "xeodmstate" ) );
		if( xeodmEnable ) {
			Cookie c = new Cookie( "xeodmstate", Boolean.toString( true ) );
			c.setMaxAge( Integer.MAX_VALUE );
			c.setPath("/");
			oServeletResponse.addCookie( c );
		}
		else {
			Cookie c = new Cookie( "xeodmstate", Boolean.toString( false ) );
			c.setMaxAge( -1 );
			c.setPath("/");
			oServeletResponse.addCookie( c );
		}
	}
}
