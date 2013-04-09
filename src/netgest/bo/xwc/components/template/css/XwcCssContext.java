package netgest.bo.xwc.components.template.css;

import netgest.bo.xwc.components.template.javascript.JavaScriptContext.Position;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;

/**
 * Implementation of the CSS Context
 *
 */
public class XwcCssContext implements CssContext {

	private XUIStyleContext styleCtx;
	
	public XwcCssContext(XUIStyleContext style){
		this.styleCtx = style;
	}
	
	@Override
	public void add( String script, String scriptId, Position position ) {
		switch (position ){
		case HEADER : styleCtx.add( XUIScriptContext.POSITION_HEADER, scriptId, script ); break;
		case FOOTER : styleCtx.add( XUIScriptContext.POSITION_FOOTER, scriptId, script ); break;
		case INLINE : styleCtx.add( XUIScriptContext.POSITION_INLINE, scriptId, script ); break;
		default : styleCtx.add( XUIScriptContext.POSITION_HEADER, scriptId, script ); break;
	}
	}

	@Override
	public void include( String url, String scriptId, Position position ) {
		switch (position ){
		case HEADER : styleCtx.addInclude( XUIScriptContext.POSITION_HEADER, scriptId, url ); break;
		case FOOTER : styleCtx.addInclude( XUIScriptContext.POSITION_FOOTER, scriptId, url ); break;
		case INLINE : styleCtx.addInclude( XUIScriptContext.POSITION_INLINE, scriptId, url ); break;
		default : styleCtx.add( XUIScriptContext.POSITION_HEADER, scriptId, url ); break;
	}
	}
	

}
