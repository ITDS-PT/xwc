package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeFile;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

public class AttributeObjectLookup extends AttributeBase {

    private XUICommand oLookupCommand;
    private XUICommand oOpenCommand;

    public void initComponent() {
        // per component inicializations.
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
            XEOEditBean oXEOBaseBean;
            oXEOBaseBean = (XEOEditBean)getRequestContext().getViewRoot().getBean("viewBean");
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
            ((AttributeObjectLookup)((XUICommand)event.getSource()).getParent()).doLookup();
        }
    }
    
    public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
        	AttributeObjectLookup  oAttr;
            
            XUIResponseWriter w = getResponseWriter();
            oAttr = (AttributeObjectLookup)oComp; 
            
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
        	AttributeObjectLookup  oAttr;
            String              sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 250 );

            oAttr = (AttributeObjectLookup)oComp; 

            if ( !oAttr.getEffectivePermission(SecurityPermissions.READ) ) {
            	return "";
            }
            
            //'javax.faces.ViewState'
            String sViewState = getRequestContext().getViewRoot().getViewState();
            //xvw.servlet

            HttpServletRequest req = (HttpServletRequest)getRequestContext().getFacesContext().getExternalContext().getRequest();
            
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
            
            
            String link = 
            	(req.isSecure()?"https":"http") + "://" + 
            	req.getServerName() +
            	(req.getServerPort()==80?"":":"+req.getServerPort()) + sActionUrl;
            
            
            
            sJsValue = String.valueOf( oAttr.getDisplayValue() ); 
            
            ExtConfig oDs = new ExtConfig("Ext.data.Store");
            
            
            //ExtConfig oProxy = oDs.addChild("proxy");
            //oProxy.setComponentType( "Ext.data.ScriptTagProxy" );
            oDs.addJSString( "url", link );
            
            oDs.add("reader", "new Ext.data.JsonReader(" +
            		"{root: 'results',totalProperty: 'totalCount',id: 'BOUI'}," +
            		"[{name:'BOUI',mapping:'BOUI'},{name:'SYS_CARDID', mapping:'SYS_CARDID'}])"
            );
            /*
            oReader.setComponentType( "Ext.data.JsonReader" );
            oReader.addJSString( "root","results" );
            oReader.addJSString( "totalProperty","totalCount" );
            oReader.addJSString( "id","BOUI" );
            ExtConfigArray array = oReader.addChildArray( null );
            ExtConfig field1 = array.addChild();
            field1.addJSString("name","BOUI" );
            field1.addJSString("mapping","BOUI" );
            ExtConfig field2 = array.addChild();
            field2.addJSString("name","SYS_CARDID" );
            field2.addJSString("mapping","SYS_CARDID" );
            
            */
            
            ExtConfig oInpConfig = new ExtConfig("Ext.form.ComboBox");
            oInpConfig.addJSString( "id" , oAttr.getClientId() );
            oInpConfig.addJSString( "name" , oAttr.getClientId() );
            oInpConfig.addJSString( "value" , sJsValue );
            oInpConfig.add( "store" , oDs );
            oInpConfig.addJSString( "displayField" , "SYS_CARDID" );
            oInpConfig.add( "typeAhead" , "false" );
            oInpConfig.addJSString("loadingText","Loading...");
            oInpConfig.add("width", oAttr.getWidth() );
            oInpConfig.addJSString("renderTo", oComp.getClientId());
            oInpConfig.add("pageSize", "30");
            oInpConfig.add("tpl", 
            		"new Ext.XTemplate('<tpl for=\".\">" +
            		"	<div class=\"search-item\">" +
            		"		<span style=\"display:none\">{BOUI}</span>" +
            		"		<span>{SYS_CARDID}</span>" +
            		"	</div>" +
            		"</tpl>')");
            oInpConfig.addJSString("itemSelector", "div.search-item");
            
            return oInpConfig.renderExtConfig().toString();
        }

        @SuppressWarnings("unchecked")
		public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	boObjectList l = boObjectList.list( boApplication.currentContext().getEboContext(), "select Ebo_Package where 1=1" );
        	XEOObjectListConnector lc = new XEOObjectListConnector( l );
        	
        	
            //String cb = request.getParameter("callback");
        	
        	ExtConfig result = new ExtConfig();
        	result.add("totalCount", lc.getRecordCount() );
        	ExtConfigArray r = result.addChildArray("results");
        	
        	
        	DataListIterator lcit = lc.iterator();
        	
        	while( lcit.hasNext() ) {
        		DataRecordConnector lcr = lcit.next();
        		
        		ExtConfig s = r.addChild();
        		s.add("BOUI", lcr.getAttribute("BOUI").getValue()  );
        		s.addJSString("SYS_CARDID",
        				JavaScriptUtils.safeJavaScriptWrite( 
        				String.valueOf( lcr.getAttribute("name").getValue() )
        				,'\'')
        			);
        	}
        	
        	resp.setContentType("text/javascript");
        	
        	PrintWriter pw = resp.getWriter();
        	//pw.print( cb );
        	//pw.print( '(' );
        	pw.print( result.renderExtConfig() );
        	//pw.print( ");" );
        	
        }
    }
}
