package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.html.GenericTag;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

public class Footer extends XUIComponentBase {
	
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
			Footer footer = (Footer) component;
			String content = footer.getContent();
			XUIResponseWriter footerWriter = getResponseWriter().getFooterWriter();
			if (StringUtils.hasValue( content ))
				footerWriter.write( content );
			for (UIComponent child: footer.getChildren()){
				if (child instanceof GenericTag){
					footerWriter.write( ((GenericTag)child).serialize() );
				}
			}
			
		}
		
		@Override
		public void encodeChildren(XUIComponentBase component)
				throws IOException {
			
		}
		
		
		
	}

	
}
