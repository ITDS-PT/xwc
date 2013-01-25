package netgest.bo.xwc.components.template.resolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import netgest.bo.xwc.components.template.wrappers.LocalizationWrapper;
import netgest.bo.xwc.components.template.wrappers.XVWScriptsWrapper;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class TemplateContextVariables implements Map<String,Object> {

	private Map<String,Object> map = new HashMap< String , Object >();
	
	public TemplateContextVariables(XUIViewRoot requestView){
		String[] beanIds = requestView.getAllBeanIds( );
		Map<String,Object> beans = new HashMap< String , Object >( );
		for (String beanId : beanIds){
			beans.put(beanId,requestView.getBean( beanId ));
		}
		map.put( "beans", beans);
		map.put( "bundles", new LocalizationWrapper( requestView.getLocalizationClasses( ) ) );
		map.put( "js", new XVWScriptsWrapper( ));
	}
	
	@Override
	public int size() {
		return map.size( );
	}
	
	

	@Override
	public boolean isEmpty() {
		return map.isEmpty( );
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
	public Object get(Object key) {
		Object value = map.get( key );
		return value;
	}

	@Override
	public Object put(String key, Object value) {
		if (map.containsKey( key ))
			throw new RuntimeException( String.format("Duplicate variable %s in context", key)	 );
		return map.put( key , value );
	}

	@Override
	public Object remove(Object key) {
		return map.remove( key );
	}

	@Override
	public void putAll(Map< ? extends String , ? extends Object > m) {
		map.putAll( m );
	}

	@Override
	public void clear() {
		map.clear( );
	}

	@Override
	public Set< String > keySet() {
		return map.keySet( );
	}

	@Override
	public Collection< Object > values() {
		return map.values( );
	}

	@Override
	public Set< java.util.Map.Entry< String , Object >> entrySet() {
		return map.entrySet( );
	}



}
