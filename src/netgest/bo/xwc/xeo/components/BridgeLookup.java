package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import netgest.bo.ejb.boManagerLocal;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boBridgeMasterAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsBaseRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;	
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.xeo.components.utils.BridgeLookupFavoriteSwitcher;
import netgest.bo.xwc.xeo.components.utils.DefaultFavoritesSwitcherAlgorithm;

/**
 * 
 * The BridgeLookup Component allows to select any number of instances
 * (in a collection relation) by using a "Lookup" component (instead of
 * the regular Bridge component) 
 * 
 * @author PedroRio
 *
 */
public class BridgeLookup extends AttributeBase {
	
	private XUICommand oLookupCommand;
    private XUICommand oOpenCommand;
    private XUICommand oRemoveElementCommand;
    private XUICommand oCleanCommand;
    
    private XUICommand oFavoriteCommand;
    
    /**
     * Name of the key to store the favorites as a preference
     */
    public final static String PREFERENCE_NAME = "favorites";
    
    /**
     * Prefix for the preference key
     */
    public final static String PREFERENCE_PREFIX = "bridgeLookup.";
    
    /**
     * The preference separator
     */
    public final static String PREFERENCE_SEPARATOR = ".";
    
    
    private Menu oOpenEditViewer;
    
    /**
     * The height of the component
     */
    private XUIViewStateBindProperty<String> height = 
    	new XUIViewStateBindProperty<String>( "height", this, "22", String.class );
    
    /**
     * The number of favorites 
     */
    private XUIViewBindProperty<Integer> numFavorites = 
    	new XUIViewBindProperty<Integer>( "numFavorites", this, 10, Integer.class );
    
    
    /**
     * 
     */
    private XUIBindProperty<BridgeLookupFavoriteSwitcher> algorithm = 
    	new XUIBindProperty<BridgeLookupFavoriteSwitcher>( "algorithm", this, BridgeLookupFavoriteSwitcher.class );
    
    /**
     * Set the height of the component, only works with multi-line components
     *  like textArea and HtmlEditor
     *  
     * @param sWidth Integer or a {@link ValueExpression} 
     */
    public void setHeight( String sHeight ) {
        this.height.setExpressionText( sHeight );
    }
    
    /**
     * Get the current Height of the component
     * @return String with a integer value with the Height of the Component
     */
    public String getHeight() {
        return this.height.getEvaluatedValue();
    }
    
    public void setNumFavorites(String numFavExpr){
    	this.numFavorites.setExpressionText(numFavExpr);
    }
    
    public void setNumFavorites(Integer numFavs){
    	this.numFavorites.setValue(numFavs);
    }
    
    public Integer getNumFavorites(){
    	return this.numFavorites.getEvaluatedValue();
    }
    
    public void setAlgorithm(String algorithmExpr){
    	this.algorithm.setExpressionText(algorithmExpr);
    }
    
    public BridgeLookupFavoriteSwitcher getAlgorithm(){
    	BridgeLookupFavoriteSwitcher currImpl = this.algorithm.getEvaluatedValue();
    	if (currImpl != null)
    		return currImpl;
    	else
    		return new DefaultFavoritesSwitcherAlgorithm();
    }
    
    @Override
    public boolean isVisible() {
    	// TODO Auto-generated method stub
    	return true;
    }
	
    @Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}
	
    @Override
	public void initComponent() {
    	
    	// per component initializations.
        oLookupCommand = new XUICommand();
        oLookupCommand.setId( getId() + "_lk" );
        oLookupCommand.addActionListener( 
                new LookupActionListener()
            );
        getChildren().add( oLookupCommand );
        
        oOpenCommand = new XUICommand();
        oOpenCommand.setId( getId() + "_op" );
        oOpenCommand.setActionExpression( createMethodBinding( "#{viewBean.openLookupObject}" ) );
        getChildren().add( oOpenCommand );
        
        oOpenEditViewer = new Menu();
        oOpenEditViewer.setId( getId() + "_ed" );
        oOpenEditViewer.setTarget( "tab" );
        oOpenEditViewer.setActionExpression( createMethodBinding( "#{viewBean.editBridgeLookup}" ) );
        
        getChildren().add( oOpenEditViewer );
        
        oRemoveElementCommand = new XUICommand();
        oRemoveElementCommand.setId( getId() + "_rmBridge" );
        oRemoveElementCommand.setActionExpression( createMethodBinding( "#{viewBean.removeBridgeLookup}" ) );
        getChildren().add( oRemoveElementCommand );

        oCleanCommand = new XUICommand();
        oCleanCommand.setId( getId() + "_cleanBridge" );
        oCleanCommand.setActionExpression( createMethodBinding( "#{viewBean.cleanBridgeLookup}" ) );
        getChildren().add( oCleanCommand );
        
        oFavoriteCommand = new XUICommand();
        oFavoriteCommand.setId( getId() + "_showFav" );
        oFavoriteCommand.setActionExpression( createMethodBinding( "#{viewBean.showFavorite}" ) );
        getChildren().add( oFavoriteCommand );
        
        
    }
    @Override
    public String getDisplayValue(){
    	return super.getDisplayValue();
    }
    
    /**
     * 
     * Generates the HTML List
     * 
     * @return
     */
    public String getDislayList(){
    	try {
			StringBuilder b = new StringBuilder();
			boBridgeMasterAttribute bridge = (boBridgeMasterAttribute)((XEOObjectAttributeConnector)getDataFieldConnector()).getAttributeHandler();
			long[] bouis = bridge.getValuesLong();
			boManagerLocal objManager = boApplication.getDefaultApplication().getObjectManager();
			String formId = findParentComponent(XUIForm.class).getId();
			boolean orphan = bridge.getDefAttribute().getChildIsOrphan();
			for (long boui: bouis){
				if (boui > 0){ //getValuesLong returns an array with boui = 0 when no elements are in the list 
					boObject curr = objManager.loadObject(bridge.getEboContext(), boui);
					String openTabCmd = "XVW.openBridgeLookup(\""+formId+"\",\""+getId()+"\","+curr.getBoui()+","+!orphan+");";
					String removeCmd = "XVW.removeBridgeLookup(\""+formId+"\",\""+getId()+"\","+curr.getBoui()+");";
					if (getEnableCardIdLink())
						b.append("<span href=\"javascript:void(0)\" onClick="+openTabCmd+">");
					b.append(curr.getTextCARDID());
					if (getEnableCardIdLink())
						b.append("</span>");
					
					if (isUsable()){
						b.append("<span href=\"javascript:void(0)\" onClick="+removeCmd+">");
						b.append("<img src=\"ext-xeo/icons/remove-bridge.png\" width=\"16\" style=\"cursor:pointer;vertical-align:middle;_margin-left:3px;_margin-top:1px;_margin-bottom:1px;\" height=\"16\" />");
						b.append("</span>");
					}
					b.append("; ");
				}
			}
			return b.toString();
		} catch (boRuntimeException e) {
			e.printStackTrace();
			
		}
		return "";
    }
    
    /**
     * 
     * Returns whether or not the component is enabled
     * 
     * @return True if the component is visible and enabled, false otherwise (disabled, readOnly, not write permissions)
     */
    public boolean isUsable(){
    	boolean disabled = isDisabled();
		boolean read = isReadOnly();
		boolean writePermissions = getEffectivePermission(SecurityPermissions.WRITE);
		boolean visible = isVisible();
		if (disabled || read || !writePermissions || !visible){
			return false;
		}
		return true;
    }
    
    @Override
    public void preRender() {
        oLookupCommand = (XUICommand)getChild( 0 );
        oOpenCommand = (XUICommand)getChild( 1 );
        oOpenEditViewer = (Menu)getChild( 2 );
        oRemoveElementCommand = (XUICommand)getChild( 3 );
        oCleanCommand = (XUICommand)getChild( 4 );
        oFavoriteCommand = (XUICommand)getChild( 5 );
        getRequestContext().getScriptContext().addInclude(XUIScriptContext.POSITION_HEADER, "bridgeLook", "ext-xeo/js/bridgeLookup.js");
    }
    
    public AttributeHandler getAttributeHandler(){
    	return ((XEOObjectAttributeConnector) getDataFieldConnector()).getAttributeHandler();
    }

    private void doLookup() {
        try {
            XEOEditBean oXEOBaseBean;
            oXEOBaseBean = (XEOEditBean)getRequestContext().getViewRoot().getBean("viewBean");
            oXEOBaseBean.lookupAttribute( this.getClientId() );
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setLookupCommand(XUICommand oLookupCommand) {
        this.oLookupCommand = oLookupCommand;
    }

    protected XUICommand getLookupCommand() {
        return oLookupCommand;
    }
    
    protected XUICommand getCleanCommand() {
        return oCleanCommand;
    }
    
    protected XUICommand getFavoriteCommand() {
        return oFavoriteCommand;
    }

    @Override
    public Object saveState() {
    	return super.saveState();
    }
    @Override
    public void setRenderedOnClient(boolean renderedOnClient) {
    	super.setRenderedOnClient(renderedOnClient);
    }
    
    public static class LookupActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            ((BridgeLookup)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    public static class XEOHTMLRenderer extends ExtJsBaseRenderer {

    	
		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.BridgeLookup";
		}
		
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oComp )
				throws IOException {

            BridgeLookup  oAttr;
            XUIResponseWriter w = getResponseWriter();
            oAttr = (BridgeLookup)oComp; 
            String tableStyle = "width:100%;table-layout:fixed;" + (oAttr.isUsable()?"":"display:none;");
            
            w.startElement( HTMLTag.TABLE, oComp);
        	w.writeAttribute( HTMLAttr.CELLPADDING, "0", null );
        	w.writeAttribute( HTMLAttr.CELLSPACING, "0", null );
        	w.writeAttribute( HTMLAttr.STYLE, tableStyle, null );
        	w.writeAttribute( HTMLAttr.ID, oAttr.getClientId()+"_tblBtn", null );
            	
            w.startElement( HTMLTag.COLGROUP, oComp);
            
            w.startElement( HTMLTag.COL, oComp);
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;text-align:justify;", null );
            w.writeAttribute( HTMLAttr.CLASS, "x-form-text", null );
            w.endElement(HTMLTag.COL);
            
            w.startElement( HTMLTag.COL, oComp);
        	w.writeAttribute( HTMLAttr.ID, oAttr.getClientId()+"_colBtns", null );
    		w.writeAttribute( HTMLAttr.STYLE, 
    				"vertical-align:top;width:54px;1px solid #B5B8C8;"
    	            + (oAttr.isUsable()?"":"display:none;")
    				,null 
    		);
            w.endElement(HTMLTag.COL);
            
            w.endElement( HTMLTag.COLGROUP );
            	
            w.startElement( HTMLTag.TR, oComp);
            w.startElement( HTMLTag.TD, oComp);
    		w.writeAttribute( HTMLAttr.STYLE, "border:1px solid #9AB;vertical-align:top;padding-left: 4px;", null );
            
            super.encodeBeginPlaceHolder( oComp );
            
            // Add the a style to the place holder DIV
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;display:inline;", null );
        	
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden", null);
            w.writeAttribute(VALUE, oAttr.getValue() , null);

            w.writeAttribute(NAME, oComp.getClientId() + "_toEdit", null);
            w.writeAttribute(ID, oComp.getClientId() + "_toEdit", null);
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden", null);
            w.writeAttribute(VALUE, oAttr.getValue() , null);

            w.writeAttribute(NAME, oComp.getClientId() + "_toRemove", null);
            w.writeAttribute(ID, oComp.getClientId() + "_toRemove", null);
            
            w.endElement(INPUT);
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden", null);
            w.writeAttribute(VALUE, "" , null);

            w.writeAttribute(NAME, oComp.getClientId() + "_top", null);
            w.writeAttribute(ID, oComp.getClientId() + "_top", null);
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden", null);
            w.writeAttribute(VALUE, "" , null);

            w.writeAttribute(NAME, oComp.getClientId() + "_left", null);
            w.writeAttribute(ID, oComp.getClientId() + "_left", null);
            
            w.endElement(INPUT);
            
            
            
		}
		
		@Override
		public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
			
            BridgeLookup oBridgeLookp = (BridgeLookup)oComp;

            ExtConfig config = super.getExtJsConfig(oComp);
            
			config.addString( "html" , JavaScriptUtils.writeValue( oBridgeLookp.getDislayList() ) );
			
//            if( !oBridgeLookp.isVisible() )
//            	config.add("hidden",true);
            
            config.addJSString("height", oBridgeLookp.getHeight() );
            
//            if (oBridgeLookp.isVisible())
            	config.addString("cls", "xwc-bridge-lookup" );
            
            //Listeners, for the resize (handles resize to smaller width)
            //by some strange effect resize to bigger width works, but to small doesn't
            StringBuilder b = new StringBuilder(70);
            b.append("function(component){");
            b.append(" component.getEl().setStyle('width','100%'); }");
            ExtConfig listeners 	= new ExtConfig();
            listeners.add( "'resize'" , b.toString());
            
            config.add( "listeners" , listeners );
        	
            	
			return config;
		}
		
		
		
		@Override
		public ScriptBuilder getEndComponentScript(XUIComponentBase oComp) {
			
			ScriptBuilder s = null;
			
			if( oComp.isRenderedOnClient() ) {
				BridgeLookup oBridgeLkup = (BridgeLookup)oComp;
	
				s = new ScriptBuilder();
				s.startBlock();
				super.writeExtContextVar(s, oComp);
			
				s.w( "c.setText('" ).writeValue( oBridgeLkup.getDislayList() ).l("',false);");

//				if( oComp.getStateProperty("visible").wasChanged() )
//					s.w( "c.setVisible(" ).writeValue( oBridgeLkup.isVisible() ).l(");");
					
				s.endBlock();
			}
			return s;
			
		}

		

        @Override
        public void decode(XUIComponentBase component) {

            BridgeLookup oAttrComp;
            
            oAttrComp = (BridgeLookup)component;
            
            String clear = getRequestContext().getRequestParameterMap().get( oAttrComp.getClientId()+"_clear" );
            if( "true".equals( clear ) ) {
                oAttrComp.setSubmittedValue( "" );
            } else {
                String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() + "_ci" );
                if( "NaN".equals( value ) ) {
                    oAttrComp.setSubmittedValue( "" );
                }
                else {
                	oAttrComp.setSubmittedValue( value );
                }
            }
            super.decode(component);
        }
        
        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
        	
        	XUIResponseWriter w = getResponseWriter();
            BridgeLookup oAttr = (BridgeLookup)oComp;
        	
            super.encodeEnd(oComp);
        	 
        	if (!oAttr.isRenderedOnClient()){
        		w.endElement(HTMLTag.TD);
        		w.startElement( HTMLTag.TD, oComp);
            	w.writeAttribute( HTMLAttr.ID, oAttr.getClientId()+"_tdBtns", null );
        		w.writeAttribute( 
        			HTMLAttr.STYLE, "vertical-align:top;"
        	        + (oAttr.isUsable()?"":"display:none;")
        			, null 
        		);
        		
            	w.startElement( HTMLTag.TABLE, oComp);
            	w.writeAttribute( HTMLAttr.CELLPADDING, "0", null );
            	w.writeAttribute( HTMLAttr.CELLSPACING, "2", null );
        		w.startElement( HTMLTag.TR, oComp);
        		w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( HTMLAttr.STYLE, "vertical-align:top;", null );
        		
        		w.writeAttribute( HTMLAttr.STYLE, "vertical-align:top;", null );
        		        		
        		//Div for the add button
            	w.startElement( DIV, oComp);
            	
            		w.writeAttribute(ID, oComp.getClientId() + "_addButton", null);
            		w.writeAttribute(HTMLAttr.STYLE, "display:inline", null);
            		
            		
            		w.startElement(HTMLTag.A, null);
            			
		    			w.writeAttribute(HTMLAttr.SRC, "javascript:void(0)", null);
		    			w.writeAttribute(HTMLAttr.ONCLICK, XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_DIALOG ), null);
		    			w.writeAttribute(HTMLAttr.CLASS, "xwc-search-trigger", null);
	            		
	    			w.endElement(HTMLTag.A);
	    			
        	    w.endElement(DIV);
    			
    			w.endElement(HTMLTag.TD);
    			w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( HTMLAttr.STYLE, "vertical-align:top;", null );
        		
        		//Div for the Remove
            	w.startElement( DIV, oComp);
            		w.writeAttribute(ID, oComp.getClientId() + "_rmButton", null);
            		w.writeAttribute(HTMLAttr.STYLE, "display:inline", null);
            		
	            		w.startElement(HTMLTag.A, null);
			    			w.writeAttribute(HTMLAttr.SRC, "javascript:void(0)", null);
			    			w.writeAttribute(HTMLAttr.ONCLICK, XVWScripts.getAjaxCommandScript( oAttr.getCleanCommand(),XVWScripts.WAIT_DIALOG ), null);
			    			w.writeAttribute(HTMLAttr.CLASS, "xwc-clean-trigger", null);
		    			w.endElement(HTMLTag.A);
	    			
    			w.endElement(DIV);
    			
    			w.endElement(HTMLTag.TD);
    			
    			
    			w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( HTMLAttr.STYLE, "vertical-align:top;", null );
        		
            	w.startElement( DIV, oComp);
            		w.writeAttribute(ID, oComp.getClientId() + "_favButton", null);
            		w.writeAttribute(HTMLAttr.STYLE, "display:inline", null);
            		
            		if ( oAttr.getShowFavorites()){
            			StringBuilder b = new StringBuilder(300);
            			b.append("var fav = Ext.get('").append(oComp.getClientId()).append("_fav');");
            			b.append("Ext.get('").append(oComp.getClientId())
            				.append("_left').dom.value=").append("fav.getX();");
            			b.append("Ext.get('").append(oComp.getClientId())
        				.append("_top').dom.value=").append("fav.getY();");
            			b.append(XVWScripts.getAjaxCommandScript( oAttr.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
            			
	            		w.startElement(HTMLTag.A, null);
	            			w.writeAttribute(HTMLAttr.ID, oAttr.getClientId()+"_fav", null);	
			    			w.writeAttribute(HTMLAttr.SRC, "javascript:void(0)", null);
			    			w.writeAttribute(HTMLAttr.ONCLICK, b.toString() , null);
			    			w.writeAttribute(HTMLAttr.CLASS, "xwc-favorite-trigger", null);
		    			w.endElement(HTMLTag.A);
            		}
	    			
    			w.endElement(DIV);
    			
    			w.endElement(HTMLTag.TD);
    			w.endElement(HTMLTag.TR);
    			w.endElement(HTMLTag.TABLE);
    			
    			w.endElement(HTMLTag.TD);
    			
    			//END teste favorites
    			
    			w.endElement(HTMLTag.TR);
    			w.endElement(HTMLTag.TABLE);
    			
    			StringBuilder b = new StringBuilder(300);
    			b.append("var fav = Ext.get('").append(oComp.getClientId()).append("_fav');");
    			b.append("Ext.get('").append(oComp.getClientId())
    				.append("_left').dom.value=").append("fav.getX();");
    			b.append("Ext.get('").append(oComp.getClientId())
				.append("_top').dom.value=").append("fav.getY();");
    			b.append(XVWScripts.getAjaxCommandScript( oAttr.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
    			
    			
    			//String result = "Ext.onReady(function() { Ext.get('"+oAttr.getClientId()+"_fav').on('mouseover',function(){"+b.toString()+";},this,{delay:200}) });";
    			
    			//String result2 = "Ext.onReady(function() { Ext.getCmp('ext-"+oAttr.getClientId()+"').hover('"+oAttr.getClientId()+"')});";
    			
    			//getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "bridgeLookupWindowEvent" + oComp.getId(), 
    			//		result);
    			
    			/*getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "bridgeLookupWindowEvent2" + oComp.getId(), 
    					result2);*/
			
            } else{ //Deal with Visible/ReadOnly and Disabled
            	
            	StringBuilder b = new StringBuilder(200);
            	if (oAttr.isUsable()){ //Everything OK, show the buttons
        			b.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('');");
            		b.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('');");
            		b.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('');");
            	} else {
            		if( !oAttr.isVisible() ) {
            			b.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('none');");
            		} else {
            			b.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('');");
            			if (oAttr.isDisabled() || oAttr.isReadOnly() || oAttr.getEffectivePermission( SecurityPermissions.WRITE ) ) {
                    		b.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('none');");
                    		b.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('none');");
                		}
            			else {
                    		b.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('');");
                    		b.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('');");
            			}
            		}
            		
            	}
            	getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "hideTbl" + oComp.getId(), 
            			b.toString());
            }
        	
        }
        
        
}
    
    
    
    
}
