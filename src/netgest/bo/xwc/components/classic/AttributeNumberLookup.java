package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefObjectFilter;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;
import netgest.bo.xwc.framework.jsf.XUIValueChangeEvent;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.io.FSiFile;
import netgest.io.iFile;
/**
 * This component render's a hidden number field with a number and text box with
 * the displayValue of the {@link DataFieldConnector}
 * 
 * @author jcarreira
 *
 */
public class AttributeNumberLookup extends AttributeBase {

	private XUICommand oLookupCommand;
    private XUICommand oOpenCommand;
    
    private XUICommand oFavoriteCommand;

    
    
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
        oOpenCommand.setActionExpression( createMethodBinding( "#{"+getBeanId()+".openLookupObject}" ) );
        getChildren().add( oOpenCommand );

        oFavoriteCommand = new XUICommand();
        oFavoriteCommand.setId( getId() + "_showFav" );
        oFavoriteCommand.setActionExpression( createMethodBinding( "#{"+getBeanId()+".showFavorite}" ) );
        getChildren().add( oFavoriteCommand );
    }
    
    
    @Override
    public void preRender() {
        oLookupCommand = (XUICommand)getChild( 0 );
        oOpenCommand = (XUICommand)getChild( 1 );
        oFavoriteCommand = (XUICommand)getChild( 2 );
    }

    private void doLookup() {
        try {
        	XUIViewRoot root = (XUIViewRoot) findParent( XUIViewRoot.class );
            XEOEditBean oXEOEditBean;
            oXEOEditBean = (XEOEditBean) root.getBean( getBeanId() );
            oXEOEditBean.lookupAttribute( this.getClientId() );
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected XUICommand getFavoriteCommand() {
        return oFavoriteCommand;
    }
    
    protected XUICommand getOpenCommand() {
        return oOpenCommand;
    }
    
    
    
    @Override
	public void validate( FacesContext context ) {
        Object      oSubmitedValue = getSubmittedValue();
        String      sSubmitedValue = null;
        BigDecimal  oSubmitedBigDecimal;
        
        Object oldValue = getValue();
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                    oSubmitedBigDecimal = new BigDecimal( String.valueOf( sSubmitedValue ) );
                    setValue( oSubmitedBigDecimal );
                    //Since we're overriding  the validate, we need to 
                    //activate the value change listeners
                    if (!compareValue(oldValue, oSubmitedValue))
                    	queueEvent(new XUIValueChangeEvent(this, oldValue, oSubmitedValue));
                }
                catch( NumberFormatException ex ) {
                    getRequestContext().addMessage( getClientId(), 
                    		new XUIMessage(
                                XUIMessage.TYPE_MESSAGE,
                                XUIMessage.SEVERITY_ERROR,
                                getLabel(),
                                ComponentMessages.VALUE_ERROR_ON_FORMAT.toString( oSubmitedValue )
                           )
			        );
                    setValid( false );
                }
            }
            else {
                setValue( null );
            }
        }
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
    
    public static class LookupActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            ((AttributeNumberLookup)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    public static class XEOHTMLRenderer extends ExtJsFieldRendeder implements XUIRendererServlet {

    	
		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			AttributeNumberLookup oAtt = (AttributeNumberLookup)oComp;
			if (oAtt.getShowFavorites()){
				XEOObjectAttributeConnector attConnector = (XEOObjectAttributeConnector)oAtt.getDataFieldConnector();
				boDefAttribute attributeDefinition = attConnector.getBoDefAttribute();
				boDefObjectFilter[] objFilter = attributeDefinition.getObjectFilter();
				if (objFilter == null || objFilter.length <= 1)
					return "ExtXeo.form.Lookup";
			}
			return "Ext.form.TwinTriggerField";
		}
		
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oComp )
				throws IOException {

            AttributeNumberLookup  oAttr;
            XUIResponseWriter w = getResponseWriter();
            oAttr = (AttributeNumberLookup)oComp; 
            
            super.encodeBeginPlaceHolder( oComp );
            
            // Add the a style to the place holder DIV
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;height:100%", null );
        	
            w.startElement( HTMLTag.INPUT, oComp);
            w.writeAttribute(HTMLAttr.TYPE, "hidden", null);
            w.writeAttribute(HTMLAttr.VALUE, oAttr.getValue() , null);

            w.writeAttribute(HTMLAttr.NAME, oComp.getClientId() + "_ci", null);
            w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_ci", null);
            
            w.endElement(HTMLTag.INPUT);
            
            if (oAttr.getShowFavorites()){
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
	            
	            //Must init the behavior like this, because the img tag that has the trigger 
	            //is only available ah this time (and not in the initComponent)
	            /*String initHoverBehaviorForFavorites = "Ext.onReady(function() { Ext.getCmp('ext-"+oAttr.getClientId()+"').initFavorite()})";
	            
	            getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, oAttr.getClientId()+"_initFav" + oComp.getId(), 
						initHoverBehaviorForFavorites);*/
            }
            
		}
		
		@Override
		public StateChanged wasStateChanged( XUIComponentBase component, List<XUIBaseProperty<?>> changedProperties ) {
			StateChanged state = super.wasStateChanged( component, changedProperties );
			if (state == StateChanged.FOR_UPDATE){
				if (component.getStateProperty( "disabled" ).wasChanged()){
					if (((AttributeNumberLookup) component).getEnableCardIdLink() ){
						state = StateChanged.FOR_RENDER;
					}
				}
			}
			return state;
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig(AttributeBase oComp) {
            AttributeNumberLookup  	oAttr;
            String              	sFormId;
            Form                	oForm;
			
            oAttr = (AttributeNumberLookup)oComp; 

            ExtConfig oInpConfig = super.getExtJsFieldConfig(oComp);

			boolean enableCardIdLink = oAttr.getEnableCardIdLink();
			boolean readOnly		 = oAttr.isReadOnly();
			boolean disabled		 = oAttr.isDisabled();
            if ( enableCardIdLink && disabled ){
            	disabled =  false;
            	readOnly = true;
            }
			
			sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            
            oInpConfig.add("maxLength", "32" );
            oInpConfig.add("readOnly", true );
        	oInpConfig.add( "enableKeyEvents", true);
            oInpConfig.add("hideTrigger1", false);
            
            if( enableCardIdLink ) {
            	oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            }
            
            
            StringBuilder b = new StringBuilder(300);
            if (oAttr.getShowFavorites()){
	            //We only show favorites if this is not disabled 
	            //Get reference to the image to extract coordinates X,Y
				//b.append("Ext.get('ext-").append(oComp.getClientId()).append("-search')");
				//Get X,Y coordinates and set the input values so that component can read them
				b.append("Ext.get('").append(oComp.getClientId()).append("_left').dom.value=").append("Ext.get('ext-").append(oComp.getClientId()).append("-search')").append(".getX();");
				b.append("Ext.get('").append(oComp.getClientId()).append("_top').dom.value=").append("Ext.get('ext-").append(oComp.getClientId()).append("-search')").append(".getY();");
				//Set the show favorites command
				b.append(XVWScripts.getAjaxCommandScript( oAttr.getFavoriteCommand(),XVWScripts.WAIT_DIALOG ));
				b.append(";");
	            //oInpConfig.add("onLookupHover", b.toString());
            }
            
            
            
            if( readOnly || !oAttr.getEffectivePermission(SecurityPermissions.WRITE)) {
            	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
	            oInpConfig.addJSString("trigger2Class", "x-hidden x-form-search-trigger");
	       
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
	        	//this.onTargetOut(); <<--- Por cause do onMouseOver
	        	oInpConfig.add("onTrigger2Click", "function(){ if(!this.disabled){  " + 
	            		XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_DIALOG ) +
	            		"}}"
	            );
	        	
	        	if (oAttr.getShowFavorites()){
	            	oInpConfig.addJSString("trigger3Class", "x-form-favorite-trigger ");
	                oInpConfig.add("onTrigger3Click", "function(){ if(!this.disabled){" +
	                		b.toString() +	"}}"
	             	);
	            }
            }
			return oInpConfig;
		}
		
		@Override
		public ExtConfig getExtJsFieldListeners(AttributeBase oAttr) {
            String              sFormId;
            Form                oForm;
			
			boolean enableCardIdLink = oAttr.getEnableCardIdLink();
			boolean readOnly		 = oAttr.isReadOnly();
			boolean disabled		 = oAttr.isDisabled();
            if ( enableCardIdLink && disabled ){
            	readOnly = true;
            }
            
            sFormId = oAttr.getNamingContainerId();
            oForm   = (Form)oAttr.findComponent( sFormId );
			
			AttributeNumberLookup oAttLk = (AttributeNumberLookup)oAttr;

            ExtConfig oInpLsnr = super.getExtJsFieldListeners(oAttr);
            if( enableCardIdLink ) {
	            oInpLsnr.add("'render'", "function(){ " +
	            		"this.getEl().dom.onclick=function(event){" +
	            			XVWScripts.getAjaxCommandScript( oAttLk.oOpenCommand ,  XVWScripts.WAIT_DIALOG ) +
	            		"};" +
	            		"}" );
            }
            
            
            if( !disabled && !readOnly ) {
	            oInpLsnr.add("'keydown'", "function(f,e){ " +
	            		"if(e.getKey()==13) {" +
	            			XVWScripts.getAjaxCommandScript( oAttLk.getLookupCommand(),XVWScripts.WAIT_STATUS_MESSAGE ) +
	            			";\ne.stopEvent();" +
	            		"} else if ( e.getKey() == 8 || e.getKey() == 46  ) {" +
	            			getClearCode(oForm, oAttr) +
	            		";\ne.stopEvent();"+
	            "}}" );
            }
            
            /** 
             * Doesn't work well in ExtJs
             */
            //oInpLsnr.add("'focus'", "function(c){ c.selectText(); }");
			
			return oInpLsnr;
		}
		
		
		@Override
		public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
			
			AttributeNumberLookup oAttr = (AttributeNumberLookup)oComp;
			ScriptBuilder sb = super.getEndComponentScript(oComp, false, true );
			String sJsValue = String.valueOf( oComp.getValue() );
			
			boolean enableCardIdLink = oAttr.getEnableCardIdLink();
			boolean disabled		 = oAttr.isDisabled();
            if ( enableCardIdLink && disabled ){
            	disabled =  false;
            }
			
			if( oComp.isRenderedOnClient() ) {
	        	if( "null".equals( sJsValue ) ) {
	        		sJsValue = "NaN";
	        	}
	        	sb.w("var look = document.getElementById('" ).w( oComp.getClientId() ).w("_ci');");
	        	sb.w( " if (look) {document.getElementById('" ).w( oComp.getClientId() ).w("_ci').value = '").w(  sJsValue  ).w( "'; }" );
			}
        	if( oComp.getStateProperty("visible").wasChanged() )
        		sb.w("c.setVisible(").writeValue( oComp.isVisible() ).w(")").endStatement();
        		
    		sb.w("c.setDisabled(").writeValue( disabled ).w(")").endStatement();
			
			sb.endBlock();
			return sb;
		}

        public String getClearCode( Form oForm, AttributeBase oAttr ) {
            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
            	XUIForm form = (XUIForm) oAttr.findParent( XUIForm.class );
	            return "XVW.AjaxCommand( '" + form.getClientId() +  "','" + oAttr.getClientId() + "_clear','true',0);";
            }
            else {
            	return "Ext.ComponentMgr.get('" + getExtComponentId(oAttr) + "').setValue('');\n" + 
	            	   "document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';";
            }
        }

        @Override
        public void decode(XUIComponentBase component) {

            AttributeNumberLookup oAttrComp;
            
            oAttrComp = (AttributeNumberLookup)component;
            
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
        
        
		public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	AttributeFile oFile = (AttributeFile)comp;
        	DataFieldConnector oConnector = oFile.getDataFieldConnector();
        	
        	HttpServletRequest hRequest = (HttpServletRequest)request;
        	
        	if( oConnector instanceof XEOObjectAttributeConnector ) {
        		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
	        	if( "POST".equals( hRequest.getMethod() ) && hRequest instanceof XUIMultiPartRequestWrapper ) {
	        		XUIMultiPartRequestWrapper mRequest = (XUIMultiPartRequestWrapper)hRequest;
					Enumeration<String> enumFiles = mRequest.getFileNames();
	        		if( enumFiles.hasMoreElements() ) {
	        			String fname = (String)enumFiles.nextElement();
	        			File file = mRequest.getFile( fname );
	        			try {
	        				boObject bobj = oXeoConnector.getAttributeHandler().getObject();
	        				bobj.getAttribute("file").setValueiFile( new FSiFile( null, file, null ) );
						} catch (boRuntimeException e) {
							throw new RuntimeException(e);
						}
	        		}
	        	}
	        	else {
	        		try {
        				boObject bobj = oXeoConnector.getAttributeHandler().getObject();
						iFile file = bobj.getAttribute("file").getValueiFile();
						if( file != null ) {
							String sName = file.getName();
							
							ServletContext oCtx = (ServletContext)getFacesContext().getExternalContext().getContext();
							
				            String mimetype = oCtx.getMimeType(sName.toLowerCase());
	
				            resp.setHeader("Cache-Control","private");               
				            ServletOutputStream so = response.getOutputStream(); 
			                resp.setHeader("Content-Disposition","attachment; filename="+sName);
	
				            Long FileSize = new Long(file.length()); 
				            int xfsize = FileSize.intValue(); 
	
				            response.setContentType(mimetype); 
				            response.setContentLength(xfsize); 
	
				            int rb=0; 
				            InputStream is= null;
				            try { 
				                is = file.getInputStream();
				                byte[] a=new byte[4*1024];
				                while ((rb=is.read(a)) > 0) { 
				                    so.write(a,0,rb); 
				                } 
				                is.close();
				            } 
				            catch (Exception e) 
				            {
				            }
				            finally
				            {
				                if( is != null ) is.close();
				            }
				            so.close(); 
						}
					} catch (boRuntimeException e) {
						throw new RuntimeException(e);
					}
	        	}
        	}
		}
    }


}
