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
		
		Object description = null;
		if (this.oObjectList.getRslt() != null) {
			DataSet dataSet = this.oObjectList.getRslt().getDataSet();
			int col = dataSet.findColumn(name);
			if (col > 0) {
				description = dataSet.rows(row).getObject(col);
			}
		}

		if (isLov( name , description )) {
			try{
				return createLovFieldConnector( name , description );
			} catch (boRuntimeException e){
				logger.warn( "Could not read model definition", e );
			}
		} 
	
		preloadObject();
		
		DataFieldConnector ret = null;
		ret = super.getAttribute(name);
		if (fieldIsNotPartOfObject( ret )) {
			ret = createGenericField( name , description );
		}
		return ret;
	}

	private DataFieldConnector createLovFieldConnector(String name,
			Object description) throws boRuntimeException {
		String attributeName = new LovColumnNameExtractor( name ).extractName();
		boDefAttribute attributeDefinition = this.oObjectList.getBoDef().getAttributeRef( attributeName );
		LovValueGridDisplay displayLovValue = new LovValueGridDisplay( attributeDefinition );
		return displayLovValue.getConnectorForValue( description );
	}

	private boolean isLov(String name, Object description) {
		return LovColumnNameExtractor.isXeoLovColumn(name) && description != null;
	}

	private GenericFieldConnector createGenericField(String name,
			Object description) {
		return new XEOObjectConnector.GenericFieldConnector(name,
						description != null ? String.valueOf(description) : null,
						DataFieldTypes.VALUE_CHAR);
	}

	private boolean fieldIsNotPartOfObject(DataFieldConnector ret) {
		return ret == null || ret instanceof NullFieldConnector;
	}
	
	
	protected void preloadObject() {
		// Force preload...
		try {
			this.oObjectList.getObject();
		} catch (boRuntimeException e) {
			
		}
	}}
