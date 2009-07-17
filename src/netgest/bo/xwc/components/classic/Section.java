package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Section extends ViewerSecurityBase {
    public XUIStateProperty<String> label = new XUIStateProperty<String>( "label", this );

    public void setLabel(String label)
    {
        this.label.setValue( label );
    }

    public String getLabel()
    {
        return label.getValue();
    }

	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}

    public static class XEOHTMLRenderer extends XUIRenderer
    {

        @Override 
        public void encodeBegin(XUIComponentBase component) throws IOException 
        {
            Section oSection = (Section)component;
            super.encodeBegin(component);
            ResponseWriter w = getResponseWriter();

        	w.startElement( "fieldset", component );
        	w.writeAttribute( "id", oSection.getId(), null );
        	w.writeAttribute( "class", "x-fieldset x-form-label-left", null );
        	if( !XUIRenderer.Util.isEmpty( oSection.getLabel() ) ) {
//                <legend class="x-fieldset-header x-unselectable" id="static-ext-gen90"
//                        style="MozUserSelect: none; KhtmlUserSelect: none"
//                        unselectable="on">
//                      <input id="static-ext-gen94" type="checkbox"
//                             name="static-ext-comp-1011-checkbox" value="on"/>
//                      <span class="x-fieldset-header-text" id="static-ext-gen96">User
//                                                                          Information</span>
//                </legend>
        		w.startElement("legend", oSection );
        		w.writeAttribute("class","x-fieldset-header x-unselectable", null);
        		w.writeAttribute("style","MozUserSelect: none; KhtmlUserSelect: none", null);
        		w.writeAttribute("unselectable","on", null);
        		w.startElement("span", oSection);
        		w.writeAttribute( "class", "x-fieldset-header-text", null );
        		w.writeText( oSection.getLabel(), null );
        		w.endElement("span");
        		w.endElement("legend"); 
        	}
            
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            ResponseWriter w = getResponseWriter();
            w.endElement("fieldset");
            super.encodeEnd(component);
        }


        @Override
        public boolean getRendersChildren() {
            return true;
        }

    }

    //
    // Methods from SecurableComponent
    //

	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.AREA;
	}

	public String getViewerSecurityId() {
		String securityId = null;
 		if (getLabel()!=null && getLabel().length()>0) {
 			securityId = getLabel();
 		}
		return securityId;
	}

	public String getViewerSecurityLabel() {
		String label = getViewerSecurityComponentType().toString();
		if ( getViewerSecurityId()!=null ) {
			label += " "+ getViewerSecurityId();
		}
		return label; 
	}

	public boolean isContainer() {
		return false;
	}

}

