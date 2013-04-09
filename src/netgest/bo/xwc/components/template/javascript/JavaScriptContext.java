package netgest.bo.xwc.components.template.javascript;


public interface JavaScriptContext {
	
	public enum Position{
		HEADER,
		INLINE,
		FOOTER;
		
		public static Position fromString(String name){
			for (Position pos : values()){
				if (pos.name().equalsIgnoreCase( name ))
					return pos;
			}
			return HEADER;
		}
		
		
	}
	
	/**
	 * 
	 * Adds a script to execute
	 * 
	 * @param script The script
	 * @param scriptId The script identifier
	 * @param position The position of the script
	 */
	public void add(String script, String scriptId, Position position);
	
	/**
	 * 
	 * Includes a javascript file
	 * 
	 * @param url The script url
	 * @param scriptId The script identifier
	 * @param position The position of the script
	 */
	public void include(String url, String scriptId, Position position);

}
