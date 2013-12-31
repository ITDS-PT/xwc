package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans.error;

import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class ResultSetPrinter {
	
	private ResultSet result;
	private String printed = null;
	
	public ResultSetPrinter(ResultSet set){
		this.result = set;
	}
	
	
	public String print(){
		if (printed != null)
			return printed;
			
		StringBuilder b = new StringBuilder(3000);
		try {
			b.append("<table class='errors'>");
			ResultSetMetaData metadata = this.result.getMetaData();
			int columns = metadata.getColumnCount();
			b.append("<tr>");
			for (int k = 1 ; k <= columns ; k++){
				b.append("<th>");
				b.append(metadata.getColumnLabel(k));
				b.append("</th>");
			}
			b.append("</tr>");
			while (this.result.next()){
				b.append("<tr>");
				for (int i = 1 ; i <= columns ; i++){
					b.append("<td>");
					if (metadata.getColumnType(i) == Types.CLOB){
						b.append(readClob(result.getClob(i)));
					} else {
						b.append(result.getObject(i));
					}
					b.append("</td>");
				}
				b.append("</tr>");
			}
			b.append("</table>");
			printed = b.toString();
		} catch (SQLException e ) {
			printed = "An error occurred";
		} finally {
			try {
				this.result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return printed;
		
	}
	
		private String readClob(Clob clob) throws SQLException{
		
		char[] buffer;
	    int count = 0;
	    int length = 0;
	    String data = null;
	    String[] type;
	    if (clob == null)
	    	return "";
		Reader is = clob.getCharacterStream();
		 
        // Initialize local variables.
        StringBuilder sb = new StringBuilder();
       length = (int) clob.length();

        // Check CLOB is not empty.
        if (length > 0) {
          // Initialize control structures to read stream.
          buffer = new char[length];
          count = 0;

          // Read stream and append to StringBuffer.
          try {
            while ((count = is.read(buffer)) != -1)
              sb.append(buffer);

              // Assign StringBuffer to String.
              data = sb.toString(); }
          catch (Exception e) {} }
        else{
        	return "";
        }
        return data;
	}

}
