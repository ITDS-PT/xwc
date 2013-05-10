package netgest.bo.xwc.components.assync;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;

public class AsyncRenderer extends XUIRenderer {
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	protected boolean wasRenderParameterSentInRequest(){
		XUIRequestContext ctx = getRequestContext();
		return ctx.getRequestParameterMap().containsKey( XUIViewHandler.RENDER_COMPONENT_PARAMETER );
	}
	
	@Override
	public void encodeChildren(XUIComponentBase component) throws IOException {
		
		if (component.isRenderedOnClient() && wasRenderParameterSentInRequest()){
			super.encodeChildren( component );
		} else {
			//Do nothing on purpose
		}
	}

	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		AsyncRegion comp = (AsyncRegion) component;
		w.startElement( HTMLTag.DIV );
		w.writeAttribute( HTMLAttr.ID , component.getClientId() );
		
		if (renderLoading(comp)){
			if (comp.usesCustomLoadingHtml()){
				w.write( comp.getLoadingHtml() );
			} else {
				w.startElement( HTMLTag.IMG );
					w.writeAttribute( HTMLAttr.SRC , comp.getPathIcon() );
					w.writeAttribute( HTMLAttr.STYLE , "display:inline" );
				w.endElement( HTMLTag.IMG );
				w.write( comp.getWaitMessage() );
			}
		}
	}
	
	
	protected boolean renderLoading(AsyncRegion comp) {
		return !comp.isRenderedOnClient() && !wasRenderParameterSentInRequest();
	}

	@Override
	public void encodeEnd(XUIComponentBase component)
			throws IOException {
		
		AsyncRegion comp = (AsyncRegion) component;
		XUIResponseWriter w = getResponseWriter();
		w.endElement( HTMLTag.DIV );
		XUIRequestContext ctx = getRequestContext();
		
		if (!component.isRenderedOnClient() && !wasRenderParameterSentInRequest()){
			XUIForm form = (XUIForm) component.findParent( XUIForm.class );
			Integer delay = comp.getDelay();
			
			String renderComponent = String.format("XVW.AjaxRenderComp('%s', '%s', false )", form.getClientId(), component.getClientId());
			
			if (delay.intValue() > 0 )
				ctx.addFooterScript( component.getClientId() , "window.setTimeout(function (){"+renderComponent+";},"+delay.intValue()+")");
			else
				ctx.addFooterScript( component.getClientId() , renderComponent);
			
			
			Integer refreshInterval = comp.getRefreshInterval();
			if (refreshInterval.intValue() > 0){
				String refresh = String.format( "window.setInterval(function(){%s;},%d)",renderComponent, refreshInterval * 1000 );
				ctx.addFooterScript( component.getClientId() +"_refresh" , refresh);
			}
				
		}
		
	}
	

	
	
}
