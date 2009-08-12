package netgest.bo.xwc.xeo.beans;

import java.util.Date;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.framework.localization.XUILocalizationUtils;

public class XEOEditPropertiesBean extends XEOBaseBean {

	public String getCardId() throws boRuntimeException {
		return getXEOObject().getCARDID().toString();
	}

	public String getCreatedBy() throws boRuntimeException {
		boObject creator = getXEOObject().getAttribute("CREATOR").getObject();
		if( creator != null ) {
			return creator.getCARDID().toString();
		}
		return "n/a";
	}
	
	public String getCreationDate() throws boRuntimeException {
		Date date = getXEOObject().getAttribute("SYS_DTCREATE").getValueDate();
		if( date != null ) {
			return XUILocalizationUtils.dateTimeToString( date );
		}
		return "";
	}
	
	public String getLastModificationDate() throws boRuntimeException {
		Date date = getXEOObject().getAttribute("SYS_DTSAVE").getValueDate();
		if( date != null ) {
			return XUILocalizationUtils.dateTimeToString( date );
		}
		return "";
	}

	public String getLastModificationBy() throws boRuntimeException {
		if( getXEOObject().getDataSet().findColumn( "SYS_USER" ) > 0 ) {
			String user = getXEOObject().getDataRow().getString("SYS_USER");
			if( user != null ) {
				return boObject.getBoManager().loadObject(  getEboContext(), Long.parseLong( user )  ).getCARDID().toString();
			}
		}
		return "";
	}
	
	public String getVersion() {
		if( getXEOObject().getDataSet().findColumn( "SYS_ICN" ) > 0 ) {
			long sys_icn = getXEOObject().getDataRow().getLong("SYS_ICN");
			return Long.toString( sys_icn );
		}
		return "0";
		
	}
	
	public String getBoui() {
		return Long.toString( getXEOObject().getBoui() );
	}
	
	@Override
	public boolean getIsChanged() {
		return false;
	}
	
}
