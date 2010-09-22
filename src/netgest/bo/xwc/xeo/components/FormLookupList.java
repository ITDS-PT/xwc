package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;

/**
 * 
 *  A Form to display a list of XEO Objects so that one (or more)
 *  objects can be selected
 * 
 * 
 * @author jcarreira
 *
 */
public class FormLookupList extends Form {

	/**
	 * If the title of the viewer should be rendered
	 */
	private XUIBindProperty<Boolean> renderViewerTitle = 
		new XUIBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);

	/**
	 * Whether the window to place the form should be rendered or not
	 */
	private XUIBindProperty<Boolean> renderWindow = 
		new XUIBindProperty<Boolean>("renderWindow", this, true, Boolean.class);
	
	/**
	 * The height of the window where the form is rendered
	 */
	private XUIViewBindProperty<Integer> windowHeight = 
		new XUIViewBindProperty<Integer>("windowHeight", this, 400, Integer.class);

	/**
	 * The width of the window where the form is rendered
	 */
	private XUIViewBindProperty<Integer> windowWidth = 
		new XUIViewBindProperty<Integer>("windowWidth", this, 600, Integer.class);

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
