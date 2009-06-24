package netgest.bo.xwc.components.classic;


import java.io.IOException;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import static netgest.bo.xwc.components.HTMLAttr.*;
import static netgest.bo.xwc.components.HTMLTag.*;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.XUIStateProperty;



public class ActionButton extends XUICommand
{
    
    private XUIStateProperty<String>    width = new XUIStateProperty<String>( "width", this, "75" );
    private XUIStateProperty<String>     label = new XUIStateProperty<String>( "label", this, "#Button#" );
    private XUIStateProperty<String>     action = new XUIStateProperty<String>( "action", this );

    private XUIStateBindProperty<Boolean> disabled = new XUIStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    private XUIStateBindProperty<Boolean> visible  = new XUIStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    
    public void setAction( String sExpr ) {
        this.action.setValue( sExpr );
        setActionExpression( createMethodBinding( sExpr ) );
    }
    
    public String getActionString() {
        return action.getValue();
    }
    
    public ActionButton()
    {
        super.setValue("Hello");
        setRendererType( null );
    }
    
    public boolean isRendered()
    {
        return true;
    }

    public void setWidth(String width) {
        this.width.setValue( width ); 
    }

    public String getWidth() {
        return width.getValue();
    }

    public void setLabel(String label) {
        this.label.setValue( label );
    }

    public String getLabel() {
        return label.getValue();
    }

    public void setServerAction( String sAction ) {
        this.action.setValue( sAction );
        setActionExpression( createMethodBinding( sAction ) );
    }

    public String getServerAction(  ) {
        return action.getValue();
    }
    
    public void setDisabled(String disabled) {
        this.disabled.setExpressionText( disabled );
    }

    public boolean isDisabled() {
        return disabled.getEvaluatedValue();
    }

    public void setVisible(String visible) {
        this.visible.setExpressionText( visible );
    }

    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    

    public static class XEOHTMLRenderer extends XUIRenderer implements ExtJsRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            super.encodeBegin(component);
        }

        @Override
        public void encodeEnd(XUIComponentBase oRenderComp ) throws IOException {
            ActionButton    oActionButton = (ActionButton)oRenderComp;
            XUIResponseWriter  w = getResponseWriter();
            
            // Place holder for the element
            w.startElement( DIV, oActionButton );
            w.writeAttribute( ID, oActionButton.getClientId(), "id" );
            w.endElement( DIV );

            ExtConfig oConfig = renderExtComponent( oActionButton );
            oConfig.addJSString( "renderTo", oActionButton.getClientId() );
            oConfig.renderExtConfig();
            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, oActionButton.getClientId(), oConfig.renderExtConfig().toString() );
            
        }
        
        public ExtConfig extEncodeAll( XUIComponentBase oActionButton  ) {
            
            return renderExtComponent( oActionButton );
            
        }
        
        public ExtConfig renderExtComponent( XUIComponentBase oActionButton ) {
            ExtConfig oExtConfig;
            oExtConfig = new ExtConfig( "Ext.Button" );
            renderExtComponent( (ActionButton)oActionButton, oExtConfig );
            return oExtConfig;
        }
        
        public ExtConfig renderExtComponent( XUIComponentBase oComponent, ExtConfig oConfig ) {
            ActionButton oActionButton;
            
            oActionButton = (ActionButton)oComponent;

            oConfig.addJSString( "id", oActionButton.getClientId() + "_b" );
            oConfig.add( "minWidth", oActionButton.getWidth() );
            
            if( oActionButton.isDisabled() )
            	oConfig.add( "disabled", true );

            if( !oActionButton.isVisible() )
            	oConfig.add( "hidden", true );
            
            oConfig.addJSString( "text", oActionButton.getLabel() );
            if( oActionButton.getActionExpression() != null ) {
                oConfig.add( "handler", 
                             "function(){" + 
                             XVWScripts.getAjaxCommandScript( oActionButton, XVWScripts.WAIT_DIALOG ) +
                             "}"
                            );
            }
            return oConfig;
        }
    }
}
