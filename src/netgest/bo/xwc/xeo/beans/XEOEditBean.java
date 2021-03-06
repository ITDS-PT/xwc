package netgest.bo.xwc.xeo.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.preferences.Preference;
import netgest.bo.preferences.PreferenceManager;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boThread;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.components.classic.AttributeAutoComplete;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.classic.MessageBox;
import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.Tabs;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWScripts.WaitMode;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.ExportMenu;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIEditableValueHolder;
import netgest.bo.xwc.framework.XUIErrorLogger;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.messages.XUIMessageSender;
import netgest.bo.xwc.framework.messages.XUIPopupMessageFactory;
import netgest.bo.xwc.xeo.beans.lookup.SplitLookupSearchBean;
import netgest.bo.xwc.xeo.components.Bridge;
import netgest.bo.xwc.xeo.components.BridgeLookup;
import netgest.bo.xwc.xeo.components.BridgeToolBar;
import netgest.bo.xwc.xeo.components.FormEdit;
import netgest.bo.xwc.xeo.components.LookupList;
import netgest.bo.xwc.xeo.components.SplitedLookup;
import netgest.bo.xwc.xeo.components.lookup.LookupComponent;
import netgest.bo.xwc.xeo.components.utils.DefaultFavoritesSwitcherAlgorithm;
import netgest.bo.xwc.xeo.components.utils.LookupFavorites;
import netgest.bo.xwc.xeo.components.utils.XEOListVersionHelper;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.xeo.localization.XEOViewersMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;
import netgest.utils.StringUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 */
public class XEOEditBean extends XEOBaseBean{
	
	public static final String VIEW_BEAN_ERRORS_ID = "viewBean_erros";


	public XEOEditBean(){
		super();
	}
	
	/**
	 * 
	 * For testing purposes only
	 */
	public XEOEditBean(EboContext ctx){
		super(ctx);
	}
	
    public static final Logger log = Logger.getLogger( XEOEditBean.class.getName() );
    
	private byte 					initialPermissions = SecurityPermissions.FULL_CONTROL;
	private boolean 				initialPermissionsInitialized = false;
	
    private XEOObjectConnector      oCurrentData;
    private Object                  oCurrentObjectKey;
    private boObject                oBoObect;
    private boThread                oBoThread;
    
    private String      			sParentComponentId;
    private XEOEditBean				parentBean;
    
    private String					sBridgeKeyToEdit;
    
    private boolean 				bValid = true;
    
    private Boolean					editInOrphanMode = null;
    private boolean 				bTransactionStarted = false;
    /**
     * Whether there is a relation with this object that makes it
     * different from its normal status (as in object is orphan and relation is non-orphan)
     * by default relations are orphan 
     */
    private boolean					bRelationInOrphanMode = true;
    
	private StackTraceElement[] createIn = (new Throwable()).getStackTrace();
    
    /**
     * @return	The current XEO Object associated to this bean
     */
    public boObject getXEOObject() {
      try {

        	if( getCurrentObjectKey() != null ) {
	            oBoObect = boObject.getBoManager().loadObject
	                ( boApplication.currentContext().getEboContext() , Long.parseLong(String.valueOf( getCurrentObjectKey() )));
	            
	            if( !oBoObect.userReadThis() )
	            	oBoObect.markAsRead(); 
	            
				if( !this.bTransactionStarted ) {
			    	if( !getEditInOrphanMode() ) {
			    		oBoObect.poolSetStateFull();
			    		oBoObect.transactionBegins();
			    		bTransactionStarted = true;
			    	}
					Window wnd = (Window) getRequestContext().getViewRoot().findComponent( Window.class );
					if( wnd != null && wnd.getOnClose() == null ) {
						wnd.setOnClose( "#{"+ getId() +".cancel}" );
					}
				}
	            return oBoObect;
            }
        	else {
            	try {
	        		StringBuilder sb = new StringBuilder();
	        		sb.append( "\n======================================================================================\n" );
	        		sb.append( "getXEOObject without currentObjectKey.\n" );
	        		sb.append( "--------------------------------------------------------------------------------------" + "\n" );
	        		sb.append(" Created in:" );
	        		for( StackTraceElement stackElement : this.createIn ) {
	        			sb.append( "\t" + stackElement.toString() + "\n" );
	        		}
	        		sb.append( "--------------------------------------------------------------------------------------" + "\n" );
	        		log.warn( sb.toString() );
            	}
            	catch( Exception e ) { log.warn( e ); /*Ignore error generating debug info*/ }
        	}
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

   
    /**
     * @return the current DataRecordConnector associated with the XEO Object
     */
    public DataRecordConnector getCurrentData() {
        if( oCurrentData == null ) {
            oCurrentData = new XEOObjectConnector( getXEOObject().getBoui(), 1 );
        }
        return oCurrentData;
    }

    public boThread getThread() {
        if( oBoThread == null )
        {
            oBoThread = new boThread();
        }
        return oBoThread;
    }

    /**
     * @param sObjectName
     */
    public void createNew( String sObjectName ) {
    	createNew( sObjectName, 0 );
    }
    
    public void setRelationInOrphanMode(boolean orphanMode){
    	this.bRelationInOrphanMode = orphanMode;
    }
    
    /**
     * 
     * Whether or not this object should be edited in orphan mode from its relation
     * 
     * @return True if the relation with the parent is an orphan relation and false otherwise
     */
    private boolean getEditInOrphanModeFromRelation(){
    	return this.bRelationInOrphanMode;
    }
    
    /**
     * 
     * Whether or not this object should be edited in orphan mode from its definition
     * 
     * @return True if the object should be edited in orphan mode and false otherwise
     */
    private boolean getEditInOrphaModeFromDefinition(){
    	if( this.editInOrphanMode == null ) {
    		if( this.oBoObect == null  ) {
    			getXEOObject();
    		}
    		this.editInOrphanMode = this.oBoObect.getBoDefinition().getBoCanBeOrphan();
    		if(!this.editInOrphanMode) {
    			if( getParentBean() == null ) {
    				this.editInOrphanMode = true;
    			}
    		}
    	}
    	return this.editInOrphanMode;
    }
    
    /**
     * 
     * Whether or not this object should be edited in orphan mode
     * 
     * @return True if the object should be edited in orphan mode and false otherwi
     * 
     * Checks both the definition of the object and the relation it may have from a parent
     */
    public boolean getEditInOrphanMode() {
    	return getEditInOrphaModeFromDefinition() && getEditInOrphanModeFromRelation();
    }

    public void setEditInOrphanMode( boolean editInOrphanMode ) {
    	this.editInOrphanMode = editInOrphanMode;
    }
    
    /**
     * @param sObjectName
     */
    public void createNew( String sObjectName, long parentBoui ) {
        EboContext oEboContext = getEboContext();
        try {
        	if( parentBoui != 0 ) {
        		this.oBoObect = 
    	                boObject.getBoManager().createObjectWithParent( oEboContext, sObjectName, parentBoui );
	        }
        	else {
	            this.oBoObect = 
	                boObject.getBoManager().createObject( oEboContext, sObjectName );
        	}
            this.oBoObect.poolSetStateFull();
            this.setCurrentObjectKey( String.valueOf( this.oBoObect.getBoui() ) );

        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    


    /**
     * @throws boRuntimeException
     */
    @Visible
    public void save() throws boRuntimeException {
    	processValidate();
    	if( this.isValid() ) {
    		processUpdate();
    	}
    }
    @Visible
    public void saveAndClose() throws boRuntimeException {
    	this.save();
    	if( this.isValid() ) {
    		closeView();
    	}
    }
    

    
    
    
    /**
     * 
     * Retrieves a XSLT that's the merge of the system and project XSLT
     * 
     * @param systemXSLT A stream to the system XSLT
     * @param projectXSLT A stream to the project XSLT
     * 
     * @return A string with the contents of the two XSLTs merged
     */
    private InputStream getMergedXSLT(InputStream systemXSLT, InputStream projectXSLT)
    {
		if (projectXSLT != null)
		{
			XMLDocument systemDoc = ngtXMLUtils.loadXML(systemXSLT);
			XMLDocument projectDoc = ngtXMLUtils.loadXML(projectXSLT);
			
			XMLDocument doc = new XMLDocument();
			
			String namespace = "http://www.w3.org/1999/XSL/Transform";
			Element root = doc.createElementNS(namespace,"stylesheet");
			root.setPrefix("xsl");
			root.setAttribute("version", "1.0");
			doc.setEncoding("utf-8");
			 
			//Get the Children of the First (Root) node
			Hashtable<String,Boolean> nodesMerged = new Hashtable<String,Boolean>();
			NodeList childProjectNodes = projectDoc.getChildNodes().item(0).getChildNodes();
			for (int i = 0; i < childProjectNodes.getLength(); i++)
			{
				Node currentSystemNode = childProjectNodes.item(i);
				Node clonedNode = doc.importNode(currentSystemNode, true);
				root.appendChild(clonedNode);
				
				StringBuilder keyStr = new StringBuilder(clonedNode.getNodeName());
				NamedNodeMap namedNodeMap = clonedNode.getAttributes();
				if( namedNodeMap != null ) {
					for(int k = 0; k < namedNodeMap.getLength(); k++ ) {
						Node att = namedNodeMap.item( k );
						keyStr.append( '_' );
						keyStr.append( att.getNodeName() );
						keyStr.append( '=' );
						keyStr.append( att.getNodeValue() );
					}
				}
				nodesMerged.put( keyStr.toString() , Boolean.TRUE );
			}
			
			NodeList childNodes = systemDoc.getChildNodes().item(0).getChildNodes();
			for (int k = 0; k < childNodes.getLength(); k++)
			{
				Node currentProjectNode = childNodes.item(k);
				StringBuilder keyStr = new StringBuilder(currentProjectNode.getNodeName());
				NamedNodeMap namedNodeMap =currentProjectNode.getAttributes();
				if( namedNodeMap != null ) {
					for(int l = 0; l < namedNodeMap.getLength(); l++ ) {
						Node att = namedNodeMap.item( l );
						keyStr.append( '_' );
						keyStr.append( att.getNodeName() );
						keyStr.append( '=' );
						keyStr.append( att.getNodeValue() );
					}
				}
				if( !nodesMerged.containsKey( keyStr.toString()) ) {
					Node clonedNode = doc.importNode(currentProjectNode, true);
					root.appendChild(clonedNode);
				}
			}
			
			doc.appendChild(root);
			return new ByteArrayInputStream( ngtXMLUtils.getXMLBytes( doc ) );
		}
		else
		{
			return systemXSLT;	
		}
    }
    
    /**
     * 
     *  Given a System XSLT and a Project XSLT and the XML content, renders the XML content with
     *  the merge of the two XSLTs
     * 
     * @param systemXSLT The name of the system XSLT
     * @param projectXSLT The name of the project XSLT
     * @param xmlSourceContent The XML content to render 
     * @param parameters The XSLT parameters
     * 
     * @return The rendered content
     * 
     * @throws boRuntimeException If the transformation fails for some reason
     */
    private byte[] renderXSLT(String systemXSLT, String projectXSLT, 
    		String xmlSourceContent, HashMap<String,String> parameters) throws boRuntimeException
    {
    	
    	//Retrieve the system default templates 
    	InputStream systemTransformer = Thread.currentThread().getContextClassLoader().getResourceAsStream( systemXSLT ); 
    	//ngtXMLUtils.loadXML( systemTransformer );
    	//systemTransformer = Thread.currentThread().getContextClassLoader().getResourceAsStream( systemXSLT ); 
    	
    	
    	//Retrieve the project-specific templates
    	InputStream projectTransformer = null;
    	if (projectXSLT != null)
    		projectTransformer = Thread.currentThread().getContextClassLoader().getResourceAsStream( projectXSLT );
    	
    	//Merge the Two transforms 
    	InputStream finalTransformer = getMergedXSLT(systemTransformer, projectTransformer);
    	
    	// JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(new StringReader(xmlSourceContent));
        Source xsltSource = new StreamSource(finalTransformer);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            TransformerFactory transFact =
	                TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(xsltSource);
	        if (parameters != null){
	        	Iterator<String> it = parameters.keySet().iterator();
	        	while (it.hasNext()) {
					String paramName = (String) it.next();
					String paramValue = parameters.get(paramName);
					trans.setParameter(paramName, paramValue);
				}
	        }
	        trans.transform(xmlSource, new StreamResult(out) );
        }
        catch (Exception e) 
        {
        	log.severe("XEOEditBean - "+MessageLocalizer.getMessage("XSLT_TRANSFORMATION_ERROR"), e);
        	throw new boRuntimeException("XEOEditBean - "+ExceptionMessage.XSLT_TRANSFORMATION_ERROR.toString(), e.getMessage(), e);
        }
        
        return out.toByteArray();
    }
    
    
    /**
     * 
     * Temporary function that retrieves the content of a system file
     * 
     * @return
     */
    private String getViewerContentAsXML()
    {
    	XMLDocument doc;
		XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
		
		XUISessionContext sessionContext = requestContext.getSessionContext();
		String s;
		try {
			s = sessionContext.renderViewToBuffer("XEOXML", requestContext.getViewRoot().getViewState()  ).toString();
			doc = ngtXMLUtils.loadXMLPreserveCData(s);
			return ngtXMLUtils.getXML(doc);
			//return result;
		} catch (IOException e) {
			log.severe(MessageLocalizer.getMessage("COULD_NOT_RETRIEVE_THE_VIEWER"), e);
			//e.printStackTrace();
		} 
		return null;
    }
    
    /**
     * 
     * Converts a JSON String to a map of XSLT parameters
     * 
     * @param jsonValue The JSON String
     * 
     * @return Map with the parameters or null if the JSON string is incorrect
     */
    @SuppressWarnings("unchecked")
	private HashMap<String,String> convertJSONToParameters(String jsonValue){
    	
    	try {
			JSONObject obj = new JSONObject(jsonValue);
			HashMap<String, String> params = new HashMap<String, String>();
			
			Iterator<String> itKeys = obj.keys();
			while (itKeys.hasNext()){
				String key = (String) itKeys.next();
				String value = obj.getString(key);
				params.put(key, value);
			}
			
			return params;
			
		} catch (JSONException e) 
		{
			e.printStackTrace();
			return null;
		}
    	
    }
    
    /**
     * 
     * Exports the Edit Form to HTML
     * 
     * @throws boRuntimeException
     */
    @Visible
    public void exportHTML() throws boRuntimeException
    {
    	final String		HTML_TEMPLATES = "html_templates.xsl";
    	final String		PROJECT_HTML_TEMPLATES = "projectHtmlTemplates.xsl";
    	
    	String customXSLT = null;
    	HashMap<String,String> parameters = null;
    	
    	//If we have a custom XSLT via an exportMenu component, retrieve the value
    	XUIComponentBase c =  getRequestContext().getEvent().getComponent();
    	if (c != null)
    	{
	        if( c instanceof ExportMenu ) 
	        {
	        	customXSLT = ((ExportMenu)c).getStyleSheet();
	        	String jsonParams = ((ExportMenu)c).getParameters();
	        	parameters = convertJSONToParameters(jsonParams);
	        }
    	}
    	
    	String xmlContent = this.getViewerContentAsXML();
    	byte[] result = null;
    	if (customXSLT != null)
    		result = this.renderXSLT(customXSLT, null, xmlContent, parameters);
    	else
    		result = this.renderXSLT(HTML_TEMPLATES, PROJECT_HTML_TEMPLATES, xmlContent,parameters);
    	try{
    		HttpServletResponse response = (HttpServletResponse) getRequestContext().getResponse();
    		response.getOutputStream().write( result );
			getRequestContext().responseComplete();
		}  
    	catch (IOException e) 
		{
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 
     * Exports the Edit Form to a PDF file
     * 
     * @throws boRuntimeException
     */
    @Visible
    public void exportPDF() throws boRuntimeException
    {
    	final String		PDF_TEMPLATES = "pdf_templates.xsl";
    	final String		PROJECT_PDF_TEMPLATES = "projectPdfTemplates.xsl";
    	
    	String xmlContent = this.getViewerContentAsXML();
    	
    	
    	String customXSLT = null;
    	HashMap<String,String> parameters = null;
    	
    	//If we have a custom XSLT via an exportMenu component, retrieve the value
    	XUIComponentBase c =  getRequestContext().getEvent().getComponent();
    	if (c != null)
    	{
	        if( c instanceof ExportMenu ) 
	        {
	        	customXSLT = ((ExportMenu)c).getStyleSheet();
	        	String jsonParams = ((ExportMenu)c).getParameters();
	        	if (jsonParams != null)
	        		parameters = convertJSONToParameters(jsonParams);
	        }
    	}
    	
    	byte[] result; 
    	
    	if (customXSLT != null)
    		result = this.renderXSLT(customXSLT, null, xmlContent, parameters);
    	else
    		result = this.renderXSLT(PDF_TEMPLATES, PROJECT_PDF_TEMPLATES, xmlContent,parameters);
    	
    	FopFactory fopFactory = FopFactory.newInstance();

    	// Step 2: Set up output stream.
    	// Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
    	
    	try 
    	{
    		
    	  boObject currentEditObject = getXEOObject();
    		
    	  HttpServletResponse response = (HttpServletResponse) getRequestContext().getResponse();
    	  response.setContentType("application/pdf");
    	  String fileName = currentEditObject.getTextCARDID().toString().replace(" ","_");
    	  response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".pdf\""); 
      	  OutputStream out = response.getOutputStream();
    		
    	  // Step 3: Construct fop with desired output format
    	  Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

    	  // Step 4: Setup JAXP using identity transformer (could not use Oracle)
    	  System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");
    	  
    	  TransformerFactory factory = TransformerFactory.newInstance();
    	  Transformer transformer = factory.newTransformer(); // identity transformer
    	           
    	  // Step 5: Setup input and output for XSLT transformation 
    	  // Setup input stream
    	  Source src = new StreamSource( new ByteArrayInputStream(result) );

    	  // Resulting SAX events (the generated FO) must be piped through to FOP
    	  Result res = new SAXResult(fop.getDefaultHandler());
    	            
    	  // Step 6: Start XSLT transformation and FOP processing
    	  transformer.transform(src, res);
    	  
    	  getRequestContext().responseComplete();
    	} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		} 
    	
    }
    
    /**
     * 
     * Exports the edit form as an Excel file (basically HTML with a XLS extension)
     * 
     * @throws boRuntimeException
     */
    @Visible
    public void exportExcel() throws boRuntimeException
    {
    	final String		EXCEL_TEMPLATES = "excel_templates.xsl";
    	final String		PROJECT_EXCEL_TEMPLATES = "projectExcelTemplates.xsl";
    	
    	String xmlContent = this.getViewerContentAsXML();
        
    	
    	String customXSLT = null;
    	HashMap<String,String> parameters = null;
    	
    	//If we have a custom XSLT via an exportMenu component, retrieve the value
    	XUIComponentBase c =  getRequestContext().getEvent().getComponent();
    	if (c != null)
    	{
	        if( c instanceof ExportMenu ) 
	        {
	        	customXSLT = ((ExportMenu)c).getStyleSheet();
	        	String jsonParams = ((ExportMenu)c).getParameters();
	        	parameters = convertJSONToParameters(jsonParams);
	        }
    	}
    	
    	byte[] result; 
    	
    	if (customXSLT != null)
    		result = this.renderXSLT(customXSLT, null, xmlContent, parameters);
    	else
    		result = this.renderXSLT(EXCEL_TEMPLATES, PROJECT_EXCEL_TEMPLATES, xmlContent,parameters);
    	
    	boObject currentEditObject = getXEOObject();
    	
    	HttpServletResponse response = (HttpServletResponse) getRequestContext().getResponse();
    	response.setContentType("application/excel");
  	  	response.setHeader("Content-disposition","attachment; filename=\""+currentEditObject.getTextCARDID()+".xls\""); 
  	  	try 
  	  	{
			response.getOutputStream().write( result );
		} 
  	  	catch (IOException e) 
  	  	{
  	  		getRequestContext().responseComplete();
			throw new boRuntimeException("XEOEditBean - exportExcel", "", e);
		}
  	  	getRequestContext().responseComplete();
    }
    
    @Visible
    public void saveAndCreateNew() throws boRuntimeException 
    {
    	this.save();
    	if( this.isValid() ) {
    		createNew(
    			getXEOObject().getName()
    		);
    	}
    }
    
    
    /**
     * 
     * @throws boRuntimeException
     */
    public void saveAsTemplate() {
    	
    }
    @Visible
    public void processUpdate() throws boRuntimeException {
		update();
    	if( getEditInOrphanMode() ) {
    		updateParentComponents();
    	}
    }
    @Visible
    public void remove() throws boRuntimeException {
    	processDestroy();
    	if( isValid() ) {
    		closeView();
    	}
    }
    
    @Visible
    public void removeConfirm() throws boRuntimeException{
    	XUIForm f = (XUIForm) getViewRoot().findComponent(XUIForm.class);
    	MessageBox alertBox = (MessageBox) f.findComponent(f.getId()+"_removeAlertBox");
    	alertBox.show();
    }
    
    @Visible
    public void dummy(){
    	//Do nothing, just exist to bind
    }
    
    
    @Visible
    public void closeView() {
    	XUIRequestContext oRequestContext = getRequestContext();
		XVWScripts.closeView( oRequestContext.getViewRoot() );
    	XUIViewRoot viewRoot = oRequestContext.getSessionContext().createView(SystemViewer.DUMMY_VIEWER);
    	oRequestContext.setViewRoot( viewRoot );
		oRequestContext.renderResponse();
    }
    
    @Visible
    public void processDestroy()  throws boRuntimeException {
    	destroy();
    }
    @Visible
    public void destroy()  throws boRuntimeException {
    	boObject xeoobject = null;
    	try {
    		xeoobject = getXEOObject();
    		xeoobject.destroy(); 
	        getRequestContext().addMessage(
	                "Bean",
	                new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, XUIMessage.SEVERITY_INFO, 
	                    BeansMessages.TITLE_SUCCESS.toString(), 
	                    BeansMessages.BEAN_DESTROY_SUCCESS.toString() 
	                )
	            );
    	} catch ( Exception e ) {
    		if( e instanceof boRuntimeException ) {
    			boRuntimeException boEx = (boRuntimeException)e;
    			if ( "BO-3022".equals( boEx.getErrorCode() ) ) {
    				XUIMessageSender.alertError( BeansMessages.DATA_CHANGED_BY_OTHER_USER );
    				setValid(false);
    			}
    			else if ("BO-3023".equals( boEx.getErrorCode() ) )
    			{
    				XUIMessageSender.alertError( BeansMessages.REMOVE_FAILED_REFERENCED_BY_OBJECTS );
    				setValid(false);
    			    
    			}
    			else if( "BO-3021".equals( boEx.getErrorCode() ) ) {
    				setValid(false);
    				showObjectErrors(xeoobject);
    			}
        		else {
        			throw new RuntimeException( e );
        		}
    		}
    		else {
    			throw new RuntimeException( e );
    		}
    	}
    }
    @Visible
    public void update() throws boRuntimeException {
    	XUIRequestContext oRequestContext;
    	
    	
    	if( !getEditInOrphanMode() ) {
			// Commit changes on object
	        boObject currentObject = getXEOObject();
	        currentObject.transactionEnds( true );
	        this.bTransactionStarted = false;
	        XEOEditBean oParentBean = getParentBean();
	
	        if( oParentBean != null )
	        {
	            oParentBean.setOrphanEdit( this );
	        }
	    	
    	}
    	else {
	    	oRequestContext = getRequestContext();
	    	boObject obj = getXEOObject();
	    	try {
	    		obj.update();
	    		obj.setChanged( false );
	    		oRequestContext.addMessage(
		                "Bean",
		                new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, XUIMessage.SEVERITY_INFO, 
		                    BeansMessages.TITLE_SUCCESS.toString(), 
		                    BeansMessages.BEAN_SAVE_SUCESS.toString() 
		                )
		            );
	    		
	    	} catch ( Exception e ) {
	    		if( e instanceof boRuntimeException ) {
	    			boRuntimeException boEx = (boRuntimeException)e;
	    			if ( "BO-3022".equals( boEx.getErrorCode() ) ) {
	    				XUIMessageSender.alertError(BeansMessages.DATA_CHANGED_BY_OTHER_USER );
	    		        setValid(false);
	    			}
	    			else if ("BO-3054".equals( boEx.getErrorCode() ) )
	    			{
	    					if ( boEx.getAttributeNames().isEmpty() ){
	    						XUIMessageSender.alertError(BeansMessages.UPDATE_FAILED_KEY_VIOLATED );
	    					} else {
	    						List<String> attributeNames = boEx.getAttributeNames();
	    						String parameter = "";
	    						String toAdd = ", ";
	    						for (Iterator<String> it = attributeNames.iterator() ;  it.hasNext() ; ){
	    							String name = it.next();
	    							boDefAttribute attribute = obj.getBoDefinition().getAttributeRef( name );
	    							parameter += attribute.getLabel();
	    							if (it.hasNext()){
	    								parameter += toAdd;
	    							}
	    						}
	    						if (attributeNames.size() == 1){
	    							XUIMessageSender.alertError( BeansMessages.UPDATE_FAILED_KEY_VIOLATED_FOR_FIELD.toString( parameter ) );
	    						} else {
	    							XUIMessageSender.alertError( BeansMessages.UPDATE_FAILED_KEY_VIOLATED_FOR_FIELDS.toString( parameter ) );
	    						}
	    					}
	        		        setValid(false);	        		        
	    			    
	    			}
	    			else if( "BO-3021".equals( boEx.getErrorCode() ) ) {
	    				if( boEx.getSrcObject() != getXEOObject() ) {
	    					XUIMessageSender.alertError( 
	    							BeansMessages.ERROR_SAVING_RELATED_OBJECT.toString(), 
	    							boEx.getMessage() );
	    					showObjectErrors(boEx.getSrcObject());
	    				}
	    				else {
	        				showObjectErrors();
	    				}
	    				setValid(false);
	    			}
	    			else {
	    				throw new RuntimeException( e );
	    			}
	    		}
	    		else {
	    			throw new RuntimeException( e );
	    		}
	    	}
    	}
    }
    
    /**
     * 
     * Opens the lookup viewer to allow selecting a certain instance. Used by lookup component)
     * Not by bridge components, see {@link #lookupBridge()}
     * 
     * @throws boRuntimeException
     */
    public void lookupAttribute( String sCompId ) throws boRuntimeException {
        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        Window				oWnd;

        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        AttributeBase oAtt = (AttributeBase)getViewRoot().findComponent( sCompId );
        LookupComponent lookup = null;
        if (oAtt instanceof LookupComponent){
        	lookup = (LookupComponent) oAtt;
        }
        AttributeHandler    oAttHandler = ((XEOObjectAttributeConnector)oAtt.getDataFieldConnector()).getAttributeHandler();
        boDefAttribute      oAttDef     = oAttHandler.getDefAttribute();
        
    	String className = oAttDef.getReferencedObjectName(); 
    	String[] objects = oAttDef.getObjectsName();
    	boolean hasReferencedObjectsList = objects != null && objects.length > 0;
    	if( "boObject".equals( oAttDef.getReferencedObjectName()) || hasReferencedObjectsList ) {
    		if( hasReferencedObjectsList ) {
    			className = objects[0];
    		}
    	}
        
    	String lookupViewerName = oAtt.getLookupViewer();
    	if( lookupViewerName == null ) {
    		if (!hasReferencedObjectsList) {
    			lookupViewerName = getLookupViewer( oAttHandler );
    		}
    		else {
    			lookupViewerName = getLookupViewer( oAttHandler , boDefHandler.getBoDefinition(className) );
    		}
    	}
    	
        
        if( !oAttDef.getChildIsOrphan( className ) ) {
            XEOEditBean   oBaseBean;
            
            oViewRoot = oSessionContext.createChildView( lookupViewerName );
            oBaseBean = (XEOEditBean)oViewRoot.getBean( "viewBean" );
            
            oBaseBean.setParentBean( this );
            oBaseBean.setParentBeanId( getId() );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            
            oWnd = (Window)oViewRoot.findComponent(Window.class); 
            if( oWnd != null ) {
            	oWnd.setAnimateTarget( sCompId );
            }
            	
            if( oAttHandler.getValueObject() == null || oAttHandler.getValueString().length() == 0 
            		|| boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equalsIgnoreCase(oAttHandler.getDefAttribute().getAtributeDeclaredType())) {
            	//This situation is for non-orphan adding on bridge or regular lookup
                oBaseBean.createNew( oAttDef.getReferencedObjectName(), getXEOObject().getBoui() );
            }
            else {
                oBaseBean.setCurrentObjectKey( Long.valueOf( oAttHandler.getValueString() ) );
            }
        }
        else
        {
        	
            XEOBaseLookupList   oBaseBean;
            oViewRoot = oSessionContext.createChildView( lookupViewerName );
            oBaseBean = (XEOBaseLookupList)oViewRoot.getBean( "viewBean" );

            oWnd = (Window)oViewRoot.findComponent(Window.class); 
            if( oWnd != null ) {
            	oWnd.setAnimateTarget( sCompId );
            }
            
            LookupList lookUp_list=(LookupList)oViewRoot.findComponent(LookupList.class); 
            if (lookUp_list!=null &&  
            		boDefAttribute.ATTRIBUTE_OBJECT.equalsIgnoreCase(
            				oAttHandler.getDefAttribute().getAtributeDeclaredType())){
            	lookUp_list.setRowSelectionMode(GridPanel.SELECTION_ROW);
            }
            
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( oAttHandler.getName() );
            oBaseBean.setParentParentBeanId( getId() );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( oAttHandler ) );
            //Order is important, this must come after setParentComponentId
            oBaseBean.setSelectedObject( className );
            
            String sBoql = null;
            if (lookup != null){
            	sBoql = lookup.getLookupQuery();
            	if (StringUtils.hasValue( sBoql )){
            		oBaseBean.executeBoql( sBoql );
            	}
            }
        }

        // Diz a que a view corrente ��� a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
    }
    
    public Map<String, String> getLookupObjectsMap( AttributeHandler oAttHandler ) {
    	return getLookupObjectsMap( oAttHandler.getDefAttribute() );
    }
    
    public Map<String, String> getLookupObjectsMap( bridgeHandler oBridgeHandler ) {
    	return getLookupObjectsMap( oBridgeHandler.getDefAttribute() );
    }

    public String getLookupQuery( String attributeName, String lookupObject ) {
    	return getLookupQuery( getXEOObject().getAttribute( attributeName ), lookupObject );
    }
    
    public String getLookupQuery( AttributeHandler oAttHandler, String lookupObject ) {
    	String boql = null;
    	
		if( lookupObject == null  ) {
			lookupObject = "";
		}
		
		boql = oAttHandler.getFilterBOQL_query( lookupObject );
		if( boql == null || boql.length() == 0 ) {
			
			if( lookupObject.length() == 0 ) {
				lookupObject = oAttHandler.getDefAttribute().getReferencedObjectName();
				if( "boObject".equals( lookupObject ) ) {
					String[] classNames = oAttHandler.getDefAttribute().getObjectsName();
					if( classNames != null && classNames.length > 0 ) {
						lookupObject = classNames[0];
					}
				}
			}
			boql = "select " + lookupObject;
		}
		return boql;
    }


    public String getLookupViewer( AttributeHandler oAttHandler ) {
    	return getLookupViewer( oAttHandler.getDefAttribute(), oAttHandler.getDefAttribute().getReferencedObjectDef() );
    }
    
    public String getLookupViewer( AttributeHandler oAttHandler, boDefHandler relObject ) {
    	return getLookupViewer( oAttHandler.getDefAttribute(), relObject );
    }

    public String getLookupViewer( bridgeHandler oBridgeHandler ) {
    	return getLookupViewer( oBridgeHandler.getDefAttribute(), oBridgeHandler.getDefAttribute().getReferencedObjectDef() );
    }
    
    public String getLookupViewer( bridgeHandler oBridgeHandler, boDefHandler relObject ) {
    	return getLookupViewer( oBridgeHandler.getDefAttribute(), relObject );
    }

    private String getLookupViewer( boDefAttribute defAtt, boDefHandler relObject ) {
    	String className = relObject.getName(); 
    	if( "boObject".equals( relObject.getName() ) ) {
    		String[] objects = defAtt.getObjectsName();
    		if( objects != null && objects.length > 0 ) {
    			className = objects[0];
    		}
    	}
    	if( defAtt.getChildIsOrphan( className ) ) {
    		return getViewerResolver().getViewer( className, XEOViewerResolver.ViewerType.LOOKUP, defAtt );
    	} else {
    		return 
    			getViewerResolver().getViewer( relObject.getName(), XEOViewerResolver.ViewerType.EDIT, defAtt );
    	}
    }
    
    /**
     * @param oEditBean
     */
    public void setOrphanEdit( XEOEditBean oEditBean ) throws boRuntimeException {
        XUIRequestContext   oRequestContext;
        XUIViewRoot         oLastView;
        oRequestContext = getRequestContext();
        oLastView = oRequestContext.getViewRoot();
        boObject currentObject = getXEOObject();
        try {
            XUIViewRoot oViewRoot = getViewRoot();

            if( oEditBean.getParentComponentId() != null )
            {
                XUIComponentBase oSrcComp = (XUIComponentBase)oViewRoot.findComponent( oEditBean.getParentComponentId() );
            
                    
                long lEditedBoui = oEditBean.getXEOObject().getBoui();
                boolean updateModel = true;    
                // Check if it's an attribute
                if( oSrcComp instanceof AttributeBase ) {
                    oRequestContext.setViewRoot( oViewRoot );
                    XUIInput oInput = (XUIInput)oSrcComp;
                    oInput.setValue( BigDecimal.valueOf( lEditedBoui ) );
                    AttributeBase oAttBase = (AttributeBase) oSrcComp;
                    if (oAttBase instanceof BridgeLookup){
                    	bridgeHandler oBridgeHndlr = currentObject.getBridge(oAttBase.getObjectAttribute());
                        if( !oBridgeHndlr.haveBoui( lEditedBoui ) ) {
                            oBridgeHndlr.add( lEditedBoui );
                            updateModel = false;
                        }
                    } 
                    updateUserFavorites(currentObject.getName(), oAttBase.getObjectAttribute(),
                    		new long[]{lEditedBoui});
                    
                    
                    if (updateModel)
                    	oInput.updateModel();
                }
                else if( oSrcComp instanceof GridPanel ) {
                    // It's a grid, add to thebridge
                    GridPanel oGrid = (GridPanel)oSrcComp;
                    String sObjectAttribute = oGrid.getObjectAttribute();
                    
                    // Set the current view because de resolvers...
                    oRequestContext.setViewRoot( oViewRoot );
        
                    if( sObjectAttribute != null ) {
                        XEOBridgeListConnector oBridgeConnector = (XEOBridgeListConnector)oGrid.getDataSource();
                        bridgeHandler oBridgeHndlr = oBridgeConnector.getBridge();
                        
                        if( !oBridgeHndlr.haveBoui( lEditedBoui ) ) {
                            oBridgeHndlr.add( lEditedBoui );
                        }
                        long[] bouisToCheck = new long[]{lEditedBoui};
                        updateUserFavorites(oBridgeHndlr.getParent().getName(), oBridgeHndlr.getAttributeName(),
                        		bouisToCheck);
                    }
                    
                }
            }
            showObjectErrors();
        }
        finally {
            oRequestContext.setViewRoot( oLastView );
        }
    }

    /**
     * 
     * Opens the lookup viewer to add an element to a bridge
     * 
     */
    @Visible
    public void lookupBridge() throws boRuntimeException {

        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        Window				oWnd;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();

        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        XUIViewRoot oCurrentView = getViewRoot();
        
        String objectName = null;
        
        GridPanel oGrid = (GridPanel)oCurrentView.findComponent( String.valueOf( oCommand.getValue() ) );
        if( oGrid == null ) {
        	oGrid = (GridPanel)oCommand.findParentComponent( GridPanel.class );
        	objectName = String.valueOf( oCommand.getValue() );
        }

        bridgeHandler   bridge  = ((XEOBridgeListConnector)oGrid.getDataSource()).getBridge();
        boDefAttribute  oAttDef = bridge.getDefAttribute();
        
        boDefHandler refObj;
        if( objectName == null ) {
        	refObj = oAttDef.getReferencedObjectDef();        	
        }
        else {
        	refObj = boDefHandler.getBoDefinition( objectName );
        }
        
        String viewerName = getLookupViewer( bridge, refObj );
        
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        boolean addInOrphanMode;
        if( addInOrphanMode = oAttDef.getChildIsOrphan( refObj.getName() ) )
        {
            XEOBaseLookupList   oBaseBean;
            oViewRoot = oSessionContext.createChildView( viewerName );
            if( oRequestContext.getEvent() != null ) {  
	            oWnd = (Window)oViewRoot.findComponent(Window.class); 
	            if( oWnd != null ) {
	            	oWnd.setAnimateTarget( oRequestContext.getEvent().getComponent().getClientId() );
	            }
            }
            
            oBaseBean = (XEOBaseLookupList)oViewRoot.getBean("viewBean");
            
            if (oAttDef.getMaxOccurs() < Integer.MAX_VALUE){
            	int elementsInBridge = (int) bridge.getRecordCount();
            	oBaseBean.setMaxSelections( oAttDef.getMaxOccurs() -  elementsInBridge );
            }
            
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( bridge.getName() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( bridge ) );
            oBaseBean.setParentParentBeanId( getId() );
            oBaseBean.setParentComponentId( oGrid.getClientId() );
            //Order is important, setSelectedObject cannot be called before setComponentId
            oBaseBean.setSelectedObject(refObj.getName());
            oBaseBean.executeBoql( 
            		getLookupQuery( bridge.getParent().getAttribute( bridge.getName() ), objectName )
            	);
            oBaseBean.setMultiLookup( true );
        }
        else {
            XEOEditBean   oEditBean;

            oViewRoot = oSessionContext.createChildView( viewerName );
            if( oRequestContext.getEvent() != null ) {  
	            oWnd = (Window)oViewRoot.findComponent(Window.class); 
	            if( oWnd != null ) {
	            	oWnd.setAnimateTarget( oRequestContext.getEvent().getComponent().getClientId() );
	            }
            }

            oEditBean = (XEOEditBean)oViewRoot.getBean("viewBean");
            oEditBean.setParentBeanId( getId() );
            oEditBean.setParentComponentId( oGrid.getClientId() );
            oEditBean.setEditInOrphanMode( addInOrphanMode );
            oEditBean.createNew( refObj.getName(), getXEOObject().getBoui() );
            
//            oBaseBean.getXEOObject().addParent( getXEOObject() );
            
        }
        // Current view is the newly created one
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
    }
    
    /**
     * Opens the edit viewer when an element is double clicked in a bridge
     */
    @Visible
    public void editBridge() {
        XUIRequestContext   oRequestContext;
        XUIViewRoot			oViewRoot;
        XUISessionContext	oSessionContext;
        
        boDefAttribute  	oAttDef; 
        DataListConnector 	listConnector;
        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot		= null;
        oAttDef			= null;

        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        
        GridPanel oGrid = (GridPanel)oCommand.findParentComponent(GridPanel.class);

        listConnector = oGrid.getDataSource();
        
        bridgeHandler gridBridge = getGridPanelBridge( oGrid, listConnector );
        
        if( gridBridge != null ) {
        	oAttDef = gridBridge.getDefAttribute();
        }
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        if( oAttDef != null ) {

        	DataRecordConnector oSelectedRow = oGrid.getFirstSelectedOrActiveRow();
            if( oSelectedRow == null ) {
            	String value = (String)oCommand.getValue();
            	if( value != null ) {
            		oSelectedRow = oGrid.getRowByIdentifier( value );
            	}
            }
            
            if (oSelectedRow != null){
            	sBridgeKeyToEdit = String.valueOf( oSelectedRow.getAttribute("BOUI").getValue() );
            } else {
            	XUIMessageSender.alertWarning(
            			BeansMessages.ROW_DOUBLE_CLICK_FAILED_TITLE, 
            			BeansMessages.ROW_DOUBLE_CLICK_FAILED_MESSAGE );
            	return;
            }
    		
    		try {
				boObject childObj = XEO.load(Long.valueOf(sBridgeKeyToEdit));
				if (securityRights.canRead(getEboContext(), childObj.getName())) {
					if (oAttDef.getChildIsOrphan( childObj.getName() ) ) {
						if (oRequestContext.isAjaxRequest()) {
							// Resubmit the to the command... to save the selected row.
							oCommand.setValue( oSelectedRow
									.getAttribute("BOUI")
									.getValue().toString());
							
							String frameId = "rowDblClick_"+oSelectedRow
							.getAttribute("BOUI")
							.getValue().toString();
							
							oRequestContext.getScriptContext().add(
									XUIScriptContext.POSITION_FOOTER,
									"editBrigde_openTab",
									XVWScripts.getOpenCommandTab(frameId,oCommand, "" , null ));
							
							oRequestContext.renderResponse();
						} else {
							try {
								oViewRoot = oSessionContext
										.createChildView(
								        		getViewerResolver().getViewer( childObj, XEOViewerResolver.ViewerType.EDIT, oAttDef )
											);
								XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
										.getBean("viewBean");
								oBaseBean.setCurrentObjectKey(sBridgeKeyToEdit);
							} catch (NumberFormatException e) {
								throw new RuntimeException(e);
							} 
						}
					} else {
						long lCurrentBoui;

						lCurrentBoui = ((BigDecimal) oSelectedRow.getAttribute(
								"BOUI").getValue()).longValue();

						String sClassName;
						try {
							sClassName = boObject.getBoManager()
									.getClassNameFromBOUI(getEboContext(),
											lCurrentBoui);
							oViewRoot = oSessionContext
									.createChildView(
							        		getViewerResolver().getViewer( childObj, XEOViewerResolver.ViewerType.EDIT, oAttDef )
									);
							XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
									.getBean("viewBean");
							oBaseBean.setCurrentObjectKey(sBridgeKeyToEdit);
						} catch (NumberFormatException e) {
							throw new RuntimeException(e);
						} catch (boRuntimeException e) {
							throw new RuntimeException(e);
						}

						oViewRoot = oSessionContext.createChildView(
				        		getViewerResolver().getViewer( childObj, XEOViewerResolver.ViewerType.EDIT, oAttDef )
							);
						XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
								.getBean("viewBean");
						oBaseBean.setEditInOrphanMode( false );
						oBaseBean.setParentBeanId( getId() );
						oBaseBean.setParentComponentId(oGrid.getClientId());
						oBaseBean.setCurrentObjectKey(String
								.valueOf(lCurrentBoui));
					}
				} else {
					
					oRequestContext
							.addMessage(
									"error_edit_bridge",
									new XUIMessage(XUIMessage.TYPE_ALERT,
											XUIMessage.SEVERITY_ERROR,
											BeansMessages.ERROR_EXECUTING_OPERATION.toString(),
											BeansMessages.NOT_ENOUGH_PERMISSIONS_TO_OPEN_OBJECT.toString()
										));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
        else {
        	oRequestContext.addMessage( "error_edit_bridge" , 
        			new XUIMessage( 
        					XUIMessage.TYPE_ALERT, 
        					XUIMessage.SEVERITY_ERROR, 
        					BeansMessages.ERROR_EXECUTING_OPERATION.toString(),
        					BeansMessages.ERROR_ASSOCIATING_BRIDGE.toString()
        			) 
        	);
        }

        if( oViewRoot != null ) {
	        // Diz a que a view corrente ��� a criada.
	        oRequestContext.setViewRoot( oViewRoot );
	        oRequestContext.getFacesContext().renderResponse();
        }
    }
    @Visible
    public void addNewToBridge() {
        XUIRequestContext   oRequestContext;
        XUIViewRoot			oViewRoot;
        XUISessionContext	oSessionContext;
        
        boDefAttribute  	oAttDef; 
        DataListConnector 	listConnector;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot		= null;
        oAttDef			= null;

        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        String 	   oObjectName = String.valueOf( oCommand.getValue() );
        GridPanel oGrid = (GridPanel)oCommand.findParentComponent(GridPanel.class);

        listConnector = oGrid.getDataSource();
        
        bridgeHandler gridBridge = getGridPanelBridge( oGrid, listConnector );
        
        if( gridBridge != null ) {
        	oAttDef = gridBridge.getDefAttribute();
        }
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        
        boolean addInOrphanMode;
        
        if( oAttDef != null ) {
    		try {
				if (securityRights.canRead(getEboContext(), oObjectName )) {
					if (addInOrphanMode = oAttDef.getChildIsOrphan( oObjectName ) ) {
						if (oRequestContext.isAjaxRequest()) {
							// Resubmit the to the command... to save the selected row.
							oCommand.setValue( oObjectName );
							oRequestContext.getScriptContext().add(
									XUIScriptContext.POSITION_FOOTER,
									"editBrigde_openTab",
									XVWScripts.getOpenCommandTab(oCommand,
											""
									));
									
							oRequestContext.renderResponse();
						} else {
							try {
								oViewRoot = oSessionContext
										.createChildView(
								        		getViewerResolver().getViewer( oObjectName, XEOViewerResolver.ViewerType.EDIT, oAttDef )
											);
								XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
										.getBean("viewBean");
								
								if( gridBridge.getDefAttribute().getSetParent() == boDefAttribute.SET_PARENT_YES ) {
									oBaseBean.createNew( oObjectName, getXEOObject().getBoui() );
								}
								else { 
									oBaseBean.createNew( oObjectName );
								}
								oBaseBean.setParentBeanId( getId() );
								oBaseBean.setParentBean( this );
								oBaseBean.setParentComponentId( oGrid.getClientId() );
								oBaseBean.setEditInOrphanMode( addInOrphanMode );
								
							} catch (NumberFormatException e) {
								throw new RuntimeException(e);
							}
						}
					} else {
						oViewRoot = oSessionContext
								.createChildView(
					        		getViewerResolver().getViewer( oObjectName, XEOViewerResolver.ViewerType.EDIT, oAttDef )
								);
						XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
								.getBean("viewBean");

						oBaseBean.createNew( oObjectName, getXEOObject().getBoui() );
						
						oBaseBean.setParentBeanId( getId() );
						oBaseBean.setParentBean( this );
						oBaseBean.setParentComponentId( oGrid.getClientId() );
						oBaseBean.setEditInOrphanMode( addInOrphanMode );
					}
				} else {
					oRequestContext
							.addMessage(
									"error_edit_bridge",
									new XUIMessage(XUIMessage.TYPE_ALERT,
											XUIMessage.SEVERITY_ERROR,
											BeansMessages.ERROR_EXECUTING_OPERATION.toString(),
											BeansMessages.NOT_ENOUGH_PERMISSIONS_TO_OPEN_OBJECT.toString()
										));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
        else {
        	oRequestContext.addMessage( "error_edit_bridge" , 
        			new XUIMessage( 
        					XUIMessage.TYPE_ALERT, 
        					XUIMessage.SEVERITY_ERROR, 
        					BeansMessages.ERROR_EXECUTING_OPERATION.toString(),
        					BeansMessages.ERROR_ASSOCIATING_BRIDGE.toString()
        			) 
        	);
        }

        if( oViewRoot != null ) {
	        // Diz a que a view corrente ��� a criada.
	        oRequestContext.setViewRoot( oViewRoot );
	        oRequestContext.getFacesContext().renderResponse();
        }
    }
    
    public bridgeHandler getGridPanelBridge( GridPanel gridPanel ) {
    	return getGridPanelBridge( gridPanel, gridPanel.getDataSource() );
    }
    
    protected bridgeHandler getGridPanelBridge( GridPanel gridPanel, DataListConnector dataConnector ) {
    	
    	bridgeHandler ret;
    	String		  gridAtt;
    	
    	gridAtt = gridPanel.getObjectAttribute();
    	ret = null;
    	
    	if( dataConnector instanceof XEOBridgeListConnector ) {
    		ret = ((XEOBridgeListConnector)dataConnector).getBridge();
    	}
    	else if ( gridAtt != null ) {
    		boObject xeoObj;
    		xeoObj = getXEOObject();
    		if( xeoObj != null ) {
    			ret = xeoObj.getBridge( gridAtt );
    		}
    	}
    	return ret;
    }
    
    public void setLookupBridgeResults( String parentBridgeId, DataRecordConnector[] oSelRecs ) {
    	XUIRequestContext   oRequestContext;
        GridPanel           oGridPanel;
        bridgeHandler       oBridgeHandler;
        XUIViewRoot         oLastViewRoot;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oLastViewRoot = oRequestContext.getViewRoot();
        
        try {
            if (oSelRecs.length > 0) {
                
                XUIViewRoot oViewRoot = getViewRoot(); 

                oGridPanel = 
                        (GridPanel)oViewRoot.findComponent(parentBridgeId);
                oRequestContext.setViewRoot(oViewRoot);

                
                oBridgeHandler  = ((XEOBridgeListConnector)oGridPanel.getDataSource()).getBridge();
                String name = oBridgeHandler.getName();
                long[] bouisToCheck = new long[oSelRecs.length];
                for (int i = 0; i < oSelRecs.length; i++) {
                	BigDecimal boui = (BigDecimal)oSelRecs[i].getAttribute("BOUI").getValue();
                	if( boui != null ) {
                		bouisToCheck[i] = boui.longValue();
                		if( !oBridgeHandler.haveBoui( boui.longValue() ) ) {
                            oBridgeHandler.add( boui );
                		}
                	}
                }
                updateUserFavorites(oBridgeHandler.getParent().getName(), name, bouisToCheck);
                
            }
            showObjectErrors();
        }
        catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
        finally {
            oRequestContext.setViewRoot( oLastViewRoot );
        }
    }
    
    /**
     * @param lookupListBean
     * @param oSelRecs
     */
    public void setLookupBridgeResults( XEOBaseLookupList lookupListBean, DataRecordConnector[] oSelRecs ) {
    	setLookupBridgeResults(lookupListBean.getParentComponentId(), oSelRecs);
    }

    public void setLookupAttributeResults( String parentCompId, DataRecordConnector[] oSelRecs ) {
    	XUIRequestContext   oRequestContext;
        XUIInput            oInput;
        XUIViewRoot         oLastViewRoot;
        
        
        oRequestContext = getRequestContext();
        oLastViewRoot = oRequestContext.getViewRoot();
        try {
            if( oSelRecs.length > 0 )         
            {
                XUIViewRoot oViewRoot = getViewRoot();
                oInput = (XUIInput)oViewRoot.findComponent( parentCompId );
                oRequestContext.setViewRoot( oViewRoot );
                //The BridgeLookup is a special case of Lookup
                if (oInput instanceof BridgeLookup){
                	BridgeLookup bridge = (BridgeLookup) oInput;
                	XEOObjectAttributeConnector conn = (XEOObjectAttributeConnector) bridge.getDataFieldConnector();
                	
                	//Group the bouis selected to update the favorites 
                	long[] bouisToCheck = new long[oSelRecs.length];
                	int i = 0;
                	try {
                		String name = conn.getAttributeHandler().getName();
                		bridgeHandler bh = conn.getAttributeHandler().getParent().getBridge(name);
                		for (DataRecordConnector rec : oSelRecs){
                			long boui = Long.valueOf(rec.getAttribute( "BOUI" ).getValue().toString());
                			bouisToCheck[i++] = boui;
                			if (!bh.haveBoui(boui)){
                				bh.add(boui);
                			}
                		}
                		//Update favorite selections
                		updateUserFavorites(bh.getParent().getName(), name, bouisToCheck);
					} catch (boRuntimeException e) {
						e.printStackTrace();
					}
                	
                }
                else{
                	AttributeBase att = (AttributeBase) oViewRoot.findComponent( parentCompId );
                	String boui = oSelRecs[0].getAttribute( "BOUI" ).getValue().toString();
                	oInput.setValue( boui );
                	String objectName = ((XEOObjectAttributeConnector)att.getDataFieldConnector()).getAttributeHandler().getParent().getName();
                	updateUserFavorites(objectName, att.getObjectAttribute(), new long[]{Long.valueOf(boui)});
                }
                
                
                oInput.updateModel();
                oInput.validateModel();
                showObjectErrors();
            }
        } finally {
            oRequestContext.setViewRoot( oLastViewRoot );
        }
    }
    
    /**
     * 
     * Updates the user favorites for a given bridge/object name pair
     * 
     * @param objectName
     * @param attributeName
     * @param bouis
     */
    private void updateUserFavorites(String objectName, 
    		String attributeName, long[] bouis){
    	PreferenceManager manager = boApplication.getDefaultApplication().getPreferencesManager();
    	Preference pref = manager.getUserPreference(BridgeLookup.PREFERENCE_PREFIX 
    			+ objectName+ BridgeLookup.PREFERENCE_SEPARATOR +
    			attributeName, getEboContext().getBoSession().getUser().getUserName());
    	
    	try{
    		
	    	Object jsonArraPref = pref.get(BridgeLookup.PREFERENCE_NAME);
	    	JSONArray array = null;
	    	if (jsonArraPref != null && jsonArraPref.toString().length() > 0){
	    		String jsonArra = jsonArraPref.toString();
	    		array = new JSONArray(jsonArra);
	    	}
	    	else
	    		array = new JSONArray();
    	
	    	List<LookupFavorites> bouisLst = LookupFavorites.
	    		getFavorites(array);
	    	
	    	bouisLst = new DefaultFavoritesSwitcherAlgorithm().replaceFavorites(bouisLst, bouis,
	    			10);
	    	
	    	JSONArray value = LookupFavorites.encodeFavoritesAsJSON(bouisLst);
	    	pref.put(BridgeLookup.PREFERENCE_NAME, value.toString());
	    	pref.savePreference();
	    }
    	catch (JSONException e ){
    		log.warn(e);
    	}
    }
    
    /**
     * @param lookupListBean
     * @param oSelRecs
     */
    public void setLookupAttributeResults( XEOBaseLookupList lookupListBean, DataRecordConnector[] oSelRecs ) {
    	setLookupAttributeResults( lookupListBean.getParentComponentId(), oSelRecs );
    	
        
    }

    /**
     * @throws boRuntimeException
     */
    @Visible
    public void removeFromBridge() throws boRuntimeException {
        
        XUIRequestContext   oRequestContext;
        bridgeHandler       oBridgeHandler;
        
        oRequestContext = getRequestContext();

        XUIViewRoot oCurrentView = oRequestContext.getViewRoot();

        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        
        GridPanel oGrid = (GridPanel)oCurrentView.findComponent( String.valueOf( oCommand.getValue() ) );
        if( oGrid == null ) {
        	oGrid = (GridPanel)oCommand.findParentComponent( GridPanel.class );
        }

        oBridgeHandler  = ((XEOBridgeListConnector)oGrid.getDataSource()).getBridge();
        
        DataRecordConnector[] oSelectedRows = oGrid.getSelectedRows();
        
        for (int i = 0; i < oSelectedRows.length; i++) {
            long rowBoui = ((BigDecimal)oSelectedRows[i].getAttribute("BOUI").getValue()).longValue();
            if( oBridgeHandler.haveBoui( rowBoui ) )
                oBridgeHandler.remove();
        }
        oGrid.resetSelections();
        showObjectErrors();
    }
    
    /**
     * Removes an element from a bridge Lookup Component
     */
    public void removeBridgeLookup() throws boRuntimeException{
    	
    	ActionEvent oEvent = getRequestContext().getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();	
    	
    	String bouiToRemove = getRequestContext().getRequestParameterMap().
    	get(((XUIComponentBase)oCommand.getParent()).getClientId()+"_toRemove");
    	
    	BridgeLookup lookup = ((BridgeLookup)oCommand.getParent());
    	if (lookup.isDisabled() || !lookup.isVisible()){
    		return;
    	}
    	
    	AttributeHandler att = ((BridgeLookup)oCommand.getParent()).getAttributeHandler();
    	boObject parent = att.getParent();
    	String bridgeName = att.getName();
    	
    	bridgeHandler oBridgeHandler  = parent.getBridge(bridgeName);
        
    	if (bouiToRemove != null && bouiToRemove.length() > 0){
    		long bouiLong = Long.valueOf(bouiToRemove);
	        if( oBridgeHandler.haveBoui( bouiLong ) )
	            oBridgeHandler.remove();
    	}
        showObjectErrors();
    	
    }
    
    public void cleanBridgeLookup() throws boRuntimeException{
    	ActionEvent oEvent = getRequestContext().getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();	
    	
        BridgeLookup lookup = ((BridgeLookup)oCommand.getParent());
    	if (lookup.isDisabled() || !lookup.isVisible()){
    		return;
    	}
        
    	AttributeHandler att = ((BridgeLookup)oCommand.getParent()).getAttributeHandler();
    	boObject parent = att.getParent();
    	String bridgeName = att.getName();
    	
    	bridgeHandler oBridgeHandler  = parent.getBridge(bridgeName);
        oBridgeHandler.truncate();	    	
        
        showObjectErrors();
    }
    
    /**
     * @param oCurrentObjectKey
     */
    public void setCurrentObjectKey(Object oCurrentObjectKey) {
    	if (oCurrentObjectKey == null){
    		String customContext = "";
    		try {
    			StringWriter w = new StringWriter();
    			PrintWriter pw = new PrintWriter(w);
    			new IllegalArgumentException("Cannot set null as BOUI").printStackTrace(pw);
    			customContext = w.toString();
    		} catch (Throwable e){ //Ignore exception so that things keep running		
    		}
    		XUIErrorLogger.addDebugInfo( customContext);
    	}
        this.oCurrentObjectKey = oCurrentObjectKey;
        this.oBoObect = null;
        this.oCurrentData = null;
    }

    /**
     * @return the BOUI as String of the current Object
     */
    public Object getCurrentObjectKey() {
        return oCurrentObjectKey;
    }
    
    @Visible
    public void processValidate() {
    	// Reset valid state
    	setValid( true );
    	
    	// Validate Components
    	validateComponents();
    	
    	// Validate Objects
    	if( isValid() ) {
    		validate();
    	}
    }
    
    @Visible
    public void duplicate() throws boRuntimeException {
    	boObject clonedObject;

    	clonedObject = getXEOObject().cloneObject();
    	XUIViewRoot clonedView = getSessionContext().createChildView( getRequestContext().getViewRoot().getViewId() );
    	
    	( ( XEOEditBean ) clonedView.getBean( getId() ) ).setCurrentObjectKey( 
    			Long.toString( clonedObject.getBoui() ) 
    	);
    	getRequestContext().setViewRoot( clonedView );
    	clonedView.processInitComponents();

    	getRequestContext().addMessage( "duplicate", 
				new XUIMessage(
						XUIMessage.TYPE_POPUP_MESSAGE, 
						XUIMessage.SEVERITY_INFO, 
						"Duplicar",
						BeansMessages.CLONE_SUCCESS.toString()
				)
    	);
    	
    }
    @Visible
    public void validate() {
    	validate( false );
    }
    public void validate( boolean silent ) {
    	try {
    		
    		if( !getXEOObject().valid() ) {
    			if( !silent ) {
    				showObjectErrors();
    			}
    			setValid( false );
    		}
    		else {
    			if( !silent ) {
	        		getRequestContext().addMessage( "validation" ,
	        				XUIPopupMessageFactory.createInfo( 
	        						BeansMessages.VALID_SUCCESS_TITLE,
	        						BeansMessages.VALID_SUCCESS ) 
	        		);
	        				
	        		
    			}
    			setValid(true);
    		}
    	} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
    }
    
    public void validateComponents() {
    	// Validate components
		XUIViewRoot viewRoot = getViewRoot();
		viewRoot.processValidateModel();
		checkValidComponentState( viewRoot );
		if( !isValid() ) {
			if (getXEOObject().haveErrors() )
				showObjectErrors();
			else{
	    		XUIRequestContext.getCurrentContext().addMessage( "validation" , 
	    				new XUIMessage(
							XUIMessage.TYPE_ALERT, 
							XUIMessage.SEVERITY_INFO, 
							BeansMessages.VALID_ERRORS_TITLE.toString(),
							BeansMessages.VALID_ERRORS.toString()
	    				)
	    		);
			}
		}
    }
    
    private void checkValidComponentState( UIComponent component ) {
		if( component instanceof XUIEditableValueHolder ) {
			XUIEditableValueHolder valueHolder = (XUIEditableValueHolder)component;
			if( !valueHolder.isModelValid() ) {
				setValid( false );
			}
		}
		if( isValid() ) {
			Iterator<UIComponent> childComponents = component.getFacetsAndChildren();
			UIComponent childComponent;
			for( ; childComponents.hasNext();  ) {
				childComponent = childComponents.next();
				checkValidComponentState( childComponent );
			}
		}
    }
    
    
    public boolean isValid() {
		return bValid;
	}

	public void setValid(boolean valid) {
		bValid = valid;
	}
	
	
	@Visible
	public void showObjectErrors() {
	    showObjectErrors(getXEOObject());
	}
	
	private void showObjectErrors(boObject oXEOObject) {
        XUIRequestContext   oRequestContext = getRequestContext();
        
		StringBuilder sErros = new StringBuilder();
		
		if( oXEOObject.getAttributeErrors() != null ) {
			Iterator attError = oXEOObject.getAttributeErrors().keySet().iterator();
			for (; attError.hasNext();) {
				AttributeHandler att = (AttributeHandler)attError.next();
				String sLabel 	= att.getDefAttribute().getLabel();
				String sMessage = att.getErrorMessage();
	
				if (StringUtils.hasValue( sMessage )){
					if( sMessage.indexOf('[') > -1  )  
						sMessage = sMessage.substring(0,sMessage.indexOf('['));
					
					sErros.append( sLabel ).append(" - ").append( sMessage ).append("<br>");
				}
			}
		}
		
		if( oXEOObject.getObjectErrors() != null ) {
	        List oErrors = oXEOObject.getObjectErrors();
			if( oErrors != null && oErrors.size() > 0 ) {
				for( Object error : oErrors ) {
					sErros.append( (String)error ).append("<br>");
				}
			}
		}

		if( sErros.length() > 0 ) {
			oRequestContext.addMessage( VIEW_BEAN_ERRORS_ID, new XUIMessage(
					XUIMessage.TYPE_ALERT, 
					XUIMessage.SEVERITY_ERROR,
					BeansMessages.TITLE_ERRORS.toString(),
					sErros.toString()
				)
			);
			setValid(false);
		}
		
		oXEOObject.clearErrors();
		
	}


	public String getTitle() {
		try {
			if( getXEOObject().exists() ) {
				String title = getXEOObject().getCARDID().toString();
				if( title == null || title.trim().length() == 0 ) {
					return getXEOObject().getLabel();
				}
				return title;
			} else {
				String title = getXEOObject().getCARDID().toString();
				return title;
			}
		} catch (boRuntimeException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public EboContext getEboContext() {
		 return boApplication.currentContext().getEboContext();
	}
	
	@Override
	public boolean getIsChanged() {
		try {
			return getXEOObject().isChanged();
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	/**
	 * Opens the viewer when a user clicks a CardIDLink
	 */
	public void openLookupObject() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext oSessionContext = oRequestContext.getSessionContext();
		XUIComponentBase srcComponent = oRequestContext.getEvent().getComponent();

        try {
			AttributeBase oAtt = (AttributeBase)srcComponent.getParent();
			AttributeHandler    oAttHandler = ((XEOObjectAttributeConnector)oAtt.getDataFieldConnector()).getAttributeHandler();
			
			((ServletRequest)oRequestContext.getRequest()).setAttribute(
					"__skip.Layouts.doLayout", 
					Boolean.TRUE
				);
			
			if( oAttHandler.getValueObject() != null ) {
				long boui = Long.parseLong( oAttHandler.getValueString());
				boObject objectToLookup = boObject.getBoManager().loadObject( getEboContext(),  boui );

				boolean openInOrphanEdit = 
					!( !objectToLookup.exists() || !objectToLookup.getBoDefinition().getBoCanBeOrphan() );
				
				boolean canacess = 
					securityRights.canRead(  getEboContext(), objectToLookup.getName()) &&
					securityOPL.canRead( boObject.getBoManager().loadObject( getEboContext() , boui) );
				
				if( canacess ) {
					if( openInOrphanEdit ) {
						if( oRequestContext.isAjaxRequest() ) {  
							oRequestContext.getScriptContext().add( 
									XUIScriptContext.POSITION_HEADER , 
									"openObject", 
									XVWScripts.getOpenCommandTab( "edit_" + boui,srcComponent, "")
							);
							oRequestContext.renderResponse();
							return;
						}
					}
					XUIViewRoot 	oViewRoot;
					if( openInOrphanEdit )
						oViewRoot = oSessionContext.createView( 
								getViewerResolver().getViewer( objectToLookup, XEOViewerResolver.ViewerType.EDIT, oAttHandler.getDefAttribute()  )
							);
					else
						oViewRoot = oSessionContext.createChildView(
								getViewerResolver().getViewer( objectToLookup, XEOViewerResolver.ViewerType.EDIT, oAttHandler.getDefAttribute() )
							);
					
					((XEOEditBean)oViewRoot.getBean("viewBean"))
						.setCurrentObjectKey( String.valueOf( boui ) );
					
					((XEOEditBean)oViewRoot.getBean("viewBean"))
						.setEditInOrphanMode( openInOrphanEdit );
					
					oRequestContext.setViewRoot( oViewRoot );
				}
		        oRequestContext.renderResponse();
			}
        } catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
        
	}
	
	private String getDifferenceCommand(){
		UIComponent form = getViewRoot().findComponent(XUIForm.class);
		XUICommand cmd = (XUICommand) getViewRoot().findComponent(form.getId() + ":" + form.getId() + "_showDiffCmd");
		if (cmd != null){
			return XVWScripts.getAjaxCommandScript(cmd, XVWScripts.WAIT_DIALOG);
		}
		return "";
	}
	
	/**
	 * Checks if the current tab where the form is being displayed can be closed
	 * If the form was changed, when the user tries to close the tab a message box
	 * appear informing the user that he has unsaved changes, if the form was not changed
	 */
	@Visible
	public void canCloseTab() 
	{
		XUIRequestContext oRequestContext = getRequestContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		
		if( getIsChanged() ) 
		{
			String closeScript;
			
			Window xWnd = (Window)viewRoot.findComponent(Window.class);
			if( xWnd != null ) {
				if( xWnd.getOnClose() != null ) {
					XUICommand closeCmd = (XUICommand)xWnd.findComponent( xWnd.getId() + "_closecmd" );
		        	closeScript = 
		        			XVWScripts.getAjaxCommandScript( closeCmd , WaitMode.LOCK_SCREEN )+";";
	            }
				else {
					closeScript = 
		            "\nif( "+xWnd.getId()+" )" + xWnd.getId() +  ".destroy();" +
		            "else if(window.parent."+xWnd.getId()+") window.parent." + xWnd.getId() +  ".destroy();";
				}
			}
			else {
				closeScript = XVWScripts.getCloseViewScript( viewRoot );
			}
			
			boolean showDiffButton = false;
			if (viewRoot.findComponent(FormEdit.class) != null)
				showDiffButton = ((FormEdit)viewRoot.findComponent(FormEdit.class)).getShowDifferences();
			
			ExtConfig messageBoxConfig = new ExtConfig();
			messageBoxConfig.addJSString( "id", "confirmClose_"+getXEOObject().getBoui());
			messageBoxConfig.addJSString( "title" , BeansMessages.CHANGES_NOT_SAVED_TITLE.toString() );
			messageBoxConfig.addJSString( "msg" , BeansMessages.CHANGES_NOT_SAVED_MESSAGE.toString() );
			ExtConfig btnTextConfig = new ExtConfig();
			if (getIsChanged() && showDiffButton){
				messageBoxConfig.add( "buttons" , " Ext.MessageBox.YESNOCANCEL  ");
				btnTextConfig.addJSString("yes", XEOViewersMessages.FORM_CLOSE_MESSAGE_YES.toString());
				btnTextConfig.addJSString("no", XEOViewersMessages.FORM_CLOSE_MESSAGE_NO.toString());
				btnTextConfig.addJSString("cancel", XEOViewersMessages.FORM_CLOSE_MESSAGE_DIFFS.toString());
			}
			else{
				messageBoxConfig.add( "buttons" , " Ext.MessageBox.YESNO  ");
				btnTextConfig.addJSString("yes", XEOViewersMessages.FORM_CLOSE_MESSAGE_YES.toString());
				btnTextConfig.addJSString("no", XEOViewersMessages.FORM_CLOSE_MESSAGE_NO.toString());
				
			}
			messageBoxConfig.add( "buttonText" , btnTextConfig.renderExtConfig() );
			messageBoxConfig.add( "fn",  "function(a1) { var el = document.getElementsByTagName('OBJECT');for( var i=0;i<el.length;i++ ) { el[i].style.display='' }; if( a1=='yes' ) { "+closeScript+" } if (a1=='cancel') { "+ getDifferenceCommand() +" } }" );
			messageBoxConfig.add( "icon", "Ext.MessageBox.WARNING" );
			
			String url = getRequestContext().getAjaxURL();
			if (url.indexOf('?') == -1)
				{ url += '?'; }
			else
				{ url += '&'; }
			
			url += "javax.faces.ViewState=" + getRequestContext().getViewRoot().getViewState();
			String cliendID = ((XUIForm)viewRoot.findComponent(XUIForm.class)).getClientId();
			url += "&xvw.servlet="+ cliendID;
			
			String configMsgBox = messageBoxConfig.renderExtConfig().toString(); 	
			oRequestContext.getScriptContext().add(  
					XUIScriptContext.POSITION_HEADER,
					"canCloseDialog",
					// Hide elements of type object so that they don't overlap the box's zindex
					"var el = document.getElementsByTagName('OBJECT');for( var i=0;i<el.length;i++ ) { el[i].style.display='none' };\n" +
					"" +
					"ExtXeo.MessageBox.show("
					+ 
					configMsgBox
					+
					");"
			);
		}
		else {
			XUIViewRoot lastViewRoot;
			
			lastViewRoot = viewRoot;
			
			Window xWnd = (Window)viewRoot.findComponent(Window.class);
			if( xWnd != null ) {
				if( xWnd.getOnClose() != null ) {
					xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
	            }
			}
			
			// Checks if the action on close of the window
			// Stays in the same viewer.
			if( lastViewRoot == oRequestContext.getViewRoot() ) {
				closeView();
				oRequestContext.getViewRoot().setRendered( false );
			}
			
		}
		oRequestContext.renderResponse();
	}
	
	public Object validateLookupValue( AttributeHandler att, String[] atts, Object[] values ) throws boRuntimeException 
	{
		Object retValue = null;
		boolean abort = false;
		String query = getLookupQuery( att , att.getDefAttribute().getReferencedObjectDef().getName() );
		
		StringBuilder myWhere = new StringBuilder();
		List<Object> params = new ArrayList<Object>(1);
		
		for( int i=0; i < atts.length; i++ ) {
			if( values[i] != null ) {
				if( i > 0 ) {
					myWhere.append( " AND " );
				}
				myWhere.append( atts[i] ).append( "= ? " );
				params.add( values[i] );
			}
			else {
				abort = true;
				break;
			}
		}
		
		if( !abort ) {
			final List<Object> emptyArray = new ArrayList<Object>(0);
			XEOQLModifier ql = new XEOQLModifier( query, new ArrayList<Object>(0) );
			
			String wherePart = ql.getWherePart();
			if( wherePart != null && wherePart.length() == 0 ) {
				ql.setWherePart( myWhere.toString() );
			}
			else {
				ql.setWherePart( "(" + wherePart + ")" + " AND (" + myWhere.append( ')' ).toString() );
			}
			
			boObjectList list = boObjectList.list( getEboContext(), ql.toBOQL(emptyArray),params.toArray(), 1, 50, "" );
			list.beforeFirst();
			List<String> bouis = new ArrayList<String>();
			
			long records = list.getRowCount();
			if (records > 1){ 
				while (list.next()){
					boObject current = list.getObject();
					bouis.add( String.valueOf(current.getBoui()) );
				}
				retValue = bouis;
			} else if ( records == 0){
				retValue = bouis;
			} else {
				if( list.next() ) {
					retValue = new BigDecimal( list.getCurrentBoui() );
				}
			}
		}
		return retValue;
	}
	
	private static final XEOGridRowClassRenderer GRID_ROW_CLASS_RENDERER = new XEOGridRowClassRenderer();
    public GridRowRenderClass getRowClass() {
    	return GRID_ROW_CLASS_RENDERER;
    }
	
    public byte getSecurityPermissions() {
    	if( !initialPermissionsInitialized ) {
	    	boObject   obj = getXEOObject();
	    	if( obj != null ) {
		    	EboContext ctx = obj.getEboContext();
		    	byte efectivePermissions = 0;
		    	try {
					efectivePermissions += securityRights
							.canRead(ctx, obj.getName() )
							&& securityOPL.canRead( obj ) ? SecurityPermissions.READ
							: 0;
					efectivePermissions += securityRights.canWrite(ctx, obj.getName())
							&& securityOPL.canWrite( obj ) ? SecurityPermissions.WRITE
							: 0;
					efectivePermissions += securityRights.canDelete(ctx, obj.getName())
							&& securityOPL.canDelete( obj ) ? SecurityPermissions.DELETE
							: 0;
					if (efectivePermissions == 7) {
						efectivePermissions = SecurityPermissions.FULL_CONTROL;
					}
				} catch (Exception e) {
					throw new RuntimeException( e );
				}
				initialPermissions = efectivePermissions;
	    	}
	    	else {
	    		initialPermissions = SecurityPermissions.FULL_CONTROL;
	    	}
	    	initialPermissionsInitialized = true;
    	}
    	return initialPermissions;
    	
    }
    
    /**
     * Opens the viewer with the properties of the current object
     */
    @Visible
    public void showProperties() {
    	
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = getRequestContext();
        oSessionContext = getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEOEditProperties.xvw");
        ((XEOEditBean)oViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    	
    }
     
    @Visible
    public void showOPL()
    {
    	 XUIRequestContext   oRequestContext;
         XUISessionContext   oSessionContext;
         XUIViewRoot         oViewRoot;

         oRequestContext = XUIRequestContext.getCurrentContext();
         oSessionContext = oRequestContext.getSessionContext();
         
         oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEOSecurityProperties.xvw");
         
         XEOSecurityOPLBean bean = (XEOSecurityOPLBean)oViewRoot.getBean("viewBean"); 
         bean.setCurrentObjectKey( Long.toString( getXEOObject().getBoui()) );
         //bean.init();
         
         oRequestContext.setViewRoot( oViewRoot );
         oRequestContext.renderResponse();
    }
    
    public void showDifferences() throws IOException{
    	
    	XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/ShowDifferences.xvw");
        
        String s =  getSessionContext().renderViewToBuffer("XEOXML", getViewRoot().getViewState() ).toString();
        XMLDocument doc = ngtXMLUtils.loadXML(s);
        
        String result = XEOListVersionHelper.renderDifferencesWithFlashBack( getXEOObject(), doc );
        
        ShowDifferenceBean bean = (ShowDifferenceBean)oViewRoot.getBean("viewBean");
        bean.setDifferences( result );
        
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    	
    }
    
    
    /**
     * Opens the viewer with the dependencies of the current object
     */
    @Visible
    public void showDependencies() {
    	
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEODependences.xvw");
        ((XEOEditBean)oViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
        
        Tab dependencies = (Tab) oViewRoot.findComponent("formProps:tbDependencies");
        ((Tabs) dependencies.getParent()).setActiveTab(dependencies);
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    	
    }
    
    /**
     * Opens the viewer with the dependents of the current object
     */
    @Visible
    public void showDependents() {
    	
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEODependences.xvw");
        ((XEOEditBean)oViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
        
        Tab dependents = (Tab) oViewRoot.findComponent("formProps:tbDependents");
        ((Tabs) dependents.getParent()).setActiveTab(dependents);
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    	
    }
    
    /**
     * Opens the viewers with the list of versions for the current object
     */
    @Visible
    public void listVersions()
    {
    	XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEOListVersions.xvw");
        ((XEOVersionListBean)oViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    }

    public void setParentComponentId(String sParentComponentId) {
        this.sParentComponentId = sParentComponentId;
    }

    public String getParentComponentId() {
        return sParentComponentId;
    }

    public void setParentBean( XEOEditBean parentBean ) {
    	this.parentBean = parentBean;
    }
    
    public XEOEditBean getParentBean() {
    	if( this.parentBean == null ){
    		Object baseBean =getParentView().getBean( getParentBeanId() );
    		if (baseBean instanceof XEOEditBean )
    			return  (XEOEditBean) baseBean;
    		else 
    			return null;
    	}
    	else
    		return this.parentBean;
    }
    
    public void updateParentComponents() {
    	if( isValid() ) {
	    	try {
				if( getParentComponentId() != null && getParentBean() != null ) {
					getParentBean().setOrphanEdit( this );
					getParentView().syncClientView();
				}
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
    	}
    }
    
    /*
     * 
     * Non Orphan Mode Methods
     * 
     * 
     */
    
    @Visible
    public void confirm() throws boRuntimeException {
        
        XUIRequestContext oRequestContext = getRequestContext();
         
    	processValidate(); 
    	if( this.isValid() ) {
    		processUpdate();
    		
	        // Get the window in the viewer and close it!
	        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
    		if( oWndComp != null ) {
    			oWndComp.destroy();
    		}
    		else {
        		XVWScripts.closeView( oRequestContext.getViewRoot(), oRequestContext.getScriptContext() );
        		oRequestContext.getViewRoot().setRendered( false );
        		oRequestContext.renderResponse();
    		}
    		
	        // Trigger parent view sync with server
	        XUIViewRoot oParentViewRoot = getParentView();
	        if( oParentViewRoot != null ) {
		        oParentViewRoot.syncClientView();
	        }
	        
	        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( SystemViewer.DUMMY_VIEWER ) );
    	}

    }

    @Visible
    public void processCancel() throws boRuntimeException
    {
    	cancel();
    }
    @Visible
    public void cancel() throws boRuntimeException
    {
        XUIRequestContext oRequestContext = getRequestContext();
        
        // Rollback object changes
        boObject currentObject = getXEOObject();
        currentObject.transactionEnds( false );

        
        // Get the window in the viewer and destroy it!
        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
        oWndComp.destroy();

        this.bTransactionStarted = false;
        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( SystemViewer.DUMMY_VIEWER ) );

    }
    
    /**
     * Save the current viewer as favorite of the user
     * 
     * Favorites are instances of the
     * 
     * 
     */
    public void saveFavorite(){
    	
    	boObject current = getXEOObject();
    	
    	//Retrieve the object that keeps the users favorites (and history?)
    	try {
    		
    		if (current.exists()){
    		
	    		boManagerLocal objectManager = boApplication.getDefaultApplication().getObjectManager();
	    		
				boObject userPreferences = objectManager.
					loadObject(getEboContext(), "select Ebo_UserPreferences where owner = CTX_PERFORMER_BOUI");
				
				if (!userPreferences.exists()){
					//Set the value of the owner to the current 
					userPreferences.getAttribute("owner").setValueLong(getEboContext().getBoSession().getPerformerBoui());
				}
				
				boObjectList list = boObjectList.list(getEboContext(),"select Ebo_UserPreferences.favorites where Ebo_UserPreferences.favorites.targetBouiObj = ? AND Ebo_UserPreferences.owner = CTX_PERFORMER_BOUI",new Object[]{current.getBoui()});
				if (list.getRecordCount() > 0 ){
					//Favorite already exists, show duplicate error message
					getRequestContext().addMessage("duplicateMessage", new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
							ComponentMessages.FAVORITES_DUPLICATE_TITLE.toString(),
							ComponentMessages.FAVORITES_DUPLICATE_MSG.toString()));
				}
				else{
					//Create the favorite
					boObject favoriteObject = objectManager.createObject(getEboContext(), "Ebo_FavoriteViewer");
					
					favoriteObject.getAttribute("targetBouiObj").setValueLong(current.getBoui());
					//Add to bridge
					userPreferences.getBridge("favorites").add(favoriteObject.getBoui());
					getRequestContext().addMessage("success", new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, XUIMessage.SEVERITY_INFO, 
							ComponentMessages.FAVORITES_SUCCESS_TITLE.toString(), 
							ComponentMessages.FAVORITES_SUCCESS_MSG.toString(current.getTextCARDID().toString())));
					userPreferences.update();
				}
				
    		} else{
    			getRequestContext().addMessage("objMustExist", new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, 
    					XUIMessage.SEVERITY_ERROR, 
    					ComponentMessages.FAVORITES_OBJ_MUST_EXIST_TITLE.toString(), 
    					ComponentMessages.FAVORITES_OBJ_MUST_EXIST_MSG.toString()));
    		}
			
		} catch (boRuntimeException e) {
			log.severe(e);
			getRequestContext().addMessage("errorSaving", 
					new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, "Error", "Could not save your favorite"));
			//Show message to display a problem
		}
    	
    }
    
    public void editBridgeLookup(){
    	
    	XUIRequestContext   oRequestContext;
        XUIViewRoot			oViewRoot;
        XUISessionContext	oSessionContext;
        
        boDefAttribute  	oAttDef; 
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot		= null;
        oAttDef			= null;

        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        BridgeLookup oBridgeLookup = (BridgeLookup)oEvent.getComponent().getParent();
        
        oAttDef = ((XEOObjectAttributeConnector)oBridgeLookup.getDataFieldConnector()).getBoDefAttribute();
        
        sBridgeKeyToEdit = getRequestContext().getRequestParameterMap().
        	get(((XUIComponentBase)oCommand.getParent()).getClientId()+"_toEdit");
		
		try {
			boObject childObj = boObject.getBoManager().loadObject(
					getEboContext(), Long.valueOf(sBridgeKeyToEdit));
			
			if (securityRights.canRead(getEboContext(), childObj.getName())) {
				if (oAttDef.getChildIsOrphan( childObj.getName() ) ) { 
					
					oViewRoot = oSessionContext
							.createChildView(
					        		getViewerResolver().getViewer( childObj, XEOViewerResolver.ViewerType.EDIT, oAttDef )
								);
					XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
							.getBean("viewBean");
					oBaseBean.setCurrentObjectKey(sBridgeKeyToEdit);
					
				} 
				else {
					oViewRoot = oSessionContext.createChildView(
			        		getViewerResolver().getViewer( childObj, XEOViewerResolver.ViewerType.EDIT, oAttDef )
						);
					XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
							.getBean("viewBean");

					oBaseBean.setParentBeanId( getId() );
					oBaseBean.setParentComponentId(oCommand.getClientId());
					oBaseBean.setCurrentObjectKey(String
							.valueOf(sBridgeKeyToEdit));
				}
			} else {
				oRequestContext
						.addMessage(
								"error_edit_bridge",
								new XUIMessage(XUIMessage.TYPE_ALERT,
										XUIMessage.SEVERITY_ERROR,
										BeansMessages.ERROR_EXECUTING_OPERATION.toString(),
										BeansMessages.NOT_ENOUGH_PERMISSIONS_TO_OPEN_OBJECT.toString()
									));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    
    if( oViewRoot != null ) {
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.getFacesContext().renderResponse();
    }
    	
    }
    
    /**
     * Displays the window with the favorites chosen for this bridge (by this user)
     */
    public void showFavorite(){
    	
    	int left = 300;
    	int top = 300;
    	
    	ActionEvent oEvent = getRequestContext().getEvent();
    	XUICommand oCommand = (XUICommand)oEvent.getComponent();
    	
    	XUIComponentBase base = ((XUIComponentBase)oCommand.getParent());
    	String bridgeId = base.getClientId();
    	
    	boolean isBridge = false;
    	boolean multiSelect = false;
    	
    	
    	String objectAtt = "";
    	if (base instanceof AttributeNumberLookup)
    		objectAtt = ((AttributeBase)base).getObjectAttribute();
    	if (base instanceof BridgeLookup) {
    		objectAtt = ((AttributeBase)base).getObjectAttribute();
			multiSelect = true;
    	} else if (base instanceof BridgeToolBar || base instanceof BridgeLookup ){
    		BridgeToolBar b = (BridgeToolBar)base; 
    		objectAtt = b.getBridgeName();
    		bridgeId = ((Bridge)b.getParent()).getClientId();
    		isBridge = true;
			multiSelect = true;
    	}
    		
    	String leftParam = getRequestContext().getRequestParameterMap().
    	get(((XUIComponentBase)oCommand.getParent()).getClientId()+"_left");
    	
    	if (leftParam == null || leftParam.length() == 0)
    		leftParam = "0";
    	
    	Double tmpLeft =Double.parseDouble(leftParam); 
    	left = tmpLeft.intValue();
    	
    	left = left-300; //Window size is 300, need to move so that it does not fall out of screen
    	if (left < 0)
    		left = 0;
    	
    	String topParam = getRequestContext().getRequestParameterMap().
    		get(((XUIComponentBase)oCommand.getParent()).getClientId()+"_top");
    	
    	if (topParam == null || topParam.length() == 0)
    		  topParam = "0";
    	
	    	  Double tmpTop =Double.parseDouble(topParam); 
	    	  top = tmpTop.intValue();
    	
	    	  XUIViewRoot viewRoot = getSessionContext().createChildView("netgest/bo/xwc/xeo/viewers/LookupFavorites.xvw");
	    	  BridgeLookupBean bean = (BridgeLookupBean) viewRoot.getBean("viewBean");
	    	  bean.setLeft(left);
	    	  bean.setTop(top);
	    	  
	    	  
	    	  AttributeHandler handler = getHandlerFromAttribute(base);
	    	  boObject sourceObject =  handler.getParent();
	    	  
	    	  
	    	  bean.setAttributeName(objectAtt);
	    	  bean.setInvokedFromBridge(isBridge);
	    	  bean.setObjectName(sourceObject.getName());
	    	  bean.setParentComponentId(bridgeId);
	    	  bean.setMultiSelect(multiSelect);
	    	  bean.setParentObject(getXEOObject().getBoui());
    	  
	    	  LookupFavorites.eliminateDeletedObjectsFromPreference(sourceObject, objectAtt);
    	  
	    	  getRequestContext().setViewRoot(viewRoot);  
	    	  getRequestContext().renderResponse();  
    }
    
    private AttributeHandler getHandlerFromAttribute(XUIComponentBase base){
    	
    	
    	if (base instanceof AttributeBase){
    		
    		AttributeBase objectAtt = ( AttributeBase ) base;
    		XEOObjectAttributeConnector connector = (XEOObjectAttributeConnector)objectAtt.getDataFieldConnector();
  	  		return connector.getAttributeHandler();
    	}
    	else if (base instanceof BridgeToolBar   ){
    		BridgeToolBar b = ( BridgeToolBar )base; 
    		Bridge bridge = ( Bridge ) b.getParent();
    		return bridge.getTargetObject().getAttribute( b.getBridgeName() );
    	}
    	
    	return null;
    	
    }
    
    /**
     * 
     * Searches a given text in text index, if it finds 1 (one) result, sets the lookup
     * to that value, if it finds several opens a window to select. If it finds none, opens the
     * regular lookup 
     * 
     * @param lookupId The identifier of the lookup component
     * @param textToSearch The text string to search for
     * @throws boRuntimeException
     */
    public void searchTextIndexLookup(String lookupId, String textToSearch) throws boRuntimeException{
    	AttributeBase oAtt = (AttributeBase)getViewRoot().findComponent( lookupId );
    	AttributeHandler    oAttHandler = ((XEOObjectAttributeConnector)oAtt.getDataFieldConnector()).getAttributeHandler();
    	String targetObjectToSearch = oAttHandler.getDefAttribute().getReferencedObjectName();
    	String boql = "select " +  targetObjectToSearch;
    	boObjectList list = boObjectList.list(getEboContext(), boql, new Object[0],1,50,"",textToSearch,null,"",true,true);
    	long records = list.getRecordCount();
    	if (records == 1){
    		list.next();
    		oAttHandler.setValueLong(list.getObject().getBoui());
    	} else if (records > 1){
    		XUIViewRoot oViewRoot = getRequestContext().getSessionContext().createChildView( "netgest/bo/xwc/xeo/viewers/LookupSearchResults.xvw" );
            LookupSearchResultsBean oBaseBean = (LookupSearchResultsBean)oViewRoot.getBean( "viewBean" );
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( oAttHandler.getName() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( oAttHandler ) );
            oBaseBean.setParentParentBeanId( getId() );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            //Order is important setParentComponentId must be called before setSelectObject
            oBaseBean.setSelectedObject( targetObjectToSearch );
            oBaseBean.setFullTextSearch(textToSearch);
            getRequestContext().setViewRoot(oViewRoot);  
      	  	getRequestContext().renderResponse();
    	} else {
    		lookupAttribute(lookupId);
    	}
    }
    
    public void searchLookup() throws boRuntimeException{
    	
    	XUICommand source = (XUICommand) getRequestContext().getEvent().getComponent();
    	SplitedLookup lookup = (SplitedLookup)source.getParent();
    	String list = source.getCommandArgument().toString().trim();
    	String lookupId = lookup.getLookupComponent().getClientId();
    	String inputId = lookup.getInputComponent().getClientId();
    	
    	List<String> bouis = new LinkedList<String>();
    	String[] splitBouis = list.split(",");
    	for (String current : splitBouis){
    		if (StringUtils.hasValue(current))
    			bouis.add(current);
    	}
    	
    	AttributeBase oAtt = (AttributeBase)getViewRoot().findComponent( lookupId );
    	AttributeBase input = (AttributeBase)getViewRoot().findComponent( inputId );
    	AttributeHandler    oAttHandler = ((XEOObjectAttributeConnector)oAtt.getDataFieldConnector()).getAttributeHandler();
    	String targetObjectToSearch = oAttHandler.getDefAttribute().getReferencedObjectName(); 
    	input.setValue(null);
    	if (bouis.size() == 1){
    		oAttHandler.setValueString( bouis.get(0) );
    	} else if (bouis.size() > 1){
    		XUIViewRoot oViewRoot = getRequestContext().getSessionContext().createChildView( "netgest/bo/xwc/xeo/viewers/SplitLookupSearchResults.xvw" );
            SplitLookupSearchBean oBaseBean = (SplitLookupSearchBean)oViewRoot.getBean( "viewBean" );
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( oAttHandler.getName() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( oAttHandler ) );
            oBaseBean.setParentParentBeanId( getId() );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            oBaseBean.setMultiLookup(false);
            //Order is important setParentComponentId must be called before setSelectObject
            oBaseBean.setSelectedObject( targetObjectToSearch );
            oBaseBean.setBouis(bouis);
            getRequestContext().setViewRoot(oViewRoot);  
      	  	getRequestContext().renderResponse();
    	} else {
    		lookupAttribute( lookupId );
    	}
    }
    
    /**
     * 
     * Retrieve the results for an autocomplete component
     * 
     * @param objectName The name of the XEOModel to search
     * @param attributeName The name of the attribute to search for
     * @param filterQuery The filter to apply to the query
     * @param filter An existing filter to use instead of a query to the XEOModel
     * @param template The template to return values 
     * 
     * @return A JSON array with the values (each value is a JSONObject with key/value properties)
     */
    public String getAutoCompleteSearchResult( String filter , AttributeBase component){
    	
    	JSONArray result = new JSONArray();
    	
    	DataFieldConnector genericConnector = component.getDataFieldConnector( );
    	String targetObjectName = "";
    	String attribute = component.getObjectAttribute();
    	if (genericConnector instanceof XEOObjectAttributeConnector){
    		XEOObjectAttributeConnector attributeConector = (XEOObjectAttributeConnector) genericConnector;
    		boDefAttribute attributeDefinition = attributeConector.getBoDefAttribute( );
    		targetObjectName = attributeDefinition.getReferencedObjectName( );
    		boObjectList list = boObjectList.list( getEboContext( ) , "select " + targetObjectName );
    		AttributeAutoComplete autoComplete = ( AttributeAutoComplete ) component;
    		if ( autoComplete.getAllowWildCardSearch() ){
    			filter = filter + "%";
    		}
    		
    		list.setFullTextSearch( filter );
    		try {
	    		list.beforeFirst( );
	    		while (list.next()){
	    				boObject current = list.getObject();
	    				JSONObject currentObj = new JSONObject();
	    				
	    				currentObj.put( "value" , current.getTextCARDID().toString());
	    				currentObj.put( "key", String.valueOf( current.getBoui() )  );
	    				
	    				result.put( currentObj );
	    		}
    		} catch ( Exception e ) {
    			log.warn( "Could not retrieve results for attribute %s of type %s", e, attribute, targetObjectName );
    		}
    	}
		return result.toString();
    }
    
    @Override
    public void addDebugInfo(List<String> debug) {
    	try {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(  this.getClass().getName() )
	    		.append( " BOUI: " ).append( oCurrentObjectKey );
	    	
			sb.append(" Created in:" );
			for( StackTraceElement stackElement : this.createIn ) {
				sb.append( "\t" + stackElement.toString() + "\n" );
			}
			debug.add( sb.toString() );
    	}
    	catch( Exception e ) {  log.warn( e ); /* Ignore error creating debug info */ }
    }
}
