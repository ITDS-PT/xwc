package netgest.bo.xwc.framework.localization;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;

import netgest.bo.system.boSession;
import netgest.utils.ngtXMLUtils;

public class XUIMessagesLocalization {

	public static Map<String,Map> resourceBundles = new HashMap<String,Map>();
	
	public static String getString(String resourceBundle, String key)
    {
		return null;
    }

	public static void loadResourceBundle( String resourceName ) {
		/*
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourceName + ".xml" );
		
		XMLDocument xmlDoc = ngtXMLUtils.loadXML( is );
		NodeList x = xmlDoc.selectNodes("/messages/message");
		
		//Map<String, Map<String,Map<String,String>>> local_map = new HashMap<String, Map<String,String>>();
		
		for( int i=0;i < x.getLength(); i++ ) {
			
			XMLNode n = (XMLNode)x.item( i );
			
			String key = n.getAttributes().getNamedItem("key").getNodeValue();
			
			NodeList vl = n.selectNodes("value");
			
			for( int z=0; z < vl.getLength(); z++ ) {
				XMLNode vn = (XMLNode)vl.item( z );
				String lang = vn.getAttributes().getNamedItem("lang").getNodeValue();
				String value  = vn.getNodeValue();
				
				Map<String,Map> keys_map = local_map.get( lang );
				if( keys_map == null ) {
					keys_map = new HashMap<String, Map>();
					local_map
				}
				
				
				
			}
		}
		*/
	}
}
