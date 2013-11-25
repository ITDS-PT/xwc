package netgest.bo.xwc.framework.viewers.jslog;

public class LogRecord {
	
	private String key;
	private Object value;
	
	public LogRecord(final String key, final  Object value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return key;
	}
	
	public Object getValue(){
		return value;
	}

}
