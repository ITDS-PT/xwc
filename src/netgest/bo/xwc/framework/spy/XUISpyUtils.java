package netgest.bo.xwc.framework.spy;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIViewRoot;

import netgest.bo.xwc.components.classic.ViewerCommandSecurityBase;
import netgest.bo.xwc.components.classic.ViewerInputSecurityBase;
import netgest.bo.xwc.components.classic.ViewerOutputSecurityBase;
import netgest.bo.xwc.components.classic.ViewerSecurityBase;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import com.earnstone.perf.PerformanceCounter;

public class XUISpyUtils {
	
	public static final boolean LOG = false;
	
	public static final void logProperty( ValueExpression prop, long initCallMs ) {
		if(!LOG) return;
		String viewerName = getViewerName() + ".properties";
		logCalls(viewerName, prop.getExpressionString());
		logTime(viewerName, prop.getExpressionString() , initCallMs);
	}

	public static final void logMethod( MethodExpression prop, long initCallMs ) {
		if(!LOG) return;
		logMethod( getViewerName(), prop, initCallMs );
	}
	public static final void logMethod( String viewerName, MethodExpression prop, long initCallMs ) {
		if(!LOG) return;
		viewerName = viewerName + ".methods";
		logCalls(viewerName, prop.getExpressionString());
		logTime(viewerName, prop.getExpressionString() , initCallMs);
	}

	public static final void logPhase( String PhaseName , long initCallMs ) {
		if(!LOG) return;
		String viewerName = getViewerName() + ".phases";
		logCalls(viewerName, PhaseName);
		logTime(viewerName, PhaseName, initCallMs);
	}

	public static final void logComponentRenderer( XUIComponentBase comp , long initCallMs ) {
		if(!LOG) return;
		String viewerName = getViewerName() + ".renders";
		logCalls(viewerName, getComponentLabel( comp ) );
		logTime(viewerName, getComponentLabel( comp ), initCallMs);
	}
	
	public static final void logComponentServlet( XUIComponentBase comp , long initCallMs ) {
		if(!LOG) return;
		String viewerName = getViewerName() + ".servlet";
		logCalls(viewerName, getComponentLabel( comp ) );
		logTime(viewerName, getComponentLabel( comp ), initCallMs);
	}
	
	public static final void logViewerTime( UIViewRoot viewRoot, String opName , long initCallMs ) {
		if(!LOG) return;
		String viewerName = getViewerName( viewRoot ) + ".viewer";
		logTime(viewerName, opName , initCallMs);
	}

	public static final void logViewerCalls( UIViewRoot viewRoot, String opName ) {
		if(!LOG) return;
		String viewerName = getViewerName( viewRoot ) + ".viewer";
		logCalls(viewerName, opName);
	}
	
	private static final void logTime( String viewerName, String keyName, long initCallMs ) {
		long ct = System.currentTimeMillis();
		viewerName = "xvw.viewers." + viewerName.replace( '/' , '.' );
		try {
			PerformanceCounter.
				getPerfAvgTime( "viewers." +viewerName, "Avg Time " + keyName )
					.addTime( initCallMs, ct );
	
			PerformanceCounter.
				getIncrement( viewerName, "Total Time " + keyName )
					.incrementBy( ct - initCallMs );

			PerformanceCounter.
				getIncrement( viewerName, "Last Time " + keyName )
					.set( ct - initCallMs );
		}
		catch (Exception e) {
		}
	}

	private static final void logCalls(  String viewerName, String keyName ) {
		viewerName = "xvw.viewers." + viewerName.replace( '/' , '.' );
		try {
			PerformanceCounter.
				getAvgCallsPerSec( viewerName,"Avg Calls " + keyName );
	
			PerformanceCounter.
				getIncrement( viewerName, "Total Calls " + keyName )
					.increment();
		}
		catch( Exception e ) {
			
		}
	}
	
	private static final String getViewerName() {
		return getViewerName( XUIRequestContext
			.getCurrentContext().getViewRoot()
		);
	}
	private static final String getViewerName( UIViewRoot r ) {
		
		
		if( r != null ) {
			String viewId = r.getViewId();
			if( viewId.startsWith( "viewers." ) ) {
				viewId = viewId.substring(8);
			}
			if( viewId.startsWith( "viewers/" ) ) {
				viewId = viewId.substring(8);
			}
			if( viewId.endsWith(".xvw") ) {
				viewId = viewId.substring(0,viewId.length()-4);
			}
			return viewId;
		}
		return "<< null >>";
	}
	
	private static final String getComponentLabel( XUIComponentBase comp ) {
    	String compLabel = comp.getRendererType();
    	if( comp instanceof ViewerSecurityBase ) {
    		compLabel =((ViewerSecurityBase)comp)
    			.getViewerSecurityLabel();
    	}
    	else if( comp instanceof ViewerInputSecurityBase ) {
    		compLabel =
    				((ViewerInputSecurityBase)comp)
    					.getViewerSecurityLabel();
    	}
    	else if( comp instanceof ViewerOutputSecurityBase ) {
    		compLabel =
    				((ViewerOutputSecurityBase)comp)
    					.getViewerSecurityLabel();
    	}
    	else if( comp instanceof ViewerCommandSecurityBase ) {
    		compLabel =
    		 		((ViewerCommandSecurityBase)comp)
    					.getViewerSecurityLabel();
    	}
    	return compLabel;
	}
	
}
