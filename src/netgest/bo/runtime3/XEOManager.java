package netgest.bo.runtime3;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import netgest.bo.boException;
import netgest.bo.data.DataSet;
import netgest.bo.data.Driver;
import netgest.bo.def.boDefHandler;
import netgest.bo.ejb.impl.boManagerBean;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectFactoryData;
import netgest.bo.runtime.boObjectListResultFactoryLegacy;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.cacheBouis;

public class XEOManager {

	private XEOCache	cacheManager;

	public XEOManager() {
		init();
	}
	
	private void init() {
		//cacheManager = new XEOCacheManager();
	}
	
	public DataSet list( EboContext ctx, boDefHandler baseObjectDef, String nativeQuery, List<Object> nativeQueryArgs, int page, int pageSize ) {
		
		String className;
		StringBuilder queryHash;
		
		int queryHashCode;
		
		className = baseObjectDef.getName();
		queryHash = new StringBuilder();
		queryHash.append( nativeQuery );
		if( nativeQueryArgs != null && nativeQueryArgs.size() > 0 ) {
			Iterator<Object> it =  nativeQueryArgs.iterator();
			while( it.hasNext() ) {
				queryHash.append( '{' ).append( it.next() ).append( '}' );
			}
		}
		queryHashCode = queryHash.toString().hashCode();
		
		DataSet retDataSet = cacheManager.getList( className, queryHashCode );
		
		if( retDataSet == null ) {
			retDataSet = netgest.bo.data.ObjectDataManager.executeNativeQuery(
					ctx, 
					baseObjectDef, 
					(String)nativeQuery, 
					page, 
					pageSize,
					nativeQueryArgs, 
					false
				);
			cacheManager.putList( className, queryHashCode, retDataSet );
		}
		return retDataSet;
	}
	
	public boObject loadObject( EboContext ctx, long boui ) throws boRuntimeException {
		DataSet objectData;
		String className;
		
		className = getXEOClassName( ctx, boui);
		objectData = cacheManager.getObject( className, boui);
		
		boObject retObject = getObject( ctx, className );
		retObject.setEboContext( ctx );
		
		if( objectData != null ) {
			retObject.load( objectData );
		}
		else {
			retObject.load( boui );
		}
		
		return retObject;
	}
	
	public boObject createObject( EboContext ctx, String className ) throws boRuntimeException {
        boObject ret = getObject( ctx, className );
        ret.setEboContext(ctx);
        long boui = createNewBoui( ctx );
        if( ret != null ) {
        	if( !ret.getBoDefinition().getDataBaseManagerXeoCompatible() ) {
        		boObject.getBoManager().registerRemoteBoui(  ctx, boui, new boObjectListResultFactoryLegacy(), null );
        	}
        }
        return ret;
	}

    private long createNewBoui( EboContext ctx )
    {
        return 
        	ctx.getDataBaseDriver().getDBSequence( ctx.getConnectionSystem(), "borptsequence", Driver.SEQUENCE_NEXTVAL ); 
    }
	
    @SuppressWarnings("unchecked")
	private boObject getObject(EboContext ctx, String name) throws boRuntimeException {
	    try
	    {
	        boDefHandler bodef = boDefHandler.getBoDefinition(name);
	        if (bodef == null)
	        {
	            throw new boRuntimeException(this.getClass().getName() +
	                ".getObject(...)", "BO-3019", null, name);
	        }
	        String version = "v" + bodef.getBoVersion().replace('.', '_');
	        name = version + "." + name;
	        Class xclass   = Class.forName( name, true, ctx.getApplication().getClassLoader() );
	        boObject retobj = (boObject) xclass.newInstance();
	        return retobj;
	    }
	    catch (ClassNotFoundException e)
	    {
	        throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
	            "BO-2101", e, name);
	    }
	    catch (IllegalAccessException e)
	    {
	        throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
	            "BO-2101", e, name);
	    }
	    catch (InstantiationException e)
	    {
	        throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
	            "BO-2101", e, name);
	    }
    }
    
    private String getXEOClassName( EboContext ctx, long boui ) throws boRuntimeException 
    {
    	String classname;
    	Long oBoui;
    	
    	oBoui = new Long( boui );
    	Object[] r = cacheBouis.getRemoteBoui( oBoui );
    	if( r != null ) {
    		classname = ((boObjectFactoryData)r[1]).getObjectName();
    	}
        classname = cacheBouis.getClassName( oBoui );
        if ( classname==null )
        {
            Connection cn = null;
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            try
            {
                cn = ctx.getConnectionData();
                pstm = cn.prepareStatement(
                        "SELECT CLSID FROM OEBO_REGISTRY WHERE UI$=?",  ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );

                pstm.setLong(1, boui);
                rslt = pstm.executeQuery();

                if (rslt.next())
                {
                    classname = rslt.getString(1);
                }
                else
                {
                      throw new boRuntimeException(boManagerBean.class.getName() +
                    ".getClassNameFromBoui(EboContext,long) User:["+ctx.getSysUser().getUserName()+"]", "BO-3015", null, "" + boui);
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                try
                {
                    rslt.close();
                    pstm.close();
                }
                catch (Exception e)
                {
                }


            }
            cacheBouis.putBoui( boui , classname );
        }
        return classname;
    }
}
