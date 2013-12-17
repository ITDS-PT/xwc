package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWScripts.WaitMode;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;

/**
 *
 * Generic Lookup Component
 *
 */
public class GenericLookup extends AttributeBase {

	private XUIMethodBindProperty lookupCommand = new XUIMethodBindProperty( "lookupCommand", this );
	
	public void setLookupCommand( String cmdExpr ){
		this.lookupCommand.setExpressionText( cmdExpr );
	}
	
	private XUIMethodBindProperty openCommand = new XUIMethodBindProperty( "openCommand", this );
	
	public void setOpenCommand( String openExpr){
		this.openCommand.setExpressionText(  openExpr );
	}
	
	
	private XUICommand oLookupCommand;
    private XUICommand oOpenCommand;
    

    
    
    @Override
	public void initComponent() {
        // per component initializations.
        oLookupCommand = new XUICommand();
        oLookupCommand.setId( getId() + "_lk" );
        oLookupCommand.setActionExpression( lookupCommand.getValue() );
        getChildren().add( oLookupCommand );
        
        oOpenCommand = new XUICommand();
        oOpenCommand.setId( getId() + "_op" );
        oOpenCommand.setActionExpression( openCommand.getValue() );
        getChildren().add( oOpenCommand );

    }
    
    
    @Override
    public void preRender() {
        oLookupCommand = (XUICommand)getChild( 0 );
        oOpenCommand = (XUICommand)getChild( 1 );
    }

    
    protected XUICommand getOpenCommand() {
        return oOpenCommand;
    }
    
    
    
    protected void setLookupCommand(XUICommand oLookupCommand) {
        this.oLookupCommand = oLookupCommand;
    }

    protected XUICommand getLookupCommand() {
        return oLookupCommand;
    }

    @Override
    public Object saveState() {
    	return super.saveState();
    }
    @Override
    public void setRenderedOnClient(boolean renderedOnClient) {
    	super.setRenderedOnClient(renderedOnClient);
    }
    
    
    public static class XEOHTMLRenderer extends ExtJsFieldRendeder  {

    	
		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.TwinTriggerField";
		}
		
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oComp )
				throws IOException {

            GenericLookup  oAttr;
            XUIResponseWriter w = getResponseWriter();
            oAttr = (GenericLookup)oComp; 
            
            super.encodeBeginPlaceHolder( oComp );
            
            // Add the a style to the place holder DIV
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;height:100%", null );
        	
            w.startElement( HTMLTag.INPUT, oComp);
            w.writeAttribute(HTMLAttr.TYPE, "hidden", null);
            w.writeAttribute(HTMLAttr.VALUE, oAttr.getValue() , null);

            w.writeAttribute(HTMLAttr.NAME, oComp.getClientId() + "_ci", null);
            w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_ci", null);
            
            w.endElement(HTMLTag.INPUT);
            
            
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig(AttributeBase oComp) {
            GenericLookup  	oAttr;
            Form                	oForm;
			
            oAttr = (GenericLookup)oComp; 

            ExtConfig oInpConfig = super.getExtJsFieldConfig(oComp);

			boolean enableCardIdLink = oAttr.getEnableCardIdLink();
            oForm   = (Form)oComp.findParent(Form.class);
            
            oInpConfig.add("maxLength", "100" );
            oInpConfig.add("readOnly", true );
        	oInpConfig.add( "enableKeyEvents", true);
            oInpConfig.add("hideTrigger1", false);
            
            if( enableCardIdLink ) {
            	oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            }
            
            if (enableCardIdLink && oAttr.isDisabled()){
            	oAttr.setDisabled("false");
            	oAttr.setReadOnly("true");
            }
            
            
            if( oAttr.isReadOnly() || !oAttr.getEffectivePermission(SecurityPermissions.WRITE)) {
            	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
	            oInpConfig.addJSString("trigger2Class", "x-hidden x-form-search-trigger");
	            if (enableCardIdLink)
	            	oAttr.setReadOnly("true");
	            else
	            	oAttr.setDisabled("true");
            }
            else {
	        	if ( !oAttr.getEffectivePermission(SecurityPermissions.DELETE))
	            	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
	        	else
	            	oInpConfig.addJSString("trigger1Class", "x-form-clear-trigger");
	        	
	        	oInpConfig.addJSString("trigger2Class", "x-form-search-trigger");
	            
	            oInpConfig.add("onTrigger1Click", "function(){if(!this.disabled){ " +
	            			getClearCode(oForm, oAttr) +
	            		"}}"
	            );
	        	oInpConfig.add("onTrigger2Click", "function(){ if(!this.disabled){  " + 
	            		XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_DIALOG ) +
	            		"}}"
	            );
	        	
            }
			return oInpConfig;
		}
		
		@Override
		public ExtConfig getExtJsFieldListeners(AttributeBase oAttr) {
            Form                oForm;
            
            oForm   = (Form)oAttr.findParent(Form.class);
			
			GenericLookup oAttLk = (GenericLookup)oAttr;
			boolean enableCardIdLink = oAttr.getEnableCardIdLink();

            ExtConfig oInpLsnr = super.getExtJsFieldListeners(oAttr);
            if( enableCardIdLink ) {
	            oInpLsnr.add("'render'", "function(){ " +
	            		"this.getEl().dom.onclick=function(event){" +
	            			XVWScripts.getAjaxCommandScript( oAttLk.oOpenCommand ,  WaitMode.LOCK_SCREEN ) +
	            		"};" +
	            		"}" );
            }
            
            
            if( !oAttr.isDisabled() && !oAttr.isReadOnly() ) {
	            oInpLsnr.add("'keydown'", "function(f,e){ " +
	            		"if(e.getKey()==13) {" +
	            			XVWScripts.getAjaxCommandScript( oAttLk.getLookupCommand(),WaitMode.LOCK_SCREEN ) +
	            			";\ne.stopEvent();" +
	            		"} else if ( e.getKey() == 8 || e.getKey() == 46  ) {" +
	            			getClearCode(oForm, oAttr) +
	            		";\ne.stopEvent();"+
	            "}}" );
            }
            
			return oInpLsnr;
		}
		
		
		@Override
		public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
			ScriptBuilder sb = super.getEndComponentScript(oComp, false, true );
			String sJsValue = String.valueOf( oComp.getValue() );
			if( oComp.isRenderedOnClient() ) {
	        	if( "null".equals( sJsValue ) ) {
	        		sJsValue = "NaN";
	        	}
	        	sb.w("var look = document.getElementById('" ).w( oComp.getClientId() ).w("_ci');");
	        	sb.w( " if (look) {document.getElementById('" ).w( oComp.getClientId() ).w("_ci').value = '").w(  sJsValue  ).w( "'; }" );
			}
        	if( oComp.getStateProperty("visible").wasChanged() )
        		sb.w("c.setVisible(").writeValue( oComp.isVisible() ).w(")").endStatement();
        		
        	if( oComp.getStateProperty("disabled").wasChanged() )
        		sb.w("c.setDisabled(").writeValue( oComp.isDisabled() ).w(")").endStatement();
			
			sb.endBlock();
			return sb;
		}

        public String getClearCode( Form oForm, AttributeBase oAttr ) {
            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
	            return "XVW.AjaxCommand( '" + oAttr.getNamingContainerId() +  "','" + oAttr.getId() + "_clear','true');";
            }
            else {
            	return "Ext.ComponentMgr.get('" + getExtComponentId(oAttr) + "').setValue('');\n" + 
	            	   "document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';";
            }
        }

        @Override
        public void decode(XUIComponentBase component) {

            GenericLookup oAttrComp;
            
            oAttrComp = (GenericLookup)component;
            
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
        
        
		
    }


}
