package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.xeo.workplaces.admin.localization.MainAdminBeanMessages;


public class UsersDataListConnector extends GenericDataListConnector {
	
	
	private Map<String,Long> lastActivity_User=new HashMap<String,Long>();
	
	public UsersDataListConnector() {
		super();
		this.createColumn("USER", MainAdminBeanMessages.USER_NAME.toString());
		this.createColumn("CLIENT_NAME",MainAdminBeanMessages.CLIENT_NAME.toString());
		this.createColumn("CREATED_TIME",MainAdminBeanMessages.CREATED_TIME.toString());
		this.createColumn("LAST_ACTIVITY",MainAdminBeanMessages.LAST_ACTIVITY.toString());
		this.createColumn("USERNAME","Username");
	}

	@Override
	public void refresh() {
		super.refresh();
		
		long maxSessionTime=System.currentTimeMillis()-1200000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		boSession[] sessionsActive = boApplication.currentContext().getEboContext().getBoSession()
				.getApplication().getSessions().getActiveSessions();
		
		for (int i = 0; i <  sessionsActive.length; i++) {
			if ( sessionsActive[i].getUser().getName().equalsIgnoreCase("SYSTEM") )
				continue;
			
			//Only Active on the last 20 minutes
			String username= sessionsActive[i].getUser().getUserName();
			if (lastActivity_User.containsKey( username))
			{
				long existentTime=lastActivity_User.get(username);
				if (sessionsActive[i].getLastActivity().getTime()>existentTime)
				{
					removeFromList(username);
					lastActivity_User.put(username, sessionsActive[i].getLastActivity().getTime());
				}
				else
					continue;
					
			}
			else
				lastActivity_User.put(username, 
						sessionsActive[i].getLastActivity().getTime());
			
					
			if ( maxSessionTime<sessionsActive[i].getLastActivity().getTime())
			{
				this.createRow();
				
				this.createRowAttribute("USER",  sessionsActive[i].getUser().getName());
				this.createRowAttribute("CLIENT_NAME", sessionsActive[i].getClientName());
				this.createRowAttribute("CREATED_TIME", dateFormat.format(sessionsActive[i].getCreatedTime()));
				this.createRowAttribute("LAST_ACTIVITY", dateFormat.format(sessionsActive[i].getLastActivity()));
				this.createRowAttribute("USERNAME", sessionsActive[i].getUser().getUserName());
			}
		}
	}
	
	private void removeFromList(String user)
	{
		Iterator<Map<String, Object>> eRows=this.rows.iterator();
		
		while (eRows.hasNext())
		{
			Map<String, Object> cRow=eRows.next();
			String existUser=(String)cRow.get("USERNAME");
			if (user.equals(existUser))
			{
				eRows.remove();
			}
			
		}
	}
	
}
