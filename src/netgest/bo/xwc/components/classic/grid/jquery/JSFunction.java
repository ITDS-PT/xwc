package netgest.bo.xwc.components.classic.grid.jquery;

public class JSFunction implements Unescape {
	
	private String function;
	
	public JSFunction(String function){
		this.function = function;
	}
	
	@Override
	public String toString() {
		return function;
	}

}
