package netgest.bo.xwc.components.connectors;

import java.util.ArrayList;
import java.util.HashMap;

public interface AggregableDataList extends DataListConnector {
    public void addAggregateField(String fieldId, String fieldDesc,
			String aggregateType);
    
    public void setAggregateFields(HashMap<String, ArrayList<String>> aggregateFields);

	public void removeAggregateField(String fieldId, String summaryType);
	
	public HashMap<String, ArrayList<String>> getAggregateFields();
}
