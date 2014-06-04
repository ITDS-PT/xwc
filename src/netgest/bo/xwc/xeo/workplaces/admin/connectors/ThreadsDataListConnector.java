package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.rmi.RemoteException;

import netgest.bo.runtime.robots.ejbtimers.xeoEJBTimer;
import netgest.bo.system.IboAgentsController;
import netgest.bo.system.Logger;
import netgest.bo.system.boAgentsControllerEjbTimer;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.MainAdminBeanMessages;


public class ThreadsDataListConnector extends GenericDataListConnector {
	ThreadType type;

	private static final Logger logger = Logger.getLogger( ThreadsDataListConnector.class );

	public ThreadsDataListConnector() {
		super();

		if ("userThreads".equalsIgnoreCase(this.getType()))
			this.type = ThreadType.USER_THREADS;
		else 
			this.type = ThreadType.EJB_TIMERS;

		this.createColumn("NAME", MainAdminBeanMessages.NAME.toString());

		switch (this.type) {
		case USER_THREADS:
			this.createColumn("CLASS_EJB_NAME", MainAdminBeanMessages.CLASS_NAME.toString());
			break;
		case EJB_TIMERS:
			this.createColumn("CLASS_EJB_NAME", MainAdminBeanMessages.EJB_NAME.toString());
			break;
		}

		this.createColumn("INTERVAL", MainAdminBeanMessages.INTERVAL.toString());
		this.createColumn("ACTIVE", MainAdminBeanMessages.ACTIVE.toString());

	}

	@Override
	public void refresh() {
		super.refresh();
		String[] threadsNames = this.getBoApplication().getApplicationConfig().getThreadsName();
		String[] threadsClass = this.getBoApplication().getApplicationConfig().getThreadsClass();
		String[] threadsEjbName = this.getBoApplication().getApplicationConfig().getThreadsEjbName();
		String[] threadsInterval = this.getBoApplication().getApplicationConfig().getThreadsInterval();



		for (int i = 0; i <  threadsNames.length; i++) {
			String name = threadsNames[i];
			boolean isRunning = this.isThreadActive(name);
			
			this.createRow();

			this.createRowAttribute("NAME", name);

			switch (this.type) {
			case USER_THREADS:
				this.createRowAttribute("CLASS_EJB_NAME",threadsClass[i]);
				break;
			case EJB_TIMERS:
				this.createRowAttribute("CLASS_EJB_NAME", threadsEjbName[i]);
				break;
			}

			this.createRowAttribute("INTERVAL", threadsInterval[i]); 
			this.createRowAttribute("ACTIVE", isRunning ? "Yes" : "No"); 
		}
	}

	public String getType() {
		return this.getBoApplication().getApplicationConfig().getThreadsType();
	}

	private boApplication getBoApplication() {
		return boApplication.currentContext().getEboContext().getBoSession().getApplication();
	}

	private int getThreadIndex(String name) {
		String[] threadsNames = this.getBoApplication().getApplicationConfig().getThreadsName();

		for (int i = 0; i < threadsNames.length; i++) {
			if (threadsNames[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}


	public boolean isThreadActive(String name) {
		boolean isActive = false;

		try {
			switch (this.type) {
			case USER_THREADS:
				Thread thread = 
					(Thread) this.getBoApplication().getBoAgentsController().getThreadByName(name)[0];
				
				isActive = thread.isAlive();
				break;
			case EJB_TIMERS:
				xeoEJBTimer ejbTimer = 
					(xeoEJBTimer) this.getBoApplication().getBoAgentsController().getThreadByName(name)[0];
				isActive = ejbTimer.isActive(name);
				break;
			}
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isActive;
	}

	public boolean startThread(String name, StringBuilder message) {
		boolean result = true;
		try {
			IboAgentsController controller = this.getBoApplication().getBoAgentsController();
			if (controller != null){
				controller.chekAndStartThread(this.getThreadIndex(name));
			} else {
				result = false;
				message.append(ComponentMessages.THREADS_CONTROLLER_NOT_FOUND.toString());
			}
		} catch (Exception e) {
			result = false;
			message.append(e.getMessage());
			logger.warn( String.format("Could not start thread %s", name ) , e );
		}
		return result;
	}


	public boolean stopThread(String name, StringBuilder message) {
		
		IboAgentsController controller = this.getBoApplication().getBoAgentsController();
		boolean result = true;
		if (controller != null){
			try {
				switch (this.type) {
				case USER_THREADS:
					Thread thread = 
						(Thread) controller.getThreadByName(name)[0];
					thread.interrupt();
					break;
				case EJB_TIMERS:
					((boAgentsControllerEjbTimer)controller).suspendAgent(name);
					break;
				}
			} catch (Exception e) {
				result = false;
				message.append( e.getMessage() );
				logger.warn( String.format("Could not stop thread %s",name) , e );
			}
		} else {
			message.append(ComponentMessages.THREADS_CONTROLLER_NOT_FOUND.toString());
			result = false;
		}
		return result;
	}


	private enum ThreadType {
		USER_THREADS, EJB_TIMERS
	}

}
