package netgest.bo.xwc.framework.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

public class XUIPerformanceFilter
    implements Filter
{
    private FilterConfig _filterConfig = null;

    public void init(FilterConfig filterConfig)
        throws ServletException
    {
        _filterConfig = filterConfig;
    }

    public void destroy()
    {
        _filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain)
        throws IOException, ServletException
    {
        long init;
        String sRequestURI;
        String sContentType;
        
        HttpServletRequest oHttpRequest;
        HttpServletResponse oHttpResponse;
        
        init = System.currentTimeMillis();
        
        oHttpRequest = (HttpServletRequest)request;
        oHttpResponse = (HttpServletResponse)response;
        
        sRequestURI = oHttpRequest.getRequestURI();
        
        chain.doFilter(request, response);

//        System.out.println( sRequestURI + ":" + ( System.currentTimeMillis() - init ) );
    }
}
