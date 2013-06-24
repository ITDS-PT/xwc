package netgest.bo.xwc.framework.spy;

import javax.el.ELContext;
import javax.el.ValueExpression;

import com.earnstone.perf.PerformanceCounter;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XUISpyValueExpression extends ValueExpression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ValueExpression ve;

	public XUISpyValueExpression( ValueExpression ve ) {
		this.ve = ve;
	}
	
	public boolean equals(Object arg0) {
		return ve.equals(arg0);
	}

	public Class<?> getExpectedType() {
		return ve.getExpectedType();
	}

	public String getExpressionString() {
		return ve.getExpressionString();
	}

	public Class<?> getType(ELContext arg0) {
		return ve.getType(arg0);
	}

	public Object getValue(ELContext arg0) {
		long init = System.currentTimeMillis();
		try {
			return ve.getValue(arg0);
		}
		finally {
			XUISpyUtils.logProperty( ve, init );
		}
	}

	public int hashCode() {
		return ve.hashCode();
	}

	public boolean isLiteralText() {
		return ve.isLiteralText();
	}

	public boolean isReadOnly(ELContext arg0) {
		return ve.isReadOnly(arg0);
	}

	public void setValue(ELContext arg0, Object arg1) {
		ve.setValue(arg0, arg1);
	}

	public String toString() {
		return ve.toString();
	}
}
