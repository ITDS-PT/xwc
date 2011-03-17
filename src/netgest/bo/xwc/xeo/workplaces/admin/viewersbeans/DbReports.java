package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import netgest.bo.builder.boBuildRepository;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.report.BDHTMLBuilder;
import netgest.bo.report.TableComparator;
import netgest.bo.report.XMLObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Tabs;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class DbReports extends XEOBaseBean {
	
	private String packageReport = null;
	private String objectReport = null;

	public String getPackageReport() {
		return packageReport;
	}

	public String getObjectReport() {
		return objectReport;
	}

	private String getSelected(String gridPanelId, String attName) {
		GridPanel gp = (GridPanel) XUIRequestContext.
		getCurrentContext().getEvent().getComponent().findComponent("form:"+gridPanelId);
		DataRecordConnector selectedRow = gp.getActiveRow();

		return selectedRow == null ? null :  selectedRow.getAttribute(attName).getDisplayValue();
	}

	private String getActiveReport() {
		Tabs tabs = (Tabs) getViewRoot().findComponent( Tabs.class );
		return tabs.getActiveTab();
	}

	public void newReport() throws boRuntimeException {
		String activeReport = getActiveReport();
		if (activeReport.equals("package"))
			this.packageReport = null;
		else if (activeReport.equals("object"))
			this.objectReport = null;

	}

	public void createReport() throws boRuntimeException {
		String activeReport = getActiveReport();
		if (activeReport.equals("package"))
			createPackageReport();
		else if (activeReport.equals("object"))
			createObjectReport();
	}

	public Boolean getHasReport() {
		String activeReport = getActiveReport();
		if (activeReport.equals("package"))
			return getHasPackageReport();
		else if (activeReport.equals("object"))
			return getHasObjectReport();
		return null;
	}

	private void createPackageReport() throws boRuntimeException {
		String selected = this.getSelected("packageList","name");

		if (selected!=null) {			
			this.packageReport = createHtmlReport(selected,null);
		}
	}

	private void createObjectReport() throws boRuntimeException {
		String selected = this.getSelected("objectList","name");
		String packageName = this.getSelected("objectList","xeopackage.name");

		if (selected!=null) {
			this.objectReport = createHtmlReport(packageName,selected); 
		}
	}

	public boolean getHasPackageReport() {
		return this.packageReport == null ? false : true;
	}

	public boolean getHasObjectReport() {
		return this.objectReport == null ? false : true;
	}

	public DataListConnector getPackages()  {
		String boQl = "select Ebo_Package order by  name";

		return  new XEOObjectListConnector( 
				boObjectList.list(getEboContext(),boQl)
		);
	}

	public DataListConnector getObjects()  {
		String boQl = "select Ebo_ClsReg where deployed=\'1\' order by name";

		return  new XEOObjectListConnector( 
				boObjectList.list(getEboContext(),boQl)
		);
	}

	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();	
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
			}
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.getViewRoot().setTransient( true );
		oRequestContext.renderResponse();
	}

	public String createHtmlReport(String packageName, String objName) throws boRuntimeException
	{	
		String toRet = null;
		try
		{
			boBuildRepository repository = new boBuildRepository(getEboContext().getBoSession().getRepository());
			boRepository rep = boRepository.getDefaultRepository( boApplication.getApplicationFromConfig("XEO") );
			boBuildRepository brep = new boBuildRepository( rep );

			boDefHandler defs[] = boBuilder.listUndeployedDefinitions( brep, null );
			ArrayList toReport = new ArrayList();            


			File[] xfiles = repository.getXMLFilesFromDefinition();

			if ((packageName != null) && !"".equals(packageName))
			{
				out:
				for (int i = 0; i < defs.length; i++)
				{
					if (xfiles[i].getName().toLowerCase().endsWith(".xeomodel"))
					{
						int pos = xfiles[i].getName().indexOf(".xeomodel");
						if (xfiles[i].getAbsolutePath().indexOf(packageName) != -1)
						{
							for (int x = 0; x < defs.length; x++) 
							{
								if(defs[x].getName().equals(xfiles[i].getName().substring(0, pos)))
								{
									if (objName==null)
										toReport.add(defs[x]);
									else if (objName!=null && defs[x].getName().equals(objName)) {
										toReport.add(defs[x]);
										//break out;
									}
										
								}
							}
						}
					}
				}
			}
			else
			{
				toReport.addAll( Arrays.asList( defs ) );
			}

			toRet = createHtmlReport(toReport);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return MessageLocalizer.getMessage("COULD_NOT_GENERATE_THE_REPORT_ERROR")+": " +
			e.getLocalizedMessage() + ".";
		}

		return toRet;
	}

	private String createHtmlReport(ArrayList files) throws Exception
	{
		boDefHandler bodef;
		ArrayList<XMLObject> objList = new ArrayList<XMLObject>();
		XMLObject xmlOb;

		for (int i = 0; i < files.size(); i++)
		{
			bodef = (boDefHandler)files.get( i );

			if ((bodef.getClassType() != boDefHandler.TYPE_ABSTRACT_CLASS) &&
					(bodef.getClassType() != boDefHandler.TYPE_INTERFACE))
			{
				xmlOb = new XMLObject(getEboContext().getConnectionData());
				xmlOb.setXMLObject(getEboContext(), bodef);
				objList.add(xmlOb);
			}
		}

		//ordenar a list pelas nome das tabelas
		Collections.sort(objList, new TableComparator());
		giveCap(objList);

		//TODO change 
		BDHTMLBuilder bdHtml = new BDHTMLBuilder(objList,"Main_admin.xvw");
		String codeHtml = bdHtml.generate();

		return codeHtml;
	}

	private void giveCap(ArrayList<XMLObject> arr)
	{
		String lastTable = null;
		int lastCap = 1;

		for (int i = 0; i < arr.size(); i++)
		{
			if ((lastTable == null) ||
					!((XMLObject) arr.get(i)).getTableName().equals(lastTable))
			{
				((XMLObject) arr.get(i)).setCap(getCap(lastCap));
				((XMLObject) arr.get(i)).setAnchor(getAnchor(lastCap));
				lastTable = ((XMLObject) arr.get(i)).getTableName();
				lastCap++;
			}
			else
			{
				((XMLObject) arr.get(i)).setAnchor(getAnchor(lastCap));
			}
		}
	}

	private static String getCap(int i)
	{
		if (i < 10)
		{
			return "000" + i;
		}

		if (i < 100)
		{
			return "00" + i;
		}

		if (i < 1000)
		{
			return "0" + i;
		}

		return String.valueOf(i);
	}

	private static String getAnchor(int i)
	{
		return "a" + String.valueOf(i);
	}


}
