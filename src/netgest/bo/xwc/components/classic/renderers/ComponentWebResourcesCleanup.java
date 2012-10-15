package netgest.bo.xwc.components.classic.renderers;

import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Interface to mark renderers that have specific knowledge
 * of how to clean up the html and javascript of a component 
 *
 */
public interface ComponentWebResourcesCleanup {

	/**
	 * 
	 * Retrieves the script to clean up resources used by a component
	 * 
	 * @param component The component to cleanup
	 * @return A String with the Javascript required to clean up the component
	 */
	public String getCleanupScript(XUIComponentBase component);
	
	
}
