package netgest.bo.xwc.components.classic.mainRegions;

import netgest.bo.xwc.framework.XUIBaseProperty;

/**
 * An abstract class representing regions that can present on
 * the top/bottom extremes of the viewer 
 *
 */
public abstract class ExtremeRegion extends BaseRegion {

	/**
	 * The height of the region
	 */
	private XUIBaseProperty<String> height = 
		new XUIBaseProperty<String>("height", this, "50" );

	/**
	 * The maximum height of the region
	 */
	private XUIBaseProperty<String> maxHeight = 
		new XUIBaseProperty<String>("maxHeight", this, "150" );
	
	/**
	 * The minimum height of the region
	 */
	private XUIBaseProperty<String> minHeight = 
		new XUIBaseProperty<String>("minHeight", this, "20" );
	
	
	/**
	 * 
	 * Sets the height of the region
	 * 
	 * @param height The height in pixels
	 */
	public void setHeight(String height){
		this.height.setValue(height);
	}
	
	/**
	 * Retrieves the height of the region
	 * 
	 * @return The height in pixels
	 */
	public String getHeight(){
		return height.getValue();
	}
	
	/**
	 * 
	 * Sets the maximum height of the region (in pixels)
	 * 
	 * @param maxHeight In pixels
	 */
	public void setMaxHeight(String maxHeight){
		this.maxHeight.setValue(maxHeight);
	}
	
	/**
	 * 
	 * Retrieves the maximum height of the region (in pixels)
	 * 
	 * @return The maximum height in pixels
	 */
	public String getMaxHeight(){
		return maxHeight.getValue();
	}
	
	/**
	 * 
	 * Sets the minimum height of the region in pixels
	 * 
	 * @param minHeight The height in pixels
	 */
	public void setMinHeight(String minHeight){
		this.minHeight.setValue(minHeight);
	}
	
	/**
	 * 
	 * Retrieves the minimum height in pixels
	 * 
	 * @return The minimum height in pixels
	 */
	public String getMinHeight(){
		return minHeight.getValue();
	}
	
	
}
