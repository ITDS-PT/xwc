package netgest.bo.xwc.xeo.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.ql.boqlParserException;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeBoolean;
import netgest.bo.xwc.components.classic.AttributeDate;
import netgest.bo.xwc.components.classic.AttributeDateTime;
import netgest.bo.xwc.components.classic.AttributeLov;
import netgest.bo.xwc.components.classic.AttributeNumber;
import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.components.classic.AttributeText;
import netgest.bo.xwc.components.classic.AttributeTextArea;
import netgest.bo.xwc.components.classic.Cell;
import netgest.bo.xwc.components.classic.GenericLookup;
import netgest.bo.xwc.components.classic.GridExplorer;
import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.Row;
import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder.Filter;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder.ValueType;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.html.GenericTag;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.jsf.XUIViewerBuilder;
import netgest.bo.xwc.framework.jsf.XUIWriterAttributeConst;
import netgest.bo.xwc.framework.jsf.XUIWriterElementConst;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchAttributeChooser;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchLovValueChooserBean;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.JOIN_OPERATOR;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.VALUE_OPERATOR;
import netgest.bo.xwc.xeo.advancedSearch.OperatorMapLoader;
import netgest.bo.xwc.xeo.advancedSearch.RowInfo;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.utils.LovUtils;
import netgest.utils.MetadataUtils;
import netgest.utils.StringUtils;

public class AdvancedSearchBean extends XEOBaseBean {

	public AdvancedSearchBean(EboContext ctx){
		super(ctx);
	}
	
	public AdvancedSearchBean(){
		super();
	}
	
	private final Logger logger = Logger.getLogger( AdvancedSearchBean.class );
	
	/**
	 * A prefix added to every identifier for attribute names, values and operators 
	 */
	private static final String PREFIX = "ATT";
	
	/**
	 * The identifier of the form component associated with this viewer (check the AdvancedSearch.xvw viewer)
	 */
	private static final String FORM_ID = "search";
	
	/**
	 *  An enumeration with the suffixes added to 
	 *  each of the components for a row (attribute, operator, 
	 *  value operator and join operator)
	 *
	 */
	private enum SUFFIX{
		ATTRIBUTE("_attribute"),
		OPERATOR_VALUE("_opValue"),
		ATTRIBUTE_VALUE("_value"),
		JOIN_VALUE("_join");
		
		private String label;
		
		private SUFFIX(String label){
			this.label = label;
		}
		
		public String getLabel(){
			return label;
		}
	}
	
	
	/**
	 * The number of initial rows to display with search criteria
	 * when the query is empty
	 */
	private static final int INITIAL_ROWS_COUNT = 2;
	
	/**
	 * Helper class to load XEOLovs as Map<String,String> to display the join
	 * and value operator lovs
	 */
	private OperatorMapLoader mapLoader = new OperatorMapLoader();
	
	/**
	 * Apply the existing query to the parent explorer
	 */
	public void applyFilter(){
		
		GridExplorer explorer = (GridExplorer) getParentView().findComponent( GridExplorer.class );
		explorer.markAdvancedSearchActive();
		
		List<AdvancedRowComponentIds> rows = sortRows();
		String querySql = generateBoqlExpression(rows);
		Vector<Object> parameters = new Vector<Object>();
		for (Iterator<AdvancedRowComponentIds> it = rows.iterator(); it.hasNext(); ){
			AdvancedRowComponentIds curr = it.next();
			Object currValue = attributeValues.get( curr.getValueId() );
			if (currValue != null)
				parameters.add( curr.getValueId() );
		}
		QLParser parser = new QLParser();
		try{
			parser.toSql( querySql, getEboContext(), parameters );
			
			String queryResult = getFiltersAsJSON();
			explorer.setAdvancedFilters( queryResult );
			
			//Fecha a view corrent (window)
			XVWScripts.closeView( getViewRoot() );
			
			//Diz que a view corrente (que vai ser renderizada) é a do pai para que não se percam valores
			getRequestContext().setViewRoot( getParentView() );
			
		}
		catch (boqlParserException e){
			getRequestContext().addMessage( "", 
					new XUIMessage( 
							XUIMessage.TYPE_ALERT, 
							XUIMessage.SEVERITY_ERROR, 
							"!", 
							BeansMessages.ADVANCED_SEARCH_QUERY_ERROR.toString() + e.getErrorMessage() + "<br /> <span style='color:red'>"  + e.getErrorSpot() + "</span>"
							));
		}
		
		
		
	}
	
	/**
	 * Remove any existing filter from the explorer
	 */
	public void removeFilters(){
		GridExplorer explorer = (GridExplorer) getParentView().findComponent( GridExplorer.class );
		explorer.setAdvancedFilters( null );
		explorer.markAdvancedSearchInactive();
		
		//Fecha a view
		XVWScripts.closeView( getViewRoot() );
		
		//Diz que a view corrente (que vai ser renderizada) é a do pai para que não se percam valores
		getRequestContext().setViewRoot( getParentView() );
	}
	
	/**
	 * 
	 * Initialize the advanced search, whether by creating empty rows for the user
	 * to create a query or by recreating the current filters
	 * 
	 * 
	 * @param filterIterator An iterator over the filters (could be empty to 
	 * create new empty rows) 
	 */
	public void initializeFilters( Iterator<Filter> filterIterator){
		if (!filterIterator.hasNext()){
			this.createInitialRows();
		} else 
			reCreateExistingFilters( filterIterator );
	}
	
	/**
	 * The name of the Model being searched
	 */
	private String objectName;

	/**
	 * Set the Model name to be searched
	 */
	public void setObjectName( String name ) {
		this.objectName = name;
	}

	/**
	 * Retrieve the model name 
	 */
	public String getObjectName() {
		return this.objectName;
	}
	
	/**
	 * Creates the initial (empty) rows for the search
	 */
	public void createInitialRows(){
		for (int k = 0; k < INITIAL_ROWS_COUNT ; k++){
			addNewRow();
		}
		reRenderListOfRules();
	}
	
	/**
	 * Counter to generate unique Id's for the elements
	 */
	private int counterId = 1;
	
	/**
	 * 
	 * Increments the counter (returning the value), 
	 * to be used when a new id is needed
	 * 
	 * @return The counter
	 */
	private int incrementCounter(){
		return counterId++;
	}
	
	/**
	 * Map to store the mapping between components and the models they
	 * are related to (when we have a Object/Bridge relation we need to keep the
	 * mapping so that when it's required to open a lookup viewer we know which
	 * query to execute)
	 */
	private Map<String,String> lookupModelNames = new HashMap<String, String>();
	
	/**
	 * Map to store the mapping between components and the attribute of an object that's being
	 * searched (it's only used when the attribute is a from a different object than the one
	 * retrieved by {@link #getObjectName()} such as "attributeObject.name"
	 */
	private Map<Integer,String> parentAttribute = new HashMap<Integer, String>();
	
	
	/**
	 * Holds the attributes stored in each of the rows 
	 */
	private Map<String,String> attributes = new HashMap<String, String>();
	
	public Map<String,String> getAttributes(){
		return attributes;
	}
	
	/**
	 * Holds the various join operators for each of the row
	 */
	private Map<String,String> joinOperatorValues = new HashMap<String, String>();
	
	public Map<String,String> getJoinOperatorValues(){
		return joinOperatorValues;
	}
	
	/**
	 * Holds the value operators for each of the rows
	 */
	private Map<String,String> valueOperators = new HashMap<String, String>();
	
	public Map<String,String> getValueOperators(){
		return valueOperators;
	}
	
	/**
	 * Holds the attribute values for each of the rows
	 */
	private Map<String,Object> attributeValues = new HashMap<String, Object>();
	
	public Map<String,Object> getAttributeValues(){
		return this.attributeValues;
	}
	
	/**
	 * Maps the id of a row to the list of components in that row 
	 * (each row consists of join, attribute, operator and avalue) 
	 */
	private Map<Integer,AdvancedRowComponentIds> componentIds = 
		new HashMap<Integer, AdvancedSearchBean.AdvancedRowComponentIds>();
	
	/**
	 * Maps the id of a component to the id of the parent row
	 */
	private Map<String,Integer> reverseComponentIds = new HashMap<String, Integer>();
	
	
	
	/**
	 * This method is invoked from the ChooseAttribute.xvw viewer when the user selects a given attribute
	 * to use in the search (this updates the current row to display the correct value operators for the
	 * given attribute type and the correct component to choose a value) 
	 */
	public void chooseAttribute() {

		XUIViewRoot oViewRoot;
		
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext oSessionContext = oRequestContext.getSessionContext();

		XUICommand lookup = ( XUICommand ) oRequestContext.getEvent().getSource();
		GenericLookup genericLookup = (GenericLookup) lookup.getParent();
		
		oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/xeo/viewers/advancedSearch/ChooseAttribute.xvw" );
		AdvancedSearchAttributeChooser bean = ( AdvancedSearchAttributeChooser ) oViewRoot.getBean( "viewBean" );
		bean.setObjectName( getObjectName() );
		bean.setParentCompomnentId( genericLookup.getClientId() );
		
		oRequestContext.setViewRoot( oViewRoot );

	}
	
	/**
	 * This method is invoked when the user needs to select a value from an existing lov
	 */
	public void doLov(){
		XUICommand command = (XUICommand) getRequestContext().getEvent().getSource();
		GenericLookup lookup = (GenericLookup) command.getParent();
		String clientId = lookup.getId();
		
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        Integer rowId = this.reverseComponentIds.get( clientId );
        String id = this.componentIds.get(rowId).getAttributeId();
		
		AttributeBase oAtt = ( AttributeBase ) getViewRoot().findComponent( FORM_ID + ":" + id );
		String modelName = lookupModelNames.get( id );
		boDefHandler objectMetadata = boDefHandler.getBoDefinition( modelName );
		boDefAttribute      oAttDef     = objectMetadata.getAttributeRef( oAtt.getValue().toString() );
        String lovName = oAttDef.getLOVName();
        
        Map<String,String> choices = LovUtils.createMapFromLov( getEboContext(), lovName );
        
    	oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/xeo/viewers/advancedSearch/ChooseLovValue.xvw" );
        AdvancedSearchLovValueChooserBean oBaseBean = ( AdvancedSearchLovValueChooserBean )oViewRoot.getBean( "viewBean" );
        oBaseBean.setLovMap( choices );
        oBaseBean.setParentComponentId( clientId );
        oBaseBean.setLovName( lovName );
        
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
	}
	
	@Override
	public void lookupFilterObject() {

        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();

    	XUIActionEvent e = oRequestContext.getEvent();
    	XUICommand cmd = (XUICommand)e.getComponent();
    	GenericLookup lookup = (GenericLookup) cmd.getParent();
    	String clientId = lookup.getId();
    	
    	Integer rowId = this.reverseComponentIds.get( clientId );
        String id = this.componentIds.get(rowId).getAttributeId();
		
		AttributeBase oAtt = ( AttributeBase ) getViewRoot().findComponent( FORM_ID + ":" + id );
		String modelName = lookupModelNames.get( id );
		boDefHandler objectMetadata = boDefHandler.getBoDefinition( modelName );
		boDefAttribute      oAttDef     = objectMetadata.getAttributeRef( oAtt.getValue().toString() );
        
        String sLookupViewer = null;
        
        if( sLookupViewer == null || sLookupViewer.length() == 0 ) {
	    	String className = oAttDef.getReferencedObjectName(); 
	    	if( "boObject".equals( oAttDef.getReferencedObjectName() ) ) {
	    		String[] objects = oAttDef.getObjectsName();
	    		if( objects != null && objects.length > 0 ) {
	    			className = objects[0];
	    		}
	    	}
			sLookupViewer = "viewers/" + className + "/lookup.xvw";
        }
		
        oViewRoot = oSessionContext.createChildView( sLookupViewer );

        XEOBaseLookupList   oBaseBean;
        oBaseBean = (XEOBaseLookupList)oViewRoot.getBean( "viewBean" );
        
        
        Map<String, String> lookupObjs = getLookupObjectsMap( oAttDef );
        
        oBaseBean.setParentParentBeanId( "viewBean" );
        oBaseBean.setParentComponentId( cmd.getClientId() );
        oBaseBean.setMultiLookup( false );
        oBaseBean.setFilterLookup( true );
        oBaseBean.setLookupObjects( lookupObjs );
        oBaseBean.setParentParentBeanId( "viewBean" );
        oBaseBean.setParentAttributeName( clientId );
    	oBaseBean.executeBoql( "select " + lookupObjs.keySet().iterator().next() );

        oRequestContext.setViewRoot( oViewRoot );
        
    }
	
	@Override
	public void setLookupFilterResults( XEOBaseLookupList lookupBean, DataRecordConnector[] records ) {
		XUIViewRoot oRoot = this.getViewRoot();
    	XUICommand oCmd     = (XUICommand)oRoot.findComponent( ":" + lookupBean.getParentComponentId() );
    	GenericLookup genericLookup = (GenericLookup) oCmd.getParent();
    	
    	List<String> values = new LinkedList<String>();
    	for (DataRecordConnector c : records){
    		String boui = c.getAttribute( "BOUI" ).getValue().toString();
    		values.add( boui );
    	}
    	if (records.length > 1)
    		setLookupValueResultBridge( genericLookup, values );
    	else
    		setLookupValueResult( genericLookup, values.get(0) );
    	
    	
    
		XUIRequestContext.getCurrentContext().setViewRoot( oRoot );
		oRoot.processInitComponents(); 
	}
	/**
	 * Opens the lookup viewer to allow a user to choose an object (or several objects)
	 */
	public void doLookup(){
		this.lookupFilterObject();
	}

	
	
	/**
	 * 
	 * Sets the value of the lov value choice into the respective component
	 * 
	 * @param compId The component id where to set the value
	 * @param value The value of the lov entry
	 * @param lovName The name of the lov
	 */
	public void setLovChoiceValueResult(String compId, String value, String lovName){
		XUIViewRoot oViewRoot = getViewRoot();
        GenericLookup genericLookupCmp  = ( GenericLookup ) getViewRoot().findComponent( FORM_ID + ":" + compId );
        
		getRequestContext().setViewRoot( oViewRoot );
		
		String displayValue = getLovDisplayValue( lovName, value );
		
		genericLookupCmp.setDisplayValue( displayValue );
		genericLookupCmp.setValue( value );
		genericLookupCmp.updateModel();
		
		reRenderListOfRules();
	}
	
	/**
	 * 
	 * Given a lov entry value and the lov name, load and retrieve the label
	 * 
	 * @param lovName The name of the lov
	 * @param value The value for the entry in the lov
	 * 
	 * @return The corresponding description/label of the value
	 */
	private String getLovDisplayValue( String lovName, String value ) {
		return LovUtils.getDescriptionForLovValues( getEboContext(), lovName, value );
	}

	/**
	 * 
	 * Sets the value of a lookup search (loads the associated label)
	 * 
	 * @param compId The component where to set the value
	 * @param value 
	 */
	public void setLookupValueResult(GenericLookup genericLookupCmp, String value){
		XUIViewRoot oViewRoot = getViewRoot();
        
		getRequestContext().setViewRoot( oViewRoot );
		
		String displayValue = getDisplayValue( value );
		
		genericLookupCmp.setDisplayValue( displayValue );
		genericLookupCmp.setValue( value );
		genericLookupCmp.updateModel();
		
		reRenderListOfRules();
	}
	
	/**
	 * X
	 * Sets the value of a bridge lookup search (loads the associated labels)
	 * 
	 * @param compId The component where to set the value
	 * @param values The values to set
	 */
	public void setLookupValueResultBridge(GenericLookup bridgeLookupCmp, List<String> values){
		XUIViewRoot oViewRoot = getViewRoot();
        
		getRequestContext().setViewRoot( oViewRoot );
		
		String displayValue = getDisplayValueBridge( values );
		StringBuilder listOfBouis = new StringBuilder();
		String separator = "";
		for (String val : values){
			listOfBouis.append( separator );
			listOfBouis.append( val );
			separator = ",";
		}
		
		bridgeLookupCmp.setDisplayValue( displayValue );
		
		bridgeLookupCmp.setValue( listOfBouis.toString() );
		bridgeLookupCmp.updateModel();
		
		reRenderListOfRules();
	}
	
	
	/**
	 * 
	 * Given the values of objects chosen for a bridge lookup load their associated 
	 * labels
	 * 
	 * @param values A list of the BOUIs to process
	 * @return A string with the CardIDs of each object separated by a comma (and a space after the comma)
	 */
	private String getDisplayValueBridge( List<String> values ) {
		StringBuilder b = new StringBuilder();
		String append = "";
		for (String val : values){
			b.append( append );
			b.append( getDisplayValue( val ) );
			append = ", ";
		}
		
		return b.toString();
	}

	/**
	 * 
	 * Retrieves the cardid of a given object
	 * 
	 * @param value The BOUI of the object
	 * 
	 * @return The CARID of the object
	 */
	private String getDisplayValue( String value ) {
		boApplication app = boApplication.getDefaultApplication();
		boObject obj;
		try {
			if (StringUtils.hasValue( value )){
				obj = app.getObjectManager().loadObject( getEboContext(), Long.valueOf( value ) );
				return obj.getTextCARDID().toString();
			}
		} catch ( NumberFormatException e ) {
			e.printStackTrace();
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * Sets the attribute chosen when the user open the lookup with the attribute
	 * 
	 * @param compId The component where to set the value of the chosen attribute
	 * @param attributeName The name of the attribute chosen
	 * @param label The label of the attribute
	 * @param objectName The name of the model associated with the attribute 
	 * @param parentAttributeName The parent attribute name (if it exists)
	 */
	public void setLookupResult(String compId, String attributeName, String label, String objectName, String parentAttributeName, String targetObject ){
		
			XUIViewRoot oViewRoot = getViewRoot();
	        GenericLookup genericLookupCmp  = ( GenericLookup ) getViewRoot().findComponent( compId );
	        
			getRequestContext().setViewRoot( oViewRoot );
			
			genericLookupCmp.setDisplayValue( label );
			genericLookupCmp.setValue( attributeName );
			genericLookupCmp.updateModel();
			
			Integer baseId = reverseComponentIds.get( genericLookupCmp.getId() );
			if (baseId == null)
				baseId = 0;
			
			String objectNameParameter = "";
			if (StringUtils.hasValue( parentAttributeName ) )
				objectNameParameter = targetObject;
			else
				objectNameParameter = objectName;
			
			lookupModelNames.put( genericLookupCmp.getId(), objectNameParameter );
			if (StringUtils.hasValue( parentAttributeName ) )
				parentAttribute.put( baseId, parentAttributeName + "." + attributeName );
			
			
			updateLovMapForOperators( attributeName, objectNameParameter, baseId );
			replaceAttributeValueChooserAndClearOldValue( attributeName, objectNameParameter, baseId );
			
			reRenderListOfRules();
			
	}

	/**
	 * 
	 * Whenever an attribute is chosen in a row, we must update the list of value operators
	 * so that the values are valid for the given data type
	 * 
	 * @param attributeName The name of the attribute being updated
	 * @param objectName The name of the object where the attribute resides
	 * @param baseId The identifier of the row
	 */
	private void updateLovMapForOperators( String attributeName, String objectName, Integer baseId ) {
		Cell cell = (Cell) getViewRoot().findComponent( getCellOperatorId( baseId.intValue() ) );
		AttributeLov lov = (AttributeLov) cell.getChildren().get( 0 );
		boDefHandler modelMetadata = boDefHandler.getBoDefinition( objectName );
		boDefAttribute attributeMetadata = modelMetadata.getAttributeRef( attributeName );
		lov.setLovMap( getLovMapExpression( attributeMetadata ) );
		
		
	}

	/**
	 * 
	 * Replace an existing component to choose an attribute value by another (usually when a new attribute
	 * is selected in a row), and clear the old value it had
	 * 
	 * @param attributeName The name of the attribute being updated
	 * @param objectName The name of the object where the attribute resides
	 * @param baseId The identifier of the row
	 * 
	 */
	private void replaceAttributeValueChooserAndClearOldValue( String attributeName, String objectName, Integer baseId ) {
		Cell cell = (Cell) getViewRoot().findComponent( getCellValueId( baseId.intValue() ) );
		cell.getChildren().clear();
		cell.getChildren().add( createComponentFromAttributeType( objectName, attributeName, baseId ) );
		
		//Clear old value
		attributeValues.remove( createIdFromParts(baseId, SUFFIX.ATTRIBUTE_VALUE) );
		
	}
	
	/**
	 * 
	 * Given an attribute type, return the EL expression that will allow the lov 
	 * component the list of operators correct for the given input type
	 * 
	 * @param attributeMetadata The metadata about the attribute
	 * 
	 * @return An EL Expression for the 
	 */
	private String getLovMapExpression( boDefAttribute attributeMetadata ){
		String operators = "operatorsText";
		if ( MetadataUtils.isText( attributeMetadata ) && !MetadataUtils.isLov( attributeMetadata ) )
			operators = "operatorsText";
		else if ( MetadataUtils.isBoolean( attributeMetadata ) || MetadataUtils.isBinary( attributeMetadata ) )		
			operators = "operatorsBoolean";
		else if ( (MetadataUtils.isDateType( attributeMetadata ) || MetadataUtils.isNumber( attributeMetadata )) && !MetadataUtils.isLov( attributeMetadata ) )
			operators = "operatorsDate";		
		else if (MetadataUtils.isObjectOrCollection( attributeMetadata ) || MetadataUtils.isLov( attributeMetadata ))
			operators = "operatorsObject";
		else if (MetadataUtils.isLongText( attributeMetadata ))
			operators = "operatorsLongText";
		
		return "#{" + getId() + "."+ operators + "}";
	}
	
	
	/**
	 * 
	 * Utility to retrieve the Id of the Cell component where a certain operator resides
	 * 
	 * @param baseId The row id
	 * @return The cell identifier
	 */
	private String getCellOperatorId( int baseId ){
		return FORM_ID + ":op_" + baseId;
	}
	
	/**
	 * 
	 * Utility to retrieve the Id of the Cell component where a certain value component resides
	 * 
	 * @param baseId The row id
	 * @return The cell identifier
	 */
	private String getCellValueId( int baseId ){
		return FORM_ID + ":value_" + baseId;
	}
	
	
	
	/**
	 * 
	 * Creates a component based on the type of attribute
	 * 
	 * @param modelName The object name
	 * @param attributeName The name of the attribute in the object
	 * @param baseId The row id
	 * 
	 * @return A component valid to edit the attribute type
	 */
	private AttributeBase createComponentFromAttributeType(String modelName, String attributeName, Integer baseId){
		
		boDefHandler modelMetadata = boDefHandler.getBoDefinition( modelName );
		boDefAttribute attributeMetadata = modelMetadata.getAttributeRef( attributeName );
		
		AttributeBase result = createAttribute( getComponentTypeFromAttributeType( attributeMetadata ) );
		result.setMaxLength( 80 );
		
		if (! ( result instanceof AttributeText) ) //FIXME: Ugly, but if I don't do this value is not displayed in AttributeText
			result.setDisplayValue( "" );
		result.setMaxValue( Double.MAX_VALUE );
		changeSpecificSettings(result, attributeMetadata);
		
		String id = createIdFromParts( baseId, SUFFIX.ATTRIBUTE_VALUE );
		result.setId( id );
		result.setValueExpression( createActionExpression( getId(), "attributeValues." + id	) );
		setValueToFixBinding( result, id );
		
		
		this.componentIds.get( Integer.valueOf( baseId) ).setValueId( id );
		this.reverseComponentIds.put( id, Integer.valueOf( baseId) );
		
		return result;
	}

	/**
	 * 
	 * Set the value of the component to fix the binding (it only happened in attributeBoolean
	 * at the moment, but it should fix anything)
	 * 
	 * @param result The component to fix
	 * @param id The identifier of the row to retrieve the value and set in the component 
	 */
	private void setValueToFixBinding( AttributeBase result, String id ) {
		Object value  = attributeValues.get( id );
		result.setValue( value );
	}

	/**
	 * 
	 * Create an attribute based on a certain type
	 * 
	 * @param attributeType The attribute
	 * @return A componet of the given type
	 */
	private AttributeBase createAttribute(String attributeType) {
		AttributeBase result = ( AttributeBase ) new XUIViewerBuilder().createComponent( getRequestContext(), attributeType );
		return result;
	}

	//FIXME:
	private void changeSpecificSettings( AttributeBase result, boDefAttribute attributeMetadata ) {
		if (result instanceof GenericLookup){
			GenericLookup lookup = (GenericLookup) result;
			if (MetadataUtils.isLov( attributeMetadata )){
				lookup.setLookupCommand( createActionExpression( getId(), "doLov" ) );
			} else {
				lookup.setLookupCommand( createActionExpression( getId(), "doLookup" ) );
			}
		} else
			result.setOnChangeSubmit( true );
	}
	
	/**
	 * Retrieve the map with all the join operators
	 * @return
	 */
	public Map<String,String> getJoinOperators(){
		return mapLoader.get( OperatorMapLoader.JOIN_OPERATORS_ALL );
	}

	/**
	 * Retrieve the map with the join operators for the first row
	 */
	public Map<String,String> getFirstJoinOperators(){
		return mapLoader.get( OperatorMapLoader.JOIN_OPERATORS_INITIAL );
	}
	
	/**
	 * Find a corresponding component type from an attribute definition
	 * 
	 * @param attributeMetadata The attribute definition
	 * 
	 * @return A string with the attribute type (attributeText, genericLookup, etc...) 
	 */
	private String getComponentTypeFromAttributeType( boDefAttribute attributeMetadata ) {
		
		String inputType = "attributeText"; 
		if (MetadataUtils.isLov( attributeMetadata )){
			inputType = "genericLookup";
		} else {
			if (MetadataUtils.isTextualType( attributeMetadata ) )
				inputType = "attributeText";
			else if (MetadataUtils.isNumber( attributeMetadata ) )
				inputType = "attributeNumber";
			else if ( MetadataUtils.isDate( attributeMetadata ) )
				inputType = "attributeDate";
			else if (MetadataUtils.isObjectOrCollection( attributeMetadata ) )
				inputType = "genericLookup";
			else if ( MetadataUtils.isDateTime( attributeMetadata ) )
				inputType = "attributeDateTime";
			else if ( MetadataUtils.isBoolean( attributeMetadata ) )
				inputType = "attributeBoolean";
				
		}
		return inputType;
	}
	
	
	
	/**
	 * Retrieve a map with the value operators valid for a textual attribute 
	 */
	public Map<String,String> getOperatorsText(){
		return this.mapLoader.get( OperatorMapLoader.VALUE_OPERATORS_TEXT );
	}
	
	/**
	 * Retrieve a map with the value operators valid for a boolean/binary attribute 
	 */
	public Map<String,String> getOperatorsBoolean(){
		return this.mapLoader.get( OperatorMapLoader.VALUE_OPERATORS_BOOLEAN );
	}
	
	/**
	 * Retrieve a map with the value operators valid for a date/numeric attribute 
	 */
	public Map<String,String> getOperatorsDate(){
		return this.mapLoader.get( OperatorMapLoader.VALUE_OPERATORS_DATE );
	}
	
	/**
	 * Retrieve a map with the value operators valid for a long text 
	 */
	public Map<String,String> getOperatorsLongText(){
		return this.mapLoader.get( OperatorMapLoader.VALUE_OPERATORS_LONG_TEXT );
	}
	
	/**
	 * Retrieve a map with the value operators valid for an object/lov/bridge attribute 
	 */
	public Map<String,String> getOperatorsObject(){
		return this.mapLoader.get( OperatorMapLoader.VALUE_OPERATORS_OBJ );
	}
	
	/**
	 * Add a new empty row and refresh the viewer 
	 */
	public void addNewRowAndRefresh(){
		addNewRow();
		reRenderListOfRules();
	}
	
	/**
	 * Adds a new row without refreshing the viewer
	 */
	public void addNewRow(){
		int baseId = incrementCounter();
		addNewRow( baseId );
	}
	
	/**
	 * 
	 * Recreate existing filters (when opening the viewer after a first search has already been done)
	 * 
	 * @param it The filters to recreate
	 */
	private void reCreateExistingFilters(Iterator<Filter> it){
		
		while ( it.hasNext() ){
			Filter current = it.next();
		
			int baseId = incrementCounter();
			addNewRow( baseId );
			
			String attributeId = createIdFromParts( baseId, SUFFIX.ATTRIBUTE );
			String attributeName = current.getName();
			if (StringUtils.hasValue( attributeName )){
				attributes.put( attributeId, attributeName );
				setAttributeChooserComponentLabel( attributeId, attributeName );
			}
			
			String joinValueId = createIdFromParts( baseId, SUFFIX.JOIN_VALUE );
			String joinValue = current.getJoinOperator();
			joinOperatorValues.put( joinValueId, joinValue );
			
			String operatorValueId = createIdFromParts( baseId, SUFFIX.OPERATOR_VALUE );
			String operatorValue = current.getValueOperator();
			if (StringUtils.hasValue( operatorValue ))
				valueOperators.put( operatorValueId, operatorValue );
			
			String objectName = getObjectName();
			
			//Theres operatiosn only make sense when an attribute exists
			if ( StringUtils.hasValue( attributeName ) ){
				
				String objectNameParameter = getModelNameFromAttribute( attributeName );
				String attributeParameter = getTargetAttributeFromAttribute(objectName, attributeName);
				
				updateLovMapForOperators( attributeParameter, objectNameParameter , baseId );
				replaceAttributeValueChooserAndClearOldValue( attributeParameter, objectNameParameter, baseId );
				
				String attributeValueId = createIdFromParts( baseId, SUFFIX.ATTRIBUTE_VALUE );
				Object attributeValue = current.getValue();
				attributeValues.put( attributeValueId, attributeValue );
				
				setAttributeDisplayValue( attributeId, attributeName, attributeValueId, current.getValue() );
				
				String modelName = getModelNameFromAttribute(attributeName);
				lookupModelNames.put( attributeId, modelName );
			}
		}
		
	}
	
	private String getTargetAttributeFromAttribute( String objectName, String attributeName ) {
		if (attributeName.contains( "." )){
			return attributeName.split( "\\." )[1];
		}
		return attributeName;
	}

	
	/**
	 * 
	 * Retrieve the model name associated to a given attribute (necessary because
	 * of attributes such as "attName.att"
	 * 
	 * @param attributeName The name of the attribute (can be in the form "attName.att"
	 * 
	 * @return The name of the Model which is the parent of the attribute
	 */
	private String getModelNameFromAttribute( String attributeName ) {
		String modelName = getObjectName();
		if (attributeName.contains( "." )){
			boDefHandler handler = boDefHandler.getBoDefinition( modelName );
			String[] attributePair = attributeName.split( "\\." );
			String parentAttribute = attributePair[0];
			boDefAttribute refAttribute = handler.getAttributeRef( parentAttribute );
			modelName = refAttribute.getReferencedObjectName();
		}
		return modelName;
	}

	/**
	 * 
	 * Set the correct display value for a given attribute component
	 * 
	 * @param attributeId The identifier of the attribute component
	 * @param attributeName The name of the attribute
	 * @param attributeValueId The identifier of the attribute's value
	 * @param value The attribute's value
	 */
	private void setAttributeDisplayValue(String attributeId, String attributeName, String attributeValueId, Object value )
	{
		String modelName = getObjectName();
		UIComponent component = getViewRoot().findComponent( FORM_ID + ":" + attributeValueId);
		
		
		if (component != null && component instanceof GenericLookup){
			GenericLookup gl = (GenericLookup) component; 
			boDefHandler handler = boDefHandler.getBoDefinition( modelName );
			boDefAttribute refAttribute = null;
			
			if (attributeName.contains( "." )){
				String[] attributePair = attributeName.split( "\\." );
				String parentAttribute = attributePair[0];
				String childAttribute = attributePair[1];
				refAttribute = handler.getAttributeRef( parentAttribute );
				modelName = refAttribute.getReferencedObjectName();
				handler = boDefHandler.getBoDefinition( modelName );
				attributeName = childAttribute;
			} else {
				refAttribute = handler.getAttributeRef( attributeName );
			}
			
			if (MetadataUtils.isLov( refAttribute )){
				String displayValue = getLovDisplayValue( refAttribute.getLOVName(), value.toString() );
				gl.setDisplayValue( displayValue );
			} else if (MetadataUtils.isObjectOrCollection( refAttribute )){
				String currentValue = value.toString();
				List<String> listOfValues = createListFromCommaString( currentValue );
				String displayValue = getDisplayValueBridge( listOfValues );
				gl.setDisplayValue( displayValue );
			}
		}
		
	}
	
	/**
	 * 
	 * Creates a list of string from string with comma separated values
	 * 
	 * @param currentValue A string with values separated by comma
	 * @return A list of string
	 */
	private List<String> createListFromCommaString( String currentValue ) {
		List<String> result = new LinkedList<String>();
		String[] values = currentValue.split( "," );
		for ( String current : values ){
			if (StringUtils.hasValue( current )){
				result.add( current );
			}
		}
		return result;
	}

	/**
	 * 
	 * Sets the label to the component that allows to choose an attribute for a row (
	 * after the choice has been made)
	 * 
	 * @param attributeId The component identifier
	 * @param attributeName The name of the attribute
	 */
	private void setAttributeChooserComponentLabel( String attributeId, String attributeName ) {
		String modelName = getObjectName(); 
		GenericLookup gl = (GenericLookup) getViewRoot().findComponent( FORM_ID + ":" + attributeId);
		
		if (attributeName.contains( "." )){
			String[] attributePair = attributeName.split( "\\." );
			String parentAttribute = attributePair[0];
			String childAttribute = attributePair[1];
			
			boDefHandler handler = boDefHandler.getBoDefinition( modelName );
			boDefAttribute refAttribute = handler.getAttributeRef( parentAttribute );
			modelName = refAttribute.getReferencedObjectName();
			attributeName = childAttribute;
		}
		
		boDefHandler handler = boDefHandler.getBoDefinition( modelName );
		boDefAttribute attributeMetadata = handler.getAttributeRef( attributeName );
		gl.setDisplayValue( attributeMetadata.getLabel() );
	}
	
	
	/**
	 * 
	 * Adds a new row with a given identifier (does not refresh)
	 * 
	 * @param baseId The identifier
	 */
	private void addNewRow(int baseId){
		
		final Row newRow = new Row();
		
		AdvancedRowComponentIds componentIds = new AdvancedRowComponentIds();
		componentIds.setOrder( baseId );
		componentIds.setId( String.valueOf( baseId ) );
		
		this.componentIds.put( Integer.valueOf( baseId ), componentIds );
		
		Cell joinOperator = createJoinOperatorCell( baseId );
		newRow.addChild( joinOperator );
		
		Cell attributeCell = createAttributeCell( baseId );
		newRow.addChild( attributeCell );
		
		Cell valueOperatorCell = createValueOperatorCell( baseId );
		newRow.addChild( valueOperatorCell );
		
		Cell valueCell = createValueCell( baseId );
		newRow.addChild( valueCell );
		
		Cell newRowCell = createAddNewRowCell( baseId );
		newRow.addChild( newRowCell );
		
		Cell removeRowCell = createRemoveRowCell( baseId );
		newRow.addChild( removeRowCell );
		
		Rows parentRows = getRowsComponent(); 
		int position = getPositionToInsertRow();
		appendNewRow( parentRows, newRow, position );
		
		updateButtonOrder();
		
	}
	
	/**
	 * Removes an existing row
	 */
	public void removeRow(){
		Rows rows = getRowsComponent();
		
		RowInfo rowInfo = getRowInfoFromCurrentButtonAction();
		
		XUIActionEvent event = getRequestContext().getEvent();
		if ( event != null ){
			XUICommand command = ( XUICommand ) event.getSource();
			Row row = getParentRow( command );
			rows.getChildren().remove( row );
		}
		
		//Update order
		updateButtonOrder();
		
		//Remover dos Mapas os eventuais valores que lá tenham ficado
		Integer id = Integer.valueOf( rowInfo.getId() );
		AdvancedRowComponentIds rowToRemove = this.componentIds.remove( id );
		
		removeReferencesFromDeletedRow(rowToRemove);
		
		reRenderListOfRules();
	}
	
	
	/**
	 * Removes the values of each component present in a deleted tow
	 * 
	 * @param row The row deleted
	 */
	private void removeReferencesFromDeletedRow( AdvancedRowComponentIds row ) {
		this.attributes.remove( row.getAttributeId() );
		this.attributeValues.remove( row.getValueId() );
		this.joinOperatorValues.remove( row.getJoinId() );
		this.valueOperators.remove( row.getOperatorId() );
	}

	/**
	 * 
	 * Given a command (an add/remove button) retrieve the parent row where it lives 
	 * 
	 * @param command The command
	 * 
	 * @return The row where the command lives
	 */
	private Row getParentRow( XUICommand command ) {
		Row row = (Row) command.getParent().getParent();
		return row;
	}
	
	/**
	 * 
	 * Creates the Cell component wit the remove button for the current row
	 * 
	 * @param baseId The row identifier
	 * @return A cell with the remove row button
	 */
	private Cell createRemoveRowCell( int baseId ) {
		Cell cell = new Cell();
		
		if (baseId > 1){
			
			Menu button = new Menu();
			
			
			button.setServerAction( createActionExpression( getId() , "removeRow" ) );
			button.setId("deleteRow_" + baseId);
			
			
			RowInfo json = new RowInfo();
			json.setId( String.valueOf( baseId ) );
			
			button.setValue( json.toString() );
			button.setCommandArgument( json.toString() );
			
			String result = "XVW.AjaxCommand( 'search','search:" + button.getId() + "','" + json.toString() + "','1')"; 
			GenericTag tag = createTag( HTMLTag.IMG, HTMLAttr.SRC, "ext-xeo/images/menus/remover-bridge.gif" );
			tag.getProperties().put("onclick",
					result);
			
			cell.addChild( button );
			cell.addChild( tag );
			
		}
		return cell;
	}

	/**
	 * 
	 * Retrieve the only rows component in the viewer
	 * 
	 * @return The rows component
	 */
	private Rows getRowsComponent(){
		XUIViewRoot root = getViewRoot(); 
		UIComponent component = root.findComponent( "search:rows" );
		return (Rows) component; 
	}
	
	/**
	 * Check the existing row's order and updated it to a correct order
	 */
	private void updateButtonOrder(){
		Rows rows = getRowsComponent();
		List<UIComponent> listRow = rows.getChildren();
		int k = 1;
		for ( UIComponent current : listRow ){
			Row currentRow  = (Row) current;
			if (!isHeaderRow( currentRow ) ){
				updateCurrentButtonOrder(currentRow , k );
				k++;
			}
		}
	}

	/**
	 * 
	 * Checks if a given row is the header row
	 * 
	 * @param currentRow The row to check
	 * 
	 * @return True if the row is the header row and false otherwise
	 */
	private boolean isHeaderRow( Row currentRow ) {
		return "header".equalsIgnoreCase( currentRow.getId() );
	}

	private GenericTag createTag(XUIWriterElementConst name, XUIWriterAttributeConst property, String value){
		GenericTag newTag = new GenericTag();
		newTag.setProperties( new HashMap<String, String>() );
		newTag.getProperties().put( "__tagName", name.getValue() );
		newTag.getProperties().put( property.getValue()	, value );
		return newTag;
	}
	
	/**
	 * Update the order of a specific button
	 * 
	 * @param currentRow Parent row of the button
	 * @param order the new order
	 */
	private void updateCurrentButtonOrder( Row currentRow, int order ) {
		List<Menu> buttons = new LinkedList<Menu>();
		List<UIComponent> cells = currentRow.getChildren();

		final int CELL_WITH_ADD_BTN_INDEX = 4;
		final int CELL_WITH_REMOVE_BTN_INDEX = 5;
		
		Cell cellAdd = (Cell) cells.get( CELL_WITH_ADD_BTN_INDEX );
		Cell cellRemove = (Cell) cells.get( CELL_WITH_REMOVE_BTN_INDEX );
		
		Menu addBtn = (Menu) cellAdd.findComponent( Menu.class );
		Menu removeBtn = (Menu) cellRemove.findComponent( Menu.class );
		
		buttons.add( addBtn );
		if ( isSecondOrLaterRow( removeBtn ) )
			buttons.add( removeBtn );
		
		for (Menu button : buttons){
			if (button.getValue() != null){
				RowInfo row = new RowInfo(button.getValue().toString());
				int id = Integer.valueOf(row.getId());
				
				AdvancedRowComponentIds searchRow = componentIds.get( id );
				searchRow.setOrder( order );
				
				row.setOrder( order );
				
				button.setValue( row.toString() );
			}
		}
	}

	/**
	 * 
	 * Checks if an action button is in the second (or later, i.e. is not the header row ) row
	 * 
	 * @param removeBtn The button
	 * 
	 * @return True if the button is in the second
	 */
	private boolean isSecondOrLaterRow( Menu removeBtn ) {
		return removeBtn != null;
	}

	/**
	 * Refreshes the viewers and renders the current list
	 */
	private void reRenderListOfRules() {
		Panel section = (Panel) getViewRoot().findComponent( "search:conditions" );
		section.forceRenderOnClient();
	}
	
	/**
	 * 
	 * Retrieve the JSON properties of a button as a RowInfo object 
	 * 
	 * @return
	 */
	private RowInfo getRowInfoFromCurrentButtonAction(){
		Object value = null;
		XUIActionEvent event = getRequestContext().getEvent();
		if (event != null){
			XUICommand command = ( XUICommand ) event.getSource();
			value = command.getValue();
			if ( !StringUtils.isNull( value ) && !StringUtils.isEmpty( value.toString() ) ){
				RowInfo row = new RowInfo( value.toString() );
				return row;
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * Find where to insert a new row
	 * 
	 * @return the position where to insert a new row
	 */
	private int getPositionToInsertRow(){
		Object value = null;
		try{
			XUIActionEvent event = getRequestContext().getEvent();
			if (event != null){
				XUICommand command = ( XUICommand ) event.getSource();
				value = command.getValue();
				if ( !StringUtils.isNull( value ) && !StringUtils.isEmpty( value.toString() ) ){
					RowInfo row = new RowInfo( value.toString() );
					return row.getOrder();
				}
			}
		}
		catch (NumberFormatException e){
			logger.warn( "Could not parse " + value + " as a number", e );
		}
		return -1;
	}
	
	/**
	 * A append a new row to the parent rows at a specified position
	 * 
	 * @param parent The parent rows
	 * @param row The new row
	 * @param position The position to insert
	 */
	private void appendNewRow(Rows parent, Row row, int position){
		if (position == -1)
			parent.addChild( row );
		else
			parent.addChildAtIndex( row , position + 1 );
	}
	
	/**
	 * 
	 * Create the cell for the join operator component
	 * 
	 * @param baseId The row identifier
	 * 
	 * @return A cell component with the lov for the join operator
	 */
	private Cell createJoinOperatorCell( int baseId ){
		Cell cell = new Cell();
		
		String id = createIdFromParts( baseId, SUFFIX.JOIN_VALUE );
		
		Attribute attribute = new Attribute();
		attribute.setInputType( "attributeLov" );
		attribute.setRenderLabel( false );
		if (baseId <= 1)
			attribute.setLovMap( createActionExpression( getId(), "firstJoinOperators" ) );
		else
			attribute.setLovMap( createActionExpression( getId(), "joinOperators" ) );
		attribute.setValueExpression( createActionExpression( getId(), "joinOperatorValues." + id ) );
		attribute.setId( id );
		cell.addChild( attribute );
		
		this.componentIds.get( Integer.valueOf( baseId) ).setJoinId( id );
		this.reverseComponentIds.put( id, Integer.valueOf( baseId) );
		
		return cell;
	}
	
	/**
	 * 
	 * Create the cell for the attribute name component
	 * 
	 * @param baseId The row identifier
	 * 
	 * @return A cell component with the lookup for the attribute name
	 */
	private Cell createAttributeCell( int baseId ){
		Cell cell = new Cell();
		GenericLookup lookup = new GenericLookup();
		String id = createIdFromParts( baseId, SUFFIX.ATTRIBUTE );
		lookup.setId( id );
		lookup.setLookupCommand( createActionExpression( getId(), "chooseAttribute" ) );
		lookup.setValueExpression( createActionExpression( getId(), "attributes." + id	) );
		
		this.componentIds.get( Integer.valueOf( baseId) ).setAttributeId( id );
		this.reverseComponentIds.put( id, Integer.valueOf( baseId) );
		
		cell.addChild( lookup );
		
		return cell;
	}
	
	/**
	 * 
	 * Create the cell for the add new row component
	 * 
	 * @param baseId The row identifier
	 * 
	 * @return A cell component with button to add a new row
	 */
	private Cell createAddNewRowCell( int baseId ){
		Cell cell = new Cell();
		
		Menu button = new Menu();
		button.setServerAction( createActionExpression( getId() , "addNewRowAndRefresh" ) );
		button.setId("newRow_" + baseId);
		button.setServerActionWaitMode( "DIALOG" );
		RowInfo json = new RowInfo();
		json.setId( String.valueOf( baseId ) );
		json.setOrder( baseId );
		
		this.componentIds.get( Integer.valueOf( baseId) ).setOrder( baseId );
		this.componentIds.get( Integer.valueOf( baseId) ).setId( String.valueOf( baseId ) );
		
		button.setValue( json.toString() );
		button.setCommandArgument( json.toString() );
		
		cell.addChild( button );
		String result = "XVW.AjaxCommand( 'search','search:" + button.getId() + "','" + json.toString() + "','1')"; 
		GenericTag tag = createTag( HTMLTag.IMG, HTMLAttr.SRC, "ext-xeo/images/menus/add.png" );
		tag.getProperties().put("onclick",
				result);
		cell.addChild( tag );
		return cell;
	}
	
	/**
	 * 
	 * Create the cell for the value operator component
	 * 
	 * @param baseId The row identifier
	 * 
	 * @return An cell component with the lov for the value operator
	 */
	private Cell createValueOperatorCell( int baseId ){
		Cell cell = new Cell();
		cell.setId( "op_"+baseId );
		
		AttributeLov lov = new AttributeLov();
		String id = createIdFromParts( baseId, SUFFIX.OPERATOR_VALUE );
		lov.setId( id );
		lov.setValueExpression( createActionExpression( getId(), "valueOperators." + id ) );
		
		this.componentIds.get( Integer.valueOf( baseId) ).setOperatorId( id );
		this.reverseComponentIds.put( id, Integer.valueOf( baseId) );
		
		cell.addChild( lov );
		
		
		
		return cell;
	}
	
	/**
	 * Create an empty cell component for the value component 
	 * 
	 * @param baseId The row id
	 * @return An empty cell component
	 */
	private Cell createValueCell( int baseId ){
		Cell cell = new Cell();
		cell.setId( "value_"+baseId );
		return cell;
	}
	
	/**
	 * Compose a component id from its various parts
	 * 
	 * @param baseId The row base identifier
	 * @param suffix The suffix for the component
	 * 
	 * @return A string with the id of the component
	 */
	private String createIdFromParts(int baseId, SUFFIX suffix){
		return PREFIX + baseId + "_" + suffix.getLabel();
	}
	
	/**
	 * Create an EL Expression for its parts
	 * 
	 * @param beanId The bean identifier
	 * @param action The action to execute
	 * 
	 * @return An EL Expression
	 */
	private String createActionExpression(String beanId, String action){
		return "#{" + beanId + "." + action + "}" ;
	}

	/**
	 * Class representing a the ids of the components in a row (attribute name, join operator, etc..)
	 */
	private class AdvancedRowComponentIds implements Comparable<AdvancedRowComponentIds>{
		private String id;
		private int order;
		private String joinId;
		private String attributeId;
		private String operatorId;
		private String valueId;
		
		public String getId() {
			return id;
		}
		public void setId( String id ) {
			this.id = id;
		}
		
		public int getOrder(){
			return order;
		}
		
		public void setOrder(int order){
			this.order = order;
		}
		
		public String getJoinId() {
			return joinId;
		}
		public void setJoinId( String joinId ) {
			this.joinId = joinId;
		}
		public String getAttributeId() {
			return attributeId;
		}
		public void setAttributeId( String attributeId ) {
			this.attributeId = attributeId;
		}
		public String getOperatorId() {
			return operatorId;
		}
		public void setOperatorId( String operatorId ) {
			this.operatorId = operatorId;
		}
		
		public String getValueId() {
			return valueId;
		}
		public void setValueId( String valueId ) {
			this.valueId = valueId;
		}
		
		
		@Override
		public int compareTo( AdvancedRowComponentIds o ) {
			if (o.getOrder() == this.getOrder())
				return 0;
			else if (this.getOrder() < o.getOrder())
				return -1;
			else
				return 1;
		}
		
		@Override
		public boolean equals(Object o ){
			if (o instanceof AdvancedRowComponentIds){
				AdvancedRowComponentIds r = (AdvancedRowComponentIds) o;
				return
					r.getAttributeId().equals( getAttributeId() ) &&
					r.getId().equals( getId() ) &&
					r.getJoinId().equals( getJoinId() ) &&
					r.getOperatorId().equals( getOperatorId() ) &&
					r.getValueId().endsWith( getValueId() );
			}
			return false;
		}
	}
	
	/**
	 * Sorts the rows by their order
	 * 
	 * @return The sorted list
	 */
	private List<AdvancedRowComponentIds> sortRows(){
		List<AdvancedRowComponentIds> list = new LinkedList<AdvancedRowComponentIds>();
		Iterator<Entry<Integer,AdvancedRowComponentIds>> it = this.componentIds.entrySet().iterator();
		
		while (it.hasNext()){
			Entry<Integer,AdvancedRowComponentIds> curr = it.next();
			AdvancedRowComponentIds row = curr.getValue();
			list.add( row );
		}
		Collections.sort( list );
		
		return list;
	}
	
	
	/**
	 * 
	 * Compose the choices of the user for the search query a JSON object to be applied to the explorer
	 * 
	 * @return A JSON string with the filters chosen by the user
	 */
	public String getFiltersAsJSON(){
		
		List<AdvancedRowComponentIds> rows = sortRows();
		
		GridPanelJSonFiltersBuilder builder = new GridPanelJSonFiltersBuilder();
		
		for (AdvancedRowComponentIds row : rows){
			AttributeBase currentAttribute = (AttributeBase) getViewRoot().findComponent( FORM_ID + ":" + row.getValueId());
			Integer id = Integer.valueOf( row.getId() );
			
			String joinOperator = joinOperatorValues.get( row.getJoinId() );
			String attributeName = attributes.get( row.getAttributeId() );
			String valueOperator = valueOperators.get( row.getOperatorId() );
			Object valueContent = attributeValues.get( row.getValueId() );
			
			ValueType dataTypeOfAttribute = getTypeFromAttribute( currentAttribute );
			
			
			if (parentAttribute.containsKey( id )){
				attributeName = parentAttribute.get( id );
			}
			
			builder.addFilter( attributeName, dataTypeOfAttribute, joinOperator, valueOperator, valueContent ); 
			
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * Given a component, return a {@link ValueType} instance representing its type
	 * 
	 * @param currentAttribute The component
	 * 
	 */
	private ValueType getTypeFromAttribute( AttributeBase currentAttribute ) {
		if (currentAttribute instanceof AttributeText || currentAttribute instanceof AttributeTextArea)
			return ValueType.STRING;
		else if (currentAttribute instanceof AttributeNumber)
			return ValueType.NUMERIC;
		else if (currentAttribute instanceof AttributeNumberLookup)
			return ValueType.OBJECT;
		else if (currentAttribute instanceof GenericLookup){
			if (currentAttribute.getValue() == null)
				return ValueType.STRING;
			if (!currentAttribute.getValue().toString().contains( "," ))
				return ValueType.OBJECT; 
			else
				return ValueType.LIST; //FIXME Ugly as hell and does not work when only one is used (será que preciso de distinguir entre objecto e lista?
		}
		else if (currentAttribute instanceof AttributeDate || currentAttribute instanceof AttributeDateTime)
			return ValueType.DATE;
		else if (currentAttribute instanceof AttributeBoolean)
			return ValueType.BOOLEAN;
		return ValueType.NONE;
	}
	
	/**
	 * 
	 * Generate the BoQL Expression to display to the user, representing the filters
	 * we chose
	 * 
	 * @param rows The rows composing the users choice
	 * 
	 * @return A string with the BOQL expression
	 */
	private String generateBoqlExpression(List<AdvancedRowComponentIds> rows ){
		StringBuilder b = new StringBuilder(90);
		b.append("select ").append( getObjectName() ).append(" where ");
		
		
		for (AdvancedRowComponentIds row : rows){
			String operator = "";
			String attName = "";
			String valueOperator = "";
			
			String joinOperator = joinOperatorValues.get( row.getJoinId() );
			if (StringUtils.hasValue( joinOperator ) )
				operator = JOIN_OPERATOR.fromCode( joinOperator ).getLabel();
			
			AttributeBase attributeForName = (AttributeBase) getViewRoot().findComponent( FORM_ID + ":" + row.getAttributeId() );
			if (attributeForName != null){
				Object tmpValue = attributeForName.getValue();
				if (tmpValue != null){
					attName = tmpValue.toString();
				}
				else{
					attName = "";
				}
			}
			
			if (parentAttribute.containsKey( Integer.valueOf( row.getId() ) ) )
				attName = parentAttribute.get( Integer.valueOf( row.getId() ) );
			
			String valueOperatorFromMap = valueOperators.get( row.getOperatorId() );
			VALUE_OPERATOR op = null;
			if (valueOperatorFromMap != null){
				op = VALUE_OPERATOR.fromCode( valueOperatorFromMap );
				valueOperator = op.getLabel();
			}
				
			if ( StringUtils.hasValue( attName ) || StringUtils.hasValue( operator ) )
				b.append( operator ).append(" ").append( attName ).append(" ").append( valueOperator );
			if ( attributeValues.containsKey( row.getValueId() ) && op != null && op != VALUE_OPERATOR.CONTAINS_DATA && op != VALUE_OPERATOR.NOT_CONTAINS_DATA )
				b.append(" ? ");
		}
		
		return b.toString();
	}

	/**
	 * Generates a string with a friendly representation of the BOQL expression 
	 */
	public String getQueryResult(){
		StringBuilder b = new StringBuilder();
		
		List<AdvancedRowComponentIds> rows = sortRows();
		for (AdvancedRowComponentIds row : rows){
			String operator = "";
			String attName = "";
			String valueOperator = "";
			Object valueContent = "";
			
			
			String joinOperator = joinOperatorValues.get( row.getJoinId() );
			if (StringUtils.hasValue( joinOperator ) )
				operator = mapLoader.get( OperatorMapLoader.JOIN_OPERATORS_ALL ).get( joinOperator );
			
			AttributeBase attributeForName = (AttributeBase) getViewRoot().findComponent( FORM_ID + ":" + row.getAttributeId() );
			if (attributeForName != null){
				attName = attributeForName.getDisplayValue();
				if (StringUtils.isEmpty( attName ))
					attName = "";
			}
			
			String valueOperatorFromMap = valueOperators.get( row.getOperatorId() );
			if (valueOperatorFromMap != null)
				valueOperator = mapLoader.get( OperatorMapLoader.VALUE_OPERATORES_ALL ).get( valueOperatorFromMap ) ;
			
			Object con = attributeValues.get( row.getValueId() );
			if (con != null){
				
				String id = row.getAttributeId();
				AttributeBase oAtt = ( AttributeBase ) getViewRoot().findComponent( FORM_ID + ":" + id );
				String modelName = lookupModelNames.get( id );
				boDefHandler objectMetadata = boDefHandler.getBoDefinition( modelName );
				String correctAttributeName = getTargetAttributeFromAttribute( modelName, oAtt.getValue().toString() );
				boDefAttribute      oAttDef     = objectMetadata.getAttributeRef( correctAttributeName );
				
				if (MetadataUtils.isLov( oAttDef )){
					String lovName = oAttDef.getLOVName();
					valueContent = getLovDisplayValue( lovName, con.toString() );
				} else if (MetadataUtils.isObjectOrCollection( oAttDef )){
					valueContent = getDisplayValueObjectType( con.toString() );
				} else 
					valueContent = con.toString();
			}
			
			b.append(operator).append(" ").append(attName).append(" ").append(valueOperator).append(" ").append(valueContent.toString()).append(" ");
		}
		
		String query = b.toString();
		StringBuilder finalResult = new StringBuilder( BeansMessages.ADVANCED_SEARCH_SELECT.toString() ).append(" ")
			.append( boDefHandler.getBoDefinition( getObjectName() ).getLabel() ).append(" ")
			.append( BeansMessages.ADVANCED_SEARCH_WHERE.toString() ).append(" ")
			.append(query);
		return finalResult.toString();
	}

	private Object getDisplayValueObjectType( String string ) {
		assert string != null : "Cannot split null string"; 
		String[] splited = string.split( "," );
		StringBuilder b = new StringBuilder();
		String toAppend = "";
		for (String current : splited){
			if (StringUtils.hasValue( current )){
				b.append( toAppend );
				b.append( getDisplayValue( current ) );
				toAppend = ",";
			}
		}
		return b.toString();
	}
	
	
}
