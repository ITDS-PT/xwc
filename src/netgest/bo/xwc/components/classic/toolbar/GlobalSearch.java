package netgest.bo.xwc.components.classic.toolbar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.AttributeLov;
import netgest.bo.xwc.components.classic.AttributeText;
import netgest.bo.xwc.components.classic.ViewerSecurityBase;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;

/**
 * 
 * The global search component is a toolbar component, typically to be placed in a main viewer
 * and it allows you to make searches on the Ebo_TextIndex results. THe component
 * is basically a grouping of two form fields and a menu component (Hence it implements the
 * {@link IToolbarGroup} interface.
 * 
 * It has a text field for the user to set its search parameters and an (optional) combo
 * to allow users to restrict searches to a set of objects 
 * 
 * The button to perform a search, opens a new viewer in a tab with the results
 * 
 * 
 *
 */
public class GlobalSearch extends ViewerSecurityBase implements IToolbarGroup {

	/**
	 * The label of the search button
	 */
	private XUIBaseProperty<String> label = 
		new XUIBaseProperty<String>("label", this, ComponentMessages.GLOBAL_SEARCH_LBL.toString() );
	
	/**
	 * The icon of the search button
	 */
	private XUIBaseProperty<String> icon = 
		new XUIBaseProperty<String>("icon", this, "" );
	
	/**
	 * Whether to show the models combo or not (defaults to false)
	 */
	public XUIBindProperty<Boolean> showModelsCombo = 
		new XUIBindProperty<Boolean>( "showModelsCombo", this, false, Boolean.class );
	
	/**
	 * The width of the search text fields
	 */
	private XUIBaseProperty<String> searchWidth = 
		new XUIBaseProperty<String>("searchWidth", this, "80" );
	
	/**
	 * A map containing the XEO Models to restrict the search
	 */
	private XUIBindProperty<HashMap<String,String>> comboModelOptions = 
		new XUIBindProperty<HashMap<String,String>>("comboModelOptions", this, HashMap.class, "#{viewBean.searchOptions}" );
	
	/**
	 * The width of the combobox
	 */
	private XUIBaseProperty<String> comboWidth = 
		new XUIBaseProperty<String>("comboWidth", this, "80" );
	
	/**
	 * 
	 * Retrieve the width of the search text field
	 * 
	 * @return A string with the size of the box (defaults to 80)
	 */
	public String getSearchWidth(){
		return searchWidth.getValue();
	}
	
	public void setSearchWidth(String width){
		this.searchWidth.setValue(width);
	}
	
	/**
	 * 
	 * Retrieve the width of the combo box field
	 * 
	 * @return A string with the size of the box (defaults to 80)
	 */
	public String getComboWidth(){
		return this.comboWidth.getValue();
	}
	
	public void setComboWidth(String width){
		this.comboWidth.setValue(width);
	}
	
	/**
	 * 
	 * Whether or not to display the combo models with XEO Models
	 * to restrict the search (defaults to false)
	 * 
	 * @return True to display the combo and false otherwise
	 */
	public Boolean getShowModelsCombo(){
		return showModelsCombo.getEvaluatedValue();
	}
	
	public void setShowModelsCombo(String valueExpr){
		this.showModelsCombo.setExpressionText(valueExpr);
	}
	
	/**
	 * 
	 * Retrieve the label of the search button
	 * 
	 * @return A string with the button label (defaults to "Search")
	 */
	public String getLabel(){
		return label.getValue();
	}
	
	public void setLabel(String newLabel){
		this.label.setValue(newLabel);
	}
	
	/**
	 * 
	 * Retrieves the path to an icon to be placed along side the text
	 * in the search button 
	 * 
	 * @return A string with the path to the icon file
	 */
	public String getIcon(){
		return icon.getValue();
	}
	
	public void setIcon(String iconVal){
		this.icon.setValue(iconVal);
	}
	
	/**
	 * 
	 * Retrieves the list of models to be part of the combo box
	 * 
	 * @return A map with pairing ObjectName -> Label, e.g. (Ebo_Perf -> User)
	 */
	public HashMap<String,String> getComboModelOptions(){
		return comboModelOptions.getEvaluatedValue();
	}
	
	public void setComboModelOptions(String comboExpr){
		this.comboModelOptions.setValue(comboExpr);
	}
	
	//Security methods
	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.MENU;
	}

	public String getViewerSecurityId() {
		return null;
	}

	public String getViewerSecurityLabel() {
		return getViewerSecurityComponentType().toString();
	}

	public boolean isContainer() {
		return true;
	}

	@Override
	public List<UIComponent> getComponentList() {

		AttributeText t = new AttributeText();
		t.setMaxLength(30);
		t.setRenderComponent(false);
		t.setValueExpression("#{viewBean.textSearch}");
		t.setWidth(getSearchWidth());
		t.setId("textSearchXEO");
		
		AttributeLov lov = null;
		if (getShowModelsCombo()){
			lov = new AttributeLov();
			lov.setRenderComponent(false);
			lov.setValueExpression("#{viewBean.searchModel}");
			lov.setLovMap(comboModelOptions.getExpressionString());
			lov.setWidth(getComboWidth());
			lov.setOnChangeSubmit(true);
			lov.setMaxLength(20);
			lov.setId("lovOptionsXEO");
		}
		
		Menu m = new Menu();
		m.setText(getLabel());
		String icon = getIcon();
		if (icon != null && !(icon.length() == 0))
			m.setIcon(getIcon());
		m.setTarget("alwaysNewTab");
		m.setServerAction("#{viewBean.searchGlobal}");
		m.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
		m.setId("globalSearchActionXEO");
		
		
		List<UIComponent> result = new LinkedList<UIComponent>();
		result.add(t);
		if (getShowModelsCombo() && lov != null)
			result.add(lov);
		result.add(m);
		
		return result;
	}
	
	

}
