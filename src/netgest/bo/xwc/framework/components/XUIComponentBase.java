package netgest.bo.xwc.framework.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIDefaultPropertiesHandler;
import netgest.bo.xwc.framework.XUIELContextWrapper;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.jsf.XUIPropertySetter;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;
import netgest.bo.xwc.framework.jsf.XUIWriteBehindStateWriter;
import netgest.bo.xwc.framework.properties.XUIProperty;
import netgest.bo.xwc.framework.properties.XUIPropertyVisibility;
import netgest.utils.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.faces.io.FastStringWriter;


public abstract class XUIComponentBase extends UIComponentBase 
{
    private static final int UPDATED_PROPERTIES_INITIAL_SIZE = 0;

	@XUIProperty( label="Render Component" )
	XUIBindProperty<Boolean> 			renderComponent = new XUIBindProperty<Boolean>( "renderComponent", this, Boolean.class );

    @XUIProperty( label="PlugIn" )
    XUIBindProperty<XUIComponentPlugIn> plugIn = new XUIBindProperty<XUIComponentPlugIn>( "plugIn", this, XUIComponentPlugIn.class );
    
    private static final    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    
    private LinkedHashMap<String, XUIBaseProperty<?>> oStatePropertyMap;

    private boolean isPostBack = false;
    private boolean	wasPreRenderProcessed = false;
    private boolean	wasInitComponentProcessed = false;
    
    /**
     * The identifier of the bean to fetch values from,
     * defaults to "viewBean"
     */
    private XUIBindProperty<String> beanId = 
    	new XUIBindProperty<String>("beanId", this, "viewBean", String.class);
    
    public void setBeanId( String newBeanId ){
    	this.beanId.setExpressionText( newBeanId );
    }
    
    public String getBeanId(){
    	return this.beanId.getEvaluatedValue();
    }

    private XUIBaseProperty<Boolean> isRenderedOnClient = 
    	new XUIBaseProperty<Boolean>("isRenderedOnClient", this, false);
    
    private boolean _isRenderedOnClient = false;
    private boolean	destroyOnClient = false;
     
    @Override
	public String getId() {
		return super.getId();
	}

	@Override
    @XUIProperty(name="id",label="Component Id", visibility=XUIPropertyVisibility.DOCUMENTATION )
	public void setId(String id) {
		super.setId(id);
	}
	
	public XUIComponentPlugIn getPlugIn() {
		return this.plugIn.getEvaluatedValue();
	}
	
	public void setPlugIn( String expressionText ) {
		this.plugIn.setExpressionText( expressionText );
	}

	public void addStateProperty( XUIBaseProperty<?> oStateProperty ) {
        if( oStatePropertyMap == null ) {
            oStatePropertyMap = new LinkedHashMap<String, XUIBaseProperty<?>>();
        }
        oStatePropertyMap.put( oStateProperty.getName(), oStateProperty );
    }
	
	public XUIBaseProperty<?> getStateProperty( String propertyName ) {
        return oStatePropertyMap.get( propertyName );
	}
    
	public void setRendered(String renderedValueExpression) {
		if( "true".equals(renderedValueExpression) ) 
			super.setRendered(true);
		else if ( "false".equals(renderedValueExpression) )
			super.setRendered(false);
		else
			setValueExpression("rendered",  createValueExpression(renderedValueExpression, Boolean.class ) );
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
    
    public enum StateChanged {
    	NONE,
    	FOR_RENDER,
    	FOR_UPDATE;
    	
    }
    
    private List<XUIBaseProperty<?>> updatedProperties 
		= new ArrayList<XUIBaseProperty<?>>( UPDATED_PROPERTIES_INITIAL_SIZE ) ;

    private StateChanged changed = null;

	public void setStateChange(StateChanged newState){
		changed = newState;
	}
	
	@Deprecated
	public boolean wasStateChanged(){
		return wasStateChanged2() == StateChanged.FOR_RENDER;
	}
	
	public StateChanged wasStateChanged2() {
		if (changed == null){
	    	XUIRenderer renderer = (XUIRenderer ) getRenderer( getFacesContext() );
	    	if (renderer != null)
	    		changed = renderer.wasStateChanged( this, updatedProperties );
	    	else
	    		changed = StateChanged.NONE;
		}
		return changed;
    }
    
    
    
    public void processStateChanged( List<XUIComponentBase> oRenderList ) {
    	XUIRenderer renderer = (XUIRenderer ) getRenderer( getFacesContext() );
    	if (renderer != null)
    		renderer.processStateChanged( this, oRenderList );
    	
    }
    
    
    public Set<Entry<String,XUIBaseProperty<?>>> getStateProperties() {
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
    
    public XUIComponentBase findParentComponent( Class<?> cType ) {
    	UIComponent oParentComp;
    	
    	oParentComp = getParent();
    	while( oParentComp != null && !cType.isInstance( oParentComp ) ) {
    		oParentComp = oParentComp.getParent();
    	}
    	return (XUIComponentBase)oParentComp;
    }
    
    public UIComponent findParent( Class<?> cType ) {
    	UIComponent oParentComp;
    	
    	oParentComp = getParent();
    	while( oParentComp != null && !cType.isInstance( oParentComp ) ) {
    		oParentComp = oParentComp.getParent();
    	}
    	return oParentComp;
    }
    
    public XUIComponentBase findComponent( Class<?> cType ) {
    	
    	assert cType != null;
    	
    	if( cType.isInstance( this ) ) {
    		return this;
    	}
    	
    	XUIComponentBase oRet;
    	Iterator<UIComponent> oChildrenList;
    	
    	oRet = null;
    	
    	oChildrenList = getFacetsAndChildren();
    	while (oChildrenList.hasNext()){
    		UIComponent child = oChildrenList.next();
    		if( cType.isInstance( child ) ) {
    			oRet = (XUIComponentBase)child;
    			break;
    		}
    		if (child instanceof XUIComponentBase)
    		{
    			oRet = ((XUIComponentBase)child).findComponent( cType );
        		if( oRet != null ) {
        			break;
        		}
    		}
    		else
    		{
    			XUIComponentBase other = findComponent(child, cType);
    			if (other != null)
    				return other;
    		}
    			
    			
    	}
    	return oRet;
    }	
	
	private XUIComponentBase findComponent(UIComponent current, Class<?> cType )
	{
		XUIComponentBase oComp = null;
		if (current != null)
		{
			Iterator<UIComponent> list = current.getFacetsAndChildren();
			while (list.hasNext()){
				UIComponent component = list.next();
				if (component instanceof XUIComponentBase)
				{
					oComp = ((XUIComponentBase) component).findComponent(cType);
					if (oComp != null) {
						return oComp;
					}
				}
				else
				{
					Iterator<UIComponent> listChildren = component.getFacetsAndChildren();
					while (listChildren.hasNext()){
						UIComponent chilCmp = listChildren.next();
						return findComponent(chilCmp,cType);
					}
				}
			}
			
		}
		
		return null;
		
	}
	
    @Override
	public UIComponent findComponent(String expr) {
    	UIComponent oComp=super.findComponent(expr);
		
		if (oComp == null){
			UIComponent result = null;
			for (Iterator<UIComponent> i = getFacetsAndChildren(); i.hasNext();) {
				UIComponent kid =  i.next();
					if ( expr.equals(kid.getClientId( getFacesContext() ))) {
						result = kid;
						oComp = result;
						break;
					}
					result = findComponent(kid, expr, true);
					if (result != null) {
						oComp = result;
						break;
					}
			}
		}
		
		return oComp;
	}

	private static UIComponent findComponent(UIComponent base, String id,
			boolean checkId) {
		return base.findComponent( id );
	}
	
    public Object saveState() 
    {   
    	if( isRendered() || !XUIViewHandler.isSavingInCache()  ) {
	        // Save the state of all the properties of the object
	        Object[] oStateProperyState;
	        int      iCntr = 0;
	
	        if( this.oStatePropertyMap != null && oStatePropertyMap.size() > 0 ) {
	        	oStateProperyState = new Object[ this.oStatePropertyMap.size() ];
	            Iterator<Entry<String,XUIBaseProperty<?>>> oStatePropertiesIt = getStateProperties().iterator();
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
	        return new Object[] { super.saveState( getFacesContext() ), oStateProperyState, wasRendererTypeSet };
    	}
    	return null;
    }
    
    public void restoreState( Object oState ) {
    	if( isRendered() && oState != null ) {
	        Object[] oMyState = (Object[])oState;
	        Object[] oPropertiesState;
	        int      iCntr = 0;
	        
	        isPostBack = true;
	        wasInitComponentProcessed = true;
	        // Restore all the properties
	        super.restoreState( getFacesContext(), oMyState[0] );
	        oPropertiesState = (Object[])oMyState[1];
	        wasRendererTypeSet = (Boolean) ( oMyState[2] );
	        
	        if( this.oStatePropertyMap != null )
	        {
	            Iterator<Entry<String,XUIBaseProperty<?>>> oStatePropertiesIt = getStateProperties().iterator();
	            while( oStatePropertiesIt.hasNext() ) {
	            	XUIBaseProperty<?> prop = oStatePropertiesIt.next().getValue();
	            	if( oPropertiesState[ iCntr ] != null )
	            		prop.restoreState( oPropertiesState[ iCntr ]  );
	            	
	                iCntr ++;
	            }
	        }
	        _isRenderedOnClient = this.isRenderedOnClient.getValue();
    	}
    }
    
    public UIComponent getChild( int i ) {
        return super.getChildren().get( i );
    }
    
    /**
     * 
     * Appends a child to the children of the component
     * 
     * @param child The child to append
     * 
     */
    public void addChild( UIComponent child ){
    	this.getChildren().add( child );
    }
    
    /**
     * 
     * Appends a child at a specific index
     * 
     * @param child The child to append
     * @param index The index where to append
     */
    public void addChildAtIndex(UIComponent child, int index){
    	this.getChildren().add( index, child );
    }
    
    /**
     * 
     * Adds the sibling to the component
     * 
     * @param sibling The sibling to add
     */
    public void addSibling( UIComponent sibling ){
    	UIComponent parent = this.getParent();
    	if (parent != null){
    		int position = 0;
    		for (UIComponent curr : parent.getChildren()){
    			if (curr.getId().equalsIgnoreCase( getId() )){
    				break;
    			}
    			position++;
    		}
    		
    		parent.getChildren().add( position, sibling );
    	}
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
    	if (rendererType.isDefaultValue( ) || StringUtils.isEmpty( rendererType.getValue() ) ){
    		return calculateRendererType( );
    	} else 
    		return rendererType.getValue( );
    }
    
    /**
     *  Renderer to use
     */
    protected XUIBaseProperty<String> rendererType = new XUIBaseProperty<String>("rendererType", this);
   
    private Boolean wasRendererTypeSet = Boolean.FALSE;
    
    public void setRendererType(String type){
    	wasRendererTypeSet = Boolean.TRUE;
    	rendererType.setValue(type);
    }
    

    /*
     * Overridden to be used to allow multiple renderes for the same
     * component, returns the "rendererType" value 
     * 
     * @see javax.faces.component.UIComponent#getFamily()
     */
    @Override
    public String getFamily() {
    	if (rendererType.isDefaultValue( ) && !wasRendererTypeSet)
    		return getRendererType( );
    	return calculateRendererType( );
    }

    private String rendererTypeCache = null;
	private String calculateRendererType() {
		if (rendererTypeCache == null){
			String value = getClass().getName();
			value = value.substring( value.lastIndexOf( "." ) + 1 );
			rendererTypeCache = value.substring(0,1).toLowerCase() + value.substring(1);
		}
		return rendererTypeCache;
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
    	encodeAll( getFacesContext() );
    }
    
    @Override
    public void encodeAll( FacesContext context ) throws IOException {
        HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();
    	if( XUIRequestContext.getCurrentContext().isAjaxRequest() && request.getAttribute( "__xwcAjaxTagOpened") == null ) {
            XUIResponseWriter newWriter;
            
            try {
            	request.setAttribute( "__xwcAjaxTagOpened" , Boolean.TRUE );
            
	            RenderKit renderKit = 
	            	(RenderKit)request.getAttribute("__xwcRenderKit");
	
	            Document oAjaxXmlResp = 
	            	(Document)request.getAttribute("__xwcAjaxDomDoc");
	            
	            Element oRenderElement = 
	            	(Element)request.getAttribute("__xwcRenderElement");
	            
	            XUIResponseWriter previousWriter = 
	            	(XUIResponseWriter) context.getResponseWriter();
	
	            Writer oComponentWriter = new FastStringWriter(4192/4);
	            
	            // Setup new writer for each component to render
	            XUIWriteBehindStateWriter oCompBodyWriter =
	                  new XUIWriteBehindStateWriter(    oComponentWriter,
	                		  							context,
	                                                    4192/4
	                                                );
	            
	            newWriter = (XUIResponseWriter)renderKit.createResponseWriter(oCompBodyWriter,
	                                                           null,
	                                                           "utf-8");
	            
	            PackageIAcessor.setScriptContextToWriter( newWriter, previousWriter.getScriptContext() );
	            
	            context.setResponseWriter(newWriter);
	            
	            // Sets the header and footer wr
	            PackageIAcessor.setHeaderAndFooterToWriter( 
	            		newWriter, 
	            		previousWriter.getHeaderWriter(), 
	            		previousWriter.getFooterWriter() 
	            );
	            
	            newWriter.startDocument();
	
	        	if( getRenderComponent() ) {
	        		if (!isRenderForUpdate())
	        			super.encodeAll( context );
	        		else
	        			encodeUpdate(context, updatedProperties);
	        		this._isRenderedOnClient = true;
	        		this.isRenderedOnClient.setValue( true );
	        	}
	        	else {
	        		this.isRenderedOnClient.setValue( false );
	        		encodePlaceHolder( newWriter );
	        	}
	                        
	            newWriter.endDocument();
	            oCompBodyWriter.flushToWriter( false );
	            oCompBodyWriter.release();
	
	            Element  oCompElement;
	            oCompElement = oAjaxXmlResp.createElement( "component" );
	            oCompElement.setAttribute("id", getClientId() );
	        	oCompElement.setAttribute("destroy", Boolean.toString( isDestroyOnClient() ) );
	            
	            String s = oComponentWriter.toString();
	            if( s != null && s.length() > 0 ) {
	            	oCompElement.appendChild( oAjaxXmlResp.createCDATASection( s ) );
	            }
	            oRenderElement.appendChild( oCompElement );
            }
            finally {
            	request.removeAttribute( "__xwcAjaxTagOpened" );
            }
    	}
    	else {
        	if( getRenderComponent() ) {
        		super.encodeAll( context );
        		this.isRenderedOnClient.setValue( true );
        		this._isRenderedOnClient = true;
        	}
        	else {
        		this.isRenderedOnClient.setValue( false );
        		encodePlaceHolder( (XUIResponseWriter)context.getResponseWriter() );
        	}
    	}
    }
    
    public void encodeUpdate(FacesContext context, List<XUIBaseProperty<?>> updatedProperties) throws IOException {
    	String rendererType = getRendererType();
        if (rendererType != null) {
            Renderer renderer = this.getRenderer(context);
            if (renderer != null) {
            	if (renderer instanceof XUIRenderer){
            	setDestroyOnClient( false );
                ((XUIRenderer)renderer).encodeComponentChanges( this, updatedProperties );
            	}
            } 
        } else 
        	encodeAll(context);
    }
    
    public void encodePlaceHolder( XUIResponseWriter w ) throws IOException {
    	w.startElement( HTMLTag.SPAN, this );
    	w.writeAttribute( HTMLAttr.STYLE, "display:none", "style" );
    	w.writeAttribute( HTMLAttr.ID, getClientId(), "id" );
    	w.endElement( HTMLTag.SPAN );
    }
    
    public String getClientId() {
        return super.getClientId(getFacesContext());
    }

    public ValueExpression createValueExpression( String sValueExpression, Class<?> cType ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createValueExpression( 
                    getELContext(), sValueExpression, cType
                ); 
    }

    private static final Class<?>[] DUMMY_CLASS_ARRAY = new Class[0];
    public MethodExpression createMethodBinding( String sMethodExpression ) {
    	FacesContext context = FacesContext.getCurrentInstance();
        ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( getELContext(), sMethodExpression, null, DUMMY_CLASS_ARRAY );
    }

    public MethodExpression createMethodBinding( String sMethodExpression, Class<?> oReturnValue ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( getELContext(), sMethodExpression, null, DUMMY_CLASS_ARRAY );
    }
    
    public MethodExpression createMethodBinding( String sMethodExpression, Class<?>[] types ) {
        ExpressionFactory oExFactory = getFacesContext().getApplication().getExpressionFactory();
        return oExFactory.createMethodExpression( getELContext(), sMethodExpression, null, types );
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
     
    
    public void applyPropertyDefaultValue(String name,String value) {	    	
    	if (value!=null)
    		XUIPropertySetter.setProperty(this, name, value);
    }
            
    private void applyPropertiesDefaultValues() {
    	Set<Entry<String, XUIBaseProperty<?>>>  props = getStateProperties();		
		
    	for( Entry<String,XUIBaseProperty<?>> s : props ) {
			XUIBaseProperty<?> p = s.getValue();
			
			if (p.isDefaultValue())
				applyPropertyDefaultValue(p.getName(),
						XUIDefaultPropertiesHandler.getPropertyValue(p.getName(), this));
		}    	
    }
    
    public void initComponent() {
    }
    
    public final void processInitComponents() {
        // Process this component itself
    	
    	XUIComponentBase component = this;
    	
    	if( !wasInitComponentProcessed ) {
    		if ( XUIDefaultPropertiesHandler.existPropertiesForComponent(this) )
    			applyPropertiesDefaultValues();
        	
    		XUIComponentPlugIn plugIn = getPlugIn();
    		
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
            else
            	recursiveProcessInitComponents(kid);
        }
    }
    
    /**
     * 
     * Recursively invokes the <code>processInitComponents()</code> method
     * If a component is a {@link UIComponentBase} call the function
     * on its children, if it's a {@link XUIComponentBase} call the 
     * <code>processInitComponents()</code> 
     * 
     * @param oComponent The component to process
     */
    private void recursiveProcessInitComponents(UIComponent oComponent){
    	
    	Iterator<UIComponent> kids = oComponent.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if( kid instanceof XUIComponentBase ) {
                ((XUIComponentBase)kid).processInitComponents();
            }
            else
            	recursiveProcessInitComponents(kid);
        }
    	
    }

    public void preRender() {
        changed = null;
    }
    
    public final void processPreRender() {
        // Process this component itself
    	
    	XUIComponentBase component = this;

    	if( !wasPreRenderProcessed ) {
        	XUIComponentPlugIn plugIn = getPlugIn();
        	
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
            else
            	recursiveProcessPreRender(kid);
        }
    }
    
    /**
     * 
     * Recursively calls the <code>processPreRender()</code> method
     * on all children of a component
     * 
     * @param oComponent The component to process
     */
    private void recursiveProcessPreRender(UIComponent oComponent)
    {
    	Iterator<UIComponent> kids = oComponent.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if( kid instanceof XUIComponentBase ) {
            	
                ((XUIComponentBase)kid).processPreRender();

            }
            else
            	recursiveProcessPreRender(kid);
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
            else
            	recursiveProcessValidateModel(kid);
        }
    }
    
    /**
     * 
     * Recursively calls the <code>processValidateModel()</code> method
     * on all children of a component
     * 
     * @param oComponent The component to process
     */
    private void recursiveProcessValidateModel(UIComponent oComponent)
    {
    	Iterator<UIComponent> kids = oComponent.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof XUIComponentBase ) {
                ((XUIComponentBase)kid).processValidateModel();
            }
            else
            	recursiveProcessValidateModel(kid);
        }
        
    }

    public String getNamingContainerId() {
        return getNamingContainer().getId();
    }


    public XUIRequestContext getRequestContext() {
        return XUIRequestContext.getCurrentContext();
    }

    public ELContext getELContext() {
        return new XUIELContextWrapper( getFacesContext().getELContext() , this );
    }

    public XUISessionContext getSessionContext() {
        return getRequestContext().getSessionContext();
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
	
	
	boolean renderForUpdate = false;
	
	public void markRenderForUpdate(){
		renderForUpdate = true;
	}
	
	public boolean isRenderForUpdate(){
		XUIRenderer renderer = (XUIRenderer ) getRenderer( getFacesContext() );
    	if (renderer != null)
    		return renderer.isRenderForUpdate(this);
    	else
    		return false;
	}
	
	private static void resetRenderOnClientChildren(UIComponent component){
		if (component instanceof XUIComponentBase){
			((XUIComponentBase)component).isRenderedOnClient.setValue( false );
			((XUIComponentBase)component)._isRenderedOnClient = false;
		}
		Iterator<UIComponent> it = component.getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent child = it.next();
			resetRenderOnClientChildren( child );
		}
	} 
	
	public void resetRenderedOnClient(){
		if (wasStateChanged2() == StateChanged.FOR_RENDER) {
			resetRenderOnClientChildren(this);
		}
	}
	
	
	
	public void forceRenderOnClient() {
		this.setDestroyOnClient(true);
		this._isRenderedOnClient = false;
		Iterator<UIComponent> it = getFacetsAndChildren();
		while (it.hasNext()) {
			UIComponent child = it.next();
			if( child instanceof XUIComponentBase ) {
				((XUIComponentBase)child).forceRenderOnClient();
			}
			else
				recursiveForceRenderOnClient(child);
		}
	}
	
	
	
	
	
	/**
	 * 
	 * Recursively calls the <code>forceRenderOnClient()</code> on all
	 * children of a component
	 * 
	 * @param oComponent The component to process
	 */
	private void recursiveForceRenderOnClient(UIComponent oComponent)
	{
		Iterator<UIComponent> children = oComponent.getFacetsAndChildren();
		while (children.hasNext()) 
		{
			UIComponent child = children.next();
			if( child instanceof XUIComponentBase ) {
				((XUIComponentBase)child).forceRenderOnClient();
			}
			else
				recursiveForceRenderOnClient(child);
		}
	}
	
	
	///---------------------------------------
	
	
	/**
	 * The name of the template for this component
	 */
	protected XUIBindProperty<String> template =
			new XUIBindProperty<String>( "template", this, String.class );
	
	/**
	 * Content for the template (specified in a bean)
	 */
	private XUIBindProperty<String> templateContent =
			new XUIBindProperty<String>( "templateContent", this, String.class );
	
	/**
	 * 
	 * Sets the template for the component
	 * 
	 * @param templateNameExpr The template name or expression
	 */
	public void setTemplate(String templateNameExpr){
		this.template.setExpressionText( templateNameExpr );
	}
	
	/**
	 * 
	 * Retrieves the template name
	 * 
	 * @return The name of the template as a String
	 */
	public String getTemplate(){
		return template.getEvaluatedValue();
	}
	
	
	/**
	 * 
	 * Retrieve the content of an inline template
	 * 
	 * @return The template content
	 */
	public String getTemplateContent(){
		return templateContent.getEvaluatedValue();
	}

	public void setTemplateContent(String contentExpr){
		this.templateContent.setExpressionText( contentExpr );
	}
	
	public Renderer getComponentRenderer(){
		return super.getRenderer( getFacesContext() );
	}
	
	protected final void initializeTemplate(String templateName){
		if (template.isDefaultValue()){
			template.setExpressionText( templateName );
		}
	}

	public void resetState() {
		this.wasInitComponentProcessed = false;
		this.isPostBack = false;
		String id = this.getId();
		this.setId( id );
		Iterator<UIComponent> it = getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent child = it.next();
			if (child instanceof XUIComponentBase){
				((XUIComponentBase)child).resetState();
			} else {
				processResetState(child);
			}
		}
		
	}
	
	void processResetState(UIComponent comp){
		Iterator<UIComponent> it = comp.getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent child = it.next();
			if (child instanceof XUIComponentBase){
				((XUIComponentBase)child).resetState();
			} else {
				processResetState(child);
			}
		}
	}
	
	///---------------------------------------
	
}
