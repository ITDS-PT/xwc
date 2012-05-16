package netgest.bo.xwc.components.viewers.beans;

import java.io.File;
import java.net.URL;

import netgest.bo.boConfig;
import netgest.bo.runtime.boObjectList;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.V2toV3ViewerConvert;
import netgest.bo.xwc.xeo.beans.ViewerConfig;
import netgest.bo.xwc.xeo.beans.XEOBaseList;
import netgest.bo.xwc.xeo.components.List;

import org.json.JSONObject;

public class MigrateXEOViewersToXWCBean extends XEOBaseList {
	
	private String logText="";
	
    public void migrateAndOpenLog()  throws Exception {
    	
    	this.logText = "";
    	V2toV3ViewerConvert converter = new V2toV3ViewerConvert();
        
        List list = (List) XUIRequestContext.
        	getCurrentContext().getEvent().getComponent().findParentComponent(List.class);
		
        DataRecordConnector[] selectedRows=list.getSelectedRows();
        
        //boConfig.getDeploymentDir()
        
        File rootDasArvores = new File(boConfig.getUiDefinitiondir());
        if (rootDasArvores .exists() && rootDasArvores.isDirectory())
    		logText+=rootDasArvores.getAbsolutePath()+" - OK\n";
        
        File rootDosPacotes = new File(boConfig.getDefinitiondir());
    	if (rootDosPacotes.exists() && rootDosPacotes.isDirectory())
    		logText+=rootDosPacotes.getAbsolutePath()+" - OK\n";
        
    	URL path = Thread.currentThread().getContextClassLoader().getResource("xeo.home");
		File file = new File(path.getFile());
    	File destinoDoXWC = new File(file.getParentFile().getParentFile().getParentFile()+File.separator+"viewers"+File.separator+"xwcconversion");

    	//Criar se não existir
    	if (!destinoDoXWC.getParentFile().exists()) 
    		destinoDoXWC.getParentFile().mkdir();
    	
    	//Criar se não existir
    	if (!destinoDoXWC.exists()) 
    		destinoDoXWC.mkdir();
    	
    	logText+=destinoDoXWC.getAbsolutePath()+" - OK\n";
        
    	converter.convertTrees(rootDasArvores.getAbsolutePath(),destinoDoXWC.getAbsolutePath());
    	
        for (int i=0;i<selectedRows.length;i++)
        {
        	//Chamada á conversão dos pacotes vai aqui bem como a afectação ao Texto do Log
        	//Este exemplo coloca no log os packages seleccionados
        	//logText+=selectedRows[i].getAttribute("name").getDisplayValue()+"\n";        	
     
        	converter.convertPackage(selectedRows[i].getAttribute("name").getDisplayValue(), rootDosPacotes.getAbsolutePath(), destinoDoXWC.getAbsolutePath());
        }
        logText+=converter.getLog();
    	openLog();
    	
    }
    
    public String getLogText()
    {
    	return this.logText;
    }
       
    public void openLog() throws Exception
    {
    	XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();

        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 

        oViewRoot = oSessionContext.createView( sViewerName );
        
        oRequestContext.setViewRoot( oViewRoot );
        oViewRoot.processInitComponents();     
    }
    
    public XEOObjectListConnector getDataList()
	{
    	boObjectList list=boObjectList.list(this.getEboContext(),
			"select Ebo_Package where deployed='1'");
    	return new XEOObjectListConnector(list);
	}

}
