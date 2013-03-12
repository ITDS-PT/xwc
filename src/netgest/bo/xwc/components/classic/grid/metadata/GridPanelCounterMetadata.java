package netgest.bo.xwc.components.classic.grid.metadata;

import org.json.JSONException;
import org.json.JSONObject; 

public class GridPanelCounterMetadata{
		
		
		long totalCount;
		
		public void setTotalCount(long totalCount) {
			this.totalCount = totalCount;
		}
		
		public void setHasMorePages(boolean hasMorePages) {
			this.hasMorePages = hasMorePages;
		}
		
		public void setCursor(int cursor) {
			this.cursor = cursor;
		}
		
		public void setLastPage(boolean isLastPage) {
			this.isLastPage = isLastPage;
		}
		
		public void setLastPageNumber(int lastPage) {
			this.lastPage = lastPage;
		}
		
		Boolean hasMorePages = null;
		Integer cursor = null;
		Boolean isLastPage = null;
		Integer lastPage = null;
		
		public String serialize(){
			StringBuilder b = new StringBuilder(100);
			try {
				b.append("\"totalCount\"");
				b.append(":");
				b.append("\""+this.totalCount+"\"");
				
				if (metadataExists()){
					JSONObject metadata = new JSONObject();
					if (cursor != null)
						metadata.put( "cursor" , cursor.intValue() );
					if (hasMorePages != null)
						metadata.put( "hasMorePages" , hasMorePages.booleanValue() );
					if (isLastPage != null)
						metadata.put( "isLastPage" , isLastPage.booleanValue() );
					if (lastPage != null)
						metadata.put( "lastPage" , lastPage.intValue() );
					b.append( ", \"metadata\":" + metadata.toString() );
				}
				
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
			return b.toString();
			
		}
		protected boolean metadataExists() {
			return hasMorePages != null || cursor != null || isLastPage != null || lastPage != null;
		}
	
		
		
	}