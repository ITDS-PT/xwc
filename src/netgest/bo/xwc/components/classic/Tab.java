package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.List;

import javax.faces.event.ActionEvent;

import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Tab extends ViewerCommandSecurityBase
{
    protected XUIViewStateProperty<String> 		label 	= new XUIViewStateProperty<String>( "label", this );
    protected XUIViewStateBindProperty<Boolean> visible = new XUIViewStateBindProperty<Boolean>( "visible", this, "true", Boolean.class ); 	
    
    public void setLabel(String label)
    {
        this.label.setValue( label );
    }

    public Boolean isVisible() {
		return visible.getEvaluatedValue();
	}

	public void setVisible(String visible) {
		this.visible.setExpressionText( visible );
	}

	public String getLabel()
    {
        return label.getValue();
    }

    @Override
	public void actionPerformed(ActionEvent event)
    {
        ((Tabs)getParent()).setActiveTab( getId() );
    }
    
    @Override
	public boolean isRendered() {
    	if( !getEffectivePermission(SecurityPermissions.READ) ) {
    		return false;
    	}
		return super.isRendered();
	}

	@Override
    public boolean wasStateChanged() {
        return getId().equals( ((Tabs)getParent()).getActiveTab() ) && super.wasStateChanged();
    }

    @Override
    public void processStateChanged(List<XUIComponentBase> oRenderList) {
        if( getId().equals( ((Tabs)getParent()).getActiveTab() ) )
        {
            super.processStateChanged(oRenderList);
        }
    }

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
//        	super.encodeBegin(component);
//            XUIResponseWriter w = getResponseWriter();
//            w.startElement( "div", component );
//            w.writeAttribute( "class", "", null );
//            w.writeAttribute( "style", "width:100%", null );
            
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
//        	super.encodeEnd(component);
        	
//            XUIResponseWriter w = getResponseWriter();
//            w.endElement("div");
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
 		if ( getLabel()!=null && getLabel().length()>0 ) {
 			securityId = getLabel();
 		}
		return securityId;
	}

	public String getViewerSecurityLabel() {
		return getViewerSecurityComponentType().toString()+" "+getLabel();
	}

	public boolean isContainer() {
		return true;
	}
	
}
