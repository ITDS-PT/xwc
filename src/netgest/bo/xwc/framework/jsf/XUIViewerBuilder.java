package netgest.bo.xwc.framework.jsf;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
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
            
        	if (comp instanceof XUIComponentBase)
        		((XUIComponentBase) comp).applyPropertyDefaultValue(keyName, value);
        	else
        		XUIPropertySetter.setProperty(comp,keyName, value);                       
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
