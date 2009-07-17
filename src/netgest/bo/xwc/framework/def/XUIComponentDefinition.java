package netgest.bo.xwc.framework.def;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class XUIComponentDefinition
{
    public String className;
    public String name;
    public String description;
    
    public Properties oProperties    			= new Properties();
    public Map<String,String> oMapRenderKits 	= new LinkedHashMap<String, String>();
    
    public XUIComponentDefinition()
    {
    }
    
    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    
    public void addProperty( String name, String type )
    {
        oProperties.put( name, type );
    }

    public boolean haveProperty( String name )
    {
        return oProperties.containsKey( name );
    }
    
    public String getPropertyType( String name )
    {
        return oProperties.getProperty( name );
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void addRenderKit( String type, String className )
    {
        oMapRenderKits.put( type, className );
    }
    
    public String[] getRenderKitTypes()
    {
        return (String[])oMapRenderKits.keySet().toArray( new String[ oMapRenderKits.size() ] );
    }
    
    public String  getRenderKitClassName( String sRenderKitType ) 
    {
        return (String)oMapRenderKits.get( sRenderKitType );
    }
}
