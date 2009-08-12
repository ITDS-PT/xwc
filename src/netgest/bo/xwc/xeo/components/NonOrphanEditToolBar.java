package netgest.bo.xwc.xeo.components;

import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefMethod;
import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;

public class NonOrphanEditToolBar extends ToolBar {
	
	private XUIBindProperty<Boolean>  renderConfirmBtn    = 
		new XUIBindProperty<Boolean>( "renderConfirmBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderValidateBtn    = 
		new XUIBindProperty<Boolean>( "renderValidateBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderCancelBtn    = 
		new XUIBindProperty<Boolean>( "renderCancelBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderObjectMethodBtns    = 
		new XUIBindProperty<Boolean>( "renderObjectMethodBtns", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderPropertiesBtn    = 
		new XUIBindProperty<Boolean>( "renderPropertiesBtn", this, true, Boolean.class );
	
	public boolean getRenderConfirmBtn() {
		return renderConfirmBtn.getEvaluatedValue();
	}

	public void setRenderConfirmBtn( String expression ) {
		this.renderConfirmBtn.setExpressionText( expression );
	}

	public boolean getRenderCancelBtn() {
		return renderCancelBtn.getEvaluatedValue();
	}

	public void setRenderCancelBtn( String expression ) {
		this.renderCancelBtn.setExpressionText( expression );
	}

	public boolean getRenderValidateBtn() {
		return renderValidateBtn.getEvaluatedValue();
	}

	public void setRenderValidateBtn( String expression ) {
		this.renderValidateBtn.setExpressionText( expression );
	}

	public boolean getRenderObjectMethodBtns() {
		return renderObjectMethodBtns.getEvaluatedValue();
	}

	public void setRenderObjectMethodBtns( String expression ) {
		this.renderObjectMethodBtns.setExpressionText( expression );
	}

	public boolean getRenderPropertiesBtn() {
		return renderPropertiesBtn.getEvaluatedValue();
	}

	public void setRenderPropertiesBtn( String expression ) {
		this.renderPropertiesBtn.setExpressionText( expression );
	}
	
	
	
	@Override
	public String getRendererType() {
		return "toolBar";
	}
	
	public static final List<String> staticMethods = Arrays.asList(
			new String[] {"cofirmar","cancelar","valid" }
		);

	XUIBindProperty<boObject> 	targetObject = 
		new XUIBindProperty<boObject>("targetObject", this ,boObject.class, "#{viewBean.XEOObject}" );
		
	public void setTargetObject( String expressionString ) {
		this.targetObject.setExpressionText( expressionString );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	@Override
	public void initComponent() { 
		boObject xeoObject = getTargetObject();
		// Render ToolBar Methods
		if( getRenderConfirmBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createViewerBeanMethod( "Confirmar", "Confirmar","ext-xeo/images/menus/confirmar.gif", "confirm", null );
		}
		
		if( getRenderValidateBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createViewerBeanMethod( null, "Verificar se existem erros de <br/>preenchimento","ext-xeo/images/menus/confirmar.gif", "processValidate", null );
		}
		
		if( getRenderCancelBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createViewerBeanMethod( "Cancelar", "Cancelar" , "ext-xeo/images/menus/applications.gif", "cancel", null );
		}
		
		if( getRenderPropertiesBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createViewerBeanMethod( null, "Propriedades" , "extjs/resources/images/default/tree/leaf.gif", "showProperties", "self" );
			getChildren().add( Menu.getMenuSpacer() );
		}
		
		if( getRenderObjectMethodBtns() ) {
			boDefMethod[] methods = xeoObject.getToolbarMethods();
			for( boDefMethod m : methods ) {
				if( !staticMethods.contains( m.getName() ) ) {
					createMenuMethod( m.getLabel(), m.getLabel(), m.getName() );
				}
			}
		}
		super.initComponent();
	}
	
	private ViewerMethod createViewerBeanMethod( String label, String toolTip, String icon, String methodName, String target ) {
		ViewerMethod xeoMethod;
	
		xeoMethod = new ViewerMethod();
		xeoMethod.setId( getId() + "_" + methodName );
		if( target != null ) {
			xeoMethod.setTarget( target );
		}
		xeoMethod.setText( label );
		xeoMethod.setTargetMethod( methodName );
		xeoMethod.setIcon( icon );
		xeoMethod.setToolTip( toolTip );
		getChildren().add( xeoMethod );
	
		return xeoMethod;
	}
	
	@Override
	public void preRender() {
		boObject xeoObject = getTargetObject();
		for( UIComponent comp : getChildren() ) {
			if( comp instanceof ModelMethod ) {
				ModelMethod meth = (ModelMethod)comp;
				((ModelMethod)comp).setVisible(
					Boolean.toString( !XEOComponentStateLogic.isMethodHidden(xeoObject, meth.getTargetMethod()) )
				);
				meth.setDisabled( 
						Boolean.toString( XEOComponentStateLogic.isMethodDisabled(xeoObject, meth.getTargetMethod()) )
				);
			}
		}
	}
	
	private ModelMethod createMenuMethod( String label, String toolTip, String methodName ) {
		
		ModelMethod xeoMethod;
		
		xeoMethod = new ModelMethod();
		xeoMethod.setId( getId() + "_" + methodName );
		xeoMethod.setText( label );
		xeoMethod.setTargetObject( this.targetObject.getExpressionString() );
		xeoMethod.setTargetMethod( methodName );
		xeoMethod.setToolTip( toolTip );
		getChildren().add( xeoMethod );
		
		return xeoMethod;
	}
}
