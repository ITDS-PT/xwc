package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.HREF;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.ONCLICK;
import static netgest.bo.xwc.components.HTMLTag.A;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.LI;
import static netgest.bo.xwc.components.HTMLTag.UL;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.Tabs;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class TabsRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeChildren(XUIComponentBase component) throws IOException {
        
        XUIResponseWriter w = getResponseWriter();
        
        Tabs oTabs = (Tabs)component;
        Iterator<UIComponent> oChildsIt = component.getChildren().iterator();
        while( oChildsIt.hasNext() ) {
            Tab oChildTab = (Tab)oChildsIt.next();
            if( oChildTab.getId().equals( oTabs.getActiveTab() ) && oChildTab.isVisible() ) {
            	
            	w.startElement( DIV );
            		w.writeAttribute( ID, "#"+oChildTab.getId() );
            		w.writeAttribute( CLASS, "xwc-active-tab" );
            		
	            	if( oTabs.isRenderedOnClient() ) {
	            		oChildTab.forceRenderOnClient();
	            	}
            	
            	oChildTab.encodeAll();
            	
            	w.endElement( DIV );
                
            }
        }
    }
	
	public boolean isTabActive(Tab tab, String activeTabId){
		return tab.getId().equalsIgnoreCase( activeTabId );
	}

    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        
        Tabs tabs = (Tabs)component;
        XUIResponseWriter w = getResponseWriter();
        
        	int activeTabIndex = -1;
        	int countTabs = 0;
	        w.startElement( DIV );
	        	w.writeAttribute( ID, tabs.getClientId() );
	        	w.writeAttribute( CLASS, "xwc-tabs" );
	        	
	        	
	        Iterator<UIComponent> childrenIterator = component.getChildren().iterator();
	        w.startElement( UL );
	        w.writeAttribute( CLASS, "xwc-tab-navbar" );
	        while( childrenIterator.hasNext() ) {
	            Tab currentTab = (Tab)childrenIterator.next();
	            w.startElement( LI );
	            w.writeAttribute( CLASS, "xwc-tab-label" );
	            	w.startElement( A );
	            		w.writeAttribute( HREF, "#" + currentTab.getId() ); 
	            		w.writeAttribute( CLASS, "xwc-tab-label-link" );
	            		w.writeAttribute( ONCLICK , "javascript:" + XVWScripts.getAjaxCommandScript( currentTab, XVWScripts.WAIT_DIALOG ), null );
	            		w.write( currentTab.getLabel() );
	            	w.endElement( A );
	            w.endElement( LI );
	            
	            if (isTabActive( currentTab, tabs.getActiveTab() ))
	            	activeTabIndex = countTabs;
	            countTabs++;
	            
	        }
	        w.endElement( UL );
	        
	        Iterator<UIComponent> childrenIterator2 = component.getChildren().iterator();
	        while( childrenIterator2.hasNext() ) {
	            Tab currentTab = (Tab)childrenIterator2.next();
	            w.startElement( DIV );
            		w.writeAttribute( ID , currentTab.getId() , null );
            		w.writeAttribute( CLASS, "xwc-tab-content" );
            		
            	w.endElement( DIV );
	        }
	        
	        
	        JQueryWidget tabsWidget = WidgetFactory.createWidget( JQuery.TABS );
	        tabsWidget
	        	.componentSelectorById( tabs.getClientId() )
	        	.create()
	        	.option( "selected", activeTabIndex );
	        String s = tabsWidget.build();
	        addScriptFooter( tabs.getId(), s  );
	        
        
        
        
    } 

    @Override 
    public void encodeEnd(XUIComponentBase component) throws IOException {

        XUIResponseWriter w = getResponseWriter();
        w.endElement( DIV );
        
    }

}
	
	

