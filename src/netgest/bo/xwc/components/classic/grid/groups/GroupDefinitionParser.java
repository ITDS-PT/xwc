package netgest.bo.xwc.components.classic.grid.groups;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.xeo.components.ColumnAttribute;
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
		String result = "";
		if (StringUtils.hasValue( groupByExpression )){
			String[] groups = groupByExpression.split( "," );
			groupByExpression = "";
			for (String group : groups){
				
				if( result.length() > 0 ) {
					result += ",";
				}
				
				if (!LovColumnNameExtractor.isXeoLovColumn(group)){
					if (matchPartsWithColumns( group )){
						result += group;
					}
					else {
						result += createGroupExpressionAsLov(group);
					}
				} else {
					result += group;
				}
				
			}
		}
		return result;
	}


	private boolean matchPartsWithColumns( String group ) {
		columnOfGroup = grid.getColumn( group );
		if (columnOfGroup != null){
			if (columnOfGroup instanceof ColumnAttribute){
				ColumnAttribute column = (ColumnAttribute) columnOfGroup; 
				if (column.attributeIsLov()){
					return false;
				}
			}
		} else
			return false;
		
		return true;
	}



	private String createGroupExpressionAsLov(String group) {
		return new LovColumnNameExtractor(group).prefixColumnName();
	}



}
