package netgest.bo.xwc.components.classic.renderers.jquery.generators;

public class WidgetFactory {
	
	public enum JQuery{
		BUTTON("button"),
		TABS("tabs"),
		DATE_PICKER("datepicker"),
		DIALOG("dialog"),
		WINDOW("window"),
		AUTO_COMPLETE("fcbkcomplete");
		
		
		private String name;
		
		public String getName(){
			return name;
		}
		
		private JQuery(String name){
			this.name = name;
		}
	}
	
	public static JQueryWidget createWidget(JQuery widget){
		switch (widget){
			case BUTTON : return new JQueryWidget( "button" );
			case TABS : return new JQueryWidget( "tabs" );
			case DATE_PICKER : return new JQueryWidget( "datepicker" );
			case DIALOG : return new JQueryWidget( "dialog" );
			case WINDOW : return new JQueryWidget( "dialog" );
			case AUTO_COMPLETE : return new JQueryWidget( "fcbkcomplete" );
			default : return new JQueryWidget( "button" );
		}
	}

}
