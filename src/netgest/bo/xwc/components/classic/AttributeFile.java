package netgest.bo.xwc.components.classic;

import java.io.File;
import java.io.FileOutputStream;
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

import netgest.bo.impl.document.Ebo_DocumentImpl;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.xeodm.XEODMBuilder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;
import netgest.bo.xwc.xeo.beans.FileBrowseBean;
import netgest.io.FSiFile;
import netgest.io.iFile;
/**
 * This atribute works with AttributeBinnaryData from XEO Model
 * It allow download and upload of files
 * @author jcarreira
 *
 */
public class AttributeFile extends AttributeBase {

    private XUICommand oLookupCommand;

    @Override
	public void preRender() {
    	
    	super.preRender();
    	
        // component initializations.
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

	private void doLookup() {
    	
    	XUISessionContext oSessionContext = getRequestContext().getSessionContext();
    	
    	XUIViewRoot oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/components/viewers/FileBrowse.xvw" );
    	getRequestContext().setViewRoot( oViewRoot );
    	
    	FileBrowseBean oFileBrowseBean = (FileBrowseBean)oViewRoot.getBean( "viewBean" );
    	
    	oFileBrowseBean.setParentComponentId( getClientId() );
    	oFileBrowseBean.setParentBeanId("viewBean" );
    	
    	getRequestContext().renderResponse();
    	
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
            ((AttributeFile)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    public static class XEOHTMLRenderer extends ExtJsFieldRendeder implements XUIRendererServlet {

    	
    	@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {
    		
    		if( oComp.getStateProperty("readOnly").wasChanged() ) {
    			oComp.setDestroyOnClient( true );
    			oComp.setRenderedOnClient( false );
    		}
    		
			super.encodeEnd(oComp);
		}

		@Override
    	public String getExtComponentType( XUIComponentBase oComp ) {
    		
    		AttributeBase oAtt = (AttributeBase)oComp;
    		
            if( !oAtt.isReadOnly() ) 
            	return "Ext.form.TwinTriggerField";
            else
            	return "Ext.form.TextField";
    	}
    	
    	@Override
    	public void encodeBeginPlaceHolder(XUIComponentBase oAtt)
    			throws IOException {
    		super.encodeBeginPlaceHolder(oAtt);

    		XUIResponseWriter w = getResponseWriter();
            w.startElement( HTMLTag.INPUT, oAtt);
            w.writeAttribute(HTMLAttr.TYPE, "hidden", null);
            w.writeAttribute(HTMLAttr.VALUE, "" , null);
            w.writeAttribute(HTMLAttr.NAME, oAtt.getClientId() + "_ci", null);
            w.writeAttribute(HTMLAttr.ID, oAtt.getClientId() + "_ci", null);
            
            w.endElement(HTMLTag.INPUT);
    		
    	}
    	
    	
    	@Override
    	public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
    		AttributeFile  		oAttFile;
            String              sFormId;
            Form                oForm;
            
            sFormId = oAttr.getNamingContainerId();
            oForm   = (Form)oAttr.findComponent( sFormId );
            
            oAttFile = (AttributeFile)oAttr;
    		
    		ExtConfig oInpConfig = super.getExtJsFieldConfig(oAttr);
    		
            oInpConfig.addJSString("trigger1Class", "x-form-clear-trigger");
            oInpConfig.addJSString("trigger2Class", "x-form-search-trigger");
            oInpConfig.add("hideTrigger1", false);
            oInpConfig.addJSString("cls", "xwc-att-file" );
            oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            
            oInpConfig.add("maxLength", "500" );
            
            Object value = oAttFile.getValue(); 
            if( value != null ) {
            	oInpConfig.addJSString("value", JavaScriptUtils.writeValue( value ) );
            }
            oInpConfig.add("readOnly", true );
            
	            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
		            oInpConfig.add("onTrigger1Click", "function(){ if(!this.disabled){ " +
		            		"Ext.ComponentMgr.get('" + getExtComponentId(oAttr) + "_c').setValue('');\n" + 
		            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
		            		XVWScripts.getAjaxCommandScript(oAttr,  XVWScripts.WAIT_STATUS_MESSAGE ) + 
		            		"}}"
		            );
	            }
	            else {
		            oInpConfig.add("onTrigger1Click", "function(){ if(!this.disabled){" +
		            		"Ext.ComponentMgr.get('" + getExtComponentId(oAttr) + "').setValue('');\n" + 
		            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
		            		"}}"
		            );
	            }
	            oInpConfig.add("onTrigger2Click", "function(){if(!this.disabled){ " +
	            		XVWScripts.getOpenCommandWindow( oAttFile.getLookupCommand(), 
	            				oAttFile.getLookupCommand() + "_" + System.currentTimeMillis() ) +
	            		"}}"
	            );
            
            
            return oInpConfig;
    	}
    	
    	@Override
    	public ExtConfig getExtJsFieldListeners(AttributeBase oAtt) {
    		
    		ExtConfig oInpLsnr = super.getExtJsFieldListeners(oAtt);
    		
            String sViewState = getRequestContext().getViewRoot().getViewState();
            String sServletId = oAtt.getClientId();
            
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
            dmb.put( "httpp","javax.faces.ViewState=" + sViewState );
            dmb.put( "httpp", "xvw.servlet=" + sServletId );
            dmb.put( "filename", String.valueOf( oAtt.getDisplayValue() ) );
            String link = 
            	(req.isSecure()?"https":"http") + "://" + 
            	req.getServerName() +
            	(req.getServerPort()==80?"":":"+req.getServerPort()) + sActionUrl;
	            
            dmb.put("link", link );
            dmb.put("action", "open");
            oInpLsnr.add("'render'", "function(){ " +
            		"this.getEl().dom.onclick=function(){" +
            		"	if( event.srcElement.value.length > 0 ) {" +
            		"		if( window.top.xeodmstate ) \n" +
            		"			XVW.downloadFile('" + dmb.toUrlString() + "');\n" +
            		"		else \n" +
            		"			XVW.downloadFile('" + sActionUrl + "');" +
					"	}" +
            		"};" +
            		"}" );

            return oInpLsnr;          
    	}
    	
        @Override
        public void decode(XUIComponentBase component) {

        	AttributeFile oAttrComp;
            oAttrComp = (AttributeFile)component;
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() + "_ci" );
            if( "NaN".equals( value ) ) {
            	oAttrComp.setSubmittedValue( "" );
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
	        	if( "POST".equals( hRequest.getMethod() ) ) {
					if( oFile.isDisabled() ) {
						
						resp.sendError(403,ComponentMessages.FILE_NOT_EDITABLE.toString());
						
					} else {
		        		
		        		if( hRequest instanceof XUIMultiPartRequestWrapper )
		        		{
			        		XUIMultiPartRequestWrapper mRequest = (XUIMultiPartRequestWrapper)hRequest;
							Enumeration<Object> enumFiles = mRequest.getFileNames();
			        		if( enumFiles.hasMoreElements() ) {
			        			String fname = (String)enumFiles.nextElement();
			        			File file = mRequest.getFile( fname );
			        			try {
									oXeoConnector.getAttributeHandler().setValueiFile( new FSiFile( null, file, null ) );
								} catch (boRuntimeException e) {
									throw new RuntimeException(e);
								}
			        		}
		        		}
		        		else {
		        			String tempDir = Ebo_DocumentImpl.getTempDir();
		        			String name    = hRequest.getHeader("XEODM-FileName");
		        			File tempFile = new File( tempDir + name );
		        			FileOutputStream fout = new FileOutputStream( tempFile );
		        			InputStream is = hRequest.getInputStream();
		        			byte[] buffer = new byte[ 8192 ];
		        			int br = 0;
		        			while( (br=is.read( buffer )) > 0 ) {
		        				fout.write( buffer, 0, br );
		        			}
		        			fout.close();
		        			is.close();
		        			try {
			        			oXeoConnector.getAttributeHandler().setValueiFile( new FSiFile( null,tempFile, null  ) );
							} catch (boRuntimeException e) {
								throw new RuntimeException(e);
							}
		        		}
					}
					
	        	}
	        	else {
	        		try {
						iFile file = oXeoConnector.getAttributeHandler().getValueiFile();
						if( file != null ) {
							String sName = file.getName();
							
							ServletContext oCtx = (ServletContext)getFacesContext().getExternalContext().getContext();
							
				            String mimetype = oCtx.getMimeType(sName.toLowerCase());
	
				            resp.setHeader("Cache-Control","private");               
				            ServletOutputStream so = response.getOutputStream(); 
			                resp.setHeader("Content-Disposition","attachment; filename="+sName);
			                resp.setHeader("XEODM-FileName",sName);
			                resp.setHeader("XEODM-ReadOnly", Boolean.toString( oXeoConnector.getAttributeHandler().disableWhen() ) );
	
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
