package netgest.bo.xwc.components.classic.scripts;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XVWScripts {
    
    public static final int WAIT_DIALOG = 1;
    public static final int WAIT_STATUS_MESSAGE= 2;

    public static final String getCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.Command( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + oComponent.getId() + "','"+iWaitMode+"')";
    }

    public static final String getCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return 
            "XVW.Command( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + sValue + "', '"+ iWaitMode +"')";
    }
     
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + oComponent.getId() + "','"+iWaitMode+"')";
    }

    public static final String getAjaxCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + sValue + "', '"+ iWaitMode +"')";
    }

    public static final String getAjaxUpdateValuesScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.getNamingContainerId() +  "', null, null, '"+ iWaitMode +"')";
    }

    public static final String getAlertDialog( String sTitle, String sMessage ) {
    	
    	StringBuilder s = new StringBuilder();
    	if( sMessage == null )
            sMessage = "";
    	
    	JavaScriptUtils.safeJavaScriptWrite(s, sMessage.toCharArray(), '\'');
    	
        return
            "Ext.onReady(function(){ Ext.Msg.show({" + 
            "title: '"+sTitle+"'," +
            "msg: '"+ s.toString() +"'," +
            "buttons:Ext.Msg.OK" +
            "})});";
    }

    public static final String getOpenCommandWindow(  XUIComponentBase oComponent, String sActionValue )    {
        String sFrameName = "Frame_" + oComponent.getId();
        return 
            "XVW.OpenCommandWindow( " +
            "'" + sFrameName + "'," +
            "'" + oComponent.getNamingContainerId() + "'," +
            "'" + oComponent.getId() + "'," +
            "'" + sActionValue + "'" +
            ");";
    }

    
    public static final String getOpenCommandTab(  XUIComponentBase oComponent, String sActionValue )    {
        return getOpenCommandTab( oComponent, sActionValue, null );
    }
    
    public static final String getOpenCommandTab(  XUIComponentBase oComponent, String sActionValue, String sTabTitle )    {
        String sFrameName = "Frame_" + oComponent.getId();
        return 
            "XVW.openCommandTab( " +
            "'" + sFrameName + "'," +
            "'" + oComponent.getNamingContainerId() + "'," +
            "'" + oComponent.getId() + "'," +
            "'" + sActionValue + "'," +
            (sTabTitle!=null? "'" + sTabTitle + "'" : null ) +
            ");";
    }
    
    public static final String getCommandDownloadFrame(  XUIComponentBase oComponent, String sActionValue )    {
        String sFrameName = "Frame_" + oComponent.getId();
        return 
            "XVW.CommandDownloadFrame( " +
            "'" + sFrameName + "'," +
            "'" + oComponent.getNamingContainerId() + "'," +
            "'" + oComponent.getId() + "'," +
            "'" + sActionValue + "'" +
            ");";
    }
    
    public static final String getSyncClientViewScript( XUIViewRoot viewRoot ) {
    	Form form = (Form)viewRoot.findComponent( Form.class );
    	if( form != null ) {
    		return "XVW.syncView('" + form.getClientId() + "');";
    	}
    	return "";
    }
    
    public static final void closeView( XUIViewRoot oViewRoot ) {
    	// Verifica se é um window
    	Window oWnd =  (Window)oViewRoot.findComponent( Window.class );
    	if( oWnd != null ) {
    		// A vista é uma janela... simplesmente fecha a janela
    		oWnd.destroy();
    	} 
    	else {
    		// A vista não têm janela... ou seja deve estar inserida num tab
    		// o ecran inteiro... neste caso corre o script do lado do cliente
    		// para fechar a vista.
            XUIRequestContext oRequestContext;
            oRequestContext = XUIRequestContext.getCurrentContext();
    		oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER ,
    				oViewRoot + "_close",
    				"XVW.closeView('" + oViewRoot.getClientId() + "')"
    		);
    	}
    }

    public static final void setTitle( String sTitle ) {
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_HEADER, 
        		"SetTitle",
        		"XVW.setTitle('" + 
        		JavaScriptUtils.safeJavaScriptWrite( sTitle, '\'') +
        		"');"
        	);
    }
}

