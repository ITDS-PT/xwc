package netgest.bo.xwc.framework.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XUIApplicationLoader 
{

    private Log log = LogFactory.getLog( netgest.bo.xwc.framework.jsf.XUIApplicationLoader.class );

/*
    // Registered Components
    private XUIComponentStore oXVWComponents    = null;
    
    private Hashtable<String, XUIRenderKit> oRenderKits   = new Hashtable<String, XUIRenderKit>();

    public XUIApplicationLoader() {
        
        log.debug("Starting new XUI Application...");
        
    }

    public XUIComponentStore getComponents()
    {
        return oXVWComponents;
    }
    
    public void init()
    {
        loadComponents();
    }
    
    private void loadComponents()
    {

        if( oXVWComponents == null ){
            
            log.debug("Reading XUI Components definition...");
            XUIComponentParser cparser = new XUIComponentParser();
            oXVWComponents = cparser.parse();

            log.debug("Registering XUI Components Render Kit's definition...");
            registerComponentRenderKits();
            
        }
        
    }
    
    public XUIViewerDefinition getViewerDef( String sViewerName )
    {
        XUIViewerDefinitonParser vparser = new XUIViewerDefinitonParser();

        if( log.isDebugEnabled() )
        {
            log.debug("Parsing file " + sViewerName + ".xml");
        }
        
        XUIViewerDefinition oViewerDef =  vparser.parse( sViewerName + ".xml" );        

        if( log.isDebugEnabled() )
        {
            log.debug("End file " + sViewerName + ".xml");
        }
        
        return oViewerDef;

    }
*/
    
}