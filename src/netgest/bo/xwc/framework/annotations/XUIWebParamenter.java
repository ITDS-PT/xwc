package netgest.bo.xwc.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XUIWebParamenter {

	public String name();
	public String defaultValue();
	
}