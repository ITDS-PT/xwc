package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.IOException;

import oracle.xml.parser.v2.XMLDocument;

import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.ngtXMLUtils;

public class BoConfigBean extends XEOBaseBean {

    public String getXmlString() throws IOException {
    	XMLDocument xmldoc = getEboContext().getApplication().getApplicationConfig().getXmldoc();
    	String xmlString = ngtXMLUtils.getXML(xmldoc);
    	XmlXhtmlRenderer render = new XmlXhtmlRenderer();
        String ret = render.highlight(null,xmlString,xmldoc.getEncoding(), false);
        
        return ret;
    }
}
