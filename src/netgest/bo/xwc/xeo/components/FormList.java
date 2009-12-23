package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.framework.XUIBindProperty;

public class FormList extends Form {
	
	@Override
	public String getRendererType() {
		return "form";
	}

	private XUIBindProperty<Boolean> renderViewerTitle = 
		new XUIBindProperty<Boolean>("renderViewerTitle", this, true, Boolean.class);
	
	public boolean getRenderViewerTitle() {
		return renderViewerTitle.getEvaluatedValue();
	}

	public void setRenderViewerTitle(boolean renderViewer ) {
		this.renderViewerTitle.setValue( renderViewer );
	}

	@Override
	public void initComponent() {
		int position = 0;
		
		// Create the default toolbar for list object
		if( getRenderViewerTitle() ) {
			createViewerTitle( position++ );
		}

		super.initComponent();
	}
	
	private void createViewerTitle( int pos ) {
		ViewerTitle viewerTitle = new ViewerTitle();
		viewerTitle.setId( getId() + "_viewerTitle" );
		getChildren().add( pos, viewerTitle );
	}
	
	
	
}
