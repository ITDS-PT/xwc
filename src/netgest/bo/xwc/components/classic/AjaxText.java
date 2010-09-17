package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class AjaxText extends XUIComponentBase {

	private XUIBindProperty<String> text = 
		new XUIBindProperty<String>("text", this, "", String.class );
	
	private XUIBindProperty<String> updateTime = 
		new XUIBindProperty<String>("updateTime", this, "", String.class );
	
	public void setText( String textExpr ) {
		this.text.setExpressionText( textExpr );
	}
	public String getText() {
		return this.text.getEvaluatedValue();
	}
	
	public void setUpdateTime( String updateTimeExpr ) {
		this.updateTime.setExpressionText( updateTimeExpr );
	}
	public String getUpdateTime() {
		return this.updateTime.getEvaluatedValue();
	}
	
	public boolean wasStateChanged() {
		return super.wasStateChanged();
	};
		
	
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
