package netgest.bo.xwc.components.connectors.sql;

import java.util.Collection;
import java.util.Iterator;

import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;

public class SQLDataListIterator implements DataListIterator {

	protected Iterator<SQLDataRecordConnector> rows = null;
	
	public SQLDataListIterator( Collection<SQLDataRecordConnector> rows)
	{
		if (rows!=null)
			this.rows = rows.iterator();
	}
	
	@Override
	public boolean hasNext() {
		if (this.rows!=null)
			return rows.hasNext();
		else
			return false;
	}

	@Override
	public DataRecordConnector next() {
		if (this.rows!=null)
			return (DataRecordConnector)rows.next();
		else 
			return null;
	}

	@Override
	public void remove() {
		if (this.rows!=null)
			rows.remove();
		
	}

	@Override
	public boolean skip(int nRows) {
		if (this.rows!=null)
		{
			int i=0;
			while (rows.hasNext() && i<nRows)
			{
				rows.next();
				i++;
			}
			return (i==(nRows-1))?true:false;
		}
		else
			return false;
				
	}

}
