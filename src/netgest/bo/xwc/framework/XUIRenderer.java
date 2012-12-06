package netgest.bo.xwc.framework;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.http.XUIAjaxRequestWrapper;

public class XUIRenderer extends Renderer
{
    
    public XUIRenderer()
    {
    }
    
    public void decode( XUIComponentBase component )
    {
        super.decode( getFacesContext(), component );
    }
    
    /**
     * @deprecated Use in X components
     * decode
     * @see #decode(XUIComponentBase)
     */
     @Deprecated
    public void decode(FacesContext context, UIComponent component)
    {
        if( component instanceof XUIComponentBase )
            this.decode( (XUIComponentBase)component );
        else
            super.decode( context, component );
    }

    public void encodeBegin( XUIComponentBase component ) throws IOException
    {
    	if( component.getRenderComponent() )
    		super.encodeBegin( getFacesContext(), component );
    }
    
    /**
     * @deprecated Use in X components
     * encodeBegin
     * @see #decode(XUIComponentBase)
     */
     @Deprecated
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException
    {
        if( component instanceof XUIComponentBase ) {
        	XUIComponentBase comp = (XUIComponentBase)component;
        	if( comp.getRenderComponent() )
        		this.encodeBegin( comp );
        }
        else
            super.encodeBegin( context, component );
    }

    public void encodeChildren( XUIComponentBase component) throws IOException
    {
    	XUIComponentBase comp = (XUIComponentBase)component;
    	if( comp.getRenderComponent() )
    		super.encodeChildren( getFacesContext(), component );
    }
    
    /**
     * @deprecated Use in X components
     * encodeChildren
     * @see #encodeChildren(XUIComponentBase)
     */
     @Deprecated
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException
    {
        if( component instanceof XUIComponentBase ) {
        	XUIComponentBase comp = (XUIComponentBase)component;
        	if( comp.getRenderComponent() )
        		this.encodeChildren( (XUIComponentBase)component );
        } else
            super.encodeChildren( context, component );
    }


    public void encodeEnd( XUIComponentBase component ) throws IOException
    {
    	XUIComponentBase comp = (XUIComponentBase)component;
    	if( comp.getRenderComponent() )
    		super.encodeEnd( getFacesContext(), component );
    }

    /**
     * @deprecated Use in X components
     * encodeEnd
     * @see #encodeEnd(XUIComponentBase)
     */
     @Deprecated
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException
    {
        if( component instanceof XUIComponentBase ) {
        	XUIComponentBase comp = (XUIComponentBase)component;
        	if( comp.getRenderComponent() )
        		this.encodeEnd( (XUIComponentBase)component );
        } else
            super.encodeEnd( context, component );
    }


    /**
     * @deprecated Use in X components
     * getConvertedValue
     */
     @Deprecated
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException
    {
        return super.getConvertedValue(context, component, submittedValue);
    }

    /**
     * @deprecated Use in X components
     * convertClientId
     */
     @Deprecated
    public String convertClientId(String clientId)
    {
        return super.convertClientId(getFacesContext(), clientId);
    }
    
    public XUIResponseWriter getResponseWriter()
    {
        return (XUIResponseWriter)getFacesContext().getResponseWriter();
    }
    
    public FacesContext   getFacesContext()
    {
        return FacesContext.getCurrentInstance();
    }
    
    public XUIRequestContext getContext() {
    
        return XUIRequestContext.getCurrentContext();
    
    }
    
    public String getResourceURL( Class parentClass, String resourceName ) {

        String className;
        StringBuffer retUrl;

        FacesContext context;

        context = getFacesContext();
        className = parentClass.getName();
        
        retUrl = new StringBuffer();
        retUrl.append( context.getExternalContext().getRequestContextPath() );
        retUrl.append( "/res/" );
        retUrl.append( className.substring(0,className.lastIndexOf( '.' )).replace('.','/') );
        retUrl.append( '/' );
        retUrl.append( resourceName );

        return retUrl.toString();   

    }
    
    
    public boolean isRenderForUpdate(XUIComponentBase component){
    	return component.wasStateChanged() == StateChanged.FOR_UPDATE;
	}
    
    public StateChanged wasStateChanged(XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties) {
        
        //If not a post back to this component, assume state changed
        // to force render of the component
        if( !component.isPostBack() || !component.isRenderedOnClient() ) {
            return StateChanged.FOR_RENDER;
        }
        
        StateChanged changed = StateChanged.NONE;
        Iterator<Entry<String,XUIBaseProperty<?>>> oStatePropertiesIterator = component.getStateProperties().iterator();
        if( oStatePropertiesIterator != null ) {
            while( oStatePropertiesIterator.hasNext() ) {
                XUIBaseProperty<?> oStateProperty;
                oStateProperty = oStatePropertiesIterator.next().getValue();
                if (!updateProperties.contains( oStateProperty )){
	                if( oStateProperty.wasChanged() ) {
	                    changed = StateChanged.FOR_RENDER;
	                }
                } else {
                	if( oStateProperty.wasChanged() ) {
                		if (changed != StateChanged.FOR_RENDER)
                			changed =  StateChanged.FOR_UPDATE;
	                }
                }
            }
        }
        return changed;
    }
    
    public void processStateChanged( XUIComponentBase component, List<XUIComponentBase> oRenderList ) {
        boolean bChanged;
        UIComponent oKid;
        
        if( component.isRenderedOnClient() != component.getRenderComponent() ) {
        	oRenderList.add( component );
        	return;
        }
        
        if( component.getRenderComponent() ) {
	        List<UIComponent> oKids = component.getChildren();
	        for (int i = 0; i < oKids.size(); i++) {
	            
	        	bChanged = false;
	            
	            oKid = oKids.get( i );
	            
	            if( oKid instanceof XUIComponentBase ) {
	            	
	            	XUIComponentPlugIn plugIn = component.getPlugIn();
	            	
	            	if( plugIn != null &&  plugIn.wasStateChanged() ) {
	                    oRenderList.add( (XUIComponentBase)oKid );
	            	}
	            	else {
	            		StateChanged change = ((XUIComponentBase)oKid).wasStateChanged(); 
		                if( (change == StateChanged.FOR_RENDER
		                		|| change == StateChanged.FOR_UPDATE)) {
		                    oRenderList.add( (XUIComponentBase)oKid );
		                    bChanged = true;                    
		                }
		                if( !bChanged ) {
		                    ((XUIComponentBase)oKid).processStateChanged( oRenderList );    
		                }
	            	}
	            }
	            else
	            	recursiveProcessStateChanged(oKid, oRenderList);
	        }
        }
    }
    
    /**
     * 
     * Recursively calls the <code>processStateChanged</code> 
     * 
     * @param oComponent The component to process
     * @param oRenderList The list of components whose state was changed
     */
    private void recursiveProcessStateChanged(UIComponent oComponent, List<XUIComponentBase> oRenderList)
    {
    	Iterator<UIComponent> kids = oComponent.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if( kid instanceof XUIComponentBase ) {
                ((XUIComponentBase)kid).processStateChanged(oRenderList);
            }
            else
            	recursiveProcessStateChanged(kid, oRenderList);
        }
    }
    
    public String composeUrlWithWebContext( String resourcePath ) {
        StringBuffer retUrl;
        FacesContext context;
        
        context = getFacesContext();
        retUrl = new StringBuffer();
        retUrl.append( context.getExternalContext().getRequestContextPath() );
        
        if( resourcePath == null || resourcePath.length() == 0 || resourcePath.charAt( 0 ) != '/' ) {
        	retUrl.append( '/' );
        }
        retUrl.append( resourcePath );
        return retUrl.toString();
        
    }
    
    public XUITheme getTheme() {
        return ((XUIViewRoot)getFacesContext().getViewRoot()).getTheme();
    }

    public boolean isAjax() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequest() instanceof XUIAjaxRequestWrapper;
    }
    
    public XUIRequestContext getRequestContext() {
    	return XUIRequestContext.getCurrentContext();
    }
    
    public XUISessionContext getSessionContext() {
    	return getRequestContext().getSessionContext();
    }
    
    public static final class Util
    {
        public static final boolean isEmpty( String value ) {
            return ( value == null || value.length() == 0 );
        }
    }

	/**
	 * 
	 * Method called when the component needs to change visually, but not do an entire re-render.
	 * Typically, use this method to send Javascript updates to a rendered component
	 * 
	 * @param component The component to encode changes
	 * @param propertiesWithChangedState The list of properties that changed state
	 * 
	 * @throws IOException If something went wrong writing to the output stream
	 */
	public void encodeComponentChanges(XUIComponentBase component, List<XUIBaseProperty<?>> propertiesWithChangedState) throws IOException {
		component.setDestroyOnClient( true );
		component.encodeAll();
	}

}    
