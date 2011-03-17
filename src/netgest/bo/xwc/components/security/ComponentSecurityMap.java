package netgest.bo.xwc.components.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

/**
 * Map implementation that returns the security permission based on the componentId
 */
public class ComponentSecurityMap implements Map<String, Byte> {

	private Map<String, Byte> permissionsPerComponent = new HashMap<String, Byte>();
	private Map<String, String> idsByComponent = new HashMap<String, String>();

	public ComponentSecurityMap( Map<String, Byte> permissionsPerComponent, Map<String, String> idsByComponent ) {
		if ( permissionsPerComponent!=null ) {
			this.permissionsPerComponent = permissionsPerComponent;
		} 
		if ( idsByComponent!=null ) {
			this.idsByComponent = idsByComponent;
		}
	}
	
	public Byte get( Object componentInstanceId ) {
		String componentId = idsByComponent.get(componentInstanceId);
		if ( componentId!=null && this.permissionsPerComponent.containsKey(componentId) ) {
			return this.permissionsPerComponent.get(componentId);
		}
		return SecurityPermissions.FULL_CONTROL;
	}

	public void clear() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString() );
	}

	public boolean containsKey(Object key) {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}

	public boolean containsValue(Object value) {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString() );
	}

	public Set<java.util.Map.Entry<String, Byte>> entrySet() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}

	public boolean isEmpty() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}

	public Set<String> keySet() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString() );
	}

	public Byte put(String key, Byte value) {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}

	public void putAll(Map<? extends String, ? extends Byte> t) {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}
	
	public Byte remove(Object key) {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}

	public int size() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString() );
	}

	public Collection<Byte> values() {
		throw new RuntimeException( ExceptionMessage.NOT_IMPLEMENTED.toString()  );
	}
	
}
