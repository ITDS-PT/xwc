package netgest.bo.xwc.components.util;

public class ScriptBuilder implements CharSequence {
	
	private static final char ESCAPECHAR = '\'';
	
	private StringBuilder builder;
	
	public ScriptBuilder() {
		this.builder = new StringBuilder();
	}
	
	public ScriptBuilder( StringBuilder stringBuilder ) {
		this.builder = stringBuilder;
	}
	
	public ScriptBuilder statement( CharSequence script ) {
		this.builder.append( script ).append( ';' ).append( '\n' );
		return this;
	}

	public ScriptBuilder s( CharSequence script ) {
		return statement( script );
	}

	public ScriptBuilder l( CharSequence script ) {
		this.builder.append( script ).append( '\n' );
		return this;
	}

	public ScriptBuilder endStatement() {
		this.builder.append( ';' ).append( '\n' );
		return this;
	}
	
	public ScriptBuilder w( CharSequence script ) {
		this.builder.append( script );
		return this;
	}

	public ScriptBuilder w( boolean script ) {
		this.builder.append( script );
		return this;
	}
	
	public ScriptBuilder writeValue( Object value, char charToEscape  ) {
		w( JavaScriptUtils.writeValue( value, charToEscape ) );
		return this;
	}

	public ScriptBuilder writeValue( Object value  ) {
		w( JavaScriptUtils.writeValue( value ) );
		return this;
	}
	
	public ScriptBuilder block( CharSequence script ) {
		this.builder.append( '{' ).append( script ).append( '}' );
		return this;
	}
	
	public StringBuilder getScript() {
		return this.builder;
	}
	
	public ScriptBuilder startBlock() {
		this.builder.append( '{' ).append('\n');
		return this;
	}
	public ScriptBuilder endBlock() {
		this.builder.append( '}' ).append('\n');
		return this;
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}

	@Override
	public char charAt(int index) {
		return this.builder.charAt( index );
	}

	@Override
	public int length() {
		return this.builder.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.builder.subSequence( start, end );
	}
	
}
