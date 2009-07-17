package netgest.bo.transaction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boThread;
import netgest.bo.system.boApplication;
import netgest.bo.system.boMemoryArchive;
import netgest.bo.system.boPoolManager;
import netgest.bo.system.boPoolOwner;
import netgest.bo.system.boPoolable;

public class XTransactionManager extends boPoolable implements boPoolOwner {

	public Map<String,ArrayList<String>> requestTransids = new Hashtable<String,ArrayList<String>>();

	private boApplication bapp = null;
	
	public boThread getThread() {
        return null;
    }

    @Override
    public void poolObjectActivate() {
        
    }

    @Override
    public void poolObjectPassivate() {
        
    }
    
    public XTransaction getTransaction( String id ) {

        EboContext      oEboContext = boApplication.currentContext().getEboContext();
        boMemoryArchive boMemArchive = oEboContext.getApplication().getMemoryArchive();
        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
        oEboContext.setPreferredPoolObjectOwner( this.poolUniqueId() );
        
        XTransaction t = (XTransaction)boPoolMgr.getObject( oEboContext, "XTRANSACTION:ID:" + id );
        if( t != null )
        	addTransactionIdToContext( oEboContext , t);
        return t;
    }
    
    private void addTransactionIdToContext( EboContext oContext, XTransaction t ) {
        ArrayList<String> l = requestTransids.get( oContext.poolUniqueId() );
        if( l != null ) {
        	if ( !l.contains( t.poolUniqueId() ) ) {
        		l.add( t.poolUniqueId() );
        	}
        }
        else {
        	l = new ArrayList<String>();
        	l.add( t.poolUniqueId() );
        	requestTransids.put( oContext.poolUniqueId(), l );
        }
    }

    public XTransaction createTransaction() {
    
        EboContext      oEboContext = boApplication.currentContext().getEboContext();
        if( oEboContext != null ) {
        	
        	if( this.bapp == null ) {
        		this.bapp = oEboContext.getApplication();
        	}
        	
	        boMemoryArchive boMemArchive = this.bapp.getMemoryArchive();
	        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
	
	        XTransaction    oXTransaction = new XTransaction( this );
	
	        oEboContext.setPreferredPoolObjectOwner( this.poolUniqueId() );
	        oXTransaction.setEboContext( oEboContext );
	        boPoolMgr.putObject( oXTransaction, new Object[] { "XTRANSACTION:ID:" + oXTransaction.getId() });
	        oXTransaction.poolSetStateFull( this.poolUniqueId() );
	        addTransactionIdToContext( oEboContext , oXTransaction );
	        return oXTransaction;
        }
        return null;
    }
    
    public void releaseTransaction( String transactionId ) {
        EboContext      oEboContext = boApplication.currentContext().getEboContext();
        if( oEboContext != null ) {
	        boApplication   boApp        = oEboContext.getApplication();
	        boMemoryArchive boMemArchive = boApp.getMemoryArchive();
	        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
	
	        String sLastPoolOwner = oEboContext.getPreferredPoolObjectOwner();
	        try {
	        	XTransaction t = this.getTransaction( transactionId );
				if( t != null ) {
					oEboContext.setPreferredPoolObjectOwner( t.poolUniqueId() );
					boPoolMgr.realeaseAllObjects( t.poolUniqueId() );
					boPoolMgr.destroyObject( t );
				}
				
				oEboContext.setPreferredPoolObjectOwner( this.poolUniqueId() );
				boPoolMgr.realeaseObjects(this.poolUniqueId(), oEboContext);

	        } finally {
	        	oEboContext.setPreferredPoolObjectOwner( sLastPoolOwner );
			}
        }
    }
    
    public void releaseAll() {
        if( this.bapp != null ) {
	        boMemoryArchive boMemArchive = this.bapp.getMemoryArchive();
	        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
	    	boPoolMgr.realeaseAllObjects( this.poolUniqueId() );
        }
    }
    
    public void release() {

        EboContext      oEboContext = boApplication.currentContext().getEboContext();
        if( oEboContext != null ) {
	        boApplication   boApp        = oEboContext.getApplication();
	        boMemoryArchive boMemArchive = boApp.getMemoryArchive();
	        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
	
	        String sLastPoolOwner = oEboContext.getPreferredPoolObjectOwner();
	        try {
	        	ArrayList<String> l = requestTransids.get( oEboContext.poolUniqueId() );
	        	if( l != null  ) {
	        		for (String sTransId : l) {
	    				oEboContext.setPreferredPoolObjectOwner( sTransId );
	    				boPoolMgr.realeaseObjects(this.poolUniqueId(), oEboContext);
	        		}
	        	}
			} finally {
	        	oEboContext.setPreferredPoolObjectOwner( sLastPoolOwner );
			}
	        boPoolMgr.realeaseObjects(this.poolUniqueId(), oEboContext);
        }
    }
}
