package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsBaseRenderer;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
/**
 * This component renders a label for a input component
 * @author jcarreira
 *
 */
public class AttributeLabel extends ViewerOutputSecurityBase {

    public XUIStateProperty<String> text 		= new XUIStateProperty<String>("text", this, " Label Text ");
    
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

	public boolean isModelRequired() {
		Boolean ret = modelRequired.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setModelRequired(String requiredExpression) {
		this.modelRequired.setExpressionText( requiredExpression );
	}

	public boolean isRecommended() {
		Boolean ret = recommended.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setRecommended(String recommendedExpression ) {
		this.recommended.setExpressionText( recommendedExpression );
	}
	
	@Override
	public boolean wasStateChanged() {
		return super.wasStateChanged();
	}

	public static class XEOHTMLRenderer extends ExtJsBaseRenderer {

		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.Label";
		};
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oAtt) throws IOException {
			super.encodeBeginPlaceHolder(oAtt);
			XUIResponseWriter w = getResponseWriter();
            w.writeAttribute( "class" , "xwc-form-label", null);
		}
		
		@Override
		public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
			
            AttributeLabel oAttrLabel = (AttributeLabel)oComp;

            ExtConfig config = super.getExtJsConfig(oComp);
            
            //It's not working... in extjs 2.2.1
//			config.addString( "forId" , ((Attribute)oComp.getParent()).getInputComponent().getClientId() );
            
			config.addString( "text" , JavaScriptUtils.writeValue( oAttrLabel.getText() ) );
			
            if( !oAttrLabel.isVisible() )
            	config.add("hidden",true);
            
            config.addString("cls", getComponentClass(oAttrLabel) );
            
			return config;
		}
		
		public StringBuilder getComponentClass( AttributeLabel oLabel ) {
            StringBuilder cls = new StringBuilder("xwc-form-label ");

            if( oLabel.isRecommended() )
            	cls.append( "xwc-form-recommended " );

            if( oLabel.isModelRequired() )
            	cls.append( "xwc-form-required " );
            
            return cls;
		}
		
		@Override
		public ScriptBuilder getEndComponentScript(XUIComponentBase oComp) {
			
			ScriptBuilder s = null;
			
			if( oComp.isRenderedOnClient() ) {
				AttributeLabel oAttrLabel = (AttributeLabel)oComp;
	
				s = new ScriptBuilder();
				s.startBlock();
				super.writeExtContextVar(s, oComp);
			
				if( oComp.getStateProperty("text").wasChanged() )
					s.w( "c.setText('" ).writeValue( oAttrLabel.getText() ).l("');");

				if( oComp.getStateProperty("visible").wasChanged() )
					s.w( "c.setVisible(" ).writeValue( oAttrLabel.isVisible() ).l(");");
					
				if( oComp.getStateProperty("recommended").wasChanged() || oComp.getStateProperty("modelRequired").wasChanged() ) {
					s.s( "c.removeClass('xwc-form-recommended')");
					s.s( "c.removeClass('xwc-form-required')");
					s.w( "c.addClass('").w( getComponentClass(oAttrLabel) ).s("')");
				}
				s.endBlock();
			}
			return s;
			
		}
		
		
    }

}
