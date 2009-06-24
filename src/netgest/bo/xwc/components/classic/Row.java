package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;

import java.io.IOException;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Row extends XUIComponentBase
{

    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            w.startElement("tr", null );
            w.writeAttribute("valign","top", null);
            w.writeAttribute( CLASS, "xwc-rows-row", null);
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            ResponseWriter w = getResponseWriter();
            w.endElement("tr" );
        }




        @Override
        public boolean getRendersChildren() {
            return true;
        }

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
            if (component.getChildCount() > 0) {

                XUIResponseWriter w = getResponseWriter();
                Iterator<UIComponent> kids = component.getChildren().iterator();
                while (kids.hasNext()) {
                    w.startElement("td",component);
                    if( component.getChildCount() == 1 ) {
                        w.writeAttribute("colspan","2", null );
                    }
                    UIComponent kid = kids.next();
                    kid.encodeAll(getFacesContext());
                    w.endElement("td");
                }
            }
        }
    }


}
