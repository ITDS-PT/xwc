package netgest.bo.xwc.components.template.renderers;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;

public class ToolBarRenderer extends TemplateRenderer {
	
	@Override
	public StateChanged wasStateChanged( XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties ) {
		updateProperties.add( component.getStateProperty( "disabled" ) );
		updateProperties.add( component.getStateProperty( "visible" ) );
		StateChanged changed = super.wasStateChanged( component, updateProperties );
		
		Iterator<UIComponent> childs =  component.getChildren().iterator();
        while( childs.hasNext() ) {
        	UIComponent currChild = childs.next();
        	if (currChild instanceof Menu){
                Menu oMenuChild = (Menu)currChild;
                if( oMenuChild.isRendered() ) {
                	if( menuWasChanged( oMenuChild ) ) {
                		if (changed != StateChanged.FOR_RENDER)
                			changed = StateChanged.FOR_UPDATE;
                		if (oMenuChild.getChildCount() > 0)
                			recursiveWasStateChangedForChildren( oMenuChild, changed );
                	}
                }
            }
        }
        return changed;
	}
	
	public boolean menuWasChanged(Menu menu){
		Set<Entry<String,XUIBaseProperty<?>>> props = menu.getStateProperties();
		Iterator<Entry<String,XUIBaseProperty<?>>> it = props.iterator();
		while (it.hasNext()){
			Entry<String,XUIBaseProperty<?>> entry = it.next();
			if (entry.getValue().wasChanged())
				return true;
		}
		return false;
	}
	
	private void recursiveWasStateChangedForChildren(Menu m, StateChanged changed){
		Iterator<UIComponent> childs =  m.getChildren().iterator();
        while( childs.hasNext() ) {
        	UIComponent currChild = childs.next();
        	if (currChild instanceof Menu){
                Menu oMenuChild = (Menu)currChild;
                if( oMenuChild.isRendered() ) {
                	if( menuWasChanged( oMenuChild ) ) {
                		if (changed != StateChanged.FOR_RENDER)
                			changed = StateChanged.FOR_UPDATE;
                		if (oMenuChild.getChildCount() > 0)
                			recursiveWasStateChangedForChildren( oMenuChild, changed );
                	}
                	
                }
            }
        }
	}
	
	
}
