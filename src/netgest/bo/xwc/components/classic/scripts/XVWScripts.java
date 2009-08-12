package netgest.bo.xwc.components.classic.scripts;

import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XVWScripts {
    
    public static final int WAIT_DIALOG = 1;
    public static final int WAIT_STATUS_MESSAGE= 2;
    
    public static final int ALERT_ICON_INFO = 1;
    public static final int ALERT_ICON_ERROR = 2;
    public static final int ALERT_ICON_WARNING = 3;

    public static final String getCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.Command( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + oComponent.getId() + "','"+iWaitMode+"')";
    }

    public static final String getCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return 
            "XVW.Command( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + sValue + "', '"+ iWaitMode +"')";
    }
     
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
    	assert oComponent != null : "The component is null!";
    	String containerId = oComponent.getNamingContainerId();
    	assert containerId != null : "Cannot find a naming container for the component"; 

    	
    	String compId = oComponent.getId();
    	
        return 
            "XVW.AjaxCommand( '" + containerId +  "','" + compId + "','" + compId + "','"+iWaitMode+"')";
    }

    public static final String getAjaxCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.getNamingContainerId() +  "','" + oComponent.getId() + "','" + sValue + "', '"+ iWaitMode +"')";
    }

    public static final String getAjaxUpdateValuesScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.getNamingContainerId() +  "', null, null, '"+ iWaitMode +"')";
    }

    public static final String getAlertDialog( String sTitle, String sMessage, int icon ) {
    	
    	StringBuilder s = new StringBuilder();
    	if( sMessage == null )
            sMessage = "";
    	
    	JavaScriptUtils.safeJavaScriptWrite(s, sMessage, '\'');
    	ExtConfig msgBoxConfig = new ExtConfig();
    	msgBoxConfig.addJSString( "title", sTitle );
    	msgBoxConfig.addJSString( "msg", sMessage.replaceAll("\n", "<br/>") );
    	msgBoxConfig.add( "buttons", "Ext.Msg.OK" );
    	switch ( icon ) {
    		case ALERT_ICON_ERROR:
    			msgBoxConfig.add("icon", "Ext.MessageBox.ERROR" );
     			break;
    		case ALERT_ICON_WARNING:
    			msgBoxConfig.add("icon", "Ext.MessageBox.WARNING" );
    			break;
    		case ALERT_ICON_INFO:
    			msgBoxConfig.add("icon", "Ext.MessageBox.INFO" );
    			break;
    		default:
    	}
    	
        return
            "Ext.onReady(function() { Ext.Msg.show( "  + msgBoxConfig.renderExtConfig() + "  );})";
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
		// A vista não têm janela... ou seja deve estar inserida num tab
		// o ecran inteiro... neste caso corre o script do lado do cliente
		// para fechar a vista.
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
		oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER ,
				oViewRoot + "_close",
				getCloseViewScript( oViewRoot )
		);
		oViewRoot.dispose();
    	oViewRoot.setTransient( true );
    }

    public static final String getCloseViewScript( XUIViewRoot oViewRoot ) {
    	// Verifica se é um window
    	StringBuilder sb = new StringBuilder(  );
    	Window oWnd =  (Window)oViewRoot.findComponent( Window.class );
    	if( oWnd != null ) {
    		// A vista é uma janela... simplesmente fecha a janela
    		sb
    		.append( "Ext.onReady( function() { " )
	        .append( "if( "+oWnd.getId()+" )")
	        .append( oWnd.getId() )
	        .append(  ".destroy();")
	        .append(    "else if(window.parent.")
	        .append(oWnd.getId())
	        .append(") window.parent.")
	        .append( oWnd.getId())
	        .append( ".destroy();")
	        .append(  "});\n");    		
	       oWnd.destroy();
    	} 
    	else {
    		// A vista não têm janela... ou seja deve estar inserida num tab
    		// o ecran inteiro... neste caso corre o script do lado do cliente
    		// para fechar a vista.
    		sb.append( "XVW.closeView('" + oViewRoot.getClientId() + "')" );
    	}
    	return sb.toString();
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

