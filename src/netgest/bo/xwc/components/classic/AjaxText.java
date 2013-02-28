package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

/**
 * 
 * The {@link AjaxText} component renders text in a viewer
 * that's constantly updated (using Ajax)
 * 
 * @author Filipe Calo
 *
 */
public class AjaxText extends XUIComponentBase {

	/**
	 * The text to render in the viewer (and update)
	 */
	@Required
	private XUIBindProperty<String> text = 
		new XUIBindProperty<String>("text", this, "", String.class );
	
	/**
	 * The time between each update request (in mili-seconds)
	 */
	@Required
	private XUIBindProperty<String> updateTime = 
		new XUIBindProperty<String>("updateTime", this, "", String.class );
	
	/**
	 * 
	 * Sets the text to display
	 * 
	 * @param textExpr A literal text or a {@link ValueExpression}
	 */
	public void setText( String textExpr ) {
		this.text.setExpressionText( textExpr );
	}
	
	/**
	 * 
	 * Retrieves the text to display
	 * 
	 * @return The text to display
	 */
	public String getText() {
		return this.text.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the time between updates (in mili-seconds)
	 * 
	 * @param updateTimeExpr A literal value in mili-seconds or a {@link ValueExpression}
	 */
	public void setUpdateTime( String updateTimeExpr ) {
		this.updateTime.setExpressionText( updateTimeExpr );
	}
	/**
	 * 
	 * Retrieves the update time
	 * 
	 * @return The time between each update (in miliseconds)
	 */
	public String getUpdateTime() {
		return this.updateTime.getEvaluatedValue();
	}
	
		
	
	@Override
	public void initComponent() {
		super.initComponent();
		
		XUICommand cmd = new XUICommand();
		cmd.setId( getId() + "_cmd" );
		getChildren().add( cmd );
		
	}

	@Override
	public void preRender() {
	}
	
	@Override
	public Object saveState() {				
		return super.saveState();
	}
	
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet  {
		
		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp) throws IOException {
			AjaxText comp = (AjaxText)oComp;
			oResponse.setContentType("text/html");
			oResponse.getWriter().write( comp.getText() );			
		}						
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			AjaxText oComp = (AjaxText)component;
			
			if (oComp.getText() == null || oComp.getUpdateTime() == null)
				throw new RuntimeException(ExceptionMessage.PROPERTY_TEXT_AND_UPDATETIME_CANNOT_BE_NULL.toString());
			
			String sanitizedCompId = "_"+oComp.getClientId().replace(":", "");
			
			w.write("<div id='ajaxText"+sanitizedCompId+"'>");
			w.write(oComp.getText().toString());
			w.write("</div>");
			
		
            String url = getRequestContext().getAjaxURL();
            if( url.indexOf("?") == -1 ) {
            	url += "?";
            }
            else {
            	url += "&";
            }
				
			url  += "javax.faces.ViewState=" +
	        		XUIRequestContext.getCurrentContext().getViewRoot().getViewState();
			url  += "&xvw.servlet=" + component.getClientId();
			
			
			ScriptBuilder s = new ScriptBuilder();
			s.l( "function updateTime"+sanitizedCompId+"() {" );
			s.l( "var r=XVW.createXMLHttpRequest();" );
			s.l( "r.onreadystatechange=function(){ if(r.readyState==4) ");
			s.l(		"document.getElementById('ajaxText"+sanitizedCompId+"').innerHTML=r.responseText;}");
			s.l( "r.open('POST','"+url+"','true');" );
			s.l( "r.send();" );
			s.l( "} " );
			s.l( "window.setInterval( 'updateTime"+sanitizedCompId+"();', "+oComp.getUpdateTime().toString()+" )" );
			
			w.getScriptContext()	
			.add( 
				XUIScriptContext.POSITION_FOOTER , 
				component.getClientId()+"_updtime",
				s.toString()
			);
		
			
			
		}
		
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
		}

		@Override
		public void decode(XUIComponentBase component) {
			super.decode(component);
		}

	}
	
}
