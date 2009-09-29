package netgest.bo.xwc.components.model;

import javax.el.MethodExpression;

import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.AttributeDate;
import netgest.bo.xwc.components.classic.AttributeText;
import netgest.bo.xwc.framework.components.XUICommand;

public class RequestActionValue extends XUICommand {

	public void setServerAction(String actionExpression) {
		super.setActionExpression( createMethodBinding( actionExpression ) );
	}

	public String getServerAction() {
		MethodExpression oMethodExpr = super.getActionExpression();
		if( oMethodExpr != null ) {
			return oMethodExpr.getExpressionString();
		}
		return null;
	}

}
