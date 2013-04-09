package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.renderers.ComponentWebResourcesCleanup;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class WindowRenderer extends JQueryBaseRenderer implements ComponentWebResourcesCleanup {
	
	@Override
    public void encodeChildren(XUIComponentBase component) throws IOException {
		Window wnd = (Window) component;
		Iterator<UIComponent> oChildIterator;
        UIComponent           oChildComp;
        FacesContext          oFacesContext;
        
        oFacesContext = XUIRequestContext.getCurrentContext().getFacesContext();
        
        oChildIterator = wnd.getChildren().iterator();
        while( oChildIterator.hasNext() ) {
            oChildComp = oChildIterator.next();
            if (!oChildComp.isRendered()) {
                return;
            }
            
            oChildComp.encodeAll( oFacesContext );
            
        }
	}
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		Window wnd = (Window) component;
		XUIResponseWriter w = getResponseWriter();
		if (!component.isRenderedOnClient()){
			w.startElement( DIV );
			w.writeAttribute( ID, wnd.getClientId() );
			//xwc-window class is added via JS 
		}
		
		if (shouldUpdate( component ) || true ){ //FIXME o true é só para testes
			this.getRequestContext().getViewRoot().getViewId();
			XUIViewRoot root = getRequestContext().getViewRoot();
			String viewId = root.getViewId();
			String instanceId = root.getInstanceId();
			String viewInstanceId = viewId + ":" + instanceId;
			updateComponent( wnd , getIdForJquerySelector( viewInstanceId ) );
		}
	}
	
	private String getCloseScript(String formId, String viewId){
		//TODO: É mesmo assim que se faz close da view, certo?
		StringBuilder b = new StringBuilder();
		b.append("function(event, ui) {");
		b.append(" $(this).dialog('destroy').remove(); $('#"+viewId+"').remove()");
		b.append("}");
		
		
		
		return b.toString();
	}
	
	private String getFormId(Window component){
		XUIComponentBase result = (XUIComponentBase) component.findComponent( XUIForm.class );
		if (result != null)
			return result.getClientId();
		result = (XUIComponentBase) component.findParentComponent( XUIForm.class );
		if (result != null)
			return result.getClientId();
		
		throw new RuntimeException( "No form Found in viewer with component " + component.getClientId() );
	}
	
	private void updateComponent( Window window, String viewId ) {
		
		String formId = getFormId( window );
		
		JQueryWidget widget = WidgetFactory.createWidget( JQuery.WINDOW );
		
		widget.componentSelectorById( formId );
		widget.createAndStartOptions();
			widget.addOption( "modal", window.getModal() );
			widget.addOption( "width", window.getWidth() );
			widget.addOption( "height", window.getHeight() );
			widget.addOption( "title", window.getTitle() );
			widget.addNonLiteral( "beforeClose", getCloseScript( formId, viewId ) );
			widget.addNonLiteral( "resize", getResizeScript( viewId ) );
			widget.addNonLiteral( "open", getResizeScript( viewId ) );
			
		widget.endOptions();
		widget.command( "parent( )").addClass( "xwc-window" );
		addScriptFooter( window.getClientId(), widget.build() );
	}
	
	private String getResizeScript(String viewId) {
		StringBuilder onResizeJS = new StringBuilder();
        onResizeJS.append( "function(event, ui){" );
        onResizeJS.append( "ExtXeo.layoutMan.doLayout('");
        	onResizeJS.append( viewId );
        	onResizeJS.append( "');" );
        onResizeJS.append( "}" );
        return onResizeJS.toString();
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException{
		XUIResponseWriter w = getResponseWriter();
		if (!component.isRenderedOnClient()){
			w.endElement( DIV );
		}
	}
	
	@Override
	public boolean getRendersChildren(){
		return true;
	}

	@Override
	public String getCleanupScript( XUIComponentBase component ) {
		XUIRequestContext oRequestContext = component.getRequestContext();
		XUIViewRoot root = oRequestContext.getViewRoot();
        String viewIdTmp = root.getViewId();
        String instanceId = root.getInstanceId();
        String viewInstanceId = viewIdTmp + ":" + instanceId;
        
        String clientId = JQueryBuilder.convertIdJquerySelector( component.getClientId() );
        String viewId =  JQueryBuilder.convertIdJquerySelector( viewInstanceId );
        
        StringBuilder b = new StringBuilder(100);
        b.append("window.setTimeout( function() { ");
        	b.append("XVW.closeWindowJquery('");
        		b.append(clientId);
        	b.append("','");
        		b.append(viewId);
        	b.append("');");
        b.append("},10);\n");
        
        oRequestContext.getScriptContext().add( 
        	  XUIScriptContext.POSITION_FOOTER, component.getClientId() + "_closeWnd", 
  		      "window.setTimeout( function() { " +
  		      "XVW.closeWindowJquery('" + clientId +  "','" + viewId +"');" +
  		      "},10);\n"
          );
        
        return b.toString();
	}
	
}
