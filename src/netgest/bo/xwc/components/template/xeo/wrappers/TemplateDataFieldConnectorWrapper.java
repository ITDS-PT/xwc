package netgest.bo.xwc.components.template.xeo.wrappers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListRowConnector;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateDataFieldConnectorWrapper implements TemplateHashModel {
	
	private DataFieldConnector field;
	private DataRecordConnector record;
	
	public static String VALUE = "value";
	public static String LOVLABEL = "lovlabel";
	public static String LABEL = "label";
	public static String DISABLED = "disabled";
	public static String REQUIRED = "required";
	public static String VISIBLE = "visible";
	public static String ISLOV = "islov";
	public static String SYSCARDID = "syscardid";
	public static String SYSCARDIDIMG = "syscardidimg";
	public static String FILE = "file";
	
	public TemplateDataFieldConnectorWrapper(DataRecordConnector record,DataFieldConnector field){
		this.record = record;
		this.field = field;
	}
	
	public TemplateModel get(String name){
		if (this.record instanceof XEOObjectListRowConnector ||
				this.record instanceof XEOBridgeRecordConnector ||
				this.record instanceof XEOObjectConnector) {	
			if (name.toLowerCase().equals(VALUE) || name.toLowerCase().equals(LOVLABEL))
				return getValueForXEOObjects(name);
			else if (name.toLowerCase().equals(LABEL))
				return new SimpleScalar(field.getLabel());
			else if (name.toLowerCase().equals(DISABLED))
				return field.getDisabled()?TemplateBooleanModel.TRUE:TemplateBooleanModel.FALSE;
			else if (name.toLowerCase().equals(REQUIRED))
				return field.getRequired()?TemplateBooleanModel.TRUE:TemplateBooleanModel.FALSE;
			else if (name.toLowerCase().equals(VISIBLE))
				return field.getVisible()?TemplateBooleanModel.TRUE:TemplateBooleanModel.FALSE;
			else if (name.toLowerCase().equals(ISLOV))
				return field.getIsLov()?TemplateBooleanModel.TRUE:TemplateBooleanModel.FALSE;
			else if (name.toLowerCase().equals(SYSCARDID) ||
					name.toLowerCase().equals(SYSCARDIDIMG))
				return getCardId(name);
			else if (name.toLowerCase().equals(FILE))
				return getFileLink(name);
			else
				return null;
		}
		else {
			if (name.toLowerCase().equals(VALUE))
				return getValueForFields(name);
			else if (name.toLowerCase().equals(LABEL))
				return new SimpleScalar(field.getLabel());
		}
		return null;
	}
	
	private TemplateModel getFileLink(String name) {
		XEOObjectAttributeConnector attconnector = (XEOObjectAttributeConnector)field;
		if (attconnector.getBoDefAttribute().getAtributeDeclaredType()
				==boDefAttribute.ATTRIBUTE_BINARYDATA)
		{
			XEOObjectConnector objectconnector = (XEOObjectConnector)record;
			String link="file/";
			
	/*		if (!StringUtils.isEmpty(PortalApplication
					.currentPortalContext().getCurrentPortal().getId()))
				link+=PortalApplication.currentPortalContext().getCurrentPortal().getId()+"/"; */
			
			link+=objectconnector.getXEOObject().getName()+"/";
			link+=objectconnector.getXEOObject().getBoui()+"/";
			link+=attconnector.getAttributeHandler().getName()+"/";

			Object value=field.getValue();
			if (value==null)
				return new SimpleScalar("");
			else {
				String file=(String)value;
				link+=file.substring(file.lastIndexOf("/")+1,file.length());
			}
			
			return new SimpleScalar(link);
		}
		else
			return null;
	}
	
	private TemplateModel getCardId(String name) {
		XEOObjectAttributeConnector attconnector = (XEOObjectAttributeConnector)field;
		if (attconnector.getBoDefAttribute().getAtributeDeclaredType()
				==boDefAttribute.ATTRIBUTE_OBJECT)
		{
			Object value=field.getValue();
			if (value==null)
				return new SimpleScalar("");
			BigDecimal boui = (BigDecimal)value;
			XEOObjectConnector xeoobject = new XEOObjectConnector(boui.longValue(), 0);			
			try {
				if (name.toLowerCase().equals(SYSCARDIDIMG))
					return new SimpleScalar(xeoobject.getXEOObject().getCARDID().toString());
				else
					return new SimpleScalar(xeoobject.getXEOObject().getCARDIDwNoIMG().toString());
			} catch (boRuntimeException e) {
				return null;
			}
		}
		else
			return null;
	}
	
	
	private TemplateModel getValueForFields(String name) {		
		Object value = field.getValue();	
			
		if (field.getDataType() == DataFieldTypes.VALUE_DATETIME) {
			Timestamp timestampValue=(Timestamp)value;
			return new SimpleDate(timestampValue);
		}
		else if (field.getDataType() == DataFieldTypes.VALUE_DATE) {
			java.sql.Date dateValue=(java.sql.Date)value;
			return new SimpleDate(dateValue);
		}
		else if (field.getDataType() == DataFieldTypes.VALUE_NUMBER) {
			return getValueForNumber(value);
		}
		else if (field.getDataType() == DataFieldTypes.VALUE_CHAR || 
				field.getDataType()==DataFieldTypes.VALUE_CLOB) {
			String valueString = (String)field.getValue();
			return new SimpleScalar(valueString);
		}
		else
			return new SimpleScalar(value.toString());		
	}
	
	
	private TemplateModel getValueForXEOObjects(String name) {		
		if (field!=null) {
			Object value = field.getValue();
			
			//Testar
			if (field.getIsLov() && name.equals(LOVLABEL)) {
				String valueLov=field.getLovMap().get(value.toString());
				return new SimpleScalar(valueLov);
			}
			
			if (field.getDataType() == DataFieldTypes.VALUE_DATETIME) {
				Timestamp timestampValue=(Timestamp)value;
				return new SimpleDate(timestampValue);
			}
			else if (field.getDataType() == DataFieldTypes.VALUE_DATE) {
				Date dateValue=(Date)value;
				return new SimpleDate(new java.sql.Date(dateValue.getTime()));
			}
			else if (field.getDataType() == DataFieldTypes.VALUE_NUMBER) {
				//Test if is a XEOObject relation
				//Workaround since the connector treats ATTRIBUTE_OBJECT as NUMBER
				XEOObjectAttributeConnector attconnector = (XEOObjectAttributeConnector)field;
				
				if (attconnector.getBoDefAttribute().getAtributeDeclaredType()
						==boDefAttribute.ATTRIBUTE_OBJECT)
				{
					if (value==null)
						return new SimpleScalar("");
					BigDecimal boui = (BigDecimal)value;
					XEOObjectConnector xeoobject = new XEOObjectConnector(boui.longValue(), 0);
					return new TemplateDataRecordConnectorWrapper(xeoobject);
				}
				else
					return getValueForNumber(value);
			}
			else if (field.getDataType() == DataFieldTypes.VALUE_BRIDGE) {
				return new TemplateListWrapper(field.getDataList().iterator());
			}
			else
				if (value!=null)
					return new SimpleScalar(value.toString());
				else
					return new SimpleScalar("");
		}
		else
			return null;
	}

	private TemplateModel getValueForNumber(Object value) {
		if (value instanceof Byte) {
			Byte numericValue=(Byte)value;
			return new SimpleNumber(numericValue.byteValue());
		}
		else if (value instanceof Short) {
			Short numericValue=(Short)value;
			return new SimpleNumber(numericValue.shortValue());
		}
		else if (value instanceof Integer) {
			Integer numericValue=(Integer)value;
			return new SimpleNumber(numericValue.intValue());
		}
		else if (value instanceof Long) {
			Long numericValue=(Long)value;
			return new SimpleNumber(numericValue.longValue());
		}
		else if (value instanceof Float) {
			Float numericValue=(Float)value;
			return new SimpleNumber(numericValue.floatValue());
		}
		else if (value instanceof Double) {
			Double numericValue=(Double)value;
			return new SimpleNumber(numericValue.doubleValue());
		}
		else if (value instanceof BigDecimal) {
			BigDecimal numericValue=(BigDecimal)value;
			return new SimpleNumber(numericValue.longValue());
		}
		else if (value instanceof BigInteger) {
			BigInteger numericValue=(BigInteger)value;
			return new SimpleNumber(numericValue.longValue());
		}
		else return new SimpleScalar(value.toString()); 

	}
	
	@Override
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	public DataFieldConnector getField() {
		return field;
	}

	public void setField(DataFieldConnector field) {
		this.field = field;
	}

	
}
