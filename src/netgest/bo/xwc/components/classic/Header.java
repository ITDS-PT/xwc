package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.html.GenericTag;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import netgest.utils.StringUtils;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;

public class Header extends XUIComponentBase {
	
	private XUIBindProperty< String > content = new XUIBindProperty< String >( "content" , this , String.class );
	
	public String getContent() {
		return content.getEvaluatedValue();
	}
	
	public void setContent(String  contentExpr) {
		this.content.setExpressionText( contentExpr );
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
	}
	
	
	public static class XEOHTMLRenderer extends XUIRenderer{
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
		}
		
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			Header header = (Header) component;
			String content = header.getContent();
			XUIResponseWriter headerWriter = getResponseWriter().getHeaderWriter();
			if (StringUtils.hasValue( content ))
				headerWriter.write( content );
			for (UIComponent child: header.getChildren()){
				if (child instanceof GenericTag){
					GenericTag tag = ( GenericTag ) child;
					tag.setRenderedOnClient( true ); //Since we're not encoding the component
					//through the standard means we must set the renderedOnClinet value
					headerWriter.write( tag.serialize() );
				}
			}
			
		}
		
		@Override
		public void encodeChildren(XUIComponentBase component)
				throws IOException {
			//Do nothing
		}
		
		@Override
		public boolean getRendersChildren() {
			return true;
		}
		
		@Override
		public StateChanged wasStateChanged(XUIComponentBase component,
				List< XUIBaseProperty< ? >> updateProperties) {
			return super.wasStateChanged( component , updateProperties );
		}
		
		
	}

	
}
