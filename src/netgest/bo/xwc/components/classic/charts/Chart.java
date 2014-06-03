package netgest.bo.xwc.components.classic.charts;

import java.io.OutputStream;

/**
 * 
 * Represents common methods to Charts
 * 
 * 
 * @author PedroRio
 *
 */
public interface Chart {
	
	
	/**
	 * 
	 * Renders the chart to a given outputstream
	 * 
	 * @param out The stream to output the chart to
	 * @param force If the chart is in a non-image format, force it to render as an image 
	 */
	public void outputChartAsImageToStream(OutputStream out, boolean force);
	
	public static final String FORCE_IMAGE_MODE = "forceImg";
	
}
