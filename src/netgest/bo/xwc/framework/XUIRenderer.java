package netgest.bo.xwc.framework;
import java.io.IOException;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.component.UIComponent;
import javax.faces.convert.ConverterException;

import netgest.bo.xwc.framework.http.XUIAjaxRequestWrapper;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

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

}    
