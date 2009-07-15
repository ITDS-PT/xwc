package netgest.bo.xwc.framework.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;


public abstract class XUIComponentBase extends UIComponentBase 
{
    XUIBindProperty<Boolean> renderComponent = new XUIBindProperty<Boolean>( "renderComponent", this, Boolean.class );
    
    private static final    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    
    private LinkedHashMap<String, XUIBaseProperty> oStatePropertyMap;

    private boolean         isPostBack = false;
    
    @Override
	public String getId() {
		return super.getId();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}

	public void addStateProperty( XUIBaseProperty oStateProperty ) {
        if( oStatePropertyMap == null ) {
            oStatePropertyMap = new LinkedHashMap<String, XUIBaseProperty>();
        }
        oStatePropertyMap.put( oStateProperty.getName(), oStateProperty );
    }
    
    @Override
	public boolean isRendered() {
    	//if( this.renderComponent.getExpressionString() != null  ) {
    	//	return this.renderComponent.getEvaluatedValue();
    	//}
		return super.isRendered();
	}

    
	public void setRenderComponent( String sValueExpression ) {
    	this.renderComponent.setExpressionText(sValueExpression );
    }
    
    public boolean getRenderComponent() {
    	if( this.renderComponent.getExpressionString() != null )
    		return this.renderComponent.getEvaluatedValue();
    	return true;
    }
    
    public boolean wasStateChanged() {
        
        // If not a post back to this component, assume state changed
        // to force render of the component
        if( !isPostBack() ) {
            return true;
        }
        
        Iterator<XUIBaseProperty> oStatePropertiesIterator = getStateProperties();
        if( oStatePropertiesIterator != null ) {

            while( oStatePropertiesIterator.hasNext() ) {
                XUIBaseProperty oStateProperty;
                
                oStateProperty = oStatePropertiesIterator.next();
                
                if( oStateProperty.wasChanged() ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void processStateChanged( List<XUIComponentBase> oRenderList ) {
        boolean bChanged;
        UIComponent oKid;

        List<UIComponent> oKids = this.getChildren();
        for (int i = 0; i < oKids.size(); i++) {
            bChanged = false;
            oKid = oKids.get( i );
            if( oKid instanceof XUIComponentBase ) {
                if( ((XUIComponentBase)oKid).wasStateChanged() ) {
                    oRenderList.add( (XUIComponentBase)oKid );
                    bChanged = true;                    
                }
                if( !bChanged ) {
                    ((XUIComponentBase)oKid).processStateChanged( oRenderList );    
                }

            }
        }
    }
    
    public Iterator<XUIBaseProperty> getStateProperties() {
        if( oStatePropertyMap != null ) {
            return oStatePropertyMap.values().iterator();
        }
        return null;
    }

    public Iterator<String> getStatePropertyNames() {
        if( oStatePropertyMap != null ) {
            return oStatePropertyMap.keySet().iterator();
        }
        return null;
    }
    
    public XUIComponentBase findParentComponent( Class cType ) {
    	UIComponent oParentComp;
    	
    	oParentComp = getParent();
    	while( oParentComp != null && !cType.isInstance( oParentComp ) ) {
    		oParentComp = oParentComp.getParent();
    	}
    	return (XUIComponentBase)oParentComp;
    }
    
    public XUIComponentBase findComponent( Class cType ) {
    	
    	assert cType != null;
    	
    	if( cType.isInstance( this ) ) {
    		return this;
    	}
    	
    	XUIComponentBase oRet;
    	List<UIComponent> oChildrenList;
    	
    	oRet = null;
    	
    	oChildrenList = getChildren();
    	for( UIComponent child : oChildrenList ) {
    		if( cType.isInstance( child ) ) {
    			oRet = (XUIComponentBase)child;
    			break;
    		}
    		oRet = ((XUIComponentBase)child).findComponent( cType );
    		if( oRet != null ) {
    			break;
    		}
    	}
    	return oRet;
    }
    
    
    public Object saveState() 
    {   
        // Save the state of all the properties of the object
        Object[] oStateProperyState;
        int      iCntr = 0;

        if( this.oStatePropertyMap != null && oStatePropertyMap.size() > 0 ) {
        	oStateProperyState = new Object[ this.oStatePropertyMap.size() ];
            Iterator<XUIBaseProperty> oStatePropertiesIt = getStateProperties();
            while( oStatePropertiesIt.hasNext() ) {
            	
            	XUIBaseProperty x = oStatePropertiesIt.next();

            	//long ini = System.currentTimeMillis();
            	long ini_2   = System.nanoTime();
            	
            	if( !x.isDefaultValue() )
            		oStateProperyState[ iCntr ] = x.saveState();
            	else
            		oStateProperyState[ iCntr ] = null;
            	
                //long end = System.currentTimeMillis();
                long end_2 = System.nanoTime();

                if( 300000 < (end_2-ini_2) ) {
                	//System.out.println( "Save state demorado: [ " + this.getClass().getName() +  "." + x.getName() + " ]:" + ( end-ini ) );
                	//System.out.println( "Save state demorado: ["+this.getId()+"][ " + this.getClass().getName() +  "." + x.getName() + " ]:" + ( end_2-ini_2 ) );
                }
                
                iCntr ++;
            }
        }
        else {
            oStateProperyState = EMPTY_OBJECT_ARRAY;
        }
        // Call super to allow him to save her state;
        return new Object[] { super.saveState( getFacesContext() ), oStateProperyState };
    }
    
    public void restoreState( Object oState ) {
        Object[] oMyState = (Object[])oState;
        Object[] oPropertiesState;
        int      iCntr = 0;
        isPostBack = true;
        
        // Restore all the properties
        super.restoreState( getFacesContext(), oMyState[0] );
        oPropertiesState = (Object[])oMyState[1];
        
        if( this.oStatePropertyMap != null )
        {
            Iterator<XUIBaseProperty> oStatePropertiesIt = getStateProperties();
            while( oStatePropertiesIt.hasNext() ) {
            	
            	if( oPropertiesState[ iCntr ] != null )
            		oStatePropertiesIt.next().restoreState( oPropertiesState[ iCntr ]  );
            	else 
            		oStatePropertiesIt.next();
            	
                iCntr ++;
            }
        }
    }
    
    public UIComponent getChild( int i ) {
        return super.getChildren().get( i );
    }
    

    public void decode()
    {
        super.decode( FacesContext.getCurrentInstance() );
    }

    public void encodeBegin(  ) throws IOException
    {
    	if( getRenderComponent() )
    		super.encodeBegin( FacesContext.getCurrentInstance() );
    }

    public void encodeEnd(  ) throws IOException
    {
    	if( getRenderComponent() )
    		super.encodeEnd( FacesContext.getCurrentInstance() );
    }

    public void encodeChildren(  ) throws IOException
    {
    	if( getRenderComponent() )
    		super.encodeChildren( FacesContext.getCurrentInstance() );
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #decode()
     */
     @Deprecated
    public void decode(FacesContext context)
    {
        this.decode();
    }
    
    
    

    @Override
    public String getRendererType() 
    {
        String sRenderType = getClass().getName();
        sRenderType = sRenderType.substring( sRenderType.lastIndexOf( "." ) + 1 );
        sRenderType = sRenderType.substring(0,1).toLowerCase() + sRenderType.substring(1);
        return sRenderType;
    }


    public String getFamily() {
        return getRendererType();
    }


    @Override
    public Object saveState(FacesContext context) 
    {
//    	long ini = System.currentTimeMillis();
        Object ret = saveState();
//        long end = System.currentTimeMillis();
//        if( 50 < (end-ini) ) {
//        	System.out.println( "Save state demorado: [ " + this.getClass().getName() +  " ]:" + ( end-ini ) );
//        }
        return ret;
        
    }

    @Override
    public void restoreState(FacesContext context, Object state) 
    {
        restoreState( state );
    }

    public void encodeAll() throws IOException {
        super.encodeAll( FacesContext.getCurrentInstance() );
    }
    

    public String getClientId() {
        return super.getClientId(getFacesContext());
    }

    public ValueExpression createValueExpression( String sValueExpression, Class cType ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createValueExpression( 
                    getFacesContext().getELContext(), sValueExpression, cType
                ); 
    }

    private static final Class[] DUMMY_CLASS_ARRAY = new Class[0];
    public MethodExpression createMethodBinding( String sMethodExpression ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( getFacesContext().getELContext(), sMethodExpression, null, DUMMY_CLASS_ARRAY );
    }

    public MethodExpression createMethodBinding( String sMethodExpression, Class oReturnValue ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( getFacesContext().getELContext(), sMethodExpression, null, DUMMY_CLASS_ARRAY );
    }

    protected UIComponent getNamingContainer() {
        UIComponent namingContainer = this.getParent();
        while (namingContainer != null) {
            if (namingContainer instanceof NamingContainer) {
                return namingContainer;
            }
            namingContainer = namingContainer.getParent();
        }
        return null;
    }
    
    public void initComponent() {
        
    }
    
    public void processInitComponents() {
        // Process this component itself
        initComponent();

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processInitComponents();
            }
        }
    }

    public void preRender() {
        
    }
    
    public void processPreRender() {
        // Process this component itself
        preRender();

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processPreRender();
            }
        }
    }

    public void validateModel() {
        
    }

    public void processValidateModel() {
        // Process this component itself
        validateModel();

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processValidateModel();
            }
        }
    }

    public String getNamingContainerId() {
        return getNamingContainer().getId();
    }


    public XUIRequestContext getRequestContext() {
        return XUIRequestContext.getCurrentContext();
    }

    public ELContext getELContext() {
        return getFacesContext().getELContext();
    }

    public XUISessionContext getSessionContext() {
        return getRequestContext().getSessionContext();
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #encodeAll()
     */
     @Deprecated
    public void encodeAll(FacesContext context) throws IOException {
        super.encodeAll(context);
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #encodeBegin()
     */
     @Deprecated
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #encodeChildren()
     */
     @Deprecated
    public void encodeChildren(FacesContext context) throws IOException {
        super.encodeChildren(context);
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #encodeEnd()
     */
     @Deprecated
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);
    }

    /**
     * @deprecated Use in X components
     * decode
     * @see #getClientId()
     */
    @Override
    @Deprecated
    public String getClientId(FacesContext context) {
        return super.getClientId(context);
    }

    public boolean isPostBack() {
        return isPostBack;
    }

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		if ( binding != null && binding.isLiteralText() ) {
			if( binding.getExpectedType() == String.class  ) {
				binding = createValueExpression( "#{'" + binding.getExpressionString() + "'}" , binding.getExpectedType() );
			}
			else {
				binding = createValueExpression( "#{" + binding.getExpressionString() + "}" , binding.getExpectedType() );
			}
		}
		super.setValueExpression( name , binding);
	}


}
