package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIOutput;

/**
 * 
 * The {@link Title} component enables 
 * the user to set the title of the viewer
 * 
 * @author Joao Carreira
 *
 */
public class Title extends XUIOutput {

    /**
     * Evaluates the name that the title of the viewer is going to have
     */
	@RequiredAlways
    private XUIStateProperty<String> valueExpression = new XUIStateProperty<String>( "valueExpression", this );
	
	/**
     * An icon to put in the ptitle (before the text message)
     */
	@RequiredAlways
    private XUIBaseProperty<String> icon = new XUIBaseProperty<String>( "icon", this, "" );
	
    /**
     * Stores the rendered value of the title
     */
    private XUIStateProperty<Object> renderedValue  = new XUIStateProperty<Object>( "renderedValue", this );

    /**
     * 
     * Retrieve the path to the icon
     * 
     * @return A path to the icon
     */
    public String getIcon(){
    	return icon.getValue();
    }
    
    /**
     * 
     * Set the title icon
     * 
     * @param iconPath Path to the icon
     */
    public void setIcon(String iconPath){
    	this.icon.setValue(iconPath);
    }
    
    @Override
    public StateChanged wasStateChanged2() {
        if( super.wasStateChanged2() == StateChanged.NONE ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return StateChanged.FOR_RENDER;
            }
        }
        else {
            return StateChanged.FOR_RENDER;
        }
        return StateChanged.FOR_RENDER;    
    }

    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression.setValue( valueExpression ); 
        super.setValueExpression( "value", createValueExpression( valueExpression, String.class ) );
    }

    public String getValueExpression() {
        return valueExpression.getValue();
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }
    
	public static class XEOHTMLRenderer extends XUIRenderer {
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			Window oWnd = (Window)component.findParentComponent(Window.class);
			String icon = ((Title)component).getIcon();
			String iconPath = "";
			if (!"".equalsIgnoreCase(icon))
				iconPath = "<img src='"+icon+"' />";
			if( oWnd != null ) {
				oWnd.setTitle( iconPath + String.valueOf( ((Title)component).getValue() ) );
			}
			else {
				XVWScripts.setTitle( iconPath + String.valueOf( ((Title)component).getValue() ) );
			}
		}
	}
}
