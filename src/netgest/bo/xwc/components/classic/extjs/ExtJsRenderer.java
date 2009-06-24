package netgest.bo.xwc.components.classic.extjs;

import java.io.IOException;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public interface ExtJsRenderer {

    public ExtConfig extEncodeAll( XUIComponentBase oComp ) throws IOException;

}
