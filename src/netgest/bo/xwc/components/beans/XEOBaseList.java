package netgest.bo.xwc.components.beans;

import java.math.BigDecimal;

import javax.faces.event.ActionEvent;

import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XEOBaseList extends XEOBase {
    
    boObjectList currentObjectList;
    
    public void executeBoql( String boql ) {
        currentObjectList = boObjectList.list( boApplication.currentContext().getEboContext(), boql, 1, 50 );
        //currentObjectList.beforeFirst();
    }

    public void executeBoql( String boql, Object[] params ) {
        currentObjectList = boObjectList.list( boApplication.currentContext().getEboContext(), boql, params, 1, 50, false );
    }

    public DataListConnector getDataList() {
        EboContext oEboContext = boApplication.currentContext().getEboContext();
        if( currentObjectList.getEboContext() != oEboContext ) {
            currentObjectList.setEboContext( oEboContext );
            //currentObjectList.refreshData();
        }
        return new XEOObjectListConnector( this.currentObjectList );
    }
    
    public void rowDoubleClick(  ) throws Exception {        
        ActionEvent oEvent;
        GridPanel oGridPanel;
        DataRecordConnector oActiveRow;
        XUIRequestContext    oRequestContext;

        
        oRequestContext = XUIRequestContext.getCurrentContext();

        oEvent = oRequestContext.getEvent();
        oGridPanel = (GridPanel)oEvent.getComponent().getParent();
        oActiveRow = oGridPanel.getActiveRow();
        
        
        BigDecimal boui = (BigDecimal)oActiveRow.getAttribute("BOUI").getValue();
        
        String sObjectName = boObject.getBoManager().getClassNameFromBOUI( boApplication.currentContext().getEboContext(), boui.longValue());
        

        XUIViewRoot oEditViewRoot = oRequestContext.getSessionContext().createView( sObjectName + "_edit.xvw");
        XEOBaseBean oEditBean = (XEOBaseBean)oEditViewRoot.getBean("viewBean");
        
        if( oActiveRow == null ) {
        	oRequestContext.addMessage( "bean" ,  
        			new XUIMessage( 
        					XUIMessage.TYPE_ALERT, 
        					XUIMessage.SEVERITY_ERROR, 
        					"Erro", 
        					"O objecto selecionado não existe!" 
        			)
        	);
        	oRequestContext.setViewRoot( oRequestContext.getSessionContext().createView("error.xvw") );
        }
        else {
        	oEditBean.setCurrentObjectKey( String.valueOf( oActiveRow.getAttribute("BOUI").getValue() ) );
            oRequestContext.setViewRoot( oEditViewRoot );
            oEditViewRoot.processInitComponents();
        }
        
        //TODO: Call must be not necessary
        oRequestContext.renderResponse();
    }
    
    public void addNew() throws Exception {
        XUIRequestContext    oRequestContext;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        if( oRequestContext.isAjaxRequest() ) {
        	oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , "addNew", 
        			XVWScripts.getOpenCommandTab( oRequestContext.getEvent().getComponent() , "")
        	);
        }
        else {
	        String sObjectName = currentObjectList.getBoDef().getName();
	
	        XUIViewRoot oEditViewRoot = oRequestContext.getSessionContext().createView( sObjectName + "_edit.xvw");
	        XEOBaseBean oEditBean = (XEOBaseBean)oEditViewRoot.getBean("viewBean");
	        oEditBean.createNew( sObjectName );
	
	        oRequestContext.setViewRoot( oEditViewRoot );
	        
	        //TODO: Call must be not necessary
	        oEditViewRoot.processInitComponents();
	        oRequestContext.renderResponse();
        }
    }

    public boObjectList getCurrentObjectList() {
        return currentObjectList;
    }
    
    public String getTitle() {
    	String sTitle = super.getTitle();
    	if( sTitle == null ) {  
	    	if( this.currentObjectList != null ) {
	    		QLParser qp = this.currentObjectList.getQLParser(); 
	    		boDefHandler oBoDef = qp.getObjectDef();
	    		if( oBoDef != null ) {
	    			sTitle = "Lista de " + oBoDef.getLabel();
	    		}
	    	}
	    	return sTitle;
    	}
    	return sTitle;
    }
    
    public GridRowRenderClass getRowClass() {
    	return new XEOGridRowClassRenderer();
    }
    
	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
            }
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
}
