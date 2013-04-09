package netgest.bo.xwc.components.classic.grid;

public enum Direction implements WebParameter
{
	  ASCENDING("ASC")
	, DESCENDING("DESC")
	, NONE("");
	
	private String name;
	
	private Direction(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	  
	public static Direction fromString(String setting){
		if ("ASC".equalsIgnoreCase( setting ))
			return ASCENDING;
		else if ("DESC".equalsIgnoreCase( setting ))
			return DESCENDING;
		else
			return NONE;
	}
}