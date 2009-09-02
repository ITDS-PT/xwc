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

public class EditToolBar extends ToolBar {
	
	public static final List<String> staticMethods = Arrays.asList(
			new String[] {"update","destroy","cloneObject","valid" }
		);

	public static final List<String> MapObjectMethods = Arrays.asList(
			new String[] {"update", "update","valid","cloneObject", "destroy" }
	);
	
	public static final List<String> MapViewerMethods = Arrays.asList(
			new String[] {"save","saveAndClose","processValidate","duplicate", "destroy" }
	);
	
	private XUIBindProperty<boObject> 	targetObject = 
		new XUIBindProperty<boObject>("targetObject", this ,boObject.class, "#{viewBean.XEOObject}" );

		private XUIBindProperty<Boolean>  renderUpdateBtn    = 
		new XUIBindProperty<Boolean>( "renderUpdateBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderUpdateAndCloseBtn    = 
		new XUIBindProperty<Boolean>( "renderUpdateAndCloseBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderDestroyBtn    = 
		new XUIBindProperty<Boolean>( "renderDestroyBtn", this, true, Boolean.class );
	
	private XUIBindProperty<Boolean>  renderValidateBtn    = 
		new XUIBindProperty<Boolean>( "renderValidateBtn", this, true, Boolean.class );
	
	private XUIBindProperty<Boolean>  renderCloneBtn    = 
		new XUIBindProperty<Boolean>( "renderCloneBtn", this, false, Boolean.class );

	private XUIBindProperty<Boolean>  renderObjectMethodBtns    = 
		new XUIBindProperty<Boolean>( "renderObjectMethodBtns", this, true, Boolean.class );
	
	private XUIBindProperty<Boolean>  renderPropertiesBtn    = 
		new XUIBindProperty<Boolean>( "renderPropertiesBtn", this, true, Boolean.class );


		
		
	@Override
	public String getRendererType() {
		return "toolBar";
	}
	
	public boolean getRenderUpdateBtn() {
		return renderUpdateBtn.getEvaluatedValue();
	}

	public void setRenderUpdateBtn( String expression ) {
		this.renderUpdateBtn.setExpressionText( expression );
	}

	public boolean getRenderUpdateAndCloseBtn() {
		return renderUpdateAndCloseBtn.getEvaluatedValue();
	}

	public void setRenderUpdateAndCloseBtn( String expression ) {
		this.renderUpdateAndCloseBtn.setExpressionText( expression );
	}
	
	public boolean getRenderDestroyBtn() {
		return renderDestroyBtn.getEvaluatedValue();
	}

	public void setRenderDestroyBtn( String expression ) {
		this.renderDestroyBtn.setExpressionText( expression );
	}

	public boolean getRenderValidateBtn() {
		return renderValidateBtn.getEvaluatedValue();
	}

	public void setRenderValidateBtn( String expression ) {
		this.renderValidateBtn.setExpressionText( expression );
	}

	public boolean getRenderCloneBtn() {
		return renderCloneBtn.getEvaluatedValue();
	}

	public void setRenderCloneBtn( String expression ) {
		this.renderCloneBtn.setExpressionText( expression );
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
		
		int pos = 0;

		if( getRenderUpdateBtn() ) {
			createViewerBeanMethod( pos++,"Guardar", "Guardar" , "ext-xeo/images/menus/gravar.gif", "save", null );
		}
		
		if( getRenderUpdateAndCloseBtn() ) {
			getChildren().add( pos++, Menu.getMenuSpacer() );
			createViewerBeanMethod( pos++, null, "Guardar e fechar","ext-xeo/images/menus/gravar_e_sair.gif", "saveAndClose", null );
		}

		if( getRenderDestroyBtn() ) {
			getChildren().add( pos++, Menu.getMenuSpacer() );
			createViewerBeanMethod( pos++, null, "Remover","ext-xeo/images/menus/remover.gif", "destroy", null );
		}
		
		if( getRenderValidateBtn() ) {
			getChildren().add( pos++,Menu.getMenuSpacer() );
			createViewerBeanMethod( pos++,null, "Verificar se existem erros de <br/>preenchimento","ext-xeo/images/menus/confirmar.gif", "processValidate", null );
		}

		if( getRenderCloneBtn() ) {
			getChildren().add( pos++,Menu.getMenuSpacer() );
			createViewerBeanMethod( pos++,"Duplicar", "Duplicar com os dados actuais" , "ext-xeo/images/menus/applications.gif", "duplicate", "tab" );
		}

		if( getRenderPropertiesBtn() ) {
			getChildren().add( pos++,Menu.getMenuSpacer() );
			createViewerBeanMethod( pos++,null, "Propriedades" , "extjs/resources/images/default/tree/leaf.gif", "showProperties", "self" );
		}
		if( getRenderObjectMethodBtns() ) {
			getChildren().add( pos++,Menu.getMenuSpacer() );
			boDefMethod[] methods = xeoObject.getToolbarMethods();
			for( boDefMethod m : methods ) {
				if( !staticMethods.contains( m.getName() ) ) {
					createMenuMethod( pos++, m.getLabel(), m.getLabel(), m.getName() );
				}
			}
		}
		super.initComponent();
	}
	
	private ViewerMethod createViewerBeanMethod( int pos, String label, String toolTip, String icon, String methodName, String target ) {
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
		getChildren().add( pos, xeoMethod );

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
			if( comp instanceof ViewerMethod ) {
				ViewerMethod meth = (ViewerMethod)comp;
				int idx = MapViewerMethods.indexOf( meth.getTargetMethod() );
				if( idx > -1 ) {
					meth.setVisible(
							Boolean.toString(
									!XEOComponentStateLogic.isMethodHidden( xeoObject, MapObjectMethods.get( idx ))
							)
					);
					meth.setDisabled(
							Boolean.toString(
									XEOComponentStateLogic.isMethodDisabled(xeoObject, MapObjectMethods.get( idx ))
							)
					);
				}
			}
		}
	}
	
	private ModelMethod createMenuMethod( int pos, String label, String toolTip, String methodName ) {
		
		ModelMethod xeoMethod;
		xeoMethod = new ModelMethod();
		xeoMethod.setId( getId() + "_" + methodName );
		xeoMethod.setText( label );
		xeoMethod.setTargetObject( this.targetObject.getExpressionString() );
		xeoMethod.setTargetMethod( methodName );
		xeoMethod.setToolTip( toolTip );
		getChildren().add( pos, xeoMethod );
		
		return xeoMethod;
	}
	
}

