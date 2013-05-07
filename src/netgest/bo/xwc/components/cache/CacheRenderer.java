package netgest.bo.xwc.components.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import netgest.bo.system.Logger;
import netgest.bo.xwc.components.cache.Cache.CacheContent;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIScriptContext.Fragment;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class CacheRenderer extends XUIRenderer {

	private static final Logger logger = Logger.getLogger( CacheRenderer.class );
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
	}
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
	}
	
	@Override
	public void encodeChildren(XUIComponentBase component) throws IOException {
		Cache cache = (Cache) component;
		if (cache.inCache()){
			if (logger.isFineEnabled())
				logger.fine(" Reading from cache %s", cache.getCacheKey());
			String content = cache.getContentFromCache();
			Fragment[] scripts = cache.getScriptContentFromCache();
			for (Fragment f : scripts){
				getRequestContext().getScriptContext().add( f.getPosition() , f.getScriptId() , f.getContent() );
			}
			getResponseWriter().write( content );
		} else {
			if (logger.isFineEnabled())
				logger.fine(" Processing %s", cache.getCacheKey());
			XUIRequestContext ctx = XUIRequestContext.getCurrentContext();
			CacheContent contentToCache = switchWriters( component , ctx );
			if (!cache.secondsToExpire.isDefaultValue()){
				Date newExpireDate = calculateExpiredDate( cache.getSecondsToExpire() ); 
				cache.putInCache( contentToCache, newExpireDate );
			}
			else
				cache.putInCache( contentToCache );
		}
		
	}

	protected Date calculateExpiredDate(Integer secondsToExpire) {
		if (secondsToExpire != null && secondsToExpire.intValue() > 0)
			return new Date(System.currentTimeMillis() + ( secondsToExpire *  1000 ) );
		else
			return null;
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	protected CacheContent switchWriters(XUIComponentBase component,
			XUIRequestContext ctx) throws IOException {
		
		Cache cache = (Cache) component;
		StringWriter cacheContent = new StringWriter();
		XUIResponseWriter cacheWriter = new XUIResponseWriter( cacheContent , "text/html" , "UTF-8" );
		XUIScriptContext scriptContextToCache = new XUIScriptContext();
		
		XUIResponseWriter originalWriter = ctx.getResponseWriter();
		XUIScriptContext originalContext = ctx.getScriptContext();

		setNewContextForCache( ctx , cacheWriter , scriptContextToCache );
		
		super.encodeChildren( component );
		
		restoreOriginalContext( ctx , originalWriter , originalContext );
	
		CacheContent content = cache.new CacheContent( cacheContent.toString(), getScripts( scriptContextToCache ) );
		writeCapturedScriptsToOriginalContext(scriptContextToCache, originalContext);
		originalWriter.write( cacheContent.toString() );
		return content;
	}

	private void writeCapturedScriptsToOriginalContext(
			XUIScriptContext scriptContextToCache,
			XUIScriptContext originalContext) {
		
		Fragment[] footerFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_FOOTER );
		Fragment[] headerFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_HEADER );
		Fragment[] inlineFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_INLINE );
		for (Fragment f : headerFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				originalContext.add( f.getPosition() , f.getScriptId() , f.getContent() );
			}
		}
		for (Fragment f : inlineFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				originalContext.add( f.getPosition() , f.getScriptId() , f.getContent() );
			}
		}
		for (Fragment f : footerFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				originalContext.add( f.getPosition() , f.getScriptId() , f.getContent() );
			}
		}
		
	}

	private Fragment[] getScripts(XUIScriptContext scriptContextToCache) {
		Fragment[] footerFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_FOOTER );
		Fragment[] headerFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_HEADER );
		Fragment[] inlineFragments = scriptContextToCache.getFragments( XUIScriptContext.POSITION_INLINE );
		List<Fragment> b = new ArrayList< XUIScriptContext.Fragment >();
		for (Fragment f : headerFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				b.add( f );
			}
		}
		for (Fragment f : inlineFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				b.add( f );
			}
		}
		for (Fragment f : footerFragments){
			if (f.getType() == XUIScriptContext.TYPE_TEXT){
				b.add( f );
			}
		}
		
		return b.toArray(new Fragment[b.size()]);
	}

	protected void restoreOriginalContext(XUIRequestContext ctx,
			XUIResponseWriter originalWriter, XUIScriptContext originalContext) {
		ctx.getFacesContext().setResponseWriter( originalWriter );
		ctx.setScriptContext( originalContext );
	}

	protected void setNewContextForCache(XUIRequestContext ctx,
			XUIResponseWriter cacheWriter, XUIScriptContext toCacheContext) {
		ctx.getFacesContext().setResponseWriter( cacheWriter );
		cacheWriter.setScriptContext( toCacheContext );
		ctx.setScriptContext( toCacheContext );
	}
	
	@Override
	public void encodeComponentChanges(XUIComponentBase component,
			List< XUIBaseProperty< ? >> propertiesWithChangedState)
			throws IOException {
		super.encodeComponentChanges( component , propertiesWithChangedState );
	}
	
	
	
}
