package netgest.bo.xwc.xeo.components;

import java.sql.Connection;
import java.sql.SQLException;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.xeo.components.utils.CardIdLinkRenderer;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovColumnNameExtractor;
import netgest.utils.StringUtils;



public class ColumnAttribute extends netgest.bo.xwc.components.classic.ColumnAttribute {
	
	private XUICommand cardIdCommand;
	private static String castType=null;

	public XUICommand getCardIdCommand(){
		return cardIdCommand;
	}
	
	@Override
	public void initComponent(){
		if (findComponent(getClientId() +"_cardIdLink") == null){
			XUICommand cmd = new XUICommand();
    		cmd.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".openCardIdLink}") );
    		cmd.setId(getId() + "_cardIdLink");
    		this.cardIdCommand = cmd;
    		getChildren().add( cmd );
		}
		
		
		if (attributeIsLov()){
			GridPanel grid = (GridPanel) findParentComponent( GridPanel.class );
			if (grid != null){
				DataListConnector listConnector = grid.getDataSource();
				DataFieldMetaData metadata = listConnector.getAttributeMetaData( getDataField() ); 
				if (metadata != null && metadata.getIsLov() ){
					String label = metadata.getLabel();
					setLabel( label );
				}
			}
			setDataField( new LovColumnNameExtractor( getDataField() ).prefixColumnName() );
		}
	}
	
	
	
	/**
     * Whether to enable cardid links on this column or not
     */
    private XUIViewProperty<Boolean> enableCardIdLink = 
    	new XUIViewProperty<Boolean>( "enableCardIdLink", this, false );
    
    
    public boolean getEnableCardIdLink() {
		return this.enableCardIdLink.getValue().booleanValue();
	}
	
	public void setEnableCardIdLink(String isCardLinkActive){
		this.enableCardIdLink.setValue(Boolean.valueOf(isCardLinkActive));
	}
    
	/**
	 *  Whether the column is Frozen or not
	 */
	private XUIViewProperty<Boolean> frozen = 
			new XUIViewProperty<Boolean>( "frozen", this, false );
	
	public boolean getIsFrozen() {
		return this.frozen.getValue().booleanValue();
	}
	
	public void setFrozen(String isFrozen){
		this.frozen.setValue(Boolean.valueOf(isFrozen));
	}
	
	@Override
	public String getRendererType() {
		return "columnAttribute";
	}
	
	public void setObjectAttribute( String objectAttribute ) {
		super.setDataField( objectAttribute );
	}
	
	public String getObjectAttribute() {
		return super.getDataField();
	}
	
	public GridColumnRenderer getRenderer() {
		GridColumnRenderer renderer = super.getRenderer();
		if (renderer == null){
			if (getEnableCardIdLink()){
				return new CardIdLinkRenderer(this);
			}
		}
		return renderer;
		
	}

	@Override
	public String getSqlExpression() {
		if (sqlExpression.isDefaultValue()){
			GridPanel grid = (GridPanel) getParent().getParent();
			DataListConnector listConnector = grid.getDataSource();
			
			String sqlExpression = null;
			String dataField = extractLovName( getDataField() );
			DataFieldMetaData metadata = listConnector.getAttributeMetaData( dataField ); 
			if (metadata != null && metadata.getIsLov() ){
				if (listConnector instanceof XEOObjectListConnector){
					try {
						boDefHandler handler = ((XEOObjectListConnector) listConnector).getObjectList().getBoDef();
						boDefAttribute attribute = handler.getAttributeRef( dataField );
						if (attribute != null && StringUtils.hasValue( attribute.getLOVName() ) ){
								sqlExpression = "(select description from " +
											" Ebo_LovDetails ld  " +
											" inner join Ebo_Lov$details b on ld.boui = b.child$ " + 
											" inner join Ebo_Lov l on  b.parent$ = l.boui  " +
											" where l.name = '"+attribute.getLOVName()+"' and ld.value = " +
													"cast("+handler.getBoMasterTable()+"."+attribute.getName()+" as "+
													getCastType((XEOObjectListConnector)listConnector)+"))";		
							}
					} catch ( boRuntimeException e ) {
						e.printStackTrace();
					}
				}
			}
			
			return sqlExpression;
		} else
			return super.getSqlExpression();
	}
	
	private String getCastType(XEOObjectListConnector listConnector) {		
		Connection cn=null;
		try {
			if (castType==null) {
				EboContext ctx=listConnector.getObjectList().getEboContext();
				cn=ctx.getConnectionData();
				String dbName = cn.getMetaData().getDatabaseProductName().toUpperCase();
				
				if (dbName.toUpperCase().indexOf("ORACLE")>-1)	
					castType="varchar(50)";
				else if (dbName.toUpperCase().indexOf("POSTGRE")>-1)
					castType="varchar";
				else if (dbName.toUpperCase().indexOf("MYSQL")>-1)
					castType="char";						
				else if (dbName.toUpperCase().indexOf("SQL SERVER")>-1)
					castType="char";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (cn!=null)
				try {
					cn.close();
				} catch (SQLException e) {
				}
		}
		return castType;
	}
	
	private String extractLovName(String dataField){
		LovColumnNameExtractor extractor = new LovColumnNameExtractor( dataField );
		return extractor.extractName();
	}
	
	private boolean attributeIsLov(){
		GridPanel grid = (GridPanel) findParentComponent( GridPanel.class );
		if (grid != null){
			DataListConnector listConnector = grid.getDataSource();
			DataFieldMetaData metadata = listConnector.getAttributeMetaData( getDataField() ); 
			if (metadata != null && metadata.getIsLov() ){
				if (listConnector instanceof XEOObjectListConnector){
					try {
						boDefHandler handler = ((XEOObjectListConnector) listConnector).getObjectList().getBoDef();
						boDefAttribute attribute = handler.getAttributeRef( extractLovName( getDataField() ) );
						if (attribute != null && StringUtils.hasValue( attribute.getLOVName() ) ){
							return true;
						}
					} catch (boRuntimeException e ){
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean useValueOnLov() {
		if (attributeIsLov())
			return false;
		return super.useValueOnLov();
	}
	
}
