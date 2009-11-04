package netgest.bo.xwc.components.classic.extjs;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import netgest.bo.xwc.components.util.JavaScriptUtils;

public class ExtConfig {
    
	private boolean bPublic;
	
    private String sComponentType;
    private String sVarName;
    
    Map<String, Object> oConfigOptions = new LinkedHashMap<String, Object>();
    
    public ExtConfig() {
    }
    
    public ExtConfig( String sComponentType ) {
        this( sComponentType, null );
    }

    public ExtConfig( String sComponentType, String sVarName ) {
        this.sComponentType = sComponentType;
        this.sVarName = sVarName;
    }

    
    public final void add( String id, Object value ) {
        oConfigOptions.put( id, value );
    }

    public final void addString( String id, CharSequence value ) {
    	addJSString( id, value);
    }
    
    public final void addJSString( String id, CharSequence value ) {
        
        if( value == null )
            value = "";
        
        oConfigOptions.put( id, (new StringBuilder( value.length() + 2 )).append('"').append( 
        		JavaScriptUtils.safeJavaScriptWrite(value,  '\"' )
        ).append( '"' ) );
        
    }
    
    public final ExtConfigArray addChildArray( String id ) {
        ExtConfigArray oExtConfig;
        
        oExtConfig =  new ExtConfigArray(); 
        oConfigOptions.put( id, oExtConfig );
        
        return oExtConfig;
    }

    public final ExtConfig addChild( String id ) {
        ExtConfig oExtConfig;
        
        oExtConfig =  new ExtConfig();
        oConfigOptions.put( id, oExtConfig );
        return oExtConfig;

    }

    public final StringBuilder renderExtConfig() {
        return renderExtConfig( new StringBuilder(  ) );
    }
            
    public final StringBuilder renderExtConfig( StringBuilder oBuilder ) {
        Iterator<Entry<String,Object>> oKeyIt;
        Object oValue;
        boolean bIsFirst;
        
        bIsFirst = true;                
        
        if( sVarName != null ) {
        	if( isPublic() )
        		oBuilder.append( "window." ).append( this.sVarName ).append( '=' );
        	else
        		oBuilder.append( "var " ).append( this.sVarName ).append( '=' );
        }

        if( sComponentType != null ) {
            oBuilder.append( "new " );
            oBuilder.append( this.sComponentType  );
            oBuilder.append( "(" );
        }
        
        oBuilder.append( "{" );
        
        oKeyIt = oConfigOptions.entrySet().iterator();
        
        while( oKeyIt.hasNext() ) {

            Entry<String,Object> sEntry = oKeyIt.next();
            
            if( !bIsFirst ) {
                oBuilder.append( ',' );
                oBuilder.append( '\n' );
            }
            
            if( sEntry.getKey() != null ) {
	            oBuilder.append( sEntry.getKey() ).append( " : " );
            }
            oValue = sEntry.getValue();
            if( oValue instanceof ExtConfig ) {
                ((ExtConfig)oValue).renderExtConfig( oBuilder );
            }
            else if( oValue instanceof ExtConfigArray ) {
                ((ExtConfigArray)oValue).renderExtConfig( oBuilder );
            }
            else if( oValue instanceof CharSequence ) {
            	oBuilder.append( ((CharSequence)oValue).toString() );
            }
            else {
                oBuilder.append( oValue );
            }
            bIsFirst = false;
        }
        oBuilder.append( "}" );

        if( sComponentType != null ) {
            oBuilder.append( ")" );
        }

        return oBuilder;

    }

    public ExtConfig getConfig( String id ) {
    	return getConfig( id, false );
    }

    public ExtConfig getConfig( String id, boolean createNew ) {
    	ExtConfig ret;
    	
    	ret = (ExtConfig)oConfigOptions.get( id );
    	if( ret == null ) {
    		ret = new ExtConfig();
    		add( id, ret );
    	}
    	return ret;
    }

    public ExtConfigArray getConfigArray( String id ) {
    	return (ExtConfigArray)oConfigOptions.get( id );
    }
    
    public void setComponentType(String sComponentType) {
        this.sComponentType = sComponentType;
    }

    public String getComponentType() {
        return sComponentType;
    }

    public void setVarName(String sVarName) {
        this.sVarName = sVarName;
    }

    public String getVarName() {
        return sVarName;
    }

	public boolean isPublic() {
		return bPublic;
	}

	public void setPublic(boolean bPublic) {
		this.bPublic = bPublic;
	}
    
}
