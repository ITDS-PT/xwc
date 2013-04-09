package netgest.bo.xwc.framework.properties;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface XUIProperty {
	
	String name() default "[inherit]";
	String description() default "[javadoc]";
	String label();
	XUIPropertyVisibility visibility() default XUIPropertyVisibility.PUBLIC;
	
}
