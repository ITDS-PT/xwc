package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class AttributeLabel extends ViewerOutputSecurityBase {

    public XUIStateProperty<String> text 		= new XUIStateProperty<String>("text", this, " Texto da Label ");
    
    private XUIStateProperty<ValueExpression> 	disabled       = new XUIStateProperty<ValueExpression>( "disabled", this );
    private XUIStateProperty<ValueExpression> 	visible        = new XUIStateProperty<ValueExpression>( "visible", this );
    private XUIStateBindProperty<Boolean> 		modelRequired  = new XUIStateBindProperty<Boolean>( "modelRequired", this, Boolean.class );
    private XUIStateBindProperty<Boolean> 		recommended    = new XUIStateBindProperty<Boolean>( "recommended", this, Boolean.class );
    
    public void setText( String sText ) {
        this.text.setValue( sText );
    }
    
	public String getText( ) {
        return this.text.getValue();
    }

    public void setVisible( String visible) {
        this.visible.setValue( createValueExpression( visible, Boolean.class ) );
    }

    public boolean isVisible() {
        if( visible.getValue() != null && visible.getValue().isLiteralText() ) {
            return Boolean.parseBoolean( visible.getValue().getExpressionString() );
        }
        else if ( visible.getValue() != null ) {
             return (Boolean)visible.getValue().getValue( getELContext() );
        }
        return true;
    }

    public void setDisabled(String sDisable) {
        this.disabled.setValue( createValueExpression( sDisable, Boolean.class ) );
    }


    public boolean isDisabled() {
        if( disabled.getValue() != null && disabled.getValue().isLiteralText() ) {
            return Boolean.parseBoolean( disabled.getValue().getExpressionString() );
        }
        else if ( disabled.getValue() != null ) {
             return (Boolean)disabled.getValue().getValue( getELContext() );
        }
        return false;
    }

	public boolean isModelRequired() {
		if( modelRequired == null ) {
			boolean debug=true;
			debug = false;
		}
		Boolean ret = modelRequired.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setModelRequired(String requiredExpression) {
		this.modelRequired.setExpressionText( requiredExpression );
	}

	public boolean isRecommended() {
		if( recommended == null ) {
			boolean debug=true;
			debug = false;
		}
		Boolean ret = recommended.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setRecommended(String recommendedExpression ) {
		this.recommended.setExpressionText( recommendedExpression );
	}

	public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            
            XUIResponseWriter w = getResponseWriter();

            // Place holder for the component
            w.startElement( DIV, oComp ); 
            
            w.writeAttribute( "class" , "xwc-form-label", null);
            w.writeAttribute( ID, oComp.getClientId(), null );
            
            w.endElement( DIV );
            
            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( (AttributeLabel)oComp )
            );

            
        }

        public String renderExtJs( AttributeLabel oComp ) {
            
            StringBuilder sOut = new StringBuilder( 150 );
            
            AttributeLabel oAttrLabel = (AttributeLabel)oComp;

            if ( !oAttrLabel.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }
            
            ExtConfig oLabelConfig = new ExtConfig("Ext.form.Label");
            oLabelConfig.addJSString( "renderTo" , oComp.getClientId() );
            
            if( oComp.getParent() instanceof Attribute )
            	oLabelConfig.addJSString( "forId" , ((Attribute)oComp.getParent()).getInputComponent().getClientId() + "_c" );
            
            oLabelConfig.addJSString("text", oComp.getText() );
            if( !oAttrLabel.isVisible() )
            	oLabelConfig.add("hidden",true);
            
            if( oAttrLabel.isDisabled() || !oAttrLabel.getEffectivePermission(SecurityPermissions.WRITE) )  {
            	oLabelConfig.add("disabled",true);
            }
            
            StringBuilder cls = new StringBuilder("xwc-form-label ");
        	
            if( oAttrLabel.isRecommended() )
            	cls.append( "xwc-form-recommended " );

            if( oAttrLabel.isModelRequired() )
            	cls.append( "xwc-form-required " );
            
        	oLabelConfig.addJSString("cls", cls.toString() );

        	oLabelConfig.renderExtConfig(sOut);

            //sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
//            sOut.write( "var " + oComp.getId() + " = new Ext.form.Label({"); sOut.write("\n");
//            sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("',");
//
//            if( !oAttrLabel.isVisible() )
//                sOut.write( "hidden: true,");
//            if( oAttrLabel.isDisabled() )
//                sOut.write( "disabled: true,");
//
//            sOut.write( "text: '"); sOut.write( oComp.getText() ); sOut.write("'");
//            sOut.write("});");
            //sOut.write("});");
            
            return sOut.toString();
        }

    }

}
