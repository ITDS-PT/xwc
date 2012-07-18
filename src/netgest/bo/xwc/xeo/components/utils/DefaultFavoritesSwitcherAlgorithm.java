package netgest.bo.xwc.xeo.components.utils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import netgest.bo.utils.DateUtils;

public class DefaultFavoritesSwitcherAlgorithm implements
		BridgeLookupFavoriteSwitcher {

	@Override
	public List<LookupFavorites> replaceFavorites(
			List<LookupFavorites> original, long[] bouisToAdd, final int numFavorites) {

		updateElementsRatio(original);
		
		for (long currBoui: bouisToAdd){
			insertElementInList(original, currBoui, bouisToAdd, numFavorites * 2);
			Collections.sort(original);
		}
		return original;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param lst The list in which to insert/replace
	 * @param boui The boui to insert/replace
	 * @param listBouis The list of bouis to insert (to check if the boui 
	 * 		to remove was already inserted)
	 * @param maxSize The maximum size of the list
	 */
	protected void insertElementInList(List<LookupFavorites> lst, long boui, 
			long[] listBouis, int maxSizeList){
		boolean found = false;
		for (Iterator<LookupFavorites> it = lst.iterator(); it.hasNext(); ){
			LookupFavorites curr = it.next();
			if (curr.getBoui() == boui){
				//Update statistics
				curr.incrementUsage();
				curr.setDateLastUsed(new Date(System.currentTimeMillis()));
				curr.setRatio(calculateRatio(curr));
				found = true;
			}
		}
		//Element was not in the list
		if (!found){
			//Remove the element with the minimum ratio (unless it was inserted now in that case moeve to the next)
			
			//Create the element to add 
			LookupFavorites newFav = LookupFavorites.createNewFavorite(boui);
			newFav.setRatio(calculateRatio(newFav));
			
			if (boui > 0){
				if (lst.size() < maxSizeList){
					//Insert into the list
					lst.add(newFav);
				} else{ //We need to remove an existing one
					
					//Create a lookup table of elements to add to be quicker in checking if the element being
					//removed was not previously added
					Map<Long,Long> bouisLookup = new HashMap<Long, Long>();
					for (long bouiCurr : listBouis){
						bouisLookup.put(new Long(bouiCurr), null);
					}
					
					boolean removed = false;
					int k = 0;
					while (!removed && k < maxSizeList){
						LookupFavorites fav = lst.get(k);
						if ( !bouisLookup.containsKey(fav.getBoui() ) ){
							lst.remove(k);
							lst.add(newFav);
							removed = true;
						}
						k++;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * Iterates through every element of the list and updates the ratio (orders
	 * the result)
	 * 
	 * @param list
	 */
	protected void updateElementsRatio(List<LookupFavorites> list){
		
		for (Iterator<LookupFavorites> it = list.iterator(); it.hasNext(); ){
			LookupFavorites current = it.next();
			double newRatio = calculateRatio(current);
			current.setRatio(newRatio);
		}
		Collections.sort(list);
	}
	
	/**
	 * 
	 * Calculates the ratio for a given favorite 
	 * 
	 * @param favorite The favorite to calculate the ratio
	 * @param oldUsageDate The previous old date (used to check if there isn't goiing to be a big jump in ratio)
	 * 
	 * @return The ratio to use
	 */
	protected double calculateRatio(LookupFavorites favorite){
		int daysSinceUsage = DateUtils.diffInIntDays(favorite.getDateLastUsed(), new Date(System.currentTimeMillis()));
		daysSinceUsage = Math.abs(daysSinceUsage);
		if (daysSinceUsage == 0){
			daysSinceUsage = 1;
		}
		int numberUsage = favorite.getNumberSelections();
		
		double oldRatio = favorite.getRatio();
		double newRatio = (double) numberUsage / (daysSinceUsage * 2); 

		double difference = oldRatio / newRatio;
		//If the ratio would double, increase it only 25%
		if (difference > (2 * oldRatio))
			newRatio = oldRatio * 1.25;
		
		return  newRatio;
		
	}

}
