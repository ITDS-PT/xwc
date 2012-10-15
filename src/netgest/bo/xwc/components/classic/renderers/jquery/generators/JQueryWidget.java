package netgest.bo.xwc.components.classic.renderers.jquery.generators;

import java.util.Iterator;


import org.json.JSONException;
import org.json.JSONObject;


public class JQueryWidget extends JQueryBuilder {

	protected String widgetName;
	
	private boolean firstOption = true;
	
	private boolean isFirstOption(){
		return firstOption;
	}
	
	protected JQueryWidget(String name){
		super();
		this.widgetName = name;
	}
	
	protected JQueryWidget optionBase(String optionName){
		b.append( "." ).append( widgetName ).append("('option','").append(optionName)
		.append("',");
		return this;
	}
	
	public JQueryWidget option(String optionName, String value){
		optionBase( optionName );
		b.append("'").append( value ).append( "')" );
		return this;
	}
	
	public JQueryWidget option(String optionName, boolean value){
		optionBase( optionName );
		b.append("").append( value ).append( ")" );
		return this;
	}
	
	public JQueryWidget option(String optionName, int value){
		optionBase( optionName );
		b.append("").append( value ).append( ")" );
		return this;
	}
	
	public JQueryWidget create(){
		b.append(".").append(widgetName).append("()");
		return this;
	}
	
	public JQueryWidget updateOption(String name, String value){
		b.append(".").append(widgetName).append("('option',")
			.append("'").append(name).append("','").append(value)
			.append("')");
		return this;
	}
	
	public JQueryWidget updateOption(String name, long value){
		b.append(".").append(widgetName).append("('option',")
			.append("'").append(name).append("',").append(value)
			.append(")");
		return this;
	}
	
	public JQueryWidget updateOption(String name, boolean value){
		b.append(".").append(widgetName).append("('option',")
			.append("'").append(name).append("',").append(value)
			.append(")");
		return this;
	}
	
	public JQueryWidget createWithOptions(String options){
		b.append(".").append(widgetName).append("({");
		b.append(options);
		b.append("})");
		return this;
	}
	
	
	
	public JQueryWidget createAndStartOptions(){
		b.append(".").append(widgetName).append("({");
		return this;
	}
	
	public JQueryWidget addOption(String name, String value){
		checkFirstOption();
		b.append("'").append(name).append("' : ").append("'").append(value).append("'");
		return this;
	}
	
	public JQueryWidget addNonLiteral(String name, String value){
		checkFirstOption();
		b.append("'").append(name).append("' : ").append(value);
		return this;
	}

	private void checkFirstOption() {
		if (isFirstOption()){
			markFirstOptionUsed();
		} else 
			b.append(",");
	}

	private void markFirstOptionUsed() {
		this.firstOption = false;
	}
	
	public JQueryWidget addOption(String name, boolean value){
		checkFirstOption();
		b.append("'").append(name).append("' : ").append(value);
		return this;
	}
	
	public JQueryWidget addOption(String name, long value){
		checkFirstOption();
		b.append("'").append(name).append("' : ").append(value);
		return this;
	}
	
	public JQueryWidget endOptions(){
		b.append("})");
		return this;
	}
	
	public JQueryWidget createWithOptions(JSONObject object){
		b.append(".").append(widgetName).append("(");
		@SuppressWarnings( "unchecked" )
		Iterator<String> it = object.keys();
		while (it.hasNext()){
			String key = it.next();
			try {
				Object value = object.get( key );
				value.toString();
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}
		b.append(")");
		return this;
	}

	public JQueryWidget selectorById( String id ) {
		b.append( "$( '#" ).append(id).append("' )");
		return this;
	}
	
	public JQueryWidget componentSelectorById( String clientId ) {
		b.append( "$( '#" ).append( JQueryBuilder.convertIdJquerySelector( clientId ) ).append("' )");
		return this;
	}
	
	

	public JQueryWidget selectorByCss( String expression ) {
		b.append( "$( '" ).append( expression ).append("' )");
		return this;
	}

	public JQueryWidget command( String command ) {
		b.append(".").append(command);
		return this;
	}
	
	public JQueryWidget click( String command ) {
		b.append(".click( function () { ").append(command).append(" })");
		return this;
	}
	
	
}
