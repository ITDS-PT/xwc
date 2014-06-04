package netgest.bo.xwc.components.classic.grid;

import java.sql.Timestamp;

import netgest.bo.xwc.components.classic.grid.utils.DataFieldDecoder;
import netgest.bo.xwc.components.localization.ViewersMessages;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.messages.XUIMessageSender;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

/**
 * 
 * Bean that supports choosing the value for before/after dates when filtering in a GridPanel
 * 
 * @author PedroRio
 *
 */
public class GridDatesBetweenFilterBean extends XEOBaseBean {
	
	/**
	 * Keeps the start date
	 */
	private Timestamp start;
	
	/**
	 * Keeps the end date
	 */
	private Timestamp end;

	public Timestamp getStart() {
		return start;
	}

	public void setStart(Timestamp start) {
		this.start = start;
	}

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}
	
	/**
	 * Keeps the column to filter
	 */
	private String column = "";

	/**
	 * Keeps the current error message
	 */
	private String errorMessage;
	
	/**
	 * Keeps the gridId
	 */
	private String gridId;
	
	public void setColumn(String columnName){
		this.column = columnName;
	}
	
	public void setGridId(String gridId){
		this.gridId = gridId;
	}
	
	/**
	 * Applies the filter to the Grid. Affects the JavaScript filter and requests a data reload if
	 * date values are valid
	 * 
	 */
	public void applyFilter(){
		
		
		if (isValid()){
			StringBuilder dateFilter = new StringBuilder("var f = Ext.getCmp('")
				.append( gridId )
				.append("').filters.getFilter('")
				.append( DataFieldDecoder.convertForGridPanel( column ) ).append("');")
				.append( String.format("f.setBeforeValue(%s);",formatDate(getEnd())) ) //Affect the Widgets with the value
				.append( String.format("f.setAfterValue(%s);",formatDate(getStart())) )
				.append( "f.setBeforeCheck(true);" ) //Mark the widget as checked
				.append( "f.setAfterCheck(true);" )
				.append( "f.clearData();" ) //Clear the Is Null / Not is Null filters
				.append( "f.setActive(true);" ) //Set the filter as active
				.append( "f.fireEvent('update', f);" ); //Request a refresh to the grid data
			
			getRequestContext().getScriptContext()
				.add( XUIScriptContext.POSITION_FOOTER , "gridIdFilters" , dateFilter.toString() );
			
			canCloseTab();
		} else {
			XUIMessageSender.alertError( errorMessage );
		}
		
	}

	/**
	 * 
	 * Checks if the values are valid for the dates
	 * 
	 * @return True if the dates are valid and false otherwise
	 */
	private boolean isValid() {
		if (start != null && end != null){
			if (start.before( end )){
				errorMessage = "";
				return true;
			} else
				errorMessage = ViewersMessages.GRID_DATE_FILTER_START_DATE_BEFORE_END_DATE.toString();
		} else {
			errorMessage = ViewersMessages.GRID_DATE_FILTER_DATES_REQUIRED.toString();
		}
			
		return false;
	}

	/**
	 * 
	 * Retrieves the time (in ms) of a date a a string to pass to the Javascript function
	 * 
	 * @param toFormat The timestamp
	 * 
	 * @return A string with the ms 
	 */
	private String formatDate(Timestamp toFormat) {
		return String.valueOf(toFormat.getTime());
	}
	
}
