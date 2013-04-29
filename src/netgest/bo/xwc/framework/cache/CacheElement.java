package netgest.bo.xwc.framework.cache;

import java.util.Date;

/**
 * 
 * An implementation of CacheEntry
 * 
 * @author PedroRio
 *
 */
public class CacheElement implements CacheEntry {

	private String key;
	private Object element; 
	private Date dateLastUpdate;
	private Date dateCreation;
	private Date expirityDate;
	
	public CacheElement(String key, Object element, Date dateLastUpdate,
			Date dateCreation, Date expiresDate) {
		this.key = key;
		this.element = element;
		this.dateLastUpdate = dateLastUpdate;
		this.dateCreation = dateCreation;
		this.expirityDate = expiresDate;
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
	public Date getDateCreation() {
		return dateCreation;
	}

	@Override
	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	@Override
	public void updateResource(Date newDateOfLastUpdate) {
		dateLastUpdate = newDateOfLastUpdate;
	}


	@Override
	public Date getExpiredDate() {
		return expirityDate;
	}


	@Override
	public void setExpiredDate(final Date newDate) {
		this.expirityDate = newDate;
	}
	
	
	
}
