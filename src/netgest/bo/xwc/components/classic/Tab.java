package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;
import netgest.bo.xwc.framework.jsf.XUIViewHandler.PropertyEvaluation;

/**
 * 
 * A {@link Tab} is a component that's used to create tabs inside a viewer.
 * 
 * @author jcarreira
 *
 */
public class Tab extends ViewerCommandSecurityBase
{
    /**
     * The display name of the tab
     */
	@Localize
    protected XUIViewStateProperty<String> 		label 	= new XUIViewStateProperty<String>( "label", this );
    /**
     * Whether or not this tab is visible
     */
    protected XUIViewStateBindProperty<Boolean> visible = new XUIViewStateBindProperty<Boolean>( "visible", this, "true", Boolean.class );
    
    /**
     * The path to an icon to display in the tab
     */
    private XUIViewStateProperty<String> icon = new XUIViewStateProperty<String>( "icon", this );
    
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
	
	public String getIcon(){
		return icon.getValue();
	}
	
	public void setIcon(String icon){
		this.icon.setValue(icon);
	}

    @Override
	public void actionPerformed(ActionEvent event){
        ((Tabs)getParent()).setActiveTab( getId() );
    }
    
    @Override
	public boolean isRendered() {
    	if( !getEffectivePermission(SecurityPermissions.READ) ) {
    		return false;
    	}
		return super.isRendered();
	}
    
    public boolean isActiveTab(){
    	Tabs parent = (Tabs) getParent();
    	if (parent.getActiveTab() == null) 
    		parent.findActiveTab();
    	return getId().equals(parent.getActiveTab());
    }
    
    @Override
    public Object processSaveState(FacesContext context) {
    	Object savedState = null;
    	boolean isTopTab = ((Tabs)getParent()).isTopTabs();
    	if (isActiveTab() && isTopTab){
    		savedState = super.processSaveState(context);
    	} else {
    		try {
    			XUIViewHandler.setPropertyEvaluationMode(PropertyEvaluation.DONT_EVALUATE);
    			savedState = super.processSaveState(context);
    		} finally { 
    			XUIViewHandler.setPropertyEvaluationMode(PropertyEvaluation.EVALUATE);
    		}
    	}
    	return savedState;
    }

	@Override
    public StateChanged wasStateChanged2() {
		if (getId().equals( ((Tabs)getParent()).getActiveTab() ) && super.wasStateChanged2() == StateChanged.FOR_RENDER)
			return StateChanged.FOR_RENDER;
		return StateChanged.NONE;
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
            
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
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
	
	
	
	@Override
	public void processPreRender() {
		
		//super.processPreRender();
		Tab topParent = null;
		UIComponent possibleMatch = getParent();
		while (possibleMatch != null){
			if (possibleMatch instanceof Tab){
				topParent = (Tab)possibleMatch;
			}
			possibleMatch = possibleMatch.getParent();
		}
		
		if (topParent != null){
			if (topParent.isActiveTab()){
				List<UIComponent> children = getChildren();
				for (UIComponent c : children){
					if (c instanceof XUIComponentBase){
						((XUIComponentBase) c).processPreRender();
					}
				}
			}
		} else {
			if (isActiveTab()){
				List<UIComponent> children = getChildren();
				for (UIComponent c : children){
					if (c instanceof XUIComponentBase){
						((XUIComponentBase) c).processPreRender();
					}
				}
			}
		}
	}
	
}