package netgest.bo.xwc.components.connectors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.JOIN_OPERATOR;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.VALUE_OPERATOR;

public class FilterTerms {

	public static final byte OPERATOR_GREATER_THAN 			= 1;
	public static final byte OPERATOR_GREATER_OR_EQUAL_THAN = 2;
	public static final byte OPERATOR_LESS_THAN 			= 3;
	public static final byte OPERATOR_LESS_OR_EQUAL_THAN 	= 4;
	public static final byte OPERATOR_IN 					= 5;
	

	public static final byte OPERATOR_EQUAL 			= 6;
	public static final byte OPERATOR_NOT_EQUAL 		= 7;

	public static final byte OPERATOR_CONTAINS 				= 8;
	public static final byte OPERATOR_NOT_CONTAINS 			= 9;

	public static final byte OPERATOR_LIKE 					= 10;
	public static final byte OPERATOR_NOT_LIKE 				= 11;
	
	public static final byte OPERATOR_NONE 				= 12;
	
	public static final byte OPERATOR_STARTS_WITH		= 13;
	public static final byte OPERATOR_ENDS_WITH		= 14;
	public static final byte OPERATOR_NOT_IN 		= 15;
	
	public static final byte CONDITION_NOT_NULL = 1;
	public static final byte CONDITION_IS_NULL 	= 2;
	
	public static final byte JOIN_NONE = 0;
	public static final byte JOIN_AND_NOT = 1;
	public static final byte JOIN_AND = 2;
	public static final byte JOIN_OR = 3;
	public static final byte JOIN_OPEN_BRACKET = 4;
	public static final byte JOIN_CLOSE_BRACKET = 5;
	public static final byte JOIN_AND_OPEN_BRACKET = 6;
	public static final byte JOIN_CLOSE_BRACKET_AND = 7;
	public static final byte JOIN_CLOSE_BRACKET_AND_OPEN_BRACKET = 8;
	public static final byte JOIN_OR_OPEN_BRACKET = 9;
	public static final byte JOIN_CLOSE_BRACKET_OR = 10;
	public static final byte JOIN_CLOSE_BRACKET_OR_OPEN_BRACKET = 11;
	
	
	private List<FilterJoin> 	joins = new LinkedList<FilterJoin>();
	
	private FilterJoin	currentJoin = null;
	
	public FilterTerms( FilterTerm term ) {
		addJoin( JOIN_NONE, term );
	}
	
	public FilterTerms( FilterTerm term, byte join ) {
		addJoin( join, term );
	}
	
	
	public void addTerm( byte joinCondition, String dataField, String sqlExpression, byte operator, Object value ) {
		addJoin( joinCondition, new FilterTerm( dataField, sqlExpression, operator, value ) );
	}

	
	public void addTerm( byte joinCondition, FilterTerm term ) {
		addJoin( joinCondition, term );
	}

	private void addJoin( Byte joinCondition, FilterTerm term ) {
		FilterJoin join;
		
		join = new FilterJoin( currentJoin, joinCondition, term );
		
		if( currentJoin != null )
			currentJoin.next = join;
		
		joins.add( join );
		currentJoin = join;
	}
	
	public Iterator<FilterJoin> iterator() {
		return joins.iterator();
	}

	public static class FilterTerm {
		
		private String dataField;
		private String sqlExpression;
		private Object value;
		private byte   operator;
		
		public FilterTerm createEmptyFilterTerm(){
			return new FilterTerm("",null,JOIN_NONE,"");
		}
		
		public FilterTerm( String dataField, String sqlExpression, Byte operator, Object value  ) {
			
			this.dataField = dataField;
			this.value     = value;
			this.sqlExpression = sqlExpression;
			this.operator  = operator;
			
			
		}
		public String getDataField() {
			return dataField.replace( "__", "." );
		}

		public Object getValue() {
			return value;
		}

		public byte getOperator() {
			return operator;
		}
		
		public String getSqlExpression() {
			return this.sqlExpression;
		}
		
		public void setSqlExpression( String sqlExpression ) {
			this.sqlExpression = sqlExpression;
		}
		
	}

	public boolean isEmpty() {
		if( joins != null ) {
			return joins.size()==0;
		}
		return true;
	}
	
	public static byte getValueOperatorFromCode(VALUE_OPERATOR op){
		
		switch (op){
			case EQUAL:
				return OPERATOR_EQUAL;
			case DIFFERENT:
				return OPERATOR_NOT_EQUAL;
			case BIGGER:
				return OPERATOR_GREATER_THAN;
			case LESS:
				return OPERATOR_LESS_THAN;
			case LESS_OR_EQUAL:
				return OPERATOR_LESS_OR_EQUAL_THAN;
			case BIGGER_OR_EQUAL:
				return OPERATOR_GREATER_OR_EQUAL_THAN;
			case CONTAINS:
				return OPERATOR_LIKE;
			case NOT_CONTAINS:
				return OPERATOR_NOT_LIKE;
			case CONTAINS_DATA:
				return OPERATOR_CONTAINS;
			case NOT_CONTAINS_DATA:
				return OPERATOR_NOT_CONTAINS;
			case STARTS_WITH:
				return OPERATOR_STARTS_WITH;
			case ENDS_WITH:
				return OPERATOR_ENDS_WITH;
			default:
				return OPERATOR_EQUAL;
		}
	}
	
	public static byte getValueOperatorFromCode(int code){
		VALUE_OPERATOR op = VALUE_OPERATOR.fromCode( code );
		return getValueOperatorFromCode( op );
	}
	
	public static byte getJoinOperatorFromCode(JOIN_OPERATOR op){
		
		if (op == null)
			return JOIN_NONE;
		
		switch (op){
			case AND :  
				return JOIN_AND;
			case OR:
				return JOIN_OR;
			case OPEN_BRACKET:
				return JOIN_OPEN_BRACKET;
			case CLOSE_BRACKET:
				return JOIN_CLOSE_BRACKET;
			case CLOSE_BRACKET_AND:
				return JOIN_CLOSE_BRACKET_AND;
			case AND_OPEN_BRACKET:
				return JOIN_AND_OPEN_BRACKET;
			case CLOSE_BRACKET_AND_OPEN_BRACKET:
				return JOIN_CLOSE_BRACKET_AND_OPEN_BRACKET;
			case CLOSE_BRACKET_OR:
				return JOIN_CLOSE_BRACKET_OR;
			case OR_OPEN_BRACKET:
				return JOIN_OR_OPEN_BRACKET;
			case CLOSE_BRACKET_OR_OPEN_BRACKET:
				return JOIN_CLOSE_BRACKET_OR_OPEN_BRACKET;
			default:
				return JOIN_NONE;
		}
	}
	
	public static byte getJoinOperatorFromCode(int code){
		JOIN_OPERATOR op = JOIN_OPERATOR.fromCode( code );
		return getJoinOperatorFromCode( op );
		
	}
	
	public static class FilterJoin {
		
		private FilterJoin 	previous;
		private Byte 		joinType; 
		private FilterTerm	term;
		private FilterJoin 	next;
		
		private FilterJoin( FilterJoin previousJoin, Byte joinType, FilterTerm term ) {
			this.previous = previousJoin;
			this.joinType = joinType;
			this.term = term;
		}
		
		public FilterJoin previous() {
			return this.previous;
		}
		
		public FilterTerm getTerm() {
			return this.term;
		}
		
		public Byte getJoinType() {
			return this.joinType;
		}
		
		public FilterJoin next() {
			return  this.next;
		}

	}


	
}
