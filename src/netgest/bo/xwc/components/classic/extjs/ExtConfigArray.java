package netgest.bo.xwc.components.classic.extjs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import netgest.bo.xwc.components.util.JavaScriptUtils;

public class ExtConfigArray {

    private List<Object> childs = new ArrayList<Object>();
    
    public final ExtConfig addChild( ExtConfig oExtConfig ) {
        childs.add( oExtConfig );
        return oExtConfig;
    }

    public final StringBuilder renderExtConfig() {
        return renderExtConfig( new StringBuilder(  ) );
    }
    
    public final StringBuilder renderExtConfig( StringBuilder oStringBuilder ) {
        Iterator<Object> oChildsIt;
        Object   oChild;
        boolean  bIsFirst;
        
        bIsFirst = true;
        oChildsIt = childs.iterator();
        
        oStringBuilder.append( '[' );
        while( oChildsIt.hasNext() ) {
            
            if( !bIsFirst ) {
                oStringBuilder.append( ',' );
            }
            
            oChild = oChildsIt.next();
            
            if( oChild instanceof ExtConfig ) {
                ((ExtConfig)oChild).renderExtConfig( oStringBuilder );
            }
            else {
                oStringBuilder.append( oChild );
            }
            bIsFirst = false;
        }
        oStringBuilder.append( ']' );

        return oStringBuilder;

    }

    public final void add( Object oValue ) {
        childs.add( oValue );
    }

    public final void addString( String oValue ) {
        childs.add( "'" + JavaScriptUtils.writeValue( oValue ) + "'" );
    }

    public final ExtConfig addChild( String sComponetType ) {
        return addChild( new ExtConfig( sComponetType ) );
    }

    public final ExtConfig addChild() {
        return addChild( new ExtConfig() );
    }
    
    public int size() {
    	return childs.size();
    }
    
    
}
