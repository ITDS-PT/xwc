package netgest.bo.xwc.components.classic.grid;

import netgest.bo.xwc.components.classic.grid.Aggregate.AggregateAction;
import netgest.bo.xwc.components.classic.grid.Aggregate.AggregateOperation;
import netgest.utils.StringUtils;

public class AggregateDecoder {

	private AggregateAction action = AggregateAction.UNDEFINED;
	private AggregateOperation operation = AggregateOperation.NONE;
	private String field = "";
	private String fieldDescription = "";
	
	private final int ACTION_INDEX = 0;
	private final int OPERATION_INDEX = 1;
	private final int FIELD_INDEX = 2;
	private final int DESCRIPTION_INDEX = 3;
	
	
	private final int AGGREGATE_FIELD_COUNT = 4;
	
	public AggregateDecoder(String aggregateFields){
		
		if (StringUtils.hasValue( aggregateFields )){
			String[] aggregateTokens = aggregateFields.split(":");
			if (aggregateTokens.length == AGGREGATE_FIELD_COUNT){
				action = AggregateAction.fromString(aggregateTokens[ACTION_INDEX]); 
				operation = AggregateOperation.fromString( aggregateTokens[OPERATION_INDEX] ); 
				field = aggregateTokens[FIELD_INDEX];
				fieldDescription = aggregateTokens[DESCRIPTION_INDEX];
			} else {
				resetAll();
			}
		} else
			resetAll();
	}
	
	public void resetAll(){
		action = AggregateAction.UNDEFINED;
		operation = AggregateOperation.NONE;
		field = null;
		fieldDescription = null;
	}
	
	public AggregateAction getAction(){
		return action ;
	}
	
	public String getActionString(){
		if (action == AggregateAction.UNDEFINED)
			return null;
		return action.getValue() ;
	}
	
	public AggregateOperation getOperation(){
		return operation;
	}
	
	public String getOperationString(){
		if (operation == AggregateOperation.NONE)
			return null;
		return operation.getValue();
	}
	
	public String getField(){
		return field;
	}
	
	public String getFieldDescription(){
		return fieldDescription;
	}
	
}
