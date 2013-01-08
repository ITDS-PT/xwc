package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.framework.XUIBindProperty;

/**
 * 
 * A Form component to display a list of XEO Objects
 * 
 * @author jcarreira
 *
 */
public class FormList extends Form {
	

	/**
	 * If the title of the viewer should be rendered
	 */
	private XUIBindProperty<Boolean> renderViewerTitle = 
		new XUIBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);

	public boolean getRenderViewerTitle() {
		return renderViewerTitle.getEvaluatedValue();
	}

	public void setRenderViewerTitle(boolean renderViewer ) {
		this.renderViewerTitle.setValue( renderViewer );
	}

	public void setRenderViewerTitle(String renderViewerExpr ) {
		this.renderViewerTitle.setExpressionText( renderViewerExpr );
	}

	@Override
	public void initComponent() {
		int position = 0;
		
		// Create the default toolbar for list object
		if( getRenderViewerTitle() ) {
			createViewerTitle( position++ );
		}

		if (rendererType.isDefaultValue( )){
			rendererType.setValue( "form" );
		}
		
		super.initComponent();
		
		
	}
	
	
	private void createViewerTitle( int pos ) {
		ViewerTitle viewerTitle = new ViewerTitle();
		viewerTitle.setId( getId() + "_viewerTitle" );
		getChildren().add( pos, viewerTitle );
	}
	
	
	
}
