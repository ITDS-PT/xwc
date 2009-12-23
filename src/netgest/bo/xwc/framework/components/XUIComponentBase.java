package netgest.bo.xwc.framework.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

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
import netgest.bo.xwc.framework.XUIComponentPugIn;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;


public abstract class XUIComponentBase extends UIComponentBase 
{
    XUIBindProperty<Boolean> 			renderComponent = new XUIBindProperty<Boolean>( "renderComponent", this, Boolean.class );

    XUIBindProperty<XUIComponentPugIn> 	plugIn = new XUIBindProperty<XUIComponentPugIn>( "plugIn", this, XUIComponentPugIn.class );
    
    private static final    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    
    private LinkedHashMap<String, XUIBaseProperty> oStatePropertyMap;

    private boolean         isPostBack = false;
    private boolean			wasPreRenderProcessed = false;
    private boolean			wasInitComponentProcessed = false;
    private XUIBaseProperty<Boolean> 	isRenderedOnClient 
    	= new XUIBaseProperty<Boolean>("isRenderedOnClient", this, false);
    private boolean 		_isRenderedOnClient = false;
    private boolean			destroyOnClient = false;
    
    @Override
	public String getId() {
		return super.getId();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}
	
	public XUIComponentPugIn getPlugIn() {
		return this.plugIn.getEvaluatedValue();
	}
	
	public void setPlugIn( String expressionText ) {
		this.plugIn.setExpressionText( expressionText );
	}

	public void addStateProperty( XUIBaseProperty oStateProperty ) {
        if( oStatePropertyMap == null ) {
            oStatePropertyMap = new LinkedHashMap<String, XUIBaseProperty>();
        }
        oStatePropertyMap.put( oStateProperty.getName(), oStateProperty );
    }
	
	public XUIBaseProperty getStateProperty( String propertyName ) {
        return oStatePropertyMap.get( propertyName );
	}
    
    @Override
	public boolean isRendered() {
		return super.isRendered();
	}

    
	public void setRenderComponent( String sValueExpression ) {
    	this.renderComponent.setExpressionText(sValueExpression );
    }

	public void setRenderComponent( boolean renderComponent ) {
    	this.renderComponent.setValue( renderComponent );
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
        
        Iterator<Entry<String,XUIBaseProperty>> oStatePropertiesIterator = getStateProperties().iterator();
        if( oStatePropertiesIterator != null ) {
            while( oStatePropertiesIterator.hasNext() ) {
                XUIBaseProperty oStateProperty;
                oStateProperty = oStatePropertiesIterator.next().getValue();
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
            	
            	XUIComponentPugIn plugIn = getPlugIn();
            	
            	if( plugIn != null &&  plugIn.wasStateChanged() ) {
                    oRenderList.add( (XUIComponentBase)oKid );
                    bChanged = true;                    
            	}
            	else {
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
    }
    
    public Set<Entry<String,XUIBaseProperty>> getStateProperties() {
        if( oStatePropertyMap != null ) {
            return oStatePropertyMap.entrySet();
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
            Iterator<Entry<String,XUIBaseProperty>> oStatePropertiesIt = getStateProperties().iterator();
            while( oStatePropertiesIt.hasNext() ) {
            	XUIBaseProperty<?> x = oStatePropertiesIt.next().getValue();
            	if( !x.isDefaultValue() )
            		oStateProperyState[ iCntr ] = x.saveState();
            	else
            		oStateProperyState[ iCntr ] = null;
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
        wasInitComponentProcessed = true;
        // Restore all the properties
        super.restoreState( getFacesContext(), oMyState[0] );
        oPropertiesState = (Object[])oMyState[1];
        
        if( this.oStatePropertyMap != null )
        {
            Iterator<Entry<String,XUIBaseProperty>> oStatePropertiesIt = getStateProperties().iterator();
            while( oStatePropertiesIt.hasNext() ) {
            	if( oPropertiesState[ iCntr ] != null )
            		oStatePropertiesIt.next().getValue().restoreState( oPropertiesState[ iCntr ]  );
            	else 
            		oStatePropertiesIt.next();
            	
                iCntr ++;
            }
        }
        _isRenderedOnClient = this.isRenderedOnClient.getValue();
    }
    
    public UIComponent getChild( int i ) {
        return super.getChildren().get( i );
    }
    

    public void decode()
    {
        super.decode( FacesContext.getCurrentInstance() );
    }

    public void encodeBegin( ) throws IOException
    {
    	if( getRenderComponent() )
    		super.encodeBegin( FacesContext.getCurrentInstance() );
    }

    public void encodeEnd( ) throws IOException
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
        Object ret = saveState();
        return ret;
    }

    @Override
    public void restoreState(FacesContext context, Object state) 
    {
        restoreState( state );
    }

    public void encodeAll() throws IOException {
    	this.isRenderedOnClient.setValue( true );
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
    
    public final void processInitComponents() {
        // Process this component itself
    	
    	XUIComponentBase component = this;
    	
    	if( !wasInitComponentProcessed ) {
        	XUIComponentPugIn plugIn = getPlugIn();
    		
        	if( plugIn != null ) {
        		plugIn.setComponent( this );
        		plugIn.beforeInitComponent();
        	}
        	
        	if( plugIn != null && plugIn.isReplaced() )
        		component = plugIn.getComponent();

        	component.initComponent();
    		
            if( plugIn != null )
        		plugIn.afterInitComponent();
    		
    		wasInitComponentProcessed = true;
    	}
    	
        if( this != component ) {
        	List<UIComponent> childs = getParent().getChildren(); 
        	childs.set(  childs.indexOf( this ), component );
        }

        // Process all facets and children of this component
        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if( kid instanceof XUIComponentBase ) {
                ((XUIComponentBase)kid).processInitComponents();
            }
        }
        
        
    }

    public void preRender() {
        
    }
    
    public final void processPreRender() {
        // Process this component itself
    	
    	XUIComponentBase component = this;

    	if( !wasPreRenderProcessed ) {
        	XUIComponentPugIn plugIn = getPlugIn();
        	
        	if( plugIn != null ) {
        		plugIn.setComponent( this );
        		plugIn.beforePreRender();
        	}
        	
        	if( plugIn != null && plugIn.isReplaced() )
        		component = plugIn.getComponent();
        	
        	
        	component.preRender();
    		
            if( plugIn != null )
        		plugIn.afterPreRender();
    		
    		wasPreRenderProcessed = true;
    	}
    	
        if( this != component ) {
        	List<UIComponent> childs = getParent().getChildren(); 
        	childs.set(  childs.indexOf( this ), component );
        }
    	
        // Process all facets and children of this component
        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if( kid instanceof XUIComponentBase ) {
            	
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
        Iterator<UIComponent> kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof XUIComponentBase ) {
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
        this.isRenderedOnClient.setValue( true );
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

	public boolean isRenderedOnClient() {
		return this._isRenderedOnClient && XUIRequestContext.getCurrentContext().isAjaxRequest();
	}
	
	public void setRenderedOnClient( boolean renderedOnClient ) {
		this._isRenderedOnClient = renderedOnClient;
		this.isRenderedOnClient.setValue( renderedOnClient );
	}

	public void setDestroyOnClient( boolean destroyOnClient ) {
		this.destroyOnClient = destroyOnClient;
	}
	
	public boolean isDestroyOnClient() {
		return this.destroyOnClient;
	}
	
	public void resetRenderedOnClient() {
		this.isRenderedOnClient.setValue( false );
		List<UIComponent> children = getChildren();
		for( UIComponent child : children ) {
			if( child instanceof XUIComponentBase ) {
				((XUIComponentBase)child).resetRenderedOnClient();
			}
		}
	}
	
	public void forceRenderOnClient() {
		this._isRenderedOnClient = false;
		List<UIComponent> children = getChildren();
		for( UIComponent child : children ) {
			if( child instanceof XUIComponentBase ) {
				((XUIComponentBase)child).forceRenderOnClient();
			}
		}
	}
	
	
}
