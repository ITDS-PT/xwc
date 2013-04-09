package netgest.bo.xwc.framework.jsf;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.component.UIComponent;

import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIELContextWrapper;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XUIELResolver extends ELResolver {

	private static ThreadLocal< UIComponent > context = new ThreadLocal< UIComponent >( );
	
	public static void setContext(UIComponent current){
		context.set( current );
	}
	
	@Override
    public Class<?> getCommonPropertyType(ELContext eLContext, Object object) {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext eLContext, Object object) {
        return null;
    }

    @Override
    public Class<?> getType(ELContext eLContext, Object object, Object object2) {
        return null;
    }
    
    @Override
    public Object getValue(ELContext elContext, Object base, Object property ) 
    {
        Object oResult;
        String sProperty = String.valueOf( property );
        
        
        XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
        ELContext elContextc = null;
        
        // JBOSS use a diferent implementation on EvaluationContext
        try {
        	Method m = elContext.getClass().getMethod( "getELContext");
        	elContextc =  (ELContext)m.invoke( elContext );
        	
        }
        catch( Throwable a ) {};
        
        // Resolve properties from Component context
        if( elContextc != null ) {
	        if( elContextc instanceof XUIELContextWrapper ) {
	        	UIComponent contextCurrent = context.get( );
	        	if (contextCurrent == null)
	        		contextCurrent = ((XUIELContextWrapper)elContextc).getContextComponent();
		        List<Map<String,Object>> localVars = buildLocalVarsMap( contextCurrent );
		        if( localVars != null ) {
			        for( int i=0; i < localVars.size(); i++ ) {
			        	if( localVars.get(i).containsKey( sProperty ) ) {
			                elContext.setPropertyResolved( true );
			        		return localVars.get(i).get( sProperty );
			        	}
			        }
		        }
	        }
        }
        
        
        // Resolve properties from Event context 
        XUIActionEvent event = oRequestContext.getEvent(); 
        
        if( event != null ) {
	        List<Map<String,Object>> localVars = buildLocalVarsMap( event.getComponent() );
	        if( localVars != null ) {
		        for( int i=0; i < localVars.size(); i++ ) {
		        	if( localVars.get(i).containsKey( sProperty ) ) {
		                elContext.setPropertyResolved( true );
		        		return localVars.get(i).get( sProperty );
		        	}
		        }
	        }
        }

        // Resolve properties from PhaseEvent context 
        XUIPhaseEvent phaseEvent = oRequestContext.getPhaseEvent(); 
        
        if( phaseEvent != null ) {
	        List<Map<String,Object>> localVars = buildLocalVarsMap( phaseEvent.getSrcComponent() );
	        if( localVars != null ) {
		        for( int i=0; i < localVars.size(); i++ ) {
		        	if( localVars.get(i).containsKey( sProperty ) ) {
		                elContext.setPropertyResolved( true );
		        		return localVars.get(i).get( sProperty );
		        	}
		        }
	        }
        }

        // Resolve Viewer Beans
        //FIXME: Descomentei isto por cause do getLovMap do XEOObjectAttributeConnector e da martelada que lá está
        oResult = oRequestContext.getViewRoot().getBean( sProperty );
        if( oResult != null ) {
            elContext.setPropertyResolved( true );
            return oResult;
        }
        
        
        // Resolve Bean in Session
        oResult = oRequestContext.getSessionContext().getBean( sProperty );
        if( oResult != null ) {
            elContext.setPropertyResolved( true );
            return oResult;
        }
        
        // Resolve Request Attributes
        oResult = oRequestContext.getAttribute( sProperty );
        if( oResult != null ) {
            elContext.setPropertyResolved( true );
            return oResult;
        }

        // Resolve Request Attributes
        oResult = oRequestContext.getSessionContext().getAttribute( sProperty );
        if( oResult != null ) {
            elContext.setPropertyResolved( true );
            return oResult;
        }
        if (elContextc instanceof XUIELContextWrapper){
        	((XUIELContextWrapper) elContextc).setCouldNotEvaluate();
        }
        elContext.setPropertyResolved( false );
        return null;

    }

    @Override
    public boolean isReadOnly(ELContext eLContext, Object object, Object object2) {
        return true;
    }

    @Override
    public void setValue(ELContext eLContext, Object object, Object object2, Object object3) {
    }
    
    
    private List<Map<String,Object>> buildLocalVarsMap( UIComponent component ) {
    	
    	List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
    	Object parent = component;
    	while( parent != null ) {
    		if( parent instanceof XUIViewRoot ) {
    			
    			Map<String,Object> s = ((XUIViewRoot)parent).getViewBeans(); 
    			if( s != null )
    				ret.add( s );
    		}
    		if( parent instanceof UIComponent ) {
    			parent = ((UIComponent)parent).getParent();
    		}
    		else {
    			parent = null;
    		}
    	}
    	
    	return ret;
    	
    }
    
    
 
}
