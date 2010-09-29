package netgest.bo.xwc.xeo.components;

import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefMethod;
import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;
import netgest.bo.xwc.xeo.localization.XEOViewersMessages;

/**
 * 
 * Default {@link ToolBar} for a {@link FormEdit} component.
 * Includes buttons with common operations such as save, remove, create new,
 * export to PDF/HTML/Excel, show versions, edit security policies
 * 
 * @author jcarreira
 *
 */
public class EditToolBar extends ToolBar {
	
	public static final List<String> staticMethods = Arrays.asList(
			new String[] {"update","destroy","cloneObject","valid" }
		);

	public static final List<String> MapObjectMethods = Arrays.asList(
			new String[] {"update", "update","saveAndCreateNew","valid","cloneObject", "destroy" }
	);
	
	public static final List<String> MapViewerMethods = Arrays.asList(
			new String[] {"save","saveAndClose","saveAndCreateNew","processValidate","duplicate", "remove" }
	);

	public static final List<String> staticNonOrphanMethods = Arrays.asList(
			new String[] {"cofirmar","cancelar","valid", "update", "destroy", "cloneObject" }
		);

	/**
	 * Whether or not the "Confirm" button is rendered
	 * Only works if the parent {@link FormEdit} is working 
	 * with a orphan object
	 * 
	 */
	private XUIViewBindProperty<Boolean>  renderConfirmBtn    = 
		new XUIViewBindProperty<Boolean>( "renderConfirmBtn", this, true, Boolean.class );

	/**
	 * Whether or not the "Cancel button" is rendered
	 * Only works if the parent {@link FormEdit} is working 
	 * with a orphan object
	 */
	private XUIViewBindProperty<Boolean>  renderCancelBtn    = 
		new XUIViewBindProperty<Boolean>( "renderCancelBtn", this, true, Boolean.class );
	
	/**
	 * The target {@link boObject} for the buttons in the toolbar
	 * inherited from the parent {@link FormEdit}
	 */
	private XUIViewBindProperty<boObject> 	targetObject = 
		new XUIViewBindProperty<boObject>("targetObject", this ,boObject.class, "#{viewBean.XEOObject}" );

	/**
	 * Whether or not he "save" button is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderUpdateBtn    = 
		new XUIViewBindProperty<Boolean>( "renderUpdateBtn", this, true, Boolean.class );

	/**
	 * Whether or not the "save and close" button is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderUpdateAndCloseBtn    = 
		new XUIViewBindProperty<Boolean>( "renderUpdateAndCloseBtn", this, true, Boolean.class );
	
	/**
	 * Whether or nor the "save and create new" button is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderUpdateAndCreateNewBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderUpdateAndCreateNewBtn", this, "false", Boolean.class );

	/**
	 * Whether or not the "delete" button is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderDestroyBtn    = 
		new XUIViewBindProperty<Boolean>( "renderDestroyBtn", this, true, Boolean.class );
	
	/**
	 * Whether or not the "validate" button is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderValidateBtn    = 
		new XUIViewBindProperty<Boolean>( "renderValidateBtn", this, true, Boolean.class );
	
	/**
	 * Whether or not the "duplicate" button is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderCloneBtn    = 
		new XUIViewBindProperty<Boolean>( "renderCloneBtn", this, false, Boolean.class );

	/**
	 * Whether or not to render buttons with 
	 * all the Methods in the targetObject
	 */
	private XUIViewBindProperty<Boolean>  renderObjectMethodBtns    = 
		new XUIViewBindProperty<Boolean>( "renderObjectMethodBtns", this, true, Boolean.class );
	
	/**
	 * Whether or not the "Object Properties" menu is rendered
	 */
	private XUIViewBindProperty<Boolean>  renderPropertiesBtn    = 
		new XUIViewBindProperty<Boolean>( "renderPropertiesBtn", this, true, Boolean.class );

	
	/**
	 * Whether or not the entire "Information" menu is rendered
	 */
	private XUIViewBindProperty<Boolean> renderInformationMenu = 
		new XUIViewBindProperty<Boolean>( "renderInformationMenu", this, false, Boolean.class );
	
	/**
	 * Whether or not the "View Dependents" menu, under the "Information" menu is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderDependentsBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderDependentsBtn", this, "false", Boolean.class );
	
	/**
	 * Whether or not the "View Dependencies" menu, under the "Information" menu is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderDependenciesBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderDependenciesBtn", this, "false", Boolean.class );
	
	/**
	 * Whether or not the "List Versions" menu, under the "Information" menu is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderListVersionBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderListVersionBtn", this, "false", Boolean.class );

	
	/**
	 * Whether or not the entire export Menu is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderExportMenu    = 
		new XUIViewStateBindProperty<Boolean>( "renderExportMenu", this, "false", Boolean.class );
	
	/**
	 * Whether or not the "Export Form as HTML" is rendered inside an "Export" Menu
	 */
	private XUIViewStateBindProperty<Boolean>  renderHTMLBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderHTMLBtn", this, "false", Boolean.class );
	
	/**
	 * Whether or not the "Export Form as PDF" is rendered inside an "Export" Menu"
	 */
	private XUIViewStateBindProperty<Boolean>  renderPdfBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderPdfBtn", this, "false", Boolean.class );
	
	/**
	 * Whether or the "Export Form as Excel" is rendered inside an "Export" Menu
	 */
	private XUIViewStateBindProperty<Boolean>  renderExcelBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderExcelBtn", this, "false", Boolean.class );

	/**
	 * Whether or not the {@link EditToolBar} is in orphan mode
	 */
	private XUIBindProperty<Boolean> orphanMode = 
		new XUIBindProperty<Boolean>("orphanMode", this, Boolean.class, "#{viewBean.editInOrphanMode}" );
		
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
	
	public boolean getRenderUpdateAndCreateNewBtn() {
		return renderUpdateAndCreateNewBtn.getEvaluatedValue();
	}

	public void setRenderUpdateAndCreateNewBtn( String expression ) {
		this.renderUpdateAndCreateNewBtn.setExpressionText( expression );
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
	
	
	public boolean getOrphanMode() {
		return this.orphanMode.getEvaluatedValue();
	}
	
	public void setOrphanMode( String orphanModeExpr ) {
		this.orphanMode.setExpressionText( orphanModeExpr );
	}
	
	public boolean getRenderHTMLBtn() {
		return renderHTMLBtn.getEvaluatedValue();
	}

	public void setRenderHTMLBtn( String expression ) {
		this.renderHTMLBtn.setExpressionText( expression );
	}
	
	public boolean getRenderPdfBtn() {
		return renderPdfBtn.getEvaluatedValue();
	}

	public void setRenderPdfBtn( String expression ) {
		this.renderPdfBtn.setExpressionText( expression );
	}
	
	public boolean getRenderExcelBtn() {
		return renderExcelBtn.getEvaluatedValue();
	}

	public void setRenderExcelBtn( String expression ) {
		this.renderExcelBtn.setExpressionText( expression );
	}
	
	public boolean getRenderDependentsBtn(){
		return renderDependentsBtn.getEvaluatedValue();
	}
	
	public void setRenderDependentsBtn(String expression){
		this.renderDependentsBtn.setExpressionText(expression);
	}
	
	public boolean getRenderDependenciesBtn(){
		return renderDependenciesBtn.getEvaluatedValue();
	}
	
	public void setRenderDependenciesBtn(String expression){
		this.renderDependenciesBtn.setExpressionText(expression);
	}
	
	public boolean getRenderListVersionBtn(){
		return this.renderListVersionBtn.getEvaluatedValue();
	}
	
	public void setRenderListVersionBtn(String expression){
		this.renderListVersionBtn.setExpressionText(expression);
	}
	
	public boolean getRenderInformationMenu(){
		return this.renderInformationMenu.getEvaluatedValue();
	}
	
	public void setRenderInformationMenu(String renderInfoExpr)
	{
		this.renderInformationMenu.setExpressionText(renderInfoExpr);
	}
	
	public boolean getRenderExportMenu(){
		return this.renderExportMenu.getEvaluatedValue();
	}
	
	public void setRenderExportMenu(String expMenuExpr){
		this.renderExportMenu.setExpressionText(expMenuExpr);
	}
	
	@Override
	public void initComponent() {
		if( getOrphanMode() )
			initOrphanComponent();
		else
			initNonOrphanComponent();
	}
	
	@Override
	public void preRender() {
		if( getOrphanMode() ) {
			preOrphanRender();
		}
		else {
			preNonOrphanRender();
		}
		super.initComponent();
	};
	
	
	public void initOrphanComponent() { 
		
		boObject xeoObject = getTargetObject();
		
		// Render ToolBar Methods
		int pos = 0;

		createViewerBeanMethod( pos++,ComponentMessages.EDIT_TOOLBAR_BTN_SAVE.toString(), 
				ComponentMessages.EDIT_TOOLBAR_BTN_SAVE_TOOLTIP.toString() , "ext-xeo/images/menus/gravar.gif", "save", null );
		
		getChildren().add( pos++, Menu.getMenuSpacer( renderUpdateBtn.getExpressionString() ) );
		createViewerBeanMethod( pos++, null, ComponentMessages.EDIT_TOOLBAR_BTN_SAVE_AND_CLOSE_TOOLTIP.toString(),
				"ext-xeo/images/menus/gravar_e_sair.gif", "saveAndClose", null );

		getChildren().add( pos++, Menu.getMenuSpacer( renderUpdateAndCreateNewBtn.getExpressionString() ) );
		createViewerBeanMethod( pos++, null, ComponentMessages.EDIT_TOOLBAR_BTN_SAVE_AND_NEW_TOOLTIP.toString(),
				"ext-xeo/images/menus/gravar_e_criar_novo.gif", "saveAndCreateNew", null );

		getChildren().add( pos++, Menu.getMenuSpacer( renderUpdateAndCloseBtn.getExpressionString() ) );
		createViewerBeanMethod( pos++, null, ComponentMessages.EDIT_TOOLBAR_BTN_REMOVE_TOOLTIP.toString(),
				"ext-xeo/images/menus/remover.gif", "remove", null );
		
		getChildren().add( pos++,Menu.getMenuSpacer( renderDestroyBtn.getExpressionString() ) );
		createViewerBeanMethod( pos++,null, ComponentMessages.EDIT_TOOLBAR_BTN_VALIDATE_TOOLTIP.toString(),
				"ext-xeo/images/menus/confirmar.gif", "processValidate", null );

		getChildren().add( pos++,Menu.getMenuSpacer( renderCloneBtn.getExpressionString() ) );
		createViewerBeanMethod( pos++,ComponentMessages.EDIT_TOOLBAR_BTN_DUPLICATE.toString(), 
				ComponentMessages.EDIT_TOOLBAR_BTN_DUPLICATE_TOOLTIP.toString(), 
				"ext-xeo/images/menus/applications.gif", "duplicate", "tab" );

		
		if (getRenderPropertiesBtn()){
			getChildren().add( pos++,Menu.getMenuSpacer( renderPropertiesBtn.getExpressionString() ) );
			getChildren().add(pos++,createPropertiesMenu());
		}
		
		//Add the information Menus
		if (	getRenderDependenciesBtn()  
			|| 	getRenderDependentsBtn() 
			|| 	getRenderListVersionBtn()
			||  getTargetObject().getBoDefinition().implementsSecurityRowObjects()
			||  getRenderInformationMenu())
			
		{
			getChildren().add( pos++,Menu.getMenuSpacer( ) );
			getChildren().add(pos++,createInformationMenu());
		}
		
		//Add the Export Menus
		if (	getRenderExcelBtn() 
			||	getRenderPdfBtn()
			|| 	getRenderHTMLBtn()
			|| getRenderExportMenu()
		)
		{	
			getChildren().add( pos++, Menu.getMenuSpacer() );	
			getChildren().add( pos++,createExportMenu());
		}
		
		boDefMethod[] methods = xeoObject.getToolbarMethods();
		for( boDefMethod m : methods ) {
			if( !staticMethods.contains( m.getName() ) ) {
				getChildren().add( pos++,Menu.getMenuSpacer( renderObjectMethodBtns.getExpressionString() ) );
				ModelMethod m1 = createMenuMethod( pos++, m.getLabel(), m.getLabel(), m.getName() );
				m1.setVisible( renderObjectMethodBtns.getExpressionString() );
			}
		}
		
		
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
	
	public void preOrphanRender() {
		boObject xeoObject = getTargetObject();
		for( UIComponent comp : getChildren() ) {
			if( comp instanceof ModelMethod ) {
				ModelMethod meth = (ModelMethod)comp;
				
				meth.setVisible(
					Boolean.toString(
						isVisibleByDefinition( meth ) &&  !XEOComponentStateLogic.isMethodHidden(xeoObject, meth.getTargetMethod()) 
					)
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
								isVisibleByDefinition( meth ) && !XEOComponentStateLogic.isMethodHidden( xeoObject, MapObjectMethods.get( idx ))
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
	
	public void initNonOrphanComponent() { 
		boObject xeoObject = getTargetObject();
		// Render ToolBar Methods
		if( getRenderConfirmBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createNonOrphanViewerBeanMethod( 
					XEOComponentMessages.EDITTB_CONFIRM.toString(), 
					XEOComponentMessages.EDITTB_CONFIRM_TTIP.toString(),
					"ext-xeo/images/menus/confirmar.gif", "confirm", null );
		}
		
		if( getRenderValidateBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createNonOrphanViewerBeanMethod( null, 
					XEOComponentMessages.EDITTB_VALIDATE_TTIP.toString(),
					"ext-xeo/images/menus/confirmar.gif", 
					"processValidate", null );
		}
		
		if( getRenderCancelBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createNonOrphanViewerBeanMethod( 
					XEOComponentMessages.EDITTB_CANCEL.toString(), 
					XEOComponentMessages.EDITTB_CANCEL_TTIP.toString(), 
					"ext-xeo/images/menus/applications.gif", "cancel", null );
		}
		
		if( getRenderPropertiesBtn() ) {
			getChildren().add( Menu.getMenuSpacer() );
			createNonOrphanViewerBeanMethod( 
					null, 
					XEOComponentMessages.EDITTB_VIEW_PROPERTIES_TTIP.toString() , 
					"extjs/resources/images/default/tree/leaf.gif", 
					"showProperties", "self" );
			getChildren().add( Menu.getMenuSpacer() );
		}
		
		if( getRenderObjectMethodBtns() ) {
			boDefMethod[] methods = xeoObject.getToolbarMethods();
			for( boDefMethod m : methods ) {
				if( !staticNonOrphanMethods.contains( m.getName() ) ) {
					createNonOrphanMenuMethod( m.getLabel(), m.getLabel(), m.getName() );
				}
			}
		}
		super.initComponent();
	}
	
	private ViewerMethod createNonOrphanViewerBeanMethod( String label, String toolTip, String icon, String methodName, String target ) {
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
	
	public void preNonOrphanRender() {
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
	
	private ModelMethod createNonOrphanMenuMethod( String label, String toolTip, String methodName ) {
		
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
	
	
	/**
	 * 
	 * Creates the properties Menu
	 * 
	 * @return
	 */
	private Menu createPropertiesMenu(){
		
		Menu propertiesMenu = new Menu();
		propertiesMenu.setText("");
		propertiesMenu.setIcon("extjs/resources/images/default/tree/leaf.gif;");
		propertiesMenu.setToolTip(XEOViewersMessages.LBL_PROPERTIES.toString());
		propertiesMenu.setServerAction("#{viewBean.showProperties}");
		propertiesMenu.setTarget("self");
		return propertiesMenu;
		
	}

	/**
	 * 
	 * Creates a menu with the information options
	 * (dependents, dependencies and versioning)
	 * 
	 * @return A menu with the information options
	 */
	private Menu createInformationMenu()
	{
		Menu informationGroup = new Menu();
		informationGroup.setIcon("ext-xeo/images/menus/information.gif");
		informationGroup.setToolTip(XEOComponentMessages.EDITTB_INFORMATION_TTIP.toString());
		
		//Only show if all are enabled
		if (getRenderDependenciesBtn() || getRenderDependentsBtn()  ||
				getRenderInformationMenu()) //|| getRenderPropertiesBtn()
		{
			/*if (getRenderPropertiesBtn() || getRenderInformationMenu()){
				Menu propertiesMenu = new Menu();
				propertiesMenu.setText(XEOViewersMessages.LBL_PROPERTIES.toString());
				propertiesMenu.setServerAction("#{viewBean.showProperties}");
				propertiesMenu.setTarget("window");
				informationGroup.getChildren().add(propertiesMenu);
			}*/
			
			if (getRenderDependentsBtn() || getRenderInformationMenu()){
				Menu dependents = new Menu();
				dependents.setText(XEOViewersMessages.LBL_DEPENDENTS.toString());
				dependents.setServerAction("#{viewBean.showDependents}");
				dependents.setTarget("window");
				informationGroup.getChildren().add(dependents);
			}
			
			if (getRenderDependenciesBtn() || getRenderInformationMenu()){
				Menu dependenciesMenu = new Menu();
				dependenciesMenu.setText(XEOViewersMessages.LBL_DEPENDENCIES.toString());
				dependenciesMenu.setServerAction("#{viewBean.showDependencies}");
				dependenciesMenu.setTarget("window");
				informationGroup.getChildren().add(dependenciesMenu);
			}
		}
		
		if (getRenderListVersionBtn() && getTargetObject().getVersioning())
		{
			Menu versioningMenu = new Menu();
			versioningMenu.setText(XEOComponentMessages.EDITTB_LIST_VERSIONS_TTIP.toString());
			versioningMenu.setServerAction("#{viewBean.listVersions}");
			versioningMenu.setTarget("window");
			informationGroup.getChildren().add(versioningMenu);
		}
		
		if (getTargetObject().getBoDefinition().implementsSecurityRowObjects())
		{
			Menu oplMenu = new Menu();
			oplMenu.setText(XEOComponentMessages.EDITTB_OPL.toString());
			oplMenu.setServerAction("#{viewBean.showOPL}");
			oplMenu.setTarget("self");
			informationGroup.getChildren().add(oplMenu);
		}
		
		return informationGroup;
	}
	
	/**
	 * 
	 * Creates the Export Menu with all options for export (HTML, PDF, Excel)
	 * 
	 * @return A Menu item with all export options
	 */
	private Menu createExportMenu()
	{
		Menu exportGroup = new Menu();
		exportGroup.setIcon("ext-xeo/images/menus/export-group.gif");
		exportGroup.setToolTip(XEOComponentMessages.EDITTB_EXPORT_TTIP.toString());
		
		if (getRenderExcelBtn()  || getRenderExportMenu())
		{
			Menu exportExcelMenu = new Menu();
			exportExcelMenu.setText(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_EXCEL_TOOLTIP.toString());
			exportExcelMenu.setToolTip(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_EXCEL_TOOLTIP.toString());
			exportExcelMenu.setIcon("ext-xeo/images/menus/exportar-excel.gif");
			exportExcelMenu.setServerAction("#{viewBean.exportExcel}");
			exportExcelMenu.setTarget("download");
			exportGroup.getChildren().add(exportExcelMenu);
		}
		
		if (getRenderHTMLBtn() || getRenderExportMenu())
		{
			Menu exportHTMLMenu = new Menu();
			exportHTMLMenu.setText(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_HTML.toString());
			exportHTMLMenu.setIcon("ext-xeo/images/menus/exportar-html.gif");
			exportHTMLMenu.setToolTip(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_HTML_TOOLTIP.toString());
			exportHTMLMenu.setServerAction("#{viewBean.exportHTML}");
			String parameters = "{width:700, height:550, title:''}";
			exportHTMLMenu.setTarget("window:" + parameters);
			exportGroup.getChildren().add(exportHTMLMenu);
		}
		
		if (getRenderPdfBtn() || getRenderExportMenu())
		{
			Menu exportPDFMenu = new Menu();
			exportPDFMenu.setText(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_PDF_TOOLTIP.toString());
			exportPDFMenu.setIcon("ext-xeo/images/menus/exportar-pdf.gif");
			exportPDFMenu.setToolTip(ComponentMessages.EDIT_TOOLBAR_BTN_EXPORT_PDF_TOOLTIP.toString());
			exportPDFMenu.setServerAction("#{viewBean.exportPDF}");
			exportPDFMenu.setTarget("download");
			exportGroup.getChildren().add(exportPDFMenu);
		}
		
		return exportGroup;
	}
	
	private boolean isVisibleByDefinition( ViewerMethod vm ) {
		
		if( "save".equals(  vm.getTargetMethod() ) )
			return getRenderUpdateBtn();
		
		if( "saveAndClose".equals(  vm.getTargetMethod() ) )
			return getRenderUpdateAndCloseBtn();
		
		if( "saveAndCreateNew".equals(  vm.getTargetMethod() ) )
			return getRenderUpdateAndCreateNewBtn();
		
		if( "remove".equals( vm.getTargetMethod() ) )
			return getRenderDestroyBtn();
		
		if( "processValidate".equals( vm.getTargetMethod() ) )
			return getRenderValidateBtn();
		
		if( "duplicate".equals( vm.getTargetMethod() ) )
			return getRenderCloneBtn();
		
		if( "showProperties".equals( vm.getTargetMethod() ) )
			return getRenderPropertiesBtn();
		
		if( "exportHTML".equals( vm.getTargetMethod() ) )
			return getRenderHTMLBtn();
		
		if( "exportPDF".equals( vm.getTargetMethod() ) )
			return getRenderPdfBtn();
		
		if( "exportExcel".equals( vm.getTargetMethod() ) )
			return getRenderExcelBtn();
		
		boDefMethod[] methods = getTargetObject().getToolbarMethods();
		if( Arrays.asList( methods ).indexOf( vm.getTargetMethod() ) > -1 )
			return getRenderObjectMethodBtns();
			
		return true;
	}
	
}

