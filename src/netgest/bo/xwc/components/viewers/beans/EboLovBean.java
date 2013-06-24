package netgest.bo.xwc.components.viewers.beans;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.system.boApplication;
import netgest.bo.utils.XeoApplicationLanguage;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

public class EboLovBean extends XEOEditBean {

	public Map<Object, String> languagesLovMap = getLanguagesLovMap();
	
	public Map<Object, String> getLanguagesLovMap() {
    	Map<Object, String> olanguagesMap = new LinkedHashMap<Object, String>();
    	HashSet<XeoApplicationLanguage> hs=boApplication.getDefaultApplication().getAllApplicationLanguages();
    	Iterator<XeoApplicationLanguage> it =hs.iterator();
    	XeoApplicationLanguage apl;
    	while(it.hasNext()){
    		apl=(XeoApplicationLanguage) it.next();
    		olanguagesMap.put(apl.getCode(), apl.getDescription());
    	}
    	return olanguagesMap;
    }
    
    
    public void setLanguagesLovMap()
    {
    	languagesLovMap = getLanguagesLovMap();
    }
	
}
