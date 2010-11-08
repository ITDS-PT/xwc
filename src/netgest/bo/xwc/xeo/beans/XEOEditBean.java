package netgest.bo.xwc.xeo.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import netgest.bo.system.boApplication;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.Tabs;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.model.ExportMenu;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIEditableValueHolder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.components.FormEdit;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.xeo.localization.XEOViewersMessages;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 */
public class XEOEditBean extends XEOBaseBean 
{
    
	
	
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
					oBoObect.poolSetStateFull();
					oBoObect.transactionBegins();
					bTransactionStarted = true;
					Window wnd = (Window)XUIRequestContext.getCurrentContext().getViewRoot().findComponent( Window.class );
					if( wnd != null && wnd.getOnClose() == null ) {
						wnd.setOnClose( "#{viewBean.cancel}" );
					}
				}
	            return oBoObect;
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
    
    public boolean getEditInOrphanMode() {
    	if( this.editInOrphanMode == null ) {
    		this.editInOrphanMode = getXEOObject().getBoDefinition().getBoCanBeOrphan();
    		if(!this.editInOrphanMode) {
    			if( getParentBean() == null ) {
    				this.editInOrphanMode = true;
    			}
    		}
    	}
    	return this.editInOrphanMode;
    }

    public void setEditInOrphanMode( boolean editInOrphanMode ) {
    	this.editInOrphanMode = editInOrphanMode;
    }
    
    /**
     * @param sObjectName
     */
    public void createNew( String sObjectName, long parentBoui ) {
        EboContext oEboContext = boApplication.currentContext().getEboContext();
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
			 
			NodeList childNodes = systemDoc.getChildNodes().item(0).getChildNodes();
			for (int k = 0; k < childNodes.getLength(); k++)
			{
				Node currentProjectNode = childNodes.item(k);
				Node clonedNode = doc.importNode(currentProjectNode, true);
				root.appendChild(clonedNode);
			}
			
			//Get the Children of the First (Root) node
			NodeList childProjectNodes = projectDoc.getChildNodes().item(0).getChildNodes();
			for (int i = 0; i < childProjectNodes.getLength(); i++)
			{
				Node currentSystemNode = childProjectNodes.item(i);
				Node clonedNode = doc.importNode(currentSystemNode, true);
				root.appendChild(clonedNode);
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
        	log.severe("XEOEditBean - XSLT Transformation Error", e);
        	throw new boRuntimeException("XEOEditBean - XSLT Transformation Error", e.getMessage(), e);
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
			doc = ngtXMLUtils.loadXML( s );
			return ngtXMLUtils.getXML(doc);
			//return result;
		} catch (IOException e) {
			log.severe("Could not retrieve the Viewer", e);
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
			
			Iterator itKeys = obj.keys();
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
    	try 
    	{
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
    	  response.setHeader("Content-disposition","attachment; filename=" + fileName + ".pdf"); 
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
  	  	response.setHeader("Content-disposition","attachment; filename="+currentEditObject.getTextCARDID()+".xls"); 
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
    public void closeView() {
    	XUIRequestContext oRequestContext;
		oRequestContext = XUIRequestContext.getCurrentContext();
		XVWScripts.closeView( oRequestContext.getViewRoot() );
    	XUIViewRoot viewRoot = oRequestContext.getSessionContext().createView("netgest/bo/xwc/components/viewers/Dummy.xvw");
    	oRequestContext.setViewRoot( viewRoot );
		oRequestContext.renderResponse();
    }
    
    @Visible
    public void processDestroy()  throws boRuntimeException {
    	destroy();
    }
    @Visible
    public void destroy()  throws boRuntimeException {
    	try {
    		getXEOObject().destroy(); 
	        XUIRequestContext.getCurrentContext().addMessage(
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
        		        XUIRequestContext.getCurrentContext().addMessage(
        		                "Bean",
        		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, 
        		                    BeansMessages.TITLE_ERROR.toString(), 
        		                    BeansMessages.DATA_CHANGED_BY_OTHER_USER.toString() 
        		                )
        		            );
        		        setValid(false);
    			}
    			else if ("BO-3023".equals( boEx.getErrorCode() ) )
    			{
        		        XUIRequestContext.getCurrentContext().addMessage(
            		                "Bean",
            		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, 
            		                    BeansMessages.TITLE_ERROR.toString(), 
            		                BeansMessages.REMOVE_FAILED_REFERENCED_BY_OBJECTS.toString()
            		                )            		            );        		        
        		        setValid(false);
    			    
    			}
    			else if( "BO-3021".equals( boEx.getErrorCode() ) ) {
    				setValid(false);
    				showObjectErrors();
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
	    	oRequestContext = XUIRequestContext.getCurrentContext();
	    	
	    	try {
	    		getXEOObject().update();
	    		getXEOObject().setChanged( false );
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
	    		        XUIRequestContext.getCurrentContext().addMessage(
	    		                "Bean",
	    		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
	    		                    BeansMessages.TITLE_ERROR.toString(), 
	    		                    BeansMessages.DATA_CHANGED_BY_OTHER_USER.toString() 
	    		                )
	    		            );
	    		        setValid(false);
	    			}
	    			else if ("BO-3054".equals( boEx.getErrorCode() ) )
	    			{
	        		        XUIRequestContext.getCurrentContext().addMessage(
	            		                "Bean",
	            		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, 
	            		                    BeansMessages.TITLE_ERROR.toString(), 
	            		                BeansMessages.UPDATE_FAILED_KEY_VIOLATED.toString()
	            		                )            		            );        
	        		        setValid(false);	        		        
	    			    
	    			}
	    			else if( "BO-3021".equals( boEx.getErrorCode() ) ) {
	    				if( boEx.getSrcObject() != getXEOObject() ) {
	    					oRequestContext.addMessage( "viewBean_erros", new XUIMessage(
	    							XUIMessage.TYPE_ALERT, 
	    							XUIMessage.SEVERITY_ERROR,
	    							BeansMessages.ERROR_SAVING_RELATED_OBJECT.toString(),
	    							boEx.getMessage()
	    						)	    					
	    					);	    					
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
     * @throws boRuntimeException
     */
    public void lookupAttribute( String sCompId ) throws boRuntimeException {
        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        Window				oWnd;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        AttributeBase oAtt = (AttributeBase)getViewRoot().findComponent( sCompId );
        AttributeHandler    oAttHandler = ((XEOObjectAttributeConnector)oAtt.getDataFieldConnector()).getAttributeHandler();
        boDefAttribute      oAttDef     = oAttHandler.getDefAttribute();
        
    	String className = oAttDef.getReferencedObjectName(); 
    	if( "boObject".equals( oAttDef.getReferencedObjectName() ) ) {
    		String[] objects = oAttDef.getObjectsName();
    		if( objects != null && objects.length > 0 ) {
    			className = objects[0];
    		}
    	}
        
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        
        // Verifica o modo de edi��o do Objecto... se for orf�o
        // abre o edit para associar um novo
        
    	String lookupViewerName = oAtt.getLookupViewer();
    	if( lookupViewerName == null ) {
    		lookupViewerName = getLookupViewer( oAttHandler );
    	}
        
        if( !oAttDef.getChildIsOrphan( className ) ) {
            XEOEditBean   oBaseBean;
            
            oViewRoot = oSessionContext.createChildView( lookupViewerName );
            
            oWnd = (Window)oViewRoot.findComponent(Window.class); 
            if( oWnd != null ) {
            	oWnd.setAnimateTarget( sCompId );
            }
            oBaseBean = (XEOEditBean)oViewRoot.getBean( "viewBean" );
            	
            if( oAttHandler.getValueObject() == null ) {
                oBaseBean.createNew( oAttDef.getReferencedObjectName() );
                oBaseBean.getXEOObject().addParent( getXEOObject() );
            }
            else {
                oBaseBean.setCurrentObjectKey( Long.valueOf( oAttHandler.getValueLong() ) );
            }
            
            oBaseBean.setParentBean( this );
            oBaseBean.setParentBeanId( "viewBean" );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            
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
            
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( oAttHandler.getName() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( oAttHandler ) );
            oBaseBean.setSelectedObject( className );
            oBaseBean.setParentParentBeanId( "viewBean" );
            oBaseBean.setParentComponentId( oAtt.getClientId() );
            String sBoql = getLookupQuery( oAttHandler, null );
        	oBaseBean.executeBoql( sBoql );
        }

        // Diz a que a view corrente � a criada.
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
    		return getViewerResolver().getViewer( className, XEOViewerResolver.ViewerType.LOOKUP );
    	} else {
    		return 
    			getViewerResolver().getViewer( relObject.getName(), XEOViewerResolver.ViewerType.EDIT );
    	}
    }
    
    /**
     * @param oEditBean
     */
    public void setOrphanEdit( XEOEditBean oEditBean ) throws boRuntimeException {
        XUIRequestContext   oRequestContext;
        XUIViewRoot         oLastView;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oLastView = oRequestContext.getViewRoot();

        try {
            XUIViewRoot oViewRoot = getViewRoot();

            if( oEditBean.getParentComponentId() != null )
            {
                XUIComponentBase oSrcComp = (XUIComponentBase)oViewRoot.findComponent( oEditBean.getParentComponentId() );
            
                    
                long lEditedBoui = oEditBean.getXEOObject().getBoui();
                    
                // Verifica se � atributo
                if( oSrcComp instanceof AttributeBase ) {
                    oRequestContext.setViewRoot( oViewRoot );
                    XUIInput oInput = (XUIInput)oSrcComp;
                    oInput.setValue( BigDecimal.valueOf( lEditedBoui ) );
                    oInput.updateModel();
                }
                else if( oSrcComp instanceof GridPanel ) {
                    // � uma grid... por isso deve-se adicionar � bridge
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
        
//        if( objectName != null ) {
////        	refObj = boDefHandler.getBoDefinition( objectName );
//        	if( oAttDef.getChildIsOrphan() ) {
//        		viewerName = getViewerResolver().getViewer( objectName, XEOViewerResolver.ViewerType.LOOKUP );
//        	}
//        	else {
//        		viewerName = getViewerResolver().getViewer( objectName, XEOViewerResolver.ViewerType.EDIT );
//        	}
//        }
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        if( oAttDef.getChildIsOrphan( refObj.getName() ) )
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
            
            oBaseBean.setParentBean( this ); 
            oBaseBean.setParentAttributeName( bridge.getName() );
            oBaseBean.setLookupObjects( getLookupObjectsMap( bridge ) );
            oBaseBean.setParentParentBeanId( "viewBean" );
            oBaseBean.setParentComponentId( oGrid.getClientId() );
            oBaseBean.executeBoql( 
            			//"select "+ oAttDef.getReferencedObjectName()
            		getLookupQuery( bridge.getName(), objectName )
            	);
            oBaseBean.setMultiLookup( true );
        }
        else {
            XEOEditBean   oBaseBean;

            oViewRoot = oSessionContext.createChildView( viewerName );
            if( oRequestContext.getEvent() != null ) {  
	            oWnd = (Window)oViewRoot.findComponent(Window.class); 
	            if( oWnd != null ) {
	            	oWnd.setAnimateTarget( oRequestContext.getEvent().getComponent().getClientId() );
	            }
            }

            oBaseBean = (XEOEditBean)oViewRoot.getBean("viewBean");

            oBaseBean.setParentBeanId( "viewBean" );
            oBaseBean.setParentComponentId( oGrid.getClientId() );
            
            
            oBaseBean.createNew( refObj.getName(), getXEOObject().getBoui() );
//            oBaseBean.getXEOObject().addParent( getXEOObject() );
            
        }
        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
    }
    
    @Visible
    public void editBridge() {
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
        
        GridPanel oGrid = (GridPanel)oCommand.findParentComponent(GridPanel.class);

        listConnector = oGrid.getDataSource();
        
        bridgeHandler gridBridge = getGridPanelBridge( oGrid, listConnector );
        
        if( gridBridge != null ) {
        	oAttDef = gridBridge.getDefAttribute();
        }
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro
        if( oAttDef != null ) {

        	DataRecordConnector oSelectedRow = oGrid.getActiveRow();
            if( oSelectedRow == null ) {
            	String value = (String)oCommand.getValue();
            	if( value != null ) {
            		oSelectedRow = oGrid.getRowByIdentifier( value );
            	}
            }
            
    		sBridgeKeyToEdit = String.valueOf( oSelectedRow.getAttribute("BOUI").getValue() );
    		
    		try {
				boObject childObj = boObject.getBoManager().loadObject(
						getEboContext(), Long.valueOf(sBridgeKeyToEdit));
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
							boObject sObjectToOpen;
							try {
								sObjectToOpen = boObject
										.getBoManager()
										.loadObject(
												getEboContext(),
												Long
														.parseLong(sBridgeKeyToEdit));
								oViewRoot = oSessionContext
										.createChildView(
								        		getViewerResolver().getViewer( sObjectToOpen, XEOViewerResolver.ViewerType.EDIT )
											);
								XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
										.getBean("viewBean");
								oBaseBean.setCurrentObjectKey(sBridgeKeyToEdit);
							} catch (NumberFormatException e) {
								throw new RuntimeException(e);
							} catch (boRuntimeException e) {
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
							        		getViewerResolver().getViewer( sClassName, XEOViewerResolver.ViewerType.EDIT )
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
				        		getViewerResolver().getViewer( sClassName, XEOViewerResolver.ViewerType.EDIT )
							);
						XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
								.getBean("viewBean");

						oBaseBean.setParentBeanId("viewBean");
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
	        // Diz a que a view corrente � a criada.
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
        if( oAttDef != null ) {
    		try {
				if (securityRights.canRead(getEboContext(), oObjectName )) {
					if (oAttDef.getChildIsOrphan( oObjectName ) ) {
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
								        		getViewerResolver().getViewer( oObjectName, XEOViewerResolver.ViewerType.EDIT )
											);
								XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
										.getBean("viewBean");
								
								if( gridBridge.getDefAttribute().getSetParent() == boDefAttribute.SET_PARENT_YES ) {
									oBaseBean.createNew( oObjectName, getXEOObject().getBoui() );
								}
								else { 
									oBaseBean.createNew( oObjectName );
								}
								oBaseBean.setParentBeanId("viewBean");
								oBaseBean.setParentBean( this );
								oBaseBean.setParentComponentId( oGrid.getClientId() );
								
							} catch (NumberFormatException e) {
								throw new RuntimeException(e);
							}
						}
					} else {
						oViewRoot = oSessionContext
								.createChildView(
					        		getViewerResolver().getViewer( oObjectName, XEOViewerResolver.ViewerType.EDIT )
								);
						XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
								.getBean("viewBean");

						oBaseBean.createNew( oObjectName, getXEOObject().getBoui() );
						
						oBaseBean.setParentBeanId("viewBean");
						oBaseBean.setParentBean( this );
						oBaseBean.setParentComponentId( oGrid.getClientId() );
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
	        // Diz a que a view corrente � a criada.
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
    
    
    /**
     * @param lookupListBean
     * @param oSelRecs
     */
    public void setLookupBridgeResults( XEOBaseLookupList lookupListBean, DataRecordConnector[] oSelRecs ) {
        // Cria view
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
                        (GridPanel)oViewRoot.findComponent(lookupListBean.getParentComponentId());
                oRequestContext.setViewRoot(oViewRoot);

                oBridgeHandler  = ((XEOBridgeListConnector)oGridPanel.getDataSource()).getBridge();
                for (int i = 0; i < oSelRecs.length; i++) {
                	BigDecimal boui = (BigDecimal)oSelRecs[i].getAttribute("BOUI").getValue();
                	if( boui != null ) {
                		if( !oBridgeHandler.haveBoui( boui.longValue() ) ) {
                            oBridgeHandler.add( boui );
                		}
                	}
                }
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
    public void setLookupAttributeResults( XEOBaseLookupList lookupListBean, DataRecordConnector[] oSelRecs ) {
        // Cria view
        XUIRequestContext   oRequestContext;
        XUIInput            oInput;
        XUIViewRoot         oLastViewRoot;
        
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oLastViewRoot = oRequestContext.getViewRoot();
        try {
            if( oSelRecs.length > 0 )         
            {
                XUIViewRoot oViewRoot = getViewRoot();
                oInput = (XUIInput)oViewRoot.findComponent( lookupListBean.getParentComponentId() );
                oRequestContext.setViewRoot( oViewRoot );
                oInput.setValue( oSelRecs[0].getAttribute( "BOUI" ).getValue() );
                oInput.updateModel();
                showObjectErrors();
            }
        } finally {
            oRequestContext.setViewRoot( oLastViewRoot );
        }
    }

    /**
     * @throws boRuntimeException
     */
    @Visible
    public void removeFromBridge() throws boRuntimeException {
        
        XUIRequestContext   oRequestContext;
        bridgeHandler       oBridgeHandler;
        
        oRequestContext = XUIRequestContext.getCurrentContext();

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
            if( oBridgeHandler.haveBoui( rowBoui ) );
                oBridgeHandler.remove();
        }
        showObjectErrors();
    }
    
    /**
     * @param oCurrentObjectKey
     */
    public void setCurrentObjectKey(Object oCurrentObjectKey) {
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
    	
    	((XEOEditBean)clonedView.getBean("viewBean")).setCurrentObjectKey( 
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
	        		XUIRequestContext.getCurrentContext().addMessage( "validation" , 
	        				new XUIMessage(
        						XUIMessage.TYPE_POPUP_MESSAGE, 
        						XUIMessage.SEVERITY_INFO, 
        						BeansMessages.VALID_SUCCESS_TITLE.toString(),
        						BeansMessages.VALID_SUCCESS.toString()
	        				)
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
	
	
	@SuppressWarnings("unchecked")
	@Visible
	public void showObjectErrors() {
        XUIRequestContext   oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();

        boObject oXEOObject = getXEOObject();
		
		StringBuilder sErros = new StringBuilder();
		
		if( oXEOObject.getAttributeErrors() != null ) {
			Iterator attError = oXEOObject.getAttributeErrors().keySet().iterator();
			for (; attError.hasNext();) {
				AttributeHandler att = (AttributeHandler)attError.next();
				String sLabel 	= att.getDefAttribute().getLabel();
				String sMessage = att.getErrorMessage();
	
				if( sMessage.indexOf('[') > -1  )  
					sMessage = sMessage.substring(0,sMessage.indexOf('['));
				
				sErros.append( sLabel ).append(" - ").append( sMessage ).append("<br>");
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
			oRequestContext.addMessage( "viewBean_erros", new XUIMessage(
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
				long boui = oAttHandler.getValueLong();
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
									XVWScripts.getOpenCommandTab( srcComponent, "")
							);
							oRequestContext.renderResponse();
							return;
						}
					}
					XUIViewRoot 	oViewRoot;
					if( openInOrphanEdit )
						oViewRoot = oSessionContext.createView( 
								getViewerResolver().getViewer( objectToLookup, XEOViewerResolver.ViewerType.EDIT )
							);
					else
						oViewRoot = oSessionContext.createChildView(
								getViewerResolver().getViewer( objectToLookup, XEOViewerResolver.ViewerType.EDIT )
							);
					
					((XEOEditBean)oViewRoot.getBean("viewBean"))
						.setCurrentObjectKey( String.valueOf( boui ) );
					
					((XEOEditBean)oViewRoot.getBean("viewBean"))
						.setEditInOrphanMode( openInOrphanEdit );
					
					oRequestContext.setViewRoot( oViewRoot );
			        oViewRoot.processInitComponents();
				}
		        oRequestContext.renderResponse();
			}
        } catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
        
	}
	
	
	
	/**
	 * Checks if the current tab where the form is being displayed can be closed
	 * If the form was changed, when the user tries to close the tab a message box
	 * appear informing the user that he has unsaved changes, if the form was not changed
	 */
	@Visible
	public void canCloseTab() 
	{
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		
		if( getIsChanged() ) 
		{
			String closeScript;
			
			String openDiffScript = "openDiffWindow()" ;
			
			Window xWnd = (Window)viewRoot.findComponent(Window.class);
			if( xWnd != null ) {
				if( xWnd.getOnClose() != null ) {
					XUICommand closeCmd = (XUICommand)xWnd.findComponent( xWnd.getId() + "_closecmd" );
		        	closeScript = 
		        			XVWScripts.getAjaxCommandScript( closeCmd , XVWScripts.WAIT_STATUS_MESSAGE )+";";
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
			messageBoxConfig.addJSString( "title" , BeansMessages.CHANGES_NOT_SAVED_TITLE.toString() );
			messageBoxConfig.addJSString( "msg" , BeansMessages.CHANGES_NOT_SAVED_MESSAGE.toString() );
			if (getIsChanged() && showDiffButton)
				messageBoxConfig.add( "buttons" , " {yes:'"+XEOViewersMessages.FORM_CLOSE_MESSAGE_YES.toString()+"', " +
						"cancel:'"+XEOViewersMessages.FORM_CLOSE_MESSAGE_DIFFS.toString()+"', " +
						"no:'"+XEOViewersMessages.FORM_CLOSE_MESSAGE_NO.toString()+"'}  "); 
			else
				messageBoxConfig.add( "buttons" , " Ext.MessageBox.YESNO  ");
			messageBoxConfig.add( "fn",  "function(a1) { if( a1=='yes' ) { "+closeScript+" } if (a1=='cancel') { "+ openDiffScript +" } }" );
			messageBoxConfig.add( "icon", "Ext.MessageBox.WARNING" );
			
			String url = getRequestContext().getAjaxURL();
			if (url.indexOf('?') == -1)
				{ url += '?'; }
			else
				{ url += '&'; }
			
			url += "javax.faces.ViewState=" + getRequestContext().getViewRoot().getViewState();
			String cliendID = ((XUIForm)viewRoot.findComponent(XUIForm.class)).getClientId();
			url += "&xvw.servlet="+ cliendID;
			
			String openWindowScript = "function openDiffWindow() {" +
					"var winDiff = new Ext.Window({ " +
                	" title:' "+XEOViewersMessages.VIEW_DIFFS_WINDOW_TITLE.toString()+" '" +
                	",width       : 800" +
                	",autoScroll : true" +
                	",height      : 550" +
                	",html : '<iframe src=\""+url+"\" width=\"100%\" height=\"520\" frameborder=\"0\">'" +
                	"});" +
            		"winDiff.show();}";
			
			//Add the function to open a new Window
			if (getIsChanged())
			oRequestContext.getScriptContext().add(  
					XUIScriptContext.POSITION_HEADER,
					"openDifferencesWindow",openWindowScript
					);
			
			//
			oRequestContext.getScriptContext().add(  
					XUIScriptContext.POSITION_HEADER,
					"canCloseDialog", 
					"Ext.MessageBox.show("
					+ 
					messageBoxConfig.renderExtConfig()
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
			boObjectList list = boObjectList.list( getEboContext(), ql.toBOQL(emptyArray),params.toArray(), 1, 1, "" );
			if( list.next() ) {
				retValue = new BigDecimal( list.getCurrentBoui() );
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

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
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
    	if( this.parentBean == null )
    		return (XEOEditBean)getParentView().getBean( getParentBeanId() );
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
        
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
         
    	processValidate(); 
    	if( this.isValid() ) {
    		processUpdate();
    		
	        // Get the window in the viewer and close it!
	        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
    		if( oWndComp != null ) {
    			oWndComp.destroy();
    		}
    		else {
        		XVWScripts.closeView( oRequestContext.getViewRoot() );
        		oRequestContext.getViewRoot().setRendered( false );
        		oRequestContext.renderResponse();
    		}
    		
	        // Trigger parent view sync with server
	        XUIViewRoot oParentViewRoot = getParentView();
	        if( oParentViewRoot != null ) {
		        oParentViewRoot.syncClientView();
	        }
	        
	        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "netgest/bo/xwc/components/viewers/Dummy.xvw" ) );
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
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        // Rollback object changes
        boObject currentObject = getXEOObject();
        currentObject.transactionEnds( false );

        
        // Get the window in the viewer and destroy it!
        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
        oWndComp.destroy();

        this.bTransactionStarted = false;
        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "netgest/bo/xwc/components/viewers/Dummy.xvw" ) );

    }
    
    
}
