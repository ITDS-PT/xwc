package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.connectors.DataGroupConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.data.JavaScriptArrayProvider;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseList;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelRenderer extends XUIRenderer implements XUIRendererServlet {
    

	GridPanelExtJsRenderer extRenderer;
	
    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        super.encodeBegin(component);
        extRenderer = new GridPanelExtJsRenderer();
    	extRenderer.encodeBegin( component );
    }

    @Override
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
    	extRenderer.encodeEnd( oComp );
    	super.encodeEnd( oComp );
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void decode(XUIComponentBase oComp) {
        GridPanel oGridComp;
        String    sGridSelectedIds;
        
        oGridComp = (GridPanel)oComp;
        
        sGridSelectedIds = oComp.getRequestContext().getRequestParameterMap().get( oComp.getClientId() +"_srs" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 )
        {
            oGridComp.setSelectedRowsByIdentifier( sGridSelectedIds.split("\\|") );
        }
        
        sGridSelectedIds = oComp.getRequestContext().getRequestParameterMap().get( oComp.getClientId() +"_act" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 )
        {
            oGridComp.setActiveRowByIdentifier( sGridSelectedIds );
        }
    }

    @Override
    public void encodeChildren(XUIComponentBase oComp) throws IOException {
    	this.extRenderer.encodeChildren( oComp );
    }
    
	public ExtConfig getExtJsConfig(XUIComponentBase comp) throws IOException {
		if( this.extRenderer == null ) 
			this.extRenderer = new GridPanelExtJsRenderer();
		
		return this.extRenderer.extEncodeAll( comp );
	}
    
    
	public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp ) throws IOException
    {
        GridPanel oGrid;
        GridPanelRequestParameters reqParam;

        oGrid = (GridPanel)oComp;
        
        reqParam = decodeServiceParmeters( oGrid, (HttpServletRequest)oRequest );
        
        
        String sType = oRequest.getParameter("type");
        if( "pdf".equalsIgnoreCase( sType ) ) {
        	GridPanelPDFRenderer pdfRender = new GridPanelPDFRenderer();
        	pdfRender.render( oRequest , oResponse, oGrid );
        }
        else if ( "excel".equalsIgnoreCase( sType ) ) {
        	GridPanelExcelRenderer excelRenderer = new GridPanelExcelRenderer();
        	excelRenderer.getExcel( oRequest, oResponse, oGrid );
        }
        else if ( oGrid.getGroupBy() == null ) {
        	oGrid.setGroupBy( null );
        	GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
        	jsonRenderer.getJsonData(oRequest, oResponse, reqParam, oGrid);

        }
        else if ( reqParam.getGroupByValue() != null ) {
        	oResponse.setContentType( "text/plain;charset=utf-8" );
        	DataListConnector c = oGrid.getDataSource();
        	
        	oGrid.applyFilters( c );
        	oGrid.applySort( c );
        	oGrid.applyFullTextSearch( c );
        	
        	c.setGroupBy( new String[] { oGrid.getGroupBy() } );
        	DataListConnector groupDetails = 
        		c.getGroupDetails(1, 
        				new String[] { oGrid.getGroupBy() }, 
        				new Object[] { reqParam.getGroupByValue() }, 
        				reqParam.getPage(), 
        				reqParam.getPageSize()
        			);

        	GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
            
            StringBuilder oStrBldr = jsonRenderer.buildDataArray( 
            		oGrid, 
            		groupDetails, 
            		groupDetails.iterator(), 
            		0, 
            		reqParam.getPageSize() 
            	);
            oResponse.getWriter().print( oStrBldr );
        	
        }
        else if ( oGrid.getGroupBy() != null ) {
        	oResponse.setContentType( "text/plain;charset=utf-8" );
            Map<String,GridColumnRenderer> columnRenderer = new HashMap<String,GridColumnRenderer>();
            Column[] oAttributeColumns = oGrid.getColumns();
            for( Column gridCol : oAttributeColumns ) {
                    if( gridCol != null && gridCol instanceof ColumnAttribute ) {
                            GridColumnRenderer r = ((ColumnAttribute)gridCol).getRenderer();
                            if( r != null ) {
                                    columnRenderer.put( gridCol.getDataField(), r );
                            }
                    }
            }
        	oGrid.setGroupBy( oGrid.getGroupBy() );
        	DataListConnector c = oGrid.getDataSource();
        	c.setGroupBy( new String[] { oGrid.getGroupBy() } );
        	DataGroupConnector groupConnector = c.getGroups( 1, null, null, reqParam.getPage(), reqParam.getPageSize() );
            StringBuilder s = new StringBuilder(200);
            s.append( '{' );
            s.append( oGrid.getId() ); 
            s.append( ":" );
            JavaScriptArrayProvider oJsArrayProvider = new JavaScriptArrayProvider( 
            		groupConnector.iterator(),
            		new String[] { oGrid.getGroupBy(), oGrid.getGroupBy() + "__value", oGrid.getGroupBy() + "__count" },
            		0,
            		50
            	);
            oJsArrayProvider.getJSONArray( s, oGrid, oGrid.getGroupBy(), null, columnRenderer );
            s.append(",totalCount:").append( groupConnector.getRecordCount() );
            s.append('}');
        	oResponse.getWriter().print( s );
        }
    }
	
	public GridPanelRequestParameters decodeServiceParmeters( GridPanel oGridPanel, HttpServletRequest oRequest ) {
		
		// Paramterers at gridLevel
		String selectedRows = oRequest.getParameter( "selectedRows" );
        String activeRow 	= oRequest.getParameter( "activeRow" );
        String groupBy 		= oRequest.getParameter( "groupBy" );
		
        oGridPanel.setGroupBy( groupBy );
        oGridPanel.setActiveRowByIdentifier( activeRow );
        if( selectedRows != null && selectedRows.length() > 0 ) {
        	oGridPanel.setSelectedRowsByIdentifier( selectedRows.split("\\|"));
        }
        
        // Parameters at requestLevel
		String groupValue   = oRequest.getParameter( "groupValue" );
        String sStart = oRequest.getParameter("start");
        String sLimit = oRequest.getParameter("limit");
        
        int start = Integer.parseInt( sStart!=null&&sStart.length()>0?sStart : "0" );
        int limit = Integer.parseInt( sLimit!=null&&sLimit.length()>0?sLimit : oGridPanel.getPageSize() );
        
        int page = 1;
        if( start > 0 && start % limit == 0 )
            page = (start / limit) + 1;
        else
        	page = (start / limit) + 1;
		
		GridPanelRequestParameters reqParam;
		reqParam = new GridPanelRequestParameters();

		reqParam.setGroupByValue( groupValue );
		reqParam.setPage( page );
		reqParam.setPageSize( limit );
		reqParam.setStart( start );
		reqParam.setLimit( limit );
        
        String sFullText    = oRequest.getParameter("fullText");
        oGridPanel.setCurrentFullTextSearch( sFullText );
        
        String rSort 		= oRequest.getParameter("sort");
        if( rSort != null ) {
        	rSort = rSort.replaceAll("__",".");
        }

        String sSort	    = rSort + "|" + oRequest.getParameter("dir");
        
        // Sort Terms
        if( rSort != null ) {
        	if( sSort.length() > 0 )
        		oGridPanel.setCurrentSortTerms( sSort );
        	else 
        		oGridPanel.setCurrentSortTerms( null );
        }
        
        // Filter terms
        String sFilters = oRequest.getParameter("filters");
		String sServerFilters = oGridPanel.getCurrentFilters();
        if( sFilters != null ) {
	        try {
				// Fix something in json filters.
	        	// BUG... when setting a value to a objecto, client doesn't send that! so, it must be
	        	// merged with server side.
	        	
	        	// System.out.println( "REQ:" + sFilters );
	        	
	    		
	        	JSONObject jFilters;
	    		JSONObject serverFilters;
	    		
				if( sFilters != null ) {
					jFilters = new JSONObject( sFilters );
				} else {
					jFilters = new JSONObject( "{}" );
				}
				
				if( sServerFilters != null ) {
					serverFilters = new JSONObject( sServerFilters );
				}
				else {
					serverFilters  = new JSONObject( "{}" );
				}
				
	    		String[] names = JSONObject.getNames( jFilters );
	    		if( names != null ) {
	        		for( String name : names ) {
	        			JSONObject serverFilter = serverFilters.optJSONObject( name );
	        			JSONObject clientFilter = jFilters.getJSONObject(name);
        				if( serverFilter != null && clientFilter.opt("value") != null ) {
        					serverFilter.put( "active", clientFilter.getBoolean("active") );
        					// Filter's in objects can't be readed from the client
        					// because it clears all other searchs
        					//if( !"object".equals( clientFilter.getString("type") ) ) {
        						serverFilter.put( "value", clientFilter.get("value") );
        					//}
        					jFilters.put( name, serverFilter );
	        			}
	        		}
	    		}
	    		oGridPanel.setCurrentFilters( jFilters.toString() );
			} catch (JSONException e) {
				// Do nothing.. ignore filters...
				e.printStackTrace();
			}
        }
		return reqParam;
	}
	
	public static String getExportTitle( GridPanel oGrid ) {
    	XUIViewRoot oViewRoot = XUIRequestContext.getCurrentContext().getViewRoot();
    	Object viewBean = oViewRoot.getBean("viewBean");
    	String sTitle;
    	if( viewBean instanceof XEOEditBean  ) {
    		sTitle = ((XEOEditBean)viewBean).getTitle();
    	} else if( viewBean instanceof XEOBaseList ) {
    		sTitle = ((XEOBaseList)viewBean).getTitle();
    	}
    	else {
    		sTitle = "Listagem";
    	}
    	Tab t = (Tab)oGrid.findParentComponent( Tab.class );
    	if( t != null ) {
    		sTitle += " - " + t.getLabel();
    	}
    	sTitle = sTitle.trim();
    	sTitle = sTitle.replaceAll( "<[a-zA-Z\\/][^>]*>", "");
		sTitle = HTMLEntityDecoder.htmlEntityToChar( sTitle );  
    	return sTitle;
	}

}
