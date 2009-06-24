package netgest.bo.xwc.framework;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public interface XUIRendererServlet {

    public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp ) throws IOException;

}
