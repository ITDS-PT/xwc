package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

public class BoConfigBean extends XEOBaseBean {

	private String render;
	
    public String getXmlString() throws IOException {
    	XMLDocument xmldoc = getEboContext().getApplication().getApplicationConfig().getXmldoc();
    	String xmlString = ngtXMLUtils.getXML(xmldoc);
    	XmlXhtmlRenderer render = new XmlXhtmlRenderer();
        String ret = render.highlight(null,xmlString,xmldoc.getEncoding(), false);
        
        return ret;
    }
    
    public void setRender(String render){
    	this.render = render;
    }
    
    public String getRender(){
    	TransformerFactory transFact = TransformerFactory.newInstance();
	    InputStream xsltSource = BoConfigBean.class.getResourceAsStream("boconfig.xsl");
	    Transformer trans;
		try 
		{
			trans = transFact.newTransformer(new StreamSource(xsltSource));
			String xml = ngtXMLUtils.getXML(getEboContext().getApplication().getApplicationConfig().getXmldoc());
			Source xmlSource = new StreamSource(new StringReader(xml));
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			trans.transform(xmlSource, result);
			render = writer.toString();
			return render;
		} 
		catch (TransformerConfigurationException e) 
		{ e.printStackTrace();	} 
		catch (TransformerException e)
		{ e.printStackTrace(); }
		return "";
    }
}
