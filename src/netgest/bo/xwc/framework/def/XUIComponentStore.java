package netgest.bo.xwc.framework.def;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.localizations.MessageLocalizer;


public class XUIComponentStore
{

    /**
     * A map with all the registered components
     * Key is namespace:componentName and value is the component definition 
     */
    private Map<String, XUIComponentDefinition> components = 
    	new Hashtable<String, XUIComponentDefinition>();
    
    private Map<String, String> renderKits = 
    	new Hashtable<String, String>();
    /**
     * A map with all the renderer components (and corresponding renderKits)
     */
    private Map<String, Map<String,XUIRendererDefinition>> renderers = 
    	new Hashtable<String, Map<String,XUIRendererDefinition>>();
    
    
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
    
    
    /**
     * 
     * Return a list with all the render kit names
     * 
     * @return An array with the name of the renderkits
     */
    public Iterator<String> getRenderKitsList(){
    	return this.renderers.keySet().iterator();
    }
    
    /**
     * 
     * Retrieves the class name of a given renderer family and type
     * in a given renderkit
     * 
     * @param renderkitName The name of the render kit 
     * @param familyName The family name of the renderer
     * @param rendererType The renderer type
     * 
     * @return The name of the class that renders
     */
    public String getRenderKitClassName(String renderkitName, String familyName, 
    		String rendererType)
    {
    	if (this.renderers.containsKey(renderkitName))
    	{
    		String key = familyName + ":" + rendererType;
    		Map<String,XUIRendererDefinition> map = this.renderers.get(renderkitName);
    		if (map.containsKey(key))
    		{
    			return map.get(key).getClassName();
    		}
    		return null;
    		
    	}
    	
    	return null;
    }
    
    /**
     * 
     * Retrieves the list of renderers for a given render kit
     * 
     * @param name The name of the render kit
     * @return A map with the renderers for the render kit
     */
    public Map<String,XUIRendererDefinition> getMapOfRenderKit(String name)
    {
    	return this.renderers.get(name);
    }
    
    /**
     * 
     * Registers a renderer with the component store
     * 
     * @param renderKitName The name of the renderkit
     * @param renderkitClassName The class that will make the render
     * @param familyName The name of the family of the component
     * @param rendererType The type of the component
     */
    public void registerRenderKit(String renderKitName, 
    		String renderkitClassName, String familyName, String rendererType)
    {
    	XUIRendererDefinition oRendererDef = new XUIRendererDefinition();
    	oRendererDef.setClassName(renderkitClassName);
    	oRendererDef.setFamilyName(familyName);
    	oRendererDef.setRendererType(rendererType);
    	
    	String rendererKey = familyName + ":" + rendererType;
    	
    	if (this.renderers.containsKey(renderKitName))
    	{
    		Map<String,XUIRendererDefinition> renderMap = this.renderers.get(renderKitName);
    		renderMap.put(rendererKey, oRendererDef);
    	}
    	else
    	{
    		Map<String,XUIRendererDefinition> renderMap = new Hashtable<String, XUIRendererDefinition>();
    		renderMap.put(rendererKey, oRendererDef);
    		renderers.put(renderKitName, renderMap);
    	}
    }
    
    
    /**
     * 
     * Registers a new component in the component store
     * 
     * @param nameSpace The namespace associated with the component
     * @param component The component definition
     */
    public void registerComponent( String nameSpace, XUIComponentDefinition component )
    {
        components.put( nameSpace + ":" + component.getName(), component );        
    }
    
    /**
     * 
     * Finds a component given its name
     * 
     * @param name The name of the component
     * @return The definition of the component or null if it
     * does not exist
     */
    public XUIComponentDefinition findComponent( String name )
    {
        assert name != null : MessageLocalizer.getMessage("NAME_CANNOT_BE_NULL");
        
        return (XUIComponentDefinition)components.get( name );        
    }
    
    /**
     * 
     * Retrieves a list with all component names
     * 
     * @return An array with the name of the components
     */
    public String[] getComponentNames()
    {
        return (String[])components.keySet().toArray( new String[ components.size() ] );
    }
    
    public XUIComponentDefinition getComponent( String sComponentName )
    {
        return (XUIComponentDefinition)components.get( sComponentName );
    }
}
