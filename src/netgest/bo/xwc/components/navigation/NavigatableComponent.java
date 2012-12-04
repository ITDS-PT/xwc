package netgest.bo.xwc.components.navigation;

/**
 * Represents a component which has somekind of navigation
 * meaning that
 *
 */
public interface NavigatableComponent {

	public int getHierarchyLevel();

	public boolean getHasParentNavigator();
	
}
