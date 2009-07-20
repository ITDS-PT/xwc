package netgest.bo.xwc.components.data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GridRowRenderClass;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.util.DateUtils;
import netgest.bo.xwc.components.util.JavaScriptUtils;

public class JavaScriptArrayProvider {
    
    private static final int INITIAL__BUFFER_SIZE = 150;
    
    private static final char[] EMPTY_FIELD = "''".toCharArray();
    
    private Iterator<DataRecordConnector> oDataIterator;
    private String[]          sDataFields;
    
    private int iStart;
    private int iLimit;
    
    public JavaScriptArrayProvider( Iterator<DataRecordConnector> oDataIterator, String[] dataFields, int start, int limit ) {

        assert oDataIterator != null : "Data connector cannot be null";
        assert dataFields != null : "Data fields must be specified";
        
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
    	getJSONArray( oStringBuilder, grid, keyField, selectedRows, null );
    }

    public final void getJSONArray( StringBuilder oStringBuilder, GridPanel grid,String keyField, String[] selectedRows, Map<String,GridColumnRenderer> columnRenderers ) 
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
        
        GridRowRenderClass rowClass = null;
        
        if( grid != null ) {
        	rowClass = grid.getRowClass(); 
        }
        
        for (int i = 0; i < this.iStart; i++) {
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
            	oStringBuilder.append( "__rc:'" );
    			JavaScriptUtils.safeJavaScriptWrite( oStringBuilder, rowClass.getRowClass( grid, oDataRecord ),'\'');
                oStringBuilder.append( "'," );
        	}
        
            for (int i = 0; i < sDataFields.length; i++) { 
                
                if( i > 0 )
                    oStringBuilder.append( ',' );
                

                oStringBuilder.append( sDataFields[i].replaceAll("\\.", "__") );	
                oStringBuilder.append(':');

                oDataField = oDataRecord.getAttribute( sDataFields[i] );
                
                if( oDataField != null ) {
                	if( columnRenderers != null && columnRenderers.containsKey( sDataFields[i] ) ) {
                		oDataFieldValue = columnRenderers.get( sDataFields[i] ).render(  grid, oDataRecord, oDataField );
                        oStringBuilder.append( '\'' );
                        JavaScriptUtils.safeJavaScriptWrite( 
                            oStringBuilder, 
                            ((String)oDataFieldValue),
                            '\''
                        );
                        oStringBuilder.append( '\'' );
                	}
                	else {
	                    oDataFieldValue = oDataField.getValue();
	                    if( oDataFieldValue != null )
	                    {
	                    	
	                        if( sDataFields[i].equals( keyField ) ) {
	                        	if( selRows.indexOf( String.valueOf( oDataFieldValue ) ) > -1 ) {
	                        		selRowNums += iCntr + "|";
	                        	}
	                        }
	                    	
	                    	sDisplayValue = oDataField.getDisplayValue(); 
	                    	if( sDisplayValue != null ) {
	                            oStringBuilder.append( '\'' );
	                            JavaScriptUtils.safeJavaScriptWrite( oStringBuilder, sDisplayValue, '\'' );
	                            oStringBuilder.append( '\'' );
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
		                        
		                            case DataFieldTypes.VALUE_CHAR:
		                            case DataFieldTypes.VALUE_CLOB:
		                                oStringBuilder.append( '\'' );
		                                JavaScriptUtils.safeJavaScriptWrite( 
		                                    oStringBuilder, 
		                                    ((String)oDataFieldValue),
		                                    '\''
		                                );
		                                oStringBuilder.append( '\'' );
		                                break;
		                            case DataFieldTypes.VALUE_NUMBER:
		                                oStringBuilder.append( ((BigDecimal)oDataFieldValue).toString() );
		                                break;
		                            case DataFieldTypes.VALUE_DATE:
		                                oStringBuilder.append('\'');
		                                DateUtils.formatTimestampToDate( oStringBuilder, (Timestamp)oDataFieldValue );
		                                oStringBuilder.append('\'');
		                                break;
		                            case DataFieldTypes.VALUE_DATETIME:
		                                oStringBuilder.append('\'');
		                                DateUtils.formatTimestampToDateTime( oStringBuilder, (Timestamp)oDataFieldValue );
		                                oStringBuilder.append('\'');
		                                break;
		                        }
	                    	}
	                    }
	                    else {
	                        oStringBuilder.append( EMPTY_FIELD );
	                    }
                    }
                }
                else {
                	oStringBuilder.append('\'');
                    oStringBuilder.append( JavaScriptUtils.safeJavaScriptWrite( ComponentMessages.GRID_INVALID_COLUMN.toString( sDataFields[i] ), '\'') );
                	oStringBuilder.append('\'');
                    //oStringBuilder.append( EMPTY_FIELD );
                }
            }
            bFirstRow = false;

            oStringBuilder.append( ',' );
            oStringBuilder.append( "__sRows:" );	
            oStringBuilder.append('\'');
            oStringBuilder.append( selRowNums );
            oStringBuilder.append('\'');
            
            
            oStringBuilder.append( '}' );
    
            iCntr++;
            if( iCntr >= this.iLimit ) {
                break;
            }
            
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
                                oStringBuilder.append( '\'' );
                                JavaScriptUtils.safeJavaScriptWrite( 
                                    oStringBuilder, 
                                    ((String)oDataFieldValue),
                                    '\''
                                );
                                oStringBuilder.append( '\'' );
                                break;
                            case DataFieldTypes.VALUE_NUMBER:
                                oStringBuilder.append( ((BigDecimal)oDataFieldValue).toString() );
                                break;
                            case DataFieldTypes.VALUE_DATE:
                                DateUtils.formatTimestampToDate( oStringBuilder, (Timestamp)oDataFieldValue );
                                break;
                            case DataFieldTypes.VALUE_DATETIME:
                                DateUtils.formatTimestampToDateTime( oStringBuilder, (Timestamp)oDataFieldValue );
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
