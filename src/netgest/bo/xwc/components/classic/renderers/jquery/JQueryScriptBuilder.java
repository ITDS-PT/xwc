package netgest.bo.xwc.components.classic.renderers.jquery;

import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;

public abstract class JQueryScriptBuilder {

	protected StringBuilder b;
	
	protected JQueryScriptBuilder(){
		b = new StringBuilder(200);
		b.append( " $(function() { " );
	}
	
	public String build(){
		b.append( ";}); " ); //Terminates the command and the function 
		return b.toString();
	}
	
	protected String getClientId(String id){
		return JQueryBuilder.convertIdJquerySelector( id );
	}
	
}
