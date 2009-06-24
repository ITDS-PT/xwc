package netgest.bo.xwc.components.connectors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SortTerms {
	
	public static final int SORT_ASC = 1;
	public static final int SORT_DESC = 2;
	
	public static final SortTerms EMPTY_SORT_TERMS = new SortTerms();
	
	private static final Iterator<SortTerm> EMPTY_SORT_TERM_ITEM = new LinkedList<SortTerm>().iterator();
	
	private List<SortTerm> sortTerms;
	
	public void addSortTerm( String field, int direction ) {
		if( sortTerms == null ) {
			sortTerms = new LinkedList<SortTerm>();
		}
		sortTerms.add( new SortTerm( field, direction ) );
	}
	
	public Iterator<SortTerm> iterator() {
		if( sortTerms != null ) {
			return sortTerms.iterator();
		}
		else
		{
			return EMPTY_SORT_TERM_ITEM;
		}
	}
	
	public boolean isEmpty() {
		if( sortTerms != null ) {
			return sortTerms.size()==0;
		}
		return true;
	}
	
	public static class SortTerm {
		public String field;
		public int direction;
		
		private SortTerm( String field, int direction ) {
			this.field = field;
			this.direction = direction;
		}
		
		public String getField() {
			return field;
		}

		public int getDirection() {
			return direction;
		}
		
	}
}
