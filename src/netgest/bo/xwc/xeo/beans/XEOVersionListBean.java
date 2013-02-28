package netgest.bo.xwc.xeo.beans;

import java.io.StringWriter;
import java.util.Date;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.Logger;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.localization.XUILocalizationUtils;
import netgest.bo.xwc.xeo.components.FormEdit;
import netgest.bo.xwc.xeo.components.utils.XEOListVersionHelper;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.MainAdminBeanMessages;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

/**
 * 
 * This Bean is used to support the List of Versions viewer of a given XEO object
 * 
 * It contains the methods required to show a list of versions of the given object
 * (if there are any) and the ability to compare different versions
 * 
 * @author Pedro Pereira
 *
 */
public class XEOVersionListBean extends XEOEditBean
{
	
	
	private static final Logger logger = Logger.getLogger(XEOVersionListBean.class);
	
	public void showDifference() {
		
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/ShowVersionDifferences.xvw");
        ShowVersionDifferenceBean bean = (ShowVersionDifferenceBean) oViewRoot.getBean( "viewBean" );
        
        Object value = ((XUICommand)oRequestContext.getEvent().getSource()).getCommandArgument();
        long 			bouiVersion = Long.valueOf(value.toString());
        
        boSession 		session = getXEOObject().getEboContext().getBoSession();
        XMLDocument viewToShowDiff = getViewerContentAsXML(getRequestContext().getViewRoot().getParentView());
        EboContext		newContext = session.createRequestContextInServlet(null,null,null);
        
    	String 			result = XEOListVersionHelper.renderDifferencesWithPreviousVersion(getXEOObject(),bouiVersion,viewToShowDiff,newContext);
    	
    	bean.setDifferences(result);
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
		
	}
	
	public void showLogs(){
		
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/ShowLogsVersion.xvw");
        ShowLogsVersionBean bean = (ShowLogsVersionBean) oViewRoot.getBean( "viewBean" );
        
        Object value = ((XUICommand)oRequestContext.getEvent().getSource()).getCommandArgument();
        long 			bouiVersion = Long.valueOf(value.toString());
        
        if (bouiVersion > 0 ){
	        String 			result = XEOListVersionHelper.getListOfLogsObject(bouiVersion, getXEOObject());
	    	bean.setDifferences(result);
        } else 
        	bean.setDifferences("Cannot display");
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
		
	}
	
	/**
     * 
     * Retrieves the content of a viewer as XML
     * 
     * @return A {@link XMLDocument} with a viewer converted to XML
     */
    private XMLDocument getViewerContentAsXML(XUIViewRoot root)
    {
    	XUIRequestContext r = XUIRequestContext.getCurrentContext();
    	XMLDocument doc;
    	
		try 
		{
			String s =  r.getSessionContext().renderViewToBuffer("XEOXML", root.getViewState() ).toString();
			doc = ngtXMLUtils.loadXML(s);
			return doc;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
    	
    }
	
	/**
	 * 
	 * Returns the title message of the window
	 * 
	 * @return A message for the window title
	 */
	public String getTitleMessage()
	{
		return MainAdminBeanMessages.LIST_OF_VERSIONS.toString(); 
	}
	
	
	/**
	 * 
	 * Builds a table with the list of versions of the current instance, the table
	 * has columns such as version number, cardId, creation date, creator, and button to
	 * open a new window to compare with a specific version
	 * 
	 * @return An HTML table with the list of versions or a string
	 * 
	 */
	public String getListOfVersions()
	{
		getRequestContext().getScriptContext().addInclude(
				XUIScriptContext.POSITION_FOOTER, 
				"versionListScript", 
				"ext-xeo/js/versionList.js");
		
		
		boObject currentObject = getXEOObject();
    	boObjectList list = boObjectList.list(getEboContext(), "select Ebo_Versioning where CHANGEDOBJECT = "
    			+ currentObject.getBoui() + " ORDER BY version ASC");
    	
    	StringWriter writer = new StringWriter();
		XUIResponseWriter w = new XUIResponseWriter(writer, "text/html", "UTF-8");
    	
		XUIViewRoot viewRoot = getRequestContext().getViewRoot();
		String url = getRequestContext().getAjaxURL();
		if (url.indexOf('?') == -1)
			{ url += '?'; }
		else
			{ url += '&'; }
		
		url += "javax.faces.ViewState=" + getRequestContext().getViewRoot().getViewState();
		String cliendID = ((FormEdit)viewRoot.findComponent(FormEdit.class)).getClientId();
		url += "&xvw.servlet="+ cliendID;
		

		 try 
		 {
    	
			w.startDocument();
			w.startElement(HTMLTag.DIV, null);
				w.writeAttribute("class", "body", null);
			w.startElement(HTMLTag.TABLE, null);
				w.writeAttribute(HTMLAttr.CLASS, "relations", null);
					w.startElement(HTMLTag.TR, null);
						w.startElement(HTMLTag.TH, null);
							w.write(BeansMessages.VERSION_NUMBER.toString());
						w.endElement(HTMLTag.TH);
						w.startElement(HTMLTag.TH, null);
							w.write(BeansMessages.CREATION_DATE.toString());
						w.endElement(HTMLTag.TH);
						w.startElement(HTMLTag.TH, null);
							w.write(BeansMessages.CREATOR_USER.toString());
						w.endElement(HTMLTag.TH);
						w.startElement(HTMLTag.TH, null);
							w.write("");
						w.endElement(HTMLTag.TH);
						w.startElement(HTMLTag.TH, null);
							w.write("");
						w.endElement(HTMLTag.TH);
					w.endElement(HTMLTag.TR);
			
			if (list.getRecordCount() > 0 )
	    	{
	    		list.beforeFirst();
	    		while (list.next())
	    		{
	    				w.startElement(HTMLTag.TR, null);
	    			
		    			 //Fetch the version object
		    			 boObject currentObjectVersion = list.getObject();
		    			 //Create the logs from that version object
		    			
		    			 long version = currentObjectVersion.
		    			 	getAttribute("version").getValueLong();
		    			 
		    			 Date createDate = currentObjectVersion.
		    			 	getAttribute("SYS_DTCREATE").getValueDate();
		    			 String createDateString = "";
		    			 if (createDate != null)
		    			 	 createDateString = XUILocalizationUtils.dateTimeToString( createDate );
		    			 		
		    			 String creatorUser = currentObjectVersion.getAttribute("CREATOR").getValueString();
		    			 if (creatorUser != null)
		    			 {
		    				boObject creatorObject = boObject.getBoManager().loadObject(getEboContext(),Long.parseLong(creatorUser));
		    				creatorUser = creatorObject.getAttribute("username").getValueString();
		    			 }
		    			 
		    			 
		    			 w.startElement(HTMLTag.TD, null);
							w.write(String.valueOf(version));
						 w.endElement(HTMLTag.TD);
						 
						 w.startElement(HTMLTag.TD, null);
							w.write(String.valueOf(createDateString));
						 w.endElement(HTMLTag.TD);
						 
						 w.startElement(HTMLTag.TD, null);
							w.write(String.valueOf(creatorUser));
						 w.endElement(HTMLTag.TD);
						 
						 w.startElement(HTMLTag.TD, null);
							w.write(String.valueOf("<input type='radio' name='versionItem' value='"+
									currentObjectVersion.getBoui()+"' onClick='XVW.checkButtons(document.listVersionForm.versionItem);' />"));
						 w.endElement(HTMLTag.TD);
						 
						 w.startElement(HTMLTag.TD, null);
						 
						 
						 
						w.write(String.valueOf("<a class='logs' onClick=\"javascript:XVW.openLogWindow('"+currentObjectVersion.getBoui()+ "','"+
									BeansMessages.LBL_WND_SHOW_LOGS.toString()
									+"')\">"
									+BeansMessages.SHOW_LOGS.toString() +" </a>"));
						 w.endElement(HTMLTag.TD);
		    			 
						 w.endElement(HTMLTag.TR);
						 
				}
	    		
	    		w.endElement(HTMLTag.TABLE);
	    		
	    		//Build the URL for the edit Servlet
	    		String openDifferenceWindowScript = "XVW.openDiffWindow(document.listVersionForm.versionItem);";
	    		
				//Write the buttons
	    		/*w.write("<input type='button' id='compareCurrent' 	" +
	    				"value='"+BeansMessages.LBL_BTN_CMP_ACTUAL.toString()+"' 		disabled='true' 	" +
	    				"onClick=\"javascript:openDiffWindow('"+url+"',document.listVersionForm.versionItem,'"+
	    				BeansMessages.LBL_WND_SHOW_DIFF.toString()
	    				+"');\" />");*/
	    		
	    		w.write("<input type='button' id='compareCurrent' 	" +
	    				"value='"+BeansMessages.LBL_BTN_CMP_ACTUAL.toString()+"' disabled='true' " +
	    				"onClick=\""+openDifferenceWindowScript+";\" />");
	    		
	    		w.endElement(HTMLTag.DIV);
			
	    	}
	    	else
	    	{
	    		if (hasVersioning(currentObject.getBoDefinition()))
	    			return BeansMessages.NO_VERSIONS_EXIST.toString();
	    		else
	    			return BeansMessages.VERSIONING_NOT_ACTIVE.toString();
	    	}
	    	
			w.endDocument();
			w.close();
			return writer.toString();
		
		 } 
		 catch (Exception e) 
		 {
			logger.warn(e);
			return "";
		 }
			
	}
	
	private boolean hasVersioning(boDefHandler objectMetadata){
		return objectMetadata.getVersioning();
	}
	
	
	
	
}
