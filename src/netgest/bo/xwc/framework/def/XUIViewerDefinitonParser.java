package netgest.bo.xwc.framework.def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import netgest.bo.system.Logger;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;
import netgest.utils.StringUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XUIViewerDefinitonParser
{
	
	private static final Logger logger = Logger
			.getLogger( XUIViewerDefinitonParser.class );
	
	public static final String DEFAULT_VIEWERS_ROOT = "viewers";
	public static final String DEFAULT_BEAN_ID = "viewBean";
    private static NSResolver ns = new GenericResolver();
    private static HashMap<String, XUIViewerDefinition> viewCache = new HashMap<String, XUIViewerDefinition>();
    private static final String EMPTY = "";
    
    public XUIViewerDefinitonParser(){
    }
    
    
    public XUIViewerDefinition parse( InputStream inputStream ) {
    	return parse(inputStream, new IncludeCounter());
    }
    
    public XUIViewerDefinition parse( InputStream inputStream, IncludeCounter counter, String viewerName ) {
    	XUIViewerDefinition xwvr;
        try
        {
            XMLElement node;
            XMLDocument xmldoc;
            xmldoc = ngtXMLUtils.loadXML( inputStream );

            node = (XMLElement)xmldoc.selectSingleNode("/xvw:root/xvw:viewer", ns);
            
            XMLElement element = (XMLElement) node;
            
            Map<String,XMLElement> defines = new HashMap< String , XMLElement >();
            
            xwvr = new XUIViewerDefinition();
            
            String isTransient = node.getAttribute("transient");
            boolean originalHasTransient = false;
            if( isTransient != null ) {
            	originalHasTransient = true;
            	xwvr.setTransient( Boolean.valueOf( isTransient ) );
            }
            
            //Deal with a Composition 
            String currentBeanId = null;
            
            XMLElement potentialComposition = findPotentialCompositionElement( element );
            if ( isPageComposition( potentialComposition ) ){
            	defines = findDefineElements( potentialComposition );
            	String templateToParse = potentialComposition.getAttribute( "template" );
            	parseBeanClasses( xwvr, node.getAttribute( "beanClass" ) );
                parseBeanIds( xwvr, node.getAttribute( "beanId" ) );
                currentBeanId = getBeanId( xwvr );
                ViewerXMLWrapper wrapper =  reloadViewerFromTemplate( templateToParse );
            	node = wrapper.getNode();
            	//Set date
            	xwvr.setDateLastUpdate( wrapper.getTime() );
            	if (!originalHasTransient){
	            	isTransient = node.getAttribute("transient");
	                if( isTransient != null ) {
	                	xwvr.setTransient( Boolean.valueOf( isTransient ) );
	                }
            	}
            }

            parseBeanClasses( xwvr, node.getAttribute( "beanClass" ) );
            parseBeanIds( xwvr, node.getAttribute( "beanId" ) );
            currentBeanId = getBeanId( xwvr ); 
            
            List<String> beanIds = xwvr.getViewerBeanIds( );
            for (String beanId : beanIds){
            	if (counter.wasBeanIdFound( beanId ))
            		throw new RuntimeException( String.format("Duplicate bean id %s in viewer %s", beanId, viewerName ) );
            }
            
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
            	String[] classes = localizationClasses.split(",");
            	for (int i = 0; i < classes.length; i++ ){
            		classes[i] = classes[i].trim();
            	}
            	xwvr.setLocalizationClasses( classes );
            }
            
            
            
            counter.parsed( viewerName , xwvr.getViewerBeanIds( ) );
            
            xwvr.setRootComponent( parseNode( xwvr, (XMLElement)node, null, counter, defines, currentBeanId ) );
            
        }
        catch (Exception e)
        {
        	throw new RuntimeException( "Viewer " + viewerName + " "  +  e, e );
        }
        finally {
        }
        return xwvr;
    }


	protected String getBeanId(XUIViewerDefinition xwvr) {
		if (xwvr.getViewerBeanIds().isEmpty() && StringUtils.isEmpty( xwvr.getViewerBeanId() ) ){
			return "";
		} else {
			if (xwvr.getViewerBeanIds().size() > 0)
				return xwvr.getViewerBeanIds().get( 0 );
			if ( StringUtils.hasValue( xwvr.getViewerBeanId() ) )
				return xwvr.getViewerBeanId();
		}
		return "";
	}


	protected XMLElement findPotentialCompositionElement(XMLElement element) {
		Node node = element.getChildNodes().item( 0 );
		if (node instanceof XMLElement)
			return (XMLElement) node;
		
		return null;
	}


	protected boolean isPageComposition(XMLElement potentialComposition) {
		if (potentialComposition == null)
			return false;
		return "xvw:composition".equalsIgnoreCase( potentialComposition.getNodeName() );
	}


	protected ViewerXMLWrapper reloadViewerFromTemplate(String templateToParse)
			throws XSLException {
		XMLElement node;
		XMLDocument xmldoc;
		
		//Ver aqui o resolver da expressao
		FacesContext context = FacesContext.getCurrentInstance();
    	
        ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
        ValueExpression m = oExFactory.createValueExpression( context.getELContext(), templateToParse, String.class);
        
        if ( !m.isLiteralText() ){
        	templateToParse = (String)m.getValue( context.getELContext() );
        	if ("".equalsIgnoreCase(templateToParse))
        		throw new RuntimeException( String.format("The expression %s does not resolve to a viewer path to include", m.getExpressionString() ) );
        }
		
		StreamWrapper wrapper = resolveViewerWithTime( templateToParse );
		xmldoc =  ngtXMLUtils.loadXML( wrapper.getInputStream() );
		node = (XMLElement)xmldoc.selectSingleNode( "/xvw:root/xvw:viewer" , ns);
		return new ViewerXMLWrapper( node , wrapper.getTime() );
	}
	
	private class ViewerXMLWrapper{
		
		private XMLElement node;
		private Timestamp time;
		public ViewerXMLWrapper(XMLElement node, Timestamp time) {
			super();
			this.node = node;
			this.time = time;
		}
		public XMLElement getNode() {
			return node;
		}
		public Timestamp getTime() {
			return time;
		}
		
	}
    
    private Map< String , XMLElement > findDefineElements(XMLElement element) {
    	Map<String,XMLElement> result = new HashMap< String , XMLElement >();
    	NodeList children = element.getChildNodes();
    	for (int k = 0 ; k < children.getLength(); k++){
    		XMLElement child = (XMLElement) children.item( k );
    		if ("xvw:define".equals(child.getNodeName())){
    			result.put( child.getAttribute( "name" ) , child );
    		}
    	}
    	return result;
	}

	public XUIViewerDefinition parse( InputStream inputStream, IncludeCounter counter ) {
        return parse( inputStream , counter , EMPTY );
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
    		if (StringUtils.hasValue( beanId ))
    			vdef.addViewerBeanId( beanId );
    	}
    }
    
    public XUIViewerDefinition parse( String viewerName ){
    	return parse( viewerName, new IncludeCounter( ) );
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
    public XUIViewerDefinition parse( String viewerName, IncludeCounter counter ) 
    {
        XUIViewerDefinition xwvr;
        
        xwvr = null;//viewCache.get( viewerName );
        if( xwvr == null ) {
            
        	StreamWrapper wrapper = resolveViewerWithTime( viewerName );
            InputStream is = null;
            if( wrapper != null )
            {
            	try {
            		is = wrapper.getInputStream();
            		if (is == null)
            			throw new RuntimeException( String.format("Viewer %s does not exist",viewerName) );
            		xwvr = parse( is , counter, viewerName );
            		if (xwvr.getDateLastUpdate() != null){
	            		if (xwvr.getDateLastUpdate().before( wrapper.getTime() ))
	            			xwvr.setDateLastUpdate( wrapper.getTime() );
            		} else {
            			xwvr.setDateLastUpdate( wrapper.getTime() );
            		}
            		viewCache.put( viewerName, xwvr );
            	} catch (Exception e ){
	            		logger.warn( "Could not parse %s", e , viewerName );
	            		throw new RuntimeException( e );
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
    private boolean wasViewerAlreadyParsed(String viewerName, IncludeCounter counter){
    	return counter.wasViewerParsed( viewerName );
    }

    public StreamWrapper resolveViewerWithTime( String viewerName ) {
    	StreamWrapper is = resolveViewerFromWebContext( viewerName,false );
    	if( is == null ) {
    		is = resolveViewerFromClassLoader( viewerName );
    	}
    	return is;
    }
    
    public InputStream resolveViewer( String viewerName ) {
    	return resolveViewerWithTime(viewerName).getInputStream();
    }
    
    private Timestamp getLastDateOfResource(String viewerName, ClassLoader loader){
    	URLConnection connection = null;
    	try {
    		URL url = loader.getResource( viewerName );
    		if (url != null){
	    		connection = url.openConnection(); 
				Long time = connection.getLastModified();
				return new Timestamp( time );
    		} else 
    			return new Timestamp( System.currentTimeMillis() );
		} catch ( IOException e ) {
			return new Timestamp( System.currentTimeMillis() );
		} finally {
			try {
				if (connection != null)
					connection.getInputStream().close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
    }
    
    private StreamWrapper resolveViewerFromClassLoader( String viewerName ) {
    	
    	ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    	
     	InputStream is = null;
     	Timestamp time = null;
    	
    	is = contextClassLoader.getResourceAsStream( viewerName );
    	if (is != null){
    		time = getLastDateOfResource( viewerName , contextClassLoader );
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
				if (is != null)
					time = getLastDateOfResource( viewerName , contextClassLoader );
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
				if (is != null)
					time = getLastDateOfResource( viewerName , contextClassLoader );
			}
    	}
		
    	return new StreamWrapper(is,time);
    }
    
    private StreamWrapper resolveViewerFromWebContext( String viewerName, boolean fromModule ) {
    	
    	File viewerFile;
    	String xeodeploy=".xeodeploy" + File.separator; 
    	viewerFile = null; 
    	
    	XUIRequestContext oReqCtx = XUIRequestContext.getCurrentContext();
    	  
    	ServletContext servletContext = 
    		(ServletContext)oReqCtx.getFacesContext().getExternalContext().getContext();
    	
    	String sRealPath = servletContext.getRealPath(fromModule?xeodeploy+viewerName:viewerName );
    	if( sRealPath != null ) {
    		viewerFile = new File( sRealPath );
    		if( !viewerFile.exists() ) {
    			viewerFile = null;
    	    } 
    	}
    	
    	if( viewerFile == null ) {
    		String path=DEFAULT_VIEWERS_ROOT + File.separator + viewerName;
    		if (fromModule) {
    			path = xeodeploy+path;
    		}
        	sRealPath = servletContext.getRealPath( path );
        	if( sRealPath != null ) {
        		viewerFile = new File( sRealPath );
            	if( !viewerFile.exists() ) {
            		viewerFile = null;
            	}
        	}
    	}
    	
    	if( viewerFile != null ) {
    		try {
    			Timestamp time = new Timestamp( viewerFile.lastModified() );
				return new StreamWrapper(new FileInputStream( viewerFile ), time);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
    	}
    	if (!fromModule) {
    		return resolveViewerFromWebContext(viewerName, true);
    	}
    	else { 
    		return null;
    	}
    }
    
    public XUIViewerDefinitionNode parseNode( XUIViewerDefinition root, XMLElement node, XUIViewerDefinitionNode parent, String beanId ){
    	return parseNode(root, node, parent, new IncludeCounter(), new HashMap<String,XMLElement>(), beanId);
    }
    
    public XUIViewerDefinitionNode parseNode( XUIViewerDefinition root, XMLElement node, 
    		XUIViewerDefinitionNode parent, IncludeCounter counter, Map<String,XMLElement> defines, String beanId )
    {
        XUIViewerDefinitionNode component = new XUIViewerDefinitionNode();
        component.setRoot( root );
        component.setParent( parent );
        
        if ( isIncludeComponent( node ) ){
        	return replaceIncludeContent( root, node, parent, counter, defines );
        } else if (isInsertComponent( node ) ){
        	String name = node.getAttribute( "name" );
        	if (defines.containsKey( name )){
    			XMLElement element = defines.get( name );
    			return replaceInsertContent(root, element, parent, defines, beanId);
    		} else {
    			if ( StringUtils.hasValue( node.getAttribute( "src" ) ) ){
    				return replaceIncludeContent( root, node, parent, counter, defines );
    			}
    			else{
    				XUIViewerDefinitionNode newNode = new XUIViewerDefinitionNode();
    				newNode.setName( "xvw:genericTag" );
				    NodeList nlist = node.getChildNodes();
			        for (int i = 0; i < nlist.getLength(); i++) 
			        {
			            Node cnode = nlist.item( i );
			            if( cnode.getNodeType() == Node.ELEMENT_NODE )
			            {
			            	newNode.addChild( parseNode( root, (XMLElement)nlist.item( i ), component, counter, defines, beanId ) );
			            } 
			            else if ( cnode.getNodeType() == Node.TEXT_NODE ) 
			            {
			            	XUIViewerDefinitionNode childText = new XUIViewerDefinitionNode();
			            	childText.setName("xvw:genericTag");
			            	childText.setTextContent( getLocalizedMessage( root, cnode.getNodeValue() ) );
			            	childText.setRawContent( true );
			            	newNode.addChild( childText );
			            	
			            }
			        }
    				return newNode;
    			}
    		}
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
        
        setComponentBeanIdPropertyForNonDefaultBean( component, beanId );
        
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
                component.addChild( parseNode( root, (XMLElement)nlist.item( i ), component, counter, defines, beanId ) );
            } 
            else if ( cnode.getNodeType() == Node.TEXT_NODE ) 
            {
            	XUIViewerDefinitionNode childText = new XUIViewerDefinitionNode();
            	childText.setName("xvw:genericTag");
            	childText.setTextContent( getLocalizedMessage( root, cnode.getNodeValue() ) );
            	childText.setRawContent( true );
            	component.addChild( childText );
            	
            }
        }
        return component; 
    }
    
    private XUIViewerDefinitionNode replaceInsertContent(
			XUIViewerDefinition root, XMLElement element,
			XUIViewerDefinitionNode parent, Map< String , XMLElement > defines, String beanId) {
    	
    	List<XUIViewerDefinitionNode> nodes = new ArrayList< XUIViewerDefinitionNode >();
    	NodeList children = element.getChildNodes();
    	for (int k = 0 ; k < children.getLength(); k++){
    		Node child = children.item( k );
    		nodes.add( parseNode( root , (XMLElement) child , parent, beanId ) );
    	}
		return wrapInclusion( root , nodes , parent );
	}

	private boolean isInsertComponent(XMLElement node) {
		return "xvw:insert".equalsIgnoreCase( node.getNodeName() );
	}

	private void setComponentBeanIdPropertyForNonDefaultBean( XUIViewerDefinitionNode component, String otherBeanId ){
    	String beanId = component.getProperty( "beanId" );
    	if ( StringUtils.isEmpty( beanId ) && StringUtils.hasValue( otherBeanId )){
			if ( !DEFAULT_BEAN_ID.equalsIgnoreCase( otherBeanId ) ){
				component.setProperty( "beanId" , otherBeanId );
			}
    	}
    }
    
    
    private boolean isIncludeComponent( XMLElement node ){
    	return "xvw:include".equalsIgnoreCase( node.getNodeName());
    }
    
    private XUIViewerDefinitionNode replaceIncludeContent( XUIViewerDefinition def,  XMLElement node, XUIViewerDefinitionNode parent, 
    		IncludeCounter parsedCounter, Map<String,XMLElement> defines ){
    	
    	String includeFilePath = node.getAttribute( "src" );
    	
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	
        ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
        ValueExpression m = oExFactory.createValueExpression( context.getELContext(), includeFilePath, String.class);
        
        if ( !m.isLiteralText() ){
        	includeFilePath = (String)m.getValue( context.getELContext() );
        	if ("".equalsIgnoreCase(includeFilePath))
        		throw new RuntimeException( String.format("The expression %s does not resolve to a viewer path to include", m.getExpressionString() ) );
        }
        
    	
    	if (wasViewerAlreadyParsed(includeFilePath, parsedCounter))
    		throw new RuntimeException( String.format("Cyclic reference: %s was already processed", includeFilePath ) );
    	
    	
    	XUIViewerDefinition included =  this.parse( includeFilePath , parsedCounter );
    	//Update date if included file was changed after the current viewer
    	if (def.getDateLastUpdate() != null ){
    		if (included.getDateLastUpdate() != null){
    			if (def.getDateLastUpdate().before( included.getDateLastUpdate() )){
    	    		def.setDateLastUpdate( included.getDateLastUpdate() );
    	    	} 
    		}
    	} else {
    		if (included.getDateLastUpdate() != null){
    			def.setDateLastUpdate( included.getDateLastUpdate() );
    		}
    	}
    	
    	
    	
    	parsedCounter.parsed( includeFilePath, included.getViewerBeanIds( ) );
    	
    	
    	addBeansToViewerDefinition( def , included.getViewerBeans() );
    	addBeanIdsToViewerDefinition( def , included.getViewerBeanIds() );
    	addEventsFromIncludedViewer( def, included );
    	
    	//XUIViewerDefinitionNode wrapper = wrapInclusion( def, included.getRootComponent().getChildren().get( 0 ).getChildren(), parent );
    	List<XUIViewerDefinitionNode> listToWrap = findSuitableRootToInclude( included.getRootComponent() );
    	XUIViewerDefinitionNode wrapper = wrapInclusion( def, listToWrap, parent );
    	return wrapper;
    }
    
    private List<XUIViewerDefinitionNode> findSuitableRootToInclude(XUIViewerDefinitionNode root){
    	String rootName = root.getName();
    	if ("xvw:root".equalsIgnoreCase( rootName ) || "xvw:viewer".equalsIgnoreCase( rootName )){
    		if (root.getChildren().size() == 1)
    			return findSuitableRootToInclude( root.getChildren().get( 0 ) );
    		else
    			return root.getChildren();
    	} else if ("xvw:composition".equalsIgnoreCase( rootName ) || "xvw:container".equalsIgnoreCase( rootName )  )
    		return root.getChildren();
    	else if (StringUtils.hasValue( rootName )){
    		 List<XUIViewerDefinitionNode> result = new ArrayList< XUIViewerDefinitionNode >();
    		 result.add( root );
    		 return result;
    	}
    	else
    		return new ArrayList< XUIViewerDefinitionNode >();
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
	
	private static class IncludeCounter{
		
		private Map<String,List<String>> parsed;
		
		public IncludeCounter(){
			parsed = new HashMap< String , List<String> >( );
		}
		
		public void parsed(String viewer, List<String> beanIds){
			parsed.put( viewer , beanIds );
		}
		
		public boolean wasViewerParsed(String viewer){
			return parsed.containsKey( viewer );
		}
		
		public boolean wasBeanIdFound(String beanIdToCheck){
			Iterator<List<String>> it = parsed.values( ).iterator( );
			while (it.hasNext()){
				List<String> current = it.next();
				for (String beanId : current){
					if (beanId.equals( beanIdToCheck ))
						return true;
				}
			}
			return false;
		}
		
	}
	
	private class StreamWrapper {
		
		private InputStream is;
		private Timestamp time;
		
		public StreamWrapper(InputStream is, Timestamp ts){
			this.is = is;
			this.time = ts;
			if (this.time == null)
				this.time = new Timestamp( System.currentTimeMillis() );
		}

		public InputStream getInputStream() {
			return is;
		}

		public Timestamp getTime() {
			return time;
		}
		
		
		
		
	}
    
}
