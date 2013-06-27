package netgest.bo.xwc.components.connectors;



import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovColumnNameExtractor;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovValueGridDisplay;

public class XEOObjectListRowConnector extends XEOObjectConnector {

	private static final Logger logger = Logger.getLogger( XEOObjectListRowConnector.class );
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
		preloadObject();
		DataFieldConnector ret = null;
		if (this.oObjectList.getRslt() != null) {
			DataSet dataSet = this.oObjectList.getRslt().getDataSet();

			int col = dataSet.findColumn(name);
			if (col > 0) {
				Object description = dataSet.rows(row).getObject(col);

				if (LovColumnNameExtractor.isXeoLovColumn(name)) {
					try{
						String attributeName = new LovColumnNameExtractor( name ).extractName();
						boDefAttribute attributeDefinition = this.oObjectList.getBoDef().getAttributeRef( attributeName );
						LovValueGridDisplay displayLovValue = new LovValueGridDisplay( attributeDefinition );
						return displayLovValue.getConnectorForValue( description );
					} catch (boRuntimeException e){
						logger.warn( "Could not read model definition", e );
					}
				} else
					ret = new XEOObjectConnector.GenericFieldConnector(name,
							description != null ? String.valueOf(description) : null,
							DataFieldTypes.VALUE_CHAR);
			}
		}
		if (ret == null) {
			ret = super.getAttribute(name);
		}
		return ret;
	}
	
	
	protected void preloadObject() {
		// Force preload...
		try {
			this.oObjectList.getObject();
		} catch (boRuntimeException e) {
			
		}
	}}
