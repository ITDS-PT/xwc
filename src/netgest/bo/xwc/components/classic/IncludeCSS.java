package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class IncludeCSS extends XUIComponentBase {

	/**
	 * Path to the CSS file
	 */
	private XUIBindProperty<String> source = new XUIBindProperty<String>("source", this, String.class, "");
	
	public void setSource(String sourceExpr){
		this.source.setExpressionText( sourceExpr );
	}
	
	/**
	 * 
	 * Retrieve the path to the CSS file to include
	 * 
	 * @return
	 */
	public String getSource(){
		return source.getEvaluatedValue();
	}
	
	@Override
	public void initComponent(){
		getRequestContext().getStyleContext().addInclude(
				XUIScriptContext.POSITION_HEADER, getId(), getSource());
	}
	
	
}
