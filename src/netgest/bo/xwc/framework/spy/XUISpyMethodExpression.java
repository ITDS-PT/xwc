package netgest.bo.xwc.framework.spy;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodInfo;

public class XUISpyMethodExpression extends MethodExpression {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MethodExpression methodExpression;
	
	public XUISpyMethodExpression( MethodExpression m ) {
		methodExpression = m;
	}
	
	public boolean equals(Object arg0) {
		return methodExpression.equals(arg0);
	}

	public String getExpressionString() {
		return methodExpression.getExpressionString();
	}

	public MethodInfo getMethodInfo(ELContext arg0) {
		return methodExpression.getMethodInfo(arg0);
	}

	public int hashCode() {
		return methodExpression.hashCode();
	}

	public Object invoke(ELContext arg0, Object[] arg1) {
		long init = System.currentTimeMillis();
		try {
			return methodExpression.invoke(arg0, arg1);
		}
		finally {
			XUISpyUtils.logMethod( methodExpression, init );
		}
	}

	public boolean isLiteralText() {
		return methodExpression.isLiteralText();
	}

	public String toString() {
		return methodExpression.toString();
	}
	
	
	
}
