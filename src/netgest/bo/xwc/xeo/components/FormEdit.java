package netgest.bo.xwc.xeo.components;

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
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.MessageBox;
import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.components.utils.XEOListVersionHelper;
import netgest.bo.xwc.xeo.localization.XEOViewersMessages;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

/**
 * 
 * 
 * A Form component to edit a single XEO Object
 * 
 * @author jcarreira
 *
 */
public class FormEdit extends Form {

	/**
	 * The target XEO object {@link boObject} that's associated to the edit form
	 *  
	 */
	private XUIViewBindProperty<boObject> targetObject = 
		new XUIViewBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
	
	/**
	 * Whether or not the default {@link EditToolBar} is rendered
	 */
	private XUIViewBindProperty<Boolean> renderToolBar = 
		new XUIViewBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);

	
	/**
	 * Whether or not the title of the viewer should be rendered
	 */
	private XUIViewBindProperty<Boolean>  renderViewerTitle = 
		new XUIViewBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);

	/**
	 * Whether or not viewer messages (XUIMessage) are rendered in the form
	 */
	private XUIViewBindProperty<Boolean>  renderViewerMessages = 
		new XUIViewBindProperty<Boolean>("renderViewerMessages", this, true, Boolean.class);

	
	private XUIBindProperty<Boolean>  renderWindow = 
		new XUIBindProperty<Boolean>("renderWindow", this, true, Boolean.class);
	

	
	/**
	 *The height of the window in which the form should be rendered 
	 *(if it's to be rendered in a separate window and not in a tab) 
	 */
	private XUIViewBindProperty<Integer> windowHeight = 
		new XUIViewBindProperty<Integer>("windowHeight", this, 400, Integer.class);

	/**
	 * 
	 *	The width of the window in which the form should be rendered 
	 *	(if it's to be rendered in a separate window and not in a tab)
	 */
	private XUIViewBindProperty<Integer> windowWidth = 
		new XUIViewBindProperty<Integer>("windowWidth", this, 600, Integer.class);
	
	/**
	 * Whether or not the "Show Differences" button should be shown when a user 
	 * tries to close a tab with an unsaved object, that button allows the user
	 *  to see the differences between the saved object and the current state 
	 *  of the object in the form
	 */
	private XUIBindProperty<Boolean> showDifferences = 
		new XUIBindProperty<Boolean>("showDifferences", this, false, Boolean.class);

	/**
	 * Whether or not the object associated to
	 *  this form should be edited in orphan mode.
	 */
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
		return renderViewerTitle.getEvaluatedValue();
	}

	/**
	 * Sets the render viewer title.
	 * 
	 * @param renderToolbar the new render viewer title
	 */
	public void setRenderViewerTitle( boolean renderToolbar ) {
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

	/**
	 * Sets the render viewer title.
	 * 
	 * @param renderTitleExpr the new render viewer title
	 */
	public void setRenderViewerTitle( String renderTitleExpr ) {
		this.renderViewerTitle.setExpressionText( renderTitleExpr );
	}

	/**
	 * Gets the render tool bar.
	 * 
	 * @return the render tool bar
	 */
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	/**
	 * Sets the render tool bar.
	 * 
	 * @param renderToolbar the new render tool bar
	 */
	public void setRenderToolBar( boolean renderToolbar ) {
		this.renderToolBar.setValue( renderToolbar );
	}

	/**
	 * Sets the render tool bar.
	 * 
	 * @param renderToolbarExpr the new render tool bar
	 */
	public void setRenderToolBar( String renderToolbarExpr ) {
		this.renderToolBar.setExpressionText( renderToolbarExpr );
	}

	/**
	 * Gets the render viewer messages.
	 * 
	 * @return the render viewer messages
	 */
	public boolean getRenderViewerMessages() {
		return renderViewerMessages.getEvaluatedValue();
	}

	/**
	 * Sets the render viewer messages.
	 * 
	 * @param renderViewerMessages the new render viewer messages
	 */
	public void setRenderViewerMessages( boolean renderViewerMessages ) {
		this.renderViewerMessages.setValue( renderViewerMessages );
	}

	/**
	 * Sets the render viewer messages.
	 * 
	 * @param renderViewerMessagesExpr the new render viewer messages
	 */
	public void setRenderViewerMessages( String renderViewerMessagesExpr ) {
		this.renderViewerMessages.setExpressionText( renderViewerMessagesExpr );
	}

	/**
	 * Gets the render window.
	 * 
	 * @return the render window
	 */
	public boolean getRenderWindow() {
		return renderWindow.getEvaluatedValue();
	}

	/**
	 * Sets the render window.
	 * 
	 * @param renderWindow the new render window
	 */
	public void setRenderWindow( boolean renderWindow ) {
		this.renderWindow.setValue( renderWindow );
	}

	/**
	 * Sets the render window.
	 * 
	 * @param renderWindowExpr the new render window
	 */
	public void setRenderWindow( String renderWindowExpr ) {
		this.renderWindow.setExpressionText( renderWindowExpr );
	}

	/**
	 * Gets the window height.
	 * 
	 * @return the window height
	 */
	public int getWindowHeight() {
		return windowHeight.getEvaluatedValue();
	}

	/**
	 * Sets the window height.
	 * 
	 * @param windowHeight the new window height
	 */
	public void setWindowHeight( int windowHeight ) {
		this.windowHeight.setValue( windowHeight );
	}

	/**
	 * Sets the window height.
	 * 
	 * @param windowHeightExpr the new window height
	 */
	public void setWindowHeight( String windowHeightExpr ) {
		this.windowHeight.setExpressionText( windowHeightExpr );
	}

	/**
	 * Gets the window width.
	 * 
	 * @return the window width
	 */
	public int getWindowWidth() {
		return windowWidth.getEvaluatedValue();
	}

	/**
	 * Sets the window width.
	 * 
	 * @param windowWidth the new window width
	 */
	public void setWindowWidth(int windowWidth) {
		this.windowWidth.setValue( windowWidth );
	}

	/**
	 * Sets the window width.
	 * 
	 * @param windowWidthExpr the new window width
	 */
	public void setWindowWidth(String windowWidthExpr) {
		this.windowWidth.setExpressionText( windowWidthExpr );
	}
	
	/**
	 * Sets the target object.
	 * 
	 * @param sExprText the new target object
	 */
	public void setTargetObject( String sExprText ) {
		this.targetObject.setExpressionText( sExprText );
	}
	
	/**
	 * Gets the target object.
	 * 
	 * @return the target object
	 */
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.components.XUIComponentBase#initComponent()
	 */
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
	
	/**
	 * Creates the edit tool bar.
	 * 
	 * @param pos the pos
	 */
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

	/**
	 * Creates the viewer messages.
	 * 
	 * @param pos the pos
	 */
	private void createViewerMessages( int pos ) {
		ViewerMessages viewerMessages = (ViewerMessages)findComponent( getId() + "_viewerMsgs" );
		if( viewerMessages == null ) {
			viewerMessages = new ViewerMessages();
			viewerMessages.setId( getId() + "_viewerMsgs" );
			getChildren().add( pos, viewerMessages );
		}
		
		MessageBox box = (MessageBox) findComponent( getId() + "_removeAlertBox" );
		if (box == null){
			box = new MessageBox();
			box.setButtons(MessageBox.MessageBoxButtons.YESNO);
			box.setMessageBoxType(MessageBox.MessageBoxType.QUESTION);
			box.setTitle(XEOViewersMessages.FORMEDIT_REMOVE_CONFIRM_TITLE.toString());
			box.setMessage(XEOViewersMessages.FORMEDIT_REMOVE_CONFIRM_MESSAGE.toString());
			box.setId(getId() + "_removeAlertBox");
			box.setActions("['#{viewBean.remove}','#{viewBean.dummy}']");
			getChildren().add( pos++, box );
		}
	}

	/**
	 * Creates the viewer title.
	 * 
	 * @param pos the pos
	 */
	private void createViewerTitle( int pos ) {
		ViewerTitle viewerTitle = (ViewerTitle)findComponent( getId() + "_viewerTitle" );
		if( viewerTitle == null ) {
			viewerTitle = new ViewerTitle();
			viewerTitle.setId( getId() + "_viewerTitle" );
			getChildren().add( pos, viewerTitle );
		}
	}
	
	/**
	 * Creates the edit window.
	 */
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
         * Retrieves the content of a viewer as XML
         * 
         * @return A {@link XMLDocument} with a viewer converted to XML
         */
        private XMLDocument getViewerContentAsXML(XUIViewRoot root)
        {
        	XUIRequestContext r = XUIRequestContext.getCurrentContext();
        	XMLDocument doc;
        	
    		try 
    		{
    			String s =  r.getSessionContext().renderViewToBuffer("XEOXML", root.getViewState() ).toString();
    			doc = ngtXMLUtils.loadXML(s);
    			return doc;
    		} 
    		catch (Exception e) 
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
	        
	        
	        XUIRequestContext.getCurrentContext().getTransactionManager().release();
	        boApplication.currentContext().getEboContext().close();
	        
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
	        	String 			result = XEOListVersionHelper.renderDifferencesWithPreviousVersion(frmComponent,bouiVersion,getViewerContentAsXML(getContext().getViewRoot().getParentView()),newContext);
	        	
	        	response.getWriter().write(result);
				getRequestContext().responseComplete();
			}
			else
			{	//FIXME: Colocar um parametro para isto
				/*
				 * Renders the difference between the current object and the object
				 * saved in the database
				 * 
				 * */
				String result = XEOListVersionHelper.renderDifferencesWithFlashBack(frmComponent, getViewerContentAsXML(getRequestContext().getViewRoot()));
				response.getWriter().write(result);
				getRequestContext().responseComplete();
			}
		}
		
		
		
	
		
	}

}
