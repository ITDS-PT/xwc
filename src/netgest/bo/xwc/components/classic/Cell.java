package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Cell of a row
 * 
 * He can have children of any type
 * 
 * @author jcarreira
 *
 */
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
