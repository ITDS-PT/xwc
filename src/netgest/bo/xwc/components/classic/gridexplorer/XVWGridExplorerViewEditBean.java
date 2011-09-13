package netgest.bo.xwc.components.classic.gridexplorer;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.GridExplorer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

public class XVWGridExplorerViewEditBean extends XEOEditBean {
	
	public boolean refreshExplorer = false;
	public String  explorerComponentId;
	public boolean isNew = false;
	
	public void setExplorerComponentId( String explorerId ) {
		this.explorerComponentId = explorerId;
	}
	
	public void createExplorerView( String explorerComponentId, String explorerViewId ) {
		try {
			this.isNew = true;
			this.explorerComponentId = explorerComponentId;
			createNew("XVWGridExplorerView");
			getXEOObject().getAttribute("gridExplorerId").setValueString( explorerViewId );
			getXEOObject().setChanged( false );
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void save() throws boRuntimeException {
		// TODO Auto-generated method stub
		super.save();
		if( isValid() ) {
			refreshExplorer = true;
		}
	}
	
	@Override
	public void saveAndClose() throws boRuntimeException {
		
		XUIViewRoot parentView = getParentView();
		
		super.saveAndClose();

		Long savedBoui = getXEOObject().getBoui(); 
		
		GridExplorer gridExplorer = (GridExplorer)parentView
			.findComponent( this.explorerComponentId );
		
		// Associar à nova vista
		gridExplorer.setCurrentSavedViewBOUI(savedBoui);
		gridExplorer.saveUserState( true );
		
		if( isNew ) {  
			// Colocar a vista default com os valores default
			gridExplorer.setCurrentSavedViewBOUI(null);
			gridExplorer.resetToDefaults();
			gridExplorer.saveUserState( true );
			
			// Volta a activar a vista gravada
			gridExplorer.setCurrentSavedViewBOUI(savedBoui);
			gridExplorer.restoreUserState();
		}
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		oRequestContext.setViewRoot( parentView );
	}
	
	@Override
	public void closeView() {
		/*
		XUIViewRoot parentView = getParentView();
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XVWScripts.closeView( oRequestContext.getViewRoot() );
		oRequestContext.setViewRoot( parentView );
		*/
		super.closeView();
	}
}
