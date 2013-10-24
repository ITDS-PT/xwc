package netgest.bo.xwc.components.classic.renderers;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.localization.XUILocalization;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Renders the GridPanel as XML
 * 
 * @author Pedro Pereira
 *
 */
public class XMLGridPanelRenderer extends XMLBasicRenderer {

    
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
		super.encodeBegin( component );
		GridPanel 		grid = (GridPanel)component;
		
		Column[] columns = grid.getColumns();
		XUIResponseWriter w = getResponseWriter();
		
		String[] sDataFields = grid.getDataColumns();
		
		DataFieldConnector      oDataField;
        int                     oDataFieldType;
        Object                  oDataFieldValue;
        String					sDisplayValue;
        String					sCustomRenderDisplay = "";
		
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
				w.writeAttribute("width", curr.getWidth(), null);
				columnsUsed.add(label);
				w.writeText(label==null?"":label,null);
				w.endElement("gridheadercolumn");
		}
		w.endElement("gridheaderrow");
		
		for ( ; oDataIterator.hasNext(); ) 
		{
			//Returns a line of data from the grid
            DataRecordConnector oDataRecord = oDataIterator.next();
            
            w.startElement("gridrow", null);
            
            for (int i = 0; i < columns.length; i++) 
            { 
            	
            	oDataField = oDataRecord.getAttribute( columns[i].getDataField() );
            	oDataFieldValue = null;
            	boolean hasCustomRender = false;
            	
                if( oDataField != null ) 
                {	
                	if (!columns[i].isHidden()) //Tinha isto antes columnsUsed.contains(oDataField.getLabel())
                	{
	                	if( columnRenderers != null && columnRenderers.containsKey( sDataFields[i] ) ) 
	                	{}
	                	else 
	                	{
	                		Column current = columns[i];
	                		if (current instanceof ColumnAttribute)
	                		{
	                			ColumnAttribute currentCol = (ColumnAttribute) current;
	                			GridColumnRenderer colRenderer = currentCol.getRenderer();
	                			if (colRenderer != null){
	                				sCustomRenderDisplay = colRenderer.render(grid, oDataRecord, oDataField);
	                				hasCustomRender = true;
	                			}
	                		}
	                		
	                		if (oDataFieldValue == null)
	                			oDataFieldValue = oDataField.getValue();
	                		
	                    	w.startElement("gridcolumn", null);
		                    w.writeAttribute("width",current.getWidth(),null);
		                    if( oDataFieldValue != null )
		                    {
		                    	w.writeAttribute("visible", oDataField.getDisabled(), null);
		                    	
		                    	
	                    		if (hasCustomRender)
	                    			sDisplayValue = sCustomRenderDisplay ;
	                    		else
	                    			sDisplayValue = oDataField.getDisplayValue();
	                    		
	                    		if (sDisplayValue != null)
		                    	{
		                    		oDataFieldType = oDataField.getDataType();
			                        switch( oDataFieldType ) 
			                        {
			                            case DataFieldTypes.VALUE_BLOB:
			                                // Don't passe to JavaScript
			                                break;
			                            case DataFieldTypes.VALUE_BRIDGE:
			                            	w.writeAttribute("displayValue",  oDataFieldValue );
			                            	 w.write(sDisplayValue);
			                            	break;
			                            case DataFieldTypes.VALUE_BOOLEAN:
			                            	 w.writeAttribute("displayValue",  oDataFieldValue );
			                            	 w.write(sDisplayValue);
			                                break;
			                            case DataFieldTypes.VALUE_CHAR: 
			                            	w.writeAttribute("displayValue",  oDataFieldValue );
			                            	w.write(sDisplayValue);
			                            	break;
			                            case DataFieldTypes.VALUE_CLOB:
			                            	w.writeAttribute("displayValue",  oDataFieldValue );
			                            	w.write(sDisplayValue);
			                            	break;
			                            case DataFieldTypes.VALUE_NUMBER:
			                            	w.writeAttribute("displayValue", XUILocalization.formatNumber( ((BigDecimal)oDataFieldValue).longValue() ));
			                            	w.write(sDisplayValue);
			                                break;
			                            case DataFieldTypes.VALUE_CURRENCY:
			                            	w.writeAttribute("displayValue",XUILocalization.formatCurrency( ((BigDecimal)oDataFieldValue).longValue() ) );
			                            	w.write(sDisplayValue);
			                                break;    
			                            case DataFieldTypes.VALUE_DATE:
			                            	String date = XUILocalization.formatDate( (Timestamp)oDataFieldValue );
			                            	w.writeAttribute("displayValue",  date );
			                            	w.write(date);
			                                break;
			                            case DataFieldTypes.VALUE_DATETIME:
			                            	String datetime = XUILocalization.formatDateTime( (Timestamp)oDataFieldValue );
			                            	w.writeAttribute("displayValue",  datetime );
			                            	w.write(datetime);
			                            	break;
			                        }
			                   }
		                    }
		                    w.endElement("gridcolumn");
	                	}
	                }
                }
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

