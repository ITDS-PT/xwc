package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.ONCLICK;
import static netgest.bo.xwc.components.HTMLAttr.SRC;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.COLGROUP;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;
import static netgest.bo.xwc.components.HTMLTag.TABLE;

import java.io.IOException;
import java.util.List;

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
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.xeo.components.lookup.LookupComponent;
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
public class BridgeLookup extends AttributeBase implements LookupComponent {
	
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
    	new XUIViewStateBindProperty<String>( "height", this, "19", String.class );
    
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
     * Lookup Query to use
     */
    private XUIViewBindProperty<String>  lookupQuery =
    	new XUIViewBindProperty<String>( "lookupQuery", this, String.class );
    
    public String getLookupQuery(){
    	return lookupQuery.getEvaluatedValue(); 
    }
    
    public void setLookupQuery(String queryExpr){
    	this.lookupQuery.setExpressionText( queryExpr );
    }
    
    
    
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
        oOpenCommand.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".openLookupObject}" ) );
        getChildren().add( oOpenCommand );
        
        oOpenEditViewer = new Menu();
        oOpenEditViewer.setId( getId() + "_ed" );
        oOpenEditViewer.setTarget( "tab" );
        oOpenEditViewer.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".editBridgeLookup}" ) );
        
        getChildren().add( oOpenEditViewer );
        
        oRemoveElementCommand = new XUICommand();
        oRemoveElementCommand.setId( getId() + "_rmBridge" );
        oRemoveElementCommand.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".removeBridgeLookup}" ) );
        getChildren().add( oRemoveElementCommand );

        oCleanCommand = new XUICommand();
        oCleanCommand.setId( getId() + "_cleanBridge" );
        oCleanCommand.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".cleanBridgeLookup}" ) );
        getChildren().add( oCleanCommand );
        
        oFavoriteCommand = new XUICommand();
        oFavoriteCommand.setId( getId() + "_showFav" );
        oFavoriteCommand.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".showFavorite}" ) );
        getChildren().add( oFavoriteCommand );
        
        
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
			String formId = findParentComponent(XUIForm.class).getClientId();
			boolean orphan = bridge.getDefAttribute().getChildIsOrphan();
			for (long boui: bouis){
				if (boui > 0){ //getValuesLong returns an array with boui = 0 when no elements are in the list 
					boObject curr = objManager.loadObject(bridge.getEboContext(), boui);
					String openTabCmd = "XVW.openBridgeLookup(\""+formId+"\",\""+getId()+"\","+curr.getBoui()+","+!orphan+");";
					String removeCmd = "XVW.removeBridgeLookup(\""+formId+"\",\""+getId()+"\","+curr.getBoui()+");";
					if (getEnableCardIdLink())
						b.append("<span class=\"xwc-bridge-Link\" href=\"javascript:void(0)\" onClick="+openTabCmd+">");
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
            oXEOBaseBean = (XEOEditBean)getRequestContext().getViewRoot().getBean( getBeanId() );
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
		public void encodeComponentChanges(XUIComponentBase oComp,
				List<XUIBaseProperty<?>> propertiesWithChangedState)
				throws IOException {

			StringBuilder b = encodeChanges((BridgeLookup)oComp);
			addScriptFooter(oComp.getClientId() + "_update", b.toString());
			
		}
		
		
		private StringBuilder encodeChanges(BridgeLookup oAttr){
		
			StringBuilder b2 = new StringBuilder(200);
			if (oAttr.isUsable()){ //Everything OK, show the buttons
				b2.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('');");
				b2.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('');");
				b2.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('');");
			} else {
				if( !oAttr.isVisible() ) {
					b2.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('none');");
				} else {
					b2.append("Ext.get('").append(oAttr.getClientId()).append("_tblBtn').setDisplayed('');");
					if (oAttr.isDisabled() || oAttr.isReadOnly() || !oAttr.getEffectivePermission( SecurityPermissions.WRITE ) ) {
						b2.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('none');");
						b2.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('none');");
					}
					else {
						b2.append("Ext.get('").append(oAttr.getClientId()).append("_colBtns').setDisplayed('');");
						b2.append("Ext.get('").append(oAttr.getClientId()).append("_tdBtns').setDisplayed('');");
					}
				}
				
			}return b2;
			
			
		}
		
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oComp )
				throws IOException {

            BridgeLookup  oAttr;
            XUIResponseWriter w = getResponseWriter();
            oAttr = (BridgeLookup)oComp; 
            String tableStyle = "width:100%;table-layout:fixed;" + (oAttr.isUsable()?"":"display:none;");
            
            w.startElement( HTMLTag.TABLE, oComp);
        	w.writeAttribute( HTMLAttr.CELLPADDING, "0" );
        	w.writeAttribute( HTMLAttr.CELLSPACING, "0" );
        	w.writeAttribute( HTMLAttr.STYLE, tableStyle );
        	w.writeAttribute( HTMLAttr.ID, oAttr.getClientId()+"_tblBtn" );
            	
            w.startElement( HTMLTag.COLGROUP, oComp);
            
            w.startElement( HTMLTag.COL, oComp);
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;text-align:justify;" );
            w.writeAttribute( HTMLAttr.CLASS, "x-form-text" );
            w.endElement(HTMLTag.COL);
            
            w.startElement( HTMLTag.COL, oComp);
        	w.writeAttribute( HTMLAttr.ID, oAttr.getClientId()+"_colBtns" );
    		w.writeAttribute( HTMLAttr.STYLE, 
    				"vertical-align:top;width:54px;1px solid #B5B8C8;"
    	            + (oAttr.isUsable()?"":"display:none;")
    				,null 
    		);
            w.endElement(HTMLTag.COL);
            
            w.endElement( HTMLTag.COLGROUP );
            	
            w.startElement( HTMLTag.TR, oComp);
            w.startElement( HTMLTag.TD, oComp);
    		w.writeAttribute( HTMLAttr.STYLE, "border:1px solid #9AB;vertical-align:top;padding-left: 4px;" );
            
            super.encodeBeginPlaceHolder( oComp );
            
            // Add the a style to the place holder DIV
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;display:inline;" );
        	
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden");
            //w.writeAttribute(VALUE, oAttr.getValue() );

            w.writeAttribute(NAME, oComp.getClientId() + "_toEdit");
            w.writeAttribute(ID, oComp.getClientId() + "_toEdit");
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden");
            //w.writeAttribute(VALUE, oAttr.getValue() );

            w.writeAttribute(NAME, oComp.getClientId() + "_toRemove");
            w.writeAttribute(ID, oComp.getClientId() + "_toRemove");
            
            w.endElement(INPUT);
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden");
            w.writeAttribute(VALUE, "" );

            w.writeAttribute(NAME, oComp.getClientId() + "_top");
            w.writeAttribute(ID, oComp.getClientId() + "_top");
            
            w.endElement(INPUT);
            
            w.startElement( INPUT, oComp);
            w.writeAttribute(TYPE, "hidden");
            w.writeAttribute(VALUE, "" );

            w.writeAttribute(NAME, oComp.getClientId() + "_left");
            w.writeAttribute(ID, oComp.getClientId() + "_left");
            
            w.endElement(INPUT);
            
            
            
		}
		
		@Override
		public StateChanged wasStateChanged(XUIComponentBase component,
				List<XUIBaseProperty<?>> changedProperties) {
			
			if (component.getStateProperty("visible").wasChanged())
				return StateChanged.FOR_UPDATE;
			else{
				return StateChanged.FOR_RENDER;
			}
			
		}
		
		@Override
		public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
			
            BridgeLookup oBridgeLookp = (BridgeLookup)oComp;

            ExtConfig config = super.getExtJsConfig(oComp);
            config.addJSString( "renderTo" , oComp.getClientId() + "_container" );
			config.addString( "html" , JavaScriptUtils.writeValue( oBridgeLookp.getDislayList() ) );
			
            if( !oBridgeLookp.isVisible() )
            	config.add("hidden",true);
            
            config.addJSString("height", oBridgeLookp.getHeight() );
            
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

				if( oComp.getStateProperty("visible").wasChanged() )
					s.w( "c.setVisible(" ).writeValue( oBridgeLkup.isVisible() ).l(");");
					
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
            BridgeLookup bridgeLookup = (BridgeLookup)oComp;
        	
            // Write the field
    		oComp.setDestroyOnClient( true );
        	// Create a place holder for the field!

    		w.startElement(DIV);
    		w.writeAttribute(ID, oComp.getClientId());
    		 w.writeAttribute( STYLE, "width:100%;display:inline;" );
    		
        	if( oComp instanceof SecurableComponent ) {
        		if (!((SecurableComponent)oComp).getEffectivePermission(SecurityPermissions.READ) ) {
        			return;
        		}
        	}
        	
        	//Start Table
        	String tableStyle = "width:100%;table-layout:fixed;" + (bridgeLookup.isUsable()?"":"display:none;");
            
            w.startElement( TABLE, oComp);
        	w.writeAttribute( CELLPADDING, "0" );
        	w.writeAttribute( CELLSPACING, "0" );
        	w.writeAttribute( STYLE, tableStyle );
        	w.writeAttribute( ID, bridgeLookup.getClientId()+"_tblBtn" );
            	
            w.startElement( COLGROUP, oComp);
            
            w.startElement( COL, oComp);
            w.writeAttribute( STYLE, "width:100%;text-align:justify;" );
            w.writeAttribute( CLASS, "x-form-text" );
            w.endElement(HTMLTag.COL);
            
            w.startElement( HTMLTag.COL, oComp);
        	w.writeAttribute( ID, bridgeLookup.getClientId()+"_colBtns" );
    		w.writeAttribute( STYLE, 
    				"vertical-align:top;width:54px;1px solid #B5B8C8;"
    	            + (bridgeLookup.isUsable()?"":"display:none;")
    				,null 
    		);
            w.endElement(HTMLTag.COL);
            
            w.endElement( HTMLTag.COLGROUP );
            	
            w.startElement( HTMLTag.TR, oComp);
            w.startElement( HTMLTag.TD, oComp);
            w.writeAttribute( ID, oComp.getClientId() + "_container");
    		w.writeAttribute( STYLE, "border:1px solid #9AB;vertical-align:top;padding-left: 4px;" );
    		
    		
    		
    		
        	//End start table
        	 
        		w.endElement(HTMLTag.TD);
        		w.startElement( HTMLTag.TD, oComp);
            	w.writeAttribute( ID, bridgeLookup.getClientId()+"_tdBtns" );
        		w.writeAttribute( 
        			STYLE, "vertical-align:top;"
        	        + (bridgeLookup.isUsable()?"":"display:none;")
        			 
        		);
        		
            	w.startElement( HTMLTag.TABLE, oComp);
            	w.writeAttribute( CELLPADDING, "0" );
            	w.writeAttribute( CELLSPACING, "0" );
        		w.startElement( HTMLTag.TR, oComp);
        		w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( STYLE, "vertical-align:top;" );
        		
        		w.writeAttribute( STYLE, "vertical-align:top;" );
        		        		
        		//Div for the add button
            	w.startElement( DIV, oComp);
            	
            		w.writeAttribute(ID, oComp.getClientId() + "_addButton");
            		w.writeAttribute(STYLE, "display:inline");
            		
            		
            		w.startElement(HTMLTag.A);
            			
		    			w.writeAttribute(SRC, "javascript:void(0)");
		    			w.writeAttribute(ONCLICK, XVWScripts.getAjaxCommandScript( bridgeLookup.getLookupCommand(),XVWScripts.WAIT_DIALOG ));
		    			w.writeAttribute(CLASS, "search-lookup-trigger");
	            		
	    			w.endElement(HTMLTag.A);
	    			
        	    w.endElement(DIV);
    			
    			w.endElement(HTMLTag.TD);
    			w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( STYLE, "vertical-align:top;" );
        		
        		//Div for the Remove
            	w.startElement( DIV, oComp);
            		w.writeAttribute(ID, oComp.getClientId() + "_rmButton");
            		w.writeAttribute(STYLE, "display:inline");
            		
	            		w.startElement(HTMLTag.A);
			    			w.writeAttribute(SRC, "javascript:void(0)");
			    			w.writeAttribute(ONCLICK, XVWScripts.getAjaxCommandScript( bridgeLookup.getCleanCommand(),XVWScripts.WAIT_DIALOG ));
			    			w.writeAttribute(CLASS, "search-lookup-clean-trigger");
		    			w.endElement(HTMLTag.A);
	    			
    			w.endElement(DIV);
    			
    			w.endElement(HTMLTag.TD);
    			
    			
    			w.startElement( HTMLTag.TD, oComp);
        		w.writeAttribute( STYLE, "vertical-align:top;" );
        		
            	w.startElement( DIV, oComp);
            		w.writeAttribute(ID, oComp.getClientId() + "_favButton");
            		w.writeAttribute(STYLE, "display:inline");
            		
            		if ( bridgeLookup.getShowFavorites()){
            			StringBuilder b = new StringBuilder(300);
            			b.append("var fav = Ext.get('").append(oComp.getClientId()).append("_fav');");
            			b.append("Ext.get('").append(oComp.getClientId())
            				.append("_left').dom.value=").append("fav.getX();");
            			b.append("Ext.get('").append(oComp.getClientId())
        				.append("_top').dom.value=").append("fav.getY();");
            			b.append(XVWScripts.getAjaxCommandScript( bridgeLookup.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
            			
	            		w.startElement(HTMLTag.A);
	            			w.writeAttribute(ID, bridgeLookup.getClientId()+"_fav");	
			    			w.writeAttribute(SRC, "javascript:void(0)");
			    			w.writeAttribute(ONCLICK, b.toString() );
			    			w.writeAttribute(CLASS, "search-lookup-favorite-trigger");
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
    			
    			w.startElement( INPUT, oComp);
                w.writeAttribute(TYPE, "hidden");
                //w.writeAttribute(VALUE, oAttr.getValue() );

                w.writeAttribute(NAME, oComp.getClientId() + "_toEdit");
                w.writeAttribute(ID, oComp.getClientId() + "_toEdit");
                
                w.endElement(INPUT);
                
                w.startElement( INPUT, oComp);
                w.writeAttribute(TYPE, "hidden");
                //w.writeAttribute(VALUE, oAttr.getValue() );

                w.writeAttribute(NAME, oComp.getClientId() + "_toRemove");
                w.writeAttribute(ID, oComp.getClientId() + "_toRemove");
                
                w.endElement(INPUT);
                
                w.endElement(INPUT);
                
                w.startElement( INPUT, oComp);
                w.writeAttribute(TYPE, "hidden");
                w.writeAttribute(VALUE, "" );

                w.writeAttribute(NAME, oComp.getClientId() + "_top");
                w.writeAttribute(ID, oComp.getClientId() + "_top");
                
                w.endElement(INPUT);
                
                w.startElement( INPUT, oComp);
                w.writeAttribute(TYPE, "hidden");
                w.writeAttribute(VALUE, "" );

                w.writeAttribute(NAME, oComp.getClientId() + "_left");
                w.writeAttribute(ID, oComp.getClientId() + "_left");
                
                w.endElement(INPUT);
    			
    			StringBuilder b = new StringBuilder(300);
    			b.append("var fav = Ext.get('").append(oComp.getClientId()).append("_fav');");
    			b.append("Ext.get('").append(oComp.getClientId())
    				.append("_left').dom.value=").append("fav.getX();");
    			b.append("Ext.get('").append(oComp.getClientId())
				.append("_top').dom.value=").append("fav.getY();");
    			b.append(XVWScripts.getAjaxCommandScript( bridgeLookup.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
    			
    			StringBuilder b2 = encodeChanges(bridgeLookup);
    			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "hideTbl" + oComp.getId(), 
    					b2.toString());
			
        	
        	w.endElement(DIV);
        	
        	encodeExtJs( oComp );
        	
        	
        	// Write Scripts
        	encodeComponentScript( oComp );
        	
        }
        
        
        
}
    
    
    
    
}
