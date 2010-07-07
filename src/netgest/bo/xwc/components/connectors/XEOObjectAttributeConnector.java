package netgest.bo.xwc.components.connectors;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
import netgest.bo.xwc.components.localization.ConnectorsMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.def.XUIComponentParser;
import netgest.io.FSiFile;

public class XEOObjectAttributeConnector extends XEOObjectAttributeMetaData implements DataFieldConnector {

	private final Class[] LOOKUPQUERY_ARGUMENTS = { AttributeHandler.class, String.class };
	
    private static ThreadLocal<SimpleDateFormat> sdfDT = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
        }
    };

    private static ThreadLocal<SimpleDateFormat> sdfD = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };

    private static final Logger log = Logger.getLogger( XUIComponentParser.class.getName() );
 
    private AttributeHandler    oAttHandler;
        
    public XEOObjectAttributeConnector(XEOObjectConnector oXEODataRecordConnector, AttributeHandler oAttHandler ) 
    {
        super( oAttHandler!=null?oAttHandler.getDefAttribute():null );
        assert oAttHandler != null : new NullPointerException();
        assert oXEODataRecordConnector != null : new NullPointerException();
        this.oAttHandler = oAttHandler;
    }

    public Object getValue() {
        try {
        	if( (getSecurityPermissions()&SecurityPermissions.READ) == SecurityPermissions.READ ) {
	            Object oRetValue = oAttHandler.getValueObject();
	            return oRetValue;
        	}
        	return null;
        } catch (boRuntimeException e) {
            throw new RuntimeException( e );
        }
    }

    public String getDisplayValue() {

    	if( (getSecurityPermissions()&SecurityPermissions.READ) != SecurityPermissions.READ ) {
        	return null;
    	}
    	
    	String sRetValue = null;
    	try {
			if ( oAttHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE ) {
				boObject obj = oAttHandler.getObject();
				if( obj != null ) {
					sRetValue = obj.getCARDIDwNoIMG().toString();
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_BOOLEAN ) {
				sRetValue = oAttHandler.getValueString();
				if ( "0".equals( sRetValue ) ) {
					sRetValue = ConnectorsMessages.TEXT_NO.toString();;
				} else if ( "1".equals( sRetValue ) ) {
					sRetValue = ConnectorsMessages.TEXT_YES.toString();
				}
			} else if ( getInputRenderType() == DataFieldTypes.RENDER_IFILE_LINK ) {
				sRetValue = oAttHandler.getValueString();
				
				if( sRetValue.lastIndexOf("/") > -1 ) {
					sRetValue = sRetValue.substring( sRetValue.lastIndexOf("/") + 1 );
				}
				if( sRetValue.lastIndexOf("\\") > -1 ) {
					sRetValue = sRetValue.substring( sRetValue.lastIndexOf("\\") + 1 );
				}
				
			} else if ( getIsLov() ) {

				Object  value;
				Map<Object,String> 	lovMap;
				
				value  = getValue();
				lovMap = getLovMap();
				
				if( value != null && lovMap.size() > 0 )
				{
					if( !(value instanceof String) ) {
						value = String.valueOf( value );
					}
					if( lovMap.containsKey( value ) ) {
						return lovMap.get( value );
					}
					sRetValue = String.valueOf( value ); 
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_DATE || getDataType() == DataFieldTypes.VALUE_DATETIME ) {
				Date oDate = oAttHandler.getValueDate();
				if( oDate != null ) {
					if( getDataType() == DataFieldTypes.VALUE_DATE ) {
						sRetValue = sdfD.get().format( oDate );
					}
					else {
						sRetValue = sdfDT.get().format( oDate );
					}
				} else {
					sRetValue = null;
				}
			} else if ( getDataType() == DataFieldTypes.VALUE_NUMBER ) {
				BigDecimal oValue = (BigDecimal)oAttHandler.getValueObject();
				if( oValue != null ) {
					NumberFormat nf = NumberFormat.getInstance();
					boDefAttribute oDefAtt = oAttHandler.getDefAttribute();
					nf.setMinimumFractionDigits( oDefAtt.getMinDecimals() );
					nf.setMaximumFractionDigits( oDefAtt.getDecimals() );
					nf.setGroupingUsed( "Y".equals( oDefAtt.getGrouping() ) );
					sRetValue = nf.format( oValue );
				}
				else {
					sRetValue = null;
				}
			}
			else {
				Object oValue = oAttHandler.getValueObject();
				if( oValue != null )
					sRetValue = String.valueOf( oValue );
			}
			return sRetValue;
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
    }

    
    public boolean getDisabled() {
        try {
            return oAttHandler.isDisabled();
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate() {
        try {
        	boolean ret;
			ret = oAttHandler.valid();
			return ret;
			
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
    }
    
    public String getInvalidMessage() {
    	String sMessage = oAttHandler.getErrorMessage();
    	if( sMessage != null && sMessage.length() > 0 ) {
			if( sMessage.indexOf('[') > -1  )  
				sMessage = sMessage.substring(0,sMessage.lastIndexOf('['));
    	}
    	return sMessage;
    }

    public boolean getVisible() {
        try {
            return oAttHandler.isVisible();
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean getRequired() {
        try {
            return oAttHandler.required();
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getRecomended() {
        return oAttHandler.getRecommend();
    }

    public String[] getDependences() {

        boDefAttribute      oDefAttr;
        ArrayList<String>   allDependencesList;
        boDefXeoCode        oXeoCode;

        allDependencesList = new ArrayList<String>();
        oDefAttr           = oAttHandler.getDefAttribute();
        
        oXeoCode = oDefAttr.getDisableWhen();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }
    
        oXeoCode = oDefAttr.getOnChangeSubmit();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }

        oXeoCode = oDefAttr.getFormula();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }

        oXeoCode = oDefAttr.getDefaultValue();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }
        
        oXeoCode = oDefAttr.getRequired();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }

        oXeoCode = oDefAttr.getRecommend();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }

        oXeoCode = oDefAttr.getHiddenWhen();
        if( oXeoCode != null && oXeoCode.getDepends() != null )
        {
            allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
        }

//            oXeoCode = oDefAttr.getValid();
//            if( oXeoCode != null && oXeoCode.getDepends() != null )
//            {
//                allDependencesList.addAll( Arrays.asList( oXeoCode.getDepends() ) );
//            }
        
        return allDependencesList.toArray( new String[ allDependencesList.size() ] );
    }

    public boolean getOnChangeSubmit() {
    	return oAttHandler.getParent().onChangeSubmit( oAttHandler.getName() );
    }
    
    public void setValue( Object value ) {
        try {
            //TODO: This is not type safe... 
            if( value instanceof String ) {
            	try {
            		oAttHandler.setValueString( (String)value );
            	} catch ( Exception e ) {
            		log.log( LoggerLevels.WARNING , 
            				"Error binding value to attribute:[" + oAttHandler.getName() + "] value [" + value + "] " );
            		e.printStackTrace();
            	}
            }
            else
            {
            	if( boDefAttribute.ATTRIBUTE_BINARYDATA.equals( oAttHandler.getDefAttribute().getAtributeDeclaredType() 
            			 ) && (value instanceof File) )
            	{
            		oAttHandler.setValueiFile( new FSiFile( null, (File)value, null ) );
            	}
            	else {
            		oAttHandler.setValueObject( value );
            	}
            }
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public byte getSecurityPermissions() {
    	EboContext ctx = oAttHandler.getEboContext();
    	String attName = oAttHandler.getName();
    	String clsName = oAttHandler.getParent().getName();
    	
    	byte efectivePermissions = 0;

    	try {
			efectivePermissions += 
				securityRights
					.canRead(ctx, clsName, attName)
					&& securityOPL.canRead(oAttHandler.getParent()) ? SecurityPermissions.READ
					: 0;
			efectivePermissions +=
					securityRights.canWrite(ctx, clsName)
					&&
					securityRights.canWrite(ctx, clsName, attName)
					&& securityOPL.canWrite(oAttHandler.getParent()) ? SecurityPermissions.WRITE
					: 0;
			efectivePermissions += securityRights.canDelete(ctx, clsName,
					attName)
					&& securityOPL.canDelete(oAttHandler.getParent()) ? SecurityPermissions.DELETE
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
    	return super.getMaxLength();
    }

    public int getDecimalPrecision() {
        return oAttHandler.getDefAttribute().getDecimals();
    }

    public int getMinDecimals() {
        return oAttHandler.getDefAttribute().getMinDecimals();
    }

    public boolean getNumberGrouping() {
        return "Y".equalsIgnoreCase( oAttHandler.getDefAttribute().getGrouping() );
    }

    public Map<Object,String> getLovMap() {
        String           sLovName;
        sLovName = oAttHandler.getDefAttribute().getLOVName();

        Map<Object, String> lovMap = new LinkedHashMap<Object, String>();

        if( sLovName == null || sLovName.length() == 0 ) {
            ArrayList<Object> list = new ArrayList<Object>();
            try {
            	if( oAttHandler.isObject() ) {
                	
            		list.add( new Object[] { "", "" } );
            		
            		String sql;
            		
            		try {
	                	FacesContext context = FacesContext.getCurrentInstance();
	                    ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
	                    MethodExpression m = oExFactory.createMethodExpression( 
	                    		context.getELContext(), 
	                    		"#{viewBean.getLookupQuery}", 
	                    		String.class,
	                    		LOOKUPQUERY_ARGUMENTS                    		
	                    		
	                    );
	            		sql = 
	            			(String)m.invoke( 
	            					context.getELContext() , 
	            					new Object[] { 
	            						oAttHandler, 
	            						oAttHandler.getDefAttribute().getReferencedObjectDef().getName()
	            					} 
	            			);
            		}
            		catch ( Exception e ) {
                		log.severe("Error getting SQL to Object rendered as Lov", e );
                		sql = "select " + oAttHandler.getDefAttribute().getReferencedObjectDef().getName();
            		}
                   
	                boObjectList oObjectList = 
	                    boObjectList.list(oAttHandler.getEboContext(), 
	                                      //"select " + oAttHandler.getDefAttribute().getReferencedObjectDef().getName()
	                    				  sql
	                                      ,1,5000
	                                     );
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
            		lovMap = super.getLovMap();
            	}
            	else {
	            	try {
	                    lovObject oLovObject = LovManager.getLovObject( oAttHandler.getEboContext(), sLovName );
	
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
	                    log.severe( "Erro loading lov [" + sLovName + "]" + e.getClass().getName() + "-" + e.getMessage() );
	                }
            	}
            }
        }
        return lovMap;
    }

    public boolean getIsLovEditable() {
        boDefXeoCode oXeoCode = oAttHandler.getDefAttribute().getLovEditable();
        if( oXeoCode != null ) {
            oXeoCode.getBooleanValue();                
        }
        return false;
    }


    public DataListConnector getDataList() {
        if( oAttHandler.isBridge() ) {
            return new XEOBridgeListConnector( oAttHandler.getParent().getBridge( oAttHandler.getName() ) );
        }
        //
        throw new RuntimeException("This attribute is not a bridge");
    }

    public AttributeHandler getAttributeHandler() {
        return oAttHandler;
    }
}

