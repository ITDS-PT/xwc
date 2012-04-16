package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class IncludeScript extends XUIComponentBase {

	/**
	 * The path to the javascript file
	 */
	private XUIBindProperty<String> source = new XUIBindProperty<String>("source", this, String.class);
	
	public void setSource(String sourceExpr){
		this.source.setExpressionText( sourceExpr );
	}
	
	/**
	 * 
	 * Retrieve the path to the Javascript
	 * 
	 * @return
	 */
	public String getSource(){
		return source.getEvaluatedValue();
	}
	
	@Override
	public void preRender(){
		getRequestContext().getScriptContext().addInclude(
				XUIScriptContext.POSITION_HEADER, getId(), getSource());
	}
	
	
}
