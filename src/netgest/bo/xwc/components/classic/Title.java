package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIOutput;

public class Title extends XUIOutput {

    private XUIViewStateProperty<String> valueExpression = new XUIViewStateProperty<String>( "valueExpression", this );
    private XUIViewStateProperty<Object> renderedValue  = new XUIViewStateProperty<Object>( "renderedValue", this );

    @Override
    public boolean wasStateChanged() {
        if( !super.wasStateChanged() ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return true;
            }
        }
        else {
            return true;
        }
        return false;    
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
			if( oWnd != null ) {
				oWnd.setTitle( String.valueOf( ((Title)component).getValue() ) );
			}
			else {
				XVWScripts.setTitle( String.valueOf( ((Title)component).getValue() ) );
			}
		}
	}
}
