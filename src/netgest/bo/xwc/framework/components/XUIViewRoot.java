package netgest.bo.xwc.framework.components;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.Renderer;
import javax.faces.webapp.FacesServlet;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.framework.XUIELContextWrapper;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.bo.xwc.framework.jsf.XUIPhaseEvent;
import netgest.bo.xwc.framework.jsf.XUIStateManagerImpl;
import netgest.utils.StringUtils;

import com.sun.faces.util.LRUMap;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.TypedCollections;
import com.sun.faces.util.Util;

public class XUIViewRoot extends UIViewRoot {
	
	private static AtomicInteger oInstanceIdCntr = new AtomicInteger(0);
	

	private String sInstanceId = null;
	private String sBeanIds = "";
	private Object oViewerBean;
	private String sStateId = null;
	private XUITheme oTheme;

	private String sParentViewState = null;
	private String sTransactionId = null;
	private boolean bOwnsTransaction = false;

	private XUIViewRoot oParentView = null;
	private boolean isPostBack = false;
	
	private String	_renderKit = null;

	private static Lifecycle lifecycle;
	private String[] localizationClasses;

	private boolean wasInitComponentsProcessed = false;

	public void setOwnsTransaction(boolean owns) {
		this.bOwnsTransaction = owns;
	}
	
	public String[] getLocalizationClasses(){
		/*String[] selfIds = localizationClasses;
		List<String> list = new ArrayList< String >( );
		findChildLocalizationClasses( this , list );
		
		if (localizationClasses == null)
			return new String[0];
		for (String id :selfIds){
			list.add(id);
		}*/
		return localizationClasses;
		//return list.toArray(new String[list.size()]);
	}
	
	public void setLocalizationClasses(String[] localizations){
		this.localizationClasses = localizations;
	}
	
	public String getTemplate(){
		return "templates/components/viewroot.ftl";
	}

	public XUIViewRoot() {
			try {
				Class<?> themeClass = Class.forName( getRenderKitClass() );
				oTheme = (XUITheme) themeClass.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(String.format("Class %s for renderKit %s not found",getRenderKitClass(), getRenderKitId()), e);
			} catch (InstantiationException e) {
				throw new RuntimeException(String.format("Class %s for renderKit %s could not be instatiated",getRenderKitClass(), getRenderKitId()), e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
	}
	
	public XUIViewRoot(String instanceId, String viewState) {
		this();
		//System.out.println("InstanceId: " + instanceId +  " ViewState: " + viewState);
		this.sInstanceId = instanceId;
		this.sStateId = viewState;
	}
	

	public Object getBean(String sBeanName) {
		Object bean = new ViewRootBeanFinder().getBean( this , sBeanName );
		if (bean == null){
			if (StringUtils.hasValue( sBeanName ) && sBeanName.equalsIgnoreCase( beanId )){
				bean = beanReference;
			}
		}
		return bean;
	}
	
	public String getBeanUniqueId(String sBeanName) {
		return getBeanPrefix() + sBeanName;
	}
	
	private Object beanReference;
	private String beanId;
	private Map<String,String> beanMapping = new HashMap< String , String >();

	public void addBean(String sBeanName, Object oBean) {
		if( this.sBeanIds != null && this.sBeanIds.length() > 0 ) {
			this.sBeanIds += "|";
		}
		this.sBeanIds += sBeanName;
		this.beanId = sBeanName;
		this.beanReference = oBean;
		this.beanMapping.put( sBeanName , oBean.getClass().getName() );
		XUIRequestContext.getCurrentContext().getSessionContext().setAttribute(
				getBeanPrefix() + sBeanName, oBean);
	}
	
	public String getBeanClass(String beanId){
		return beanMapping.get( beanId );
	}
	
	public String[] getBeanIds() {
		
		if( this.sBeanIds == null )
			return null;
		
		return this.sBeanIds.split("\\|");
		
	}
	
	String[] beanIds = null;
	
	/**
	 * 
	 * Retrieves the bean identifiers from the current view and child views
	 * 
	 * @return
	 */
	public String[] getAllBeanIds(){
		//if (beanIds == null){
			String[] selfIds = getBeanIds( );
			List<String> list = new ArrayList< String >( );
			findChildBeanIds( this , list );
			
			for (String id :selfIds){
				list.add(id);
			}
			
			beanIds = list.toArray(new String[list.size()]);
		//}
		return beanIds;
	}
	
	void findChildBeanIds(UIComponent component, List<String> ids){
		Iterator<UIComponent> children = component.getFacetsAndChildren();
		
		while (children.hasNext()){
			UIComponent comp = children.next();
				if (comp instanceof XUIViewRoot){
					String[] beanIds = ((XUIViewRoot) comp).getAllBeanIds( );
					for (String b : beanIds){
						ids.add( b );
					}
				} 
				findChildBeanIds( comp , ids ); 
		}
	}
	
	void findChildLocalizationClasses(UIComponent component, List<String> localizations){
		Iterator<UIComponent> children = component.getFacetsAndChildren();
		
		while (children.hasNext()){
			UIComponent comp = children.next();
				if (comp instanceof XUIViewRoot){
					String[] beanIds = ((XUIViewRoot) comp).getLocalizationClasses( );
					for (String b : beanIds){
						localizations.add( b );
					}
				} 
				findChildLocalizationClasses( comp , localizations ); 
		}
	}
	
	public Map<String,Object> getViewBeans() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for( String beanId : getBeanIds() ) {
			map.put( beanId , getBean( beanId ) );
		}
		return map;
	}

	public void dispose() {

		XUISessionContext sc = XUIRequestContext.getCurrentContext()
				.getSessionContext();
		String[] sBeans = this.sBeanIds.split("\\|");
		for (String bean : sBeans) {
			sc.removeAttribute(getBeanPrefix() + bean);
		}

		if (this.bOwnsTransaction) {
			XUIRequestContext.getCurrentContext().getSessionContext()
					.getTransactionManager().releaseTransaction(
							this.getTransactionId());
		}
		
		String viewId = this.getViewState();
		if( viewId != null ) {
            XUIStateManagerImpl oStateManagerImpl = (XUIStateManagerImpl)Util.getStateManager( FacesContext.getCurrentInstance() );
            oStateManagerImpl.closeView( viewId );
		}
		
		
		setTransient(true);
	}
	
	/**
	 * Retrieves the Theme class name associated with the current renderkit
	 * (must be an instance of XUITheme)
	 * 
	 * @return The name of the class
	 */
	private String getRenderKitClass(){
		return XUIRequestContext.getCurrentContext().getXUIApplicationConfig().getThemeForRenderKit( getRenderKitId() );
	}
	
	@Override
	public String getRenderKitId() {
		if( _renderKit == null ) {
	    	_renderKit = super.getRenderKitId();
	    	if( _renderKit == null || "HTML_BASIC".equals( _renderKit ) ) {
		    	XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
		    	
		    	//Get renderKit from Parameter, overrides anything
		    	_renderKit = requestContext.getRequestParameterMap().get("__renderKit");
		    	if( _renderKit == null ){
		    		//Get from servlet
		    		_renderKit = (String) requestContext.getAttribute( "__renderKit" );
		    		if (_renderKit == null){
		    			//Get application default
		    			_renderKit = requestContext.getXUIApplicationConfig().getDefaultRenderKitId();
		    		}
		    	}
	    	}
		}
    	return _renderKit; 
    } 
	
	@Override
	public void setRenderKitId(String renderKitId) {
		super.setRenderKitId(renderKitId);
		if( !"HTML_BASIC".equals( renderKitId ) ) {  
			this._renderKit = renderKitId;
		}
	}
 
	public XUITheme getTheme() {
		return oTheme;
	}

	 final String getBeanPrefix() {
		return getViewId() + ":" + sInstanceId + ":";
	}
	 

	@Override
	public Object saveState(FacesContext context) {
		Object oSuperState;
		Object[] oMyState;

		oSuperState = super.saveState(context);
		oMyState = new Object[9];

		oMyState[0] = sInstanceId;
		oMyState[1] = sParentViewState;
		oMyState[2] = sTransactionId;
		oMyState[3] = bOwnsTransaction;
		oMyState[4] = sBeanIds;
		oMyState[5] = sStateId;

		oMyState[6] = oSuperState;
		oMyState[7] = localizationClasses;
		oMyState[8] = beanMapping;

		return oMyState;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] oMyState;

		isPostBack = true;

		oMyState = (Object[]) state;
		sInstanceId = (String) oMyState[0];
		sParentViewState = (String) oMyState[1];
		sTransactionId = (String) oMyState[2];
		bOwnsTransaction = (Boolean) oMyState[3];
		sBeanIds = (String) oMyState[4];
		sStateId = (String) oMyState[5];
		super.restoreState(context, oMyState[6]);
		localizationClasses = (String[]) oMyState[7];
		beanMapping = (Map<String,String>) oMyState[8];
	}

	public boolean wasStateChanged() {

		// If not a post back to this component, assume state changed
		// to force render of the component
		if (!isPostBack()) {
			return true;
		}
		return false;
	}

	public boolean wasInitComponentsProcessed() {
		if (isPostBack()) {
			return true;
		}
		return this.wasInitComponentsProcessed;
	}

	public void processStateChanged(List<XUIComponentBase> oRenderList) {
		boolean bChanged;
		UIComponent oKid;

		Iterator<UIComponent> it = this.getFacetsAndChildren();
		while (it.hasNext()){
			bChanged = false;
			oKid = it.next();
			if (oKid instanceof XUIComponentBase) {
				StateChanged change = ((XUIComponentBase) oKid).wasStateChanged2();
				if (change == StateChanged.FOR_RENDER || change == StateChanged.FOR_UPDATE) {
					oRenderList.add((XUIComponentBase) oKid);
					bChanged = true;
				}
				if (!bChanged) {
					((XUIComponentBase) oKid).processStateChanged(oRenderList);
				}

			}
		}
	}

	public void syncClientView() {
		Iterator<UIComponent> it = getFacetsAndChildren();
		Form oForm;
		UIComponent component = null;
		while (it.hasNext()){
			component = it.next();
			oForm = (Form) ((XUIComponentBase) component)
					.findComponent(Form.class);
			if (oForm != null) {
				XUIRequestContext.getCurrentContext().getScriptContext().add(
						XUIScriptContext.POSITION_HEADER,
						oForm.getClientId() + "_syncView",
						"XVW.syncView('" + oForm.getClientId() + "');");
				break;
			}
		}
	}
	
	public XUIComponentBase findComponent(String clientId) {
		
		UIComponent found = null;

		Iterator<UIComponent> it = getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent component = it.next();
			if (clientId.equalsIgnoreCase( component.getClientId( getFacesContext() ) )){
				return (XUIComponentBase) component;
			}
			found = findComponent( component , clientId );
		}
		return (XUIComponentBase) found ;
	}
	
	public XUIComponentBase findComponent(UIComponent mcomponent, String clientId) {
		Iterator<UIComponent> list;
		XUIComponentBase oComp;

		oComp = null;
		UIComponent found = null;

		list = mcomponent.getFacetsAndChildren();
		while (list.hasNext()){
			UIComponent component = list.next();
			if (component instanceof XUIComponentBase)
			{
				found = ((XUIComponentBase) component).findComponent(clientId);
				if (found != null) {
					return (XUIComponentBase) found;
				}
			}
			else
			{
				Iterator<UIComponent> listChildren = component.getFacetsAndChildren();
				while (listChildren.hasNext())
				{
					UIComponent childCmp = listChildren.next();
					found  = childCmp.findComponent( clientId );
					if (found != null && found instanceof XUIComponentBase)
						return ((XUIComponentBase) found);
				}
			}
			
		}
		return oComp;
	}

	public XUIComponentBase findComponent(Class<?> cType) {
		Iterator<UIComponent> list;
		XUIComponentBase oComp;

		oComp = null;

		list = getFacetsAndChildren();
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
				while (listChildren.hasNext())
				{
					UIComponent childCmp = listChildren.next();
					findComponent(childCmp, cType);
				}
			}
			
		}
		return oComp;
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

	public void setParentViewState(String sParentViewId) {
		this.oParentView = null;
		this.sParentViewState = sParentViewId;
	}

	public void getParentViewState(String sParentViewId) {
		this.sParentViewState = sParentViewId;
	}

	public void setParentView(XUIViewRoot oViewRoot) {
		setParentViewState(oViewRoot.getViewState());
	}
	
	

	public XUIViewRoot getParentView() {
		if (sParentViewState != null && oParentView == null) {
			oParentView = XUIRequestContext.getCurrentContext()
					.getSessionContext().getView(sParentViewState);
				oParentView.sStateId = sParentViewState;
			}
		return oParentView;
	}

	public Object getViewerBean() {
		return oViewerBean;
	}

	@Override
	public String getFamily() {
		return XUIViewRoot.class.getName();
	}

	@Override
	public String getRendererType() {
		return XUIViewRoot.class.getName();
	}

	public void processInitComponents() {
		// Process all facets and children of this component
		Iterator<UIComponent> kids = getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent kid = (UIComponent) kids.next();
			if (kid instanceof UIComponent) {
				((XUIComponentBase) kid).processInitComponents();
			}
		}
		wasInitComponentsProcessed = true;

	}

	public void processValidateModel() {
		// Process all facets and children of this component
		Iterator<UIComponent> kids = getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent kid = (UIComponent) kids.next();
			if (kid instanceof XUIComponentBase) {
				((XUIComponentBase) kid).processValidateModel();
			}
		}
	}

	public void processPreRender() {
		// Process all facets and children of this component
		Iterator<UIComponent> kids = getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent kid = (UIComponent) kids.next();
			if (kid instanceof UIComponent) {
				((XUIComponentBase) kid).processPreRender();
			}
		}
	}

	public String getInstanceId() {
		return this.sInstanceId;
	}
	
	public void setInstanceId(String instanceId){
		this.sInstanceId = instanceId;
	}

	public String getClientId() {
		return getViewId() + ":" + sInstanceId;
	}

	public boolean isPostBack() {
		return isPostBack;
	}
	
	public void setViewState(String newState){
		sStateId = newState;
	}

	public String getViewState() {

		if (sStateId == null) {

			FacesContext context = FacesContext.getCurrentInstance();
			XUIStateManagerImpl stateManager = (XUIStateManagerImpl) Util
					.getStateManager(context);

			ExternalContext externalContext = context.getExternalContext();
			Object sessionObj = externalContext.getSession(true);
			Map<String, Object> sessionMap = externalContext.getSessionMap();

			synchronized (sessionObj) {
				Map<String, Map> logicalMap = TypedCollections
						.dynamicallyCastMap((Map) sessionMap
								.get(XUIStateManagerImpl.LOGICAL_VIEW_MAP),
								String.class, Map.class);

				int logicalMapSize = stateManager.getNumberOfViewsParameter();

				if (logicalMap == null) {
					logicalMap = new LRUMap<String, Map>(logicalMapSize);
					sessionMap.put(XUIStateManagerImpl.LOGICAL_VIEW_MAP,
							logicalMap);
				}

				String idInLogicalMap = (String) RequestStateManager.get(
						context, RequestStateManager.LOGICAL_VIEW_MAP);

				if (idInLogicalMap == null) {
					idInLogicalMap = stateManager
							.createUniqueRequestId(context);
				}
				assert (null != idInLogicalMap);

				String idInActualMap = stateManager
						.createUniqueRequestId(context);
				int actualMapSize = stateManager
						.getNumberOfViewsInLogicalViewParameter();

				Map<String, Object[]> actualMap = (Map<String, Object[]>) TypedCollections
						.dynamicallyCastMap(logicalMap.get(idInLogicalMap),
								String.class, Object[].class);
				if (actualMap == null) {
					actualMap = new LRUMap<String, Object[]>(actualMapSize);
					logicalMap.put(idInLogicalMap, actualMap);
				}

				this.sStateId = idInLogicalMap + NamingContainer.SEPARATOR_CHAR
						+ idInActualMap;
			}
		}
		return this.sStateId;
	}

	public void setTransactionId(String sTransactionId) {
		this.sTransactionId = sTransactionId;
	}

	public String getTransactionId() {
		return sTransactionId;
	}

	@Override
	public String createUniqueId() {
		return "v" + sInstanceId + "_" + super.createUniqueId();
	}

	public static final boolean renderHead() {
		XUIRequestContext oRequestContext = XUIRequestContext
				.getCurrentContext();
		return !(oRequestContext.isAjaxRequest()
				|| oRequestContext.isIncludeRequest() || oRequestContext
				.isPortletRequest());
	}

	public static final boolean renderHead(XUIViewRoot root ) {
		return renderHead() && root.getParent() == null;
	}
	
	public static final boolean renderScripts() {
		XUIRequestContext oRequestContext = XUIRequestContext
				.getCurrentContext();
		return !(oRequestContext.isAjaxRequest());
	}
	
	public static final boolean renderScripts(XUIViewRoot root) {
		return renderScripts() && root.getParent() == null;
	}

	public static class XEOHTMLRenderer extends XUIRenderer {

		@Deprecated
		public void encodeBegin(FacesContext context, UIComponent component)
				throws IOException {
			
			XUIResponseWriter w = getResponseWriter();
			XUITheme theme = getTheme();
			XUIViewRoot viewRoot = (XUIViewRoot) component;

			if (renderHead(viewRoot) ) {

				// Add Scripts and Style
				XUIResponseWriter headerW = getResponseWriter()
						.getHeaderWriter();

				// Write Header

				headerW.write(theme.getDocType());
				headerW.writeText('\n');
				
				headerW.startElement("html", component);
				if( getTheme() != null )
					headerW.writeAttribute("style", getTheme().getHtmlStyle());

				headerW.writeText('\n');
				headerW.startElement("head", component);
				
				theme.writeHeader( headerW );
				
				renderBaseUrl( component , headerW );
				
				// Write Body
				w.startElement("body", component);
				if (getTheme() != null && getTheme().getBodyStyle() != null) {
					w.writeAttribute( "style", getTheme().getBodyStyle() );
				}
				headerW.writeText('\n');
			}


			if (renderScripts(viewRoot) && getTheme() != null ) {
				getTheme().addStyle(w.getStyleContext());
				getTheme().addScripts(w.getScriptContext());
			}
			
			theme.writePostBodyContent( getRequestContext() , w, viewRoot );

		}

		protected void renderBaseUrl(UIComponent component,
				XUIResponseWriter headerW) throws IOException {
			headerW.startElement("base", component);
			HttpServletRequest req = (HttpServletRequest) getRequestContext()
					.getRequest();
			String link = (req.isSecure() ? "https" : "http")
					+ "://"
					+ req.getServerName()
					+ (req.getServerPort() == 80 ? "" : ":"
							+ req.getServerPort())
					+ getRequestContext().getResourceUrl("");
			headerW.writeAttribute("href", link, "href");
			headerW.endElement( "base" );
		}

		@Deprecated
		public void encodeEnd(FacesContext context, UIComponent component)
				throws IOException {
			XUIRequestContext oRequestContext;
			XUIViewRoot viewRoot = (XUIViewRoot) component;
			oRequestContext = XUIRequestContext.getCurrentContext();
			XUITheme theme = getTheme();
			XUIResponseWriter w = getResponseWriter();
			
			XUIResponseWriter footerW = getResponseWriter().getFooterWriter();
			XUIResponseWriter headerW = getResponseWriter().getHeaderWriter();

			if (renderScripts(viewRoot)) {

				w.getStyleContext().render(headerW, w, footerW);
				w.getScriptContext().render(headerW, w, footerW);
				oRequestContext.getStyleContext().render(headerW, w, footerW);
				oRequestContext.getScriptContext().render(headerW, w, footerW);
			}
			
			
			theme.writePreFooterContent( oRequestContext , w , viewRoot );
			
			if (renderHead(viewRoot) ) {
				// Write footer Elements
				if ( getTheme() != null && getTheme().getHtmlStyle() != null) {
					w.writeAttribute("style", getTheme().getHtmlStyle(),
							"style");
				}

				// Write Head Elements
				headerW.writeText('\n');
				headerW.endElement("head");
				headerW.writeText('\n');
				

				// End tag body
				w.writeText('\n');
				w.endElement("body");

				// End Tag HTML
				footerW.writeText('\n');
				footerW.endElement("html");
			}
		}

		@Override
		public boolean getRendersChildren() {
			return true;
		}

	}
	
	private boolean skipPhase;
	
	public void encodeBegin(FacesContext context) throws IOException {
		skipPhase = false;
		// avoid creating the PhaseEvent if possible by doing redundant
		// null checks.
		if (null != getBeforePhaseListener() || null != getFacesListeners( FacesListener.class )) {
			notifyPhaseListeners(context, PhaseId.RENDER_RESPONSE, true);
		}
 
		if (!skipPhase) {
	        if (context == null) {
	            throw new NullPointerException();
	        }
	        if (!isRendered()) {
	            return;
	        }
	        String rendererType = getRendererType();
	        if (rendererType != null) {
	            Renderer renderer = this.getRenderer(context);
	            if (renderer != null) {
	                renderer.encodeBegin(context, this);
	            }
	        }
		}
	}

	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);

		// avoid creating the PhaseEvent if possible by doing redundant
		// null checks.
		if (null != getAfterPhaseListener() || null != getFacesListeners( FacesListener.class )) {
			notifyPhaseListeners(context, PhaseId.RENDER_RESPONSE, false);
		}

	}
	
	private static Field phaseListenersField = null;
	
	public void notifyPhaseListeners(FacesContext context, PhaseId phaseId,
			boolean isBefore) {
		
		PhaseEvent event = createPhaseEvent(context, phaseId);

		boolean hasPhaseMethodExpression = (isBefore && (null != getBeforePhaseListener()))
				|| (!isBefore && (null != getAfterPhaseListener()));
		MethodExpression expression = isBefore ? getBeforePhaseListener() : getAfterPhaseListener();
		
		XUIRequestContext.getCurrentContext().setPhaseEvent( new XUIPhaseEvent(this, event ) );
		try {

			if (hasPhaseMethodExpression) {
				try {
					expression.invoke(getELContext(),
							new Object[] { event });
					skipPhase = context.getResponseComplete()
							|| context.getRenderResponse();
				} catch (Exception e) {
					// PENDING(edburns): log this
				}
			}
			
			List<PhaseListener> phaseListeners;
			
			try {
				if( phaseListenersField == null ) {
					phaseListenersField = UIViewRoot.class.getDeclaredField("phaseListeners");
					phaseListenersField.setAccessible( true );
				}
				phaseListeners = (List<PhaseListener>)phaseListenersField.get( this );
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			
			if (null != phaseListeners) {
				
				for (PhaseListener curFacesListener : phaseListeners ) {
					PhaseListener curListener = (PhaseListener)curFacesListener;
					if (phaseId == curListener.getPhaseId()
							|| PhaseId.ANY_PHASE == curListener.getPhaseId()) {
						try {
							if (isBefore) {
								curListener.beforePhase(event);
							} else {
								curListener.afterPhase(event);
							}
							skipPhase = context.getResponseComplete();
						} catch (Exception e) {
							// PENDING(edburns): log this
						}
					}
				}
			}
		}
		finally {
			XUIRequestContext.getCurrentContext()
				.setPhaseEvent(null);
		}
	}
	
	private static PhaseEvent createPhaseEvent(FacesContext context,
			PhaseId phaseId) throws FacesException {

		if (lifecycle == null) {
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			String lifecycleId = context.getExternalContext().getInitParameter(
					FacesServlet.LIFECYCLE_ID_ATTR);
			if (lifecycleId == null) {
				lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
			}
			lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
		}

		return (new PhaseEvent(context, phaseId, lifecycle));

	}

	public ELContext getELContext() {
        return new XUIELContextWrapper( getFacesContext().getELContext() , this );
    }

    
    @Override
    public void processApplication(FacesContext context) {
    	
    	processApplicationOnChildViews(context, this.getFacetsAndChildren() );
    	
    	super.processApplication(context);
    }
	
    private void processApplicationOnChildViews( FacesContext context, Iterator<UIComponent> components ) {
		for( ; components.hasNext(); ) {
			
			UIComponent child = components.next();
			
	    	if( child instanceof UIViewRoot ) {
	    		((UIViewRoot)child).processApplication( context );
	    	}
	    	else {
    			processApplicationOnChildViews( context, child.getFacetsAndChildren() );
	    	}
    	}
    }
    
	@Override
	public String toString() {
		return getViewId() + " " + getViewState() + " " + getBeanIds();
	}
	
	public Renderer getRenderer(){
		return super.getRenderer( getFacesContext() );
	}

	public void resetState() {
		isPostBack = false;
		resetStateOnComponents();
		
	}
	
	void resetStateOnComponents(){
		Iterator<UIComponent> it = getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent comp = it.next();
			if (comp instanceof XUIComponentBase){
				((XUIComponentBase)comp).resetState();
			} else {
				processResetStateOnComponents( comp );
			}
		}
	}
	
	void processResetStateOnComponents(UIComponent comp){
		Iterator<UIComponent> it = getFacetsAndChildren();
		while (it.hasNext()){
			UIComponent child = it.next();
			if (child instanceof XUIComponentBase){
				((XUIComponentBase)child).resetState();
			} else {
				processResetStateOnComponents(child);
			}
		}
	}
	
	
    
}
