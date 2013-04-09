package netgest.bo.xwc.framework.jsf;

import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIPropertySetter {

	
    public static void setProperty( UIComponent comp, String propertyName,String value )
    {
        String setterName = "set" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
        
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
            	//Added so that the JSF Components can be binded to beans
                // Apparently there's something in the JSF platform that makes
                // binding automatically and we need to make some adjustments
                //in out platform (like setting the property value as a valueExpression)
            	FacesContext context = FacesContext.getCurrentInstance();
            	ELContext elContext = context.getELContext();
            	Application jsfApp = context.getApplication();
            	ExpressionFactory exprFactory = jsfApp.getExpressionFactory();
            	ValueExpression vExpr = exprFactory.createValueExpression(elContext, value, Object.class);
				try 
				{
            		comp.setValueExpression(propertyName, vExpr);
            	} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
            	if (vExpr.isLiteralText())
            		setComponentProperty(comp, setterName, value);
            }
        }     
    }
    
    private static boolean setComponentProperty( Object component, String setterName, boolean value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Boolean.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }
    
    private static boolean setComponentProperty( Object component, String setterName, byte value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Byte.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private static boolean setComponentProperty( Object component, String setterName, short value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Short.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private static boolean setComponentProperty( Object component, String setterName, int value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Integer.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private static boolean setComponentProperty( Object component, String setterName, long value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Long.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private static boolean setComponentProperty( Object component, String setterName, float value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Float.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
    	catch( Exception e ) {
    		return false;
    	} 
    }

    private static boolean setComponentProperty( Object component, String setterName, double value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Double.TYPE }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }

    private static boolean setComponentProperty( Object component, String setterName, String value ) {
		try {
			Method m = component.getClass().getMethod( setterName, new Class[] { String.class }  );
			m.invoke(  component,  new Object[] { value } );
			return true;
		}
		catch( Exception e ) {
			return false;
		}
    }
    
    private static boolean setComponentProperty( Object component, String setterName, Object value ) {
    	try {
    		Method m = component.getClass().getMethod( setterName, new Class[] { Object.class }  );
			m.invoke(  component,  new Object[] { value } );
    		return true;
    	}
		catch( Exception e ) {
			return false;
		}
    }
}
