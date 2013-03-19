package netgest.bo.xwc.framework;

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

public class XUIELContextWrapper extends ELContext {
	
	private ELContext 	elContext;
	
	private UIComponent	contextComponent;
	
	private boolean wasEvaluated = true;
	
	public XUIELContextWrapper( ELContext elContext, UIComponent component ) {
		this.elContext = elContext;
		this.contextComponent = component;
	}
	
	
	public UIComponent getContextComponent() {
		return this.contextComponent;
	}
	
	/**
	 * @param arg0
	 * @return
	 * @see javax.el.ELContext#getContext(java.lang.Class)
	 */
	public Object getContext(Class arg0) {
		return elContext.getContext(arg0);
	}

	/**
	 * @return
	 * @see javax.el.ELContext#getELResolver()
	 */
	public ELResolver getELResolver() {
		return elContext.getELResolver();
	}

	/**
	 * @return
	 * @see javax.el.ELContext#getFunctionMapper()
	 */
	public FunctionMapper getFunctionMapper() {
		return elContext.getFunctionMapper();
	}

	/**
	 * @return
	 * @see javax.el.ELContext#getLocale()
	 */
	public Locale getLocale() {
		return elContext.getLocale();
	}

	/**
	 * @return
	 * @see javax.el.ELContext#getVariableMapper()
	 */
	public VariableMapper getVariableMapper() {
		return elContext.getVariableMapper();
	}

	/**
	 * @return
	 * @see javax.el.ELContext#isPropertyResolved()
	 */
	public boolean isPropertyResolved() {
		return elContext.isPropertyResolved();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @see javax.el.ELContext#putContext(java.lang.Class, java.lang.Object)
	 */
	public void putContext(Class arg0, Object arg1) {
		elContext.putContext(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @see javax.el.ELContext#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0) {
		elContext.setLocale(arg0);
	}

	/**
	 * @param arg0
	 * @see javax.el.ELContext#setPropertyResolved(boolean)
	 */
	public void setPropertyResolved(boolean arg0) {
		elContext.setPropertyResolved(arg0);
	}
	
	public void setCouldNotEvaluate(){
		wasEvaluated = false;
	}
	
	/**
	 * 
	 * Retrieves whether the property value was evaluated or not
	 * Had to add this because the "propertyResolved" method is always true
	 * the XUIELResolver sets it to false but somewhere after the JSF runtime
	 * sets the value to true
	 * 
	 * @return If the property was evaluated
	 */
	public boolean wasPropertyEvaluated(){
		return wasEvaluated;
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return elContext.toString();
	}

}
