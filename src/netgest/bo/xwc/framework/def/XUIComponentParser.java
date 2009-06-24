package netgest.bo.xwc.framework.def;

import java.io.InputStream;


import java.util.logging.Level;
import java.util.logging.Logger;

import netgest.bo.xwc.framework.XUIApplicationContext;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NodeList;

public class XUIComponentParser
{
    private static final Logger log = Logger.getLogger( XUIComponentParser.class.getName() );
    
    private static NSResolver nr = new GenericResolver();
    
    
    
    public XUIComponentParser(  )
    {
    }
    
    public void loadComponents( XUIApplicationContext oXUIApplication )
    {
        loadComponents( oXUIApplication, getClass().getClassLoader().getResourceAsStream( "XWVComponents.xml" ) );
    }   
    
    public void loadComponents( XUIApplicationContext oXUIApplication, InputStream xml )
    {
        try
        {
            XMLDocument doc = ngtXMLUtils.loadXML( xml );
            XMLElement oConfigElement       = (XMLElement)doc.selectSingleNode("xwc:config",nr);
            XMLElement oComponentsElement   = (XMLElement)oConfigElement.selectSingleNode("xwc:components",nr);
            XMLElement oRenderKitsElement   = (XMLElement)oConfigElement.selectSingleNode("xwc:renderKits",nr);
            
            XUIComponentStore oComponentStore = oXUIApplication.getComponentStore();
            // Load components in XML file
            parseComponents( oComponentStore, oComponentsElement );

            // Add render Kits
            parseRenderKits( oComponentStore, oRenderKitsElement );
            parseComponentRenders( oComponentStore, oRenderKitsElement );
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private void parseRenderKits( XUIComponentStore oComponents, XMLElement oRenderKitsElement ) throws Exception {
        String sRenderKitName;
        String sRenderKitClassName;

        NodeList listRenderKits = oRenderKitsElement.selectNodes("xwc:renderKit",nr);
        for (int i = 0; i < listRenderKits.getLength(); i++)  
        {
            XMLElement oRenderKitElement = (XMLElement)listRenderKits.item( i );    
            sRenderKitName = oRenderKitElement.getAttribute("name");
            sRenderKitClassName = oRenderKitElement.getAttribute("className");
            if( sRenderKitClassName != null && sRenderKitClassName.length() != 0 ) {
            	oComponents.registerRenderKit( sRenderKitName, sRenderKitClassName);
            }
        }
    }
    
    
    private void parseComponentRenders( XUIComponentStore oComponents, XMLElement oRenderKitsElement ) throws Exception
    {
        try 
        {
            String sRenderKitName;
            String sRenderFor;
            String sRenderClassName;

            NodeList    oRendersNodeList;
            XMLElement  oRenderElement;
            XUIComponentDefinition oComponent;
            
            
            NodeList listRenderKits = oRenderKitsElement.selectNodes("xwc:renderKit",nr);
            for (int i = 0; i < listRenderKits.getLength(); i++)  
            {
                XMLElement oRenderKitElement = (XMLElement)listRenderKits.item( i );    
                
                sRenderKitName = oRenderKitElement.getAttribute("name");
                
                oRendersNodeList = oRenderKitElement.selectNodes("xwc:render",nr);
                
                for (int j = 0; j < oRendersNodeList.getLength(); j++)  {
                    
                    oRenderElement = (XMLElement)oRendersNodeList.item( j );
                    
                    sRenderFor = oRenderElement.getAttribute( "for" );
                    
                    assert sRenderFor != null:"for attribute must be defined in element";
                    oComponent = oComponents.getComponent( sRenderFor );
                    
                    if( oComponent != null )
                    {
                        
                        sRenderClassName = oRenderElement.getText();
                        
                        oComponent.addRenderKit( sRenderKitName, sRenderClassName );
                        
                        
                    }
                    else{
                        if ( log.isLoggable( Level.INFO ) ){
                            log.warning("Component id ["+sRenderFor+"] not found parsing renderKit" );
                        }
                    }
                }
            }
            
        } 
        finally 
        {
        
        }

    }

    private XUIComponentStore parseComponents( XUIComponentStore oComponents, XMLElement xcomponents ) throws Exception
    {
        try
        {
            
            NodeList list = xcomponents.selectNodes("xwc:component",nr);
            for (int i = 0; i < list.getLength(); i++) 
            {
                oComponents.registerComponent(
                    parseComponent( (XMLElement)list.item( i ) )
                );
            }
            return oComponents;
        }
        catch (XSLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private XUIComponentDefinition parseComponent( XMLElement xcomponent ) throws Exception
    {

        XUIComponentDefinition component = new XUIComponentDefinition();
        
        component.setName( xcomponent.getAttribute("name") );
        component.setClassName( getNodeText( xcomponent, "xwc:className" ) );
        component.setDescription( getNodeText( xcomponent, "xwc:description" ) );
/*        
        XMLElement propertiesElement = (XMLElement)xcomponent.selectSingleNode("xwc:properties",nr);
        if ( propertiesElement != null ) 
        {
            parseComponentProperties( component, propertiesElement );
        }

        
        XMLElement renderKitsElement = (XMLElement)xcomponent.selectSingleNode("xwc:renderKits",nr);
        if ( renderKitsElement != null ) 
        {
            parseComponentRenderKits( component, renderKitsElement );
        }
*/

        return component;
    }
     
/*
 * Vai ser substituido por java anotations
    private void parseComponentProperties( XPFComponent component, XMLElement propertiesElement ) throws Exception
    {
        NodeList nlist = propertiesElement.selectNodes("xwc:property",nr);
        for (int i = 0; i < nlist.getLength(); i++) 
        {
            XMLElement nelement = (XMLElement)nlist.item( i );
            component.addProperty( nelement.getAttribute("name"),nelement.getAttribute("type"));
        }
    }
*/    
    
    private static final String getNodeText( XMLElement node, String nodeName )
    {
        try
        {
            XMLNode tnode = (XMLNode)node.selectSingleNode( nodeName, nr );
            if( tnode != null )
            {
                return tnode.getText();
            }
            return null;
        }
        catch (XSLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
    private static class GenericResolver implements NSResolver
    {
        public String resolveNamespacePrefix( String prefix )
        {
            return "http://www.netgest.net/xeo/xvc";
        }
    }
    
}
