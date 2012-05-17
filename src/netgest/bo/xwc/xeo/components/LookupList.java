package netgest.bo.xwc.xeo.components;

import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.framework.XUIBindProperty;


/**
 * 
 * A component to display a list of XEO Objects so that
 * one (or more) can be selected
 * 
 * @author jcarreira
 *
 */
public class LookupList extends List {
	
	/**
	 * If the lookup is a lookup for the boObject
	 */
	private XUIBindProperty<Boolean> isBoObjectLookup = 
		new XUIBindProperty<Boolean>("isBoObjectLookup", this, Boolean.class);
	
	@Override
	public String getRendererType() {
		return "gridPanel";
	}
	
	public void setIsBoObjectLookup(String boObjExpr){
		this.isBoObjectLookup.setExpressionText(boObjExpr);
	}
	
	public boolean getIsBoObjectLookup(){
		return this.isBoObjectLookup.getEvaluatedValue();
	}
	
	@Override
	public void initComponent(){
		super.initComponent();
		
		if( this.isBoObjectLookup.isDefaultValue() )
			this.setIsBoObjectLookup("#{" + getBeanId() + ".boObjectLookup}");
		
		if( getStateProperty("maxSelections").isDefaultValue() )
			this.setMaxSelections("#{" + getBeanId() + ".maxSelections}");
		
		if (getIsBoObjectLookup()){
			for (Iterator<UIComponent> it = this.getChildren().iterator(); it.hasNext();){
				UIComponent comp = (UIComponent) it.next();
				if (comp instanceof Columns)
					((Columns)comp).setPlugIn("#{" + getBeanId() + ".attributesColPlugIn}");
			}
		}
		
	}
	
	@Override
	public void applyComponentProperties() {
		
		if( getStateProperty("dataSource").isDefaultValue() )
			super.setDataSource( "#{" + getBeanId() + ".dataList}" );

		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_MULTI_ROW );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{" + getBeanId() + ".rowDoubleClick}" );
		
		if( super.getStateProperty( "enableSelectionAcrossPages" ).isDefaultValue() )
			setEnableSelectionAcrossPages(true);
		
	}
	
	public void createToolBar(int pos) {
		LookupListToolBar toolBar = new LookupListToolBar();
		toolBar.setVisible( ((XUIBindProperty<?>)getStateProperty("renderToolBar")).getExpressionString() );
		toolBar.setId( getId() + "_lookupToolBar" );
		getChildren().add(pos, toolBar );
	}

	
	
}
