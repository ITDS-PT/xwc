package netgest.bo.xwc.components.connectors;

import org.apache.commons.lang.StringUtils;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovColumnNameExtractor;

public class XEOObjectListRowConnector extends XEOObjectConnector {

	int row;
	boObjectList oObjectList;

	public XEOObjectListRowConnector(long boui, boObjectList oObjectList,
			int row) {
		super(boui, row);
		this.row = row;
		this.oObjectList = oObjectList;
	}

	@Override
	public DataFieldConnector getAttribute(String name) {
		DataFieldConnector ret = null;
		if (this.oObjectList.getRslt() != null) {
			DataSet dataSet = this.oObjectList.getRslt().getDataSet();

			int col = dataSet.findColumn(name);
			if (col > 0) {
				Object value = dataSet.rows(row).getObject(col);

				if (LovColumnNameExtractor.isXeoLovColumn(name)) {
					LovColumnNameExtractor lovExtractor = new LovColumnNameExtractor(
							name);
					String attname = lovExtractor.extractName();
					String lovValue = getLovValue(attname, value);
					if (lovValue != null) {
						ret = new XEOObjectConnector.GenericFieldConnector(
								name, lovValue, DataFieldTypes.VALUE_CHAR,
								value != null ? String.valueOf(value) : null);
					} else {
						ret = new XEOObjectConnector.GenericFieldConnector(
								name, value != null ? String.valueOf(value)
										: null, DataFieldTypes.VALUE_CHAR);
					}

				} else
					ret = new XEOObjectConnector.GenericFieldConnector(name,
							value != null ? String.valueOf(value) : null,
							DataFieldTypes.VALUE_CHAR);
			}
		}
		if (ret == null) {
			ret = super.getAttribute(name);
		}
		return ret;
	}

	private String getLovValue(String name,Object value) {
		String toRet=null;
		try {
			String lovName=null;
			boDefAttribute defatt=this.oObjectList.getBoDef().getAttributeRef(name);
			if (defatt!=null && !StringUtils.isEmpty(defatt.getLOVName()))
				lovName=this.oObjectList.getBoDef().getAttributeRef(name).getLOVName();
			
			if (value!=null && lovName!=null) {
				lovObject lovObj = LovManager.getLovObject(this.oObjectList.getEboContext(), lovName);
				lovObj.beforeFirst();
				boolean found=lovObj.findLovItemByDescription(String.valueOf( value ));
				
				if (found) {							
					toRet=lovObj.getCode();
				}
			}
		} catch (boRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toRet;
	}

}
