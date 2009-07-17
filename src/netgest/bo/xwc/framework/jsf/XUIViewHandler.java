package netgest.bo.xwc.framework.jsf;

import com.sun.faces.RIConstants;
import com.sun.faces.application.ViewHandlerResponseWrapper;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.io.FastStringWriter;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;

import java.io.IOException;
import java.io.InputStream;

import java.io.Writer;

import java.lang.reflect.Field;
import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.faces.render.Renderer;

import javax.faces.render.ResponseStateManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.def.boDefHandler;
import netgest.bo.system.boApplication;
import netgest.bo.transaction.XTransaction;
import netgest.bo.xwc.components.beans.XEOSecurityBaseBean;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;
import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIApplicationContext;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBean;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.http.XUIAjaxRequestWrapper;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import netgest.bo.xwc.framework.components.XUIViewRoot;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


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
    private static final Logger logger = FacesLogger.APPLICATION.getLogger();
    

    /**
     * <p>The <code>request</code> scoped attribute to store the
     * {@link javax.faces.webapp.FacesServlet} path of the original
     * request.</p>
     */
    private static final String INVOCATION_PATH =
        RIConstants.FACES_PREFIX + "INVOCATION_PATH";        

    //
    // Relationship Instance Variables
    //

    public XUIViewHandler() {
        if (log.isDebugEnabled()) {
            log.debug("Created ViewHandler instance ");
        }
    }


    public void renderView(FacesContext context,
                           UIViewRoot viewToRender) throws IOException,
        FacesException {

        ExternalContext extContext = context.getExternalContext();

        ServletRequest request = (ServletRequest) extContext.getRequest();
        ServletResponse response = (ServletResponse) extContext.getResponse();

        // set up the ResponseWriter

        // TODO: Inicializar uma unica vez por sess√£o.

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
            renderNormal( context, request, response, renderKit, viewToRender );
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
    
    public UIViewRoot restoreView(FacesContext context, String viewId ) {
        return restoreView( context, viewId, null );
    }
    public UIViewRoot restoreView(FacesContext context, String viewId, Object savedId ) {
        ExternalContext extContext = context.getExternalContext();

        long init = System.currentTimeMillis();
        
        // set the request character encoding. NOTE! This MUST be done
        // before any request praameter is accessed.

        /*
        HttpServletRequest request =
            (HttpServletRequest) extContext.getRequest();
        */
        
        if( viewId.startsWith("/") ) {
        	viewId = viewId.substring(1);
        }
        
        
        Map headerMap = extContext.getRequestHeaderMap();
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

        String mapping = Util.getFacesMapping(context);
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
            String pValue = context.getExternalContext().getRequestParameterMap().
            	get(ResponseStateManager.VIEW_STATE_PARAM);

            // Actualiza as beans
	        if ( viewRoot.getBean("viewBean") instanceof XUIViewBean ) {
	        	if( savedId != null )
	        		((XUIViewBean)viewRoot.getBean("viewBean")).setViewRoot( (String)savedId );
	        	else
	        		((XUIViewBean)viewRoot.getBean("viewBean")).setViewRoot( pValue );
	        }
	        
	        // Se existe view restora a transacÁ„o associada a view.
            String sTransactionId = viewRoot.getTransactionId();
            
            if( sTransactionId != null ) {
                XTransaction oTransaction = XUIRequestContext.getCurrentContext().getTransactionManager().getTransaction( sTransactionId );
                oTransaction.activate();
            }
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
            log.debug("Created new view for " + viewId);
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
                log.debug("Locale for this view as determined by calculateLocale "
                          + locale.toString());
            }
        } 
        else 
        {
            if (log.isDebugEnabled()) 
            {
                log.debug(
                    "Using locale from previous view " + locale.toString());
            }
        }

        if (renderKitId == null) 
        {
            renderKitId =
                context.getApplication().getViewHandler().calculateRenderKitId(
                    context);
            if (log.isDebugEnabled()) {
                log.debug("RenderKitId for this view as determined by calculateRenderKitId "
                          + renderKitId);
            }
        } 
        else
        {
            if (log.isDebugEnabled()) 
            {
                log.debug(
                    "Using renderKitId from previous view " + renderKitId);
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
        
        // Set Bean to the viewer
        String sBeanName      = oViewerDef.getViewerBeanId();
        String sBeanClassName = oViewerDef.getViewerBean();
        try {
            // Initialize View Bean.
            if( log.isDebugEnabled() ) {
                log.debug("Initializing beans for view " + viewId );    
            }
            
            if( sBeanClassName != null && sBeanClassName.length() > 0 ) {
	            Class   oBeanClass = Class.forName( sBeanClassName );
	            
	            Object oViewBean = oBeanClass.newInstance();
	
	            result.addBean( sBeanName, oViewBean );
	            
	            if( oViewBean instanceof XUIViewBean ) {
	            	((XUIViewBean)oViewBean).setViewRoot( result.getViewState() );
	            }
            }
        } 
        catch ( Exception ex ) {
            throw new FacesException( XUICoreMessages.VIEWER_CLASS_NOT_FOUND.toString( sBeanClassName, viewId ) );
        }
        
        // Create a new instance of the view bean
        if (log.isDebugEnabled()) 
        {
            log.debug(
                "Start building component view " + viewId );
        }
        oViewerBuilder.buildView( oContext, oViewerDef, result );
        if (log.isDebugEnabled()) 
        {
            log.debug(
                  "End building component view " + viewId );
        }

        // Initialize security
        try {
        	Object bean = result.getBean( "viewBean" );
    		// Only activates viewerSecurity if the XVWAccessPolicy object is deployed.
    		if ( ViewerAccessPolicyBuilder.applyViewerSecurity ) {
    			ViewerAccessPolicyBuilder.applyViewerSecurity = boDefHandler.getBoDefinition( "XVWAccessPolicy" ) != null;
    		}
    		if( ViewerAccessPolicyBuilder.applyViewerSecurity ) {
	        	if ( bean!=null && bean instanceof XEOSecurityBaseBean ) {
	        		ViewerAccessPolicyBuilder viewerAccessPolicyBuilder = new ViewerAccessPolicyBuilder();
	        		viewerAccessPolicyBuilder.processViewer( result, boApplication.currentContext().getEboContext(), false );
	        		((XEOSecurityBaseBean)bean).initializeSecurityMap(viewerAccessPolicyBuilder, result.getViewId() );
	        	}
    		}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

        return result;

        
    }
    
    public void processInitComponent( XUIViewRoot oViewRoot ) {

        // Process all facets and children of this component
        Iterator kids = oViewRoot.getFacetsAndChildren();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
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
                log.warn("Can't get Renderer for type " + rendererType + " Component Servlet request aborted! ");
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

        responseWriter = 
                new XUIWriteBehindStateWriter(response.getWriter(),
                                         context,
                                         bufSize);

        XUIResponseWriter newWriter = (XUIResponseWriter)renderKit.createResponseWriter(responseWriter,
                                                       null,
                                                       request.getCharacterEncoding());

        context.setResponseWriter( newWriter );

        oAjaxXmlResp = getAjaxXML( context, request, renderKit, (XUIViewRoot)viewToRender );
        
        response.setContentType("text/xml");
        
        String sXml = ngtXMLUtils.getXML( (XMLDocument)oAjaxXmlResp );
        
        responseWriter.write(
                    sXml
                );
        
        responseWriter.flushToWriter();
        responseWriter.release();
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
        
        if( oViewToRender.isPostBack() )
        {
        
            // Phase 2... callRenders for the Objects
            for (int i = 0; i < oToRenderList.size(); i++) {
                
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
                
                // For√ßa o ScriptContext guardado da Writer anterior.
                if( oSavedScriptContext != null ) {
                    //newWriter.setScriptContext( oSavedScriptContext );
                    PackageIAcessor.setScriptContextToWriter( newWriter, oSavedScriptContext );
                }
                
                context.setResponseWriter(newWriter);
                
                // Sets the header and footer wr
                PackageIAcessor.setHeaderAndFooterToWriter( newWriter, headWriter, footerWriter );
                //newWriter.setHeaderAndFooterWriters( headWriter, footerWriter );
    
                newWriter.startDocument();
    
                UIComponent oComp = oToRenderList.get( i );
                
                oComp.encodeAll( context );
                            
                newWriter.endDocument();
    
                oCompBodyWriter.flushToWriter( false );
                oCompBodyWriter.release();
    
                oCompElement = oAjaxXmlResp.createElement( "component" );
                oCompElement.setAttribute("id", oComp.getClientId( context ) );
                oCompElement.appendChild( oAjaxXmlResp.createCDATASection( oComponentWriter.toString() ) );
                oRenderElement.appendChild( oCompElement );
                
                // Verifica se foi criada uma Writer, se sim guarda o script context
                // para passar √† nova que √© criada especificamente para 
                // este componente.
                
                if( newWriter != null ) {
                    oSavedScriptContext = newWriter.getScriptContext();
                }
    
            } 
            
            //TODO: Martelada por causa dos Layouts... este cÛdigo n„o devia estar a este nivÈl.
            if( newWriter != null ) {
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
            
            // For√ßa o ScriptContext guardado da Writer anterior.
            if( oSavedScriptContext != null ) {
                //newWriter.setScriptContext( oSavedScriptContext );
                PackageIAcessor.setScriptContextToWriter( newWriter, oSavedScriptContext );
            }
            
            context.setResponseWriter(newWriter);
            
            // Sets the header and footer wr
            PackageIAcessor.setHeaderAndFooterToWriter( newWriter, headWriter, footerWriter );
            //newWriter.setHeaderAndFooterWriters( headWriter, footerWriter );
            
            newWriter.startDocument();
            
            oViewToRender.encodeAll( context );
                        
            newWriter.endDocument();
            
            oCompBodyWriter.flushToWriter( false );
            oCompBodyWriter.release();
            
            oCompElement = oAjaxXmlResp.createElement( "component" );
            oCompElement.setAttribute("id", oViewToRender.getClientId() );
            oCompElement.appendChild( oAjaxXmlResp.createCDATASection( oComponentWriter.toString() ) );
            oRenderElement.appendChild( oCompElement );
            
            // Verifica se foi criada uma Writer, se sim guarda o script context
            // para passar √† nova que √© criada especificamente para 
            // este componente.
            
            if( newWriter != null ) {
                oSavedScriptContext = newWriter.getScriptContext();
            }
        }

        //TODO: D√° erro se n√£o existir elementos para se efectuar o render do lado do cliente
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
        boolean     bChanged = false;
        
        List<UIComponent> oKids = oRootView.getChildren();
        for (int i = 0; i < oKids.size(); i++) {
            bChanged = false;
            oKid = oKids.get( i );
            if( oKid instanceof XUIComponentBase ) {
                ((XUIComponentBase)oKid).processStateChanged( oRenderList );
            }
        }
    }

}

