package netgest.bo.xwc.framework.http;

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletRequestWrapper;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XUIAjaxRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest          oHttpRequest;
    private Hashtable<String,String[]>  oRequestParmatersMap;
    private Document                    oXmlRequestDocument;
    private String                      sTextRequest;
    
    
    public XUIAjaxRequestWrapper( HttpServletRequest oHttpRequest ) {
        super( oHttpRequest );
        this.oHttpRequest = oHttpRequest;
        try {
            loadRequest();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load AJAX request", e );
        }
    }

    @Override
    public Map getParameterMap() {
        return oRequestParmatersMap;
    }

    @Override
    public Enumeration getParameterNames() {
        return oRequestParmatersMap.keys();
    }

    @Override
    public String getParameter( String sParameterName ) {
        
        String[] sParValue = oRequestParmatersMap.get( sParameterName );
        if( sParValue != null ) {
            return sParValue[ 0 ];
        }
        return null;
    }

    @Override
    public String[] getParameterValues( String sParameterName ) {
        return oRequestParmatersMap.get( sParameterName );
    }

    public void loadRequest() throws IOException {

    	String oRequestEncoding = oHttpRequest.getCharacterEncoding();
    	if( oRequestEncoding != null && !"UTF-8".equals( oRequestEncoding ) ) {
    		int br;
    		byte[] buff = new byte[4096];
        	StringBuilder sb = new StringBuilder();
        	InputStream i = oHttpRequest.getInputStream();
        	while( (br=i.read(buff)) > 0 ) {
        		sb.append( new String( buff, 0, br, oRequestEncoding ) );
        	}
        	oXmlRequestDocument = ngtXMLUtils.loadXML( sb.toString() );
    	}
    	else {
            oXmlRequestDocument = ngtXMLUtils.loadXML( oHttpRequest.getInputStream() );
    	}
        //sTextRequest = ngtXMLUtils.getXML( (XMLDocument)oXmlRequestDocument );
        parseXmlRequest( oXmlRequestDocument );        

    }
    
    private void parseXmlRequest( Document oXmlDoc ) {
        
        Element oXvwAjaxElem;
        NodeList oParametersList;
        Element oParameters;
        
        oRequestParmatersMap = new Hashtable<String,String[]>();
        
        oXvwAjaxElem = oXmlDoc.getDocumentElement();
        
        oParametersList = oXvwAjaxElem.getElementsByTagName("parameters");
        for (int i = 0; i < oParametersList.getLength(); i++) {
            parseParameters( (Element)oParametersList.item( i ) );
        }

    }
    
    private void parseParameters( Element oParametersElement ) {
        
        NodeList    oParametersList;
        Element     oParameter;
        String      sParName;
        String      sParValue;
        String[]    sParValues;
        String[]    sParValuesTemp;
        
        oParametersList = oParametersElement.getElementsByTagName( "p" );
        
        for (int i = 0; i < oParametersList.getLength(); i++) {
            
            oParameter  = (Element)oParametersList.item( i );
            sParName    = oParameter.getAttribute("name");
            
            sParValue   = ((XMLElement)oParameter).getTextContent();
            
            if( sParValue == null ) {
            	sParValue = "";
            }
                
            if( oRequestParmatersMap.containsKey( sParName ) ) {
                
                sParValuesTemp = oRequestParmatersMap.get( sParName );
                
                sParValues = new String[ sParValuesTemp.length + 1 ];
                System.arraycopy( sParValues, 0, sParValues, 0, sParValuesTemp.length );
                sParValues[ sParValuesTemp.length ] = sParValue;
                
                oRequestParmatersMap.put( sParName, sParValues );
                
            }
            else {
                sParValues = new String[] { sParValue };
            }
            
            // Add parameters to the request Map;
            oRequestParmatersMap.put( sParName, sParValues );

        }
    }

    @Override
    @Deprecated
    public String getRealPath(String string) {
        return super.getRealPath(string);
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return super.isRequestedSessionIdFromUrl();
    }
}
