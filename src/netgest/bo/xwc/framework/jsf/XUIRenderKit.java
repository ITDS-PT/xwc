package netgest.bo.xwc.framework.jsf;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

import netgest.bo.xwc.framework.XUIResponseWriter;

import com.sun.faces.RIConstants;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;
import com.sun.faces.renderkit.RenderKitImpl;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;

public class XUIRenderKit extends RenderKitImpl
{
    private static final String[] SUPPORTED_CONTENT_TYPES_ARRAY =
         new String[]{
              RIConstants.HTML_CONTENT_TYPE,
              RIConstants.XHTML_CONTENT_TYPE,
              RIConstants.APPLICATION_XML_CONTENT_TYPE,
              RIConstants.TEXT_XML_CONTENT_TYPE
         };

    private static final String SUPPORTED_CONTENT_TYPES =
         RIConstants.HTML_CONTENT_TYPE + ','
              + RIConstants.XHTML_CONTENT_TYPE + ','
              + RIConstants.APPLICATION_XML_CONTENT_TYPE + ','
              + RIConstants.TEXT_XML_CONTENT_TYPE;


    private Map<WebConfiguration.BooleanWebContextInitParameter,
                Boolean> configPrefs;

    public XUIRenderKit() {
        initConfigPrefs();
    }

    @Override
    public ResponseStream createResponseStream(OutputStream out) {
        return super.createResponseStream(out);
    }

    private void initConfigPrefs() {
        assert(null == configPrefs); // class invariant

        FacesContext context = FacesContext.getCurrentInstance();
        WebConfiguration webConfig = WebConfiguration.getInstance(
                                  context.getExternalContext());
        Map<WebConfiguration.BooleanWebContextInitParameter,
                Boolean> prefs = 
            new HashMap<WebConfiguration.BooleanWebContextInitParameter,
                        Boolean>();

        prefs.put(BooleanWebContextInitParameter.PreferXHTMLContentType,
                        webConfig.isOptionEnabled(
                         BooleanWebContextInitParameter.PreferXHTMLContentType));
        prefs.put(BooleanWebContextInitParameter.EnableJSStyleHiding,
                        webConfig.isOptionEnabled(
                         BooleanWebContextInitParameter.EnableJSStyleHiding));
        prefs.put(BooleanWebContextInitParameter.EnableScriptInAttributeValue,
                        webConfig.isOptionEnabled(
                         BooleanWebContextInitParameter.EnableScriptInAttributeValue));
        
        configPrefs = Collections.unmodifiableMap(prefs);
        
    }


    @Override
    public ResponseWriter createResponseWriter(java.io.Writer writer, String desiredContentTypeList, String characterEncoding) {
        //return super.createResponseWriter(writer, desiredContentTypeList, characterEncoding);
        if (writer == null) {
            return null;
        }
        String contentType = null;
        boolean contentTypeNullFromResponse = false;
        FacesContext context = FacesContext.getCurrentInstance();

        // Step 1: Check the content type passed into this method 
        if (null != desiredContentTypeList) {
            contentType = findMatch(
                 desiredContentTypeList,
                 SUPPORTED_CONTENT_TYPES_ARRAY);
        }

        // Step 2: Check the response content type
        if (null == desiredContentTypeList) {
            desiredContentTypeList = null;
                 //context.getExternalContext().getResponseContentType();
            if (null != desiredContentTypeList) {
                contentType = findMatch(
                     desiredContentTypeList,
                     SUPPORTED_CONTENT_TYPES_ARRAY);
                if (null == contentType) {
                    contentTypeNullFromResponse = true;
                }
            }
        }

        // Step 3: Check the Accept Header content type
        // Evaluate the accept header in accordance with HTTP specification - 
        // Section 14.1
        // Preconditions for this (1 or 2):
        //  1. content type was not specified to begin with
        //  2. an unsupported content type was retrieved from the response 
        if (null == desiredContentTypeList || contentTypeNullFromResponse) {
            String[] typeArray =
                 context.getExternalContext().getRequestHeaderValuesMap().get("Accept");
            if (typeArray.length > 0) {
                StringBuffer buff = new StringBuffer();
                buff.append(typeArray[0]);
                for (int i = 1, len = typeArray.length; i < len; i++) {
                    buff.append(',');
                    buff.append(typeArray[i]);
                }
                desiredContentTypeList = buff.toString();
            }

            if (null != desiredContentTypeList) {
                if (configPrefs.get(BooleanWebContextInitParameter.PreferXHTMLContentType)) {
                    desiredContentTypeList = RenderKitUtils.determineContentType(
                         desiredContentTypeList, SUPPORTED_CONTENT_TYPES, RIConstants.XHTML_CONTENT_TYPE);
                } else {
                    desiredContentTypeList = RenderKitUtils.determineContentType(
                         desiredContentTypeList, SUPPORTED_CONTENT_TYPES, null);
                }
                if (null != desiredContentTypeList) {
                    contentType = findMatch(
                         desiredContentTypeList,
                         SUPPORTED_CONTENT_TYPES_ARRAY);
                }
            }
        }

        // Step 4: Default to text/html
        if (contentType == null) {
                if (null == desiredContentTypeList) {
                        contentType = RIConstants.HTML_CONTENT_TYPE;
                } else {
                        String[] desiredContentTypes = contentTypeSplit(desiredContentTypeList);
                        for (String desiredContentType : desiredContentTypes) {
                                if (RIConstants.ALL_MEDIA.equals(desiredContentType.trim())) {
                                        contentType = RIConstants.HTML_CONTENT_TYPE;
                                }
                        }
                }
        }

        if (null == contentType) {
            throw new IllegalArgumentException(MessageUtils.getExceptionMessageString(
                 MessageUtils.CONTENT_TYPE_ERROR_MESSAGE_ID));
        }

        if (characterEncoding == null) {
            characterEncoding = "UTF-8";
        }

        return new XUIResponseWriter(writer,
                                      contentType,
                                      characterEncoding,
                                      configPrefs);
        
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        return super.getRenderer(family, rendererType);
    }

    @Override
    public synchronized ResponseStateManager getResponseStateManager() {
        return super.getResponseStateManager();
    }

    private String findMatch(String desiredContentTypeList,
                             String[] supportedTypes) {

        String contentType = null;
        String[] desiredTypes = contentTypeSplit(desiredContentTypeList);

        // For each entry in the desiredTypes array, look for a match in
        // the supportedTypes array
        for (int i = 0, ilen = desiredTypes.length; i < ilen; i++) {
            String curDesiredType = desiredTypes[i];
            for (int j = 0, jlen = supportedTypes.length; j < jlen; j++) {
                String curContentType = supportedTypes[j].trim();
                if (curDesiredType.contains(curContentType)) {
                    if (curContentType.contains(RIConstants.HTML_CONTENT_TYPE)) {
                        contentType = RIConstants.HTML_CONTENT_TYPE;
                    } else
                    if (curContentType.contains(RIConstants.XHTML_CONTENT_TYPE) ||
                         curContentType.contains(RIConstants.APPLICATION_XML_CONTENT_TYPE) ||
                         curContentType.contains(RIConstants.TEXT_XML_CONTENT_TYPE)) {
                        contentType = RIConstants.XHTML_CONTENT_TYPE;
                    }
                    break;
                }
            }
            if (null != contentType) {
                break;
            }
        }
        return contentType;
    }

    private String[] contentTypeSplit(String contentTypeString) {
        String[] result = Util.split(contentTypeString, ",");
        for (int i = 0; i < result.length; i++) {
            int semicolon = result[i].indexOf(";");
            if (-1 != semicolon) {
                result[i] = result[i].substring(0, semicolon);
            }
        }
        return result;
    }

}
