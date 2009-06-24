package netgest.bo.xwc.framework.def;

import java.util.Hashtable;
import java.util.Map;


public class XUIComponentStore
{

    private Map<String, XUIComponentDefinition> components = new Hashtable<String, XUIComponentDefinition>();
    private Map<String, String> renderKits = new Hashtable<String, String>();

    public void registerRenderKit( String renderKitName, String renderKitClassName )
    {
        renderKits.put( renderKitName, renderKitClassName );
    }
    
    public String getRenderKitClassName( String renderKitName ) {
    	return renderKits.get( renderKitName );
    }
    
    public String[] getRenderKits() {
    	return (String[])renderKits.keySet().toArray( new String[0] );
    }
    
    public void registerComponent( XUIComponentDefinition component )
    {
        components.put( component.getName(), component );        
    }
    
    public XUIComponentDefinition findComponent( String name )
    {
        assert name != null : "Name cannot be null";
        
        return (XUIComponentDefinition)components.get( name );        
    }
    
    public String[] getComponentNames()
    {
        return (String[])components.keySet().toArray( new String[ components.size() ] );
    }
    
    public XUIComponentDefinition getComponent( String sComponentName )
    {
        return (XUIComponentDefinition)components.get( sComponentName );
    }
}
