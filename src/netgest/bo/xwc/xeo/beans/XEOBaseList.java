package netgest.bo.xwc.xeo.beans;

import java.math.BigDecimal;

import javax.faces.event.ActionEvent;

import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;

public class XEOBaseList extends XEOBaseBean {
    
    boObjectList currentObjectList;
    
    public void executeBoql( String boql ) {
        currentObjectList = boObjectList.list( boApplication.currentContext().getEboContext(), boql, 1, 50 );
        //currentObjectList.beforeFirst();
    }
    public GridColumnRenderer getMyRenderer() {    
	return new GridColumnRenderer() {
        public String render( GridPanel grid, DataRecordConnector record, DataFieldConnector field ) {
               String oValue = (String) field.getValue();
            return "<i>" + oValue + "</i><script type='text/javascript' src='http://wiki.itds.pt/pub/TWiki/TinyMCEPlugin/tinymce/jscripts/tiny_mce/themes/advanced/langs/en.js'></script><script type='text/javascript' src='http://wiki.itds.pt/pub/TWiki/TinyMCEPlugin/tinymce/jscripts/tiny_mce/plugins/twikibuttons/langs/en.js'></script><script type='text/javascript' src='http://wiki.itds.pt/pub/TWiki/TinyMCEPlugin/tinymce/jscripts/tiny_mce/plugins/twikiimage/langs/en.js'></script>;";

            };
            };
}
       public void arquivarQuestion() {
    	  Dialogs.showDialog( getViewRoot(), "formUser:myMessageBox" );
    	 }
       public void ok() {
    	   // O utilizador pressionou no ok
    	   // Do Work
    	  }
    	  public void cancel() {
    	   // O utilizador pressionou em cancelar
    	   // Do Work
    	  }

    public void executeBoql( String boql, Object[] params ) {
        currentObjectList = boObjectList.list( boApplication.currentContext().getEboContext(), boql, params, 1, 50, false );
    }

    public XEOObjectListConnector getDataList() {
        EboContext oEboContext = boApplication.currentContext().getEboContext();
        if( currentObjectList.getEboContext() != oEboContext ) {
            currentObjectList.setEboContext( oEboContext );
            //currentObjectList.refreshData();
        }
        return new XEOObjectListConnector( this.currentObjectList );
    }
    
    @Visible
    public void rowDoubleClick(  ) throws Exception {
        ActionEvent oEvent;
        GridPanel oGridPanel;
        DataRecordConnector oActiveRow;
        XUIRequestContext    oRequestContext;

        oRequestContext = XUIRequestContext.getCurrentContext();

        oEvent = oRequestContext.getEvent();
        oGridPanel = (GridPanel)oEvent.getComponent().getParent();
        oActiveRow = oGridPanel.getActiveRow();
        
        if( oActiveRow != null ) {
	        BigDecimal boui = (BigDecimal)oActiveRow.getAttribute("BOUI").getValue();
	        if( boui == null ) {
	        	oRequestContext.addMessage( "bean" ,  
	        			new XUIMessage( 
	        					XUIMessage.TYPE_POPUP_MESSAGE, 
	        					XUIMessage.SEVERITY_ERROR, 
	        					BeansMessages.TITLE_ERROR.toString(), 
	        					BeansMessages.SELECTED_OBJECT_NOT_EXISTS.toString() 
	        			)
	        	);
	        	XUIViewRoot viewRoot = oRequestContext.getSessionContext().createView("netgest/bo/xwc/components/viewers/Dummy.xvw");
	        	oRequestContext.setViewRoot( viewRoot );
	        	XVWScripts.closeView( viewRoot );
	        }
	        else {
		        boObject sObjectToOpen = boObject.getBoManager().loadObject( boApplication.currentContext().getEboContext(), boui.longValue());
		
		        XUIViewRoot oEditViewRoot = oRequestContext.getSessionContext().createView( 
		        		getViewerResolver().getViewer( sObjectToOpen, XEOViewerResolver.ViewerType.EDIT )
		        );
		        XEOEditBean oEditBean = (XEOEditBean)oEditViewRoot.getBean("viewBean");
		        
		        if( oActiveRow == null ) {
		        	oRequestContext.addMessage( "bean" ,  
		        			new XUIMessage( 
		        					XUIMessage.TYPE_ALERT, 
		        					XUIMessage.SEVERITY_ERROR, 
		        					BeansMessages.TITLE_ERROR.toString(), 
		        					BeansMessages.SELECTED_OBJECT_NOT_EXISTS.toString() 
		        			)
		        	);
		        	oRequestContext.setViewRoot( oRequestContext.getSessionContext().createView("netgest/bo/xwc/components/viewers/Error.xvw") );
		        }
		        else {
		        	oEditBean.setCurrentObjectKey( String.valueOf( oActiveRow.getAttribute("BOUI").getValue() ) );
		            oRequestContext.setViewRoot( oEditViewRoot );
		            oEditViewRoot.processInitComponents();
		        }
		        
		        //TODO: Call must be not necessary
		        oRequestContext.renderResponse();
	        }
        }
        else {
        	oRequestContext.setViewRoot( oRequestContext.getSessionContext().createView("netgest/bo/xwc/components/viewers/Dummy.xvw") );
	        oRequestContext.renderResponse();
        }
    }
    
    @Visible
    public void addNew() throws Exception {
        XUIRequestContext    oRequestContext;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        if( oRequestContext.isAjaxRequest() ) {
        	
        	XUIComponentBase oCommand = oRequestContext.getEvent().getComponent();
        	String frameId = "addNew_"+System.currentTimeMillis();
			oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , "addNew",
        			XVWScripts.getOpenCommandTab( frameId, oCommand , "", null)
        	);
        }
        else {
	        String sObjectName = currentObjectList.getBoDef().getName();
        	if( oRequestContext.getEvent().getComponent() instanceof XUICommand ) {
	        	XUICommand	eventComp = (XUICommand) oRequestContext.getEvent().getComponent();
	        	sObjectName = String.valueOf( eventComp.getValue() );
	        	if( boDefHandler.getBoDefinition( sObjectName ) == null ) {
	        		sObjectName = null;
	        	}
        	}
        	
        	if( sObjectName == null ) {
        		sObjectName = currentObjectList.getBoDef().getName();        		
        	}
	
	        XUIViewRoot oEditViewRoot = oRequestContext.getSessionContext().createChildView(
	        		getViewerResolver().getViewer( sObjectName, XEOViewerResolver.ViewerType.EDIT )
	        	);
	        XEOEditBean oEditBean = (XEOEditBean)oEditViewRoot.getBean("viewBean");
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
    	if( this.currentObjectList != null ) {
    		QLParser qp = this.currentObjectList.getQLParser();
    		if( qp != null ) {
	    		boDefHandler oBoDef = qp.getObjectDef();
	    		if( oBoDef != null ) {
	    			if( sTitle == null ) {
	    				sTitle =  BeansMessages.LIST_OF.toString() + " " + oBoDef.getLabel();
	    			}
	    			sTitle = "<img align='middle' src='resources/" + oBoDef.getName() + "/ico16.gif'/>&nbsp;" + sTitle;
	    		}
    		}
    	}
    	return sTitle;
    }
    
    public GridRowRenderClass getRowClass() {
    	return new XEOGridRowClassRenderer();
    }
    
    @Visible
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
