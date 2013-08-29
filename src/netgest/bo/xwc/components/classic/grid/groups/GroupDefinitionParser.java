package netgest.bo.xwc.components.classic.grid.groups;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovColumnNameExtractor;
import netgest.utils.StringUtils;

/**
 * 
 * Decodes a GroupBy Expression to check if it has a Lov-encoded name or just
 *
 * 
 * @author PedroRio
 *
 */
public class GroupDefinitionParser {
	
	private GridPanel grid;
	private Column columnOfGroup = null;
	private String groupByExpression = null;
	
	public GroupDefinitionParser(GridPanel grid, String expression){
		this.grid = grid;
		this.groupByExpression = expression;
	}
	
	
	public String getGroupByExpression(){
		if (StringUtils.hasValue( groupByExpression )){
			String[] groups = groupByExpression.split( "," );
			groupByExpression = "";
			for (String group : groups){
				
				if( groupByExpression.length() > 0 ) {
					groupByExpression += ",";
				}
				
				groupByExpression += matchPartsWithColumns( group );
				if (cannotMatchWithColumns()){
					groupByExpression = createGroupExpressionAsLov();
				}
			}
		}
		return groupByExpression;
	}


	private String matchPartsWithColumns( String group ) {
		columnOfGroup = grid.getColumn( group );
		if (columnOfGroup != null)
			return group;
		return null;
	}



	private String createGroupExpressionAsLov() {
		return LovColumnNameExtractor.LOV_ID_PREFIX + groupByExpression;
	}



	private boolean cannotMatchWithColumns() {
		return columnOfGroup == null;
	}
}
