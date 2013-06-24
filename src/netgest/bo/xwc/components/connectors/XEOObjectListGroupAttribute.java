package netgest.bo.xwc.components.connectors;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.localization.ConnectorsMessages;

public class XEOObjectListGroupAttribute extends XEOObjectAttributeMetaData implements DataFieldConnector {

	private XEOObjectListGroupConnector parent;
	private int 						row;
	private boolean 					displayValue;
	
	public XEOObjectListGroupAttribute( boDefAttribute def, int row, XEOObjectListGroupConnector parent, boolean displayValue ) {
		super( def );
		this.parent = parent;
		this.row = row;
		this.displayValue = displayValue;
	}
	
	public DataListConnector getDataList() {
		return null;
	}

	public String[] getDependences() {
		return null;
	}

	public boolean getDisabled() {
		return false;
	}

	public String getDisplayValue() {
		Object attValue =  getValue();
		if( !displayValue ) {
			return String.valueOf( attValue );
		}
    	String sRetValue = null;
    	try {
			if ( super.getBoDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE ) {
				
				boObject obj = boObject.getBoManager().loadObject( getEboContext(), ((BigDecimal)attValue).longValue() );
				if( obj != null ) {
					sRetValue = obj.getCARDIDwNoIMG().toString();
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_BOOLEAN ) {
				sRetValue = (String)attValue;
				if ( "0".equals( sRetValue ) ) {
					sRetValue = ConnectorsMessages.TEXT_NO.toString();
				} else if ( "1".equals( sRetValue ) ) {
					sRetValue = ConnectorsMessages.TEXT_YES.toString();
				}
			} else if ( getInputRenderType() == DataFieldTypes.RENDER_IFILE_LINK ) {
				sRetValue = String.valueOf( attValue );
				//iFile oFile = oAttHandler.getValueiFile();
				//if( oFile != null ) {
				//	sRetValue = oFile.getName();
				//}
				
			} else if ( getIsLov() ) {
				Map<Object,String> 	lovMap;
				Object value;
				
				value  = attValue;
				lovMap = getLovMap();
				
				if( value != null && lovMap.size() > 0 )
				{
					if( lovMap.containsKey( value ) ) {
						return lovMap.get( value );
					}
					sRetValue = String.valueOf( value ); 
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_DATE || getDataType() == DataFieldTypes.VALUE_DATETIME ) {
				Date oDate = new Date(((Timestamp)attValue).getTime());
				if( oDate != null ) {
					SimpleDateFormat sdfD = new SimpleDateFormat("dd/MM/yyyy");
					sRetValue = sdfD.format( oDate );
				} else {
					sRetValue = null;
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_NUMBER ) {
				BigDecimal oValue = (BigDecimal)attValue;
				if( oValue != null ) {
					NumberFormat nf = NumberFormat.getInstance();
					boDefAttribute oDefAtt = getBoDefAttribute();
					nf.setMinimumFractionDigits( oDefAtt.getMinDecimals() );
					nf.setMinimumFractionDigits( oDefAtt.getDecimals() );
					nf.setGroupingUsed( "Y".equals( oDefAtt.getGrouping() ) );
					sRetValue = nf.format( oValue );
				}
				else {
					sRetValue = null;
				}
			}
			else {
				Object oValue = attValue;
				if( oValue != null )
					sRetValue = String.valueOf( oValue );
			}
			return sRetValue;
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean getIsLovEditable() {
		return false;
	}

	public boolean getOnChangeSubmit() {
		return false;
	}

	public boolean getRecomended() {
		return false;
	}

	public boolean getRequired() {
		return false;
	}

	public boolean getValid() {
		return false;
	}

	public Object getValue() {
		if ( super.getBoDefAttribute().getMaxOccurs() > 1 ) {
			return this.parent.getDataSet().rows( row ).getObject( 1 );
		}
		else {
			return this.parent.getDataSet().rows( row ).getObject( getBoDefAttribute().getDbName() );
		}
	}
	
	public boolean getVisible() {
		return true;
	}

	public void setValue(Object newValue) {
	}

	public EboContext getEboContext() {
		return boApplication.currentContext().getEboContext();
	}

	@Override
	public String getInvalidMessage() {
		return null;
	}

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
}
