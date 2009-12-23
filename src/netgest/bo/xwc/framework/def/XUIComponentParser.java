package netgest.bo.xwc.framework.def;

import java.io.IOException;
import java.io.InputStream;

import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
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
    	InputStream systemsComponents = null;
    	try {
			systemsComponents = Thread.currentThread().getContextClassLoader().getResourceAsStream( "XWVComponents.xml" );
			if( systemsComponents != null ) 
				loadComponents( oXUIApplication, systemsComponents );
		}
		finally {
			try {
				if( systemsComponents != null ) 
					systemsComponents.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

    	InputStream projectComponents = null;
    	try {
			projectComponents = Thread.currentThread().getContextClassLoader().getResourceAsStream( "XWVProjectComponents.xml" );
			if( projectComponents != null ) {
				loadComponents( oXUIApplication, projectComponents );
			}
		}
		finally {
			try {
				if( projectComponents != null )
					projectComponents.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }   
    
    public void loadComponents( XUIApplicationContext oXUIApplication, InputStream xml )
    {
        try
        {
            XMLDocument doc = ngtXMLUtils.loadXML( xml );
            
            // Query config Element
            XMLElement oConfigElement       = (XMLElement)doc.selectSingleNode("xwc:config",nr);

            // Get the application component store
            XUIComponentStore oComponentStore = oXUIApplication.getComponentStore();
            
            // Load components in XML file
            NodeList oComponentsNodeList   = oConfigElement.selectNodes("xwc:components",nr);
            for( int i=0; i < oComponentsNodeList.getLength(); i++ ) {
            	XMLElement	element = (XMLElement)oComponentsNodeList.item( i );
            	String nameSpace = element.getAttribute("namespace");
            	parseComponents( oComponentStore, nameSpace, element );

            }

            // Add render Kits
            XMLElement oRenderKitsElement   = (XMLElement)oConfigElement.selectSingleNode("xwc:renderKits",nr);
            if( oRenderKitsElement != null ) {
            	parseRenderKits( oComponentStore, oRenderKitsElement );
            }
            
            // Add component Renders
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
            String sComponentNamespace;

            NodeList    oRendersNodeList;
            XMLElement  oRenderElement;
            XUIComponentDefinition oComponent;
            
            
            NodeList listRenderKits = oRenderKitsElement.selectNodes("xwc:renderKit",nr);
            for (int i = 0; i < listRenderKits.getLength(); i++)  
            {
                XMLElement oRenderKitElement = (XMLElement)listRenderKits.item( i );    
                
                sRenderKitName 		= oRenderKitElement.getAttribute("name");
                sComponentNamespace = oRenderKitElement.getAttribute("componentNamespace");
                
                oRendersNodeList = oRenderKitElement.selectNodes("xwc:render",nr);
                
                for (int j = 0; j < oRendersNodeList.getLength(); j++)  {
                    
                    oRenderElement = (XMLElement)oRendersNodeList.item( j );
                    
                    sRenderFor = oRenderElement.getAttribute( "for" );
                    
                    assert sRenderFor != null:"for attribute must be defined in element";
                    oComponent = oComponents.getComponent( sComponentNamespace +":" + sRenderFor );
                    
                    if( oComponent != null )
                    {
                        sRenderClassName = oRenderElement.getText();
                        oComponent.addRenderKit( sRenderKitName, sRenderClassName );
                    }
                    else{
                        if ( log.isLoggable( LoggerLevels.FINER ) ){
                            log.warn("Component id ["+sRenderFor+"] not found parsing renderKit" );
                        }
                    }
                }
            }
            
        } 
        finally 
        {
        
        }

    }

    private XUIComponentStore parseComponents( XUIComponentStore oComponents, String namespace, XMLElement xcomponents ) throws Exception
    {
        try
        {
            NodeList list = xcomponents.selectNodes("xwc:component",nr);
            for (int i = 0; i < list.getLength(); i++) 
            {
                oComponents.registerComponent(
                    namespace, parseComponent( namespace, (XMLElement)list.item( i ) )
                );
            }
            return oComponents;
        }
        catch (XSLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private XUIComponentDefinition parseComponent( String namespace, XMLElement xcomponent ) throws Exception
    {

        XUIComponentDefinition component = new XUIComponentDefinition();
        component.setName( xcomponent.getAttribute("name") );
        component.setNameSpace( namespace );
        component.setClassName( getNodeText( xcomponent, "xwc:className" ) );
        component.setDescription( getNodeText( xcomponent, "xwc:description" ) );
        return component;
    }
     
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
            return "http://www.netgest.net/xeo/" + prefix;
        }
    }
    
}
