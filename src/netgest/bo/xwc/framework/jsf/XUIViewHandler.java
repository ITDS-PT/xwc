package netgest.bo.xwc.framework.jsf;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.boApplication;
import netgest.bo.transaction.XTransaction;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;
import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIApplicationContext;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBean;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.http.XUIAjaxRequestWrapper;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.xeo.beans.XEOSecurityBaseBean;
import netgest.utils.StringUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.faces.RIConstants;
import com.sun.faces.application.ViewHandlerResponseWrapper;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.io.FastStringWriter;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;


/**
 * <B>ViewHandlerImpl</B> is the default implementation class for ViewHandler.
 *
 * @version $Id: ViewHandlerImpl.java,v 1.45.12.2.2.1 2006/04/12 19:32:04 ofung Exp $
 * @see javax.faces.application.ViewHandler
 */
public class XUIViewHandler extends XUIViewHandlerImpl {

    //
    // Private/Protected Constants
    //
    private static final Log log = LogFactory.getLog(netgest.bo.xwc.framework.jsf.XUIViewHandler.class);
    
	// 1. Instantiate a TransformerFactory.
	 javax.xml.transform.TransformerFactory tFactory = 
	                   javax.xml.transform.TransformerFactory.newInstance();

	 javax.xml.transform.Transformer transformer;


    //
    // Relationship Instance Variables
    //

    public XUIViewHandler() {
        if (log.isDebugEnabled()) {
            log.debug(MessageLocalizer.getMessage("CREATE_VIEWHANDLER_INSTANCE"));
        }
    }


    public void renderView(FacesContext context,
                           UIViewRoot viewToRender) throws IOException,
        FacesException {
    	
    	long ini = System.currentTimeMillis();
    	
    	XUIRequestContext.getCurrentContext()
    		.setRenderedViewer( viewToRender );
    		
        ExternalContext extContext = context.getExternalContext();

        ServletRequest request = (ServletRequest) extContext.getRequest();
        ServletResponse response = (ServletResponse) extContext.getResponse();

        // set up the ResponseWriter

        // TODO: Inicializar uma unica vez por sessão.

        RenderKitFactory renderFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        
        RenderKit renderKit =
                renderFactory.getRenderKit(context, viewToRender.getRenderKitId());

        ResponseWriter oldWriter = context.getResponseWriter();

        if (bufSize == -1) {
            WebConfiguration webConfig = 
                  WebConfiguration
                        .getInstance(context.getExternalContext());
            try {
                bufSize = Integer
                      .parseInt(webConfig.getOptionValue(
                            WebContextInitParameter.ResponseBufferSize));
            } catch (NumberFormatException nfe) {
                bufSize = Integer
                      .parseInt(WebContextInitParameter.ResponseBufferSize.getDefaultValue());
            }
        }

        
        
        // Check if this is a direct connect to a component
        String[] oCompId = (String[])request.getParameterMap().get("xvw.servlet");
        if( oCompId != null )
        {
            renderServlet( context, request, response, renderKit, viewToRender, oCompId[0] );
        }
        else if( request instanceof XUIAjaxRequestWrapper ) {
            renderAjax( context, request, response, renderKit, viewToRender );
        }
        else
        {
            if( "XEOXML".equals( viewToRender.getRenderKitId() ) ) {
            	renderToBuffer( context, request, response, renderKit, viewToRender );
            }
            else {
            	renderNormal( context, request, response, renderKit, viewToRender );
            }
        }

        if (null != oldWriter) {
            context.setResponseWriter(oldWriter);
        }

        // write any AFTER_VIEW_CONTENT to the response
        // side effect: AFTER_VIEW_CONTENT removed
        ViewHandlerResponseWrapper wrapper = (ViewHandlerResponseWrapper)
              RequestStateManager.remove(context, RequestStateManager.AFTER_VIEW_CONTENT);
        if (null != wrapper) {
            wrapper.flushToWriter(response.getWriter(),
                    response.getCharacterEncoding());
        }

        response.flushBuffer();
    }
    
    public void renderToBuffer( FacesContext context, ServletRequest request, ServletResponse response, RenderKit renderKit, UIViewRoot viewToRender ) throws IOException {
		if( !viewToRender.isRendered() ) {
			return;
		}
		
		response.setContentType("text/html;charset=utf-8");
		CharArrayWriter w = new CharArrayWriter();
		
	    XUIWriteBehindStateWriter headWriter =
	          new XUIWriteBehindStateWriter(w,
	                                     context,
	                                     bufSize);
	
	    XUIWriteBehindStateWriter bodyWriter =
	          new XUIWriteBehindStateWriter(w,
	                                     context,
	                                     bufSize);
	
	    XUIWriteBehindStateWriter footerWriter =
	          new XUIWriteBehindStateWriter(w,
	                                     context, 
	                                     bufSize);
	
	    ResponseWriter newWriter;
	    newWriter = renderKit.createResponseWriter(bodyWriter,
	                                                   null,
	                                                   request.getCharacterEncoding());
	
	    context.setResponseWriter(newWriter);
	    
	    // Sets the header and footer writer
	    if( newWriter instanceof XUIResponseWriter )
	    	PackageIAcessor.setHeaderAndFooterToWriter( (XUIResponseWriter)newWriter, headWriter, footerWriter );
	
	    newWriter.startDocument();
	    viewToRender.encodeAll( context );
	    newWriter.endDocument();
	
	    // Write header part of document
	    headWriter.flushToWriter( false );
	    headWriter.release();
	    
	    bodyWriter.flushToWriter(false);
	    // clear the ThreadLocal reference.
	    bodyWriter.release();
	    
	    // Write footer (Before tag </body> ) part.
	    footerWriter.flushToWriter( false );
	    footerWriter.release();
	    
	    String temp = w.toString();
	    
	    XMLDocument doc = ngtXMLUtils.loadXML( temp );
	    String xmlContent =ngtXMLUtils.getXML(doc);
	    
	     
	    
	    
	    
		final String		HTML_TEMPLATES = "html_templates.xsl";
    	final String		PROJECT_HTML_TEMPLATES = "projectHtmlTemplates.xsl";

		
    	if( "true".equals( request.getAttribute("xsltransform") ) ) {
			//Merge the Two transforms 
			InputStream finalTransformer = 
				Thread.currentThread().getContextClassLoader().getResourceAsStream( PROJECT_HTML_TEMPLATES ); 
			
			if( finalTransformer == null ) {
				finalTransformer = 
					Thread.currentThread().getContextClassLoader().getResourceAsStream( HTML_TEMPLATES );
			}
			
			// JAXP reads data using the Source interface
			//System.out.println(xmlContent);
			
		    Source xmlSource = new StreamSource(new StringReader(xmlContent));
		    Source xsltSource = new StreamSource(finalTransformer);
		    
		    CharArrayWriter out = new CharArrayWriter();
		    try
		    {
		    	TransformerFactory transFact =
		                TransformerFactory.newInstance();
		        Transformer trans = transFact.newTransformer(xsltSource);
		        trans.transform(xmlSource, new StreamResult(out) );
		        xmlContent = out.toString();
		    }
		    catch (Exception e) 
		    {
		    	throw new RuntimeException("XEOEditBean - XSLT Transformation Error", e);
		    }
		    finally {
			    response.getWriter().write( out.toString() );
		    }
    	}
    	else {
		    response.getWriter().write( xmlContent );
    	}
    }
    
    
    public UIViewRoot restoreView(FacesContext context, String viewId ) {
        return restoreView( context, viewId, null );
    }
    public UIViewRoot restoreView(FacesContext context, String viewId, Object savedId ) {
        ExternalContext extContext = context.getExternalContext();

        
        if( viewId.startsWith("/") ) {
        	viewId = viewId.substring(1);
        }
        
        
        Map<String,String> headerMap = (Map<String,String>)extContext.getRequestHeaderMap();
        String
            contentType = null,
            charEnc = null;

        // look for a charset in the Content-Type header first.
        if (null != (contentType = (String) headerMap.get("Content-Type"))) {
            // see if this header had a charset
            String charsetStr = "charset=";
            int
                len = charsetStr.length(),
                i = 0;

            // if we have a charset in this Content-Type header AND it
            // has a non-zero length.
            if (-1 != (i = contentType.indexOf(charsetStr)) &&
                (i + len < contentType.length())) {
                charEnc = contentType.substring(i + len);
            }
        }
        // failing that, look in the session for a previously saved one
        if (null == charEnc) {
            if (null != extContext.getSession(false)) {
                charEnc = (String) extContext.getSessionMap().get
                    (CHARACTER_ENCODING_KEY);
            }
        }
        if (null != charEnc) {
            try {
                Object request = extContext.getRequest();
                if (request instanceof ServletRequest) {
                    ((ServletRequest) request).setCharacterEncoding(charEnc);
                }
            } catch (java.io.UnsupportedEncodingException uee) {
                if (log.isErrorEnabled()) {
                    log.error(uee.getMessage(), uee);
                }
                throw new FacesException(uee);
            }
        }

        XUIViewRoot viewRoot = null;

        
        // this is necessary to allow decorated impls.
        ViewHandler outerViewHandler =
            context.getApplication().getViewHandler();
        String renderKitId =
            outerViewHandler.calculateRenderKitId(context);
        
        XUIStateManagerImpl oStateManagerImpl = (XUIStateManagerImpl)Util.getStateManager(context);
        viewRoot = (XUIViewRoot)oStateManagerImpl.restoreView(context, viewId, renderKitId, savedId );
        
        if( viewRoot != null )
        {
            // Actualiza as beans
	        if ( viewRoot.getBean("viewBean") instanceof XUIViewBean ) {
	        	if( savedId != null )
	        		((XUIViewBean)viewRoot.getBean("viewBean")).setViewRoot( (String)savedId );
	        	else
	        		((XUIViewBean)viewRoot.getBean("viewBean")).setViewRoot( viewRoot.getViewState() );
	        }
	        
	        // Se existe view restora a transac磯 associada a view.
            String sTransactionId = viewRoot.getTransactionId();
            
            if( sTransactionId != null ) {
                XTransaction oTransaction = XUIRequestContext.getCurrentContext().getTransactionManager().getTransaction( sTransactionId );
                if( oTransaction != null ) {
                	oTransaction.activate();
                }
            }
    		
            UIViewRoot savedView = context.getViewRoot();
    		context.setViewRoot( viewRoot );

    		viewRoot.notifyPhaseListeners(context, PhaseId.RESTORE_VIEW, false );
            
            if( viewRoot == context.getViewRoot() && savedView != null )
            	context.setViewRoot( savedView );
            
            
        }
        return viewRoot;
    }

    public UIViewRoot createView(FacesContext context, String viewId ) 
    {
        return createView( context, viewId, null, null );
    }
    

    public UIViewRoot createView(FacesContext context, String viewId,  String sTransactionId ) 
    {
    	return createView( context, viewId, null, sTransactionId );
    }
    
    public UIViewRoot createView(FacesContext context, String viewId, InputStream viewerInputStream, String sTransactionId ) 
    {
        XUIViewerBuilder oViewerBuilder;
        XUIRequestContext      oContext;
        XUIApplicationContext  oApp;

        Locale locale = null;
        String renderKitId = null;
        
        oContext = XUIRequestContext.getCurrentContext();
        oApp      = oContext.getApplicationContext();
        oViewerBuilder = oApp.getViewerBuilder();
        
        // Arranja o viewId caso este comece por /
        if( viewId.startsWith("/") ) {
        	viewId = viewId.substring(1);
        }
        
        // use the locale from the previous view if is was one which will be
        // the case if this is called from NavigationHandler. There wouldn't be 
        // one for the initial case.

        if (context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
            renderKitId = context.getViewRoot().getRenderKitId();
        }

        //String sOldViewId = viewId;
        //viewId = context.getExternalContext().getRequestServletPath() + viewId;
        
        XUIViewRoot result = new XUIViewRoot();
        UIViewRoot previousViewRoot = context.getViewRoot();
        try {
        	// Work around to allows expression evaluation during the viewer creation.
        	// Property viewBean is mapped to the FacesContext viewRoot.
        	context.setViewRoot( result );
        
	        result.setViewId( viewId );
	        
	        XTransaction oTransaction;
	        
	        if( sTransactionId == null )
	        {
	            oTransaction = XUIRequestContext.
	                getCurrentContext().
	                    getSessionContext().
	                    getTransactionManager().
	                        createTransaction();
	            result.setOwnsTransaction( true );
	        }
	        else {
	            oTransaction = XUIRequestContext.
	                getCurrentContext().
	                    getSessionContext().
	                    getTransactionManager().getTransaction( sTransactionId );
	            result.setOwnsTransaction( false );
	        }
	        
	        if( oTransaction != null ) {
		        result.setTransactionId( oTransaction.getId() );
		        oTransaction.activate();
	        }
	        
	        if( context.getViewRoot() != null ) {
	            String pValue = XUIRequestContext.getCurrentContext().getRequestParameterMap().
	                  get(ResponseStateManager.VIEW_STATE_PARAM);
	            result.setParentViewState( pValue );
	        }
	        
	        if (log.isDebugEnabled()) 
	        {
	            log.debug(MessageLocalizer.getMessage("CREATE_NEW_VIEW_FOR")+" " + viewId);
	        }
	        
	        // PENDING(): not sure if we should set the RenderKitId here.
	        // The UIViewRoot ctor sets the renderKitId to the default
	        // one.
	        // if there was no locale from the previous view, calculate the locale 
	        // for this view.
	        if (locale == null) 
	        {
	            locale =
	                context.getApplication().getViewHandler().calculateLocale(
	                    context);
	            if (log.isDebugEnabled()) {
	                log.debug(MessageLocalizer.getMessage("LOCALE_FOR_THIS_VIEW_AS_DETERMINED_BY_CALCULATELOCALE")+" "
	                          + locale.toString());
	            }
	        } 
	        else 
	        {
	            if (log.isDebugEnabled()) 
	            {
	                log.debug(
	                    MessageLocalizer.getMessage("USING_LOCALE_FROM_PREVIOUS_VIEW")+" " + locale.toString());
	            }
	        }
	
	        if (renderKitId == null) 
	        {
	            renderKitId =
	                context.getApplication().getViewHandler().calculateRenderKitId(
	                    context);
	            if (log.isDebugEnabled()) {
	                log.debug(MessageLocalizer.getMessage("RENDERKITID_FOR_THIS_VIEW_AS_DETERMINATED_BY_CALCULTERENDERKIT")+" "
	                          + renderKitId);
	            }
	        } 
	        else
	        {
	            if (log.isDebugEnabled()) 
	            {
	                log.debug(MessageLocalizer.getMessage("USING_RENDERKITID_FROM_PREVIOUS_VIEW")+
	                    " " + renderKitId);
	            }
	        }
	
	        result.setLocale(locale);
	        result.setRenderKitId(renderKitId);
	        
	        // Load the Viewer definition a build component tree
	        
	        XUIViewerDefinition oViewerDef;
	        
	        if( viewerInputStream != null )
	        	oViewerDef = oApp.getViewerDef( viewerInputStream );
	        else
	        	oViewerDef = oApp.getViewerDef( viewId );
	        
	        result.setTransient( oViewerDef.isTransient() );
	        
	        initializeAndAssociateBeansToView(oViewerDef, result, viewId);
	        
	        //ReplaceDynamyicIncludes
	        
	        //Método próprio na bean que depois o que faz é acrescentar ao request o nome do viewer
	        //que se quer incluir.
	        
	        
	        // Create a new instance of the view bean
	        if (log.isDebugEnabled()) 
	        {
	            log.debug(
	                MessageLocalizer.getMessage("START_BUILDING_COMPONENT_VIEW")+" " + viewId );
	        }
	        
	        oViewerBuilder.buildView( oContext, oViewerDef, result );
	
	        if (log.isDebugEnabled()) 
	        {
	            log.debug(MessageLocalizer.getMessage("END_BUILDING_COMPONENT_VIEW")+
	                  " " + viewId );
	        }
	
	        // Initialize security
	        initializeSecurity(result, oViewerDef, context);
//	        
        }
        finally {
        	if (previousViewRoot != null)
        		context.setViewRoot( previousViewRoot );
        }

        return result;

        
    }
    
    private void initializeAndAssociateBeansToView( XUIViewerDefinition oViewerDef, XUIViewRoot result, String viewId  ){
    	
    	List<String> beanList = oViewerDef.getViewerBeans();
        List<String> beanIdList = oViewerDef.getViewerBeanIds();
        
        // Initialize View Bean.
        if( log.isDebugEnabled() ) {
            log.debug(MessageLocalizer.getMessage("INITIALIZING_BEANS_FOR_VIEW")+" " + viewId );    
        }
        
        int k = 0;
        for (String beanClassName : beanList){
        	
            if( !StringUtils.isEmpty(beanClassName) ) {
            	
            	try {
            		String beanId = beanIdList.get(k);
            		
		            Object oViewBean = instantiateBean(beanClassName);
		            
		            associateBeanToView(oViewBean, result, beanId);
		            setBeanId( oViewBean, beanId );
		            associateViewToBean(oViewBean, result);
	            
            	} 
                catch ( Exception ex ) {
                    throw new FacesException( XUICoreMessages.VIEWER_CLASS_NOT_FOUND.toString( beanClassName, viewId ) );
                }
            }
            k++;
        }
    }
    
    private Object instantiateBean (String beanClassName) throws ClassNotFoundException, 
    	InstantiationException, IllegalAccessException{
    	Class<?> oBeanClass = 
        	Thread.currentThread().getContextClassLoader().loadClass( beanClassName );
    	return oBeanClass.newInstance();
    	
    }
    
    private void setBeanId(Object oViewBean, String beanId){
    	if (oViewBean instanceof XEOBaseBean){
            XEOBaseBean bean = (XEOBaseBean) oViewBean;
            bean.setId( beanId );
        }
    }
    
    private void associateBeanToView(Object bean, XUIViewRoot root, String beanId){
    	root.addBean( beanId, bean );
    }
    
    private void associateViewToBean(Object bean, XUIViewRoot root){
    	if( bean instanceof XUIViewBean ) {
        	((XUIViewBean)bean).setViewRoot( root.getViewState() );
        }
    }
    
    private void initializeSecurity(XUIViewRoot result, XUIViewerDefinition definition, FacesContext context){
    	try {
        	
    		List<String> beanIds = definition.getViewerBeanIds();
    		
    		for (String beanId: beanIds){
    		
	    		Object bean = result.getBean( beanId );
	        	
	    		// Only activates viewerSecurity if the XVWAccessPolicy object is deployed.
	    		if ( ViewerAccessPolicyBuilder.applyViewerSecurity ) {
	    			
	    			ViewerAccessPolicyBuilder.applyViewerSecurity = boDefHandler.getBoDefinition( "XVWAccessPolicy" ) != null;
		        	if ( bean!=null && bean instanceof XEOSecurityBaseBean ) {
		        		ViewerAccessPolicyBuilder viewerAccessPolicyBuilder = new ViewerAccessPolicyBuilder();
		        		viewerAccessPolicyBuilder.processViewer( result, boApplication.currentContext().getEboContext(), false );
		        		((XEOSecurityBaseBean)bean).initializeSecurityMap(viewerAccessPolicyBuilder, result.getViewId() );
		        	}
	    		}
    		}	
	    		
    		UIViewRoot savedView = context.getViewRoot();
    		context.setViewRoot( result );
            result.notifyPhaseListeners(context, PhaseId.RESTORE_VIEW, true );
            
            if( result == context.getViewRoot() && savedView != null  )
            	context.setViewRoot( savedView );
            
    		
    		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    public void processInitComponent( XUIViewRoot oViewRoot ) {

        // Process all facets and children of this component
        Iterator<UIComponent> kids = oViewRoot.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processInitComponents();
            }
        }
    }
    
    public String getAjaxActionURL(FacesContext context, String viewId) 
    {
        // Acquire the context path, which we will prefix on all results
        String contextPath =
            context.getExternalContext().getRequestContextPath();

        // Acquire the mapping used to execute this request (if any)
        String mapping = Util.getFacesMapping(context);

        viewId = context.getViewRoot().getViewId();

        // If no mapping can be identified, just return a server-relative path
        if (mapping == null) {
            return contextPath + viewId;
        }

        // Deal with prefix mapping
        if( !contextPath.endsWith("/") && !viewId.startsWith("/") ) {
            contextPath += "/";
        }
        
        return contextPath + viewId;

    }

    
    public String getActionURL(FacesContext context, String viewId) 
    {
    	String sActionUrl;
    	
    	sActionUrl = (String)((ServletRequest)context.getExternalContext().getRequest()).getAttribute("xvw.actionUrl");
    	if( sActionUrl != null ) {
    		return sActionUrl;
    	}
    	sActionUrl = (String)((ServletRequest)context.getExternalContext().getRequest()).getParameter("xvw.actionUrl");
    	if( sActionUrl != null ) {
    		return sActionUrl;
    	}

    	// Acquire the context path, which we will prefix on all results
        String contextPath =
            context.getExternalContext().getRequestContextPath();

        // Acquire the mapping used to execute this request (if any)
        String mapping = Util.getFacesMapping(context);

        if( viewId == null )
        	viewId = context.getViewRoot().getViewId();

        // If no mapping can be identified, just return a server-relative path
        if (mapping == null) {
            return contextPath + viewId;
        }

        // Deal with prefix mapping
        if( !contextPath.endsWith("/") && !viewId.startsWith("/") ) {
            contextPath += "/";
        }
        
        return contextPath + viewId;

    }

    public void renderNormal( FacesContext context, ServletRequest request, ServletResponse response, RenderKit renderKit, UIViewRoot viewToRender ) throws IOException {

    	if( !viewToRender.isRendered() ) {
    		return;
    	}
    	
		response.setContentType("text/html;charset=utf-8");
    	Writer w = response.getWriter();
    	
        XUIWriteBehindStateWriter headWriter =
              new XUIWriteBehindStateWriter(w,
                                         context,
                                         bufSize);

        XUIWriteBehindStateWriter bodyWriter =
              new XUIWriteBehindStateWriter(w,
                                         context,
                                         bufSize);

        XUIWriteBehindStateWriter footerWriter =
              new XUIWriteBehindStateWriter(response.getWriter(),
                                         context, 
                                         bufSize);

        ResponseWriter newWriter;

        //if (null != oldWriter) {
        //    newWriter = oldWriter.cloneWithWriter(stateWriter);
        //} else {

        newWriter = renderKit.createResponseWriter(bodyWriter,
                                                       null,
                                                       request.getCharacterEncoding());
        //}



        context.setResponseWriter(newWriter);
        
        // Sets the header and footer writer
        
        if( newWriter instanceof XUIResponseWriter )
        	PackageIAcessor.setHeaderAndFooterToWriter( (XUIResponseWriter)newWriter, headWriter, footerWriter );

        //newWriter.setHeaderAndFooterWriters( headWriter, footerWriter );

        newWriter.startDocument();
        
        

        //RequestDispatcher disp_ini = request.getRequestDispatcher("/ui/classic/common_ini.inc");
        //disp_ini.include( request,response );
        
        viewToRender.encodeAll( context );
        
        newWriter.endDocument();

        // flush directly to the response

        // Write header part of document
        headWriter.flushToWriter( false );
        headWriter.release();

        // XEO implementation, the is allways writen in cache.
        //if (bodyWriter.stateWritten()) {
            // replace markers in the body content and write it to response.
        bodyWriter.flushToWriter();
        //}

        // clear the ThreadLocal reference.
        bodyWriter.release();
        
        // Write footer (Before tag </body> ) part.
        footerWriter.flushToWriter( false );
        footerWriter.release();

        
    }

    public void renderServlet( FacesContext context, 
                               ServletRequest request, 
                               ServletResponse response, 
                               RenderKit renderKit, 
                               UIViewRoot viewToRender, 
                               String oCompId ) throws IOException {
        

        String pValue = context.getExternalContext().getRequestParameterMap().
        get(ResponseStateManager.VIEW_STATE_PARAM);
		if (pValue != null && pValue.length() == 0) {
		   pValue = null;
		}
		
		if( pValue != null ) {
			try {
				Field f = ((XUIViewRoot)viewToRender).getClass().getDeclaredField("sStateId");
				f.setAccessible(true);
				f.set(  viewToRender, pValue );
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
    	
    	XUIComponentBase oComp = (XUIComponentBase)viewToRender.findComponent( oCompId );

        String rendererType = oComp.getRendererType();
        if (rendererType != null) {
            Renderer renderer = null;
            if (rendererType != null) {
                renderer = context.getRenderKit().getRenderer(oComp.getFamily(),
                                                            rendererType);
            }
            if (renderer != null) {
                ((XUIRendererServlet)renderer).service( request, response, oComp );
            } else {
                // TODO: i18n
                log.warn(MessageLocalizer.getMessage("CANT_GET_RENDERER_FOR_TYPE")+" " + rendererType + " "+MessageLocalizer.getMessage("COMPONENT_SERVLET_REQUEST_ABORTED"));
            }
        }
        context.responseComplete();
        
        XUIWriteBehindStateWriter responseWriter = 
            new XUIWriteBehindStateWriter(new FastStringWriter(),
                                     context,
                                     bufSize);

        XUIResponseWriter newWriter = (XUIResponseWriter)renderKit.createResponseWriter(responseWriter,
                                                   null,
                                                   request.getCharacterEncoding());
        
        context.setResponseWriter( newWriter );
        responseWriter.flushToWriter( true );
        responseWriter.release();

    
    }

    public void renderAjax ( FacesContext context, ServletRequest request, ServletResponse response, RenderKit renderKit, UIViewRoot viewToRender ) throws IOException {
        XUIWriteBehindStateWriter responseWriter;
        Document oAjaxXmlResp;

		response.setContentType("text/xml;charset=utf-8");
        
        responseWriter = 
                new XUIWriteBehindStateWriter(response.getWriter(),
                                         context,
                                         bufSize);

        XUIResponseWriter newWriter = (XUIResponseWriter)renderKit.createResponseWriter(responseWriter,
                                                       null,
                                                       request.getCharacterEncoding());

        context.setResponseWriter( newWriter );
        
        ((XUIViewRoot)viewToRender).notifyPhaseListeners(context, PhaseId.RENDER_RESPONSE, true);
        
    	oAjaxXmlResp = getAjaxXML( context, request, renderKit, (XUIViewRoot)viewToRender );
        ((XMLDocument)oAjaxXmlResp).setEncoding("utf-8");
        String sXml = ngtXMLUtils.getXML( (XMLDocument)oAjaxXmlResp );
        responseWriter.write( sXml );
        responseWriter.flushToWriter();
        responseWriter.release();

        ((XUIViewRoot)viewToRender).notifyPhaseListeners(context, PhaseId.RENDER_RESPONSE, false);
        
    }

    
    public Document getAjaxXML( FacesContext context, ServletRequest request, RenderKit renderKit, XUIViewRoot oViewToRender ) throws IOException {
        XUIResponseWriter newWriter;
        
        newWriter = null;

        // Scan components that need to be rendered
        // Phase 1... determinde what element should be renderered;
        ArrayList<XUIComponentBase> oToRenderList = new ArrayList<XUIComponentBase>();
        
        if( oViewToRender.isPostBack() && oViewToRender.isRendered() ) {
	        String[] oRenderCompId = (String[])request.getParameterMap().get("xvw.render");
	        if( oRenderCompId != null && oRenderCompId.length > 0 ) {
	        	for( int i=0; i <oRenderCompId.length; i++ ) {
	        		
	        		XUIComponentBase comp = (XUIComponentBase)oViewToRender.findComponent( oRenderCompId[i] );
	        		if( comp != null ) {
	        			oToRenderList.add( comp );
	        		}
	        	}
	        }
	        else
	        {
	            processStateWasChanged( oToRenderList, oViewToRender );
	        }
        }

        // Prepare Writers
        Writer oHeadWriter = new FastStringWriter(bufSize/8);
        Writer oFooterWriter = new FastStringWriter(bufSize/8);

        XUIWriteBehindStateWriter headWriter =
              new XUIWriteBehindStateWriter(oHeadWriter,
                                         context,
                                         bufSize/8);

        XUIWriteBehindStateWriter footerWriter =
              new XUIWriteBehindStateWriter(oFooterWriter,
                                         context,
                                         bufSize/8);
        
        Document oAjaxXmlResp;
        
        Element  oXvwAjaxResp;
        Element  oRenderElement;
        Element  oCompElement;
        
        // Guarda o Script context, para que o mesmo scriptcontext seja usado 
        // no render de todos os elementos que marcados para render.

        XUIScriptContext oSavedScriptContext = null;
        
         
        oAjaxXmlResp = new XMLDocument();
        
        oXvwAjaxResp = oAjaxXmlResp.createElement("xvwAjaxResp");
        oAjaxXmlResp.appendChild( oXvwAjaxResp );
        
        oXvwAjaxResp.setAttribute("viewId", oViewToRender.getClientId() );
        oXvwAjaxResp.setAttribute("isPostBack", String.valueOf( ((XUIViewRoot)oViewToRender).isPostBack() ) );
        
        oRenderElement = oAjaxXmlResp.createElement("render");
        oXvwAjaxResp.appendChild( oRenderElement );
        
        ((HttpServletRequest)request).setAttribute( "__xwcAjaxDomDoc" , oAjaxXmlResp );
        ((HttpServletRequest)request).setAttribute( "__xwcRenderElement" , oRenderElement );
        ((HttpServletRequest)request).setAttribute( "__xwcRenderKit" , renderKit );
        
        
        if( oViewToRender.isPostBack() )
        {
        	for( UIComponent comp : oToRenderList ) {
        		if( comp instanceof XUIComponentBase ) {
        			((XUIComponentBase)comp).resetRenderedOnClient();
        		}
        	}
        	
                
            Writer oComponentWriter = new FastStringWriter(bufSize/4);
    
            // Setup new writer for each component to render
            XUIWriteBehindStateWriter oCompBodyWriter =
                  new XUIWriteBehindStateWriter(    oComponentWriter,
                                                    context,
                                                    bufSize/4
                                                );
                
            newWriter = (XUIResponseWriter)renderKit.createResponseWriter(oCompBodyWriter,
                                                           null,
                                                           request.getCharacterEncoding());
                
            // Força o ScriptContext guardado da Writer anterior.
            if( oSavedScriptContext != null ) {
                //newWriter.setScriptContext( oSavedScriptContext );
                PackageIAcessor.setScriptContextToWriter( newWriter, oSavedScriptContext );
            }
            
            context.setResponseWriter(newWriter);
            
            // Sets the header and footer wr
            PackageIAcessor.setHeaderAndFooterToWriter( newWriter, headWriter, footerWriter );
            //newWriter.setHeaderAndFooterWriters( headWriter, footerWriter );

            newWriter.startDocument();
            oSavedScriptContext = newWriter.getScriptContext();
        	newWriter.getStyleContext();
    
            // Phase 2... callRenders for the Objects
            for (int i = 0; i < oToRenderList.size(); i++) {
                XUIComponentBase oComp = oToRenderList.get( i );
                oComp.encodeAll();
            }
            newWriter.endDocument();
            
            oCompBodyWriter.flushToWriter( false );
            oCompBodyWriter.release();
            
            //TODO: Martelada por causa dos Layouts... este código não devia estar a este nivél.
            if( newWriter != null && request.getAttribute("__skip.Layouts.doLayout") == null ) {
            	Layouts.doLayout( newWriter );
            }
            
        }
        else {

            Writer oComponentWriter = new FastStringWriter(bufSize/4);
            // Setup new writer for each component to render
            XUIWriteBehindStateWriter oCompBodyWriter =
                  new XUIWriteBehindStateWriter(    oComponentWriter,
                                                    context,
                                                    bufSize/4
                                                );
            
            newWriter = (XUIResponseWriter)renderKit.createResponseWriter(oCompBodyWriter,
                                                           null,
                                                           request.getCharacterEncoding());
            
            // Força o ScriptContext guardado da Writer anterior.
            if( oSavedScriptContext != null ) {
                //newWriter.setScriptContext( oSavedScriptContext );
                PackageIAcessor.setScriptContextToWriter( newWriter, oSavedScriptContext );
            }
            
            context.setResponseWriter(newWriter);
            
            // Sets the header and footer wr
            PackageIAcessor.setHeaderAndFooterToWriter( newWriter, headWriter, footerWriter );
            //newWriter.setHeaderAndFooterWriters( headWriter, footerWriter );
            
            newWriter.startDocument();
            
            request.setAttribute( "__xwcAjaxTagOpened", Boolean.TRUE );
            oViewToRender.encodeAll( context );
                        
            newWriter.endDocument();
            
            oCompBodyWriter.flushToWriter( false );
            oCompBodyWriter.release();
            
            String sResult = oComponentWriter.toString();
            //System.out.println(sResult);
            if( "XEOXML".equals( oViewToRender.getRenderKitId() ) ) {
            	
            	long init = System.currentTimeMillis();
	             try {
	
					 // 2. Use the TransformerFactory to process the stylesheet Source and
	//                 generate a Transformer.
	            	 InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream( "html_templates.xsl" );
	            	 
	            	 if( transformer == null ) {
	            		 transformer = tFactory.newTransformer
	            	     	(new javax.xml.transform.stream.StreamSource(
	            	     			in));	            		 
	            	 }
	
					 // 3. Use the Transformer to transform an XML Source and send the
	//                 output to a Result object.
					 CharArrayWriter out = new CharArrayWriter();
					 
					 transformer.transform
					     (new javax.xml.transform.stream.StreamSource( new CharArrayReader( 
					    		 sResult.toCharArray() ) ), 
					      new javax.xml.transform.stream.StreamResult( out ));
					 
					 sResult = out.toString();
					 
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}                
				
				log.debug(( MessageLocalizer.getMessage("XSL_TIME") + (System.currentTimeMillis()-init) ));
				
            }
            
        	if( !oViewToRender.getViewId().equals("netgest/bo/xwc/components/viewers/Dummy.xvw") ) {
	            oCompElement = oAjaxXmlResp.createElement( "component" );
	            oCompElement.setAttribute("id", oViewToRender.getClientId() );
	            oCompElement.appendChild( oAjaxXmlResp.createCDATASection( sResult ) );
	            oRenderElement.appendChild( oCompElement );
        	}
            
            // Verifica se foi criada uma Writer, se sim guarda o script context
            // para passar à nova que é criada especificamente para 
            // este componente.
            if( newWriter != null ) {
                oSavedScriptContext = newWriter.getScriptContext();
            }
        }

        //TODO: Dá erro se não existir elementos para se efectuar o render do lado do cliente
        if( oToRenderList.size() > 0 || !oViewToRender.isPostBack() )
        {
            
            // Write header part of document
            headWriter.flushToWriter( false );
            headWriter.release();
        
            // Write footer (Before tag </body> ) part.
            footerWriter.flushToWriter( false );
            footerWriter.release();
            
            if( oSavedScriptContext != null ) {
                oSavedScriptContext.renderForAjaxDom( oXvwAjaxResp );
            }
            
        }
        
        XUIRequestContext.getCurrentContext().getScriptContext().renderForAjaxDom( oXvwAjaxResp );
        
        
        // Create state field Marker;
        Element oStateNode = oAjaxXmlResp.createElement("viewState");
        oStateNode.appendChild( oAjaxXmlResp.createTextNode( RIConstants.SAVESTATE_FIELD_MARKER ) );
        oXvwAjaxResp.appendChild( oStateNode );
        
        //TODO: Rever encoding
        ((XMLDocument)oAjaxXmlResp).setEncoding( "windows-1252" );
        ((XMLDocument)oAjaxXmlResp).setVersion( "1.0" );
        return oAjaxXmlResp;
        
    }

    public void processStateWasChanged( ArrayList<XUIComponentBase> oRenderList, XUIViewRoot oRootView ) {
        UIComponent oKid;
        
        List<UIComponent> oKids = oRootView.getChildren();
        for (int i = 0; i < oKids.size(); i++) {
            oKid = oKids.get( i );
            if( oKid instanceof XUIComponentBase ) {
                ((XUIComponentBase)oKid).processStateChanged( oRenderList );
            }
        }
    }

}

