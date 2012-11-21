package netgest.bo.xwc.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;



import netgest.bo.system.Logger;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIComponentStore;

public class XUIDefaultPropertiesHandler {
	
	
	private static Logger LOGGER = Logger.getLogger( XUIDefaultPropertiesHandler.class );
	
	//If the file exists but an error is generated parsing the file
	//this variable is always false
	private static Boolean fileExists = null;
	
	private static String FILE = "XWVDefaultProperties.properties";
	
	private static Map<Class,Map<String,String>> translated_properties=null;	
	
	
	public static String getPropertyValue(String name,XUIComponentBase oComp)
	{
		String value=null;

		//Only Runs Once
		if (translated_properties==null && fileExists())
			initializePropertiesFromFile(oComp);
	
		if (translated_properties!=null)
		{
			Map<String,String> props=translated_properties.get(oComp.getClass());			
			if (props!=null)
				value=props.get(name);
		}
		
		return value;
	}
	
	public static Object getComponentProperties(XUIComponentBase oComp)
	{
		Map<String,String> props=null;

		//Only Runs Once
		if (translated_properties==null && fileExists())
			initializePropertiesFromFile(oComp);
		
		if (translated_properties!=null)
			props=translated_properties.get(oComp.getClass());			
		
		return props;
	}
	
	public static boolean existPropertiesForComponent(XUIComponentBase oComp)
	{
		boolean toRet=false;
		
		if (translated_properties==null && fileExists())
			initializePropertiesFromFile(oComp);
		
		if(translated_properties!=null)
		{
			Map<String,String> props=translated_properties.get(oComp.getClass());
			if (props!=null)
				toRet=true;
		}
			
		return toRet;
	}
	
	private static synchronized void initializePropertiesFromFile(XUIComponentBase oComp)
	{
		if (translated_properties==null)
		{
			InputStream stream = null;
			try {
				
				Properties properties = new Properties();    
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE);
				properties.load(stream);
				
				if (properties!=null)
				{
					XUIComponentStore cStore=oComp.getRequestContext().getApplicationContext().
							getComponentStore();
					
					String compNames[]=cStore.getComponentNames();
					
					for (String compName:compNames)
					{
						XUIComponentDefinition compDef=cStore.getComponent(compName);
						Enumeration<Object> compKeys=properties.keys();
						while (compKeys.hasMoreElements())
						{
							String compKey=(String)compKeys.nextElement();
							if (compKey.startsWith(compDef.getNameSpace()+"_"
								+compDef.getName()))
							{
								String compKeyProperty=compKey.substring(compKey.lastIndexOf("_")+1,
										compKey.length());
								
								addToTranslatedProperties(compDef, 
										compKeyProperty, properties.getProperty(compKey));							
							}
								
						}
						
					}
				}
			} catch (Exception e) {
				//If an error occurs put fileExists to false
				//It prevent the file from being parsed again
				fileExists = false;
				LOGGER.severe("ERROR OCCURED IN PARSING COMPONENT DEFAULT PROP FILE", e);
			}
			finally
			{
				if (stream!=null)
					try {
						stream.close();
					} catch (IOException e) {
						//Do nothing
					}
			}
		}
	}

	private static void addToTranslatedProperties(XUIComponentDefinition compDef,
			String propertyName,String propertyValue)
	{
		XUIComponentBase comp=null;
		if(compDef != null)
	    {
			 try {
				comp = (XUIComponentBase)Class.forName( compDef.getClassName() ).newInstance();
			} catch (InstantiationException e) {	
				LOGGER.severe("ERROR INVOKING COMPONENT", e);
			} catch (IllegalAccessException e) {
				LOGGER.severe("ERROR INVOKING COMPONENT", e);
			} catch (ClassNotFoundException e) {
				LOGGER.severe("ERROR INVOKING COMPONENT", e);
			};
	    }
		
		if (translated_properties==null)
			translated_properties = new ConcurrentHashMap<Class,Map<String,String>>();
		
		if (comp!=null)
		{
			Map<String, String> props=translated_properties.get(comp.getClass());
			if (props==null) 
				props=new ConcurrentHashMap<String, String>();
			
			if (!props.containsKey(propertyName))
			{
				if (propertyValue!=null)
					props.put(propertyName, propertyValue);			
				translated_properties.put( comp.getClass(), props);
			}
		}
	}
	
	private static boolean fileExists() {
		InputStream stream = null;
		if (fileExists==null)
		{
			try {
				Properties properties = new Properties();    
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE);
				properties.load(stream);
				fileExists=true;
			}
			catch (Exception e)
			{
				LOGGER.severe("ERROR OCCURED IN PARSING COMPONENT DEFAULT PROP FILE", e);
				fileExists = false;
			}
			finally
			{
				if (stream!=null)
					try {
						stream.close();
					} catch (IOException e) {
					}
			}
		}
		return fileExists;
	}
	
}
