package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.framework.XUIBindProperty;

public class FormLookupList extends Form {

	private XUIBindProperty<Boolean> renderViewerTitle = 
		new XUIBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);

	private XUIBindProperty<Boolean> renderWindow = 
		new XUIBindProperty<Boolean>("renderWindow", this, true, Boolean.class);
	
	private XUIBindProperty<Integer> windowHeight = 
		new XUIBindProperty<Integer>("windowHeight", this, 400, Integer.class);

	private XUIBindProperty<Integer> windowWidth = 
		new XUIBindProperty<Integer>("windowWidth", this, 600, Integer.class);

	public boolean getRenderViewerTitle() {
		return renderViewerTitle.getEvaluatedValue();
	}

	public void setRenderViewerTitle(boolean renderEditToolbar) {
		this.renderViewerTitle.setValue( renderEditToolbar );
	}

	public void setRenderWindow(boolean renderWindow) {
		this.renderWindow.setValue( renderWindow );
	}

	public boolean getRenderWindow() {
		return this.renderWindow.getEvaluatedValue();
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
	
	@Override
	public String getRendererType() {
		return "form";
	}
	
	@Override
	public void initComponent() {
		
		super.initComponent();

		// Create the default toolbar for the object
		if( getRenderViewerTitle() )
			createViewerTitle();
		
		if( getRenderWindow() ) {
			createEditWindow();
		}
		
	}
	
	private void createViewerTitle() {
		ViewerTitle viewerTitle = (ViewerTitle)findComponent( getId() + "_viewerTitle" );
		if( viewerTitle == null ) {
			viewerTitle = new ViewerTitle();
			viewerTitle.setId( getId() + "_viewerTitle" );
			getChildren().add( viewerTitle );
		}
	}
	
	private void createEditWindow() {
		ViewerWindow wnd;

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
