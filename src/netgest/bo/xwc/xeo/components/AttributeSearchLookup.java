package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.HREF;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.ONCLICK;
import static netgest.bo.xwc.components.HTMLAttr.SRC;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.A;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.COLGROUP;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;
import static netgest.bo.xwc.components.HTMLTag.SPAN;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWScripts.WaitMode;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

/**
 * 
 * 
 * Truques da classe?
 * 
 * Gera uma função Javascript para invocar o método de pesquisa (por causa do update() precisar de " e de ' e depois tudo ´dentro
 * de uma string em Java dá asneira)
 * 
 * 
 *
 */
public class AttributeSearchLookup extends AttributeNumberLookup {
	
	
	XUICommand oSearchCommand;
	
	@Override
	public void initComponent(){
		super.initComponent();
		oSearchCommand = new XUICommand();
		oSearchCommand.setId( getId() + "_search" );
		oSearchCommand.addActionListener( 
                new SearchActionListener()
        );
        getChildren().add( oSearchCommand );
	}
	
	public static class SearchActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            ((AttributeSearchLookup)((XUICommand)event.getSource()).getParent()).search((XUICommand)event.getSource());
        }
    }
	
	
	private void search(XUICommand command) {
        try {
            XEOEditBean oXEOBaseBean;
            oXEOBaseBean = (XEOEditBean)getRequestContext().getViewRoot().getBean( getBeanId() );
            oXEOBaseBean.searchTextIndexLookup(getClientId(),String.valueOf(command.getCommandArgument()));
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	public void preRender() {
		super.preRender();
	}
	
	@Override
	public StateChanged wasStateChanged2(){
		return StateChanged.FOR_RENDER;
	}
	
	@Override
	public String getDisplayValue(){
		String displayValue = super.getDisplayValue();
		if (displayValue != null && displayValue.length() > 0){
			String objectAttributeReferenceName = ((XEOObjectAttributeConnector)getDataFieldConnector()).
				getAttributeHandler().getDefAttribute().getReferencedObjectName();
			return "<img src='resources/"+objectAttributeReferenceName +
					"/ico16.gif' width='16' height='16' align='bottom' />" +
					super.getDisplayValue();
		} else {
			return "";
		}
	}
	
	
	public String getDisplayValueCardIdLink(){
		String displaValue = getDisplayValue();
		if ( displaValue.length() > 0 ){
			return "<a href='javascript:void(0)' onclick='openListTab"+getId()+"();return false;'>" + getDisplayValue() + "</a>";
		} else {
			return "";
		}
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
    public boolean isReadOnly() {
    	return false;
    }
    
	/**
	 * 
	 * HTML Renderer for the class
	 * 
	 * 
	 * @author PedroRio
	 *
	 */
	public static class XEOHTMLRenderer extends XUIRenderer {

		
		@Override
		public void encodeBegin( XUIComponentBase oComp ) throws IOException{
			
			XUIResponseWriter w = getResponseWriter();
			AttributeSearchLookup oAttr =  (AttributeSearchLookup)oComp;
			
			if (!oAttr.isRenderedOnClient()){
			
				//Placeholder
				w.startElement( DIV, oComp);
				w.writeAttribute( ID, oComp.getClientId(), null );
				
				//Hidden inputs
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
	            
	            w.startElement( HTMLTag.INPUT, oComp);
	            w.writeAttribute(HTMLAttr.TYPE, "hidden", null);
	            w.writeAttribute(HTMLAttr.VALUE, oAttr.getValue() , null);
	
	            w.writeAttribute(HTMLAttr.NAME, oComp.getClientId() + "_ci", null);
	            w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_ci", null);
				
				 
	            String tableStyle = "width:100%;table-layout:fixed;height:21px;margin:0px 0xp 0px 0px; padding:0px 0px 0px 0px;" + (oAttr.isVisible()?"":"display:none;");
	            
	            //Table with elements
	            w.startElement( TABLE, oComp);
	        	w.writeAttribute( CELLPADDING, "0", null );
	        	w.writeAttribute( CELLSPACING, "0", null );
	        	w.writeAttribute( STYLE, tableStyle, null );
	        	w.writeAttribute( ID, oAttr.getClientId()+"_tblBtn", null );
	            	
	            w.startElement( COLGROUP, oComp);
	            
	            w.startElement( COL, oComp);
	            w.writeAttribute( STYLE, "width:100%;text-align:justify;", null );
	            //w.writeAttribute( CLASS, "x-form-text", null );
	            w.endElement(COL);
	            
	            w.startElement( COL, oComp);
	        	w.writeAttribute( ID, oAttr.getClientId()+"_colBtns", null );
	        	String width = "51px;";
	        	if (!oAttr.getShowFavorites())
	        		width = "34px;";
	    		w.writeAttribute( STYLE, 
	    				"vertical-align:top;width:"+width+""
	    	            + (oAttr.isUsable()?"":"display:none;")
	    				,null 
	    		);
	            w.endElement(COL);
	            
	            w.endElement( COLGROUP );
	            	
	            w.startElement( TR, oComp);
	            w.startElement( TD, oComp);
	            w.writeAttribute( ID, oComp.getClientId() + "_lookupColumn", null );
	            //Top 0x = IE9 fix because of ext-ie (top:-1px);
	    		w.writeAttribute( STYLE, "top:0px;border:1px solid #9AB; margin-left:0x; margin-right:0px; padding-left:0px; padding-right:0px;"
	    				+ (oAttr.isVisible()?"":"display:none;"), null );
	    		w.writeAttribute( CLASS, "x-form-text x-form-field", null );
	    		
	    		w.startElement( DIV, oComp);
	    		w.writeAttribute( STYLE, "padding-left:2px;height:19px", null );
	            w.writeAttribute( ID, oComp.getClientId() + "_list", null );
	            if (oAttr.getEnableCardIdLink()){
	            	w.startElement(A, null);
	            	w.writeAttribute( HREF, "javascript:void(0)", null );
	        		w.writeAttribute( ONCLICK, 
	        				XVWScripts.getAjaxCommandScript( oAttr.getOpenCommand() ,  XVWScripts.WAIT_DIALOG ) , null );
	            	w.write(oAttr.getDisplayValue());
	            	w.endElement(A);
	            } else{
	            	w.write(oAttr.getDisplayValue());
	            }
	            
	            w.endElement(DIV);
			}
            
		}
		
		@Override
		public void encodeEnd( XUIComponentBase oComp ) throws IOException{
			
			XUIResponseWriter w = getResponseWriter();
			AttributeSearchLookup oAttr = (AttributeSearchLookup) oComp;
			
			if (!oAttr.isRenderedOnClient()){
				
				w.startElement(INPUT, oComp);
				w.writeAttribute(ID, oComp.getClientId() + "_inputSearch", null);
    			w.writeAttribute(NAME, oComp.getClientId()+ "_inputSearch", null);
				w.writeAttribute(TYPE, "text", null);
				if (!oAttr.isUsable())
					w.writeAttribute(CLASS, "xwc-unusable", null);
				
				w.writeAttribute(STYLE, "width:100%;height:19px;border-width:0px;margin: 0x 0px 0px 0px; padding:0px 0px 0px 2px;display:none;" +
						"background-color:transparent;font-family: tahoma,verdana,helvetica;font-size: 12px;", null);
				w.endElement(INPUT);
				
        		w.endElement(TD);
        		
        		w.startElement( TD, oComp);
            	w.writeAttribute( ID, oAttr.getClientId()+"_tdBtns", null );
            	w.writeAttribute( 	STYLE, "border-right:1px solid #9AB;border-bottom:1px solid #9AB;border-top:1px solid #9AB;"
        	        + (oAttr.isUsable()?"":"display:none;")
        			, null 
        		);
        		
            	w.startElement( TABLE, oComp);
            	w.writeAttribute( CELLPADDING, "0", null );
            	w.writeAttribute( CELLSPACING, "0", null );
        		w.startElement( TR, oComp);
        		w.startElement( TD, oComp);
        		w.writeAttribute( STYLE, "", null );
        		
        		        		
        		//Div for the add button
            	w.startElement( DIV, oComp);
            	
            		w.writeAttribute(ID, oComp.getClientId() + "_addButton", null);
            		w.writeAttribute(STYLE, "display:inline;margin:0px 0px 0px 0px;padding:0px 0px 0px 0px;", null);
            		
            		w.startElement( SPAN, oComp);
            		w.writeAttribute(CLASS, "search-lookup-trigger", null);
            		
            			w.startElement(A, null);
	            			w.writeAttribute(ID, oAttr.getClientId()+"_add", null);
			    			w.writeAttribute(SRC, "javascript:void(0)", null);
			    			if (oAttr.isUsable())
			    				w.writeAttribute(ONCLICK, XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_DIALOG ), null);
			    			w.writeAttribute(CLASS, "search-lookup-trigger", null);
			    			
		    			w.endElement(A);
		    			
		    			w.endElement(SPAN);		
		    			
        	    w.endElement(DIV);
    			
    			w.endElement(TD);
    			w.startElement( TD, oComp);
        		w.writeAttribute( STYLE, "", null );
        		
        		//Div for the Remove
            	w.startElement( DIV, oComp);
            		w.writeAttribute(ID, oComp.getClientId() + "_rmButton", null);
            		w.writeAttribute(STYLE, "display:inline", null);
            		
	            		w.startElement(A, null);
	            		w.writeAttribute(ID, oAttr.getClientId()+"_rm", null);
			    			w.writeAttribute(SRC, "javascript:void(0)", null);
			    			if (oAttr.isUsable())
			    				w.writeAttribute(ONCLICK, getClearCode((Form)oAttr.findParentComponent(XUIForm.class), oAttr), null);
			    			w.writeAttribute(CLASS, "search-lookup-clean-trigger", null);
		    			w.endElement(A);
	    			
    			w.endElement(DIV);
    			
    			w.endElement(TD);
    			
    			if (oAttr.getShowFavorites()){
	    			w.startElement( TD, oComp);
	        		w.writeAttribute( STYLE, "", null );
	        		//Div for the remove button
	            	w.startElement( DIV, oComp);
	            		w.writeAttribute(ID, oComp.getClientId() + "_favButton", null);
	            		w.writeAttribute(STYLE, "display:inline", null);
	            		
	            		
	            			StringBuilder b = new StringBuilder(300);
	            			//We only show favorites if this is not disabled 
	        	            //Get reference to the image to extract coordinates X,Y
	        				//b.append("Ext.get('ext-").append(oComp.getClientId()).append("-search')");
	        				//Get X,Y coordinates and set the input values so that component can read them
	        				b.append("Ext.get('").append(oComp.getClientId()).append("_left').dom.value=").append("Ext.fly('").append(oComp.getClientId()).append("_fav')").append(".getX();");
	        				b.append("Ext.get('").append(oComp.getClientId()).append("_top').dom.value=").append("Ext.fly('").append(oComp.getClientId()).append("_fav')").append(".getY();");
	        				//Set the show favorites command
	        				b.append(XVWScripts.getAjaxCommandScript( oAttr.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
	        				b.append(";");
	            			
		            		w.startElement(A, null);
		            			w.writeAttribute(ID, oAttr.getClientId()+"_fav", null);	
				    			w.writeAttribute(SRC, "javascript:void(0)", null);
				    			if (oAttr.isUsable())
				    				w.writeAttribute(ONCLICK, b.toString() , null);
				    			w.writeAttribute(CLASS, "search-lookup-favorite-trigger", null);
			    			w.endElement(A);
	            		
		    			
	    			w.endElement(DIV);
	    			
	    			w.endElement(TD);
    			}
    			w.endElement(TR);
    			w.endElement(TABLE);
    			
    			w.endElement(TD);
    			
    			//END teste favorites
    			
    			w.endElement(TR);
    			w.endElement(TABLE);
    			
    			w.endElement(DIV);
    			
    			addEventsToHtmlComponent(oAttr, oAttr.getClientId());
    			
			}
			else{ //Deal with Visible/ReadOnly and Disabled
            	
            	JavascriptGeneratorForVisibilityAndUsability scriptGenerator = new JavascriptGeneratorForVisibilityAndUsability(oAttr);
            	getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "hideTbl" + oComp.getId(), 
            		scriptGenerator.generate());
			}
            	
		}
		
		
		
		/**
		 * 
		 * Helper Class to generate all the Javascript calls to deal with visiblity / disabled
		 * states of the component
		 * 
		 * 
		 */
		private class JavascriptGeneratorForVisibilityAndUsability{
			
			private StringBuilder b;
			private AttributeSearchLookup oAttr;
			
			public JavascriptGeneratorForVisibilityAndUsability(AttributeSearchLookup oAtt){
				this.b = new StringBuilder(300);
				this.oAttr = oAtt;
			}
			
			
			public String generate(){
				
				Form oForm = (Form)oAttr.findParentComponent(XUIForm.class);
				boolean needsToTriggerSubmitBecauseOfFormulas =  oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit(); 
				if ( needsToTriggerSubmitBecauseOfFormulas ){
					b.append("XVW.updateValue( '" + oAttr.getClientId() + "_ci', "+oAttr.getValue()+" );");
				} else{
					b.append("document.getElementById('" + oAttr.getClientId() + "_ci').value="+oAttr.getValue()+";");
				}
					
				b.append("Ext.fly('").append(oAttr.getClientId()).append("_list').update(\""+getDisplayValueForLookup()+"\");");
            	final String lookupColumnId = oAttr.getClientId() + "_lookupColumn";
            	final String entireTableId = oAttr.getClientId() + "_tblBtn";
            	final String inputSearchId = oAttr.getClientId() + "_inputSearch";
            	final String actionButtonsColumnsId = oAttr.getClientId() + "_tdBtns";
            	final String columnButtonGroup = oAttr.getClientId() + "_colBtns";
            	
            	if (oAttr.isUsable()){ //Everything OK, show the buttons
            		showHtmlElement( entireTableId );
            		showHtmlElement( lookupColumnId );
            		showHtmlElement( actionButtonsColumnsId );
            		showHtmlElement( columnButtonGroup );
            		removeCssClass(b, inputSearchId, "xwc-unusable" );
            	} else {
            		addCssClass( b, inputSearchId, "xwc-unusable" );
            		if( !oAttr.isVisible() ) {
            			hideHtmlElement( entireTableId );
            			hideHtmlElement( lookupColumnId );
            		} else {
            			showHtmlElement( entireTableId );
            			showHtmlElement( lookupColumnId );
            			boolean cannotChangeObjectRelation = (oAttr.isDisabled() || oAttr.isReadOnly() || oAttr.getEffectivePermission( SecurityPermissions.WRITE )); 
            			if (cannotChangeObjectRelation) {
            				hideHtmlElement( actionButtonsColumnsId );
            				hideHtmlElement( columnButtonGroup );
                		}
            			else {
            				showHtmlElement( actionButtonsColumnsId );
            				showHtmlElement( lookupColumnId );
            			}
            		}
            	}
				
				return b.toString();
			}
			
			private String getDisplayValueForLookup(){
				if (oAttr.getEnableCardIdLink())
	        		return oAttr.getDisplayValueCardIdLink();
	        	else
	        		return oAttr.getDisplayValue();
			}
			
			private void showHtmlElement(String componentId){
				setDisplayOfElement(componentId, true);
			}
			
			private void hideHtmlElement(String componentId){
				setDisplayOfElement(componentId, false);
			}
			
			
			
			private void setDisplayOfElement(String idComponent, boolean visible ){
				String cssDisplayProperty = "";
				if ( !visible )
					cssDisplayProperty = "none";
				//Ext.fly('elementId').setDisplayed(''|'none');
				b.append("Ext.fly('").append(idComponent).append("').setDisplayed('").append(cssDisplayProperty).append("');");
			}
			
			private void addCssClass(StringBuilder builder, String idComponent, String cssClass){
				setElementClass(builder, idComponent, cssClass, true);
			}
			
			private void removeCssClass(StringBuilder builder, String idComponent, String cssClass){
				setElementClass(builder, idComponent, cssClass, false);
			}
			
			private void setElementClass(StringBuilder builder, String idComponent, String cssClass, boolean add){
				String extFunction = "addClass(";
				if (!add)
					extFunction = "removeClass(";
				//Ext.fly('elementId').addClass|removeClass('className')
				builder.append("Ext.fly('").append(idComponent).append("').").append(extFunction).append("'").append(cssClass).append("');");
				
			}
			
		}
		
		
		
		private void addEventsToHtmlComponent(AttributeSearchLookup att, String clientId){
			StringBuilder builder = new StringBuilder();
			
			//Create function to invoke the open tab command (can't seem to update
			//the html with a function cla
			builder.append("openListTab"+att.getId()+" = function(){");
			builder.append(XVWScripts.getAjaxCommandScript( att.getOpenCommand() ,  
					WaitMode.LOCK_SCREEN));
			builder.append("};");
			
			builder.append("Ext.onReady(function(){ ");
			builder.append("ExtXeo.switchLookup('");
			builder.append(clientId);
			builder.append("');");
			//Fix For IE9 size of column
			String width = "55px";
			if (!att.getShowFavorites())
				width = "36px";
			builder.append("if (Ext.isIE) {Ext.fly('"+att.getClientId()+"_colBtns').setStyle('width','"+width+"');}");
			builder.append("});");
			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "openTab" + att.getClientId(), builder.toString());	
		}
		
		/**
		 * 
		 * Generates the Javascript to clear the value of the component 
		 * 
		 * @param oForm The parent form of the component
		 * @param oAttr The component
		 * 
		 * @return A javascript statement to clear the component's value
		 */
		private String getClearCode( Form oForm, AttributeBase oAttr ) {
            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
            	return "XVW.AjaxCommand( '" + oAttr.getNamingContainerId() +  "','" + oAttr.getId() + "_clear','true');";
            }
            else {
            	return "document.getElementById('" + oAttr.getClientId() + 
            		"_ci').value='NaN';document.getElementById('"+oAttr.getClientId()+"_list').innerHTML=''";
            }
        }
		
		
		
		
		@Override
	    public void decode(XUIComponentBase component) {
			
			 AttributeSearchLookup oAttrComp;
	         oAttrComp = (AttributeSearchLookup) component;
	         
	         if (oAttrComp.isUsable()){
	         
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
	         }
	         super.decode(component);
		}
		
	}
	
	

}


