package netgest.bo.xwc.framework.jsf;

import java.beans.FeatureDescriptor;
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

import com.sun.el.lang.EvaluationContext;

public class XUIELResolver extends ELResolver {

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
        
        // Resolve properties from Component context
        if( elContext instanceof EvaluationContext ) {
        	
        	EvaluationContext elContextb = (EvaluationContext)elContext;
        	ELContext elContextc = ((EvaluationContext) elContextb).getELContext();
        	
	        if( elContextc instanceof XUIELContextWrapper ) {
		        List<Map<String,Object>> localVars = buildLocalVarsMap( ((XUIELContextWrapper)elContextc).getContextComponent() );
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
        /*
        oResult = oRequestContext.getViewRoot().getBean( sProperty );
        if( oResult != null ) {
            eLContext.setPropertyResolved( true );
            return oResult;
        }
        */
        
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
