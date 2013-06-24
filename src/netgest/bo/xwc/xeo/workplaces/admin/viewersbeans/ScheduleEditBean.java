package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.robots.blogic.boScheduleThreadBussinessLogic;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.utils.StringUtils;

public class ScheduleEditBean extends XEOEditBean {
	
	public void executeNow(){
		String description = "";
		boObject schedule = getXEOObject();
		try {
			description = schedule.getAttribute( "description" ).getValueString();
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
		if (StringUtils.isEmpty( description ))
			description = "";
		
		boScheduleThreadBussinessLogic logic = 
				new boScheduleThreadBussinessLogic( 
						boApplication.getDefaultApplication() , description );
		logic.setSchedule( schedule );
		logic.execute();
		
		
	}

	
}
