package netgest.bo.xwc.framework.jsf;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;
import netgest.bo.xwc.framework.localization.XUICoreMessages;


public class XUIViewerBuilder
{
//    private static final Log log = LogFactory.getLog(netgest.bo.xwc.framework.jsf.XUIViewerBuilder.class);
    
    public void buildView( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, UIViewRoot oUIViewRoot  )    {
        buildComponentTree( oContext, oViewerDefinition, oUIViewRoot );
    }

    private void buildComponentTree( XUIRequestContext oContext, XUIViewerDefinition oViewerDefinition, UIViewRoot root )
    {
        buildComponent( oContext, root, root, oViewerDefinition.getRootComponent() );
    }
    
    private void buildComponent( XUIRequestContext oContext, UIViewRoot root,UIComponent parent, XUIViewerDefinitionNode dcomponent )
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
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (long)valueLong );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (short)valueLong );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (byte)valueLong );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (float)valueLong );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (double)valueLong );
            }

            if( !wasSet && isFloat ) {
            	wasSet = setComponentProperty( comp, setterName, (float)valueDouble );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (double)valueDouble );
            }
            
            if( !wasSet ) {
            	wasSet = setComponentProperty( comp, setterName, value );
            	wasSet = !wasSet && setComponentProperty( comp, setterName, (Object)value );
            }
            
            /*
            try
            {

            	try {
            		
					Method m = comp.getClass().getMethod( setterNameName, new Class[] { String.class } );
					m.invoke( 	
							comp, 
							new Object[] { 
								dcomponent.getProperty( keyName ) 
							} 
						);
				} catch (NoSuchMethodException e) {
					Method m = comp.getClass().getMethod( setterNameName, new Class[] { Object.class } );
					m.invoke( 	
							comp, 
							new Object[] { 
								 
							} 
						);
				}
            } catch (Exception ex) {
            }
            */
        }
        
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
        assert name != null :"Name cannot be null";
        try
        {
            XUIComponentDefinition componentDef = oContext.getApplicationContext().getComponentStore().findComponent( name );
            if( componentDef == null && name.indexOf( ':' ) == -1 ) {
            	componentDef = oContext.getApplicationContext().getComponentStore().findComponent(  "xvw:" + name );
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

}
