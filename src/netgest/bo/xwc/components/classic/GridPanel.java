package netgest.bo.xwc.components.classic;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GridPanel extends ViewerInputSecurityBase {
    
//    private static final DataRecordConnector[] EMPTY_ARRAY_DATARECORDCONNECTOR = new DataRecordConnector[0];

	private String childViewers;
	
	public static final String SELECTION_ROW            = "ROW";
    public static final String SELECTION_MULTI_ROW      = "MULTI_ROW";
    public static final String SELECTION_CELL           = "CELL";

    private XUIBindProperty<DataListConnector>   
        dataSource          = new XUIBindProperty<DataListConnector>( "dataSource", this, DataListConnector.class );
    
    private XUIMethodBindProperty   
    	filterLookup          = new XUIMethodBindProperty( "filterLookup", this, "#{viewBean.lookupFilterObject}" );

    public  XUIStateBindProperty<String>                    
        rowSelectionMode    = new XUIStateBindProperty<String>( "rowSelectionMode", this, String.class );

	private XUIStateBindProperty<String>   
        objectAttribute     = new XUIStateBindProperty<String>( "objectAttribute", this, String.class );

    private XUIStateProperty<String>            
        rowUniqueIdentifier = new XUIStateProperty<String>( "rowUniqueIdentifier", this, "BOUI" );
    
    private XUIStateProperty<String>            
        autoExpandColumn = new XUIStateProperty<String>( "autoExpandColumn", this );
    
    private XUIBaseProperty<Boolean> forceColumnsFitWidth = new XUIBaseProperty<Boolean>( "forceColumnsFitWidth", this, true );
    
	private XUIStateProperty<String>
        pageSize = new XUIStateProperty<String>( "pageSize", this, "50" );

    private XUIBaseProperty<String>
    	rowDblClickTarget = new XUIBaseProperty<String>( "rowDblClickTarget", this, "tab" );

    private XUIBaseProperty<String>
		rowClickTarget = new XUIBaseProperty<String>( "rowClickTarget", this, "" );

    private XUIBaseProperty<String>
    	sActiveRow = new XUIBaseProperty<String>( "sActiveRow", this, "tab" );;

    private XUIBaseProperty<String> currentFilters = 
    		new XUIBaseProperty<String>( "currentFilters", this );

	private String[] sSelectedRowsUniqueIdentifiers;

    private transient   Column[]        oGridColumns;
    
	private XUIStateProperty<String> layout = new XUIStateProperty<String>( "layout", this, "fit-parent" );
    
    private XUIBaseProperty<String> height = 
			new XUIBaseProperty<String>( "height", this, "250" );
    
    private XUIBaseProperty<Boolean> autoHeight = 
		new XUIBaseProperty<Boolean>( "autoHeight", this, false );

    private XUIBaseProperty<Integer> minHeight = 
    	new XUIBaseProperty<Integer>("minHeight", this, 60 );
	
    private XUIStateBindProperty<String> groupBy = 
		new XUIStateBindProperty<String>( "groupBy", this, String.class );
    
    private XUIBindProperty<GridRowRenderClass> rowClass =
    	new XUIBindProperty<GridRowRenderClass>( "rowClass", this, GridRowRenderClass.class );
    
    private XUIBindProperty<Boolean> enableGroupBy =
    	new XUIBindProperty<Boolean>( "enableGroupBy", this, "false",Boolean.class );

    private XUIBindProperty<Boolean> enableColumnSort =
    	new XUIBindProperty<Boolean>( "enableColumnSort", this, "true",Boolean.class );

    private XUIBindProperty<Boolean> enableColumnFilter =
    	new XUIBindProperty<Boolean>( "enableColumnFilter", this, "true", Boolean.class );
    
    private XUIBindProperty<Boolean> enableColumnHide =
    	new XUIBindProperty<Boolean>( "enableColumnHide", this, "true", Boolean.class );
    	
    private XUIBindProperty<Boolean> enableColumnMove  =
    	new XUIBindProperty<Boolean>( "enableColumnMove", this, "true", Boolean.class );
    
    private XUIBindProperty<Boolean> enableColumnResize  =
    	new XUIBindProperty<Boolean>( "enableColumnResize", this, "true", Boolean.class );
    
    private XUIBindProperty<Boolean> enableHeaderMenu  =
    	new XUIBindProperty<Boolean>( "enableHeaderMenu", this, "true", Boolean.class );
    
    private XUIBaseProperty<String> currentSortTerms  =
    	new XUIBaseProperty<String>( "currentSortTerms", this, null );
    
    private String		currentFullTextSearch;
    
    private XUICommand  filterLookupCommand;
    private XUIInput  	filterLookupInput;
    
    public XUICommand getFilterLookupCommand() {
    	return this.filterLookupCommand;
    }
    
    public XUIInput	  getFilterLookupInput() {
    	return this.filterLookupInput;
    }
    
	@Override
	public boolean wasStateChanged() {
		return true;
	}

    public boolean getOnlyRefreshData() {
		return !super.wasStateChanged();
	}

	@Override
	public void restoreState(Object state) {
		super.restoreState(state);
		setRendered( true );
	}
    
    @Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}

    @Override
    public void preRender() {
        // per component inicializations.
    	
        if( findComponent( getId() + "_lookupCommand" ) == null ) {
        	filterLookupCommand = new XUICommand();
        	filterLookupCommand.setId( getId() + "_lookupCommand" );
        	filterLookupCommand.addActionListener(
        			new FilterLookupListener()
                );
            getChildren().add( filterLookupCommand );
        }
        else {
        	filterLookupCommand = (XUICommand)findComponent( getId() + "_lookupCommand" );
        }

        if( findComponent( getId() + "_lookupInput" ) == null ) {
        	filterLookupInput = new XUIInput();
        	filterLookupInput.setId( getId() + "_lookupInput" );
            getChildren().add( filterLookupInput );
        }
        else {
        	filterLookupInput = (XUIInput)findComponent( getId() + "_lookupInput" );
        }
        
		String viewerSecurityId = getInstanceId();
		if ( viewerSecurityId!=null ) {
			setViewerSecurityPermissions( "#{viewBean.viewerPermissions."+viewerSecurityId+"}" );    		
		}
    }
    
    public static class FilterLookupListener implements ActionListener {

		public void processAction(ActionEvent event) throws AbortProcessingException {
			
			XUICommand cmd = (XUICommand)event.getComponent();
			cmd.setValue( 
					((HttpServletRequest)cmd.getRequestContext().getRequest())
						.getParameter( cmd.getClientId() )
			);
			((GridPanel)cmd.getParent()).doFilterLookup();
		}
    }
    
    private void doFilterLookup() {
    	this.filterLookup.invoke();
    }

    public void setRowClass( String rowClassExpressionText ) {
    	this.rowClass.setExpressionText( rowClassExpressionText );
    }
    
    public GridRowRenderClass getRowClass() {
    	return this.rowClass.getEvaluatedValue();
    }
    
    public void setEnableGroupBy( String rowClassExpressionText ) {
    	this.enableGroupBy.setExpressionText( rowClassExpressionText );
    }
    
    public boolean getEnableGroupBy() {
    	return this.enableGroupBy.getEvaluatedValue();
    }

    public void setEnableColumnSort( String rowClassExpressionText ) {
    	this.enableColumnSort.setExpressionText( rowClassExpressionText );
    }
    
    public boolean getEnableColumnSort() {
    	return this.enableColumnSort.getEvaluatedValue();
    }

    public void setEnableColumnFilter( String rowClassExpressionText ) {
    	this.enableColumnFilter.setExpressionText( rowClassExpressionText );
    }
    
    public boolean getEnableColumnFilter() {
    	return this.enableColumnFilter.getEvaluatedValue();
    }

    public void setEnableColumnHide( String sExpressionText ) {
    	this.enableColumnHide.setExpressionText( sExpressionText );
    }
    
    public boolean getEnableColumnHide() {
    	return this.enableColumnHide.getEvaluatedValue();
    }
    
    public void setEnableColumnMove( String sExpressionText ) {
    	this.enableColumnMove.setExpressionText( sExpressionText );
    }

    public boolean getEnableColumnMove() {
    	return this.enableColumnMove.getEvaluatedValue();
    }
    
    public boolean getEnableColumnResize() {
    	return this.enableColumnResize.getEvaluatedValue();
    }
    public void setEnableColumnResize( String sExpressionText ) {
    	this.enableColumnResize.setExpressionText( sExpressionText );
    }
    
    public boolean getEnableHeaderMenu() {
    	return this.enableHeaderMenu.getEvaluatedValue();
    }
    public void setEnableHeaderMenu( String sExpressionText ) {
    	this.enableHeaderMenu.setExpressionText( sExpressionText );
    }
    
	public String getCurrentFilters() {
		return currentFilters.getValue();
	}

	public void setCurrentFilters(String currentFilters) {
		this.currentFilters.setValue( currentFilters );
	}
	
	public FilterTerms getCurrentFilterTerms() {
		FilterTerms terms = null;
		
		String sCFilter = this.getCurrentFilters();

		try {
			JSONObject jFilters = new JSONObject( sCFilter );
			String[] names = JSONObject.getNames( jFilters );
			if( names != null ) {
				for( String name : names ) {
					
					JSONObject jsonColDef = jFilters.getJSONObject( name );
					JSONArray  jsonColFilters = jsonColDef.getJSONArray( "filters" );

					String submitedType  = jsonColDef.getString("type");
					
					boolean active		 = jsonColDef.getBoolean( "active" );
					
					for ( int i=0;active && i < jsonColFilters.length(); i++ ) {
						
						boolean bAddCodition = true;
						
						JSONObject jsonColFilter = jsonColFilters.getJSONObject( i );
						
						String submitedValue = jsonColFilter.optString("value");
						
						if( submitedValue != null ) {
			    			Object 	value = null;
			    			Byte	operator = null;
			    			
			    			if( "object".equals( submitedType ) ) {
			    				List<String> valuesList = new ArrayList<String>();

			    				JSONArray jArray = jsonColFilter.optJSONArray( "value" );
			    				if( jArray != null ) {
				    				for( int z=0; z < jArray.length(); z++ ) {
				    					valuesList.add( jArray.getString( z ) );
				    				}
				    				value		= valuesList.toArray();
				    				operator 	= FilterTerms.OPERATOR_IN;
			    				}
			    				if( valuesList.size() == 0 ) {
			    					bAddCodition = false;
			    				}
			    				
			    			} else if( "list".equals( submitedType ) ) {
			    				List<String> valuesList = new ArrayList<String>();
			    				JSONArray jArray = jsonColFilter.getJSONArray( "value" );
			    				
			    				for( int z=0; z < jArray.length(); z++ ) {
			    					valuesList.add( jArray.getString( z ) );
			    				}
			    				value		= valuesList.toArray();
			    				operator 	= FilterTerms.OPERATOR_IN;

			    				if( valuesList.size() == 0 ) {
			    					bAddCodition = false;
			    				}
			    				
			    				
			    			} else if( "string".equals( submitedType ) ) {
			    				value 		= submitedValue;
			    				operator 	= FilterTerms.OPERATOR_CONTAINS;
			    			}
			    			else if ( "date".equals( submitedType ) ) {
			    				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    				try {
									value = sdf.parse( submitedValue );
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									value = null;
								}
			    				String comp  = jsonColFilter.getString("comparison");
			    				if( "lt".equals( comp ) )  
			    					operator 	= FilterTerms.OPERATOR_LESS_THAN;
			    				else if( "eq".equals( comp ) ) 
									operator 	= FilterTerms.OPERATOR_EQUAL;
			    				else
			    					operator 	= FilterTerms.OPERATOR_GREATER_THAN;
			    			}
			    			else if ("boolean".equals( submitedType )) {
			    				value = Boolean.valueOf( submitedValue );
			    				operator 	= FilterTerms.OPERATOR_EQUAL;
			    			}
			    			else if ("numeric".equals( submitedType )) {
			    				String comp  = jsonColFilter.getString("comparison");
			    				value = new BigDecimal( submitedValue );
			    				if( "lt".equals( comp ) )  
			    					operator 	= FilterTerms.OPERATOR_LESS_THAN;
			    				else if( "eq".equals( comp ) ) 
									operator 	= FilterTerms.OPERATOR_EQUAL;
			    				else
			    					operator 	= FilterTerms.OPERATOR_GREATER_THAN;
			    			}
			    			else {
			    				value = null;
			    			}
			    			
			    			if( bAddCodition ) {
			        			if( terms == null ) { 
			            			terms = 
			            				new FilterTerms( new FilterTerm( name, operator, value ) );
			        			}
			        			else {
			        				terms.addTerm( FilterTerms.JOIN_AND, name, operator, value );
			        			}
			    			}
						}
					}
				}
			}
		} catch (JSONException e) {
			// Error reading filters....
			e.printStackTrace();
		}
		return terms;
	}
    
    public void setDataSource(String dataSource) {
        this.dataSource.setValue( createValueExpression( dataSource, DataListConnector.class ) );
    }
    
	public String getLayout() {
		return layout.getValue();
	}

	public void setLayout( String layoutMan ) {
		this.layout.setValue( layoutMan );
	}

	public String getHeight() {
		return height.getValue();
	}

	public void setHeight( String height ) {
		this.height.setValue( height );
	}

	public boolean getAutoHeight() {
		return autoHeight.getValue();
	}

	public void setAutoHeight( String booleanAutoHeight ) {
		this.autoHeight.setValue( Boolean.parseBoolean( booleanAutoHeight ) );
	}
	
	public int getMinHeight() {
		return this.minHeight.getValue();
	}

	public void setMinHeight(String minHeight) {
		this.minHeight.setValue( Integer.parseInt( minHeight ) );
	}

	public void setMinHeight(int minHeight) {
		this.minHeight.setValue( minHeight );
	}

	public void setGroupBy( String groupByExpr ) {
		this.groupBy.setExpressionText( groupByExpr );	
	}
	
	public String getGroupBy() {
		return this.groupBy.getEvaluatedValue();
	}

    public DataListConnector getDataSource() {
    	try {
	        if ( dataSource.getValue() != null ) {
	             return (DataListConnector)dataSource.getValue().getValue( getELContext() );
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
        return null;
    }

    public void setObjectAttribute(String sObjectAttribute ) {
        this.objectAttribute.setValue( createValueExpression( sObjectAttribute, String.class ) );
        this.setDataSource( "#{viewBean.currentData." + getObjectAttribute() + ".dataList}" );
        this.setRowClass( "#{viewBean.rowClass}" );
        this.setOnRowDoubleClick( "#{viewBean.editBridge}" );
        this.setRowDblClickTarget("self");
    }
    
    public String getObjectAttribute(  ) {
        if ( this.objectAttribute.getValue().isLiteralText() ) {
            return String.valueOf( this.objectAttribute.getValue().getExpressionString() );
        }
        return (String)this.objectAttribute.getValue().getValue( getELContext() );
    }
    
    
    public String getRowDblClickTarget() {
		return rowDblClickTarget.getValue();
	}

	public void setRowDblClickTarget(String rowDblClickTarget) {
		this.rowDblClickTarget.setValue( rowDblClickTarget );
	}

    public String getRowClickTarget() {
		return rowClickTarget.getValue();
	}

	public void setRowClickTarget(String rowDblClickTarget) {
		this.rowClickTarget.setValue( rowDblClickTarget );
	}
	
    public String[] getDataColumns() {
        Column[]        oColumns;
        String          sDataColumn;
        List<String>    dataColumns;
        boolean         bAddUniqueIdentifier = true;
        
        dataColumns = new ArrayList<String>(  );
        oColumns = getColumns();
        for (int i = 0; i < oColumns.length; i++) {
            sDataColumn = oColumns[ i ].getDataField();
            dataColumns.add( sDataColumn );
            if( getRowUniqueIdentifier().equals( sDataColumn ) ) {
                bAddUniqueIdentifier = false;
            }
        }
        if( bAddUniqueIdentifier ) {
            dataColumns.add( getRowUniqueIdentifier() );
        }
        return dataColumns.toArray( new String[ dataColumns.size() ] );

    }

    public Column getColumn( String dataFieldName ) {
        Column ret = null;
        if( dataFieldName != null ) {
            Column[] columns = getColumns();
            for( Column column : columns ) {
                if( dataFieldName.equals( column.getDataField() ) ) {
                    ret = column;
                    break;
                }
            }
        }
        return ret;
    }

    public Column[] getColumns() {
        Iterator<UIComponent>   oChildrenIt;
        Iterator<UIComponent>   oColumnsIt;
        Columns                 oColumns;
        
        if( oGridColumns == null )
        {
            oChildrenIt = getChildren().iterator();
            while( oChildrenIt.hasNext() ) {
                
                UIComponent oKid = oChildrenIt.next();
                if( oKid instanceof Columns ) {
                    oColumns = (Columns)oKid;
                    
                    ArrayList<Column> oRetColumns;
                    
                    oRetColumns = new ArrayList<Column>();
                    oColumnsIt = oColumns.getChildren().iterator();
                    
                    for ( ;oColumnsIt.hasNext() ; )  {
                        oRetColumns.add( (Column)oColumnsIt.next() );
                        
                    }
                    oGridColumns = oRetColumns.toArray( new Column[ oRetColumns.size() ] );
                    break;
                }
            }
        }
        if( oGridColumns == null ) {
            oGridColumns = new Column[0];
        }
        return oGridColumns;
        
    }

    public void setRowSelectionMode(String rowSelectionMode) {
        this.rowSelectionMode.setValue( createValueExpression( rowSelectionMode, String.class ) );
    }

    public String getRowSelectionMode() {
        if( this.rowSelectionMode.getValue() != null ) {
            ValueExpression oValExpr = this.rowSelectionMode.getValue();
            if( oValExpr.isLiteralText() ) {
                return oValExpr.getExpressionString();
            }
            else {
                return (String)oValExpr.getValue( getELContext() );
            }
        }
        
        return SELECTION_ROW;
    }

    public DataRecordConnector getCurrentSelectedRow() {
        return null;
    }

    public void setRowUniqueIdentifier(String sRowIdentifier) {
        this.rowUniqueIdentifier.setValue( sRowIdentifier );
    }

    public String getRowUniqueIdentifier() {
        return rowUniqueIdentifier.getValue();
    }

    public void setSelectedRowsByIdentifier(String[] sSelectedRowsUIndetifiers) {
        this.sSelectedRowsUniqueIdentifiers = sSelectedRowsUIndetifiers;
    }
    
    public void setActiveRowByIdentifier( String rowIdentifier ) {
        this.sActiveRow.setValue( rowIdentifier );
    }

    public String getActiveRowByIdentifier() {
        return this.sActiveRow.getValue();
    }

    private String[] getSelectedRowsIdentifiers() {
        return sSelectedRowsUniqueIdentifiers;
    }
    
    public DataRecordConnector getActiveRow() {

    	if( this.sActiveRow != null ) {
        	return getDataSource().findByUniqueIdentifier( this.getActiveRowByIdentifier() );
        }
        return null;
        
    }
    
    public DataRecordConnector[] getSelectedRows() {
        String sUniqueIdentifier;
        DataRecordConnector oCurrentRecord;

        List<DataRecordConnector>   oRetSelectRows = new ArrayList<DataRecordConnector>();
        
        if( this.getSelectedRowsIdentifiers() != null )
        {
            sUniqueIdentifier = getRowUniqueIdentifier();
            
            for (int i = 0; i < sSelectedRowsUniqueIdentifiers.length; i++) {
				sUniqueIdentifier = sSelectedRowsUniqueIdentifiers[i];
	        	oCurrentRecord = getDataSource().findByUniqueIdentifier( sUniqueIdentifier );
	        	if( oCurrentRecord != null ) {
	        		oRetSelectRows.add( oCurrentRecord );
	        	}
			}
        }
        return oRetSelectRows.toArray( new DataRecordConnector[ oRetSelectRows.size() ] );
    }

    public void setOnRowClick(String onRowDoubleClick) {
        XUICommand oRowClickComp = (XUICommand)this.findComponent( getId() + "_rowClick" );
        
        if( oRowClickComp == null ) {
            oRowClickComp = new XUICommand();
            oRowClickComp.setId( getId() + "_rowClick" );
            this.getChildren().add( oRowClickComp );
        }
        if( onRowDoubleClick == null || onRowDoubleClick.trim().length() == 0 ) {
        	getChildren().remove( oRowClickComp );
        } else {
        	oRowClickComp.setActionExpression( createMethodBinding( onRowDoubleClick ) );
        }
    }

    public String getOnRowClick() {
        XUICommand oRowClickComp = (XUICommand)this.findComponent( getId() + "_rowClick" );
        if( oRowClickComp != null ) {
            MethodExpression oMethodExpression = oRowClickComp.getActionExpression();
            return oMethodExpression!=null?oMethodExpression.getExpressionString():null;
        }
        return null;
    }
    
    public void setOnRowDoubleClick(String onRowDoubleClick) {
        XUICommand oRowDblClickComp = (XUICommand)this.findComponent( getId() + "_rowDblClick" );
        
        if( oRowDblClickComp == null ) {
            oRowDblClickComp = new XUICommand();
            oRowDblClickComp.setId( getId() + "_rowDblClick" );
            this.getChildren().add( oRowDblClickComp );
        }
        if( onRowDoubleClick == null || onRowDoubleClick.trim().length() == 0 ) {
        	getChildren().remove( oRowDblClickComp );
        } else {
        	oRowDblClickComp.setActionExpression( createMethodBinding( onRowDoubleClick ) );
        }
    }

    public String getOnRowDoubleClick() {
        XUICommand oRowDblClickComp = (XUICommand)this.findComponent( getId() + "_rowDblClick" );
        if( oRowDblClickComp != null ) {
            MethodExpression oMethodExpression = oRowDblClickComp.getActionExpression();
            return oMethodExpression!=null?oMethodExpression.getExpressionString():null;
        }
        return null;
    }

    public void setAutoExpandColumn(String autoExpandColumn) {
        this.autoExpandColumn.setValue( autoExpandColumn );
    }

    public String getAutoExpandColumn() {
        return autoExpandColumn.getValue();
    }

    public boolean getForceColumnsFitWidth() {
		return forceColumnsFitWidth.getValue();
	}

	public void setForceColumnsFitWidth( String forceColumnsFitWidth) {
		this.forceColumnsFitWidth.setValue( Boolean.valueOf( forceColumnsFitWidth ) );
	}
    
    public void setPageSize(String pageSize) {
        this.pageSize.setValue( pageSize );
    }

    public String getPageSize() {
        return pageSize.getValue();
    }
    
    public static final String getColumnLabel(  DataListConnector dataList, Column col  ) {
        String label = col.getLabel();
        if( label == null ) {
        	 DataFieldMetaData fm = dataList.getAttributeMetaData( col.getDataField() );
        	 if( fm != null ) {
        		 label = fm.getLabel();
        	 }
        }
        return label;
    }
    
    //
    // Methods from SecurableComponent
    //

	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.GRID;
	}

	public String getViewerSecurityId() {
		String securityId = null;
 		if (getId()!=null && getId().length()>0) {
 			securityId = getId();
 		}
		return securityId;
	}

	public String getViewerSecurityLabel() {
		String label = getViewerSecurityComponentType().toString();
		if ( getViewerSecurityId()!=null ) {
			label += " "+ getViewerSecurityId();
		}
		return label; 
	}

	public boolean isContainer() {
		return false;
	}

	public String getChildViewers() {
		return this.childViewers;
	}
	
	public void setChildViewers( String childViewers ) {
		this.childViewers = childViewers;
	}
	
	public void setCurrentSortTerms( String sortQuery ) {
		this.currentSortTerms.setValue( sortQuery );
	}
	
	public SortTerms getCurrentSortTerms() {
		String sSort = this.currentSortTerms.getValue();
		
		SortTerms st = null;
		if( sSort != null ) {
			String[] sSortDef = sSort.split("\\|");
			if( sSortDef.length == 2 ) {
				String sSortField = sSortDef[0]; 
				String sSortDir  = sSortDef[1];
		    	st = new SortTerms();
		    	st.addSortTerm( sSortField , "DESC".equals( sSortDir )?SortTerms.SORT_DESC:SortTerms.SORT_ASC );
			}
		}
		return st;
	}
	
    public String getCurrentFullTextSearch() {
		return this.currentFullTextSearch;
	}

	public void setCurrentFullTextSearch(String fullTextSearch) {
		this.currentFullTextSearch = fullTextSearch;
	}
	
    public void applyFilters( DataListConnector listConnector ) {
		if( (listConnector.dataListCapabilities() & DataListConnector.CAP_FILTER) > 0 ) {
	    	FilterTerms filterTerms = getCurrentFilterTerms();
	    	listConnector.setFilterTerms( filterTerms );
		}
    }
    
    public void applySort( DataListConnector listConnector ) {
		if( (listConnector.dataListCapabilities() & DataListConnector.CAP_SORT) > 0 ) {
			SortTerms sortTerms = getCurrentSortTerms();
			if( sortTerms != null )
				listConnector.setSortTerms( sortTerms );
			else
				listConnector.setSortTerms( SortTerms.EMPTY_SORT_TERMS );
		}
    }
    
    public void applyFullTextSearch( DataListConnector listConnector ) {
		if( (listConnector.dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH ) > 0 ) {
			String fullTextSearch = getCurrentFullTextSearch();
			listConnector.setSearchText( fullTextSearch );
		}
    }
    
    public Iterator<DataRecordConnector> applyLocalSort( Iterator<DataRecordConnector> dataListIterator ) {
    	
    	
        SortTerms sortTerms = getCurrentSortTerms();
        
        if( sortTerms != null && !sortTerms.isEmpty() ) {
        	
        	List<DataRecordConnector> orderedList = new ArrayList<DataRecordConnector>();
            while( dataListIterator.hasNext() ) { 
            	orderedList.add( dataListIterator.next() );
            }

            SortTerm term = sortTerms.iterator().next();
            
            final String sSort  = term.getField();
            final int direction = term.getDirection();
        	
            Collections.sort( orderedList, new Comparator<DataRecordConnector>() {
                public int compare( DataRecordConnector left, DataRecordConnector right )  {
                    Comparable<Comparable> sLeft, sRight;

                    DataFieldConnector leftField = left.getAttribute( sSort );
                    byte fieldType 				 = leftField.getDataType();
                    
                    if ( fieldType == DataFieldTypes.VALUE_DATE || 
                    	 fieldType == DataFieldTypes.VALUE_DATETIME ||
                    	 fieldType == DataFieldTypes.VALUE_NUMBER ) 
                    {
                    	
                        sLeft = (Comparable)leftField.getValue();
                        sRight = (Comparable)right.getAttribute( sSort ).getValue();
                        
                    } else {
                    	
                        sLeft = (Comparable)leftField.getDisplayValue();
                        sRight = (Comparable)right.getAttribute( sSort ).getDisplayValue();
                        
                    }
                    
                    if( sLeft == null || sRight == null ) {
                    	return sLeft==null?1:-1;
                    }
                    
                    int ret = direction==SortTerms.SORT_ASC?sLeft.compareTo( sRight ):sRight.compareTo( sLeft );
                    return ret;
                }
                }
            );
            dataListIterator = orderedList.iterator();
        }
    	return dataListIterator;
    }

    public Iterator<DataRecordConnector> applyLocalFilter( Iterator<DataRecordConnector> iterator ) {
        
    	List<DataRecordConnector> finalList = new ArrayList<DataRecordConnector>();
        
    	Iterator<FilterTerms.FilterJoin> it;
    	
    	FilterTerms filterTerms;
    	
    	filterTerms = getCurrentFilterTerms();
    	
        if ( filterTerms==null ) {
            return iterator;
        }
        
        
        try {
			while( iterator.hasNext() ) {
			    DataRecordConnector dataRecordConnector = iterator.next();        
			    it = filterTerms.iterator();
			    boolean addLine = true;
			    while( it.hasNext() ) {
			        FilterTerms.FilterJoin filterJoin = it.next();
			        // TODO Parse filterJoin
			        FilterTerm filterTerm =  filterJoin.getTerm();
			        Object val = filterTerm.getValue();
			        String column = filterTerm.getDataField();
			        if( val != null ) {
			            if( val instanceof String ) {
			                String sVal = val==null?"":val.toString().toUpperCase();
			                String sDisplayValue = dataRecordConnector.getAttribute( column ).getDisplayValue();
			                String sColumnValue = sDisplayValue==null?"":sDisplayValue.toUpperCase();
			                if ( filterTerm.getOperator()==FilterTerms.OPERATOR_CONTAINS ) {
			                    if ( !sColumnValue.contains(sVal) ) {
			                        addLine = false;
			                    }
			                } else if ( filterTerm.getOperator()==FilterTerms.OPERATOR_NOT_CONTAINS ) {
			                    if ( sColumnValue.contains(sVal) ) {
			                        addLine = false;
			                    }
			                } else {
			                    System.out.println( "Local Filter: Unsupported String filter" );
			                }
			        } else if ( val instanceof java.util.Date ) {
			                Date dVal = (Date)val;
			                Date dColumnValue = (Date)dataRecordConnector.getAttribute( column ).getValue();
			                if ( filterTerm.getOperator()==FilterTerms.OPERATOR_EQUAL ) {
			                    if ( dVal.compareTo(dColumnValue)!=0 ) {
			                        addLine = false;
			                    }
			                } else if ( filterTerm.getOperator()==FilterTerms.OPERATOR_GREATER_THAN ) {
			                    if ( dVal.compareTo(dColumnValue)>=0 ) {
			                        addLine = false;
			                    }
			                } else if ( filterTerm.getOperator()==FilterTerms.OPERATOR_LESS_THAN ) {
			                    if ( dVal.compareTo(dColumnValue)<=0 ) {
			                        addLine = false;
			                    }
			                } else {
			                    System.out.println( "Local Filter: Unsupported Date filter" );
			                }                        
			        } else if ( val instanceof Boolean ) {
			                // Only supports OPERATOR_EQUAL
			                String sVal = ((Boolean)val).booleanValue()?"1":"0";
			                String sColumnValue = (String)dataRecordConnector.getAttribute( column ).getValue();
			                if ( !sVal.equals(sColumnValue) ) {
			                    addLine = false;
			                }
			        } else if ( val instanceof BigDecimal ) {
			                BigDecimal nVal = (BigDecimal)val;
			                BigDecimal nColumnValue = (BigDecimal)dataRecordConnector.getAttribute( column ).getValue();
			                if ( filterTerm.getOperator()==FilterTerms.OPERATOR_EQUAL ) {
			                    if ( nVal.compareTo(nColumnValue)!=0 ) {
			                        addLine = false;
			                    }
			                } else if ( filterTerm.getOperator()==FilterTerms.OPERATOR_GREATER_THAN ) {
			                    if ( nVal.compareTo(nColumnValue)>=0 ) {
			                        addLine = false;
			                    }
			                } else if ( filterTerm.getOperator()==FilterTerms.OPERATOR_LESS_THAN ) {
			                    if ( nVal.compareTo(nColumnValue)<=0 ) {
			                        addLine = false;
			                    }
			                } else {
			                    System.out.println( "Local Filter: Unsupported BigDecimal filter" );
			                }
			        } else if ( val instanceof Object[] ) {
			            BigDecimal nColumnValue = (BigDecimal)dataRecordConnector.getAttribute( column ).getValue();
			            Set<BigDecimal> bouis = new HashSet<BigDecimal>();
			            Object[] aVals = (Object[])val;
			            for (int i = 0; i<aVals.length; i++) {
			                bouis.add( new BigDecimal(aVals[i].toString()) );
			            }
			            
			            if ( !bouis.contains(nColumnValue) ) {
			                addLine = false;
			            }
			        }
			        
			        if ( addLine ) {
			            finalList.add( dataRecordConnector );
			        }
			        
			        }
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return finalList.iterator();
    }
    
}


