package netgest.bo.xwc.components.classic;


import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLAttr.HREF;
import static netgest.bo.xwc.components.HTMLAttr.CLASS;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.el.MethodBinding;

import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIBaseProperty;
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
 * 
 * This components represents a XUICommand in the form of an HTML button
 *  
 * @author AC
 *
 */
@SuppressWarnings("deprecation")
public class ActionLink extends XUICommand
{
    
    /**
     * The label of the button
     */
    @Localize
    private XUIViewStateProperty<String>    text 	= new XUIViewStateProperty<String>( "text", this, "#Button#" );
    /**
     * The action to be invoked server-side
     */
    private XUIStateProperty<String>     	action 	= new XUIStateProperty<String>( "action", this );
    
    /**
     * The target where the action will be executed
     */
    @Values({ "blank","window","self","top","download","tab" })
    private XUIStateProperty<String> 		target 	= new XUIStateProperty<String>( "target", this , "self" );

    /**
     * Whether or not the button is disabled (can't be clicked)
     */
    private XUIViewStateBindProperty<Boolean> 	disabled = new XUIViewStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    /**
     * Whether or not the button is visible
     */
    private XUIViewStateBindProperty<Boolean> 	visible  = new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    
    /**
     * Defines the "waiting" message type while the request is processing
     * 
     * 1 Corresponds to {@link XVWScripts#WAIT_DIALOG}
     * 2 Corresponds to {@link XVWScripts#WAIT_STATUS_MESSAGE}
     * 
     */
    @Values({ "1","2" })
    private XUIViewProperty<Integer>		waitMode = new XUIViewProperty<Integer>( "waitMode" , this, XVWScripts.WAIT_DIALOG );		
    
    private XUIBaseProperty<String>			cssClass = new XUIBaseProperty<String>( "cssClass", this, "" );
    
    
    @Override
    public void initComponent() {
    	super.initComponent();
    }
    
    public ActionLink()
    {
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
     * Text of the Link
     * 
     * @param text - Label of the link
     */
    public void setText(String text) {
        this.text.setValue( text );
    }
    
    /**
     * Current text of the link
     * 
     * @return Current text of the link
     */
    public String getText() {
        return text.getValue();
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

    public String getCssClass() {
    	return cssClass.getValue();
    }
    
    public void setCssClass(String cssClass) {
    	this.cssClass.setValue( cssClass );
    }
    
    
    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            super.encodeBegin(component);
        }

        
        @Override
        public void encodeEnd(XUIComponentBase oRenderComp ) throws IOException {
            ActionLink    oActionLink = (ActionLink)oRenderComp;
            XUIResponseWriter  w = getResponseWriter();
            
            if( oActionLink.getActionExpression() != null ) {
            	Object commandArg = oActionLink.getCommandArgument();
            	String arg = "";
            	if (commandArg != null)
            		arg = commandArg.toString();
            	
            	String script="function actionLink"+oActionLink.getId()+"(){" + 
                     	XVWScripts.getCommandScript( oActionLink.getTarget() , oActionLink, arg , oActionLink.getWaitMode() ) +
                     "}";
            	
            	w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, oActionLink.getClientId(),script);
            }
        	
        	// Place holder for the element
        	w.startElement( DIV, oActionLink );
        	w.writeAttribute( ID, oActionLink.getClientId(), "id" );
        	w.startElement(HTMLTag.A);
        	w.writeAttribute( HREF, "javascript:actionLink"+oActionLink.getId()+"()");
        	if (oActionLink.getCssClass()!=null && !oActionLink.getCssClass().equals("")) {
        		w.writeAttribute( CLASS, oActionLink.getCssClass());
        	}
        	w.write(oActionLink.getText());
        	w.endElement(HTMLTag.A);
        	w.endElement( DIV);            
            
        }
    }
    
    
}
