package netgest.bo.xwc.xeo.beans.lookup;

import java.io.InputStream;
import java.util.List;

import netgest.bo.runtime.boObjectList;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;
import netgest.bo.xwc.framework.def.XUIViewerDefinitonParser;
import netgest.bo.xwc.xeo.beans.XEOBaseLookupList;
import netgest.bo.xwc.xeo.components.ColumnAttribute;

public class SplitLookupSearchBean extends XEOBaseLookupList {
	
	public void setBouis(List<String> bouis){
		convertedBouis = new long[bouis.size()];
		int k = 0;
		for (String value : bouis){
			convertedBouis[k] = Long.valueOf(value).longValue(); 
			k++;
		}
	}
	
	private long[] convertedBouis = new long[0];
	
	
	public DataListConnector getDataSource(){
		XEOObjectListConnector connector = null;
		boObjectList list = boObjectList.list( getEboContext( ) , getSelectedObject() , convertedBouis );
		connector = new XEOObjectListConnector( list );
		return connector;
	}

	
	
	@Override
	public XUIComponentPlugIn getAttributesColPlugIn() {
		return new SplitLookupColPlugIn();
	}

	/**
	 * 
	 * Column Plugin that when using a boObject Lookup sets the columns of the list to the
	 * columns of the Lookup viewer for that
	 *
	 */
	private class SplitLookupColPlugIn extends XUIComponentPlugIn {

		@Override
		public void beforePreRender() {

			GridPanel grid = (GridPanel) ((XUIComponentBase)getComponent().getParent());
			grid.resetColumns();
			grid.forceRenderOnClient();
			getComponent().getChildren().clear();
			if(getSelectedObject() != null && !"".equalsIgnoreCase(getSelectedObject())){

				XUIViewerDefinitonParser parser = new XUIViewerDefinitonParser();

				InputStream is = parser.resolveViewer(getSelectedObject()+"/lookup.xvw");
				if (is == null)
					is = parser.resolveViewer(getSelectedObject()+"_lookup.xvw");

				XUIViewerDefinition viewer = parser.parse(is);

				List<XUIViewerDefinitionNode> cols = new FindLookupListCols(viewer.getRootComponent().
						getChildren()).getColns();
				for (int i = 0, n = cols.size(); i < n; i++){
					ColumnAttribute ca = new ColumnAttribute();
					XUIViewerDefinitionNode node = cols.get(i);
					ca.setDataField(node.getProperty("dataField"));
					ca.setWidth("100");
					if (node.getProperty("hidden") != null){
						ca.setHidden(node.getProperty("hidden"));
					}
					getComponent().getChildren().add(ca);
				}
			}
		}
	

	}
	

}
