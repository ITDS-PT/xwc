package netgest.bo.xwc.components.viewers.beans;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseList;


public class LoadViewerBean {
	
	
	public void openViewer()
	{		
		XUIRequestContext r = XUIRequestContext.getCurrentContext();	
		EboContext ctx=boApplication.currentContext().getEboContext();
		
		String actionUrl=ctx.getRequest().getParameter("viewer"); 
    	boolean oldViewerFormat=new Boolean(ctx.getRequest().getParameter("oldviewer")).booleanValue();    	   
    	XUIViewRoot v = r.getSessionContext().createView(actionUrl);
    	
    	try
    	{
	    	XEOBaseBean bean=(XEOBaseBean) v.getBean("viewBean");
    		String currObject="";
    		if (oldViewerFormat)
    		{
    			if (actionUrl.indexOf("/")>-1)
    			{
    				//Não irá funcionar em situações em que o viewer
    				//terá como novo <objecto>_edit_<maisqqcoisa>
    				currObject=actionUrl.substring(actionUrl.lastIndexOf("/"),actionUrl.length());
    				currObject=currObject.substring(0,currObject.lastIndexOf("_"));
    			}
    			else
    			{
    				currObject=actionUrl.substring(0,actionUrl.lastIndexOf("_"));
    			}
    		}
    		else if (!oldViewerFormat && actionUrl.indexOf("/")>-1)
    			currObject=actionUrl.substring(0,actionUrl.indexOf("/"));

	    	if (bean instanceof XEOBaseList)
	    	{
	    		
	    		String boql="select "+currObject+" where 1=1";    
	    		XEOBaseList viewer=(XEOBaseList)bean;
	    		viewer.executeBoql(boql);    		    		
	    	}
	    	else if (bean instanceof XEOEditBean)
	    	{
	    		XEOEditBean viewer=(XEOEditBean)bean;
	    		viewer.createNew(currObject);   		 
	    	}
    	}
    	catch (ClassCastException e)
    	{
    		//other beans
    	}
    	r.setViewRoot( v );
    	v.processInitComponents();
    	r.renderResponse();
	}
	
	
}
