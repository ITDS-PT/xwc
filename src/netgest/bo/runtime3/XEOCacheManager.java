package netgest.bo.runtime3;

import netgest.bo.data.DataSet;

//import org.jboss.cache.Cache;
//import org.jboss.cache.CacheFactory;
//import org.jboss.cache.DefaultCacheFactory;
//import org.jboss.cache.Fqn;
//import org.jboss.cache.Node;

public class XEOCacheManager implements XEOCache {
//	
//	private Cache<Byte, DataSet> cache;
//	
//	public XEOCacheManager() {
//		initialize();
//	}
	
	public void initialize() {
//		CacheFactory< Byte, DataSet> factory = new DefaultCacheFactory<Byte, DataSet>();
//		cache = factory.createCache("jbosscacheconfig.xml", false);
//		cache.start();
	}
	
	public void putObject( String className, long boui, DataSet data ) {
	
//		this.cache.putForExternalRead( getObjectFqn(className, boui), OBJECT_DATA_KEY , data );

	}
	
	public void putList( String className, int listHashCode, DataSet data ) {

//		this.cache.putForExternalRead( getListFqn( className, listHashCode ), OBJECT_LIST_KEY , data );
		
	}

	public DataSet getList( String className, int listHashCode ) {
//		Node<Byte, DataSet> node = this.cache.getNode( getListFqn( className, listHashCode ) );
//		if( node != null ) {
//			return node.get( OBJECT_LIST_KEY );
//		}
		return null;
	}
	
	public DataSet getObject( String className, long boui ) {
//		Node<Byte, DataSet> node = this.cache.getNode( getObjectFqn( className, boui ) );
//		if( node != null ) {
//			return node.get( OBJECT_DATA_KEY );
//		}
		return null;
	}
	
//	private static final Fqn<String> getObjectFqn( String className, Long boui ) {
//		StringBuilder sb = new StringBuilder( BASE_OBJECT_DATA );
//		sb.append( className ).append( '/' ).append( boui.toString() );
//		return Fqn.fromString( sb.toString() );
//	}
//	
//	private static final Fqn<String> getListFqn( String className, int listHashCode ) {
//		StringBuilder sb = new StringBuilder( BASE_LIST_DATA );
//		sb.append( className ).append( '/' ).append( listHashCode );
//		return Fqn.fromString( sb.toString() );
//	}

}
