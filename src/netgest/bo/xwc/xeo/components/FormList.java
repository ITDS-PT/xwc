package netgest.bo.xwc.xeo.components;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
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

	public void setRenderViewerTitle(boolean renderToolbar) {
		this.renderViewerTitle.setValue( renderToolbar );
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
