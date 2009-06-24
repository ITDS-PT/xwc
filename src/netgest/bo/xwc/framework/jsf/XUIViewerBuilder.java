package netgest.bo.xwc.framework.jsf;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;
import netgest.bo.xwc.framework.def.XUIViewerDefinitonParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XUIViewerBuilder
{
    private static final Log log = LogFactory.getLog(netgest.bo.xwc.framework.jsf.XUIViewerBuilder.class);

    /*
    public void pre_Render( UIViewRoot root )
    {
        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        if( root.getChildCount() == 0 ) 
        {
            XUIViewerDefinitonParser vparser = new XUIViewerDefinitonParser();
            vdef = oXUIApplication.getViewerDef( viewerName );
            buildComponentTree( root );
        }

    }
*/
    
    public void buildView( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, UIViewRoot oUIViewRoot  )    {
        buildComponentTree( oContext, oViewerDefinition, oUIViewRoot );
    }

    private void buildComponentTree( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, UIViewRoot root )
    {
        buildComponent( oContext, root, root, oViewerDefinition.getRootComponent() );
    }
    
    private void buildComponent( XUIRequestContext oContext, UIViewRoot root,UIComponent parent, XUIViewerDefinitionNode dcomponent )
    {
    	if( "genericTag".equalsIgnoreCase( dcomponent.getName() ) ) {
    		boolean todebug = true;
    		todebug = false;
    	}
    	
    	UIComponent comp = createComponent( oContext, dcomponent.getName() );
        comp.setId( dcomponent.getId()==null?root.createUniqueId():dcomponent.getId() );
        
        //TODO: Set properties
        try
        {
        	Method m = comp.getClass().getMethod( "setProperties",new Class[] { Map.class } );
            m.invoke( comp, new Object[] { dcomponent.getProperties() } );
        }
        catch(Exception e)
        {
        	e = null;
            //TODO:Warning Message;
        }
        
        try
        {
        	Method m = comp.getClass().getMethod( "setTextContent",new Class[] { String.class } );
            m.invoke( comp, new Object[] { dcomponent.getTextContent() } );
        }
        catch(Exception e)
        {
        	e = null;
            //TODO:Warning Message;
        }
        
        Iterator<String> keysEnum = dcomponent.getProperties().keySet().iterator();
        while( keysEnum.hasNext() )
        {
            String keyName = String.valueOf( keysEnum.next() );
            String fieldName = "set" + keyName.substring(0,1).toUpperCase() + keyName.substring(1);

            try
            {

            	try {
            		
					Method m = comp.getClass().getMethod( fieldName, new Class[] { String.class } );
					m.invoke( 	
							comp, 
							new Object[] { 
								dcomponent.getProperty( keyName ) 
							} 
						);
				} catch (NoSuchMethodException e) {
					Method m = comp.getClass().getMethod( fieldName, new Class[] { Object.class } );
					m.invoke( 	
							comp, 
							new Object[] { 
								dcomponent.getProperty( keyName ) 
							} 
						);
				}
            } catch (Exception ex) {
            	/*
                try {
                	
                    Method m = 
                        comp.getClass().getMethod("set" + fieldName, 
                                                          new Class[] { Object.class });
                    m.invoke(comp, 
                             new Object[] { dcomponent.getProperty(keyName) });
                } catch (Exception e) {
                    Throwable t;
                    //TODO:Warning Message;
                    if( e.getCause() != null ) {
                        t = e.getCause();
                    }
                    else {
                        t = e;
                    }
                    log.warn( "Error setting component property " + dcomponent.getName() + ":" +  fieldName + ":" + t.getClass() + "-" + t.getMessage() );
                }
                */
            }
        }
		//if (  log.isDebugEnabled() )
		//	log.debug( " END Creating component " + dcomponent.getName() );
        
        parent.getChildren().add( comp );

        List children = dcomponent.getChildren();
        Iterator it = children.iterator();
        while( it.hasNext() )
        {
            buildComponent( oContext, root, comp, (XUIViewerDefinitionNode)it.next() );           
        }
        
    }
    
    
    public UIComponent createComponent( XUIRequestContext oContext, String name )
    {
        assert name != null :"Name cannot be null";
        try
        {
            XUIComponentDefinition componentDef = oContext.getApplicationContext().getComponentStore().findComponent( name );
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
        throw new RuntimeException("O componente ["+name+"] não está registado.");
    }
    
    public void render() throws IOException
    {
    }

}
