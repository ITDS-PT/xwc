package netgest.bo.xwc.framework.jsf;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIMethodExpressionPhaseListener;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;
import netgest.bo.xwc.framework.localization.XUICoreMessages;


public class XUIViewerBuilder
{
//    private static final Log log = LogFactory.getLog(netgest.bo.xwc.framework.jsf.XUIViewerBuilder.class);
    
    public void buildView( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, XUIViewRoot oUIViewRoot  )    {
        buildComponentTree( oContext, oViewerDefinition, oUIViewRoot );
    }
    
    

    private void buildComponentTree( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, XUIViewRoot root )
    {
    	// Set the view root properties
    	String renderKitId = oViewerDefinition.getRenderKitId();
    	
    	if( !isEmpty( renderKitId ) ) {
    		root.setRenderKitId( renderKitId );
    	}
    	
    	addOnCreateViewEvents( oViewerDefinition, root );
    	addOnRestoreViewEvents( oViewerDefinition, root );
    	
    	addBeforeApplyRequestValuesEvents( oViewerDefinition, root );
    	addAfterApplyRequestValuesEvents( oViewerDefinition, root );
    	
    	addBeforeUpdateModelValuesEvents( oViewerDefinition, root );
    	addAfterUpdateModelValuesEvents( oViewerDefinition, root );
    	
    	addBeforeRenderEvents( oViewerDefinition, root );
    	addAfterRenderEvents( oViewerDefinition, root );
    	
    	for (XUIViewerDefinitionNode node : oViewerDefinition.getRootComponent().getChildren()){
    		buildComponent( oContext, root, root, node );
    	}
    }
    
    private void addOnCreateViewEvents(XUIViewerDefinition definition, XUIViewRoot root){
    	addEventListenersBefore( definition.getOnCreateViewPhaseList(), root, PhaseId.RESTORE_VIEW );
    }
    private void addOnRestoreViewEvents(XUIViewerDefinition definition, XUIViewRoot root){
    	addEventListeners( definition.getOnRestoreViewPhaseList(), root, PhaseId.RESTORE_VIEW );
    }
    
    
    private void addBeforeApplyRequestValuesEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListenersBefore( definition.getBeforeApplyRequestValuesPhaseList(), root, PhaseId.APPLY_REQUEST_VALUES );
    }
    private void addAfterApplyRequestValuesEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListeners( definition.getAfterApplyRequestValuesPhaseList(), root, PhaseId.APPLY_REQUEST_VALUES );
    }
    
    
    private void addBeforeUpdateModelValuesEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListenersBefore( definition.getBeforeUpdateModelPhaseList(), root, PhaseId.UPDATE_MODEL_VALUES );
    }
    private void addAfterUpdateModelValuesEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListeners( definition.getAfterUpdateModelPhaseList(), root, PhaseId.UPDATE_MODEL_VALUES );
    }
    
    
    private void addBeforeRenderEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListenersBefore( definition.getBeforeRenderPhaseList(), root, PhaseId.RENDER_RESPONSE );
    }
    private void addAfterRenderEvents( XUIViewerDefinition definition, XUIViewRoot root ){
    	addEventListeners( definition.getAfterRenderPhaseList(), root, PhaseId.RENDER_RESPONSE );
    }
    
    
    private void addEventListeners( List<String> events, XUIViewRoot root, PhaseId phaseId ){
    	if ( hasEvents( events ) ){
    		for ( String phaseEvent : events ){
    			if ( !isEmpty( phaseEvent ) ){
    				System.out.println("Adding "  + phaseEvent + "/" + phaseId);
	    			root.addPhaseListener(
	    					new XUIMethodExpressionPhaseListener(
	    							createEventMethodExpression( phaseEvent ) , 
	    							phaseId,
	    							false
	    					)
	    	    		);
    			}
    		}
    	}
    }
    
    private void addEventListenersBefore( List<String> events, XUIViewRoot root, PhaseId phaseId ){
    	if ( hasEvents( events ) ){
    		for ( String phaseEvent : events ){
    			if ( !isEmpty( phaseEvent ) ){
	    			root.addPhaseListener(
	    					new XUIMethodExpressionPhaseListener(
	    							createEventMethodExpression( phaseEvent ) , 
	    							phaseId,
	    							true
	    					)
	    	    		);
    			}
    		}
    	}
    }
    
    
    private boolean hasEvents( List<String> events){
    	return events.size() > 0;
    }
    
    private void buildComponent( XUIRequestContext oContext, XUIViewRoot root,UIComponent parent, XUIViewerDefinitionNode dcomponent )
    {
    	UIComponent comp = createComponent( oContext, dcomponent.getName() );
    	comp.setId( dcomponent.getId()==null?root.createUniqueId():dcomponent.getId() );
        
        try
        {
        	Method m = comp.getClass().getMethod( "setProperties",new Class[] { Map.class } );
            m.invoke( comp, new Object[] { dcomponent.getProperties() } );
        }
        catch(Exception e)
        {
        }
        
        try
        {
        	Method m = comp.getClass().getMethod( "setTextContent",new Class[] { String.class } );
            m.invoke( comp, new Object[] { dcomponent.getTextContent() } );
        }
        catch(Exception e)
        {
        }
        
        Iterator<String> keysEnum = dcomponent.getProperties().keySet().iterator();
        while( keysEnum.hasNext() )
        {

        	String keyName = String.valueOf( keysEnum.next() );
        	String value   = dcomponent.getProperty( keyName );
            String setterName = "set" + keyName.substring(0,1).toUpperCase() + keyName.substring(1);
            
            boolean wasSet 		= false;
            boolean isBool 		= false;
            boolean isFloat 	= false;
            boolean isInt 		= false;
            
            double  valueDouble = 0;
            long	valueLong	= 0;
            boolean boolValue	= false;
            
            if( "true".equals( value ) || "false".equals( value ) ) {
            	boolValue 	= Boolean.parseBoolean( value );
            	isBool 		= true;
            }
            
            if( !isBool ) {
	            try {
	            	valueLong = Long.parseLong( value );
	            	isInt     = true;
	            }
	            catch ( NumberFormatException e ) {
	            }
	            
	            if( !isInt ) {
		            try {
		            	valueDouble = Long.parseLong( value );
		            	isFloat     = true;
		            }
		            catch ( NumberFormatException e ) {
		            }
	            }
            }
            
            if( !wasSet && isBool ) {
            	wasSet = setComponentProperty( comp, setterName, boolValue );
            }
            
            if( !wasSet && isInt ) {
            	wasSet = setComponentProperty( comp, setterName, (int)valueLong );
            	wasSet = wasSet || setComponentProperty( comp, setterName, (long)valueLong );
            	wasSet = wasSet || setComponentProperty( comp, setterName, (short)valueLong );
            	wasSet = wasSet || setComponentProperty( comp, setterName, (byte)valueLong );
            	wasSet = wasSet || setComponentProperty( comp, setterName, (float)valueLong );
            	wasSet = wasSet || setComponentProperty( comp, setterName, (double)valueLong );
            }

            if( !wasSet && isFloat ) {
            	wasSet = setComponentProperty( comp, setterName, (float)valueDouble );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (double)valueDouble );
            }
            
            if( !wasSet && (comp instanceof XUIComponentBase)) {
            	wasSet = setComponentProperty( comp, setterName, value );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (Object)value );
            }
            else
            {
            	if (!wasSet && !(comp instanceof XUIComponentBase))
            	{
	            	// Added so that the JSF Components can be binded to beans
	                // Apparently there's something in the JSF platform that makes
	                // binding automatically and we need to make some adjustments
	                // in out platform (like setting the property value as a valueExpression)
	            	FacesContext context = FacesContext.getCurrentInstance();
	            	ELContext elContext = context.getELContext();
	            	Application jsfApp = context.getApplication();
	            	ExpressionFactory exprFactory = jsfApp.getExpressionFactory();
	            	ValueExpression vExpr = exprFactory.createValueExpression(elContext, value, Object.class);
					try 
					{
	            		comp.setValueExpression(keyName, vExpr);
	            	} catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					}
	            	if (vExpr.isLiteralText())
	            		setComponentProperty(comp, setterName, value);
	            	
	            }
            }
            
            
        }
        if (dcomponent.getName().equals("f:facet"))
        	parent.getFacets().put(dcomponent.getProperty("name"), comp);
        else
        	parent.getChildren().add( comp );
        

        List<XUIViewerDefinitionNode> children = dcomponent.getChildren();
        Iterator<XUIViewerDefinitionNode> it = children.iterator();
        while( it.hasNext() )
        {
            buildComponent( oContext, root, comp, it.next() );           
        }
        
    }
    
    private boolean setComponentProperty( Object component, String setterName, boolean value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Boolean.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }
    
    private boolean setComponentProperty( Object component, String setterName, byte value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Byte.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private boolean setComponentProperty( Object component, String setterName, short value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Short.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private boolean setComponentProperty( Object component, String setterName, int value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Integer.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private boolean setComponentProperty( Object component, String setterName, long value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Long.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private boolean setComponentProperty( Object component, String setterName, float value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Float.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private boolean setComponentProperty( Object component, String setterName, double value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Double.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private boolean setComponentProperty( Object component, String setterName, String value ) {
		try {
			Method m = component.getClass().getMethod( setterName, new Class[] { String.class }  );
			m.invoke(  component,  new Object[] { value } );
			return true;
		}
		catch( Exception e ) {
			return false;
		}
    }
    
    private boolean setComponentProperty( Object component, String setterName, Object value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Object.class }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }
    
    public UIComponent createComponent( XUIRequestContext oContext, String name )
    {
        assert name != null :MessageLocalizer.getMessage("NAME_CANNOT_BE_NULL");
        try
        {
            XUIComponentDefinition componentDef = oContext.getApplicationContext().getComponentStore().findComponent( name );
            if( componentDef == null && name.indexOf( ':' ) == -1 ) {
            	componentDef = oContext.getApplicationContext().getComponentStore().findComponent(  "xvw:" + name );
            }
            if( componentDef == null && name.indexOf( ':' ) == -1 ) {
            	componentDef = oContext.getApplicationContext().getComponentStore().findComponent(  "xeo:" + name );
            }
            if(componentDef != null)
            {
                UIComponent fcomponent = (UIComponent)Class.forName( componentDef.getClassName() ).newInstance();
                return fcomponent;
            }
            
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(
        		XUICoreMessages.COMPONENT_NOT_REGISTRED.toString( name )
        	);
    }
    
    public void render() throws IOException
    {
    }

    private static final boolean isEmpty( String s ) {
    	
    	return s == null || s.trim().length() == 0;
    	
    }
    
    private static final Class[] EVENT_ARGS = new Class[] { PhaseEvent.class };
    
    private MethodExpression createEventMethodExpression( String sMethodExpression ) {
    	FacesContext f = FacesContext.getCurrentInstance();
        ExpressionFactory oExFactory = f.getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( f.getELContext(), sMethodExpression, null, EVENT_ARGS );
    }
    
    
}
