package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class TemplateInclude extends XUIComponentBase implements NamingContainer {
	
	
	private transient boolean				wasStateChanged;	  
	private transient XUIViewRoot 			viewRoot;
	
	private XUIBindProperty<XUIViewRoot> 	view = new XUIBindProperty<XUIViewRoot>("view", this, XUIViewRoot.class );
	
	public void setView( String view ) {
		this.view.setExpressionText(view);
	}
	
	public XUIViewRoot getViewRoot(){
		return viewRoot;
	}

	@Override
	public void initComponent() {

		if( this.viewRoot == null )
			this.viewRoot = view.getEvaluatedValue();
		
		if (this.viewRoot == null){
			throw new IllegalStateException(String.format("TemplateInclude %s cannot have a null view",this.getClientId()));
		}
		
		this.getChildren().clear();
		
		this.getChildren().add( viewRoot );
		
		UIComponent r = this;
        while( r.getParent() != null )
               r = r.getParent();
        
        for( String s : viewRoot.getBeanIds() ) {
        		Object bean = viewRoot.getBean( s );
        		if (bean instanceof XEOBaseBean){
        			XEOBaseBean castBean = (XEOBaseBean) bean;
        			castBean.setViewRoot( ((XUIViewRoot)r).getViewState() );
        		}
        }


	}
	
	public void replaceView( XUIViewRoot viewRoot ) {

		if( this.viewRoot != null ) {
			// Dispose da view
			this.viewRoot.dispose();
			this.viewRoot = null;
		}
		
		
		this.viewRoot = viewRoot;
		initComponent();
		
		forceRenderOnClient();
		wasStateChanged = true;
	}
	
	@Override
	public void restoreState(Object oState) {
		super.restoreState(oState);
		
		if( this.getChildCount() == 1 ) {
			this.viewRoot = (XUIViewRoot)getChild(0);
		}
		
	}
	
	
	@Override
	public StateChanged wasStateChanged2() {
		return wasStateChanged?StateChanged.FOR_RENDER:StateChanged.NONE;
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {
	
		@Override
		public void encodeBegin( XUIComponentBase component ) throws IOException {
			super.encodeBegin(component);
			XUIResponseWriter w = XUIRequestContext.getCurrentContext().getResponseWriter();
			w.startElement( HTMLTag.DIV , component );
			w.writeAttribute( HTMLAttr.ID , component.getClientId(), "id" );
		}
	
		@Override
		public void encodeEnd( XUIComponentBase component ) throws IOException {
			XUIResponseWriter w = XUIRequestContext.getCurrentContext().getResponseWriter();
			w.endElement( HTMLTag.DIV );
			super.encodeEnd(component);
		}
	
	}
	
	

}
