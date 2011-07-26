package netgest.bo.xwc.components.connectors;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.security.SecurityPermissions;

public class XEOObjectListGroupDataRecord implements DataRecordConnector {
	
	private XEOObjectListGroupConnector parent;
	private int row;
	
	public XEOObjectListGroupDataRecord( int row, XEOObjectListGroupConnector parent ) {
		this.row = row;
		this.parent = parent;
	}
	
	public DataFieldConnector getAttribute(String name) {
		try {
			boDefHandler objDef = parent.getRootList().oObjectList.getBoDef();
			
			if( objDef.getAttributeRef( name ) != null ) {
				return new XEOObjectListGroupAttribute( objDef.getAttributeRef( name ), this.row, this.parent, true );
			} else if ( name.contains("__" ) && !name.endsWith("__count") && !name.endsWith("__value")) {
				//In the column definition for the attribute boql's dot syntax is used, but internal transformations
				//use "__" instead of dot syntax, as such the comparison is done against the "__" string. 
				//The "__value" and "__count" expressions are special to the grid's datastore
				return new XEOObjectListGroupAttribute( 
						XEOObjectConnector.getAttributeDefinitionFromName(name, objDef), this.row, this.parent, true );
				}
			else {
                            if( this.parent.getDataSet() != null ) { 
                                    DataSet dataSet = this.parent.getDataSet();
                                    int col = dataSet.findColumn( name );
                                    if( col > 0 ) {
                                            Object value = dataSet.rows( row ).getObject( col );
                                            if( value == null ) {
                                                value = "";
			}
                                            return new XEOObjectConnector.GenericFieldConnector( 
                                                            name, 
                                                            String.valueOf( value ), 
                                                            DataFieldTypes.VALUE_CHAR 
                                            );
                                    }
                            }
                        }
                        
			if( name.endsWith("__count") ) {
				return new XEOObjectListAttributeCount( this.parent, this.row, name );
			} if( name.endsWith("__value") ) {
				String nName = name.substring( 0, name.indexOf( "__value" ) );
                                if( objDef.getAttributeRef( nName ) != null ) {
                                    return new XEOObjectListGroupAttribute( objDef.getAttributeRef( nName ), this.row, this.parent, false );
                                } else if ( nName.contains("__")) {
                                	boDefAttribute targetAttributeDefinition = XEOObjectConnector.getAttributeDefinitionFromName(nName, objDef);
                    				return new XEOObjectListGroupAttribute( targetAttributeDefinition, this.row, this.parent, false );
                                }
                                else {
                                    if( this.parent.getDataSet() != null ) {
                                            DataSet dataSet = this.parent.getDataSet();
                                            int col = dataSet.findColumn( nName );
                                            if( col > 0 ) {
                                                    Object value = dataSet.rows( row ).getObject( col );
                                                    if( value == null ) {
                                                        value = "";
                                                    }
                                                    return new XEOObjectConnector.GenericFieldConnector( 
                                                                    name, 
                                                                    String.valueOf( value ), 
                                                                    DataFieldTypes.VALUE_CHAR 
                                                    );
                                            }
                                    }
                                }
			}
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

	public String getRowClass() {
		return "";
	}

	public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}

	@Override
	public int getRowIndex() {
		return row;
	}
	
}
