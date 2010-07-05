package netgest.bo.xwc.xeo.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.components.utils.XEOListVersionHelper;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

public class FormEdit extends Form {

	private XUIBindProperty<boObject> targetObject = 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
	
	private XUIBindProperty<Boolean> renderToolBar = 
		new XUIBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);

	private XUIBaseProperty<Boolean> renderViewerTitle = 
		new XUIBaseProperty<Boolean>("renderViewerTitle", this, true);

	private XUIBaseProperty<Boolean> renderViewerMessages = 
		new XUIBaseProperty<Boolean>("renderViewerMessages", this, true);

	private XUIBaseProperty<Boolean> renderWindow = 
		new XUIBaseProperty<Boolean>("renderWindow", this, true);
	
	private XUIBindProperty<Integer> windowHeight = 
		new XUIBindProperty<Integer>("windowHeight", this, 400, Integer.class);

	private XUIBindProperty<Integer> windowWidth = 
		new XUIBindProperty<Integer>("windowWidth", this, 600, Integer.class);
	
	/**
	 * If the show differences button should appear in the dialog 
	 * that appears if the  close tab button is pressed and the object was changed
	 */
	private XUIBindProperty<Boolean> showDifferences = 
		new XUIBindProperty<Boolean>("showDifferences", this, false, Boolean.class);

	private XUIBindProperty<Boolean> orphanMode = 
		new XUIBindProperty<Boolean>("orphanMode", this, Boolean.class, "#{viewBean.editInOrphanMode}" );

	@Override
	public String getRendererType() {
		return "formEdit";
	}
	
	public boolean getOrphanMode() {
		return this.orphanMode.getEvaluatedValue();
	}
	
	public void setOrphanMode( String orphanModeExpr ) {
		this.orphanMode.setExpressionText( orphanModeExpr );
	}
	
	public boolean getRenderViewerTitle() {
		return renderViewerTitle.getValue();
	}

	public void setRenderViewerTitle(boolean renderToolbar) {
		this.renderViewerTitle.setValue( renderToolbar );
	}
	
	/**
	 * 
	 * Whether or not the "show differences" button should appear when
	 * an edited object's tab is closed
	 * 
	 * @return True if the button should appear and false otherwise
	 */
	public boolean getShowDifferences()
	{
		return showDifferences.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the showDifferences button option
	 * 
	 * @param showDifferences If the button should appear or not
	 */
	public void setShowDifferences(boolean showDifferences)
	{
		this.showDifferences.setValue(showDifferences);
	}

	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderToolbar) {
		this.renderToolBar.setValue( renderToolbar );
	}

	public void setRenderToolBar(String renderToolbarExpr) {
		this.renderToolBar.setExpressionText( renderToolbarExpr );
	}

	public boolean getRenderViewerMessages() {
		return renderViewerMessages.getValue();
	}

	public void setRenderViewerMessages(boolean renderViewerMessages) {
		this.renderViewerMessages.setValue( renderViewerMessages );
	}

	public boolean getRenderWindow() {
		return renderWindow.getValue();
	}

	public void setRenderWindow(boolean renderWindow) {
		this.renderWindow.setValue( renderWindow );
	}

	public int getWindowHeight() {
		return windowHeight.getEvaluatedValue();
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight.setValue( windowHeight );
	}

	public void setWindowHeight(String windowHeightExpr) {
		this.windowHeight.setExpressionText( windowHeightExpr );
	}

	public int getWindowWidth() {
		return windowWidth.getEvaluatedValue();
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth.setValue( windowWidth );
	}

	public void setWindowWidth(String windowWidthExpr) {
		this.windowWidth.setExpressionText( windowWidthExpr );
	}
	
	public void setTargetObject( String sExprText ) {
		this.targetObject.setExpressionText( sExprText );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}

	@Override
	public void initComponent() {
		int position = 0;
		
		// Create the default toolbar for the object
		if( getRenderViewerTitle() ) {
			createViewerTitle( position++ );
		}

		if ( getRenderToolBar() ) {
			createEditToolBar( position++ );
		}
		
		// Create the default messages renderer
		if ( getRenderViewerMessages() ) {
			createViewerMessages( position++ );
		}
		
		// Create the window, if the object is to be rendered in a window
		// Must be the last on because can change the tree structure
		if ( getRenderWindow() ) {
			createEditWindow();
		}
		super.initComponent();
	}
	
	private void createEditToolBar( int pos ) {
		ToolBar toolBar;
		if( getOrphanMode() ) {
			toolBar = new EditToolBar();
			toolBar.setId( getId() + "_editToolBar" );
			getChildren().add( pos, toolBar );
		}
		else {
			toolBar = new NonOrphanEditToolBar();
			Panel p = new Panel();
			p.getChildren().add( toolBar );
			toolBar.setId( getId() + "_editToolBar" );
			getChildren().add( pos, p );
		}
	}

	private void createViewerMessages( int pos ) {
		ViewerMessages viewerMessages = (ViewerMessages)findComponent( getId() + "_viewerMsgs" );
		if( viewerMessages == null ) {
			viewerMessages = new ViewerMessages();
			viewerMessages.setId( getId() + "_viewerMsgs" );
			getChildren().add( pos, viewerMessages );
		}
	}

	private void createViewerTitle( int pos ) {
		ViewerTitle viewerTitle = (ViewerTitle)findComponent( getId() + "_viewerTitle" );
		if( viewerTitle == null ) {
			viewerTitle = new ViewerTitle();
			viewerTitle.setId( getId() + "_viewerTitle" );
			getChildren().add( pos, viewerTitle );
		}
	}
	
	private void createEditWindow() {
		ViewerWindow wnd = (ViewerWindow)findComponent( getId() + "_editWnd" );
		if( wnd == null ) {
			boObject object = getTargetObject();
			//TODO: Check to see if the parent attribute have the flag orphanRelation = true....
			if( !getOrphanMode() ) {
				wnd = new ViewerWindow();
				wnd.setId( getId() + "_editWnd" );
				
				wnd.setHeight( getWindowHeight() );
				wnd.setWidth( getWindowWidth() );
				
				//Muda todos os descendentes directos do form, para filhos da janela
				wnd.getChildren().addAll( getChildren() );
				getChildren().clear();
				getChildren().add( wnd );
			}
		}
	}
	
	public static class XEOHTMLRenderer extends Form.XEOHTMLRenderer implements XUIRendererServlet
	{
		public XEOHTMLRenderer()
		{
			super();
		}
		
		 public void decode( XUIComponentBase component ) 
		 {
             super.decode(component);
	     }

	     @Override
	     public void encodeBegin(XUIComponentBase component) throws IOException 
         {
	        super.encodeBegin(component);
	     }
	    
	    
        @Override
        public void encodeEnd( XUIComponentBase component) throws IOException
        {
        	super.encodeEnd(component);
        }

      
        
        @Override
		public void encodeChildren(FacesContext context, UIComponent component)
				throws IOException {
        	super.encodeChildren(context, component);
        }
        
        @Override
        public void encodeChildren(XUIComponentBase component)
		throws IOException {
        	super.encodeChildren(component);
        }
        
        /**
         * 
         * Temporary function that retrieves the content of a system file
         * 
         * @return
         */
        private XMLDocument getViewerContentAsXML()
        {
        	String pathXML = "/Users/useruser/Desktop/Tese/ITDS/XWC - Exportação para XML/MegaObjecto.xml";
        	XMLDocument doc;
    		try 
    		{
    			doc = ngtXMLUtils.loadXML(new FileInputStream(new File(pathXML)));
    			return doc;
    		} 
    		catch (FileNotFoundException e) 
    		{
    			e.printStackTrace();
    			return null;
    		}
        	
        }
        
        
        /* (non-Javadoc)
         * 
         * 
         * 
		 * @see netgest.bo.xwc.framework.XUIRendererServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse, netgest.bo.xwc.framework.components.XUIComponentBase)
		 */
		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse,
				XUIComponentBase oComp) throws IOException 
		{
			
			HttpServletRequest request = 
				(HttpServletRequest) getRequestContext().getRequest();
			HttpServletResponse response = 
				(HttpServletResponse) getRequestContext().getResponse();
	        response.setCharacterEncoding("UTF-8");
	        
	        //Get the FormEdit component
			FormEdit 		frmComponent = (FormEdit) oComp;
	        
			/*
			 * Shows the Logs of the version of the object
			 * 
			 * */
	        if (request.getParameter("showLogs") != null)
	        {
	        	
	        	//Get the BOUI of the object
	        	long			bouiOfVersion = Long.valueOf(request.getParameter("versionBoui"));
	        	
	        	//Produce the HTML result
	        	String 			result = XEOListVersionHelper.getListOfLogsObject(bouiOfVersion, frmComponent);
	        	
	        	response.getWriter().write(result);
    			getRequestContext().responseComplete();
	        }
	        else if (request.getParameter("version") != null)
			{
	        	boSession 		session = frmComponent.getTargetObject().getEboContext().getBoSession();
	        	EboContext		newContext = session.createRequestContextInServlet(
        				(HttpServletRequest)getRequestContext().getRequest(), 
        				(HttpServletResponse)getRequestContext().getResponse(), 
        				(ServletContext)getRequestContext().getServletContext());
	        	long 			bouiVersion = Long.valueOf(request.getParameter("version"));
	        	String 			result = XEOListVersionHelper.renderDifferencesWithPreviousVersion(frmComponent,bouiVersion,getViewerContentAsXML(),newContext);
	        	
	        	response.getWriter().write(result);
				getRequestContext().responseComplete();
			}
			else
			{
				/*
				 * Renders the difference between the current object and the object
				 * saved in the database
				 * 
				 * */
				String result = XEOListVersionHelper.renderDifferencesWithFlashBack(frmComponent, getViewerContentAsXML());
				response.getWriter().write(result);
				getRequestContext().responseComplete();
			}
		}
		
		
		
	
		
	}

}
