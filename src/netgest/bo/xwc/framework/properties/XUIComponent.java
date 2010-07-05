package netgest.bo.xwc.framework.properties;

public @interface XUIComponent {
	 
	String name();
	String label();
	String description() default "[n/d]";
	
}
