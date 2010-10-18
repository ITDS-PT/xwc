package netgest.bo.xwc.components.viewers.beans;

import org.json.JSONObject;

import netgest.bo.builder.boBuilder;
import netgest.bo.builder.boBuilderOptions;
import netgest.bo.builder.boBuilderProgress;
import netgest.bo.http.XEOBuilderFilter;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ViewersMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.ViewerConfig;
import netgest.utils.StringUtils;

public class BuilderDevelopmentBean {

	private boolean builderInProgress = false;
	private Boolean buildSucess = null;
	
	
	private boBuilderProgress 	buildProgress;
	private boBuilderOptions	buildOptions = new boBuilderOptions();
	
	private long   startedMs = 0;
	private String elapsedMs =  ViewersMessages.BUILDER_ELAPSED_TIME.toString( "00:00:00" );
	
	

	public String getElapsedTime() {
		if( builderInProgress ) {
		    // Get elapsed time in milliseconds
		    long elapsedTimeMillis = System.currentTimeMillis()-startedMs;
		    
		    // Get elapsed time in seconds
		    float elapsedTimeSec = elapsedTimeMillis/1000F%60F;
		    
		    // Get elapsed time in minutes
		    float elapsedTimeMin = elapsedTimeMillis/(60*1000F)%60F;
		    
		    // Get elapsed time in hours
		    float elapsedTimeHour = elapsedTimeMillis/(60*60*1000F);
		    elapsedMs = ViewersMessages.BUILDER_ELAPSED_TIME.toString(  
		    			StringUtils.padl( Long.toString( (int)elapsedTimeHour  ), 2, "0") + ":" + 
						StringUtils.padl( Long.toString( (int)elapsedTimeMin ), 2, "0") + ":" + 
						StringUtils.padl( Long.toString( (int)elapsedTimeSec ), 2, "0")
					);
		}
		return elapsedMs;
	}
	
	public String getLogText() {
		if( this.buildProgress != null && !builderInProgress ) {
			return buildProgress.getLog();
		}
		return "";
	}

	
	public float getOverallProgress() {
		if( this.buildProgress != null ) {
			return this.buildProgress.getOverallProgress();
		}
		return 0;
	}

	public String getOverallTaskName() {
		if( this.buildProgress != null ) {
			return this.buildProgress.getOverallTaskName();
		}
		return "";
	}

	public String getCurrentTaskName() {
		if( this.buildProgress != null ) {
			return this.buildProgress.getCurrentTaskName();
		}
		return "";
	}

	public float getCurrentTaskProgress() {
		if( this.buildProgress != null ) {
			return this.buildProgress.getCurrentTaskProgress();
		}
		return 0;
	}

	public boBuilderOptions getBuildOptions() {
		return this.buildOptions;
	}
	
	public boolean getDisableBuildLogBtn()
	{	
		if (buildSucess==null) return true;
		else return buildSucess.booleanValue();
	}
	
	
	public void build() throws Exception {
		buildSucess  = null;
		EboContext ctx = null;
		boSession session = null;
		this.startedMs = System.currentTimeMillis();
			try {
				builderInProgress = true;
				buildOptions.setBuildDatabase(true);
			    buildOptions.setIntegrateWithXEOStudioBuilder(true);
			    buildOptions.setGenerateAndCompileJava(false);
			    buildOptions.setMarkDeployedObjects(true);
			    buildOptions.setBuildWorkplaces(true);
			    buildOptions.setFullBuild(true);
				boApplication bapp = boApplication.getApplicationFromStaticContext("XEO");
				session = bapp.boLogin( "SYSTEM" , boLoginBean.getSystemKey() );
				ctx = session.createRequestContext(  null, null, null);
				XUIRequestContext.getCurrentContext().getTransactionManager().release();
				boBuilder.buildAll( ctx, this.buildOptions, this.buildProgress );
				buildSucess = true;			
				XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();	
				
				requestContext.getScriptContext().add( 
						XUIScriptContext.POSITION_FOOTER, 
						"gotologin",
						"if (window.parent) window.parent.location='LogoutXVW.jsp'; else window.location='LogoutXVW.jsp';"
					);
			}
			catch( Exception e ) {
				buildSucess = false;
				Throwable cause = e;
				int cntr = 0;
				while( cause.getCause() != null ) {
					if( cntr > 5 ) {
						cause = e;
						break;
					}
					cause = cause.getCause();					
				}
				cause.printStackTrace( buildProgress.getLogWriter() );
			}
			finally {
				XEOBuilderFilter.running=false;
				builderInProgress = false;
				if( ctx != null )
					ctx.close();
				if( session != null )
					session.closeSession();
				
			}
	}
	
	public void updateView() {
		if( builderInProgress ) {
			XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
			XUIViewRoot		  viewRoot		 = requestContext.getViewRoot();
    		XUICommand 	updateCommand = (XUICommand)viewRoot.findComponent( "builder:updateBtnHidden" );
			// Create update view script
			requestContext.getScriptContext().add( 
					XUIScriptContext.POSITION_FOOTER, 
					"builder_upd1",
					"window.setTimeout( function() { " +
	                XVWScripts.getAjaxCommandScript( updateCommand ,  XVWScripts.WAIT_STATUS_MESSAGE ) +
					"}, 100 );"
				);
			
		} else {
			if( buildSucess != null ) {
				if( !buildSucess ) {
					XUIRequestContext.getCurrentContext().addMessage("build_error", 
							new XUIMessage( XUIMessage.TYPE_ALERT,XUIMessage.SEVERITY_CRITICAL , 
									"XEO Model Builder", 
									ViewersMessages.BUILDER_FINISH_ERRORS.toString()
							)	
						);
				}			
			}
		}
	}
	
	public void startBuild() throws Exception {
		
		try {
			if (XEOBuilderFilter.developmentMode)
			{
				XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
				
				XUIViewRoot		  viewRoot		 = requestContext.getViewRoot();
				XUICommand 	hiddenCommand = (XUICommand)viewRoot.findComponent( "builder:buildBtnHidden" );
	    		XUICommand 	updateCommand = (XUICommand)viewRoot.findComponent( "builder:updateBtnHidden" );
	
				builderInProgress = true;
				this.buildProgress = new boBuilderProgress();
	
				// Submit build action from client
				requestContext.getScriptContext().add( 
						XUIScriptContext.POSITION_FOOTER, 
						"builder_cmd", 
						XVWScripts.getAjaxCommandScript( hiddenCommand, XVWScripts.WAIT_STATUS_MESSAGE )
					);
				
				
				// Create update view script
				requestContext.getScriptContext().add( 
						XUIScriptContext.POSITION_FOOTER, 
						"builder_upd1",
						"window.setTimeout( function() { " +
		                XVWScripts.getAjaxCommandScript( updateCommand ,  XVWScripts.WAIT_STATUS_MESSAGE ) +
						"}, 100 );"
					);
			}
		} finally {
			//builderInProgress = false;
		}
		
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
		oRequestContext.getViewRoot().setTransient( true );
		oRequestContext.renderResponse();
	}
	
    public void openViewer()  throws Exception {
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();

        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 

        oViewRoot = oSessionContext.createChildView( sViewerName );
        
        oRequestContext.setViewRoot( oViewRoot );
        oViewRoot.processInitComponents();
    	
    }
	
	
}
