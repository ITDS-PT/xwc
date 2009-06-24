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

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.beans.XEOBaseBean;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.xeodm.XEODMBuilder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;
import netgest.io.FSiFile;
import netgest.io.iFile;

public class AttributeNumberLookup extends AttributeBase {

    private XUICommand oLookupCommand;
    private XUICommand oOpenCommand;

    public void initComponent() {
        // per component inicializations.
        if( getChildCount() == 0 ) {
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
        else {
            oLookupCommand = (XUICommand)getChild( 0 );
            oOpenCommand = (XUICommand)getChild( 1 );
        }

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
                                                                        oSubmitedValue + " n�o est� no formato correcto "
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
    
    public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            AttributeNumberLookup  oAttr;
            
            XUIResponseWriter w = getResponseWriter();
            oAttr = (AttributeNumberLookup)oComp; 
            
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
            AttributeNumberLookup  oAttr;
            String              sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 250 );

            oAttr = (AttributeNumberLookup)oComp; 

            if ( !oAttr.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }
            
            boolean enableCardIdLink = oAttr.getEnableCardIdLink();
            
            //sJsValue = String.valueOf( oAttr.getValue() ); 
            sJsValue = String.valueOf( oAttr.getDisplayValue() ); 
            
            ExtConfig oInpConfig = new ExtConfig("Ext.form.TwinTriggerField");
            
            oInpConfig.addJSString("renderTo", oComp.getClientId());
            
            
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
            oInpConfig.add("hideTrigger1", false);
            oInpConfig.addJSString("name", oAttr.getClientId() );
            oInpConfig.addJSString("id", oAttr.getClientId() + "_c" );
            
            if( enableCardIdLink ) {
            	oInpConfig.addJSString("ctCls", "xeoObjectLink" );
            }
           
            oInpConfig.addJSString("value", sJsValue );
            oInpConfig.add("readOnly", true );
            
            //if( oAttr.isDisabled() || (permissions&SecurityPermissions.WRITE)==0 ) {
	        //    oInpConfig.add("disabled", true);
            //}
            
            if( !oAttr.isDisabled() && !oAttr.isReadOnly() ) {
	            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit()  ) {
		            oInpConfig.add("onTrigger1Click", "function(){ " +
//		            		"Ext.ComponentMgr.get('" + oAttr.getClientId() + "_c').setValue('');\n" + 
//		            		"document.getElementById('" + oAttr.getClientId() + "_ci').value='NaN';\n" + 
		            		"XVW.AjaxCommand( '" + oAttr.getNamingContainerId() +  "','" + oAttr.getId() + "_clear','true')" +
//		            		XVWScripts.getAjaxCommandScript(,"NaN"  ,XVWScripts.WAIT_STATUS_MESSAGE ) + 
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
	            		XVWScripts.getAjaxCommandScript( oAttr.getLookupCommand(),XVWScripts.WAIT_STATUS_MESSAGE ) +
	            		"}"
	            );
	            
            }
 
            oInpConfig.add("width", oAttr.getWidth() );
            oInpConfig.addJSString("renderTo", oComp.getClientId());

            ExtConfig oInpLsnr;
            oInpLsnr = oInpConfig.addChild("listeners");
            oInpLsnr.add("'change'", "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                            + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
                         );
            
            if( enableCardIdLink ) {
	            oInpLsnr.add("'render'", "function(){ " +
	            		"this.getEl().dom.onclick=function(event){" +
	            			XVWScripts.getAjaxCommandScript( oAttr.oOpenCommand ,  XVWScripts.WAIT_STATUS_MESSAGE ) +
	            		"};" +
	            		"}" );
            }
            oInpConfig.renderExtConfig( sOut );     
            
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
            
            dmb.put("link", "http://jpnbook.itds.pt:8888" + sActionUrl );
            dmb.put("action", "open");
            oInpLsnr.add("'render'", "function(){ " +
            		"this.getEl().dom.onclick=function(event){" +
//            		"	if( event.srcElement.value.length > 0 ) {" +
//            		"		XVW.downloadFile('" + dmb.toUrlString() + "');" +
            		"		document.location.href='" + dmb.toUrlString() + "';" +
//					"	}" +
            		"};" +
            		"}" );
            
            System.out.println( dmb.toUrlString() );
            
            return sOut.toString();
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
        	
        	//TODO: Criar no AttributeConnector suporte para ficheiros
        	HttpServletRequest hRequest = (HttpServletRequest)request;
        	
        	if( oConnector instanceof XEOObjectAttributeConnector ) {
        		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
	        	if( "POST".equals( hRequest.getMethod() ) && hRequest instanceof XUIMultiPartRequestWrapper ) {
	        		XUIMultiPartRequestWrapper mRequest = (XUIMultiPartRequestWrapper)hRequest;
	        		@SuppressWarnings("unused")
					Enumeration enumFiles = mRequest.getFileNames();
	        		if( enumFiles.hasMoreElements() ) {
	        			String fname = (String)enumFiles.nextElement();
	        			File file = mRequest.getFile( fname );
	        			try {
	        				boObject bobj = oXeoConnector.getAttributeHandler().getObject();
	        				bobj.getAttribute("file").setValueiFile( new FSiFile( null, file, null ) );
						} catch (boRuntimeException e) {
							// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
						throw new RuntimeException(e);
					}
	        	}
        	}
		}
        
    }


}
