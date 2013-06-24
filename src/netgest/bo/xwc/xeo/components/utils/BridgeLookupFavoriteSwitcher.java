package netgest.bo.xwc.xeo.components.utils;

import java.util.List;

import netgest.bo.runtime.EboContext;

/**
 * 
 * Represents an interface to implement the switcher of favorites
 * for a given user/bridge
 * 
 * @author PedroRio
 *
 */
public interface BridgeLookupFavoriteSwitcher {

	/**
	 * 
	 * Switches elements from the list
	 * 
	 * @param original The list (ordered by ratio in ascending order) of favorites to 
	 * @param bouisToAdd The list of bouis selected
	 * @param ctx The {@link EboContext} to be able to execute queries
	 * 
	 * @return
	 */
	public List<LookupFavorites> replaceFavorites(List<LookupFavorites> original, 
			long[] bouisToAdd, int numFavorites);
	
}
