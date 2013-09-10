package netgest.bo.xwc.framework.cache;

import java.util.Date;

import netgest.bo.xwc.framework.cache.time.TimeProvider;

/**
 * 
 * An implementation of CacheEntry
 * 
 * @author PedroRio
 *
 */
public class CacheElement implements CacheEntry {
	
	public static final String NULL_KEY = "";
	public static final String NULL_CONTENT = "";
	
	/**
	 * Represents a Cache Miss item (avoids Null checks)
	 */
	public static final CacheEntry NULL_ENTRY = new CacheEntry() {
		
		@Override
		public boolean isExpired() { return true; }
		
		@Override
		public String getKey() { return NULL_KEY; }
		
		@Override
		public Date getExpiredDate() { return new Date(0); }
		
		@Override
		public Date getDateAdded() { return new Date(0); }
		
		@Override
		public Object getContent() { return NULL_CONTENT ; }

		@Override
		public int getLength() {
			return 0;
		}

		@Override
		public void invalidate() {
		}
		
	}; 
			

	private final String key;
	private final Object element; 
	private Date expiredDate;
	private Date dateAdded;
	private boolean isExpired;
	private TimeProvider timeProvider;
	
	
	public CacheElement(final String key, final Object element, final Date expiresDate, TimeProvider time) {
		this.key = key;
		this.element = element;
		this.dateAdded = time.getCurrentTime();
		this.expiredDate = expiresDate;
		if (expiresDate == null)
			isExpired = false;
		this.timeProvider = time;
	}


	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object getContent() {
		return element;
	}


	@Override
	public Date getExpiredDate() {
		return expiredDate;
	}


	@Override
	public boolean isExpired() {
		if (expiredDate == null)
			return isExpired;
		else{ 
			if (timeProvider.getCurrentTime().after( expiredDate )){
				return true;
			} else
				return false;
		}
	}

	@Override
	public Date getDateAdded() {
		return dateAdded;
	}


	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
