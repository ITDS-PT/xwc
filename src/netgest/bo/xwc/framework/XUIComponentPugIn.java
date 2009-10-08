package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public abstract class XUIComponentPugIn {
	
	private boolean			 replaced = false;
	private XUIComponentBase component;
	
	/**
	 * Returns the component where the plugIn property was set 
	 * 
	 * @return {@link XUIComponentBase} - Where the component was applied.
	 */
	public XUIComponentBase getComponent() {
		return component;
	}

	public void setComponent( XUIComponentBase component ) {
		this.component = component;
	}
	
	/**
	 * Replace the component in the tree with a new one.
	 * 
	 * @param component - The new {@link XUIComponentBase} 
	 */
	public void replaceComponent( XUIComponentBase component ) {
		this.component = component;
		replaced = true;
	}
	
	/**
	 * This method is called before the initComponent
	 */
	public void beforeInitComponent() {
		// Initialize the binding component
	}

	/**
	 * This method is called after the initComponent
	 */
	public void afterInitComponent() {
		// Initialize the binding component
	}

	/**
	 * This method is called before the preRender
	 */
	public void beforePreRender() {
		// Ajust the render settings of the rendered component
	}

	/**
	 * This method is called after the preRender
	 */
	public void afterPreRender() {
		// Ajust the render settings of the rendered component
	}
	
	/**
	 * Method that returns if the component should be rendered in a Ajax request
	 * @return true - to force the render of the component, false - let the component decide if it need's to be re-rendered on the client.
	 */
	public boolean wasStateChanged() {
		// Use this to force a rerender on client of the component 
		return false;
	}
	
	/**
	 * Method to check if the component was replaced by another
	 * @return boolean true - if the component was replaced, false - if is the original specified in the viewer.
	 */
	public boolean isReplaced() {
		return replaced;
	}

	
}
