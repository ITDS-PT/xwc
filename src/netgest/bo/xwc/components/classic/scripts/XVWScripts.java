package netgest.bo.xwc.components.classic.scripts;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.MessageBox.MessageBoxButtons;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.renderers.ComponentWebResourcesCleanup;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class XVWScripts {
    
    public static final int WAIT_DIALOG = 1;
    public static final int WAIT_STATUS_MESSAGE= 2;
    
    public enum WaitMode{
    	LOCK_SCREEN(1),
    	DONT_LOCK_SCREEN(0);
    	
    	private int waitMode;
    	
    	private WaitMode(int mode){
    		this.waitMode = mode;
    	}
    	
    	public int getWaitMode(){
    		return this.waitMode;
    	}
    }
    
    public static final int ALERT_ICON_INFO = 1;
    public static final int ALERT_ICON_ERROR = 2;
    public static final int ALERT_ICON_WARNING = 3;
    
     public enum Queue{
    	QUEUE_COMMAND(true),
    	DONT_QUEUE_DOMMAND(false);
    	
    	private boolean queue;
    	
    	private Queue(boolean q){
    		this.queue = q;
    	}
    	
    	public boolean toBoolean(){
    		return queue;
    	}
    	
    	public static Queue fromBoolean(boolean queue){
    		if (queue)
    			return QUEUE_COMMAND;
    		else
    			return DONT_QUEUE_DOMMAND;
    	}
    }
public enum ValueType{LITERAL,
    	VAR;
    	
    	public static ValueType fromString(String value){
    		for (ValueType current : values( )){
    			if (current.name( ).equalsIgnoreCase( value ))
    				return current;
    		}
    		return LITERAL;
    	}
    }  
  public static final String getCommandScript( String target, XUIComponentBase oComponent, int iWaitMode ) { 
    	return getCommandScript( target, null,oComponent, null, iWaitMode, ValueType.LITERAL );
    }
    
    public static final String getCommandScript( String target, XUIComponentBase oComponent, int iWaitMode , ValueType type) { 
    	return getCommandScript( target, null,oComponent, null, iWaitMode, type );
    }

    public static final String getCommandScript( String target, String targetName, XUIComponentBase oComponent, int iWaitMode ) { 
    	return getCommandScript( target, targetName,oComponent, null, iWaitMode, ValueType.LITERAL );
    }
    
    public static final String getCommandScript( String target, String targetName, XUIComponentBase oComponent, String actionValue , int iWaitMode ) {
    	return getCommandScript(target, targetName ,oComponent, actionValue ,iWaitMode, ValueType.LITERAL);
    }

    public static final String getCommandScript( String target, XUIComponentBase oComponent, String actionValue , int iWaitMode ) {
    	return getCommandScript(target, null,oComponent, actionValue ,iWaitMode, ValueType.LITERAL);
    }
    public static final String getCommandScript( String target, String targetName, XUIComponentBase oComponent, 
    		String actionValue , int iWaitMode, ValueType type ) 
    {
    	
    	if( "blank".equalsIgnoreCase( target ) ) 
    	{
    		return getOpenCommandBlankWindow( targetName ,oComponent, actionValue );
    	}
    	else if( "window".equalsIgnoreCase( target ) ) {
    		return getOpenCommandWindow( oComponent, actionValue, target );
    	}
    	else if( "tab".equalsIgnoreCase( target ) ) {
    		return getOpenCommandTab( targetName, oComponent, actionValue );
    	}
    	else if ("noCloseTab".equalsIgnoreCase(target)){
    		return getOpenCommandTab(targetName, oComponent, actionValue, null, false);
    	}
    	else if ("alwaysNewTab".equalsIgnoreCase(target)){
    		return getOpenCommandTab(targetName, oComponent, actionValue, null, true, true);
    	}
    	else if( "download".equalsIgnoreCase( target ) ) {
    		return getCommandDownloadFrame( oComponent, actionValue);
    	}
    	else if( "self".equalsIgnoreCase( target ) ) {
    		return getAjaxCommandScript( oComponent, actionValue, iWaitMode );
    	}
    	else if( "top".equalsIgnoreCase( target ) ) {
    		return getCommandScript( oComponent, actionValue, iWaitMode );
    	}
    	else if (target == null)
    		return getAjaxCommandScript( oComponent, actionValue, iWaitMode );	
    	else if( target != null ) 
    	{
    		//The Window can have parameters, which will come embedded in the target, like this:
    		// window:{width: 500, height: 300} (The parameters after ':' is a JSON object
    		if (target.startsWith("window:"))
    			return getOpenCommandWindow( oComponent, actionValue, target);
    	}
    	return getAjaxCommandScript( oComponent, actionValue, iWaitMode );
		
    }
    
    public static final String getCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.Wait( " + iWaitMode + " );\n" + 
            "XVW.Command( '" + oComponent.findParentComponent(XUIForm.class).getClientId() +  "','" + oComponent.getClientId() + "','" + oComponent.getId() + "','"+iWaitMode+"')";
    }
    
    public static final String getCommandScript( String containerId, String commandId) {
        return 
            "XVW.Command( '" + containerId +  "','" + commandId + "','" + commandId + "','"+WAIT_DIALOG+"')";
    }
    
    public static final String getCommandScript( String containerId, String commandId, String value) {
        return 
            "XVW.Command( '" + containerId +  "','" + commandId + "','" + value + "','"+WAIT_DIALOG+"')";
    }
    
    public static final String getCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return 
            "XVW.Command( '" + oComponent.findParentComponent(XUIForm.class).getClientId() +  "','" + oComponent.getClientId() + "','" + sValue + "', '"+ iWaitMode +"')";
    }
    
    public static final String getAjaxCommandScript( String containerId, String compId , String value ) {
    	assert compId != null : MessageLocalizer.getMessage("THE_COMPONENT_IS_NULL");
    	assert containerId != null : MessageLocalizer.getMessage("CANNOT_FIND_A_NAMING_CONTAINER_FOR_THE_COMPONENT"); 
    	
    	if (StringUtils.isEmpty( value ))
    		value = compId;
    	
        return 
            "XVW.AjaxCommand( '" + containerId +  "','" + compId + "','" + value + "','"+WAIT_DIALOG+"')";
    }

    public static final String getAjaxCommandScript( XUIComponentBase oComponent, int iWaitMode ) {
    	return getAjaxCommandScript( oComponent , iWaitMode, Queue.QUEUE_COMMAND );
    }
    
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, WaitMode mode ) {
    	return getAjaxCommandScript( oComponent , mode.getWaitMode(), Queue.QUEUE_COMMAND );
    }

     
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, int iWaitMode, Queue queue ) {
    	assert oComponent != null : MessageLocalizer.getMessage("THE_COMPONENT_IS_NULL");
    	String containerId = oComponent.findParentComponent(XUIForm.class).getClientId();
    	assert containerId != null : MessageLocalizer.getMessage("CANNOT_FIND_A_NAMING_CONTAINER_FOR_THE_COMPONENT"); 

    	
    	String compId = oComponent.getClientId();
    	
        return 
            "XVW.AjaxCommand( '" + containerId +  "','" + compId + "','" + compId + "','"+iWaitMode+"', true, null, "+queue.toBoolean( )+")";
    }

    public static final String getAjaxCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode ) {
        return getAjaxCommandScript( oComponent , sValue , iWaitMode, ValueType.LITERAL );
    }
    
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, String sValue, WaitMode waitMode ) {
        return getAjaxCommandScript( oComponent , sValue , waitMode.getWaitMode(), ValueType.LITERAL );
    }
    
    public static final String getAjaxCommandScript( XUIComponentBase oComponent, String sValue, int iWaitMode, ValueType type ) {
    	if (sValue == null)
    		sValue = "";
        if (type == ValueType.LITERAL )
            return "XVW.AjaxCommand( '" + oComponent.findParentComponent(XUIForm.class).getClientId() +  "','" + oComponent.getClientId() + "','" + sValue + "', '"+ iWaitMode +"')";
        else
        	return "XVW.AjaxCommand( '" + oComponent.findParentComponent(XUIForm.class).getClientId() +  "','" + oComponent.getClientId() + "'," + sValue + ", '"+ iWaitMode +"')"; 
    }
    
    
    public static final String getAjaxUpdateValuesScript( XUIComponentBase oComponent, int iWaitMode ) {
        return 
            "XVW.AjaxCommand( '" + oComponent.findParentComponent(XUIForm.class).getClientId() +  "', null, null, '"+ iWaitMode +"')";
    }
    
    public static final String getAjaxUpdateValuesScript( XUIComponentBase oComponent, WaitMode iWaitMode ) {
        return getAjaxUpdateValuesScript(oComponent,iWaitMode.getWaitMode());
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
    	msgBoxConfig.addJSString( "id", "xeo"+icon+"" );
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
    	
    	//Added to reuse the ExtXeo MessageBox component
		ExtConfig btnTextConfig = new ExtConfig();
		ScriptBuilder sb = new ScriptBuilder();
		sb.l( "function(o) { " );
		sb.endBlock();
		btnTextConfig.addJSString("ok", MessageBoxButtons.OK.toString());
		msgBoxConfig.add( "buttonText" , btnTextConfig.renderExtConfig() );
		msgBoxConfig.add( "fn" , sb.toString() );
    	
        return
            "Ext.onReady(function() { ExtXeo.MessageBox.show( "  + msgBoxConfig.renderExtConfig() + "  );})";
    }

    public static final String getOpenCommandWindow(  XUIComponentBase oComponent )    {
    	return getOpenCommandWindow(  oComponent, null, null );
    }
    
    public static final String getOpenCommandWindow(  XUIComponentBase oComponent, String sActionValue ) 
    {
    	return getOpenCommandWindow(oComponent, sActionValue, null);
    }
    
    public static final String getOpenCommandWindow(  XUIComponentBase oComponent, String sActionValue, String parameters )    {
        String sFrameName = "Frame_" + oComponent.getId();
      
    	String width = "500";
    	String height = "500";
    	String title = "";
    	try
    	{
        	if (parameters != null)
        	{
        		parameters = parameters.replace( "window:" , "" );
        		if (parameters.contains("{"))
        		{	//We assume that if a "{" is present then we have a JSON object
        			//with the values
	        		JSONObject jsonParameters = new JSONObject(parameters);
					if (jsonParameters.has("width"))
						width = jsonParameters.getString("width");
					if (jsonParameters.has("height"))
						height = jsonParameters.getString("height");
					if (jsonParameters.has("title"))
						title = jsonParameters.getString("title");
        		}
        	}
    	}
    	catch (JSONException e)
    	{
    		e.printStackTrace();
    	}
				
    	String command = 
	    	"XVW.OpenCommandWindow( " +
	        "'" + sFrameName + "'," +
	        "'" + oComponent.findParentComponent(XUIForm.class).getClientId() + "'," +
	        "'" + oComponent.getClientId() + "'" +
	        
	        (sActionValue == null?",'',":
	        	",'" 
	        	+ JavaScriptUtils.writeValue( sActionValue ) + "'," ) +
	        	
	        "'" + width +  "'," +
	        "'" + height + "'," +
	        "'" + title + "'" +
	        ");"
	        ;
	        	
    	return command;
    }
    
    public static final String getOpenCommandWindow(  String formId, String windowId, String sActionValue )    {
        String sFrameName = "Frame_" + windowId;
      
    	String width = "500";
    	String height = "500";
    	String title = "";
    	
    	String command = 
	    	"XVW.OpenCommandWindow( " +
	        "'" + sFrameName + "'," +
	        "'" + formId + "'," +
	        "'" + windowId + "'" +
	        
	        (sActionValue == null?",'',":
	        	",'" 
	        	+ JavaScriptUtils.writeValue( sActionValue ) + "'," ) +
	        	
	        "'" + width +  "'," +
	        "'" + height + "'," +
	        "'" + title + "'" +
	        ");"
	        ;
	        	
    	return command;
    }
   

    public static final String getOpenCommandBlankWindow(  XUIComponentBase oComponent, String sActionValue )    {
    	
    	return getOpenCommandBlankWindow( null , oComponent,sActionValue );
    	
    }
    public static final String getOpenCommandBlankWindow(  String targetName, XUIComponentBase oComponent, String sActionValue )    {
        String sFrameName = "Frame_" + oComponent.getId();
        return 
			"{var oForm=document.getElementById('" + oComponent.findParentComponent(XUIForm.class).getClientId() +"');\n" +
			"var oldTrg=oForm.target;\n" +
			"oForm.target='"+ sFrameName +"';\n" +
			XVWScripts.getCommandScript( oComponent, XVWScripts.WAIT_DIALOG ) +";\n" +
			"oForm.target=oldTrg;\n}";
    }
    
    public static final String getOpenCommandTab(  XUIComponentBase oComponent, String sActionValue )    {
        return getOpenCommandTab( null, oComponent, sActionValue, null );
    }
    
    public static final String getOpenCommandTab( String targetName,  XUIComponentBase oComponent, String sActionValue )    {
        return getOpenCommandTab( targetName ,oComponent, sActionValue, null );
    }

    public static final String getOpenCommandTab(  XUIComponentBase oComponent, String sActionValue, String sTabTitle )    {
    	return getOpenCommandTab(  null, oComponent, sActionValue, sTabTitle );    
    }
    public static final String getOpenCommandTab(  String targetName, XUIComponentBase oComponent, String sActionValue, String sTabTitle )    {
        return getOpenCommandTab(targetName, oComponent, sActionValue, sTabTitle, true);
    }
    
    public static final String getOpenCommandTab(  String targetName, XUIComponentBase oComponent, String sActionValue, String sTabTitle, boolean closeableTab )    {
    	return getOpenCommandTab(  targetName, oComponent, sActionValue, sTabTitle, closeableTab, false );
    }
    
    public static final String getOpenCommandTab(  String targetName, XUIComponentBase oComponent, String sActionValue, String sTabTitle, boolean closeableTab, boolean alwaysNewTab )    {
    	String sFrameName =  targetName!=null?targetName:"Frame_" + oComponent.getId();
    	if (alwaysNewTab){
    		sFrameName = "new Date().valueOf()";
    	}else{
    		sFrameName = "'" + sFrameName + "'"; 
    	}
        return 
            "XVW.openCommandTab( " +
            sFrameName+"," +
            "'" + oComponent.findParentComponent(XUIForm.class).getClientId() + "'," +
            "'" + oComponent.getId() + "'," +
            "'" + JavaScriptUtils.writeValue( sActionValue ) + "'," +
            (sTabTitle!=null? "'" + JavaScriptUtils.writeValue( sTabTitle ) + "'" : null ) + ","
            + closeableTab +
            ");";
    }
    
    public static final String getOpenCommandTab(  String targetName, String tabId, String formId,  String sActionValue, String sTabTitle, boolean closeableTab, boolean alwaysNewTab )    {
    	String sFrameName =  targetName!=null?targetName:"Frame_" + tabId;
    	if (alwaysNewTab){
    		sFrameName = "new Date().valueOf()";
    	}else{
    		sFrameName = "'" + sFrameName + "'"; 
    	}
        return 
            "XVW.openCommandTab( " +
            sFrameName+"," +
            "'" + formId + "'," +
            "'" + tabId + "'," +
            "'" + JavaScriptUtils.writeValue( sActionValue ) + "'," +
            (sTabTitle!=null? "'" + JavaScriptUtils.writeValue( sTabTitle ) + "'" : null ) + ","
            + closeableTab +
            ");";
    }
    
    public static final String getCommandDownloadFrame(  XUIComponentBase oComponent, String sActionValue )    {
        String sFrameName = "Frame_" + oComponent.getId();
        return 
            "XVW.CommandDownloadFrame( " +
            "'" + sFrameName + "'," +
            "'" + oComponent.findParentComponent(XUIForm.class).getClientId() + "'," +
            "'" + oComponent.getId() + "'," +
            "'" + JavaScriptUtils.writeValue(sActionValue) + "'" +
            ");";
    }
    
    public static final String getSyncClientViewScript( XUIViewRoot viewRoot ) {
    	Form form = (Form)viewRoot.findComponent( XUIForm.class );
    	if( form != null ) {
    		return "XVW.syncView('" + form.getClientId() + "');";
    	}
    	return "";
    }
    
    public static final String getSyncClientViewScript( String formId ) {
    	return "XVW.syncView('" + formId + "');";
    }
    
    public static final void closeView( XUIViewRoot oViewRoot, XUIScriptContext scriptCtx) {
    	
    	// A vista nao tem janela... ou seja deve estar inserida num tab
    	// o ecran inteiro... neste caso corre o script do lado do cliente
    	// para fechar a vista.
    	scriptCtx.add( XUIScriptContext.POSITION_FOOTER ,
				oViewRoot + "_close",
				getCloseViewScript( oViewRoot )
		);
    	oViewRoot.dispose();
    	oViewRoot.setTransient( true );
    }
    
    public static final void closeView( XUIViewRoot oViewRoot ) {
        XUIRequestContext oRequestContext; 
        oRequestContext = XUIRequestContext.getCurrentContext();
		closeView( oViewRoot, oRequestContext.getScriptContext() );
    }
    
    protected static Renderer getRenderer(UIComponent oComp, FacesContext context) {
    	String rendererType = oComp.getRendererType();
        Renderer result = null;
        if (rendererType != null) {
            result = context.getRenderKit().getRenderer(oComp.getFamily(),
                                                        rendererType);
        }
        return result;
        
    }

    public static final String getCloseViewScript( XUIViewRoot oViewRoot ) {
    	// Check if it's a window
    	StringBuilder sb = new StringBuilder(  );
    	Window oWnd =  (Window)oViewRoot.findComponent( Window.class );
		
    	if( oWnd != null ) {
    		
    		String closeScript = "";
    		Renderer windowRenderer = getRenderer( oWnd, oWnd.getRequestContext().getFacesContext() );
    		if (windowRenderer instanceof ComponentWebResourcesCleanup){
    			closeScript = ((ComponentWebResourcesCleanup) windowRenderer).getCleanupScript( oWnd ); 
    		}
    		sb.append(closeScript);
    		
    		// The is a window, so simply close the window
    		//FIXME: Does not work for JQuery, changed to the destroy of Window
    		//When that is not the case, we must re-write this
    		/*sb.append( "Ext.onReady( function() { " )
    		.append( "if( "+oWnd.getId()+" ){")
    			.append(closeScript)
		    .append(  "}")
	        .append(   "else if(window.parent.").append(oWnd.getId()).append(") { ")
	        	.append(closeScript)
		    .append(  "}});\n");*/
    		
	       
    	} 
    	else {
    		// A vista nao tem janela... ou seja deve estar inserida num tab
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

