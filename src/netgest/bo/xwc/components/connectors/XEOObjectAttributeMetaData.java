package netgest.bo.xwc.components.connectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import netgest.bo.system.Logger;

import netgest.bo.def.boDefAttribute;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityRights;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.def.XUIComponentParser;

public class XEOObjectAttributeMetaData implements DataFieldMetaData {
	
	private static final Logger log = Logger.getLogger( XUIComponentParser.class.getName() );
	
	private boDefAttribute defAtt;
	
	public XEOObjectAttributeMetaData( boDefAttribute defAtt ) {
		this.defAtt = defAtt;
	}
	
	public boDefAttribute getBoDefAttribute() {
		return this.defAtt;
	}
	
	public byte getDataType() {
        String sValueType = defAtt.getAtributeDeclaredType();
        if ( boDefAttribute.ATTRIBUTE_BOOLEAN.equals( sValueType ) )
            return DataFieldTypes.VALUE_BOOLEAN;

        if ( boDefAttribute.ATTRIBUTE_DURATION.equals( sValueType ) )
            return DataFieldTypes.VALUE_NUMBER;
        if ( boDefAttribute.ATTRIBUTE_CURRENCY.equals( sValueType ) )
            return DataFieldTypes.VALUE_NUMBER;

        if ( boDefAttribute.ATTRIBUTE_DATE.equals( sValueType ) )
                return DataFieldTypes.VALUE_DATE;

        if ( boDefAttribute.ATTRIBUTE_DATETIME.equals( sValueType ) )
                return DataFieldTypes.VALUE_DATETIME;

        if ( boDefAttribute.ATTRIBUTE_DURATION.equals( sValueType ) )
                return DataFieldTypes.VALUE_NUMBER;

        if ( boDefAttribute.ATTRIBUTE_LONGTEXT.equals( sValueType ) )
                return DataFieldTypes.VALUE_CHAR;
            
        if ( boDefAttribute.ATTRIBUTE_NUMBER.equals( sValueType ) )
                return DataFieldTypes.VALUE_NUMBER;
            
        if ( boDefAttribute.ATTRIBUTE_OBJECT.equals( sValueType ) )
                return DataFieldTypes.VALUE_NUMBER;
            
        if ( boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equals( sValueType ) )
                return DataFieldTypes.VALUE_BRIDGE;
        
        if ( boDefAttribute.ATTRIBUTE_SEQUENCE.equals( sValueType ) )
                return DataFieldTypes.VALUE_NUMBER;

        if ( boDefAttribute.ATTRIBUTE_TEXT.equals( sValueType ) )
                return DataFieldTypes.VALUE_CHAR;

        if ( boDefAttribute.ATTRIBUTE_BINARYDATA.equals( sValueType ) )
            return DataFieldTypes.VALUE_CHAR;
        
        return DataFieldTypes.VALUE_UNKNOWN;
	
	}

	public String getLabel() {
        return defAtt.getLabel();
    }

    public byte getSecurityPermissions() {
    	EboContext ctx = boApplication.currentContext().getEboContext();
    	String attName = defAtt.getName();
    	String clsName = defAtt.getBoDefHandler().getName();
    	
    	byte efectivePermissions = 0;

    	try {
			efectivePermissions += securityRights
					.canRead(ctx, clsName, attName) ? SecurityPermissions.READ
					: 0;
			efectivePermissions += securityRights.canWrite(ctx, clsName,
					attName) ? SecurityPermissions.WRITE
					: 0;
			efectivePermissions += securityRights.canDelete(ctx, clsName,
					attName) ? SecurityPermissions.DELETE
					: 0;
			if (efectivePermissions == 7) {
				efectivePermissions = SecurityPermissions.FULL_CONTROL;
			}
		} catch (Exception e) {
			throw new RuntimeException( e );
		}
		return efectivePermissions;
	}

	public int getMaxLength() {
    	if( defAtt.getValueType() == boDefAttribute.VALUE_CLOB ) {
    		return Integer.MAX_VALUE;
    	}
        return defAtt.getLen();
        
    }

    public int getDecimalPrecision() {
        return defAtt.getDecimals();
    }

    public int getMinDecimals() {
        return defAtt.getMinDecimals();
    }

    public boolean getNumberGrouping() {
        return Boolean.parseBoolean( defAtt.getGrouping() );
    }
	
	
    public byte getInputRenderType() {
        String sValueType = defAtt.getAtributeDeclaredType();

        if ( boDefAttribute.ATTRIBUTE_TEXT.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_OBJECT.equals( sValueType ) ) {
            if( defAtt.renderAsLov() )
            {
                return DataFieldTypes.RENDER_LOV;
            }
            else {
                return DataFieldTypes.RENDER_OBJECT_LOOKUP;
            }
        }

        if ( boDefAttribute.ATTRIBUTE_DATE.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_NUMBER.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_LONGTEXT.equals( sValueType ) )
        {
        	
        	if ("HTML".equalsIgnoreCase(defAtt.getEditorType())
        			|| "HTMLADVANCED".equalsIgnoreCase(defAtt.getEditorType()))
        		return DataFieldTypes.RENDER_HTMLEDITOR;
        	else
        		return DataFieldTypes.RENDER_TEXTAREA;
        }

        if ( boDefAttribute.ATTRIBUTE_DATETIME.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_BOOLEAN.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_CURRENCY.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT; // TODO: Implement currency data field

        if ( boDefAttribute.ATTRIBUTE_DURATION.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT; // TODO: Implement clock 

        if ( boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equals( sValueType ) )
            return DataFieldTypes.RENDER_OBJECT_LOOKUP;
        
        if ( boDefAttribute.ATTRIBUTE_SEQUENCE.equals( sValueType ) )
            return DataFieldTypes.RENDER_DEFAULT;

        if ( boDefAttribute.ATTRIBUTE_BINARYDATA.equals( sValueType ) )
            return DataFieldTypes.RENDER_IFILE_LINK;
        
        return DataFieldTypes.VALUE_UNKNOWN;
    }

    public boolean getIsLov() {
        if( defAtt.renderAsLov() )
            return true;

        String sLovName = defAtt.getLOVName();
        return  sLovName != null && sLovName.length() > 0;

    }

	public Map<Object, String> getLovMap() {
        String           sLovName;
        sLovName = this.defAtt.getLOVName();

        LinkedHashMap<Object, String> lovMap = new LinkedHashMap<Object, String>();

        if( sLovName == null || sLovName.length() == 0 ) {
            ArrayList<Object> list = new ArrayList<Object>();
            try {
            	if( this.defAtt.getAtributeDeclaredType() == boDefAttribute.ATTRIBUTE_OBJECT ) {
                	list.add( new Object[] { "", "" } );
	                boObjectList oObjectList = 
	                    boObjectList.list( boApplication.currentContext().getEboContext() , 
	                                      "select " + defAtt.getReferencedObjectDef().getName(), 1, 500 );
	                while (oObjectList.next()) {
	                    list.add( new Object[] { oObjectList.getCurrentBoui(), oObjectList.getObject().getCARDIDwNoIMG().toString() } );
	                }
	                Object[] toRet = list.toArray(new Object[ list.size() ]);
	                
	                Arrays.sort( toRet,0,toRet.length, new Comparator<Object>() {
	                		public int compare( Object o1, Object o2 ) {
	                			return ((String)((Object[])o1)[1]).compareTo( (String)((Object[])o2)[1] );
	                		}
	                	}
	                );

	                for( Object lovItem : toRet ) {
	                	lovMap.put( 
	        				((Object[])lovItem)[0],
	        				(String)((Object[])lovItem)[1]
	        			);
	                }
            	}

            } catch (boRuntimeException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            if( sLovName != null && sLovName.length() > 0 ) {
            	
            	String sLovSql = getBoDefAttribute().getLOVSql();
            	if( sLovSql != null ) {

                	String sLovSqlId = getBoDefAttribute().getLOVSqlIdField();
                	String sLovSqlDesc = getBoDefAttribute().getLOVSqlDescField();
            		
            		try {
						EboContext ctx = boApplication.currentContext().getEboContext();
						PreparedStatement pstm = ctx.getConnectionData().prepareStatement(
									sLovSql
								);
						
						ResultSet rslt = pstm.executeQuery();
						while( rslt.next() ) {
							lovMap.put( rslt.getObject( sLovSqlId ), rslt.getString( sLovSqlDesc ) );
						}
						
						rslt.close();
						pstm.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            		
            	}
            	else {
	                try {
	                    lovObject oLovObject = LovManager.getLovObject( boApplication.currentContext().getEboContext(), sLovName );
	
	                    lovMap.put("", "");
	                    if( oLovObject != null ) {
		                    synchronized( oLovObject ) {
		                        oLovObject.beforeFirst();
		                        while( oLovObject.next() )
		                        	lovMap.put( oLovObject.getCode(), oLovObject.getDescription() );
		                    }
	                    }
	                }
	                catch (boRuntimeException e) {
	                    log.severe( MessageLocalizer.getMessage("ERROR_LOADING_LOV")+" [" + sLovName + "]" + e.getClass().getName() + "-" + e.getMessage() );
	                }
            	}
            }
        }
        return lovMap;
	}

	public double getNumberMaxValue() {
		return Double.parseDouble( defAtt.getMax() );
	}

	public double getNumberMinValue() {
		return Double.parseDouble( defAtt.getMin() );
	}
	
	
	
	
}
