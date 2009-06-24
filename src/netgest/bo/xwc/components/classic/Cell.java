package netgest.bo.xwc.components.classic;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Cell extends XUIComponentBase
{

    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        }

        @Override
        public boolean getRendersChildren() {
            return false;
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            super.encodeEnd(component);
        }

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            super.encodeBegin(component);
        }
    }

}
