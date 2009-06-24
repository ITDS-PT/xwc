package netgest.bo.xwc.components.connectors;

import java.util.Iterator;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.bridgeHandler;

public class XEOBridgeListConnector implements DataListConnector {
    
	public DataListConnector getGroup(String groupAttribute, int page,
			int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataListConnector getGroupDetail(String groupAttribute,
			Object groupValue, int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGroupBy(String[] attributes) {
		// TODO Auto-generated method stub
		
	}

	public String[] getGroupItens(String groupAttribute, int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	private bridgeHandler oBridge;
    
    public XEOBridgeListConnector( bridgeHandler oBridge ) {
        
        assert oBridge != null : "Cannot create a object with bridge null";
            
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
		// TODO Auto-generated method stub
		
	}

	public void setSortTerms(SortTerms sortTerms) {
    	throw new IllegalArgumentException( "Bridges doesn't support order terms" );
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
		// TODO Auto-generated method stub
		
	}

	public void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
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
		//int previousPos;
		
		DataRecordConnector drc;
		
		drc = null;
		
		//previousPos = this.oBridge.getRow();
		
		boui = Long.parseLong( sUniqueIdentifier );
		
		//if ( this.oBridge.haveBoui( boui ) ) {
			drc = new XEOObjectConnector( boui );
		//}
		//this.oBridge.moveTo( previousPos );

		return drc;
	
	}
	
	public DataListConnector getGroupDetails(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataGroupConnector getGroups(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public int dataListCapabilities() {
		return 0; //DataListConnector.CAP_PAGING;
	}
	
}
