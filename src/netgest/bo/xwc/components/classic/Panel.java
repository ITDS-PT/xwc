package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import java.io.IOException;

/**
 * 
 * The {@link Panel} component renders an HTML panel to allow 
 * 
 * @author jcarreira
 *
 */
public class Panel extends ViewerSecurityBase {
    
    /**
     * The height of the panel
     */
    private XUIViewStateProperty<String> height = new XUIViewStateProperty<String>( "height", this );
    /**
     * The width of the panel
     */
    private XUIViewStateProperty<String> width = new XUIViewStateProperty<String>( "width", this );
	/**
	 * The title of the panel
	 */
    @Localize
	private XUIViewStateBindProperty<String> title = new XUIViewStateBindProperty<String>( "title", this, String.class );
	/**
	 * The layout of the panel (not in used at the moment)
	 */
	private XUIViewProperty<String> layout = new XUIViewProperty<String>( "layout", this, "" );
	
	/**
	 * Whether the panel is collapsible or nor (defaults to false, not all renderers support this)
	 */
	private XUIBaseProperty<Boolean> collapsible = new XUIBaseProperty<Boolean>( "collapsible", this, false );
	
	/**
	 * Whether the panel is visible or not
	 */
	private XUIViewStateBindProperty<Boolean> visible = new XUIViewStateBindProperty<Boolean>( "visible", this, "true", Boolean.class );
	
	/**
     * An icon for the panel
     */
    private XUIViewStateProperty<String> icon = new XUIViewStateProperty<String>( "icon", this );
	
    private XUIViewProperty<String> css = new XUIViewProperty<String>("css", this, "");
    
    public void setCss(String css){
		this.css.setValue(css);
	}
	
	public String getCss(){
		return css.getValue();
	}
	
	private XUIViewProperty<String> headerCss = new XUIViewProperty<String>("headerCss", this, "");
    
    public void setHeaderCss(String css){
		this.headerCss.setValue(css);
	}
	
	public String getHeaderCss(){
		return headerCss.getValue();
	}
	
	private XUIViewProperty<String> contentCss = new XUIViewProperty<String>("contentCss", this, "");
    
    public void setContentCss(String css){
		this.contentCss.setValue(css);
	}
	
	public String getContentCss(){
		return contentCss.getValue();
	}
    
    
	/**
	 * Returns the title of the panel 
	 * @return Title
	 */
    public String getTitle() {
		return title.getEvaluatedValue();
	}
    
    @Override
    public void initComponent(){
    	super.initComponent();
    	initializeTemplate( "templates/components/panel.ftl" );
    }
    
    /**
     * Set the title of the panel
     * @param title String with the title
     */
	public void setTitle(String title) {
		this.title.setExpressionText( title );
	}
	
	public void setHeight(String height) {
        this.height.setValue( height );
    }
	
    public String getHeight() {
        return height.getValue();
    }
    
    public String getWidth() {
		return width.getValue();
	}

	public void setWidth(String width) {
		this.width.setValue( width );
	}
	
	public void setVisible( String visibleExpr ) {
		this.visible.setExpressionText( visibleExpr );
	}
	
	public boolean getVisible() {
		return this.visible.getEvaluatedValue();
	}
	
	public boolean getCollapsible(){
		return this.collapsible.getValue();
	}
	
	public void setCollapsible(String colapsible){
		this.collapsible.setValue( Boolean.valueOf( colapsible ) );
	}
	
	public void setCollapsible(boolean colapsible){
		this.collapsible.setValue( colapsible );
	}
	
	public void setCollapsible(Boolean colapsible){
		this.collapsible.setValue( colapsible );
	}
	
	/**
	 * Set the layout type to this panel
	 * @return the current layout model
	 */
	public String getLayout() {
		return layout.getValue();
	}
	
	/**
	 * return the current layout type of this Panel
	 * @param layoutType String with one of the next layouts ( fit-parent, fit-window, form )
	 */
	public void setLayout(String layoutType) {
		this.layout.setValue( layoutType );
	}
	
	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}
	
	
	public String getIcon(){
		return icon.getValue();
	}
	
	public void setIcon(String value){
		this.icon.setValue(value);	
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w;
            Panel oPanel;
            String sHeight;
            String sWidth;
            
            w = getResponseWriter();
            
            oPanel = (Panel)component;
            
        	sHeight = oPanel.getHeight();
        	sWidth = oPanel.getWidth();
        	
        	w.startElement( DIV , component);
        	w.writeAttribute( ID, component.getClientId(), null );
        	
        	if( !oPanel.getVisible() )
        		w.writeAttribute( HTMLAttr.CLASS, "x-hidden", null );
        	
        	/*            
        <DIV class="x-panel-tl">
        	<DIV class="x-panel-tr">
        		<DIV class="x-panel-tc">
        			<DIV class="x-panel-header x-unselectable" id="ext-gen18" style="MozUserSelect: none; KhtmlUserSelect: none" unselectable="on">
        				<SPAN class="x-panel-header-text" id="ext-gen40">Simple Form</SPAN>
        			</DIV>
        		</DIV>
        	</DIV>
        </DIV>
        	 */
        	if( oPanel.getTitle() != null ) {
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel x-panel-tl", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-tr", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-tc", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-header", null);
        		
        		w.startElement( HTMLTag.SPAN, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-header-text", null);
        		String sIcon = oPanel.getIcon();
        		
        		if (sIcon != null && sIcon.length() > 0 ){
	        		w.startElement(HTMLTag.IMG, component);
		            	w.writeAttribute(HTMLAttr.SRC, sIcon, null);
		            	w.writeAttribute(HTMLAttr.WIDTH, "16", null);
		            	w.writeAttribute(HTMLAttr.HEIGHT, "16", null);
		            	w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
	            	w.endElement(HTMLTag.IMG);
        		}
        		
        		w.writeText( oPanel.getTitle(), null );
        		
        		
        		
        		w.endElement( HTMLTag.SPAN );
        		
        		w.endElement(DIV);
        		w.endElement(DIV);
        		w.endElement(DIV);
        		w.endElement(DIV);
        	}
        	
        	
        	w.startElement( DIV , component);
//            w.writeAttribute( CLASS, "x-panel-ml", null );
        	
        	w.startElement( DIV, component);
//            w.writeAttribute( CLASS, "x-panel-mr", null );
        	if( sHeight != null || sWidth != null ) {
        		w.writeAttribute(HTMLAttr.STYLE, "height:100%", null );
        	}
        	
        	w.startElement(DIV, component);
//            w.writeAttribute( CLASS, "x-panel-mc", null );
        	
        	w.startElement(DIV, component);
//            w.writeAttribute(CLASS, "x-panel-body", null );

        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w;
            w = getResponseWriter();
            
//            w.endElement(DIV);
//            
//            ExtConfig oPanelConfig = new ExtConfig("Ext.Panel");
//            oPanelConfig.add("frame", true );
//            
//            oPanelConfig.addJSString("baseCls", "x-form");
//            oPanelConfig.addJSString("layout","border");            
//            oPanelConfig.addJSString("contentEL", component.getClientId() );
//            
//            
//            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , component.getClientId(), oPanelConfig.renderExtConfig() );
            
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
            
        }


        @Override
        public boolean getRendersChildren() {
            return true;
        }
        
    }

    //
    // Methods from SecurableComponent
    //

	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.AREA;
	}

	public String getViewerSecurityId() {
		return getTitle();
	}

	public String getViewerSecurityLabel() {
		String label = getViewerSecurityComponentType().toString();
		if ( getViewerSecurityId()!=null ) {
			label += " "+ getViewerSecurityId();
		}
		return label; 
	}

	public boolean isContainer() {
		return true;
	}

}
