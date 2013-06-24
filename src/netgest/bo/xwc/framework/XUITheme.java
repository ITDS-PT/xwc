package netgest.bo.xwc.framework;

import java.io.IOException;

import netgest.bo.xwc.framework.components.XUIViewRoot;


public interface XUITheme {

    /**
     * The base url for resources
     * 
     * @return
     */
    public String getResourceBaseUri();
    /**
     * Add CSS resources to the page
     * 
     * @param styleContext
     */
    public void addStyle( XUIStyleContext styleContext );
    /**
     * Add Javascript resources to the page
     * 
     * @param scriptContext
     */
    public void addScripts( XUIScriptContext scriptContext );
    /**
     * 
     * Html style to apply to the <code>body</code> tag
     * 
     * @return
     */
    public String getBodyStyle();
    /**
     * 
     * Html style to apply to the <code>html</code> tag
     * 
     * @return
     */
    public String getHtmlStyle();
    /**
     * 
     * The page DOCTYPE (full string including the DOCTYPE)
     * 
     * @return The string with the DOCTYPE 
     */
    public String getDocType();
    
    /**
     * 
     * Allows to write to the header of the page (metatags, etc...)
     * 
     * @param writer
     * @throws IOException
     */
    public void writeHeader(XUIResponseWriter writer) throws IOException;
    /**
     * 
     * Write content right after opening the body tag
     * 
     * @param context
     * @param writer
     * @param viewRoot
     * @throws IOException
     */
    public void writePostBodyContent(XUIRequestContext context, XUIResponseWriter writer,
    		XUIViewRoot viewRoot) throws IOException ;
    
    /**
     * 
     * Write content right before closing the body tag
     * 
     * @param context
     * @param writer
     * @param viewRoot
     * @throws IOException
     */
    public void writePreFooterContent(XUIRequestContext context, XUIResponseWriter writer,
    		XUIViewRoot viewRoot) throws IOException ;
    
}
