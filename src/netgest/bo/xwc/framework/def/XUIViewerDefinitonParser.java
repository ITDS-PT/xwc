package netgest.bo.xwc.framework.def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;
import netgest.utils.StringUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XUIViewerDefinitonParser
{
	
	private static final String DEFAULT_VIEWERS_ROOT = "viewers";
	public static final String DEFAULT_BEAN_ID = "viewBean";
    private static NSResolver ns = new GenericResolver();
    private static HashMap<String, XUIViewerDefinition> viewCache = new HashMap<String, XUIViewerDefinition>();
    private static final String EMPTY = "";
    
    public XUIViewerDefinitonParser(){
    	
    }
    
    public XUIViewerDefinition parse( InputStream inputStream ) {
    	return parse(inputStream, new HashSet<String>());
    }
    
    public XUIViewerDefinition parse( InputStream inputStream, Set<String> viewersAlreadyParsed, String viewerName ) {
    	XUIViewerDefinition xwvr;
        try
        {
            XMLElement node;
            XMLDocument xmldoc;
            xmldoc = ngtXMLUtils.loadXML( inputStream );

            node = (XMLElement)xmldoc.selectSingleNode("/xvw:root/xvw:viewer", ns);
            xwvr = new XUIViewerDefinition();

            parseBeanClasses( xwvr, node.getAttribute( "beanClass" ) );
            parseBeanIds( xwvr, node.getAttribute( "beanId" ) );
            
            xwvr.setRenderKitId( node.getAttribute( "renderKitId" ) ); 

            xwvr.addOnRestoreViewPhase( node.getAttribute( "onRestoreViewPhase" ) );
            xwvr.addOnCreateViewPhase( node.getAttribute( "onCreateViewPhase" ) );

            xwvr.addBeforeApplyRequestValuesPhase( node.getAttribute( "beforeApplyRequestValuesPhase" ) );
            xwvr.addAfterApplyRequestValuesPhase( node.getAttribute( "afterApplyRequestValuesPhase" ) );
            
            xwvr.addBeforeUpdateModelPhase( node.getAttribute( "beforeUpdateModelPhase" ) );
            xwvr.addAfterUpdateModelPhase( node.getAttribute( "afterUpdateModelPhase" ) );
            
            xwvr.addBeforeRenderPhase( node.getAttribute( "beforeRenderPhase" ) );
            xwvr.addAfterRenderPhase( node.getAttribute( "afterRenderPhase" ) );
            
            String localizationClasses = node.getAttribute( "localizationClasses" );
            
            if( localizationClasses != null && localizationClasses.length() > 0 ) {
            	xwvr.setLocalizationClasses( localizationClasses.split(",") );
            }
            
            String isTransient = node.getAttribute("transient");
            if( isTransient != null ) {
            	xwvr.setTransient( Boolean.valueOf( isTransient ) );
            }
            
            xwvr.setRootComponent( parseNode( xwvr, (XMLElement)node, null, viewersAlreadyParsed ) );
            
        }
        catch (Exception e)
        {
        	throw new RuntimeException( "Viewer " + viewerName + " "  +  e );
        }
        finally {
        }
        return xwvr;
    }
    
    public XUIViewerDefinition parse( InputStream inputStream, Set<String> viewersAlreadyParsed ) {
        return parse( inputStream , viewersAlreadyParsed , EMPTY );
    }
    
    private void parseBeanClasses(XUIViewerDefinition vdef, String beanClasses){
    	String[] beans = beanClasses.split( "," );
    	for (String bean : beans){
    		if ( !StringUtils.isEmpty( bean ) )
    			vdef.addViewerBean( bean );
    	}
    }
    
    private void parseBeanIds( XUIViewerDefinition vdef, String beanIds ){
    	String[] beanIdentifiers = beanIds.split( "," );
    	for (String beanId : beanIdentifiers){
    		vdef.addViewerBeanId( beanId );
    	}
    }
    
    public XUIViewerDefinition parse( String viewerName ){
    	return parse( viewerName, new HashSet<String>() );
    }
    
    /**
     * 
     * Parses a viewer definition from a filename
     * 
     * @param viewerName The path to the viewer
     * @param viewersParsed A list of already parsed viewers (to check for cyclic references)
     * 
     * @return The viewer definition
     */
    public XUIViewerDefinition parse( String viewerName, Set<String> viewersParsed ) 
    {
        XUIViewerDefinition xwvr;
        
        xwvr = null;//viewCache.get( viewerName );
        if( xwvr == null ) {
            
        	InputStream is = resolveViewer( viewerName );
            
            if( is != null )
            {
            	try {
            		xwvr = parse( is , viewersParsed, viewerName );
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
                throw new RuntimeException(
                		XUICoreMessages.VIEWER_NOTFOUND.toString( viewerName )
                	);
            }
        }
        return xwvr;
    }
    
    /**
     * 
     * Checks whether a given viewer was already parsed in the processing of this viewers
     * 
     * @param viewerName The viewer name
     * @return
     */
    private boolean wasViewerAlreadyParsed(String viewerName, Set<String> parsed){
    	return parsed.contains( viewerName );
    }

    public InputStream resolveViewer( String viewerName ) {
    	InputStream is = resolveViewerFromWebContext( viewerName );
    	if( is == null ) {
    		is = resolveViewerFromClassLoader( viewerName );
    	}
    	return is;
    }
    
    private InputStream resolveViewerFromClassLoader( String viewerName ) {
    	
    	ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    	
     	InputStream is = null;
    	
    	if( is == null ) {
    		is = contextClassLoader.getResourceAsStream( viewerName );
    	}
    	
    	if ( is == null)
    		is  = this.getClass().getResourceAsStream( viewerName );

    	if( is == null ) {
			int underScoreIdx = viewerName.lastIndexOf( '_' );
			if( underScoreIdx != -1 ) {
				char[] v = viewerName.toCharArray();
				v[ underScoreIdx ] = '/';
				String sviewId = DEFAULT_VIEWERS_ROOT + "/" + String.valueOf( v );
				is = contextClassLoader.getResourceAsStream( sviewId );
			}
    	}
    	
    	if( is == null ) {
    		
    		if( viewerName.startsWith( DEFAULT_VIEWERS_ROOT ) ) {
    			viewerName = viewerName.substring( DEFAULT_VIEWERS_ROOT.length() + 1 );
    		}
    		
	    	int slashIdx = viewerName.lastIndexOf( '/' );
			if( slashIdx != -1 ) {
				char[] v = viewerName.toCharArray();
				v[ slashIdx ] = '_';
				String sviewId = String.valueOf( v );
				is = contextClassLoader.getResourceAsStream( sviewId );
			}
    	}
		
    	return is;
    }
    
    private InputStream resolveViewerFromWebContext( String viewerName ) {
    	
    	File viewerFile;
    	 
    	viewerFile = null; 
    	
    	XUIRequestContext oReqCtx = XUIRequestContext.getCurrentContext();
    	  
    	ServletContext servletContext = 
    		(ServletContext)oReqCtx.getFacesContext().getExternalContext().getContext();
    	
    	String sRealPath = servletContext.getRealPath( viewerName );
    	if( sRealPath != null ) {
    		viewerFile = new File( sRealPath );
        	if( !viewerFile.exists() ) {
            	sRealPath = servletContext.getRealPath( DEFAULT_VIEWERS_ROOT + File.separator + viewerName );
        		viewerFile = new File( sRealPath );
            	if( !viewerFile.exists() ) {
            		viewerFile = null;
            	}
        	}
    	} 
    	if( viewerFile != null ) {
    		try {
				return new FileInputStream( viewerFile );
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
    	}
    	return null;
    }
    
    public XUIViewerDefinitionNode parseNode( XUIViewerDefinition root, XMLElement node, XUIViewerDefinitionNode parent ){
    	return parseNode(root, node, parent, new HashSet<String>());
    }
    
    public XUIViewerDefinitionNode parseNode( XUIViewerDefinition root, XMLElement node, XUIViewerDefinitionNode parent, Set<String> alreadyParsedViewers )
    {
        XUIViewerDefinitionNode component = new XUIViewerDefinitionNode();
        component.setRoot( root );
        component.setParent( parent );
        
        if ( isIncludeComponent( node ) ){
        	return replaceIncludeContent( root, node, parent, alreadyParsedViewers );
        }
        
        if( node.getNodeName().indexOf(':') == -1 )
        {
            component.setName("xvw:genericTag");

            LinkedHashMap<String, String> propertiesMap = new LinkedHashMap<String, String>();
            propertiesMap.put("__tagName", node.getNodeName() );
            
            NamedNodeMap attNodeMap = node.getAttributes();
            int iAttsLength = attNodeMap.getLength();

            for( int i=0; i < iAttsLength; i++ ) {
            	Node attr = attNodeMap.item( i );
            	propertiesMap.put( attr.getNodeName(), getLocalizedMessage( root, attr.getNodeValue() ) );
            }
            component.setProperties( propertiesMap );
        }
        else
        {
            component.setName( node.getNodeName() );
        }
        
        setComponentBeanIdPropertyForNonDefaultBean(component, root);
        
        NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) 
        {
        	Node xnode = atts.item( i );
            if( "id".equalsIgnoreCase( xnode.getNodeName() ) ) {
                component.setId( xnode.getNodeValue() );   
            }
            else  {
            	component.setProperty( xnode.getNodeName(), getLocalizedMessage( root, xnode.getNodeValue() ) );
            }
        }
        
        
        NodeList nlist = node.getChildNodes();
        for (int i = 0; i < nlist.getLength(); i++) 
        {
            Node cnode = nlist.item( i );
            if( cnode.getNodeType() == Node.ELEMENT_NODE )
            {
                component.addChild( parseNode( root, (XMLElement)nlist.item( i ), component, alreadyParsedViewers ) );
            } 
            else if ( cnode.getNodeType() == Node.TEXT_NODE ) 
            {
            	XUIViewerDefinitionNode childText = new XUIViewerDefinitionNode();
            	childText.setName("xvw:genericTag");
            	childText.setTextContent( getLocalizedMessage( root, cnode.getNodeValue() ) );
            	component.addChild( childText );
            }
        }
        return component; 
    }
    
    private void setComponentBeanIdPropertyForNonDefaultBean( XUIViewerDefinitionNode component, XUIViewerDefinition viewerDef ){
    	String beanId = component.getProperty("beanId");
    	if ( StringUtils.isEmpty( beanId ) ){
    		if ( viewerHasOneBeanIdentifier( viewerDef ) ) {
    			String viewerBeanIdentifier = getBeanIdentifier( viewerDef );
    			if ( !viewerBeanIdentifier.equalsIgnoreCase( DEFAULT_BEAN_ID ) ){
    				component.setProperty("beanId",viewerBeanIdentifier);
    			}
    		}
    	}
    }
    
    private String getBeanIdentifier( XUIViewerDefinition def ){
    	if (def.getViewerBeanIds().size() > 0)
    		return def.getViewerBeanIds().get( 0 );
    	return "";
    }
    
    private boolean viewerHasOneBeanIdentifier( XUIViewerDefinition def ){
    	return def.getViewerBeanIds().size() == 1 
    	 && !StringUtils.isEmpty(def.getViewerBeanIds().get(0));
    }
    private boolean isIncludeComponent( XMLElement node ){
    	return node.getNodeName().equalsIgnoreCase("xvw:include");
    }
    
    private XUIViewerDefinitionNode replaceIncludeContent( XUIViewerDefinition def,  XMLElement node, XUIViewerDefinitionNode parent, Set<String> parsed ){
    	
    	String includeFilePath = node.getAttribute( "src" );
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	
        ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
        ValueExpression m = oExFactory.createValueExpression( context.getELContext(), includeFilePath, String.class);
        
        if ( !m.isLiteralText() ){
        	includeFilePath = (String)m.getValue( context.getELContext() );
        	if ("".equalsIgnoreCase(includeFilePath))
        		throw new RuntimeException( String.format("The expression %s does not resolve to a viewer path to include", m.getExpressionString() ) );
        }
        
    	
    	if (wasViewerAlreadyParsed(includeFilePath, parsed))
    		throw new RuntimeException( String.format("Cyclic reference: %s was already processed", includeFilePath ) );
    	parsed.add( includeFilePath );
    	
    	XUIViewerDefinition included =  this.parse( includeFilePath , parsed );
    	
    	/*String rootComponentName = included.getRootComponent().getName();
    	if (!rootComponentName.contains( "composition" ) ){
    		throw new RuntimeException( String.format( "Cannot include a viewer that's not a xvw:composition fragment: %s", includeFilePath ) );
    	}*/
    	
    	addBeansToViewerDefinition( def , included.getViewerBeans() );
    	addBeanIdsToViewerDefinition( def , included.getViewerBeanIds() );
    	addEventsFromIncludedViewer( def, included );
    	
    	XUIViewerDefinitionNode wrapper = wrapInclusion( def, included.getRootComponent().getChildren().get( 0 ).getChildren(), parent );
    	return wrapper;
    }
    
    private void addBeansToViewerDefinition( XUIViewerDefinition toAdd, List<String> beans ){
    	Iterator<String> it = beans.iterator();
    	while ( it.hasNext() ){
    		toAdd.addViewerBean( it.next() );
    	}
    }
    
    private void addBeanIdsToViewerDefinition( XUIViewerDefinition toAdd, List<String> beanIds ){
    	Iterator<String> it = beanIds.iterator();
    	while ( it.hasNext() ){
    		toAdd.addViewerBeanId( it.next() );
    	}
    }
    
    private void addEventsFromIncludedViewer( XUIViewerDefinition mainViewerDefinition, XUIViewerDefinition includedViewer ){
    	
    	mainViewerDefinition.addOnRestoreViewPhase( includedViewer.getOnRestoreViewPhaseList() );
    	mainViewerDefinition.addOnCreateViewPhase( includedViewer.getOnCreateViewPhaseList() );
    	
    	mainViewerDefinition.addBeforeApplyRequestValuesPhase( includedViewer.getBeforeApplyRequestValuesPhaseList() );
    	mainViewerDefinition.addAfterApplyRequestValuesPhase( includedViewer.getAfterApplyRequestValuesPhaseList() );
    	
    	mainViewerDefinition.addBeforeUpdateModelPhase( includedViewer.getBeforeUpdateModelPhaseList() );
    	mainViewerDefinition.addAfterUpdateModelPhase( includedViewer.getAfterUpdateModelPhaseList() );
    	
    	mainViewerDefinition.addBeforeRenderPhase( includedViewer.getBeforeRenderPhaseList() );
    	mainViewerDefinition.addAfterRenderPhase( includedViewer.getAfterRenderPhaseList() );
    	
    }
    
    private XUIViewerDefinitionNode wrapInclusion( XUIViewerDefinition def, List<XUIViewerDefinitionNode> result , XUIViewerDefinitionNode parent ){
    	XUIViewerDefinitionNode wrapper = new XUIViewerDefinitionNode();
    	wrapper.setRoot( def );
    	wrapper.setParent( parent );
    	wrapper.setName( "xvw:genericTag" );
    	
    	for (XUIViewerDefinitionNode child : result){
    		wrapper.addChild(child);
    	}
    	return wrapper;
    }
    
    //END TESTING
    
	private static String getLocalizedMessage( XUIViewerDefinition vwrDef, String message ) {
    	Pattern p = Pattern.compile("\\@\\{([a-zA-Z0-9_-]{1,})\\}");
    	Matcher m = p.matcher( message );
    	if( m.find() ) {
    		
        	boolean found = false;
        	
    		String[] localizationClasses = vwrDef.getLocalizationClasses();
    		if( localizationClasses != null ) {
	    		for( String localizationClass : localizationClasses  ) {
		    		if( localizationClass != null && localizationClass.trim().length() > 0 ) {
		    			try {
							String 	fieldName = m.group(1); 
							Class<?> 	classInst = Class.forName( localizationClass );
							try {
								Field field = classInst.getField( fieldName );
								message = field.get( null ).toString();
								found = true;
								break;
							} catch ( Exception e ) {
							}
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
		    		}
	    		}
	    		if( !found ) {
	    			StringBuilder sb = new StringBuilder();
	        		for( String localizationClass : localizationClasses  ) {
	    	    		if( localizationClass != null && localizationClass.trim().length() > 0 ) {
	    	    			if( sb.length() > 0 )
	    	    				sb.append( ',' );
	    	    				
		    				sb.append( localizationClass );
	    	    		}
	        		}
	    			throw new RuntimeException( ExceptionMessage.CANNOT_FIND_RESOURCE.toString()+" [" + message + "] "+ExceptionMessage.ON_LOCATION_CLASSES.toString()+" [" + sb.toString() + "]" );
	    		}
    		}
    	}
    	return message;
    }
    
    
    private static class GenericResolver implements NSResolver
    {
        public String resolveNamespacePrefix( String prefix )
        {
            return "http://www.netgest.net/xeo/xvw";
        }
    }


	public void replaceBean( String newBean ) {
		
	}
    
}
