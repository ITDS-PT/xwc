package netgest.bo.xwc.components.connectors;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.security.SecurityPermissions;

public class XEOObjectListGroupDataRecord implements DataRecordConnector {

	private XEOObjectListGroupConnector parent;
	private int row;

	public XEOObjectListGroupDataRecord(int row,
			XEOObjectListGroupConnector parent) {
		this.row = row;
		this.parent = parent;
	}

	public DataFieldConnector getAttribute(String name) {
		try {
			boDefHandler objDef = parent.getRootList().oObjectList.getBoDef();
			
			boDefAttribute defAtt = objDef.getAttributeRef( name );
			
			if( defAtt == null && name.indexOf('.') > -1 ) {
				String[] atts = name.split("\\.");
				boDefHandler childObjDef = objDef;
				for( String att : atts ) {
					defAtt = childObjDef.getAttributeRef(att);
					if( defAtt != null && defAtt.getAtributeType()== boDefAttribute.TYPE_OBJECTATTRIBUTE ) {						
						childObjDef = defAtt.getReferencedObjectDef();
					}
					else {
						DataFieldConnector ret = handleSpecialFields(childObjDef, att);
						if( ret != null )
							return ret;
						break;
					}
				}
				if( defAtt != null )
					return new XEOObjectListGroupAttribute( defAtt, this.row, this.parent, true);
			}
			else if ( defAtt != null ) {
				return new XEOObjectListGroupAttribute( defAtt, this.row, this.parent, true);
			} else if ( name.contains("__" ) && !name.endsWith("__count") && !name.endsWith("__value") && !name.endsWith("__aggregate")) {
				//In the column definition for the attribute boql's dot syntax is used, but internal transformations
				//use "__" instead of dot syntax, as such the comparison is done against the "__" string. 
				//The "__value" and "__count" expressions are special to the grid's datastore
				return new XEOObjectListGroupAttribute( 
						XEOObjectConnector.getAttributeDefinitionFromName(name, objDef), this.row, this.parent, true );
				}
			else {
				if (this.parent.getDataSet() != null) {
					DataSet dataSet = this.parent.getDataSet();
					int col = dataSet.findColumn(name);
					if (col > 0) {
						Object value = dataSet.rows(row).getObject(col);
						if (value == null) {
							value = "";
						}
						return new XEOObjectConnector.GenericFieldConnector(
								name, String.valueOf(value),
								DataFieldTypes.VALUE_CHAR);
					}
				}
			}
			return handleSpecialFields(objDef, name);
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	private DataFieldConnector handleSpecialFields( boDefHandler objDef, String name ) {
		if (name.endsWith("__count")) {
			return new XEOObjectListAttributeCount(this.parent, this.row,
					name);
		}
		else if (name.endsWith("__aggregate")) {
			return new XEOObjectListAttributeAggregate(this.parent, this.row,
					name);
		}
		else if (name.endsWith("__value")) {
			String nName = name.substring(0, name.indexOf("__value"));
			if (objDef.getAttributeRef(nName) != null) {
				return new XEOObjectListGroupAttribute(objDef
						.getAttributeRef(nName), this.row, this.parent,
						false);
			} else {
				if (this.parent.getDataSet() != null) {
					DataSet dataSet = this.parent.getDataSet();
					int col = dataSet.findColumn(nName);
					if (col > 0) {
						Object value = dataSet.rows(row).getObject(col);
						if (value == null) {
							value = "";
						}
						return new XEOObjectConnector.GenericFieldConnector(
								name, String.valueOf(value),
								DataFieldTypes.VALUE_CHAR);
					}
				}
			}
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
