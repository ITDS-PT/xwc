package netgest.bo.xwc.framework.def;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import javax.xml.parsers.DocumentBuilderFactory;


import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XUIViewerDefinitonParser
{
    private static NSResolver ns = new GenericResolver();
    private static Logger log = Logger.getLogger( XUIViewerDefinitonParser.class.getName() );
    
    private static  HashMap<String, XUIViewerDefinition> viewCache = new HashMap<String, XUIViewerDefinition>();

    public XUIViewerDefinitonParser()
    {
    }
    
    public XUIViewerDefinition parse( InputStream inputStream ) {
        XUIViewerDefinition xwvr;
        try
        {
            XMLElement node;
            XMLDocument xmldoc;

            xmldoc = ngtXMLUtils.loadXML( inputStream );

            node = (XMLElement)xmldoc.selectSingleNode("/xvw:root/xvw:viewer", ns);
            xwvr = new XUIViewerDefinition();

            xwvr.setViewerBean( node.getAttribute( "beanClass" ) );
            xwvr.setViewerBeanId( node.getAttribute( "beanId" ) );
            
            String isTransient = node.getAttribute("transient");
            if( isTransient != null ) {
            	xwvr.setTransient( Boolean.valueOf( isTransient ) );
            }
            
            NodeList nlist = node.getChildNodes();
            for (int i = 0; i < nlist.getLength(); i++) 
            {
                Node childNode = nlist.item( i );
                if( childNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    xwvr.setRootComponent( parseNode( xwvr, (XMLElement)childNode, null ) );
                }
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );                
        }
        finally {
        }
        return xwvr;
    }
    
    public XUIViewerDefinition parse( String viewerName ) 
    {
        XUIViewerDefinition xwvr;
        xwvr = null;//viewCache.get( viewerName );
        if( xwvr == null ) {
            InputStream is = getClass().getClassLoader().getResourceAsStream( viewerName );
            if( is != null )
            {
            	try {
            		xwvr = parse( is );
            		viewCache.put( viewerName, xwvr );
            	} finally {
            		if( is != null )
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
            	}
            }
            else
            {
                throw new RuntimeException("O viewer " + viewerName + " não existe");
            }
        }
        return xwvr;
    }
    
    public XUIViewerDefinitionNode parseNode( XUIViewerDefinition root, XMLElement node, XUIViewerDefinitionNode parent )
    {
        XUIViewerDefinitionNode component = new XUIViewerDefinitionNode();
        component.setRoot( root );
        component.setParent( parent );
        
        
        if( node.getNodeName().indexOf(':') == -1 )
        {
            component.setName("genericTag");

            LinkedHashMap<String, String> propertiesMap = new LinkedHashMap<String, String>();
            propertiesMap.put("__tagName", node.getNodeName() );
            
            NamedNodeMap attNodeMap = node.getAttributes();
            int iAttsLength = attNodeMap.getLength();

            for( int i=0; i < iAttsLength; i++ ) {
            	Node attr = attNodeMap.item( i );
            	propertiesMap.put( attr.getNodeName(), attr.getNodeValue() );
            }
            component.setProperties( propertiesMap );
        }
        else
        {
            component.setName( node.getNodeName().substring( node.getNodeName().indexOf(':')+1 ) );
        }
        NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) 
        {
            Node xnode = atts.item( i );
            if( "id".equalsIgnoreCase( xnode.getNodeName() ) )
            {
                component.setId( xnode.getNodeValue() );   
            }
            else
            {
                component.setProperty( xnode.getNodeName(), xnode.getNodeValue() );
            }
        }
        NodeList nlist = node.getChildNodes();
        for (int i = 0; i < nlist.getLength(); i++) 
        {
            Node cnode = nlist.item( i );
            if( cnode.getNodeType() == Node.ELEMENT_NODE )
            {
                component.addChild( parseNode( root, (XMLElement)nlist.item( i ), component ) );
            } 
            else if ( cnode.getNodeType() == Node.TEXT_NODE ) 
            {
            	XUIViewerDefinitionNode childText = new XUIViewerDefinitionNode();
            	childText.setName("genericTag");
            	childText.setTextContent( cnode.getNodeValue() );
            	component.addChild( childText );
            }
        }
        return component; 
    }
    
    private static class GenericResolver implements NSResolver
    {
        public String resolveNamespacePrefix( String prefix )
        {
            return "http://www.netgest.net/xeo/xvw";
        }
    }
    
}
