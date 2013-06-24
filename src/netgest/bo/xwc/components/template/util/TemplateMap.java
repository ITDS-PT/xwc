package netgest.bo.xwc.components.template.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class TemplateMap<K, V> implements Map< K , V > {
	
	private Map<K,V> map = new HashMap< K , V >();
	private ELContext elCtx;
	
	public TemplateMap(ELContext ctx){
		this.elCtx = ctx;
	}
	
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey( key );
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue( value );
	}

	@Override
	public V get(Object key) {
		V value = map.get( key );
		if (value != null && value instanceof ValueExpression){
			Object newValue = ((ValueExpression) value).getValue( elCtx );
			return (V)newValue;
		}
		return value;
	}
	
	public String getExpressionText(Object key){
		V value = map.get( key );
		if (value != null && value instanceof ValueExpression){
			return ((ValueExpression) value).getExpressionString();
		}
		if (value instanceof Boolean)
			System.out.println("Boolean");
		return ((Object) value).toString();
	}

	@Override
	public V put(K key, V value) {
		return map.put( key , value );
	}

	@Override
	public V remove(Object key) {
		return map.remove( key );
	}

	@Override
	public void putAll(Map< ? extends K , ? extends V > m) {
		map.putAll( m );
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set< K > keySet() {
		return map.keySet();
	}

	@Override
	public Collection< V > values() {
		return map.values();
	}
	
	

	/* (non-Javadoc)
	 * 
	 * 
	 * This was added because the Freemarker Template makes a copy of the current (as such it does not use
	 * the get method which would re-evaluate the expressions
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set< java.util.Map.Entry< K , V >> entrySet() {
		Set<java.util.Map.Entry< K , V >> entries = map.entrySet();
		for (Iterator<Entry< K , V >> it = entries.iterator() ; it.hasNext();){
			Entry< K , V > current = it.next();
			V value = current.getValue();
			if (value instanceof ValueExpression){
				ValueExpression expr = (ValueExpression) value;
				current.setValue( (V) expr.getValue( elCtx ) );
			} else {
				String expr = (String) value;
				current.setValue( ( (V) createValueExpression(expr).getValue( elCtx ) ) );
			}
		}
		return entries;
	}
	
	protected ValueExpression createValueExpression(String value) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
		ValueExpression expression = oExFactory.createValueExpression( elCtx , value, Object.class );
		return expression;
	}
	
	
}
