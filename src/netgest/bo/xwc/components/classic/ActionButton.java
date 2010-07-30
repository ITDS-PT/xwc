package netgest.bo.xwc.components.classic;


import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.el.MethodBinding;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * This components represents a XUICommand in the form of a button
 * 
 * He cannot have children
 * 
 * @author jcarreira
 * @author PedroRio
 *
 */

public class ActionButton extends XUICommand
{
    
    private XUIViewStateProperty<Integer>   width 	= new XUIViewStateProperty<Integer>( "width", this, 75 );
    private XUIViewStateProperty<String>    label 	= new XUIViewStateProperty<String>( "label", this, "#Button#" );
    private XUIStateProperty<String>     	action 	= new XUIStateProperty<String>( "action", this );

    /**
     * Image to put inside the button
     */
    private XUIViewStateProperty<String>	image = new XUIViewStateProperty<String>( "image", this, null );
    
    private XUIStateProperty<String> 		target 	= new XUIStateProperty<String>( "target", this );

    private XUIViewStateBindProperty<Boolean> 	disabled = new XUIViewStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    private XUIViewStateBindProperty<Boolean> 	visible  = new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    
    private XUIViewProperty<Integer>		waitMode = new XUIViewProperty<Integer>( "waitMode" , this, XVWScripts.WAIT_DIALOG );		
    
    
    public ActionButton()
    {
        super.setValue("Button");
        setRendererType( null );
    }
    
    /**
     * Defines the message type while the request is processing
     * 
     * @param waitMode
     * 				1 - Show a wait a message while the request is bean processed
     * 				2 - Don't show a wait message. 
     */
    public void setWaitMode( int waitMode ) {
    	this.waitMode.setValue( waitMode ); 
    }
    
    /**
     * @return Return the current wait mode value
     * 		
     */
    public int getWaitMode() {
    	return this.waitMode.getValue();
    }
    
    /**
     * A {@link MethodBinding} to be invoked when the button is clicked 
     *  
     * 
     * @param sExpr  - {@link MethodBinding}
     * 		
     */
    public void setAction( String sExpr ) {
        this.action.setValue( sExpr );
        setActionExpression( createMethodBinding( sExpr ) );
    }
    
    /**
     * Return the {@link MethodBinding} expression associated with the action button
     * 
     * @return  {@link MethodBinding} expression string
     * 		
     */
    public String getActionString() {
        return action.getValue();
    }
    
    /**
     * Return if the component is to be rendered on the client
     */
    @Override
	public boolean isRendered()
    {
        return true;
    }
    
    /**
     * Defines the with of the button
     * 
     * @param width int with the width of the button, defaults to 75px
     */
    public void setWidth(int width) {
        this.width.setValue( width ); 
    }
    
    /**
     * Returns the current width of the button
     * 
     * @return int with the current width of the button
     */
    public int getWidth() {
        return width.getValue();
    }
    
    /**
     * Label of the button
     * 
     * @param label - Label of the button
     */
    public void setLabel(String label) {
        this.label.setValue( label );
    }
    
    /**
     * Current label of the button
     * 
     * @return Current label of the button
     */
    public String getLabel() {
        return label.getValue();
    }
    
    /**
     * Defines the server action as a {@link MethodBinding} to be trigged when the button is clicked
     * 
     * @param sAction	{@link MethodBinding}
     */
    public void setServerAction( String sAction ) {
        this.action.setValue( sAction );
        setActionExpression( createMethodBinding( sAction ) );
    }
    
    
    /**
     * 
     * @return {@link MethodBinding} expressin String
     */
    public String getServerAction(  ) {
        return action.getValue();
    }
    
    /**
     * Set disabled property of the component
     * 
     * @param disabled true/false or a {@link ValueExpression}
     */
    public void setDisabled(String disabled) {
        this.disabled.setExpressionText( disabled );
    }
    
    /**
     * Returns if the component is disabled
     * 
     * @return true/false 
     */
    public boolean isDisabled() {
        return disabled.getEvaluatedValue();
    }

    /**
     * Set the visibility of the component
     * 
     * @param true/false or a {@link ValueExpression} 
     */
    public void setVisible(String visible) {
        this.visible.setExpressionText( visible );
    }
    
    
    /**
     * 
     * Defines the path to an image to show inside the button
     * 
     * @param imagePath The path to the image
     */
    public void setImage(String imagePath){
    	image.setValue(imagePath);
    }
    
    /**
     * 
     * Retrieves the path to an image to display inside the button (along side with text) 
     * 
     * @return The path to the image
     * 
     */
    public String getImage(){
    	return image.getValue();
    }
    
    
    
    /**
     * Return if the component is visible
     * 
     * @return true/false
     */
    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    
    public void setTarget(String sText) {
        this.target.setValue( sText );
    }

    public String getTarget() {
        return target.getValue();
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
            String val = oConfig.renderExtConfig().toString();
            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, oActionButton.getClientId(),val);
            
        }
        
        public ExtConfig getExtJsConfig( XUIComponentBase oActionButton  ) {
            
            return renderExtComponent( oActionButton );
            
        }
        
        public ExtConfig renderExtComponent( XUIComponentBase oActionButton ) {
            ExtConfig oExtConfig;
            oExtConfig = new ExtConfig( "Ext.Button" );
            renderExtComponent( oActionButton, oExtConfig );
            return oExtConfig;
        }
        
        public ExtConfig renderExtComponent( XUIComponentBase oComponent, ExtConfig oConfig ) {
            ActionButton oActionButton;
            
            oActionButton = (ActionButton)oComponent;

            oConfig.addJSString( "id", oActionButton.getClientId() + "_b" );
            oConfig.add( "minWidth", oActionButton.getWidth() );
            
            if (oActionButton.getImage() != null){
            	oConfig.addJSString("icon", oActionButton.getImage() );
            	oConfig.addJSString( "cls","x-btn-text-icon");
            }
            
            
            
            if( oActionButton.isDisabled() )
            	oConfig.add( "disabled", true );

            if( !oActionButton.isVisible() )
            	oConfig.add( "hidden", true );
            
            if (!oActionButton.getLabel().equalsIgnoreCase("#Button#"))
            	oConfig.addJSString( "text", oActionButton.getLabel() );
            if( oActionButton.getActionExpression() != null ) {
                oConfig.add( "handler", 
                             "function(){" + 
                             	XVWScripts.getCommandScript( oActionButton.getTarget() , oActionButton, oActionButton.getWaitMode() ) +
                             "}"
                            );
            }
            return oConfig;
        }

    }
}
