package netgest.bo.xwc.xeo.components;

import java.util.Iterator;

import javax.faces.component.UIComponent;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

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
	
	@Override
	public String getRendererType() {
		return "gridPanel";
	}
	
	@Override
	public void initComponent(){
		super.initComponent();
		for (Iterator<UIComponent> it = this.getChildren().iterator(); it.hasNext();){
			UIComponent comp = (UIComponent) it.next();
			if (comp instanceof Columns)
				((Columns)comp).setPlugIn("#{viewBean.attributesColPlugIn}");
		}
		
		
	}
	
	@Override
	public void applyComponentProperties() {
		
		if( getStateProperty("dataSource").isDefaultValue() )
			super.setDataSource( "#{viewBean.dataList}" );

		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_MULTI_ROW );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{viewBean.rowDoubleClick}" );
		
	}
	
	public void createToolBar(int pos) {
		LookupListToolBar toolBar = new LookupListToolBar();
		toolBar.setVisible( ((XUIBindProperty<?>)getStateProperty("renderToolBar")).getExpressionString() );
		getChildren().add(pos, toolBar );
	}

	
	
}
