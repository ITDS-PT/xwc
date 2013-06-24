package netgest.bo.xwc.components.connectors;

import java.util.List;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class XEOBridgeListConnector implements DataListConnector {
    
	public DataListConnector getGroup(String groupAttribute, int page,
			int pageSize) {
		return null;
	}

	public DataListConnector getGroupDetail(String groupAttribute,
			Object groupValue, int page, int pageSize) {
		return null;
	}

	public void setGroupBy(String[] attributes) {
		
	}
	
	@Override
	public void setSqlFields(List<SqlField> sqlFields) {
		// TODO Auto-generated method stub
		
	}
	
	public String[] getGroupItens(String groupAttribute, int page, int pageSize) {
		return null;
	}

	private bridgeHandler oBridge;
    
    public XEOBridgeListConnector( bridgeHandler oBridge ) {
        
        assert oBridge != null : MessageLocalizer.getMessage("CANNOT_CREATE_OBJECT_WITH_BRIDGE_NULL");
            
        this.oBridge = oBridge;    
    }

    public void setBridge(bridgeHandler oBridge) {
        this.oBridge = oBridge;
    }

	public DataFieldMetaData getAttributeMetaData( String attributeName ) {
		
		boDefAttribute oAtt = ((boDefBridge)oBridge.getDefAttribute().getBridge()).getAttributeRef( attributeName );
		if( oAtt == null ) {
			
			boDefHandler def = oBridge.getDefAttribute().getReferencedObjectDef();
			if( def != null ) {
				oAtt = def.getAttributeRef( attributeName );
			}
		}
		if( oAtt != null ) {
			return new XEOObjectAttributeMetaData( oAtt );
		}
		return null;
	}


	public bridgeHandler getBridge() {
        return oBridge;
    }

    public void setFilterTerms(FilterTerms sortTerms) {
	}

	public void setSortTerms(SortTerms sortTerms) {
    	throw new IllegalArgumentException( ExceptionMessage.BRIDGES_DOESNT_SUPPORT_ORDER_TERMS.toString());
	}
    
    public DataListIterator iterator() {
        return new XEOBridgeRowIterator( this );
    }

    public int getPage() {
		return 1;
		
	}

	public int getPageSize() {
		return getRecordCount();
	}

	public void setPage(int pageNo) {
	}

	public void setPageSize(int pageSize) {
	}

	public void setSearchTerms(String[] columnName, Object[] columnValue) {
		
	}

	public void setSearchText(String searchText) {

	}
	
	public void refresh() {
		
	}

	public int getRecordCount() {
        
        return oBridge.getRowCount();
        
    }

    public int getRowCount() {

        return oBridge.getRowCount();

    }

	public DataRecordConnector findByUniqueIdentifier(String sUniqueIdentifier) {
		long boui;
		
		DataRecordConnector drc;
		
		drc = null;

		boui = Long.parseLong( sUniqueIdentifier );
		
		drc = new XEOObjectConnector( boui, indexOf( sUniqueIdentifier ) );

		return drc;
	
	}
	
	public DataListConnector getGroupDetails(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		return null;
	}

	public DataGroupConnector getGroups(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		return null;
	}

	public int dataListCapabilities() {
		return 0; //DataListConnector.CAP_PAGING;
	}

	@Override
	public int indexOf(String sUniqueIdentifier) {
		int ret = -1;
		
		int lastRow = this.oBridge.getRow();
		if( this.oBridge.haveBoui( Long.valueOf( sUniqueIdentifier ) ) ) {
			ret = this.oBridge.getRow();
		}
		this.oBridge.moveTo( lastRow );
		return ret;
	}
	
    public boolean hasMoreResults() {
    	return true;
    }

	@Override
	public boolean hasMorePages() {
		return oBridge.haveMorePages();
	}
	
	
}
