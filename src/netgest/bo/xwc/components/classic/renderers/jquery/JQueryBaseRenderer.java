package netgest.bo.xwc.components.classic.renderers.jquery;

import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class JQueryBaseRenderer extends XUIRenderer {

	protected void addScriptFooter(String id, String script){
		getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
				id, 
				script );
	}
	
	protected void includeHeaderScript(String id, String scriptPath){
		getRequestContext().getScriptContext().addInclude( XUIScriptContext.POSITION_FOOTER,
				id, 
				scriptPath );
	}
	
	protected void includeHeaderCss(String id, String cssPath){
		getRequestContext().getStyleContext().addInclude( 
				XUIScriptContext.POSITION_FOOTER,
				id, 
				cssPath );
	}
	
	/**
	 * 
	 * Determines whether the component is to be updated
	 * 
	 * @param component The component to check
	 * @return True if the component should be updated 
	 */
	protected boolean shouldUpdate(XUIComponentBase component){
		return !component.isRenderedOnClient() || component.wasStateChanged();
	}
	
	/**
	 * 
	 * Converts a Component id into a suitable id for a Jquery Selector
	 * 
	 * @param componentId The component identifier
	 * 
	 * @return A valid Id for a jquery selector
	 */
	protected String getIdForJquerySelector(String componentId){
		return JQueryBuilder.convertIdJquerySelector( componentId );	
	}
	
	/**
	 * 
	 * Converts a Component id into a suitable id for a JQuery Selector
	 * 
	 * @param componentId The component identifier
	 * 
	 * @return A valid Id for a JQuery selector
	 * @deprecated Use {@link JQueryBuilder#convertIdJquerySelector(String)} instead
	 */
	public static String convertIdJquerySelector(String componentId){
		return JQueryBuilder.convertIdJquerySelector( componentId );
	}
	
}
