package netgest.bo.xwc.components.classic.grid;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.GroupHeader;
import netgest.bo.xwc.components.model.Column;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class GroupHeaderBuilder {

	private JSONObject headers;
	private GridPanel grid; 
	
	public GroupHeaderBuilder(GridPanel grid){
		headers = new JSONObject();
		this.grid = grid;
		process();
	}
	
	public GroupHeaderBuilder useColSpanStyle(boolean style){
		addOption( "useColSpanStyle", style );
		return this;
	}
	
	private void addOption(String name, Object value){
		try {
			headers.put( name, value );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	public boolean hasHeaders(){
		return headers.length() > 0;
	}
	
	
	
	private void process(){
		List<UIComponent> kids = grid.getChildren();
		Iterator<UIComponent> it = kids.iterator();
		while (it.hasNext()){
			UIComponent columns = it.next();
			List<UIComponent> columnsAndGroups = columns.getChildren();
			JSONArray array = new JSONArray();
			for (Iterator<UIComponent> it2 = columnsAndGroups.iterator() ; it2.hasNext(); ){
				UIComponent headerGroup = it2.next();
				if (headerGroup instanceof GroupHeader){
					GroupHeader g = (GroupHeader) headerGroup;
					UIComponent column = g.getChild( 0 );
					String startCol = "";
					if (column instanceof Column)
						startCol = ((Column) column).getDataField();
					int length = g.getChildCount();
					String label = g.getLabel();
					GroupName name = new GroupName( startCol, length, label );
					array.put( name.toJSON() );
				}
			}
			if (array.length() > 0)
				try {
					headers.put( "groupHeaders", array );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
		}
	}
	
	
	
	
	public String serialize(){
		return headers.toString();
	}
	
	private class GroupName{
		private String startColumn;
		private int length;
		private String label;
		
		public GroupName(String startCol, int length, String label){
			this.label = label;
			this.length = length;
			this.startColumn = startCol;
		}
		
		public JSONObject toJSON(){
			
			try {
				return new JSONObject()
					.put( "startColumnName", startColumn )
					.put( "numberOfColumns", length )
					.put( "titleText", label );
			} catch ( JSONException e ) {
				
				e.printStackTrace();
			}
			return new JSONObject();
		}
	}

}
