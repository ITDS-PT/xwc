package netgest.bo.xwc.components.classic.extjs;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.xwc.components.util.JavaScriptUtils;

public class ExtConfig {
    
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

    public final void addJSString( String id, String value ) {
        
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
        Iterator<String> oKeyIt;
        Object oValue;
        String sKey;
        boolean bIsFirst;
        
        bIsFirst = true;                
        
        if( sVarName != null ) {
            oBuilder.append( "var " ).append( this.sVarName ).append( '=' );
        }

        if( sComponentType != null ) {
            oBuilder.append( "new " );
            oBuilder.append( this.sComponentType  );
            oBuilder.append( "(" );
        }
        
        
        oBuilder.append( "{" );
        
        oKeyIt = oConfigOptions.keySet().iterator();
        while( oKeyIt.hasNext() ) {

            sKey = oKeyIt.next();
            oValue = oConfigOptions.get( sKey );
            
            if( !bIsFirst ) {
                oBuilder.append( ',' );
                oBuilder.append( '\n' );
            }
            
            oBuilder.append( sKey ).append( " : " );
            
            if( oValue instanceof ExtConfig ) {
                ((ExtConfig)oValue).renderExtConfig( oBuilder );
            }
            else if( oValue instanceof ExtConfigArray ) {
                ((ExtConfigArray)oValue).renderExtConfig( oBuilder );
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
}
