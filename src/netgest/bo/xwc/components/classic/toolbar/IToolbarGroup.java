package netgest.bo.xwc.components.classic.toolbar;

import java.util.List;

import javax.faces.component.UIComponent;

/**
 * 
 * Represents a component which is a group of elements (menus or items)
 * that are meant to be placed in a toolbar
 *
 */
public interface IToolbarGroup {
	
	/**
	 * 
	 * Retrieve the list of elements (in the proper order)
	 * to be appended as children in a toolbar (main toolbar)
	 * 
	 * @return The list of components to be appended as children
	 * of a toolbar
	 */
	public List<UIComponent> getComponentList();

}
