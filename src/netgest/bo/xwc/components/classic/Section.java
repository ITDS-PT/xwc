package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * The {@link Section} component is used to create sections 
 * (HTML fieldset) inside a viewer.
 * 
 * 
 * @author jcarreira
 *
 */
public class Section extends ViewerSecurityBase {
	
    /**
     * The label to display in the section
     */
    private XUIViewStateBindProperty<String> label = new XUIViewStateBindProperty<String>( "label", this, String.class );
    
    /**
     * Whether or not the section is visible
     */
    private XUIViewStateBindProperty<Boolean> visible = new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );

    public void setLabel(String label)
    {
        this.label.setExpressionText( label );
    }

    public String getLabel()
    {
        return label.getEvaluatedValue();
    }

    public void setVisible(boolean visible )
    {
        this.visible.setExpressionText( Boolean.toString( visible ) );
    }

    public void setVisible(String visibleExpr )
    {
        this.visible.setExpressionText( visibleExpr );
    }

    public boolean getVisible()
    {
        return visible.getEvaluatedValue();
    }
    
    @Override
    public void initComponent() {
    	super.initComponent();
    	initializeTemplate( "templates/components/section.ftl" );
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
        	w.writeAttribute( "id", oSection.getClientId(), null );
        	
        	String classes = "x-fieldset x-form-label-left";
        	if( !oSection.getVisible() ) {
        		classes += " x-hidden";
        	}
        	w.writeAttribute( "class", classes, null );
        	
        	if( !XUIRenderer.Util.isEmpty( oSection.getLabel() ) ) {
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
		Object label = getStateProperty("label").getValue();
 		if (label instanceof ValueExpression) {
 			securityId = ((ValueExpression) label).getExpressionString();
 		} else if (label != null){
 			securityId = String.valueOf(label);	
 		}
 			
		return securityId;
	}

	public String getViewerSecurityLabel() {
		try {
			String label = getLabel();
			return label;
		}
		catch( Throwable e ) {
			return getViewerSecurityId();
		}
	}

	public boolean isContainer() {
		return false;
	}

}

