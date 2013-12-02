package netgest.bo.xwc.framework.errorLogging;

import java.util.List;

/**
 * Interface to be implemented by beans, so that when an error occurs
 * debug info can be recorded
 *
 */
public interface DebugInfo {
	 
	public void addDebugInfo(List<String> debug);

}
