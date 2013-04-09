package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIOutput;

public class ProgressBar extends XUIOutput {

	private XUIStateBindProperty<Float>    valueExpression	= new XUIStateBindProperty<Float>( "valueExpression", this, "0", Float.class );
	private XUIViewStateBindProperty<String>   width 		= new XUIViewStateBindProperty<String>( "width", this, "auto", String.class );
	private XUIViewStateBindProperty<String>   text 		= new XUIViewStateBindProperty<String>( "text", this, "", String.class );

	public String getWidth() {
		return width.getEvaluatedValue();
	}

	public void setWidth(String widthExpr ) {
		this.width.setExpressionText( widthExpr  );
	}
	
	public void setText( String exprText ) {
		this.text.setExpressionText( exprText );
	}
	
	public String getText() {
		return text.getEvaluatedValue();
	}

	public void setValueExpression( String sValueExpression ) {
		ValueExpression ve = createValueExpression( sValueExpression, Float.class );
		valueExpression.setValue( ve );
		super.setValueExpression( "value" , ve );
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeEnd( XUIComponentBase component ) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			w.startElement( HTMLTag.DIV , component );
			w.writeAttribute( HTMLAttr.ID, component.getClientId(), null );
			w.endElement( HTMLTag.DIV );
			ExtConfig compConfig = renderExtJs( component );
			w.getScriptContext().add( 
					XUIScriptContext.POSITION_FOOTER, 
					component.getClientId(), 
					compConfig.renderExtConfig()
			);
		}
		
        public ExtConfig renderExtJs( XUIComponentBase oComp ) {
            ProgressBar			progressBar;
            String              sJsValue;

            progressBar = (ProgressBar)oComp;
            
            sJsValue = String.valueOf( progressBar.getValue() ); 
            
            ExtConfig oExtConfig = new ExtConfig("Ext.ProgressBar");
            oExtConfig.addJSString("renderTo", oComp.getClientId());
            oExtConfig.addJSString("id", oComp.getClientId());
            oExtConfig.addJSString("width", progressBar.getWidth() );
            oExtConfig.addJSString("text", progressBar.getText() );
            
            if( progressBar.getValue() != null ) {
            	oExtConfig.add("value", sJsValue );
            }
            return oExtConfig;
        }

		
		
		
	}

}
