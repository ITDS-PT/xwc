package netgest.bo.xwc.xeo.beans;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.localization.XUILocalizationUtils;
import netgest.bo.xwc.xeo.localization.BeansMessages;

public class XEOEditPropertiesBean extends XEOEditBean {

	
	/**
	 * 
	 * Retrieves the dependencies of the current object 
	 * 
	 * @return An HTML Table with the list of dependencies
	 * 
	 * @throws boRuntimeException 
	 */
	public String getDependencies() throws boRuntimeException
	{
		
		final long START_RANGE = 0;
		
		final long END_RANGE = 200; 
		    
		boObject currObject = getXEOObject();
		boObject[] dependenciesArray = currObject.getReferencedByObjects(START_RANGE,END_RANGE);
		
		StringWriter writer = new StringWriter();
		XUIResponseWriter w = new XUIResponseWriter(writer, "text/html", "UTF-8");
		if (dependenciesArray.length > 0 )
		{
			try 
			{
				w.startDocument();
				w.startElement(HTMLTag.TABLE, null);
					w.writeAttribute(HTMLAttr.CLASS, "relations", null);
				w.startElement(HTMLTag.TR, null);
					w.startElement(HTMLTag.TH, null);
						w.write(BeansMessages.OBJECT_TYPE.toString());
					w.endElement(HTMLTag.TH);
					w.startElement(HTMLTag.TH, null);
						w.write(BeansMessages.REFERENCED_OBJECT.toString());
					w.endElement(HTMLTag.TH);
				w.endElement(HTMLTag.TR);
				
				for (boObject dependency : dependenciesArray )
				{
					w.startElement(HTMLTag.TR, null);
						w.startElement(HTMLTag.TD, null);
							w.write(dependency.getName());
						w.endElement(HTMLTag.TD);
						w.startElement(HTMLTag.TD, null);
							w.write(dependency.getTextCARDID().toString());
						w.endElement(HTMLTag.TD);
					w.endElement(HTMLTag.TR);
				}
				
				w.endElement(HTMLTag.TABLE);
				w.endDocument();
				return writer.toString();
				
				
			} 
			catch (IOException e) 
			{
				throw new boRuntimeException("XEOEditPropertiesBean.getDependencies", "", e);
			}
		}
		else
			return BeansMessages.NO_REFERENCED_BY_OBJECT.toString();
		
	}
	
	public String getDependents() throws boRuntimeException
	{
		boObject currObject = getXEOObject();
		
		final long START_RANGE = 0;
		
		final long END_RANGE = 200; 
		
		boObject[] dependentsArray = currObject.getReferencesObjects(START_RANGE,END_RANGE);
		
		StringWriter writer = new StringWriter();
		XUIResponseWriter w = new XUIResponseWriter(writer, "text/html", "UTF-8");
		if ( dependentsArray.length > 0)
		{
			try 
			{
				w.startDocument();
				w.startElement(HTMLTag.TABLE, null);
				w.writeAttribute(HTMLAttr.CLASS, "relations", null);
				w.startElement(HTMLTag.TR, null);
					w.startElement(HTMLTag.TH, null);
						w.write(BeansMessages.OBJECT_TYPE.toString());
					w.endElement(HTMLTag.TH);
					w.startElement(HTMLTag.TH, null);
						w.write(BeansMessages.REFERENCED_OBJECT.toString());
					w.endElement(HTMLTag.TH);
				w.endElement(HTMLTag.TR);
				
				for (boObject dependent : dependentsArray )
				{
					w.startElement(HTMLTag.TR, null);
						w.startElement(HTMLTag.TD, null);
							w.write(dependent.getName());
						w.endElement(HTMLTag.TD);
						w.startElement(HTMLTag.TD, null);
							w.write(dependent.getTextCARDID().toString());
						w.endElement(HTMLTag.TD);
					w.endElement(HTMLTag.TR);
				}
				w.endElement(HTMLTag.TABLE);
				w.endDocument();
				return writer.toString();
				
				
			} 
			catch (IOException e) 
			{
				throw new boRuntimeException("XEOEditPropertiesBean.getDependents", "", e);
			}
		}
		else
			return BeansMessages.NO_REFERENCED_OBJECTS.toString();
		
	}
	
	
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
			if( user != null && !"0".equals( user ) ) {
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
	
	@Override
	public void cancel() throws boRuntimeException {
	}
	
}
