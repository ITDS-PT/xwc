package netgest.bo.xwc.components.cache;

import java.util.Date;

import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIScriptContext.Fragment;
import netgest.bo.xwc.framework.cache.CacheElement;
import netgest.bo.xwc.framework.cache.CacheEngine;
import netgest.bo.xwc.framework.cache.CacheEntry;
import netgest.bo.xwc.framework.cache.provider.CacheProviderFactory;
import netgest.bo.xwc.framework.cache.provider.CacheProviderFactory.CacheType;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Cache component. Caches the content of its children
 *
 */
public class Cache extends XUIComponentBase{
	
	/**
	 * The cache key
	 */
	@Required
	XUIBindProperty< String > cacheKey = new XUIBindProperty< String >(
			"cacheKey" , this , String.class );

	public String getCacheKey() {
		return cacheKey.getEvaluatedValue();
	}

	public void setCacheKey(String newValExpr) {
		cacheKey.setExpressionText( newValExpr );
	}
	
	/**
	 * The cache type to use
	 */
	XUIBaseProperty< String > cacheType = new XUIBaseProperty< String >(
			"cacheType" , this, CacheType.TRIGGER_BASED.name() );

	public String getCacheType() {
		return cacheType.getValue();
	}

	public void setCacheType(String newValExpr) {
		cacheType.setValue( newValExpr );
	}
	
	/**
	 * The number of seconds for the cached content to expire
	 * if value is 0 the content does not expire 
	 */
	XUIBindProperty< Integer > secondsToExpire = new XUIBindProperty< Integer >(
			"secondsToExpire" , this , Integer.class );

	public Integer getSecondsToExpire() {
		return secondsToExpire.getEvaluatedValue();
	}
	
	public boolean secondsToExpireWasSet(){
		return !secondsToExpire.isDefaultValue();
	}

	public void setSecondsToExpire(String newValExpr) {
		secondsToExpire.setExpressionText( newValExpr );
	}
	
	/**
	 * The cache provider for this instance
	 */
	private CacheEngine provider = null;
	
	protected CacheEngine getCacheProvider(){
		if (provider == null)
			provider = CacheProviderFactory.getCacheProvider( CacheType.valueOf( getCacheType() ) );
		return provider;	
	}
	
	public Fragment[] getScriptContentFromCache(){
		CacheContent content = (CacheContent) getCacheProvider().get( getCacheKey() ).getContent(); 
		return content.getScriptContent();
	}
	
	public String getContentFromCache(){
		CacheContent content = (CacheContent) getCacheProvider().get( getCacheKey() ).getContent(); 
		return String.valueOf(content.getHtmlContent());
	}
	
	public void putInCache(CacheContent content, Date expiresAt){
		getCacheProvider().add( getCacheKey() , content, expiresAt );
	}
	
	public void putInCache(CacheContent content){
		getCacheProvider().add( getCacheKey() , content);
	}
	
	public boolean inCache(){
		CacheEntry entry = getCacheProvider().get( getCacheKey() ) ;
		if (entry != null && entry != CacheElement.NULL_ENTRY)
			return true;
		return false;
	}
	
	
	protected class CacheContent{
		
		public Fragment[] getScriptContent() {
			return scriptContent;
		}
		
		public String getHtmlContent() {
			return htmlContent;
		}
		
		private Fragment[] scriptContent;
		private String htmlContent;
		public CacheContent(String htmlContent, Fragment[] scriptContent ) {
			this.htmlContent = htmlContent;
			this.scriptContent = scriptContent;
		}
	}
	
}
