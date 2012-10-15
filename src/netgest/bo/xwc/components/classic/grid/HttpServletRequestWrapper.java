package netgest.bo.xwc.components.classic.grid;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Wraps the {@link HttpServletRequest} instance to
 * implement the {@link WebRequest} interface
 * 
 * @author PedroRio
 *
 */
public class HttpServletRequestWrapper implements WebRequest {

	private HttpServletRequest request;
	
	public HttpServletRequestWrapper(HttpServletRequest req){
		this.request = req;
	}
	
	public HttpServletRequestWrapper(ServletRequest req){
		this.request = (HttpServletRequest) req;
	}
	
	@Override
	public String getParameter( String name ) {
		return request.getParameter( name );
	}

	@Override
	public String[] getParameterValues( String name ) {
		return request.getParameterValues( name );
	}

	@Override
	public String getParameter( WebParameter param ) {
		return request.getParameter( param.getName() );
	}

	@Override
	public String[] getParameterValues( WebParameter param ) {
		return request.getParameterValues( param.getName() );
	}

}
