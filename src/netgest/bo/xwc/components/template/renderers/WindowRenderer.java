package netgest.bo.xwc.components.template.renderers;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.renderers.ComponentWebResourcesCleanup;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class WindowRenderer extends TemplateRenderer implements ComponentWebResourcesCleanup {

	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
    public void encodeChildren(XUIComponentBase component) throws IOException {
    }

	@Override
	public void templateEncodeChildren( XUIComponentBase component ) throws IOException  {
		
        Window wnd = (Window) component;
		Iterator<UIComponent> oChildIterator;
        UIComponent           oChildComp;
        
        oChildIterator = wnd.getChildren().iterator();
        while( oChildIterator.hasNext() ) {
            oChildComp = oChildIterator.next();
            if (!oChildComp.isRendered()) {
                return;
            }
            oChildComp.encodeAll( getRequestContext().getFacesContext() );
            
        }
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
