package netgest.bo.xwc.components.classic.grid;

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
	
	@Override
	public String getParameter( String name ) {
		return request.getParameter( name );
	}

	@Override
	public String[] getParameterValues( String name ) {
		return request.getParameterValues( name );
	}

	@Override
	public String getParameter( GridParameter param ) {
		return request.getParameter( param.getName() );
	}

	@Override
	public String[] getParameterValues( GridParameter param ) {
		return request.getParameterValues( param.getName() );
	}

}
