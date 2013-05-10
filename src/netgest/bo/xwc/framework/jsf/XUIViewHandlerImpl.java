package netgest.bo.xwc.framework.jsf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIRequestContext;

import com.sun.faces.RIConstants;
import com.sun.faces.application.ViewHandlerResponseWrapper;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;

/**
 * <B>XUIViewHandlerImpl</B> is the default implementation class for ViewHandler.
 *
 * @version $Id: XUIViewHandlerImpl.java,v 1.109.4.4 2008/01/10 21:34:08 rlubke Exp $
 * @see javax.faces.application.ViewHandler
 */
public class XUIViewHandlerImpl extends ViewHandler {

    // Log instance for this class
    private static final Logger logger = Logger.getLogger( XUIViewHandlerImpl.class );

    private XUIApplicationAssociate associate;

    /**
     * <p>Store the value of <code>DEFAULT_SUFFIX_PARAM_NAME</code>
     * or, if that isn't defined, the value of <code>DEFAULT_SUFFIX</code>
     */
    private String contextDefaultSuffix;
    protected int bufSize = -1;

    public XUIViewHandlerImpl() {
        if (logger.isFinerEnabled()) {
            logger.finer("CREATED_VIEWEHANDLER_INSTANCE");
        }
    }
    

    /**
     * Do not call the default implementation of {@link ViewHandler#initView(javax.faces.context.FacesContext)}
     * if the {@link javax.faces.context.ExternalContext#getRequestCharacterEncoding()} returns a
     * <code>non-null</code> result.
     *
     * @see ViewHandler#initView(javax.faces.context.FacesContext)
     */
    @Override
    public void initView(FacesContext context) throws FacesException {

        if (context.getExternalContext().getRequestCharacterEncoding() == null) {
            super.initView(context);
        }
        
    }


    public void renderView(FacesContext context,
            UIViewRoot viewToRender) throws IOException,
            FacesException {

        // suppress rendering if "rendered" property on the component is
        // false
        if (!viewToRender.isRendered()) {
            return;
        }

        ExternalContext extContext = context.getExternalContext();
        ServletRequest request = (ServletRequest) extContext.getRequest();
        ServletResponse response = (ServletResponse) extContext.getResponse();

        try {
            if (executePageToBuildView(context, viewToRender)) {
                response.flushBuffer();
                XUIApplicationAssociate associate = getAssociate(context);
                if (associate != null) {
                    associate.responseRendered();
                }
                return;
            }
        } catch (IOException e) {
            throw new FacesException(e);
        }

        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("COMPLETED_BUILDING_VIEW_FOR")+" : \n" +
                    viewToRender.getViewId());
            
        }

        // set up the ResponseWriter

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


        XUIWriteBehindStateWriter stateWriter =
              new XUIWriteBehindStateWriter(response.getWriter(),
                                         context,
                                         bufSize);
        ResponseWriter newWriter;
        if (null != oldWriter) {
            newWriter = oldWriter.cloneWithWriter(stateWriter);
        } else {
            newWriter = renderKit.createResponseWriter(stateWriter,
                                                       null,
                                                       request.getCharacterEncoding());
        }
        context.setResponseWriter(newWriter);

        newWriter.startDocument();

        doRenderView(context, viewToRender);

        newWriter.endDocument();

        // replace markers in the body content and write it to response.

        // flush directly to the response
        if (stateWriter.stateWritten()) {
            stateWriter.flushToWriter();
        }

        // clear the ThreadLocal reference.
        stateWriter.release();

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
        
        closeModifiedViews( context );

    }


	private void closeModifiedViews( FacesContext context ) {
		XUIRequestContext req = XUIRequestContext.getCurrentContext();
        if (req != null){
        	req.close( context );
        }
	}

    /**
     * <p>This is a separate method to account for handling the content
     * after the view tag.</p>
     *
     * <p>Create a new ResponseWriter around this response's Writer.
     * Set it into the FacesContext, saving the old one aside.</p>
     *
     * <p>call encodeBegin(), encodeChildren(), encodeEnd() on the
     * argument <code>UIViewRoot</code>.</p>
     *
     * <p>Restore the old ResponseWriter into the FacesContext.</p>
     *
     * <p>Write out the after view content to the response's writer.</p>
     *
     * <p>Flush the response buffer, and remove the after view content
     * from the request scope.</p>
     *
     * @param context the <code>FacesContext</code> for the current request
     * @param viewToRender the view to render
     * @throws IOException if an error occurs rendering the view to the client
     * @throws FacesException if some error occurs within the framework
     *  processing
     */

    private void doRenderView(FacesContext context,
                              UIViewRoot viewToRender)
    throws IOException, FacesException {

        XUIApplicationAssociate associate = getAssociate(context);

        if (null != associate) {
            associate.responseRendered();
        }

        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("ABOUT_TO_RENDER_VIEW")+" " + viewToRender.getViewId());
        }

        viewToRender.encodeAll(context);
    }


    public UIViewRoot restoreView(FacesContext context, String viewId) {
        if (context == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }

        ExternalContext extContext = context.getExternalContext();

        String mapping = Util.getFacesMapping(context);
        UIViewRoot viewRoot = null;

        if (mapping != null) {
            if (!Util.isPrefixMapped(mapping)) {
                viewId = convertViewId(context, viewId);
            } else {
                viewId = normalizeRequestURI(viewId, mapping);
            }
        }

        // maping could be null if a non-faces request triggered
        // this response.
        if (extContext.getRequestPathInfo() == null && mapping != null &&
            Util.isPrefixMapped(mapping)) {
            // this was probably an initial request
            // send them off to the root of the web application
            try {
                context.responseComplete();
                if (logger.isFinerEnabled()) {
                    logger.finer(LoggerMessageLocalizer.getMessage("RESPONSE_COMPLETE_FOR") + viewId);
                }
                extContext.redirect(extContext.getRequestContextPath());
            } catch (IOException ioe) {
                throw new FacesException(ioe);
            }
        } else {
            // this is necessary to allow decorated impls.
            ViewHandler outerViewHandler =
                    context.getApplication().getViewHandler();
            String renderKitId =
                    outerViewHandler.calculateRenderKitId(context);
            viewRoot = Util.getStateManager(context).restoreView(context,
                                                                 viewId,
                                                                 renderKitId);
        }
        
        if( viewRoot != null ) {
            // Mark the request with postBack;
            PackageIAcessor.setResponseContextIsPostBack( XUIRequestContext.getCurrentContext(), true );
        }

        return viewRoot;
    }


    public UIViewRoot createView(FacesContext context, String viewId) {
        if (context == null) {
            String message = MessageUtils.getExceptionMessageString
                    (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }

        UIViewRoot result = (UIViewRoot)
                context.getApplication().createComponent(UIViewRoot.COMPONENT_TYPE);

        if (viewId != null) {
            String mapping = Util.getFacesMapping(context);

            if (mapping != null) {
                if (!Util.isPrefixMapped(mapping)) {
                   viewId = convertViewId(context, viewId);
                } else {
                    viewId = normalizeRequestURI(viewId, mapping);
                    if (viewId.equals(mapping)) {
                        // The request was to the FacesServlet only - no
                        // path info
                        // on some containers this causes a recursion in the
                        // RequestDispatcher and the request appears to hang.
                        // If this is detected, return status 404
                        send404Error(context);
                    }
                }
            }

            result.setViewId(viewId);
        }

        Locale locale = null;
        String renderKitId = null;

        // use the locale from the previous view if is was one which will be
        // the case if this is called from NavigationHandler. There wouldn't be
        // one for the initial case.
        if (context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
            renderKitId = context.getViewRoot().getRenderKitId();
        }

        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("CREATED_NEW_VIEW_FOR")+" " + viewId);
        }
        // PENDING(): not sure if we should set the RenderKitId here.
        // The UIViewRoot ctor sets the renderKitId to the default
        // one.
        // if there was no locale from the previous view, calculate the locale
        // for this view.
        if (locale == null) {
            locale =
                context.getApplication().getViewHandler().calculateLocale(
                    context);
            if (logger.isFinerEnabled()) {
                logger.finer(LoggerMessageLocalizer.getMessage("LOCALE_FOR_THIS_VIEW_AS_DETERMINATED_BY_CALCULATELOCALE")
                            + locale.toString());
            }
        } else {
            if (logger.isFinerEnabled()) {
                logger.finer(LoggerMessageLocalizer.getMessage("USING_LOCALE_FROM_PREVIOUS_VIEW")+" "
                            + locale.toString());
            }
        }

        if (renderKitId == null) {
            renderKitId =
                context.getApplication().getViewHandler().calculateRenderKitId(
                    context);
           if (logger.isFinerEnabled()) {
               logger.finer(
               MessageLocalizer.getMessage("RENDERKITID_FOR_THIS_VIEW_AS_DETERMINATED_BY_CALCULTERENDERKIT")+" "
               + renderKitId);
            }
        } else {
            if (logger.isFinerEnabled()) {
                logger.finer(LoggerMessageLocalizer.getMessage("USING_RENDERKITID_FROM_PREVIOUS_VIEW")+" "
                            + renderKitId);
            }
        }

        result.setLocale(locale);
        result.setRenderKitId(renderKitId);

        return result;
    }

    /**
     * Execute the target view.  If the HTTP status code range is
     * not 2xx, then return true to indicate the response should be
     * immediately flushed by the caller so that conditions such as 404
     * are properly handled.
     * @param context the <code>FacesContext</code> for the current request
     * @param viewToExecute the view to build
     * @return <code>true</code> if the response should be immediately flushed
     *  to the client, otherwise <code>false</code>
     * @throws IOException if an error occurs executing the page
     */
    protected boolean executePageToBuildView(FacesContext context,
                                        UIViewRoot viewToExecute)
    throws IOException {

        if (null == context) {
            String message = MessageUtils.getExceptionMessageString
                    (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }
        if (null == viewToExecute) {
            String message = MessageUtils.getExceptionMessageString
                    (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "viewToExecute");
            throw new NullPointerException(message);
        }

        ExternalContext extContext = context.getExternalContext();

        if ("/*".equals(RequestStateManager.get(context, RequestStateManager.INVOCATION_PATH))) {
            throw new FacesException(MessageUtils.getExceptionMessageString(
                  MessageUtils.FACES_SERVLET_MAPPING_INCORRECT_ID));
        }

        String requestURI = viewToExecute.getViewId();

        if (logger.isFinerEnabled()) {
            logger.finer( LoggerMessageLocalizer.getMessage("ABOUT_TO_EXECUTE_VIEW")+" " + requestURI);
        }

        // update the JSTL locale attribute in request scope so that JSTL
        // picks up the locale from viewRoot. This attribute must be updated
        // before the JSTL setBundle tag is called because that is when the
        // new LocalizationContext object is created based on the locale.
        if (extContext.getRequest() instanceof ServletRequest) {
            Config.set((ServletRequest)
            extContext.getRequest(),
                       Config.FMT_LOCALE, context.getViewRoot().getLocale());
        }
        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("BEFORE_DISPATCHMESSAGE_TO_VIEWID")+" " + requestURI);
        }

        // save the original response
        Object originalResponse = extContext.getResponse();

        // replace the response with our wrapper
        ViewHandlerResponseWrapper wrapped = getWrapper(extContext);
        extContext.setResponse(wrapped);

        // build the view by executing the page
        extContext.dispatch(requestURI);

        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("AFTER_DISPATCHMESSAGE_TO_VIEWID")+" " + requestURI);
        }

        // replace the original response
        extContext.setResponse(originalResponse);

        // Follow the JSTL 1.2 spec, section 7.4,
        // on handling status codes on a forward
        if (wrapped.getStatus() < 200 || wrapped.getStatus() > 299) {
            // flush the contents of the wrapper to the response
            // this is necessary as the user may be using a custom
            // error page - this content should be propagated
            wrapped.flushContentToWrappedResponse();
            return true;
        }

        // Put the AFTER_VIEW_CONTENT into request scope
        // temporarily
        RequestStateManager.set(context,
                                RequestStateManager.AFTER_VIEW_CONTENT,
                                wrapped);

        return false;

    }


    public Locale calculateLocale(FacesContext context) {

        if (context == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }

        Locale result = null;
        // determine the locales that are acceptable to the client based on the
        // Accept-Language header and the find the best match among the
        // supported locales specified by the client.
        Iterator<Locale> locales = context.getExternalContext().getRequestLocales();
        while (locales.hasNext()) {
            Locale perf = locales.next();
            result = findMatch(context, perf);
            if (result != null) {
                break;
            }
        }
        // no match is found.
        if (result == null) {
            if (context.getApplication().getDefaultLocale() == null) {
                result = Locale.getDefault();
            } else {
                result = context.getApplication().getDefaultLocale();
            }
        }
        return result;
    }


    public String calculateRenderKitId(FacesContext context) {

        if (context == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }

        Map<String,String> requestParamMap = context.getExternalContext()
            .getRequestParameterMap();
        String result = requestParamMap.get(
            ResponseStateManager.RENDER_KIT_ID_PARAM);

        if (result == null) {
            if (null ==
                (result = context.getApplication().getDefaultRenderKitId())) {
                result = RenderKitFactory.HTML_BASIC_RENDER_KIT;
            }
        }
        return result;
    }


    /**
     * Attempts to find a matching locale based on <code>pref</code> and
     * list of supported locales, using the matching algorithm
     * as described in JSTL 8.3.2.
     * @param context the <code>FacesContext</code> for the current request
     * @param pref the preferred locale
     * @return the Locale based on pref and the matching alogritm specified
     *  in JSTL 8.3.2
     */
    protected Locale findMatch(FacesContext context, Locale pref) {
        Locale result = null;
        Iterator<Locale> it = context.getApplication().getSupportedLocales();
        while (it.hasNext()) {
            Locale supportedLocale = it.next();

            if (pref.equals(supportedLocale)) {
                // exact match
                result = supportedLocale;
                break;
            } else {
                // Make sure the preferred locale doesn't have country
                // set, when doing a language match, For ex., if the
                // preferred locale is "en-US", if one of supported
                // locales is "en-UK", even though its language matches
                // that of the preferred locale, we must ignore it.
                if (pref.getLanguage().equals(supportedLocale.getLanguage()) &&
                     supportedLocale.getCountry().length() == 0) {
                    result = supportedLocale;
                }
            }
        }
        // if it's not in the supported locales,
        if (null == result) {
            Locale defaultLocale = context.getApplication().getDefaultLocale();
            if (defaultLocale != null) {
                if ( pref.equals(defaultLocale)) {
                    // exact match
                    result = defaultLocale;
                } else {
                    // Make sure the preferred locale doesn't have country
                    // set, when doing a language match, For ex., if the
                    // preferred locale is "en-US", if one of supported
                    // locales is "en-UK", even though its language matches
                    // that of the preferred locale, we must ignore it.
                    if (pref.getLanguage().equals(defaultLocale.getLanguage()) &&
                         defaultLocale.getCountry().length() == 0) {
                        result = defaultLocale;
                    }
                }
            }
        }

        return result;
    }


    public void writeState(FacesContext context) throws IOException {
        if (context == null) {
           String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }

        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("BEGIN_WRITING_MARKER_FOR_VIEWID")+" " +
                        context.getViewRoot().getViewId());
        }

        XUIWriteBehindStateWriter writer = XUIWriteBehindStateWriter.getCurrentInstance();
        if (writer != null) {
            writer.writingState();
        }
        context.getResponseWriter().write(RIConstants.SAVESTATE_FIELD_MARKER);
        if (logger.isFinerEnabled()) {
            logger.finer(LoggerMessageLocalizer.getMessage("END_WRITING_MARKER_FOR_VIEWID")+" " +
                        context.getViewRoot().getViewId());
        }

    }
    
    public String getAjaxURL( FacesContext context, String viewId ) {
    	String sActionUrl;
    	sActionUrl = (String)((ServletRequest)context.getExternalContext().getRequest()).getAttribute("xvw.ajaxActionUrl");
    	if( sActionUrl != null ) {
    		return sActionUrl;
    	}
    	sActionUrl = (String)((ServletRequest)context.getExternalContext().getRequest()).getParameter("xvw.ajaxActionUrl");
    	if( sActionUrl != null ) {
    		return sActionUrl;
    	}
    	return getActionURL( context, viewId);
    }

    public String getActionURL(FacesContext context, String viewId) {
    	
    	// Este m�todo est� ser overwrited por XUIViewHandler
    	// Qualquer altera��o n�o � afectada.
    	return null;
//    	
//        if (context == null) {
//            String message = MessageUtils.getExceptionMessageString
//                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
//            throw new NullPointerException(message);
//        }
//        if (viewId == null) {
//            String message = MessageUtils.getExceptionMessageString
//                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "viewId");
//            throw new NullPointerException(message);
//        }
//
//        if (viewId.charAt(0) != '/') {
//            String message =
//                  MessageUtils.getExceptionMessageString(
//                        MessageUtils.ILLEGAL_VIEW_ID_ID,
//                        viewId);
//            if (logger.isLoggable(Level.SEVERE)) {
//                logger.log(Level.SEVERE, "jsf.illegal_view_id_error", viewId);
//            }
//        throw new IllegalArgumentException(message);
//        }
//
//        // Acquire the context path, which we will prefix on all results
//        ExternalContext extContext = context.getExternalContext();
//        String contextPath = extContext.getRequestContextPath();
//
//        // Acquire the mapping used to execute this request (if any)
//        String mapping = Util.getFacesMapping(context);
//
//        // If no mapping can be identified, just return a server-relative path
//        if (mapping == null) {
//            return (contextPath + viewId);
//        }
//
//        // Deal with prefix mapping
//        if (Util.isPrefixMapped(mapping)) {
//            if (mapping.equals("/*")) {
//                return (contextPath + viewId);
//            } else {
//                return (contextPath + mapping + viewId);
//            }
//        }
//
//        // Deal with extension mapping
//        int period = viewId.lastIndexOf('.');
//        if (period < 0) {
//            return (contextPath + viewId + mapping);
//        } else if (!viewId.endsWith(mapping)) {
//            return (contextPath + viewId.substring(0, period) + mapping);
//        } else {
//            return (contextPath + viewId);
//        }
//
    }


    public String getResourceURL(FacesContext context, String path) {

    	String sContextPath;
    	sContextPath = (String)((ServletRequest)context.getExternalContext().getRequest()).getAttribute("xvw.resourcesUrl");
    	
    	if( sContextPath != null ) {
            if (path.startsWith("/")) {
                return (sContextPath + path);
            }
            else {
            	return (sContextPath + "/" + path);
            }
    	}
    	ExternalContext extContext = context.getExternalContext();
    	sContextPath = extContext.getRequestContextPath();
        return (sContextPath + "/" + path);
    }


    /**
     * <p>if the specified mapping is a prefix mapping, and the provided
     * request URI (usually the value from <code>ExternalContext.getRequestServletPath()</code>)
     * starts with <code>mapping + '/'</code>, prune the mapping from the
     * URI and return it, otherwise, return the original URI.
     * @param uri the servlet request path
     * @param mapping the FacesServlet mapping used for this request
     * @return the URI without additional FacesServlet mappings
     * @since 1.2
     */
    private String normalizeRequestURI(String uri, String mapping) {

        if (mapping == null || !Util.isPrefixMapped(mapping)) {
            return uri;
        } else {
            int length = mapping.length() + 1;
            StringBuilder builder = new StringBuilder(length);
            builder.append(mapping).append('/');
            String mappingMod = builder.toString();
            boolean logged = false;
            while (uri.startsWith(mappingMod)) {
                if (!logged && logger.isLoggable( LoggerLevels.WARNING )) {
                    logged = true;
                    logger.warn("jsf.viewhandler.requestpath.recursion [" + uri +"] [" + mapping + "]" );
                }
                uri = uri.substring(length - 1);
            }
            return uri;
        }
    }

    private void send404Error(FacesContext context) {
        HttpServletResponse response = (HttpServletResponse)
             context.getExternalContext().getResponse();
        try {
            context.responseComplete();
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException ioe) {
            throw new FacesException(ioe);
        }
    }


    /**
     * <p>Adjust the viewID per the requirements of {@link #renderView}.</p>
     *
     * @param context current {@link FacesContext}
     * @param viewId  incoming view ID
     * @return the view ID with an altered suffix mapping (if necessary)
     */
    protected String convertViewId(FacesContext context, String viewId) {

        if (contextDefaultSuffix == null) {
            contextDefaultSuffix =
                  WebConfiguration
                        .getInstance(context.getExternalContext())
                        .getOptionValue(WebContextInitParameter.JspDefaultSuffix);
            if (contextDefaultSuffix == null) {
                contextDefaultSuffix = ViewHandler.DEFAULT_SUFFIX;
            }
            if (logger.isFinerEnabled()) {
                logger.finer("contextDefaultSuffix "
                            + contextDefaultSuffix);
            }
        }

        String convertedViewId = viewId;
        // if the viewId doesn't already use the above suffix,
        // replace or append.
        if (!convertedViewId.endsWith(contextDefaultSuffix)) {
            StringBuilder buffer = new StringBuilder(convertedViewId);
            int extIdx = convertedViewId.lastIndexOf('.');
            if (extIdx != -1) {
                buffer.replace(extIdx, convertedViewId.length(),
                               contextDefaultSuffix);
            } else {
                // no extension in the provided viewId, append the suffix
                buffer.append(contextDefaultSuffix);
            }
            convertedViewId = buffer.toString();
            if (logger.isFinerEnabled()) {
                logger.finer(LoggerMessageLocalizer.getMessage("VIEWID_AFTER_APPENDING_THE_CONTEXT_SUFFIX")+ " " +
                             convertedViewId);
            }

        }
        return convertedViewId;
    }


    protected XUIApplicationAssociate getAssociate(FacesContext context) {
        if (associate == null) {
            associate = XUIApplicationAssociate.getInstance(context.getExternalContext());
        }
        return associate;
    }


    private static ViewHandlerResponseWrapper getWrapper(ExternalContext extContext) {
        Object response = extContext.getResponse();
        if (response instanceof HttpServletResponse) {
            return new ViewHandlerResponseWrapper((HttpServletResponse) response);
        }
        throw new IllegalArgumentException();

    }

}