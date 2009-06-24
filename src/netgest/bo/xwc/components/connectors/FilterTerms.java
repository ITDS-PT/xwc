package netgest.bo.xwc.components.connectors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	
	public static final byte CONDITION_NOT_NULL = 1;
	public static final byte CONDITION_IS_NULL 	= 2;
	
	public static final byte JOIN_AND_NOT = 1;
	public static final byte JOIN_AND = 2;
	public static final byte JOIN_OR = 3;
	
	private List<FilterJoin> 	joins = new LinkedList<FilterJoin>();
	
	private FilterJoin	currentJoin = null;
	
	public FilterTerms( FilterTerm term ) {
		addJoin( null, term );
	}
	
	public void addTerm( byte joinCondition, String dataField, byte operator, Object value ) {
		addJoin( joinCondition, new FilterTerm( dataField, operator, value ) );
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
		private Object value;
		private byte   operator;
		
		public FilterTerm( String dataField, Byte operator, Object value ) {
			
			this.dataField = dataField;
			this.value     = value;
			this.operator  = operator;
			
		}
		public String getDataField() {
			return dataField;
		}

		public Object getValue() {
			return value;
		}

		public byte getOperator() {
			return operator;
		}

	}

	public boolean isEmpty() {
		if( joins != null ) {
			return joins.size()==0;
		}
		return true;
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
