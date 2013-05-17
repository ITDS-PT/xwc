package netgest.bo.xwc.framework;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * A ValueExpression evaluator to help processing EL Expressions
 *
 */
public class XUIValueExpressionEvaluator {
	
	/**
	 * 
	 * Evaluates a given expression under the current context
	 * 
	 * @param el The expression to evaluate
	 * 
	 * @return The result
	 */
	public static Object evaluate(ValueExpression el){
		return el.getValue( getElContext() );
	}
	
	protected static ELContext getElContext(){
		return XUIRequestContext.getCurrentContext().getELContext();
	}
	
	/**
	 * 
	 * Creates a value expression
	 * 
	 * @param value The expression string
	 * @param klass The returning type of the expression
	 * 
	 * @return A value expression instance
	 */
	public static ValueExpression createValueExpression(String value, Class<?> klass) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
		ValueExpression expression = oExFactory.createValueExpression( getElContext(), value, klass );
		return expression;
	}
	
	
}
