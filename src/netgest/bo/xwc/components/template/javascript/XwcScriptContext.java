package netgest.bo.xwc.components.template.javascript;

import netgest.bo.xwc.framework.XUIScriptContext;

public class XwcScriptContext implements JavaScriptContext {

	private XUIScriptContext scriptContext;
	
	public XwcScriptContext(XUIScriptContext script){
		this.scriptContext = script;
	}
	
	
	@Override
	public void add( String script, String scriptId, Position position ) {
		switch (position ){
			case HEADER : scriptContext.add( XUIScriptContext.POSITION_HEADER, scriptId, script ); break;
			case FOOTER : scriptContext.add( XUIScriptContext.POSITION_FOOTER, scriptId, script ); break;
			case INLINE : scriptContext.add( XUIScriptContext.POSITION_INLINE, scriptId, script ); break;
			default : scriptContext.add( XUIScriptContext.POSITION_HEADER, scriptId, script ); break;
		}
	}

	@Override
	public void include( String url, String scriptId, Position position ) {
		switch (position ){
			case HEADER : scriptContext.addInclude( XUIScriptContext.POSITION_HEADER, scriptId, url ); break;
			case FOOTER : scriptContext.addInclude( XUIScriptContext.POSITION_FOOTER, scriptId, url ); break;
			case INLINE : scriptContext.addInclude( XUIScriptContext.POSITION_INLINE, scriptId, url ); break;
			default : scriptContext.add( XUIScriptContext.POSITION_HEADER, scriptId, url ); break;
		}
	}

}
