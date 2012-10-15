package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseWrapper implements WebResponse {

	
	private HttpServletResponse response;
	
	public HttpServletResponseWrapper(ServletResponse response){
		this.response = (HttpServletResponse) response;
	}
	
	@Override
	public void setContentType( String type ) {
		response.setContentType( type );
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

}
