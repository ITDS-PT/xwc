package netgest.bo.xwc.components.classic.mainRegions;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;

/**
 * 
 * An interface to represent regions of the main viewer that can be
 * rendered as an ExtJS region (can also be used to render any component)
 *
 */
public interface ExtJSRegionRenderer {

	/**
	 * 
	 * Render the Region to an ExtJS Configuration
	 * 
	 * @return An ExtJS rendering of a region
	 */
	public ExtConfig renderRegion();
	
	/**
	 * 
	 * Creates the extjs listener definition for the current
	 * 
	 * @return An ExtJS definition of a listener
	 */
	public ExtConfig getListeners();
	
}
