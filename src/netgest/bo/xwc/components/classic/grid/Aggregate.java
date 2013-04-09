package netgest.bo.xwc.components.classic.grid;

public class Aggregate{
	
	public enum AggregateAction{
	
	  ADD_AGGREGATE("T")
	, REMOVE_AGGREGATE("F")
	, UNDEFINED("");
	  
	  private String value;
	  
	  private AggregateAction(String val){
		  this.value = val;
	  }
	  
	  public String getValue(){
		  return value;
	  }
	  
	  public static boolean isSame(AggregateAction agg, String value){
		  assert value != null : "Value can't be null";
		  if (agg.getValue().equalsIgnoreCase( value ))
			  return true;
		  else
			  return false;
	  }
	  
	  public static AggregateAction fromString(String value){
		  for (AggregateAction current : values()){
			  if (current.getValue().equalsIgnoreCase( value ))
				  return current;
		  }
		  return UNDEFINED;
	  }
	}
	
	
	public enum AggregateOperation{
		
		  MAX("MAX")
		, MIN("MIN")
		, AVG("AVG")
		, SUM("SUM")
		, NONE("") ;
		  
		  private String value;
		  
		  private AggregateOperation(String val){
			  this.value = val;
		  }
		  
		  public String getValue(){
			  return value;
		  }
		  
		  public static boolean isSame(AggregateOperation agg, String value){
			  assert value != null : "Value can't be null";
			  if (agg.getValue().equalsIgnoreCase( value ))
				  return true;
			  else
				  return false;
		  }
		  
		  public static AggregateOperation fromString(String value){
			  for (AggregateOperation current : values()){
				  if (current.getValue().equalsIgnoreCase( value ))
					  return current;
			  }
			  return NONE;
		  }
		}
}