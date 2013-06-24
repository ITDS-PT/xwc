package netgest.bo.transaction;

import java.util.concurrent.atomic.AtomicLong;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectContainer;
import netgest.bo.runtime.boThread;
import netgest.bo.system.boApplication;
import netgest.bo.system.boPoolOwner;

public class XTransaction extends boObjectContainer implements boPoolOwner {
    
	
//	public static final Scope SCOPE_REQUEST 	= new Scope(1);
//	public static final Scope SCOPE_TRANSACTION = new Scope(2);
	
	private static AtomicLong transactionIdSequence = new AtomicLong( 0 );

	private boThread 	oBoThread;
    
    private String 		sTransactionId = String.valueOf( transactionIdSequence.incrementAndGet() );
    
//    private Map<Long,boObject>	objectsMap			= null; //new ObjectsLRUMap(500);
//    private Map<Long,boObject>	changedObjectsMap	= new Hashtable<Long,boObject>();

//    private XTransactionManager	transactionManager;
    
    protected XTransaction( XTransactionManager m ) {
    	super(null);
//    	this.transactionManager = m;
    }
    
    public String getId() {
        return sTransactionId;
    }
    
    public void activate() {
        EboContext      oEboContext = boApplication.currentContext().getEboContext();
        oEboContext.setPreferredPoolObjectOwner( this.poolUniqueId() );
    }
    
//    public XEOList createList() {
//    	XEOList l = new XEOQLList( this );
//    	lists.add( l );
//    	return l;
//    }
//
//    public XEOList createList( Scope scope, String id ) {
//    	XEOList l = createList();
//    	switch( scope.value ) {
//    		case 1:
//				this.requestListsMap.put( id, l);
//				break;
//			case 2:
//				this.transactionListsMap.put( id, l);
//				break;
//			default: 
//				break;
//    	}
//    	return l;
//    }

    
    public void beginObjectUpdates() {
    }

    public void commitObjectUpdates() {
    }
    
    public void rollbackObjectUpdates() {
    }

    public void beginPersist() {
    }

    public void commit() {
    }
    
    public void rollback() {
    }
    
    
    public void saveObjects( boObject rootObject ) {
    	
    }

    public void saveObjects( boObject rootObject, boolean validate ) {
    	
    }


    public void saveObjects( boolean validate ) {
    	
    }
    
    public void validateObjects() {
    	
    }

    public void validateObjects( boObject rootObject ) {
    	
    }

    public EboContext getEboContext() {
    	return boApplication.currentContext().getEboContext();
    }
    
	@Override
	public void poolObjectActivate() {
	}

	@Override
	public void poolObjectPassivate() {
	}
    
    public boThread getThread(  )
    {
        if( oBoThread == null )
        {
            oBoThread = new boThread();
        }
        return oBoThread;
    }
    
/*    
    private class ObjectsLRUMap extends LRUMap {
    	
    	public ObjectsLRUMap( int lruSize ) {
    		super( lruSize );
    	}
    	
		@Override
		protected void processRemovedLRU(Object arg0, Object arg1) {
			boObject o = (boObject)arg1;
			try {
				if( o.isChanged() || o.poolIsStateFull() ) {
					changedObjectsMap.put( Long.valueOf( o.getBoui() ), o );
				}
			} catch ( boRuntimeException e ) {
				
			}
		}
    }
*/
    
//    private static class Scope {
//    	int value;
//    	private Scope( int value ) {
//    		this.value = value;
//    	}
//    }

}
