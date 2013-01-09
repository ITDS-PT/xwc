package netgest.bo.xwc.components.classic.renderers.jquery.generators;

import netgest.bo.xwc.components.classic.renderers.jquery.JQueryScriptBuilder;

public class JQueryBuilder extends JQueryScriptBuilder {

	
	public JQueryBuilder selectorById( String id ) {
		b.append( "$( XVW.get('" ).append(id).append("'))");
		return this;
	}
	
	public JQueryBuilder selectorByCss( String css ) {
		b.append( "$( '" ).append(css).append("' )");
		return this;
	}
	
	public JQueryBuilder hide() {
		b.append( ".hide()");
		return this;
	}
	
	public JQueryBuilder show() {
		b.append( ".show()");
		return this;
	}
	
	public JQueryBuilder toggle() {
		b.append( ".toggle()");
		return this;
	}
	
	public JQueryBuilder disable(){
		b.append( ".attr('disabled', 'disabled')");
		return this;
	}
	
	public JQueryBuilder enable(){
		b.append( ".removeAttr('disabled')");
		return this;
	}
	
	public JQueryBuilder componentSelectorById( String clientId ) {
		return selectorById( clientId );
	}
	
	public JQueryBuilder setInputValue(String value){
		b.append( ".val('").append(value).append("')");
		return this;
	}
	
	public JQueryBuilder addClass(String css) {
		b.append( ".addClass('").append(css).append("')");
		return this;
	}
	
	public JQueryBuilder removeClass(String css) {
		b.append( ".removeClass('").append(css).append("')");
		return this;
	}
	
	public JQueryBuilder command( String command ) {
		b.append(".").append(command);
		return this;
	}

	/**
	 * 
	 * Converts a Component id into a suitable id for a JQuery Selector
	 * 
	 * @param componentId The component identifier
	 * 
	 * @return A valid Id for a JQuery selector
	 */
	public static String convertIdJquerySelector(String componentId){
		String result1 = componentId.replaceAll( "\\.", "\\\\\\\\." );
		String result2 = result1.replaceAll( "\\/", "\\\\\\\\/" );
		String result3 = result2.replaceAll( ":", "\\\\\\\\:" );
		//return result3;
		return componentId;
		
	}
	
}
