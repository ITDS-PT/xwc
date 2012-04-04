package netgest.bo.xwc.components.classic.toolbar;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.AttributeLabel;
import netgest.bo.xwc.components.classic.AttributeLov;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;

public class ThemeChanger extends XUIComponentBase implements IToolbarGroup {

	/**
	 * The label before the lov
	 */
	private XUIBindProperty<String> label = new XUIBindProperty<String>("label", this, "", String.class); 
	
	/**
	 * The label of the menu to change the theme
	 */
	private XUIBindProperty<String> menuLabel = new XUIBindProperty<String>("menuLabel", this, 
			XEOComponentMessages.THEME_CHANGER_MENU_TEXT.toString(), String.class);
	
	/**
	 * The icon of the menu
	 */
	private XUIBindProperty<String> menuIcon = new XUIBindProperty<String>("menuIcon", this, "", String.class);
	
	/**
	 * THe tooltip of the menu
	 */
	private XUIBindProperty<String> menuTooltip = new XUIBindProperty<String>("menuTooltip", this, "", String.class);
	
	/**
	 * 
	 * Retrieve the label before the lov
	 * 
	 * @return
	 */
	public String getLabel(){
		return label.getEvaluatedValue();
	}
	
	public void setLabel(String labelExpr){
		this.label.setExpressionText(labelExpr);
	}
	
	public void setMenuLabel(String labelExpr){
		this.menuLabel.setExpressionText(labelExpr);
	}
	
	/**
	 * 
	 * Retrieve the label of the menu
	 * 
	 * @return
	 */
	public String getMenuLabel(){
		return this.menuLabel.getEvaluatedValue();
	}
	
	public void setMenuIcon(String iconExpr){
		this.menuIcon.setExpressionText(iconExpr);
	}
	
	public String getMenuIcon(){
		return this.menuIcon.getEvaluatedValue();
	}
	
	public void setMenuTooltip(String tooltipExpr){
		this.menuTooltip.setExpressionText(tooltipExpr);
	}
	
	public String getMenuTooltip(){
		return this.menuTooltip.getEvaluatedValue();
	}
	
	@Override
	public List<UIComponent> getComponentList() {
		
		List<UIComponent> list = new LinkedList<UIComponent>();
		
		if (!getLabel().equalsIgnoreCase("")){
			AttributeLabel lbl = new AttributeLabel();
			lbl.setText(getLabel());
			list.add(lbl);
		}
		
		AttributeLov themesLov = new AttributeLov();
		themesLov.setLovMap("#{ " + getBeanId() + ".themeMap}");
		themesLov.setValueExpression("#{ " + getBeanId() + ".theme}");
		themesLov.setMaxLength(20);
		themesLov.setId("themesChangedLov");
		list.add(themesLov);
		
		Menu changeThemeBtn = new Menu();
		changeThemeBtn.setText(getMenuLabel());
		changeThemeBtn.setIcon(getMenuIcon());
		changeThemeBtn.setToolTip(getMenuTooltip());
		changeThemeBtn.setId("changeThemeMenu");
		changeThemeBtn.setServerAction("#{ " + getBeanId() + ".changeTheme}");
		changeThemeBtn.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
		
		list.add(changeThemeBtn);
		return list;
	}

}
