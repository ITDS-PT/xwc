package netgest.bo.xwc.xeo.advancedSearch;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.xeo.beans.AdvancedSearchBean;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.MetadataUtils;
import netgest.utils.StringUtils;

/**
 * 
 * Supports choosing an attribute from a tree panel with the list
 * of attribute of a given Model (and attributes from a relation 
 * up to one level deep)
 * 
 * @author PedroRio
 *
 */
public class AdvancedSearchAttributeChooser extends XEOBaseBean {

	
	/**
	 * The name of the Model to list the attributes to choose
	 */
	private String objectName;

	/**
	 * Id of the component where to set the chosen attribute
	 */
	private String parentComponentId = "";
	
	public void setParentCompomnentId( String compId ){
		this.parentComponentId = compId;
	}
	
	public void setObjectName( String objName ) {
		this.objectName = objName;
	}

	public String getObjectName() {
		return objectName;
	}
	
	/**
	 * Used to generate unique ids for the attributes
	 */
	private int incrementer = 0;

	/**
	 * Create the root menu for the TreePanel with the attributes 
	 */
	public Menu getRootMenu() {
		Menu root = new Menu();
		appendMenusCreatedFromDefinition( root, objectName );
		return root;
	}

	/**
	 * 
	 * Append menus with the list of attributes to the root
	 * 
	 * @param root The root menu
	 * @param modelName The model name
	 * 
	 */
	private Menu appendMenusCreatedFromDefinition( Menu root, String modelName ) {

		boDefHandler definitionHandler = getObjectMetadata( modelName );

		boDefAttribute[] attributes = definitionHandler.getAttributesDef();
		for ( boDefAttribute current : attributes ) {

			if ( StringUtils.hasValue( current.getName() ) && isNotStateAttribute( current ) ) {
				Menu m = new Menu();
				m.setId(current.getName() + (incrementer++));
				m.setText( current.getLabel() );
				ModelInformation info = new ModelInformation( modelName, current.getName(), null, current.getReferencedObjectName() );
				m.setValue( info.toString() );
				m.setIcon( MetadataUtils.getPathModelIcon( current.getReferencedObjectName() ) );
				m.setTarget( "self" );
				String expression = "#{" + getId() + ".chooseAttribute}"; 
				m.setServerAction( expression );
				root.getChildren().add( m );
				if ( isObjectAttribute( current ) ){
					appendFirstLevelChildren( m, modelName, current.getName(), current.getReferencedObjectName() );
				}
			}

		}
		return root;
	}

	/**
	 * Check if an attribute is not a state attribute
	 * 
	 * @param current The attribute metadata
	 * 
	 * @return True if the attribute IS NOT a state attribute and false otherwise
	 */
	private boolean isNotStateAttribute( boDefAttribute current ) {
		return boDefAttribute.TYPE_STATEATTRIBUTE != current.getAtributeType();
	}

	/**
	 * 
	 * Append the child attributes of attributes that are of type object/bridge
	 * 
	 * @param root The root to append the children
	 * @param modelName The name of the XEO Model
	 * @param parentAttributeName The parent attribute name
	 * @param targetObject The target object (the type from the parentAttributeName)
	 */
	private void appendFirstLevelChildren( Menu root, String modelName, String parentAttributeName, String targetObject ) {

		boDefHandler definitionHandler = getObjectMetadata( targetObject );

		boDefAttribute[] attributes = definitionHandler.getAttributesDef();
		for ( boDefAttribute current : attributes ) {

			if ( StringUtils.hasValue( current.getName() ) ) {
				Menu m = new Menu();
				m.setId(current.getName() + (incrementer++) );
				m.setText( current.getLabel() );
				ModelInformation info = new ModelInformation( modelName, current.getName(), parentAttributeName, targetObject );
				m.setValue( info.toString() );
				String expression = "#{" + getId() + ".chooseAttribute}"; 
				m.setServerAction( expression );
				root.getChildren().add( m );
			}

		}
	}

	/**
	 * Invoked when the user chooses an attribute, set the value of the parent component id
	 */
	public void chooseAttribute() {
		AdvancedSearchBean parentBean = ( AdvancedSearchBean ) getParentView().getBean( "viewBean" );
		
		XVWScripts.closeView( getViewRoot() );
		
		Menu command = (Menu)getRequestContext().getEvent().getSource();
		String value = command.getValue().toString();
		ModelInformation info = ModelInformation.fromJSON( value );
		if (info == null)
			throw new RuntimeException( "Could not parse the JSON definition for " + value);
		
		String modelName = info.getModelName();
		String attributeName = info.getAttributeName();
		String targetObject = info.getTargetObject();
		String parentAttributeName = info.getParentAttribute();
		String label = command.getText();
		parentBean.setLookupResult( parentComponentId, attributeName, label, modelName, parentAttributeName, targetObject );
		
		
	}
	
	
	
	/**
	 * 
	 * 
	 * Checks if an attribute is of type object/collection
	 * 
	 * @param att The attribute to check
	 * 
	 * @return True if the attribute is of type object/collection and false otherwise
	 */
	private boolean isObjectAttribute( boDefAttribute att ) {
		assert att != null : "Cannot have null attribute";
		return MetadataUtils.isObjectOrCollection( att );
	}

	/**
	 * 
	 * Retrieve the metadata about an object
	 * 
	 * @param modelName the name of the model
	 * @return The model metadata
	 */
	private boDefHandler getObjectMetadata( String modelName ) {
		return boDefHandler.getBoDefinition( modelName );
	}

	
	
	
	
}
