package netgest.bo.xwc.components.classic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Enumeration;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
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

    }
    
    
    @Override
    public void preRender() {
        oLookupCommand = (XUICommand)getChild( 0 );
        oOpenCommand = (XUICommand)getChild( 1 );
    }

    private void doLookup() {
        try {
            XEOBaseBean oXEOBaseBean;
            oXEOBaseBean = (XEOBaseBean)getRequestContext().getViewRoot().getBean("viewBean");
            oXEOBaseBean.lookupAttribute( this.getClientId() );
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
	public void validate( FacesContext context ) {
        Object      oSubmitedValue = getSubmittedValue();
        String      sSubmitedValue = null;
        BigDecimal  oSubmitedBigDecimal;
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                    oSubmitedBigDecimal = new BigDecimal( String.valueOf( sSubmitedValue ) );
                    setValue( oSubmitedBigDecimal );
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

    public static class LookupActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            ((AttributeNumberLookup)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    public static class XEOHTMLRenderer extends ExtJsFieldRendeder implements XUIRendererServlet {

    	
		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
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
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig(AttributeBase oComp) {
            AttributeNumberLookup  	oAttr;
            String              	sFormId;
            Form                	oForm;
			
            oAttr = (AttributeNumberLookup)oComp; 

            ExtConfig oInpConfig = super.getExtJsFieldConfig(oComp);

			boolean enableCardIdLink = oAttr.getEnableCardIdLink();
			sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            
            oInpConfig.add("maxLength", "32" );
            oInpConfig.add("readOnly", true );
        	oInpConfig.add( "enableKeyEvents", true);
            oInpConfig.add("hideTrigger1", false);
            
            
            if( enableCardIdLink ) {
            	oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            }

            if( oAttr.isDisabled() || oAttr.isReadOnly() || !oAttr.getEffectivePermission(SecurityPermissions.WRITE) ) {
	            oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
	            oInpConfig.addJSString("trigger2Class", "x-hidden x-form-search-trigger");
            }
            else {
            	if ( !oAttr.getEffectivePermission(SecurityPermissions.DELETE) )
	            	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
            	else
	            	oInpConfig.addJSString("trigger1Class", "x-form-clear-trigger");
            		
	            oInpConfig.addJSString("trigger2Class", "x-form-search-trigger");
            }
            
            if( !oAttr.isDisabled() && !oAttr.isReadOnly() ) {
	            oInpConfig.add("onTrigger1Click", "function(){ " +
	            			getClearCode(oForm, oAttr) +
	            		"}"
	            );
            	
            	oInpConfig.add("onTrigger2Click", "function(){ " +
	            		XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_STATUS_MESSAGE ) +
	            		"}"
	            );
            }
			return oInpConfig;
		}
		
		@Override
		public ExtConfig getExtJsFieldListeners(AttributeBase oAttr) {
            String              sFormId;
            Form                oForm;
			
            sFormId = oAttr.getNamingContainerId();
            oForm   = (Form)oAttr.findComponent( sFormId );
			
			AttributeNumberLookup oAttLk = (AttributeNumberLookup)oAttr;
			boolean enableCardIdLink = oAttr.getEnableCardIdLink();

            ExtConfig oInpLsnr = super.getExtJsFieldListeners(oAttr);
            if( enableCardIdLink ) {
	            oInpLsnr.add("'render'", "function(){ " +
	            		"this.getEl().dom.onclick=function(event){" +
	            			XVWScripts.getAjaxCommandScript( oAttLk.oOpenCommand ,  XVWScripts.WAIT_STATUS_MESSAGE ) +
	            		"};" +
	            		"}" );
            }
            
            if( !oAttr.isDisabled() && !oAttr.isReadOnly() ) {
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
			ScriptBuilder sb = super.getEndComponentScript(oComp, false, true );
			String sJsValue = String.valueOf( oComp.getValue() );
			if( oComp.isRenderedOnClient() ) {
	        	if( "null".equals( sJsValue ) ) {
	        		sJsValue = "NaN";
	        	}
	        	sb.w( "document.getElementById('" ).w( oComp.getClientId() ).w("_ci').value = '").w(  sJsValue  ).w( "';" );
			}
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
        
        
        @SuppressWarnings("unchecked")
		public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	AttributeFile oFile = (AttributeFile)comp;
        	DataFieldConnector oConnector = oFile.getDataFieldConnector();
        	
        	HttpServletRequest hRequest = (HttpServletRequest)request;
        	
        	if( oConnector instanceof XEOObjectAttributeConnector ) {
        		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
	        	if( "POST".equals( hRequest.getMethod() ) && hRequest instanceof XUIMultiPartRequestWrapper ) {
	        		XUIMultiPartRequestWrapper mRequest = (XUIMultiPartRequestWrapper)hRequest;
					Enumeration<Object> enumFiles = mRequest.getFileNames();
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
