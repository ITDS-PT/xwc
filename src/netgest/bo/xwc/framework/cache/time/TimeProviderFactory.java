package netgest.bo.xwc.framework.cache.time;

import java.util.Date;

public class TimeProviderFactory {
	
	public static TimeProvider getTimeProvider(){
		
		return new TimeProvider() {
			
			@Override
			public Date getCurrentTime() {
				return new Date( System.currentTimeMillis() ) ;
			}
		};
	}
}
