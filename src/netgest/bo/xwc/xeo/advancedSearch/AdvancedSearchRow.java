package netgest.bo.xwc.xeo.advancedSearch;


/**
 * 
 * Represents a ROW in an advanced search performed on a grid panel
 * 
 * @author PedroRio
 *
 */
public class AdvancedSearchRow {

	/**
	 * Represents the JOIN operator to be used in the construction of a term
	 *
	 */
	public enum JOIN_OPERATOR {
		OPEN_BRACKET(0,"("),
		CLOSE_BRACKET(1,")"),
		AND(2,"and"),
		OR(3,"or"),
		AND_OPEN_BRACKET(4,"and ("),
		CLOSE_BRACKET_AND(5,") and"),
		CLOSE_BRACKET_AND_OPEN_BRACKET(6,") and ("),
		OR_OPEN_BRACKET(7,"or ("),
		CLOSE_BRACKET_OR(8, ") or "),
		CLOSE_BRACKET_OR_OPEN_BRACKET(9,") or ("),
		NONE(10,"");
		
		private JOIN_OPERATOR(int code, String label){
			this.code = code;
			this.label = label;
		}
		
		private int code;
		
		private String label;
		
		public int getCode(){
			return code;
		}
		
		public static JOIN_OPERATOR fromCode(int code){
			for (JOIN_OPERATOR op : values()){
				if (op.getCode() == code)
					return op;
			}
			return null;
		}
		
		public static JOIN_OPERATOR fromCode(String code){
			int codeValue = Integer.valueOf( code ).intValue();
			for (JOIN_OPERATOR op : values()){
				if (op.getCode() == codeValue)
					return op;
			}
			return null;
		}
		
		public static String getLabelFromCode(int code){
			for (JOIN_OPERATOR op : values()){
				if (op.getCode() == code)
					return op.getLabel();
			}
			return "";
		}
		
		public String getLabel(){
			return label;
		}
	}
	
	/**
	 * Represents the operator applied to the value of a given attribute
	 *
	 */
	public enum VALUE_OPERATOR {
		
		//Maps to the adv.all Lov in AdvancedSearchOperators.xeovlov
		EQUAL(0," = "),
		DIFFERENT(1," != "),
		BIGGER(2," > "),
		LESS(3," < "),
		BIGGER_OR_EQUAL(4," >= "),
		LESS_OR_EQUAL(5," <= "),
		CONTAINS(6," LIKE "),
		NOT_CONTAINS(7," NOT LIKE "),
		CONTAINS_DATA(8," IS NOT NULL "),
		NOT_CONTAINS_DATA(9," IS NULL "),
		STARTS_WITH(10, " LIKE "),
		ENDS_WITH(11, " LIKE "),
		IN(12, " IN ");
		
		
		
		private VALUE_OPERATOR(int code, String label){
			this.code = code;
			this.label = label;
		}
		
		private int code;
		
		private String label;
		
		public String getLabel(){
			return label;
		}
		
		public int getCode(){
			return code;
		}
		
		public static VALUE_OPERATOR fromCode(int code){
			for (VALUE_OPERATOR op : values()){
				if (op.getCode() == code)
					return op;
			}
			return null;
		}
		
		public static VALUE_OPERATOR fromCode(String code){
			int codeValue = Integer.valueOf( code ).intValue();
			for (VALUE_OPERATOR op : values()){
				if (op.getCode() == codeValue)
					return op;
			}
			return null;
		}
		
		public static String getLabelFromCode(int code){
			for (VALUE_OPERATOR op : values()){
				if (op.getCode() == code)
					return op.getLabel();
			}
			return null;
		}
		
	}
	
	
}
