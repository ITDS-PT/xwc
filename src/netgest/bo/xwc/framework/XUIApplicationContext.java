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

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.xwc.components.classic.renderers.XMLViewRootRenderer;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIComponentParser;
import netgest.bo.xwc.framework.def.XUIComponentStore;
import netgest.bo.xwc.framework.def.XUIRendererDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitonParser;
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
        	log.finer(MessageLocalizer.getMessage("REGISTERING_XUI_COMPONENTS_RENDER_KITS_DEFENITION"));
        
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
            log.finer(MessageLocalizer.getMessage("PARSING_FILE")+" " + sViewerName );
        }
        XUIViewerDefinition oViewerDef =  oViewerParser.parse( sViewerName );
        if( log.isFinerEnabled() )
        {
            log.finer(MessageLocalizer.getMessage("END_FILE")+" " + sViewerName + ".xml");
        }
        return oViewerDef;

    }
    
    public XUIViewerDefinition getViewerDef( InputStream inputStream )
    {
        if( log.isFinerEnabled() )
        {
            log.finer(MessageLocalizer.getMessage("PARSING_FILE_FROM_INPUTSTREAM") );
        }
        XUIViewerDefinition oViewerDef =  oViewerParser.parse( inputStream );
        if( log.isFinerEnabled() )
        {
            log.finer(MessageLocalizer.getMessage("END_PARSING_FILE_FROM_INPUTSTREAM"));
        }
        return oViewerDef;
    }

    private synchronized void registerRenderKits() {
    	
    	String sRenderKits[] = oComponentStore.getRenderKits();
    	
    	for (int i = 0; i < sRenderKits.length; i++) {

    		String sRenderKitClassName = oComponentStore.getRenderKitClassName( sRenderKits[i] );
            try
            {
                Class<?>      cRenderKit = null;
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
                log.warn(MessageLocalizer.getMessage("ERROR_LOADING_CLASS")+" "+ sRenderKitClassName +" "+MessageLocalizer.getMessage("FOR_RENDERKIT")+" " + sRenderKits[i] + ":" + e.getClass().getName() + "-" + e.getMessage() );
            }
		}
    }
    
    private synchronized void registerComponentRenders()
    {
        String sRenderClassName;

        RenderKit oRenderKit;
        

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
                    Class<?>      oRenderClass     = null;
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
                    log.warn(MessageLocalizer.getMessage("ERROR_LOADING_CLASS")+" "+ sRenderClassName +" "+MessageLocalizer.getMessage("FOR_COMPONENT")+" " + oRenderDef.getRendererType() + ":" + e.getClass().getName() + "-" + e.getMessage() );
                }
        		
        	}
        }
        
        oRenderKit = oRenderKits.get( "XEOHTML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOV2" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOJQUERY" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XUIViewRoot.XEOHTMLRenderer() );    
        }
        oRenderKit = oRenderKits.get( "XEOXML" );
        if( oRenderKit != null ) {
            oRenderKit.addRenderer( XUIViewRoot.class.getName(), XUIViewRoot.class.getName(), new XMLViewRootRenderer() );    
        }
    }
    

}
