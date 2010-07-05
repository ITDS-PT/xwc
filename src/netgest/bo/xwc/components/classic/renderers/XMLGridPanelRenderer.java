package netgest.bo.xwc.components.classic.renderers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Renders the GridPanel as XML
 * 
 * @author Pedro Pereira
 *
 */
public class XMLGridPanelRenderer extends XMLBasicRenderer {

	/**
	 * Syntactic format for Date instances
	 */
	private static final SimpleDateFormat oDateFormater = new SimpleDateFormat( "dd/MM/yyyy" );
    /**
     * Syntactic format for DateTime instances
     */
    private static final SimpleDateFormat oDateTimeFormater = new SimpleDateFormat( "dd/MM/yyyy hh:MM:ss" );
	
    
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
		super.encodeBegin( component );
		System.out.println("Entrei");
		GridPanel 		grid = (GridPanel)component;
		
		Column[] columns = grid.getColumns();
		XUIResponseWriter w = getResponseWriter();
		
		String[] sDataFields = grid.getDataColumns();
		
		DataFieldConnector      oDataField;
        int                     oDataFieldType;
        Object                  oDataFieldValue;
        String					sDisplayValue;
		
		Iterator<DataRecordConnector> oDataIterator = grid.getDataSource().iterator();
		Map<String,GridColumnRenderer> columnRenderers = null;
		HashSet<String> columnsUsed = new HashSet<String>();

		w.startElement("gridheaderrow", null);
		for ( int k = 0; k < columns.length ; k++)
		{
			Column curr = columns[k];
			w.startElement("gridheadercolumn", null);
				String label = GridPanel.getColumnLabel( grid.getDataSource(), curr );
				w.writeAttribute("datafield", curr.getDataField(), null);
			columnsUsed.add(label);
				w.write(label);
			w.endElement("gridheadercolumn");
		}
		w.endElement("gridheaderrow");
		
		for ( ; oDataIterator.hasNext(); ) 
		{
			//Returns a line of data from the grid
            DataRecordConnector oDataRecord = oDataIterator.next();
            
            w.startElement("gridrow", null);
            
            for (int i = 0; i < sDataFields.length; i++) 
            { 
            	oDataField = oDataRecord.getAttribute( sDataFields[i] );
            	
                w.startElement("gridcolumn", null);
                
                if( oDataField != null ) 
                {	
                	//FIXME: Tem de haver uma maneira de só mostrar as colunas que estão presentes no column
                	if (true) //Tinha isto antes columnsUsed.contains(oDataField.getLabel())
                	{
	                	if( columnRenderers != null && columnRenderers.containsKey( sDataFields[i] ) ) 
	                	{
	                		/*oDataFieldValue = columnRenderers.get( sDataFields[i] ).render(  grid, oDataRecord, oDataField );
	                        oStringBuilder.append( '\'' );
	                        JavaScriptUtils.safeJavaScriptWrite( 
	                            oStringBuilder, 
	                            ((String)oDataFieldValue),
	                            '\''
	                        );
	                        oStringBuilder.append( '\'' );*/
	                	}
	                	else 
	                	{
		                    oDataFieldValue = oDataField.getValue();
		                    if( oDataFieldValue != null )
		                    {
		                    	w.writeAttribute("visible", oDataField.getDisabled(), null);
		                    	
		                    	sDisplayValue = oDataField.getDisplayValue(); 
		                    	if( sDisplayValue != null ) 
		                    	{
		                            w.writeAttribute("displayValue", sDisplayValue, null);
		                            w.write(sDisplayValue);
		                        }
		                    	else 
		                    	{
			                        oDataFieldType = oDataField.getDataType();
			                        switch( oDataFieldType ) 
			                        {
			                            case DataFieldTypes.VALUE_BLOB:
			                                // Don't passe to JavaScript
			                                break;
			                            case DataFieldTypes.VALUE_BOOLEAN:
			                            	 w.writeAttribute("displayValue",  oDataFieldValue , null);
			                            	 w.write(sDisplayValue);
			                                break;
			                            case DataFieldTypes.VALUE_CHAR:
			                            case DataFieldTypes.VALUE_CLOB:
			                            case DataFieldTypes.VALUE_NUMBER:
			                            	w.writeAttribute("displayValue",((BigDecimal)oDataFieldValue).toString(),null );
			                            	w.write(sDisplayValue);
			                                break;
			                            case DataFieldTypes.VALUE_DATE:
			                            	w.writeAttribute("displayValue",  oDateFormater.format((Timestamp)oDataFieldValue) , null);
			                            	w.write(oDateFormater.format((Timestamp)oDataFieldValue));
			                                break;
			                            case DataFieldTypes.VALUE_DATETIME:
			                            	w.writeAttribute("displayValue",  oDateTimeFormater.format((Timestamp)oDataFieldValue) , null);
			                            	w.write(oDateTimeFormater.format((Timestamp)oDataFieldValue));
			                            	break;
			                        }
			                   }
		                    }
	                	}
	                }
                }
                w.endElement("gridcolumn");
            }
            w.endElement("gridrow");
		}
		
		
	}
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException 
	{
		super.encodeEnd(component);
	}
	
	
}
