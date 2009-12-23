package netgest.bo.xwc.framework.http;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class XUIMultiPartRequestWrapper extends HttpServletRequestWrapper{
	
	private com.oreilly.servlet.MultipartRequest oMultiPartRequest;
	private Hashtable<String,String[]> parameterMap = new Hashtable<String,String[]>();
	
	public XUIMultiPartRequestWrapper( HttpServletRequest oRequest ) {
		super( oRequest );
		
        String tmpFolder = netgest.bo.impl.document.Ebo_DocumentImpl.getTempDir();
        if(tmpFolder.endsWith("\\") || tmpFolder.endsWith("/"))
        {
           tmpFolder =  tmpFolder + System.currentTimeMillis() + File.separator;
        }
        else
        {
           tmpFolder =  tmpFolder + File.separator + System.currentTimeMillis();
        }
        java.io.File tmpdir = new java.io.File(tmpFolder);
        if(!tmpdir.exists()) 
        {
            tmpdir.mkdirs();
        }

		try {
			
			oMultiPartRequest = new com.oreilly.servlet.MultipartRequest( oRequest, tmpdir.getAbsolutePath(), 64 * 1024 * 1024 );
			
			Enumeration oEnum = oMultiPartRequest.getParameterNames();
			while( oEnum.hasMoreElements() ) {
				String sKey = oEnum.nextElement().toString();
				parameterMap.put( sKey, oMultiPartRequest.getParameterValues( sKey) );
			}
			
			oEnum = oRequest.getParameterNames();
			while( oEnum.hasMoreElements() ) {
				String sKey = oEnum.nextElement().toString();
				parameterMap.put( sKey, oRequest.getParameterValues( sKey) );
			}

			
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
		
	}

	@Override
	public String getContentType() {
		return super.getContentType();
	}

	@Override
	public String getParameter(String arg0) {
		String ret[] = parameterMap.get(arg0);
		if( ret != null && ret.length > 0 ) {
			return ret[0];
		}
		return null;
	}

	@Override
	public Map getParameterMap() {
		return parameterMap;
	}

	@Override
	public Enumeration getParameterNames() {
		return parameterMap.keys();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return parameterMap.get(arg0);
	}
	
	public File getFile( String sName ) {
		return oMultiPartRequest.getFile( sName );
	}

	public Enumeration<String> getFileNames() {
		return (Enumeration<String>)oMultiPartRequest.getFileNames();
	}
	
}
