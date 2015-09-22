package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.localization.XUILocalization;

/**
 * 
 * Renders the GridPanel as XML
 * 
 * @author Pedro Pereira
 *
 */
public class XMLGridPanelRenderer extends XUIRenderer {

    
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
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

		w.startElement( component.getRendererType() );
		
		
		w.startElement("gridheaderrow");
		for ( int k = 0; k < columns.length ; k++){
			Column curr = columns[k];
			if (!curr.isHidden()){
				w.startElement("gridheadercolumn");
					String label = GridPanel.getColumnLabel( grid.getDataSource(), curr );
					w.writeAttribute("width", curr.getWidth());
					columnsUsed.add(label);
					w.writeAttribute("displayValue", label==null?"": label);
					w.endElement("gridheadercolumn");
			}
		}
		w.endElement("gridheaderrow");
		
		for ( ; oDataIterator.hasNext(); ) 
		{
			//Returns a line of data from the grid
            DataRecordConnector oDataRecord = oDataIterator.next();
            
            w.startElement("gridrow");
            
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
	                		
	                    	w.startElement("gridcolumn");
		                    w.writeAttribute("width",current.getWidth());
		                    if( oDataFieldValue != null ){
		                    	w.writeAttribute("visible", oDataField.getDisabled());
		                    	
		                    	if (hasCustomRender)
	                    			sDisplayValue = sCustomRenderDisplay ;
	                    		else
	                    			sDisplayValue = oDataField.getDisplayValue();
	                    		
	                    		if (sDisplayValue != null){
	                    			//Prevent Illegal entities in 
		                    		oDataFieldType = oDataField.getDataType();
		                    		switch( oDataFieldType ){
			                            case DataFieldTypes.VALUE_BLOB:
			                                // Don't passe to JavaScript
			                                break;
			                            case DataFieldTypes.VALUE_BRIDGE:
			                            	w.writeCDATA(sDisplayValue);
			                            	break;
			                            case DataFieldTypes.VALUE_BOOLEAN:
			                            	w.writeAttribute("displayValue",sDisplayValue);
			                                break;
			                            case DataFieldTypes.VALUE_CHAR: 
			                            	//No cardId's on XML
			                            	if (sDisplayValue.indexOf("<img style='cursor:hand' hspace='3' border='0' align='absmiddle'")>-1) {
			                            		sDisplayValue=sDisplayValue.replaceAll("\\<.*?>","");
			                            	}
			                            	w.writeCDATA(JavaScriptUtils.safeJavaScriptWrite( sDisplayValue ) );
			                            	break;
			                            case DataFieldTypes.VALUE_CLOB:
			                            	w.writeCDATA(JavaScriptUtils.safeJavaScriptWrite( sDisplayValue ) );
			                            	break;
			                            case DataFieldTypes.VALUE_NUMBER:
			                            	byte renderType = oDataField.getInputRenderType();
			                            	
			                            	if (DataFieldTypes.RENDER_OBJECT_LOOKUP == renderType){
			                            		w.writeAttribute("displayValue", JavaScriptUtils.safeJavaScriptWrite( sDisplayValue ) );
			                            	} else if (DataFieldTypes.RENDER_LOV == renderType || oDataField.getIsLov()){
			                            		w.writeAttribute("displayValue", JavaScriptUtils.safeJavaScriptWrite( sDisplayValue ) );
			                            	} else {
			                            		w.writeAttribute("displayValue",XUILocalization.formatNumber( ((BigDecimal)oDataFieldValue).longValue() ));
			                            		
			                            	}
			                                break;
			                            case DataFieldTypes.VALUE_CURRENCY:
			                            	w.writeAttribute("displayValue",XUILocalization.formatCurrency( ((BigDecimal)oDataFieldValue).longValue() ));
			                                break;    
			                            case DataFieldTypes.VALUE_DATE:
			                            	String date = XUILocalization.formatDateDefaultTimeZone( (Timestamp)oDataFieldValue );
			                            	w.writeAttribute("displayValue",date);
			                                break;
			                            case DataFieldTypes.VALUE_DATETIME:
			                            	String datetime = XUILocalization.formatDateTime( (Timestamp)oDataFieldValue );
			                            	w.writeAttribute("displayValue",datetime);
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
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}
	
	
}

