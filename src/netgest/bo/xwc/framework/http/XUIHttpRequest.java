package netgest.bo.xwc.framework.http;

import javax.servlet.http.HttpServletRequest;
import static netgest.utils.StringUtils.*;

public class XUIHttpRequest {

	/**
	 * 
	 * Extracts the Client IP from a HTTP request
	 * 
	 * If a proxy/balancer was used to process the request and it added some headers about the client ip
	 * those headers are checked first, if no header is found the {@link HttpServletRequest#getRemoteAddr()} is used
	 * 
	 * 
	 * 
	 * @param request The HTTP request
	 * @return The IP address of the client
	 */
	public static String getClientIpFromRequest(HttpServletRequest request){
	        String ip = request.getHeader("X-Forwarded-For");
	        
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("CLIENTIP");  
	        }
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("ClientIp");  
	        }
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("Proxy-Client-IP");  
	        }  
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("WL-Proxy-Client-IP");  
	        }  
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_CLIENT_IP");  
	        }  
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	        } 
	        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getRemoteAddr();  
	        }  
	        return ip;  
	}

}
