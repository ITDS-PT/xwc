package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.xeodm.XEODMBuilder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;
import netgest.bo.xwc.xeo.beans.FileBrowseBean;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.io.FSiFile;
import netgest.io.iFile;

/**
 * This component renders a attributeFile type components, but add some functionality
 * to work with Microsoft Word Templates
 * @author jcarreira
 *
 */
public class AttributeWordMacro extends AttributeBase {
	
    private XUICommand oLookupCommand;

    @Override
	public void initComponent() {
        // per component inicializations.
        if( getChildCount() == 0 ) {
            oLookupCommand = new XUICommand();
            oLookupCommand.setId( getId() + "_lk" );
            oLookupCommand.addActionListener( 
                    new LookupActionListener()
                );
            getChildren().add( oLookupCommand );
        }
        else {
            oLookupCommand = (XUICommand)getChild( 0 );
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
            	AttributeHandler a = ((XEOObjectAttributeConnector)getDataFieldConnector()).getAttributeHandler();
            	if( a.isObject() ) {
	                try {
	                    oSubmitedBigDecimal = new BigDecimal( String.valueOf( sSubmitedValue ) );
	                    setValue( oSubmitedBigDecimal );
	                }
	                catch( NumberFormatException ex ) {
	                	
	                	
	                	
	                    getRequestContext().addMessage( getClientId(), new XUIMessage(
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
            		setValue( oSubmitedValue );
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
    	if(	oLookupCommand == null ) {
            oLookupCommand = (XUICommand)getChild( 0 );
    	}
        return oLookupCommand;
    }

    public static class LookupActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            ((AttributeWordMacro)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    private void doLookup() {
    	if( !((XEOObjectAttributeConnector)getDataFieldConnector()).getAttributeHandler().isObject() ) {
	    	XUISessionContext oSessionContext = getRequestContext().getSessionContext();
	    	
	    	XUIViewRoot oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/components/viewers/FileBrowse.xvw" );
	    	getRequestContext().setViewRoot( oViewRoot );
	    	
	    	FileBrowseBean oFileBrowseBean = (FileBrowseBean)oViewRoot.getBean( "viewBean" );
	    	
	    	oFileBrowseBean.setParentComponentId( getClientId() );
	    	oFileBrowseBean.setParentBeanId("viewBean" );
	    	
	    	getRequestContext().renderResponse();
    	}
    	else {
            try {
                XEOEditBean oXEOBaseBean;
                oXEOBaseBean = (XEOEditBean)getRequestContext().getViewRoot().getBean("viewBean");
                oXEOBaseBean.lookupAttribute( this.getClientId() );
            } catch (boRuntimeException e) {
                throw new RuntimeException(e);
            }
    	}
    }
    
    public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            AttributeWordMacro  oAttr;
            
            XUIResponseWriter w = getResponseWriter();
            oAttr = (AttributeWordMacro)oComp; 
            
            // Place holder for the component
            w.startElement( DIV, oComp );
            w.writeAttribute( ID, oComp.getClientId(), null );
            w.writeAttribute( HTMLAttr.STYLE, "width:100%;height:100%", null );

            w.startElement( HTMLTag.INPUT, oComp);
            w.writeAttribute(HTMLAttr.TYPE, "hidden", null);
            w.writeAttribute(HTMLAttr.VALUE, oAttr.getValue() , null);

            w.writeAttribute(HTMLAttr.NAME, oComp.getClientId() + "_ci", null);
            w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_ci", null);
            
            w.endElement(HTMLTag.INPUT);
            
            w.endElement( DIV ); 

            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( oComp )
            );

        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributeWordMacro  oAttr;
            String              sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 250 );

            oAttr = (AttributeWordMacro)oComp; 

            //sJsValue = String.valueOf( oAttr.getValue() ); 
            sJsValue = String.valueOf( oAttr.getDisplayValue() ); 
            
            ExtConfig oInpConfig = new ExtConfig("Ext.form.TwinTriggerField");
            
            oInpConfig.addJSString("renderTo", oComp.getClientId());
            oInpConfig.add("hideTrigger1", false);
            oInpConfig.addJSString("name", oAttr.getClientId() );
            oInpConfig.addJSString("id", oAttr.getClientId() + "_c" );
           
            oInpConfig.addJSString("value", sJsValue );
            oInpConfig.add("readOnly", true );
            
        	oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            
            if( oAttr.isDisabled() || oAttr.isReadOnly() ) {
            	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
	            oInpConfig.addJSString("trigger2Class", "x-hidden x-form-search-trigger");
            }
            else {
                oInpConfig.addJSString("trigger1Class", "x-form-clear-trigger");
                oInpConfig.addJSString("trigger2Class", "x-form-search-trigger");
            }
            
        	oInpConfig.addJSString("trigger1Class", "x-hidden x-form-clear-trigger");
            oInpConfig.addJSString("trigger2Class", "x-hidden x-form-search-trigger");
            
            
            
            if( !((XEOObjectAttributeConnector)oAttr.getDataFieldConnector()).getAttributeHandler().isObject() ) 
            {
	            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
		            oInpConfig.add("onTrigger1Click", "function(){ if(!this.disabled){ " +
		            		"Ext.ComponentMgr.get('" + oAttr.getClientId() + "_c').setValue('');\n" + 
		            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
		            		XVWScripts.getAjaxCommandScript(oAttr,  XVWScripts.WAIT_STATUS_MESSAGE ) + 
		            		"}}"
		            );
	            }
	            else {
		            oInpConfig.add("onTrigger1Click", "function(){ if(!this.disabled){" +
		            		"Ext.ComponentMgr.get('" + oAttr.getClientId() + "').setValue('');\n" + 
		            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
		            		"}}"
		            );
	            }
	            oInpConfig.add("onTrigger2Click", "function(){if(!this.disabled){ " +
	            		XVWScripts.getOpenCommandWindow( oAttr.getLookupCommand(), 
	            				oAttr.getLookupCommand() + "_" + System.currentTimeMillis() ) +
	            		"}}"
	            );
            }
	        else {
	            if( !oAttr.isDisabled() ) {
		            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
			            oInpConfig.add("onTrigger1Click", "function(){ " +
	                    "XVW.AjaxCommand( '" + oAttr.getNamingContainerId() +  "','" + oAttr.getId() + "_clear','true')" +
			            		"}"
			            );
		            }
		            else {
			            oInpConfig.add("onTrigger1Click", "function(){ " +
			            		"Ext.ComponentMgr.get('" + oAttr.getClientId() + "_c').setValue('');\n" + 
			            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
			            		"}"
			            );
		            }
		
		            oInpConfig.add("onTrigger2Click", "function(){ " +
		            		XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_DIALOG ) +
		            		"}"
		            );
	            }
	        }
 
            oInpConfig.add("width", oAttr.getWidth() );
            oInpConfig.addJSString("renderTo", oComp.getClientId());

            ExtConfig oInpLsnr;
            oInpLsnr = oInpConfig.addChild("listeners");
            oInpLsnr.add("'change'", "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                            + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
                         );
            //'javax.faces.ViewState'
            String sViewState = getRequestContext().getViewRoot().getViewState();
            //xvw.servlet
            String sServletId = oComp.getClientId();
            
            String sActionUrl = getRequestContext().getActionUrl();
            
            if( sActionUrl.indexOf('?') != -1 ) {
            	sActionUrl += "&";
            }
            else {
            	sActionUrl += "?";
            }
            sActionUrl += "javax.faces.ViewState=" + sViewState;
            sActionUrl += "&xvw.servlet=" + sServletId;
            	
            
            // Render XEODM Protocol
            
            XEODMBuilder dmb = new XEODMBuilder();
            HttpServletRequest req = (HttpServletRequest)getRequestContext().getFacesContext().getExternalContext().getRequest();
            Cookie[] cookies = req.getCookies();
            

            for( Cookie cookie : cookies ) {
            	if( "JSESSIONID".equals( cookie.getName() ) )
            		dmb.put( "httpc", cookie.getName() + "=" + cookie.getValue() );
            }
//            dmb.put( "httpp","javax.faces.ViewState=" + sViewState );
//            dmb.put( "httpp", "xvw.servlet=" + sServletId );
            String link = 
            	(req.isSecure()?"https":"http") + "://" + 
            	req.getServerName() +
            	(req.getServerPort()==80?"":":"+req.getServerPort()) + sActionUrl;

            
            dmb.put("link", link );
            dmb.put("action", "open");
            oInpLsnr.add("'render'", "function(){ " +
            		"this.getEl().dom.onclick=function(event){" +
//            		"	if( event.srcElement.value.length > 0 ) {" +
//            		"		XVW.downloadFile('" + dmb.toUrlString() + "');" +
            		"		document.location.href='" + dmb.toUrlString() + "';" +
//					"	}" +
            		"};" +
            		"}" );
            
            oInpConfig.renderExtConfig( sOut );          
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeWordMacro oAttrComp;
            
            oAttrComp = (AttributeWordMacro)component;
            
            String clear = getRequestContext().getRequestParameterMap().get( oAttrComp.getClientId()+"_clear" );
            if( "true".equals( clear ) ) {
                oAttrComp.setSubmittedValue( "" );
            } else {
                            
                String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() + "_ci" );
                
                if( value != null ) {
	                if( "NaN".equals( value ) ) {
	                    oAttrComp.setSubmittedValue( "" );
	                }
	                else {
	                	if( ((XEOObjectAttributeConnector)oAttrComp.getDataFieldConnector()).getAttributeHandler().isObject() ) {
	                		oAttrComp.setSubmittedValue( value );
	                	}
	                }
                }
            }
            super.decode(component);

        }
        
        
        @SuppressWarnings("unchecked")
		public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	 
        	AttributeWordMacro oFile = (AttributeWordMacro)comp;
        	DataFieldConnector oConnector = oFile.getDataFieldConnector();
        	
        	//TODO: Criar no AttributeConnector suporte para ficheiros
        	HttpServletRequest hRequest = (HttpServletRequest)request;
        	
        	if( oConnector instanceof XEOObjectAttributeConnector ) {
        		try {
	        		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
	        		String   objAtt = "file"; 
	        		boObject docObj = oXeoConnector.getAttributeHandler().getObject();
	        		
	        		if( docObj == null ) {
	        			objAtt = oFile.getObjectAttribute();
	        			docObj = oXeoConnector.getAttributeHandler().getParent();
	        		}
	        		
		        	if( "POST".equals( hRequest.getMethod() ) && hRequest instanceof XUIMultiPartRequestWrapper ) {
						if( oFile.isDisabled() ) {
							resp.sendError(403,ComponentMessages.FILE_NOT_EDITABLE.toString());
						} else {
			        		XUIMultiPartRequestWrapper mRequest = (XUIMultiPartRequestWrapper)hRequest;
							Enumeration enumFiles = mRequest.getFileNames();
			        		if( enumFiles.hasMoreElements() ) {
			        			String fname = (String)enumFiles.nextElement();
			        			File file = mRequest.getFile( fname );
			        			try {
			        				docObj.getAttribute( objAtt ).setValueiFile( new FSiFile( null, file, null ) );
								} catch (boRuntimeException e) {
									// TODO Auto-generated catch block
									throw new RuntimeException(e);
								}
			        		}
						}
		        	}
		        	else {
						iFile file = docObj.getAttribute( objAtt ).getValueiFile();
						if( file != null ) {
							String sName = file.getName();
							
							ServletContext oCtx = (ServletContext)getFacesContext().getExternalContext().getContext();
							
				            String mimetype = oCtx.getMimeType(sName.toLowerCase());
	
				            resp.setHeader("Cache-Control","private");               
				            ServletOutputStream so = response.getOutputStream(); 
			                resp.setHeader("Content-Disposition","attachment; filename="+sName);
			                
			                resp.setHeader("XEODM-FileName", sName );
			                resp.setHeader("XEODM-ReadOnly", Boolean.toString( oFile.isDisabled() ) );
	
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
		        	}
				} catch (boRuntimeException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}
        	}
		}
        
    }


}
