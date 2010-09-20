package netgest.bo.xwc.framework;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import netgest.bo.system.Logger;
import netgest.bo.xwc.components.classic.renderers.XMLViewRootRenderer;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIComponentParser;
import netgest.bo.xwc.framework.def.XUIComponentStore;
import netgest.bo.xwc.framework.def.XUIRendererDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitonParser;
import netgest.bo.xwc.framework.jsf.XUIRenderKit;
import netgest.bo.xwc.framework.jsf.XUIViewerBuilder;

public class XUIApplicationContext {

    private Logger log = Logger.getLogger( XUIApplicationContext.class ); 

    private Hashtable<String, RenderKit> oRenderKits   = new Hashtable<String, RenderKit>();
    
    private XUIComponentParser   oComponentParser;
    private XUIComponentStore    oComponentStore;

    private XUIViewerBuilder oViewerBuilder;
    private XUIViewerDefinitonParser oViewerParser;
    
    protected void initApplication() {

        // Create a component Store
        this.oComponentStore = new XUIComponentStore(  );
        
        // Parse Components
        oComponentParser = new XUIComponentParser();
        oComponentParser.loadComponents( this );
        
        // Register components into JSF
        if( log.isFinerEnabled() )
        	log.finer("Registering XUI Components Render Kit's definition...");
        
        registerRenderKits();
        registerComponentRenders();
        
        // Create the viewer Parser
        oViewerParser = new XUIViewerDefinitonParser();
        oViewerBuilder = new XUIViewerBuilder();
        
    }
    
    public XUIComponentStore getComponentStore() {
        return oComponentStore;
    }
    
    public XUIViewerBuilder getViewerBuilder() {
        return oViewerBuilder;
    }

    public XUIViewerDefinition getViewerDef( String sViewerName )
    {
        if( log.isFinerEnabled() )
        {
            log.finer("Parsing file " + sViewerName );
        }
        XUIViewerDefinition oViewerDef =  oViewerParser.parse( sViewerName );
        if( log.isFinerEnabled() )
        {
            log.finer("End file " + sViewerName + ".xml");
        }
        return oViewerDef;

    }
    
    public XUIViewerDefinition getViewerDef( InputStream inputStream )
    {
        if( log.isFinerEnabled() )
        {
            log.finer("Parsing file from InputStream" );
        }
        XUIViewerDefinition oViewerDef =  oViewerParser.parse( inputStream );
        if( log.isFinerEnabled() )
        {
            log.finer("End file parsing from InputStream");
        }
        return oViewerDef;
    }

    private synchronized void registerRenderKits() {
    	
    	String sRenderKits[] = oComponentStore.getRenderKits();
    	
    	for (int i = 0; i < sRenderKits.length; i++) {

    		String sRenderKitClassName = oComponentStore.getRenderKitClassName( sRenderKits[i] );
            try
            {
                Class      cRenderKit = null;
                RenderKit  oRenderKit = null;
                
                cRenderKit = Class.forName( sRenderKitClassName );
                oRenderKit = (RenderKit)cRenderKit.newInstance();
                
                //TODO: Register render with java server faces
                RenderKitFactory renderFactory = (RenderKitFactory)
                FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

                renderFactory.addRenderKit( sRenderKits[i], oRenderKit );
                oRenderKits.put( sRenderKits[i] , oRenderKit);
                
            }
            catch (Exception e)
            {
                log.warn("Error loading class "+ sRenderKitClassName +" for RenderKit " + sRenderKits[i] + ":" + e.getClass().getName() + "-" + e.getMessage() );
            }
		}
    }
    
    private synchronized void registerComponentRenders()
    {
        String sRenderClassName;

        RenderKit oRenderKit;
        
        FacesContext oFacesContext = FacesContext.getCurrentInstance();
        //String[] oAllComponents = oComponentStore.getComponentNames();

        Iterator<String> itAllRenderKits = oComponentStore.getRenderKitsList();
        
        while (itAllRenderKits.hasNext())
        {
        	String oNameRenderKit = itAllRenderKits.next();
        	Map<String,XUIRendererDefinition> current = oComponentStore.getMapOfRenderKit(oNameRenderKit);
        	Iterator<String> it = current.keySet().iterator();
        	
        	oRenderKit = oRenderKits.get( oNameRenderKit );
        	
    		while (it.hasNext())
        	{
        		String name = it.next();
        		XUIRendererDefinition oRenderDef = current.get(name);
        		sRenderClassName = oRenderDef.getClassName();
        		
        		try
                {
                    Class      oRenderClass     = null;
                    Renderer   oComponentRender = null;
                    String	   sFamilyName		= oRenderDef.getFamilyName();
                    String 	   sRendererType	= oRenderDef.getRendererType();
                    oRenderClass = Class.forName( sRenderClassName );
                    oComponentRender = (Renderer)oRenderClass.newInstance();
                    
                    oRenderKit.addRenderer( 
                    	sFamilyName, 
                    	sRendererType, 
                        oComponentRender
                    );
                }
                catch (Exception e)
                {
                	e.printStackTrace();
                    log.warn("Error loading class "+ sRenderClassName +" for component " + oRenderDef.getRendererType() + ":" + e.getClass().getName() + "-" + e.getMessage() );
                }
        		
        	}
        }
        
        /*for (int i = 0; i < oAllComponents.length; i++) 
        {

            // Register registered components render Kits
            XUIComponentDefinition oComponentDef = oComponentStore.getComponent( oAllComponents[i] );
            
            if( "outputText".equals( oComponentDef.getName() ) ) {
            	System.out.println( "todebug"  );
            }
            
            String[] sCompRenderKitTypes = oComponentDef.getRenderKitTypes();
                
            for (int j = 0; j < sCompRenderKitTypes.length; j++)  {
                
                
                oRenderKit = oRenderKits.get( sCompRenderKitTypes[j] );
                if( oRenderKit != null ){
                     
//                  oRenderKit = new XUIRenderKit();
                    
                    oRenderKits.put( sCompRenderKitTypes[j], oRenderKit );

                    sRenderClassName = oComponentDef.getRenderKitClassName( sCompRenderKitTypes[j] );

	                if ( log.isFinerEnabled() )
	                {
	                    log.finer("Loading class "+ sRenderClassName +" for component " + oAllComponents[i] );
	                }

	                try
	                {
	                    Class      oRenderClass     = null;
	                    Renderer   oComponentRender = null;
	                    
	                    oRenderClass = Class.forName( 
	                                                    sRenderClassName 
	                                                    );
	                    oComponentRender = (Renderer)oRenderClass.newInstance();
	                    
	                    //TODO: Register render with java server faces
	                    
	                    oRenderKit.addRenderer( 
	                        oComponentDef.getFamily(), 
	                        oComponentDef.getName(), 
	                        oComponentRender
	                    ); //FIXME: Estava aqui o getName() em vez do getFamily()
	                }
	                catch (Exception e)
	                {
	                    log.warn("Error loading class "+ sRenderClassName +" for component " + oAllComponents[i] + ":" + e.getClass().getName() + "-" + e.getMessage() );
	                }

                }
                else {
                    log.warn("Cannot register " + sCompRenderKitTypes[j] + " for component " + oAllComponents[i] + " render kit [" + sCompRenderKitTypes[j] + "] doesn't exist. "  );
                }
            }
        }*/

        // Register platform render Kits --
        // TODO: Paste this to XVWComponents;
        oRenderKit = oRenderKits.get( "XEOHTML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOV2" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOXML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XMLViewRootRenderer() );    
        }
    }
    
    private synchronized void registerComponentRenders2()
    {
        String sRenderClassName;

        RenderKit oRenderKit;
        
        FacesContext oFacesContext = FacesContext.getCurrentInstance();
        String[] oAllComponents = oComponentStore.getComponentNames();

        for (int i = 0; i < oAllComponents.length; i++) 
        {

            // Register registered components render Kits
            XUIComponentDefinition oComponentDef = oComponentStore.getComponent( oAllComponents[i] );
            
            
            if( "outputText".equals( oComponentDef.getName() ) ) {
            	System.out.println( "todebug"  );
            }
            
            String[] sCompRenderKitTypes = oComponentDef.getRenderKitTypes();
                
            for (int j = 0; j < sCompRenderKitTypes.length; j++)  {
                
                
                oRenderKit = oRenderKits.get( sCompRenderKitTypes[j] );
                if( oRenderKit != null ){
                     
//                  oRenderKit = new XUIRenderKit();
                    
                    oRenderKits.put( sCompRenderKitTypes[j], oRenderKit );

                    sRenderClassName = oComponentDef.getRenderKitClassName( sCompRenderKitTypes[j] );

	                if ( log.isFinerEnabled() )
	                {
	                    log.finer("Loading class "+ sRenderClassName +" for component " + oAllComponents[i] );
	                }

	                try
	                {
	                    Class      oRenderClass     = null;
	                    Renderer   oComponentRender = null;
	                    
	                    oRenderClass = Class.forName( 
	                                                    sRenderClassName 
	                                                    );
	                    oComponentRender = (Renderer)oRenderClass.newInstance();
	                    
	                    //TODO: Register render with java server faces
	                    
	                    oRenderKit.addRenderer( 
	                        oComponentDef.getName(), 
	                        oComponentDef.getName(), 
	                        oComponentRender
	                    ); //FIXME: Estava aqui o getName() em vez do getFamily()
	                }
	                catch (Exception e)
	                {
	                    log.warn("Error loading class "+ sRenderClassName +" for component " + oAllComponents[i] + ":" + e.getClass().getName() + "-" + e.getMessage() );
	                }

                }
                else {
                    log.warn("Cannot register " + sCompRenderKitTypes[j] + " for component " + oAllComponents[i] + " render kit [" + sCompRenderKitTypes[j] + "] doesn't exist. "  );
                }
            }
        }

        // Register platform render Kits --
        // TODO: Paste this to XVWComponents;
        oRenderKit = oRenderKits.get( "XEOHTML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOV2" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOXML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XMLViewRootRenderer() );    
        }
    }

}
