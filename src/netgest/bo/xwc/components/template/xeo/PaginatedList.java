package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;
import java.util.Enumeration;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;


public abstract class PaginatedList extends XUIComponentBase {
	
	private XUIViewStateBindProperty<String> page = 
			new XUIViewStateBindProperty<String>("page", this,"1", String.class );
	
	private XUIViewStateBindProperty<String> pagesize = 
			new XUIViewStateBindProperty<String>("pagesize", this,"30", String.class );
	
	@Values({ "default","ajax"})
	private XUIViewBindProperty<String> navigation = 
			new XUIViewBindProperty<String>("navigation", this,"default", String.class );
	
	@RequiredAlways
	private XUIViewBindProperty<String> name = 
			new XUIViewBindProperty<String>("name", this, String.class );
	
	private XUICommand nextPageCommand;
	private XUICommand previousPageCommand;
	private XUICommand firstPageCommand;
	private XUICommand lastPageCommand;
	private XUICommand pagesizeCommand;

	@Override
	public void initComponent() {
		//initConnector();
		nextPageCommand = new XUICommand();
		String id=getId()+"_nextpage";
		nextPageCommand.setId( id );
		nextPageCommand.addActionListener( 
                new GotoPageListener()
            );
        getChildren().add( nextPageCommand );

		previousPageCommand = new XUICommand();
		id=getId()+"_previouspage";
		previousPageCommand.setId( id );
		previousPageCommand.addActionListener( 
                new GotoPageListener()
            );
        getChildren().add( previousPageCommand );

		firstPageCommand = new XUICommand();
		id=getId()+"_firstpage";	
		firstPageCommand.setId( id );
		firstPageCommand.addActionListener( 
                new GotoPageListener()
            );
        getChildren().add( firstPageCommand );
        
		lastPageCommand = new XUICommand();
		id=getId()+"_lastpage";
		lastPageCommand.setId( id );
		lastPageCommand.addActionListener( 
                new GotoPageListener()
            );
        getChildren().add( lastPageCommand );
        
        pagesizeCommand = new XUICommand();
		id=getId()+"_pagesize";
		pagesizeCommand.setId( id );
		pagesizeCommand.addActionListener( 
                new PageSizeListener()
            );
        getChildren().add( pagesizeCommand );
        
		super.initComponent();
	}

	public void setName( String name ) {
		this.name.setExpressionText(name);
	}
	
	public String getName() {
		return this.name.getEvaluatedValue();
	}
	
	public void setNavigation( String navigation ) {
		this.navigation.setExpressionText(navigation);
	}
	
	public String getNavigation() {
		return this.navigation.getEvaluatedValue();
	}
	
	public void setPage( String page ) {
		this.page.setExpressionText(page);
	}
	
	public String getPage() {
		return this.page.getEvaluatedValue();
	}
	
	public void setPagesize( String pagesize ) {
		this.pagesize.setExpressionText(pagesize);
	}
	
	public String getPagesize() {
		return this.pagesize.getEvaluatedValue();
	}
	
	public abstract void initConnector();
	
	public abstract int getPages();
	
	public abstract int getRecordCount();
	
	public String getGotopage(int pageNumber) {	
		if (pageNumber>=1 && pageNumber<=getPages()) {
			if (this.getNavigation().equals("ajax"))
				return XVWScripts.getAjaxCommandScript(this.nextPageCommand,Integer.toString(pageNumber),
						XVWScripts.WAIT_STATUS_MESSAGE);
			else if (this.getNavigation().equals("default"))
				return getPageUrl("page",new Integer(pageNumber).toString());
			else 
				return null;
		}
		else
			return "";
		
	}
	
	
	private String getPageUrl(String param,String pageNumber) {
		String pageUrl="";
		String paramName=this.getName()+param;
		HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();

		pageUrl=request.getContextPath()+request.getServletPath();
		String queryString="?";
				
		//Put all parameters except the new parameter to be generated and page parameter if pagesize is requested
		Enumeration<String> en=request.getParameterNames();
		while (en.hasMoreElements()) {
			String pName=en.nextElement();
			if (!pName.equals(paramName) && !(param.equals("pagesize") && 
					!pName.equals(this.getName()+"page")) && 
					!StringUtils.isEmpty(request.getParameter(pName)))
				queryString+=pName+"="+request.getParameter(pName)+"&";
		}
		//put the generated parameter
		queryString+=paramName+"="+pageNumber+"&";
		
		if (param.equals("pagesize")) {
			queryString+=this.getName()+page+"=1&";
			
		}	
		queryString=queryString.substring(0,queryString.length()-1);
		
		return pageUrl+queryString;
	}
	
	public String getNext() {
		String pageNumber=null;
		
		int currPage = new Integer(this.getPage()).intValue();
		int nextPage = currPage+1;		
		
		if (nextPage<=getPages())
			pageNumber = Integer.toString(nextPage);
		
		if (pageNumber==null)
			return "";
		else
			if (this.getNavigation().equals("ajax"))
				return XVWScripts.getAjaxCommandScript(this.nextPageCommand,pageNumber,XVWScripts.WAIT_STATUS_MESSAGE);
			else if (this.getNavigation().equals("default"))
				return getPageUrl("page",pageNumber);
			else 
				return null;
		
	}
	
	public String getPrevious() {
		String pageNumber=null;
		
		int currPage = new Integer(this.getPage()).intValue();
		int previousPage = currPage-1;		
		if (previousPage>0)
			pageNumber = Integer.toString(previousPage);
		
		if (pageNumber==null)
			return "";
		else
			if (this.getNavigation().equals("ajax"))
				return XVWScripts.getAjaxCommandScript(this.previousPageCommand,pageNumber,XVWScripts.WAIT_STATUS_MESSAGE);
			else if (this.getNavigation().equals("default"))
				return getPageUrl("page",pageNumber);
			else 
				return null;
	}
	
	public String getApplypagesize(int pagesize) {
		
		if (this.getNavigation().equals("ajax"))
			return XVWScripts.getAjaxCommandScript(this.pagesizeCommand,String.valueOf(pagesize),XVWScripts.WAIT_STATUS_MESSAGE);
		else if (this.getNavigation().equals("default"))
			return getPageUrl("pagesize",String.valueOf(pagesize));
		else 
			return null;
	}
	
	public String getLast() {
		if (this.getNavigation().equals("ajax"))
			return XVWScripts.getAjaxCommandScript(this.lastPageCommand,Integer.toString(this.getPages())
				,XVWScripts.WAIT_STATUS_MESSAGE);
		else if (this.getNavigation().equals("default"))
			return getPageUrl("page",Integer.toString(this.getPages()));
		else 
			return null;
	}
	
	public String getFirst() {
		if (this.getNavigation().equals("ajax"))
			return XVWScripts.getAjaxCommandScript(this.firstPageCommand,"1",XVWScripts.WAIT_STATUS_MESSAGE);
		else if (this.getNavigation().equals("default"))
			return getPageUrl("page","1");
		else 
			return null;
	}
	
	@Override
	public void preRender() {
		//initConnector();
        nextPageCommand = (XUICommand)getChild( 0 );
        previousPageCommand = (XUICommand)getChild( 1 );
        firstPageCommand = (XUICommand)getChild( 2 );
        lastPageCommand = (XUICommand)getChild( 3 );
        pagesizeCommand = (XUICommand)getChild( 4 );
        
        if (this.getNavigation().equals("default")) {
			HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();
			String pagenumber=request.getParameter(this.getName()+"page");
			if (pagenumber
				!=null && !pagenumber.equals("")) {
				//Validar se a p�gina passada pode ser mostrada
				//Pesado pois implica duas queries (uma para inicializar o connector e ir buscar o n�mero de p�ginas, a outra ap�s 
				//se fazer set da p�gina e refrescar)
//				int pageInt=1;
//				try {
//					pageInt=Integer.parseInt(pagenumber);
//				}
//				catch (NumberFormatException e) {
//					
//				}
//				if (pageInt<=this.getPages()) {
//					this.setPage(Integer.toString(pageInt));
//					initConnector();
//				}
				this.setPage(pagenumber);
			}
			String pagesize=request.getParameter(this.getName()+"pagesize");
			if (pagesize
				!=null && !pagesize.equals("")) {
//				int pageSizeInt=Integer.parseInt(this.getPagesize());
//				try {					
//					pageSizeInt=Integer.parseInt(pagesize);
//				}
//				catch (NumberFormatException e) {
//					
//				}
//				if (pageSizeInt<=this.getRecordCount()) {
//					this.setPagesize(Integer.toString(pageSizeInt));
//					initConnector();
//				}
				this.setPagesize(pagesize);
			}
		}
        initConnector();
	}
	
	public static class GotoPageListener implements ActionListener {

		public void processAction(ActionEvent event)
				throws AbortProcessingException {
			XUICommand command=(XUICommand)event.getComponent();
			String pagenumber=(String)command.getCommandArgument();
			PaginatedList list=(PaginatedList)event.getComponent().getParent();
			list.setPage(pagenumber);
		}
	}
	
	public static class PageSizeListener implements ActionListener {

		public void processAction(ActionEvent event)
				throws AbortProcessingException {
			XUICommand command=(XUICommand)event.getComponent();
			String pagesize=(String)command.getCommandArgument();
			PaginatedList list=(PaginatedList)event.getComponent().getParent();
			list.setPagesize(pagesize);
			list.setPage("1");
		}
	}
	
	public static class ListRenderer extends TemplateRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {			
			XUIResponseWriter w = getResponseWriter();
			PaginatedList oComp = (PaginatedList)component;
			//oComp.initConnector();
			w.startElement("div");
			w.writeAttribute("id", oComp.getClientId());
			
//			if (oComp.getNavigation().equals("default")) {
//				HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();
//				String pagenumber=request.getParameter(oComp.getName()+"page");
//				if (pagenumber
//					!=null && !pagenumber.equals(""))
//						oComp.setPage(pagenumber);
//				
//				String pagesize=request.getParameter(oComp.getName()+"pagesize");
//				if (pagesize
//					!=null && !pagesize.equals(""))
//					oComp.setPagesize(pagesize);
//			}
//			else if (oComp.getNavigation().equals("ajax")) {
				
				//TESTES PARA MANTER CONTEXTO QUANDO É FEITO PEDIDO POR POST (A IMPLEMENTAR)
//				w.startElement("input");
//				w.writeAttribute("type","hidden");
//				w.writeAttribute("name", oComp.getName()+"page");
//				System.out.println(oComp.page.getEvaluatedValue());
//			w.writeAttribute("value", oComp.page.getEvaluatedValue());
//				w.endElement("input");
//				w.startElement("input");
//				w.writeAttribute("type","hidden");
//				w.writeAttribute("name", oComp.getName()+"pagesize");
//				w.writeAttribute("value", oComp.pagesize.getEvaluatedValue());
//				w.endElement("input");
				
//			}
			super.encodeBegin(component);
			w.endElement("div");
		}
	}
		
}
