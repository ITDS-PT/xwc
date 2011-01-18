package netgest.bo.xwc.components.classic.renderers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import netgest.bo.xwc.components.classic.charts.Chart;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLChartRenderer extends XMLBasicRenderer {

	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
		super.encodeBegin( component );
		Chart 	chartComponent	= (Chart)component;
		String servlet = ComponentRenderUtils.getCompleteServletURL(getRequestContext(), component.getClientId());
		
		String tmpFolder = netgest.bo.impl.document.Ebo_DocumentImpl.getTempDir();
		if(tmpFolder.endsWith("\\") || tmpFolder.endsWith("/"))
			tmpFolder =  tmpFolder + System.currentTimeMillis() + File.separator;
		else
			tmpFolder =  tmpFolder + File.separator + System.currentTimeMillis();
		
		java.io.File tmpdir = new java.io.File(tmpFolder);
		if(!tmpdir.exists()) 
			tmpdir.mkdirs();
		
		String id = component.getClientId();
		id = id.replace(':','_');
		
		File tempFile = new File( tmpdir +File.separator + "chart" + id + ".png");
		FileOutputStream fos = new FileOutputStream(tempFile);
		chartComponent.outputChartAsImageToStream(fos,true);
		
		XUIResponseWriter rw = getResponseWriter();
		
		rw.writeAttribute("urlPdf",tempFile.getAbsolutePath(), null);
		rw.writeAttribute("urlHtml",servlet, null);
		
		
	}
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException 
	{
		super.encodeEnd(component);
	}
	
}
