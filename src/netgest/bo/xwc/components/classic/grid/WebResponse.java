package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * Represents the response for a WebRequest
 * 
 * @author PedroRio
 *
 */
public interface WebResponse {

	
	public void setContentType(String type);
	
	public PrintWriter getWriter() throws IOException;
	
}
