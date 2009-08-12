package netgest.bo.xwc.xeo.components;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.framework.XUIBindProperty;

public class FormEdit extends Form {

	private XUIBindProperty<boObject> 	targetObject = 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
	
	private XUIBindProperty<Boolean> renderToolBar = 
		new XUIBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);

	private XUIBindProperty<Boolean> renderViewerTitle = 
		new XUIBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);

	private XUIBindProperty<Boolean> renderViewerMessages = 
		new XUIBindProperty<Boolean>("renderViewerMessages", this, true, Boolean.class);

	private XUIBindProperty<Boolean> renderWindow = 
		new XUIBindProperty<Boolean>("renderWindow", this, true, Boolean.class);
	
	private XUIBindProperty<Integer> windowHeight = 
		new XUIBindProperty<Integer>("windowHeight", this, 400, Integer.class);

	private XUIBindProperty<Integer> windowWidth = 
		new XUIBindProperty<Integer>("windowWidth", this, 600, Integer.class);

	@Override
	public String getRendererType() {
		return "form";
	}
	
	public boolean getRenderViewerTitle() {
		return renderViewerTitle.getEvaluatedValue();
	}

	public void setRenderViewerTitle(boolean renderToolbar) {
		this.renderViewerTitle.setValue( renderToolbar );
	}

	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderToolbar) {
		this.renderToolBar.setValue( renderToolbar );
	}

	public boolean getRenderViewerMessages() {
		return renderViewerMessages.getEvaluatedValue();
	}

	public void setRenderViewerMessages(boolean renderViewerMessages) {
		this.renderViewerMessages.setValue( renderViewerMessages );
	}

	public boolean getRenderWindow() {
		return renderWindow.getEvaluatedValue();
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

	public int getWindowWidth() {
		return windowWidth.getEvaluatedValue();
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth.setValue( windowWidth );
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
		if( getTargetObject().getBoDefinition().getBoCanBeOrphan() ) {
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
			if( !object.getBoDefinition().getBoCanBeOrphan() ) {
				wnd = new ViewerWindow();
				wnd.setId( getId() + "_editWnd" );
				
				wnd.setHeight( getWindowHeight() );
				wnd.setWidth( getWindowWidth() );
				
				// Muda todos os descendentes directos do form, para filhos da janela
				wnd.getChildren().addAll( getChildren() );
				getChildren().clear();
				getChildren().add( wnd );
			}
		}
	}

}
