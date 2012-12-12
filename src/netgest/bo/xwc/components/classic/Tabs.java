package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.navigation.NavigatableComponent;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

/**
 * 
 * The {@link Tabs} component is a container for {@link Tab} elements which 
 * render a "Tab" element inside a web viewer. Tabs can be used to 
 * group information, to create divisions in a form and can 
 * be used in creative ways, such as using a set of tabs inside a form
 *	as a wizard, or tutorial.
 * 
 * @author jcarreira
 *
 */
public class Tabs extends XUIComponentBase implements NavigatableComponent
{
    /**
     * Sets the active tab inside the container
     */
    public XUIViewStateProperty<String> activeTab = new XUIViewStateProperty<String>( "activeTab", this );
    
    
    /**
     * Sets the type of layout of the container, 
     * can be adjusted to window, to parent and to the form
     */
    @Values({"fit-parent", "fit-window","form"})
    public XUIViewProperty<String> layout = new XUIViewProperty<String>( "layout", this, Layouts.LAYOUT_FIT_PARENT );
    /**
     * Height of the tabs component
     */
    public XUIViewProperty<String> height = new XUIViewProperty<String>( "height", this, "150px" );
    
    /**
     * Determines where the tab bar (where tab labels are rendered)
     *  is visible or not
     */
    public XUIBindProperty<Boolean> renderTabBar = new XUIBindProperty<Boolean>( "renderTabBar", this, true, Boolean.class );
    
    
    
    public void setRenderTabBar( String renderTabBarExpr ) {
    	this.renderTabBar.setExpressionText( renderTabBarExpr );
    }

    public void setRenderTabBar( boolean renderTabBar ) {
    	this.renderTabBar.setValue( renderTabBar );
    }
    
    
    public boolean getRenderTabBar() {
    	return this.renderTabBar.getEvaluatedValue();
    }
    
    private XUIBaseProperty<String> bodyStyle = new XUIBaseProperty<String>( "bodyStyle", this, ""  );
    
    public void setBodyStyle(String value){
    	this.bodyStyle.setValue(value);
    }
    
    public String getBodyStyle(){
    	return this.bodyStyle.getValue();
    }
    
    
    public void setLayout( String layout ) {
    	this.layout.setValue( layout );
    }
    
    public String getLayout() {
    	return this.layout.getValue();
    }

    public void setHeight( String layout ) {
    	this.height.setValue( layout );
    }
    
    public String getHeight() {
    	return this.height.getValue();
    }
    
    public void setActiveTab(String activeTab)
    {
        this.activeTab.setValue( activeTab );
        int k = 0;
        for (UIComponent c : getChildren()){
        	if (c.getId().equals( activeTab )){
        		activeTabIndex.setValue( Integer.valueOf( k ) );
        		break;
        	}
        	k++;
        }
    }

    public void setActiveTab( Tab activeTab )
    {
    	setActiveTab( activeTab.getId() );
    }
    
    private XUIViewStateProperty<Integer> activeTabIndex = 
    		new XUIViewStateProperty<Integer>( "activeTabIndex", this, 0 );
    public int getActiveTabIndex(){
    	return activeTabIndex.getValue();
    }

    public String getActiveTab()
    {
        return activeTab.getValue();
    }
   
    
    public void initComponent(){
    	super.initComponent();
    	initializeTemplate( "templates/components/tabs.ftl" );
		
    }

    @Override
	public void preRender() {
    	if( getActiveTab() == null ) {
    		List<UIComponent> tabs = getChildren();
    		for( UIComponent tab : tabs ) {
    			if(((Tab)tab).isVisible() ) {
    				setActiveTab( tab.getId() );
    				break;
    			}
    		}
    	}
	}

	@Override
    public StateChanged wasStateChanged2() {
        StateChanged bStateChanged = super.wasStateChanged2();
        if( bStateChanged  == StateChanged.NONE ) {
            Iterator<UIComponent> oChildsIt = this.getChildren().iterator();
            while( bStateChanged == StateChanged.NONE && oChildsIt.hasNext() ) {
                Tab oChildTab = (Tab)oChildsIt.next();
                if (oChildTab.label.wasChanged())
                	bStateChanged = StateChanged.FOR_RENDER;
                if (oChildTab.visible.wasChanged())
                	bStateChanged = StateChanged.FOR_RENDER;
            }
        }
        if (StateChanged.FOR_RENDER == bStateChanged)
        	setDestroyOnClient( true );
        return bStateChanged;
    }

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
            
            XUIResponseWriter w = getResponseWriter();
            
            Tabs oTabs = (Tabs)component;
            Iterator<UIComponent> oChildsIt = component.getChildren().iterator();
            while( oChildsIt.hasNext() ) {
                Tab oChildTab = (Tab)oChildsIt.next();
                if( oChildTab.getId().equals( oTabs.getActiveTab() ) && oChildTab.isVisible() ) {
                	ExtConfig tabConfig = new ExtConfig( "ExtXeo.Tab" );
                	tabConfig.addJSString("id",  oChildTab.getClientId() );
                	tabConfig.add("minHeight", 200);
                	
                	if( oTabs.isRenderedOnClient() ) {
                		oChildTab.forceRenderOnClient();
                	}
                	
                	getResponseWriter().getScriptContext().add(  
                		XUIScriptContext.POSITION_FOOTER,
                		"Tabs_" + oChildTab.getClientId(),
                    	tabConfig.renderExtConfig()
                	);
                	
                	String layout = oTabs.getLayout();
                	if( layout != null && layout.length() > 0 ) {
                		Layouts.registerComponent(w, oChildTab, layout );
                	}
                	w.startElement( HTMLTag.DIV ,oChildTab);
                    w.writeAttribute( HTMLAttr.ID , oChildTab.getClientId(), null);
                    w.writeAttribute( HTMLAttr.CLASS ,"x-panel x-tab-panel-body x-tab-panel-body-top xwc-tab",null);
                    if (StringUtils.hasValue( oTabs.getBodyStyle() ))
                    	w.writeAttribute(HTMLAttr.STYLE, "height:" + oTabs.getHeight() + ";" + oTabs.getBodyStyle(), null);
                    else
                    	w.writeAttribute(HTMLAttr.STYLE, "height:" + oTabs.getHeight(), null);
                    oChildTab.encodeAll();
                    w.endElement(HTMLTag.DIV);
                } else {
                	oChildTab.resetRenderedOnClient( );
                }
            }
        }

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            
            Tabs oTabs = (Tabs)component;
            XUIResponseWriter w = getResponseWriter();
            //<div class="x-tab-panel" id="ext-comp-1001" style="WIDTH: 100%">
            w.startElement("div", component);
            
            //Layouts.registerComponent(w, oTabs, Layouts.LAYOUT_FIT_PARENT );
            
            
            w.writeAttribute( "id", component.getClientId(), null );
            w.writeAttribute( "class", "x-tab-panel", null );
            w.writeAttribute( "style", "width:100%", null );
            
            if( oTabs.getRenderTabBar() ) {
            
	            //<div class="x-tab-panel-header x-unselectable" id="ext-gen7" style="WIDTH: 99%; MozUserSelect: none; KhtmlUserSelect: none" unselectable="on">
	            w.startElement("div", component);
	            w.writeAttribute( "id", component.getClientId() + ":h" , null );
	            w.writeAttribute( "class", "x-tab-panel-header x-unselectable", null );
	            w.writeAttribute( "style", "WIDTH: 100%; MozUserSelect: none; KhtmlUserSelect: none", null );
	            w.writeAttribute( "unselectable", "on", null );
	            //<div class="x-tab-strip-wrap" id="ext-gen11">
	            w.startElement("div", component);
	            w.writeAttribute( "id", component.getClientId() + ":sw" , null );
	            w.writeAttribute( "class", "x-tab-strip-wrap", null );
	            //<ul class="x-tab-strip x-tab-strip-top" id="ext-gen13">
	            w.startElement("ul", component);
	            w.writeAttribute( "id", component.getClientId() + ":st" , null );
	            w.writeAttribute( "class", "x-tab-strip x-tab-strip-top", null );
	
	            boolean bIsActiveTab;
	            Iterator<UIComponent> oChildsIt = component.getChildren().iterator();
	            while( oChildsIt.hasNext() ) {
	                Tab oChildTab = (Tab)oChildsIt.next();
	                
	                if( oChildTab.isRendered() && oChildTab.isVisible() )
	                {
	                    if( oTabs.getActiveTab() == null ) {
	                        oTabs.setActiveTab( oChildTab.getId() );
	                    }
	                    
	                    bIsActiveTab = false;
	                    if( oTabs.getActiveTab().equals(  oChildTab.getId() ) ) {
	                        bIsActiveTab = true;                    
	                    }
	    
	                    //            <li class="" id="ext-comp-1001__ext-comp-1002">
	                    //              <a class="x-tab-right" onclick="return false;" href="http://extjs.com/deploy/dev/examples/tabs/tabs.html#"><em class="x-tab-left">
	                    //                    <span class="x-tab-strip-inner">
	                    //                        <span class="x-tab-strip-text ">Short Text</span>
	                    //                    </span></em>
	                    //              </a>
	                    //            </li>
	                    w.startElement("li", component );
	                    w.writeAttribute("id", oTabs.getId() +":" + oChildTab.getId(), null );
	                    if( bIsActiveTab ) {
	                        w.writeAttribute("class", "x-tab-strip-active", null);
	                    }
	                    w.startElement("a", component );
	                    w.writeAttribute("class", "x-tab-right", null);
	                    w.writeAttribute("tabIndex", "100", null);
	                    
	                    if( bIsActiveTab ) {
	                        w.writeAttribute("href", "javascript:void(0)", null );
	                    }
	                    else {
	                        w.writeAttribute("href", "javascript:" + XVWScripts.getAjaxCommandScript( oChildTab, XVWScripts.WAIT_STATUS_MESSAGE ), null );
	                    }
	    
	                    w.startElement("em", component );
	                    w.writeAttribute("class", "x-tab-left", null);
	    
	                    w.startElement("span", component );
	                    w.writeAttribute("class", "x-tab-strip-inner", null);
	                    
	                    w.startElement("span", component );
	                    w.writeAttribute("id", oTabs.getId() +":" + oChildTab.getId() + ":t", null );
	                    w.writeAttribute("class", "x-tab-strip-text", null);
	
	                    String sLabel = oChildTab.getLabel();
	                    String sIcon = oChildTab.getIcon();
	                    if (sIcon != null && sIcon.length() > 0){
		                    w.startElement(HTMLTag.IMG, component);
		                    	w.writeAttribute(HTMLAttr.SRC, sIcon, null);
		                    	w.writeAttribute(HTMLAttr.WIDTH, "16", null);
		                    	w.writeAttribute(HTMLAttr.HEIGHT, "16", null);
		                    	w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
		                    w.endElement(HTMLTag.IMG);
	                    }
	                    
	                    if( sLabel != null ) {
	                    	w.writeText( sLabel, component, null );
	                    }
	                    
	                    w.endElement("span");
	                    w.endElement("span");
	                    w.endElement("em");
	                    w.endElement("a");
	                    w.endElement("li");
	                }
	                
	            }
	            //<li class="x-tab-edge" id="ext-gen14">&nbsp;</li>
	            /*
	            w.startElement("li", component );
	            w.writeAttribute( "class", "x-tab-edge", null );
	            w.writeText( "&nbsp;", null );
	            w.endElement("li");
	            */
	            
	            //<div class="x-clear" id="ext-gen15"></div>
	            w.startElement("div", component );
	            w.writeAttribute( "class", "x-clear", null );
	            w.endElement("div");
	
	            w.endElement( "ul" );
	            w.endElement( "div" );
	            
	            //<div class="x-tab-strip-spacer" id="ext-gen12"></div>
	            w.startElement( "div", component );
	            w.writeAttribute( "class", "x-tab-strip-spacer", null );
	            w.endElement( "div" );
	            
	            w.endElement("div");
	            
	            //<div class="x-tab-panel-bwrap" id="ext-gen8">
	            //    <div class="x-tab-panel-body x-tab-panel-body-top" id="ext-gen9" style="WIDTH: 99%">
	            
	            //w.startElement( "div", component );
	            //w.writeAttribute( "class", "x-ie-shadow", null );
            }
            w.startElement( "div", component );
            w.writeAttribute( "class", "x-tab-panel-bwrap", null );
            
        } 
        
        @Override
        public boolean getRendersChildren() {
        	return true;
        }

        @Override 
        public void encodeEnd(XUIComponentBase component) throws IOException {
        	
            ResponseWriter w = getResponseWriter();
            w.endElement("div");
            w.endElement("div");
        }
    }

	@Override
	public boolean getHasParentNavigator() {
		boolean value = findParentComponent( Tabs.class ) != null; 
		return value;
	}
	
	@Override
	public int getHierarchyLevel() {
		XUIComponentBase tabs = (XUIComponentBase) findParentComponent( Tabs.class );
		return 1 + getHierarchyLevel( tabs );
	}
	
	private int getHierarchyLevel(XUIComponentBase component){
		if (component == null) return 0;
		XUIComponentBase tabs = component.findParentComponent( Tabs.class );
		return 1 + getHierarchyLevel( tabs );
		
	}
	
	

}
