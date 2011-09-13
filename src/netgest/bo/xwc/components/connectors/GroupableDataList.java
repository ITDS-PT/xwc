package netgest.bo.xwc.components.connectors;

public interface GroupableDataList extends DataListConnector {

	public DataGroupConnector getGroups( String[] parentGroups, Object[] parentValues, String groupField, int page, int pageSize );
    public DataListConnector getGroupDetails( String[] parentGroups, Object[] parentValues, String groupField, int page, int pageSize );

}
