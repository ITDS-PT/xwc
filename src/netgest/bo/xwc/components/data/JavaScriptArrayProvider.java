package netgest.bo.xwc.components.data;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.classic.grid.utils.DataFieldDecoder;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.localization.XUILocalization;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JavaScriptArrayProvider {
    
    private static final int INITIAL__BUFFER_SIZE = 150;
    
    private static final char[] EMPTY_FIELD = "".toCharArray();
    
    private Iterator<DataRecordConnector> oDataIterator;
    private String[]          sDataFields;
    
    private int iStart;
    private int iLimit;
    
    public JavaScriptArrayProvider( Iterator<DataRecordConnector> oDataIterator, String[] dataFields, int start, int limit ) {

        assert oDataIterator != null : MessageLocalizer.getMessage("DATA_CONNECTOR_CANNOT_BE_NULL");
        assert dataFields != null : MessageLocalizer.getMessage("DATA_FIELDS_MUST_BE_SPECIFIED");
        
        this.iStart = start;
        this.iLimit = limit;
        
        this.oDataIterator = oDataIterator;
        this.sDataFields = dataFields;
        
    }

    public final StringBuilder getJSArray() 
    {
        StringBuilder oScriptBuilder;
        
        oScriptBuilder = new StringBuilder( INITIAL__BUFFER_SIZE );
        
        getJSArray( oScriptBuilder );
        
        return oScriptBuilder;

    }

    public final StringBuilder getJSONArray() 
    {
        StringBuilder oScriptBuilder;
        
        oScriptBuilder = new StringBuilder( 150 );
        
        getJSONArray( oScriptBuilder, null,null, null );
        
        return oScriptBuilder;

    }
    
    public final void getJSONArray( StringBuilder oStringBuilder, GridPanel grid,String keyField, String[] selectedRows ) {
    	getJSONArray( oStringBuilder, grid, keyField, selectedRows, grid.getRowClass(), null );
    }

    public final void getJSONArray( StringBuilder oStringBuilder, GridPanel grid,String keyField, String[] selectedRows, GridRowRenderClass rowClass, Map<String,GridColumnRenderer> columnRenderers ) 
    {
        DataFieldConnector      oDataField;
        int                     oDataFieldType;
        Object                  oDataFieldValue;
        boolean                 bFirstRow;
        int                     iCntr;
        String					sDisplayValue;
        
        List<String>      selRows = new ArrayList<String>();
        
        if( selectedRows != null ) {
        	selRows = Arrays.asList( selectedRows );
        }
        
        bFirstRow = true;
         
        //DataListIterator oDataIterator = oDataListConnector.iterator();
        
        
        //if( this.iStart > 0 ) {
        //    oDataIterator.skip( this.iStart );
        //}
        
        for (int i = 0; oDataIterator.hasNext() && i < this.iStart; i++) {
            oDataIterator.next();
        }
        

        iCntr = 0;
        
        String selRowNums = "";
        
        oStringBuilder.append( '[' );
        for ( ; oDataIterator.hasNext(); ) {
            DataRecordConnector oDataRecord = oDataIterator.next();
            
            if( !bFirstRow ) {
                oStringBuilder.append( ',' );
            }
            oStringBuilder.append( '{' );
            
            if( rowClass != null ) {
            	oStringBuilder.append( "\"__rc\":\"" );
    			JavaScriptUtils.safeJavaScriptWrite( oStringBuilder, rowClass.getRowClass( grid, oDataRecord ),'\"');
                oStringBuilder.append( "\"," );
        	}
        
            for (int i = 0; i < sDataFields.length; i++) { 
                
                if( i > 0 )
                    oStringBuilder.append( ',' );
                

                oStringBuilder.append("\"").append( DataFieldDecoder.convertForGridPanel( sDataFields[i] ) ).append("\"");	
                oStringBuilder.append(':');

                oDataField = oDataRecord.getAttribute( sDataFields[i] );
                
                if( oDataField != null ) {
                	
                	try {
	                	if( columnRenderers != null && columnRenderers.containsKey( sDataFields[i] ) ) {
	                		oDataFieldValue = columnRenderers.get( sDataFields[i] ).render(  grid, oDataRecord, oDataField );
	                        oStringBuilder.append( "\"" );
	                        JavaScriptUtils.safeJavaScriptWrite( 
	                            oStringBuilder, 
	                            ((String)oDataFieldValue),
	                            '\"'
	                        );
	                        oStringBuilder.append( "\"" );
	                	}
	                	else {
	                		
	                		sDisplayValue = null;
	                		
	                		oDataFieldValue = oDataField.getValue();
	                		
	                		Column c = grid.getColumn( sDataFields[i] );
	                		if( c != null )
	                			sDisplayValue = c.applyRenderTemplate( oDataFieldValue );
	                        
		                    if( sDisplayValue != null ) {
	                            oStringBuilder.append( "\"" );
	                            JavaScriptUtils.safeJavaScriptWrite( 
	                                oStringBuilder, 
	                                sDisplayValue,
	                                '\"'
	                            );
	                            oStringBuilder.append( "\"" );
		                    }
		                    else if ( oDataFieldValue != null )
		                    {
		                    	
		                        if( sDataFields[i].equals( keyField ) ) {
		                        	if( selRows.indexOf( String.valueOf( oDataFieldValue ) ) > -1 ) {
		                        		selRowNums += iCntr + "|";
		                        	}
		                        }
		                        
		                    	sDisplayValue = oDataField.getDisplayValue(); 
		                    	if( sDisplayValue != null ) {
		                            oStringBuilder.append( "\"" );
		                            JavaScriptUtils.safeJavaScriptWrite( oStringBuilder, sDisplayValue, '\"' );
		                            oStringBuilder.append( "\"" );
		                    	}
		                    	else {
			                        oDataFieldType = oDataField.getDataType();
			                        switch( oDataFieldType ) {
			                            case DataFieldTypes.VALUE_BLOB:
			                                // Don't passe to JavaScript
			                                break;
			                        
			                            case DataFieldTypes.VALUE_BOOLEAN:
			                                oStringBuilder.append( oDataFieldValue );
			                                break;
			                        
			                            case DataFieldTypes.VALUE_NUMBER:
			                                oStringBuilder.append( XUILocalization.formatNumber( ((BigDecimal)oDataFieldValue).longValue()) );
			                                break;
			                            case DataFieldTypes.VALUE_CURRENCY:
			                                oStringBuilder.append( XUILocalization.formatCurrency( ((BigDecimal)oDataFieldValue).longValue()) );
			                                break;    
			                            case DataFieldTypes.VALUE_DATE:
			                                oStringBuilder.append("\"");
			                                oStringBuilder.append(XUILocalization.formatDate( (Timestamp)oDataFieldValue ));
			                                oStringBuilder.append("\"");
			                                break;
			                            case DataFieldTypes.VALUE_DATETIME:
			                                oStringBuilder.append("\"");
			                                oStringBuilder.append( XUILocalization.formatDateTime( (Timestamp)oDataFieldValue ));
			                                oStringBuilder.append("\"");
			                                break;
			                            case DataFieldTypes.VALUE_CHAR:
			                            case DataFieldTypes.VALUE_CLOB:
			                            default:
			                                oStringBuilder.append( "\"" );
			                                JavaScriptUtils.safeJavaScriptWrite( 
			                                    oStringBuilder, 
			                                    (oDataFieldValue.toString()),
			                                    '\"'
			                                );
			                                oStringBuilder.append( "\"" );
			                                break;
			                               
			                        }
		                    	}
		                    }
		                    else {
		                    	oStringBuilder.append( "\"" );
		                        //oStringBuilder.append( EMPTY_FIELD );
		                        oStringBuilder.append( "\"" );
		                    }
	                    }
                	}
                	catch( Exception e ) {
                		e.printStackTrace();
                    	oStringBuilder.append("\"");
                        oStringBuilder.append( JavaScriptUtils.safeJavaScriptWrite( e.getMessage(), '\"') );
                    	oStringBuilder.append("\"");
                	}
                }
                else {
                	oStringBuilder.append("\"");
                    oStringBuilder.append( JavaScriptUtils.safeJavaScriptWrite( ComponentMessages.GRID_INVALID_COLUMN.toString( sDataFields[i] ), '\"') );
                	oStringBuilder.append("\"");
                    //oStringBuilder.append( EMPTY_FIELD );
                }
            }
            bFirstRow = false;

            oStringBuilder.append( ',' );
            oStringBuilder.append( "\"__sRows\":" );	
            oStringBuilder.append("\"");
            oStringBuilder.append( selRowNums );
            oStringBuilder.append("\"");
            
            
            oStringBuilder.append( '}' );
    
            iCntr++;
            if( iCntr >= this.iLimit ) {
                break;
            }
            oStringBuilder.append("\n");
        }
        oStringBuilder.append( ']' );
    }
    

    public final void getJSArray( StringBuilder oStringBuilder ) 
    {
        DataFieldConnector      oDataField;
        int                     oDataFieldType;
        Object                  oDataFieldValue;
        boolean                 bFirstRow;
        int                     iCntr;
        
        
        bFirstRow = true;
         
        
        //Iterator<DataRecordConnector> oDataIterator = oDataListConnector.iterator();
        
        for (int i = 0; i < this.iStart; i++) {
            oDataIterator.next();
        }

        iCntr = 0;
        
        oStringBuilder.append( '[' );
        for ( ; oDataIterator.hasNext(); ) {
            DataRecordConnector oDataRecord = oDataIterator.next();
            
            if( !bFirstRow ) {
                oStringBuilder.append( ',' );
            }
            oStringBuilder.append( '[' );
            
            for (int i = 0; i < sDataFields.length; i++) {
                oDataField = oDataRecord.getAttribute( sDataFields[i] );
                
                if( i > 0 )
                    oStringBuilder.append( ',' );
                
                if( oDataField != null ) {
                    
                    oDataFieldValue = oDataField.getValue();
                    if( oDataFieldValue != null )
                    {
                        oDataFieldType = oDataField.getDataType();
                        switch( oDataFieldType ) {
                            case DataFieldTypes.VALUE_BLOB:
                                // Don't passe to JavaScript
                                break;
                        
                            case DataFieldTypes.VALUE_BOOLEAN:
                                oStringBuilder.append( oDataFieldValue );
                                break;
                        
                            case DataFieldTypes.VALUE_CHAR:
                            case DataFieldTypes.VALUE_CLOB:
                                oStringBuilder.append( "\"" );
                                JavaScriptUtils.safeJavaScriptWrite( 
                                    oStringBuilder, 
                                    ((String)oDataFieldValue),
                                    '\"'
                                );
                                oStringBuilder.append( "\"" );
                                break;
                            case DataFieldTypes.VALUE_NUMBER:
                                oStringBuilder.append( XUILocalization.formatNumber( ((BigDecimal)oDataFieldValue).longValue()) );
                                break;
                            case DataFieldTypes.VALUE_CURRENCY:
                                oStringBuilder.append( XUILocalization.formatCurrency( ((BigDecimal)oDataFieldValue).longValue()) );
                                break;    
                            case DataFieldTypes.VALUE_DATE:
                            	oStringBuilder.append(XUILocalization.formatDate( (Timestamp)oDataFieldValue ));
                                break;
                            case DataFieldTypes.VALUE_DATETIME:
                            	oStringBuilder.append( XUILocalization.formatDateTime( (Timestamp)oDataFieldValue ));
                                break;
                        }
                    }
                    else {
                        oStringBuilder.append( EMPTY_FIELD );
                    }
                }
                else {
                    oStringBuilder.append( EMPTY_FIELD );
                }
            }
            bFirstRow = false;
            
            oStringBuilder.append( ']' );
 
            iCntr++;
            if( iCntr > this.iLimit ) {
                break;
            }
            
        }
        oStringBuilder.append( ']' );

    }
    
}
