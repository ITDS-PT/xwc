package netgest.bo.xwc.xeo.components.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefObjectFilter;
import netgest.bo.preferences.Preference;
import netgest.bo.preferences.PreferenceManager;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectListBuilder;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.xeo.components.BridgeLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Class to support dealing with BridgeLookup Favorites
 * 
 * @author PedroRio
 *
 */
public class LookupFavorites implements Comparable<LookupFavorites>{

	
	public enum PROPERTIES{
		BOUI,
		RATIO,
		NUMBER_TIMES_USED,
		DATE_USED
	}
	
	
	/**
	 * The Logger
	 */
	private static final Logger logger = Logger.getLogger(LookupFavorites.class); 
	
	/**
	 * Date when the instance was last selected
	 */
	private Date dateLastUsed;
	
	/**
	 * The boui of the object
	 */
	private long boui;
	
	/**
	 * Number of times the instance was selected
	 */
	private int numberSelections;
	
	
	/**
	 * The usage ratio
	 */
	private double ratio;
	
	
	public LookupFavorites(final Date date, final long boui, final int numberSelec, final double ratio){
		this.dateLastUsed = date;
		this.boui = boui;
		this.numberSelections =numberSelec;
		this.ratio = ratio;
		
	}
	
	/**
	 * 
	 * The boui of the current instance
	 * 
	 * @return
	 */
	public long getBoui() {
		return boui;
	}


	/**
	 * 
	 * The date last used
	 * 
	 * @return
	 */
	public Date getDateLastUsed() {
		return dateLastUsed;
	}

	/**
	 * 
	 * The number of selections
	 * 
	 * @return
	 */
	public int getNumberSelections() {
		return numberSelections;
	}

	/**
	 * 
	 * The ration
	 * 
	 * @return
	 */
	public double getRatio() {
		return ratio;
	}
	
	/**
	 * Increment usage
	 */
	public void incrementUsage(){
		this.numberSelections++;
	}
	
	
	/**
	 * 
	 * Set the date when this element was last used
	 * 
	 * @param d
	 */
	public void setDateLastUsed(Date d){
		this.dateLastUsed = d;
	}
	
	/**
	 * 
	 * Sets a new ration
	 * 
	 * @param newRatio
	 */
	public void setRatio(double newRatio){
		this.ratio = newRatio;
	}

	/**
	 * 
	 * Decodes a list of favotires from a JSONArray
	 * 
	 * @param favs The list of favorites
	 *  
	 * @return A list of favorites ordered by ratio in ascending order
	 */
	public static List<LookupFavorites> getFavorites(JSONArray favs){
		
		List<LookupFavorites> favorites = new LinkedList<LookupFavorites>();
		for (int k = 0, length = favs.length(); k < length; k++ )
		{
			try {
				JSONObject obj = favs.getJSONObject(k);
				favorites.add(new LookupFavorites(
						new Date(obj.getLong(PROPERTIES.DATE_USED.name())),
						obj.getLong(PROPERTIES.BOUI.name()),
						obj.getInt(PROPERTIES.NUMBER_TIMES_USED.name()),
						Float.valueOf(obj.getString(PROPERTIES.RATIO.name()))
				));
			} catch (JSONException e) {
				logger.warn(e);
			}
		}
		//Sort the favorites by ratio (guaranteed as compareTo is done
		//using the ration for each favorite
		Collections.sort(favorites);
		return favorites;
	}
	
	
	public static List<LookupFavorites> getFavorites(String objectName, 
			String bridgeName, EboContext ctx){
		
		Preference p = getPreference(objectName, bridgeName, ctx.getBoSession().getUser().getUserName());
		if( p != null ) {
			Object jsonArraPref = p.get(BridgeLookup.PREFERENCE_NAME);
	    	JSONArray array = null;
	    	if (jsonArraPref != null && jsonArraPref.toString().length() > 0){
	    		String jsonArra = jsonArraPref.toString();
	    		try {
					array = new JSONArray(jsonArra);
					List<LookupFavorites> lst = LookupFavorites.getFavorites(array);
		    		return lst;
				} catch (JSONException e) {
					logger.warn(e);
				}
	    		
	    	}
		}
		else {
	    	throw new RuntimeException("Could not load preference with objectName: " 
	    			+ objectName +" and bridgeName: " + bridgeName);
		}
		return new ArrayList<LookupFavorites>();
	}
	
	/**
	 * 
	 * Retrieve a preference for the favorites of a given user/model/bridge triplet
	 * 
	 * @param objectName XEO Modelname
	 * @param bridgeName he name of the bridge
	 * @param username The user name
	 * 
	 * @return A preference for the favorites
	 */
	public static Preference getPreference(String objectName, String bridgeName, String username){
		PreferenceManager manager = boApplication.getDefaultApplication().getPreferencesManager();
    	Preference pref = manager.getUserPreference(BridgeLookup.PREFERENCE_PREFIX+ objectName+
    			BridgeLookup.PREFERENCE_SEPARATOR+bridgeName,username);
    	return pref;
	}
	
	/**
	 * 
	 * Retrieves the list of boui from the preference
	 * 
	 * @param objectName The name of the object
	 * @param bridgeName The name of the bridge
	 * @param ctx The ebo context
	 * @param reverse If the list should be ordered descending
	 * @param max the maximum number of elements to return
	 * 
	 * @return A list of bouis
	 */
	public static long[] getBouisFromPreference(String objectName, String bridgeName, EboContext ctx, boolean reverse, int max) {
		
		
		Preference pref = getPreference(objectName, bridgeName,
				ctx.getBoSession().getUser().getUserName());
    	
    	try{
	    	Object jsonArraPref = pref.get(BridgeLookup.PREFERENCE_NAME);
	    	JSONArray array = null;
	    	if (jsonArraPref != null && jsonArraPref.toString().length() > 0){
	    		String jsonArra = jsonArraPref.toString();
	    		array = new JSONArray(jsonArra);
	    		List<LookupFavorites> lst = LookupFavorites.getFavorites(array);
	    		if (reverse)
	    			Collections.reverse(lst);
	    		long[] result = new long[lst.size()];
	    		int k = 0;
	    		for (Iterator<LookupFavorites> it = lst.iterator() ; it.hasNext();){
	    			result[k] = it.next().getBoui();
	    			k++;
	    			if (k == max)
	    				break;
	    		}
	    		if (result.length == 0)
	    			return new long[]{};
	    		return result;
	    	}
	    }catch (JSONException e){
    		e.printStackTrace();
    	}
    	return new long[]{0};
	}
	
	/**
	 * 
	 * Eliminates non-existing (e.g. deleted) objects from a preference list
	 * 
	 * @param obj The object (because of the
	 * @param bridgeName The bridge name
	 
	 */
	public static void eliminateDeletedObjectsFromPreference(boObject obj, String bridgeName){
		
		assert obj != null : "Can only apply to valid objects";
		assert bridgeName != null : "Can only apply to valid bridges";
		
		String objectName = obj.getName();
		EboContext ctx = obj.getEboContext();
		long[] bouisToCheck = getBouisFromPreference(obj.getName(), bridgeName, ctx,false,0);
		if (bouisToCheck.length == 0)
			bouisToCheck = new long[]{0};
		boObjectList list = boObjectList.list(ctx, "boObject", bouisToCheck);

		if (list.getRowCount() == bouisToCheck.length)
			return ; //Nothing to remove, all objects exist
		
		
		List<Long> bouisAsListTotal = new LinkedList<Long>();
		List<Long> bouisFoundInBoql = new LinkedList<Long>();
		for (long bouiToAdd : bouisToCheck){
			bouisAsListTotal.add(Long.valueOf(bouiToAdd));
		}
		
		list.beforeFirst();
		while (list.next()){
			try {
				boObject current = list.getObject();
				bouisFoundInBoql.add(Long.valueOf(current.getBoui()));
			} catch (boRuntimeException e) {
				logger.warn(e);
			} 
		}
		
		//Check which objects were eliminated
		bouisAsListTotal.removeAll(bouisFoundInBoql);
			
		Map<Long,Long> lookupBouis = new HashMap<Long, Long>();
		for (Iterator<Long> it = bouisAsListTotal.iterator(); it.hasNext();){
			lookupBouis.put(it.next(), null);
		}
		
		//Remove them from the list
		List<LookupFavorites> favorites = getFavorites(objectName, bridgeName, ctx);
		for (Iterator<LookupFavorites> it = favorites.iterator(); it.hasNext();){
			LookupFavorites curr = it.next();
			if (lookupBouis.containsKey(Long.valueOf(curr.getBoui()))){
				try{
					boApplication.getDefaultApplication().getObjectManager().loadObject(ctx, curr.getBoui());
				}catch (boRuntimeException e ){
					it.remove();
				}
			}
		}
		
		//Dynamic filter checking
		boDefAttribute attDefinition = boDefHandler.getBoDefinition(objectName).getAttributeRef(bridgeName);
		boDefObjectFilter[] objFilters = attDefinition.getObjectFilter();
		if (objFilters != null && objFilters.length == 1){
			String filterBoqlQuery = obj.getAttribute(bridgeName).getFilterBOQL_query();
			if (filterBoqlQuery != null && !"".equalsIgnoreCase(filterBoqlQuery)){
				boObjectList listDynamicFilter = new boObjectListBuilder(ctx, filterBoqlQuery).build();
				for (Iterator<LookupFavorites> it = favorites.iterator(); it.hasNext();){
					LookupFavorites curr = it.next();
					if (!listDynamicFilter.haveBoui(curr.getBoui())){
						it.remove();
					}
				}
			}
		}
		
		//Reflect changes back to the preferences
		Preference pref = getPreference(objectName, bridgeName, ctx.getBoSession().getUser().getUserName());
		JSONArray value = LookupFavorites.encodeFavoritesAsJSON(favorites);
    	pref.put(BridgeLookup.PREFERENCE_NAME, value.toString());
    	pref.savePreference();
			
		 
		
	}
	
	/**
	 * 
	 * Encode the list of favorites as a JSON Array
	 * 
	 * @param favs The list of favorites (expected to be ordered by ratio)
	 * 
	 * @return A JSON array representing the list of favorites (to be saved as a property)
	 */
	public static JSONArray encodeFavoritesAsJSON(List<LookupFavorites> favs){
		
		JSONArray result = new JSONArray();
		LookupFavorites curr = null;
		for (Iterator<LookupFavorites> it = favs.iterator(); it.hasNext(); ){
			curr = it.next();
			JSONObject obj = new JSONObject();
			try {
				obj.put(PROPERTIES.BOUI.name(), curr.getBoui());
				obj.put(PROPERTIES.NUMBER_TIMES_USED.name(), curr.getNumberSelections());
				obj.put(PROPERTIES.DATE_USED.name(), curr.getDateLastUsed().getTime());
				obj.put(PROPERTIES.RATIO.name(), curr.getRatio());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result.put(obj);
		}
		return result;
	}
	
	/**
	 * 
	 * Creates a new LookupFavorites for insertion in a list
	 * 
	 * @param boui The boui of the favorite
	 * 
	 * @return
	 */
	public static LookupFavorites createNewFavorite(long boui){
		return new LookupFavorites(new Date(System.currentTimeMillis()), boui, 1,1);
	}

	@Override
	public int compareTo(LookupFavorites o) {
		if (this.getRatio() < o.getRatio())
			return -1;
		else if (this.getRatio() > o.getRatio())
			return 1;
		else
			return 0;
	}
	
	@Override
	public String toString(){
		return this.boui + "["+ this.getRatio()+","+ getNumberSelections()+"]";
	}
	
}
