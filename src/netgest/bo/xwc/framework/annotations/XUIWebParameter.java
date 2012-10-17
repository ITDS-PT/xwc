package netgest.bo.xwc.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XUIWebParameter {

	public String name();
	public String defaultValue();
	
}
