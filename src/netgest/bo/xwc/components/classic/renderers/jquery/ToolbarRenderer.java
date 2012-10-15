package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.BUTTON;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.SPAN;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.ToolBar.IconPosition;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

/**
 * 
 * Toolbar is rendered using JQuery UI framework.
 * It can be customized by overiding thw following css classes:
 * 
 * xwc-toolbar - Div containing the buttons of the toolbar
 * xwc-toolbar-button - Class aplied to each button in the toolbar
 * 
 * If you need to override a specific toolbar, give an id to the toolbar
 * and use that id in the css in the form of
 * #id { CSS Styles } and
 * #id button { CSS STYLES }
 * 
 * If you need to style the buttons when they: are disabled/mouse hover/clicked
 * 
 * mouse hover -> button.xwc-toolbar-button.ui-state-hover
 * clicked -> button.xwc-toolbar-button.ui-state-active
 * disabled -> button.xwc-toolbar-button[disabled]
 * 
 * 
 * @author PedroRio
 *
 */
public class ToolbarRenderer extends JQueryBaseRenderer {

	private StringBuilder generatedCss;
	
	private StringBuilder createButtonScript(List<UIComponent> components, IconPosition position){
		StringBuilder b = new StringBuilder();
		b.append( "$(function() {" );
        
        for ( UIComponent child: components ) {
			if (child instanceof Menu){
				Menu m = (Menu) child;
				JQueryWidget widget = createButtonWidget( m , position);
				addActionToButton( m, widget );
		        b.append( widget.build() );
			}
		}
		b.append( " }); " );
        return b;
	}
	
	
	
	protected void createScriptForMenuIcon(Menu m, IconPosition position  ){
		if (generatedCss == null){
			generatedCss = new StringBuilder(200);
			generatedCss.append( "$(function() {" );
		}

		if (m.getId() != null){
			JQueryBuilder builder = new JQueryBuilder();
			builder.selectorById( getButtonId( m )  ).
				command( "children('.ui-button-text')" );
			
			if (position == IconPosition.LEFT)
				builder.command( "prepend( '<img src=\""+m.getIcon()+"\" style=\"display:inline;padding:2px;vertical-align:middle\" />' );");
			else if (position == IconPosition.TOP )
				builder.command( "prepend( '<img src=\""+m.getIcon()+"\" style=\"display:block;padding:2px;vertical-align:middle;margin-left:auto; margin-right:auto\" />' );");
			
			generatedCss.append( builder.build());
			
		}
	}

	public JQueryWidget createButtonWidget( Menu m, IconPosition pos ) {
		JQueryWidget widget = WidgetFactory.createWidget( JQuery.BUTTON );
		if (StringUtils.hasValue( m.getIcon() ) ){
			createScriptForMenuIcon( m, pos );
		}
		widget.componentSelectorById( m.getId() + "_btn" ).create();
		if (m.isVisible())
			widget.show();
		else
			widget.hide();
		
		if (m.isDisabled())
			widget.disable();
		else
			widget.enable();
		
			
		return widget;
	}

	public void addActionToButton( Menu m, JQueryWidget widget ) {
		if (m.getActionExpression() != null)
			widget.click( XVWScripts.getAjaxCommandScript( m, XVWScripts.WAIT_DIALOG ) + "; return false; " );
		else
			widget.click( " return false; " );
	}
	
	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        
		ToolBar tb = (ToolBar) component;
		XUIResponseWriter w = getResponseWriter();
		
		if (!tb.isRenderedOnClient()){
			createInitialMarkup( tb, w );
			addScriptFooter( tb.getId(), createButtonScript( tb.getChildren(), tb.getIconPosition() ).toString() );
			if (shoudIncludeCss()){
				generatedCss.append("});");
				addScriptFooter( tb.getId() + "_xwc-icons", generatedCss.toString() );
			}
		}	
		else{
			addScriptFooter(tb.getId(), createUpdateScript( tb )); 
		}
		
    }

	protected boolean shoudIncludeCss() {
		return generatedCss != null;
	}
	
	protected String getButtonId(Menu m){
		return m.getId() + "_btn";
	}

	public void createInitialMarkup( ToolBar tb, XUIResponseWriter w ) throws IOException {
		w.startElement( DIV );
			w.writeAttribute( ID, tb.getClientId() );
			
			w.startElement( DIV );
			w.writeAttribute( ID, tb.getId() ); 
			w.writeAttribute( CLASS , "ui-widget-header ui-corner-all xwc-toolbar");
		List<UIComponent> children = tb.getChildren();
		for ( UIComponent child: children ) {
			if (child instanceof Menu){
				Menu m = (Menu) child;
				w.startElement( BUTTON );
					w.writeAttribute( ID, getButtonId( m ) );
					w.writeAttribute( CLASS, "xwc-toolbar-button" );
					w.startElement( SPAN );
						w.write( m.getText() );
					w.endElement( SPAN );
				w.endElement( BUTTON );
			}
		}
	}

    public String createUpdateScript( ToolBar tb  ) {
    	StringBuilder b = new StringBuilder();
		b.append( "$(function() {" );
        
        for ( UIComponent child: tb.getChildren() ) {
			if (child instanceof Menu){
				Menu m = (Menu) child;
				
				JQueryWidget widget = WidgetFactory.createWidget( JQuery.BUTTON );
				generateMenuUpdateScript( tb, m, widget );
				
				b.append(widget.build());
			}
		}
        
        
        
		b.append( " }); " );
		return b.toString();
	}

	private JQueryWidget generateMenuUpdateScript( ToolBar toolBar, Menu menu, JQueryWidget widgetBuilder ) {
		//Re initialize the buttons
		widgetBuilder.componentSelectorById( menu.getId() + "_btn"); 
		widgetBuilder.create();
		if (menu.isDisabled() || toolBar.isDisabled())
			widgetBuilder.updateOption( "disabled", true );
		else
			widgetBuilder.updateOption( "disabled", false );
		
		if (menu.isVisible() && toolBar.isVisible()){
			widgetBuilder.show();
		} else
			widgetBuilder.hide();
		
		
		
		return widgetBuilder;
	}

	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	
    	ToolBar tb = (ToolBar) component;
    	XUIResponseWriter w = getResponseWriter();
    	if (!tb.isRenderedOnClient()){
    		w.endElement( DIV );
    		w.endElement( DIV );
    	}
    	
    	generatedCss = null;
    }
	
}
