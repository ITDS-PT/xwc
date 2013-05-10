package netgest.bo.xwc.framework.def;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import netgest.utils.StringUtils;



public class XUIViewerDefinition
{
    protected String                   viewerBean;
    protected String                   viewerBeanId;
    
    protected List<String>			   viewerBeanList;
    protected List<String>			   viewerBeanListId;
    
    protected String				   renderKitId;
    
    protected String[] 				   localizationClasses;
    
    protected boolean				   btransient;

	protected XUIViewerDefinitionNode  rootComponent;
	
	protected String				   onRestoreViewPhase;
	protected List<String>			   onRestoreViewPhaseList;
	protected String				   onCreateViewPhase;
	protected List<String>			   onCreateViewPhaseList;
	
	protected String				   beforeApplyRequestValuesPhase;
	protected List<String>			   beforeApplyRequestValuesList;
	protected String				   afterApplyRequestValuesPhase;
	protected List<String>			   afterApplyRequestValuesList;
	
	protected String				   beforeUpdateModelPhase;
	protected List<String>			   beforeUpdateModelPhaseList;
	protected String				   afterUpdateModelPhase;
	protected List<String>			   afterUpdateModelPhaseList;
	
	protected String				   beforeRenderPhase;
	protected List<String>			   beforeRenderPhaseList;
	protected String				   afterRenderPhase;
	protected List<String>			   afterRenderPhaseList;
	
	protected Timestamp				   dateLastUpdate;
	
    public XUIViewerDefinition()
    {
        viewerBeanList = new LinkedList<String>();
        viewerBeanListId = new LinkedList<String>();
        
        onRestoreViewPhaseList = new LinkedList<String>();
        onCreateViewPhaseList = new LinkedList<String>();
        
        beforeApplyRequestValuesList = new LinkedList<String>();
        afterApplyRequestValuesList = new LinkedList<String>();
        
        beforeUpdateModelPhaseList = new LinkedList<String>();
        afterUpdateModelPhaseList = new LinkedList<String>();
        
        beforeRenderPhaseList = new LinkedList<String>();
        afterRenderPhaseList = new LinkedList<String>();
    }
    
    
    
    
	/**
	 * @return the onRestoreViewPhase
	 */
	public String getOnRestoreViewPhase() {
		return onRestoreViewPhase;
	}

	/**
	 * @param onRestoreViewPhase the onRestoreViewPhase to set
	 */
	public void setOnRestoreViewPhase(String onRestoreViewPhase) {
		this.onRestoreViewPhase = onRestoreViewPhase;
	}

	public List<String> getOnRestoreViewPhaseList(){
		return onRestoreViewPhaseList;
	}
	
	public void addOnRestoreViewPhase(String action){
		if ( !StringUtils.isEmpty( action ) )
			onRestoreViewPhaseList.add( action );
	}
	
	public void addOnRestoreViewPhase( List<String> actions ){
		for ( String act : actions )
			addOnRestoreViewPhase( act );
	}
	
	/**
	 * @return the onCreateViewPhase
	 */
	public String getOnCreateViewPhase() {
		return onCreateViewPhase;
	}

	/**
	 * @param onCreateViewPhase the onCreateViewPhase to set
	 */
	public void setOnCreateViewPhase(String onCreateViewPhase) {
		this.onCreateViewPhase = onCreateViewPhase;
	}

	public List<String> getOnCreateViewPhaseList(){
		return onCreateViewPhaseList;
	}
	
	public void addOnCreateViewPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			onCreateViewPhaseList.add( action );
	}
	
	public void addOnCreateViewPhase( List<String> actions ){
		for ( String action : actions )
			addOnCreateViewPhase( action );
	}


	/**
	 * @return the beforeApplyRequestValuesPhase
	 */
	public String getBeforeApplyRequestValuesPhase() {
		return beforeApplyRequestValuesPhase;
	}

	/**
	 * @param beforeApplyRequestValuesPhase the beforeApplyRequestValuesPhase to set
	 */
	public void setBeforeApplyRequestValuesPhase(
			String beforeApplyRequestValuesPhase) {
		this.beforeApplyRequestValuesPhase = beforeApplyRequestValuesPhase;
	}
	
	public List<String> getBeforeApplyRequestValuesPhaseList(){
		return beforeApplyRequestValuesList;
	}
	
	public void addBeforeApplyRequestValuesPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			beforeApplyRequestValuesList.add( action );
	}

	public void addBeforeApplyRequestValuesPhase( List<String> actions ){
		for ( String action : actions) 
			addBeforeApplyRequestValuesPhase( action );
	}


	/**
	 * @return the afterApplyRequestValuesPhase
	 */
	public String getAfterApplyRequestValuesPhase() {
		return afterApplyRequestValuesPhase;
	}

	/**
	 * @param afterApplyRequestValuesPhase the afterApplyRequestValuesPhase to set
	 */
	public void setAfterApplyRequestValuesPhase(String afterApplyRequestValuesPhase) {
		this.afterApplyRequestValuesPhase = afterApplyRequestValuesPhase;
	}
	
	public List<String> getAfterApplyRequestValuesPhaseList(){
		return afterApplyRequestValuesList;
	}
	
	public void addAfterApplyRequestValuesPhase(String action){
		if ( !StringUtils.isEmpty( action ) )
			afterApplyRequestValuesList.add( action );
	}
	
	public void addAfterApplyRequestValuesPhase(List<String> actions){
		for ( String action : actions )
			addAfterApplyRequestValuesPhase( action );
	}

	


	/**
	 * @return the beforeUpdateModelPhase
	 */
	public String getBeforeUpdateModelPhase() {
		return beforeUpdateModelPhase;
	}

	/**
	 * @param beforeUpdateModelPhase the beforeUpdateModelPhase to set
	 */
	public void setBeforeUpdateModelPhase(String beforeUpdateModelPhase) {
		this.beforeUpdateModelPhase = beforeUpdateModelPhase;
	}
	
	public List<String> getBeforeUpdateModelPhaseList(){
		return beforeUpdateModelPhaseList;
	}
	
	public void addBeforeUpdateModelPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			beforeUpdateModelPhaseList.add( action );
	}

	public void addBeforeUpdateModelPhase( List<String> actions ){
		for ( String action : actions )
			addBeforeUpdateModelPhase( action );
	}


	/**
	 * @return the afterUpdateModelPhase
	 */
	public String getAfterUpdateModelPhase() {
		return afterUpdateModelPhase;
	}

	/**
	 * @param afterUpdateModelPhase the afterUpdateModelPhase to set
	 */
	public void setAfterUpdateModelPhase(String afterUpdateModelPhase) {
		this.afterUpdateModelPhase = afterUpdateModelPhase;
	}

	public List<String> getAfterUpdateModelPhaseList(){
		return afterUpdateModelPhaseList;
	}
	
	public void addAfterUpdateModelPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			afterUpdateModelPhaseList.add( action );
	}
	
	public void addAfterUpdateModelPhase( List<String> actions ){
		for ( String action : actions )
			addAfterUpdateModelPhase( action );
	}


	
	
	/**
	 * @return the beforeRenderPhase
	 */
	public String getBeforeRenderPhase() {
		return beforeRenderPhase;
	}

	/**
	 * @param beforeRenderPhase the beforeRenderPhase to set
	 */
	public void setBeforeRenderPhase(String beforeRenderPhase) {
		this.beforeRenderPhase = beforeRenderPhase;
	}
	
	public List<String> getBeforeRenderPhaseList(){
		return beforeRenderPhaseList;
	}
	
	public void addBeforeRenderPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			beforeRenderPhaseList.add( action );
	}

	public void addBeforeRenderPhase( List<String> actions ){
		for( String action : actions )
			addBeforeRenderPhase( action );
	}


	/**
	 * @return the afterRenderPhase
	 */
	public String getAfterRenderPhase() {
		return afterRenderPhase;
	}
	/**
	 * @param afterRenderPhase the afterRenderPhase to set
	 */
	public void setAfterRenderPhase(String afterRenderPhase) {
		this.afterRenderPhase = afterRenderPhase;
	}

	public List<String> getAfterRenderPhaseList(){
		return afterRenderPhaseList;
	}
	
	public void addAfterRenderPhase( String action ){
		if ( !StringUtils.isEmpty( action ) )
			afterRenderPhaseList.add( action );
	}
	
	public void addAfterRenderPhase( List<String> actions ){
		for ( String action : actions )
			addAfterRenderPhase( action );
	}


	public String[] getLocalizationClasses() {
		return localizationClasses;
	}

	public void setLocalizationClasses(String[] localizationClass) {
		this.localizationClasses = localizationClass;
	}

	public boolean isTransient() {
		return btransient;
	}

    public void setTransient(boolean btransient) {
		this.btransient = btransient;
	}

    public void setViewerBean(String viewerBean)
    {
        this.viewerBean = viewerBean;
        addViewerBean( viewerBean );
    }

    public String getViewerBean()
    {
        return viewerBean;
    }
    
    public void addViewerBean( String viewerBean ){
    	this.viewerBeanList.add( viewerBean );
    }
    
    public void addViewerBeanId( String viewerBeanId ){
    	this.viewerBeanListId.add( viewerBeanId );
    }
    
    public List<String> getViewerBeans(){
    	return viewerBeanList; 
    }
    
    public List<String> getViewerBeanIds(){
    	return viewerBeanListId; 
    }
    
    public String getViewerBeanId() {
        if ( viewerBeanId == null ) {
            return viewerBean;
        }
        return viewerBeanId;
    }

    public void setRootComponent(XUIViewerDefinitionNode rootComponent)
    {
        this.rootComponent = rootComponent;
    }

    public XUIViewerDefinitionNode getRootComponent()
    {
        return rootComponent;
    }

    public void setViewerBeanId(String viewerBeanId) {
        this.viewerBeanId = viewerBeanId;
        addViewerBeanId( viewerBeanId );
    }




	/**
	 * @return the renderKitId
	 */
	public String getRenderKitId() {
		return renderKitId;
	}




	/**
	 * @param renderKitId the renderKitId to set
	 */
	public void setRenderKitId(String renderKitId) {
		this.renderKitId = renderKitId;
	}
	
	
	/**
	 * 
	 * Checks if a given component (prefix + name) exists in the
	 * component tree
	 * 
	 * @return True if the component exists and false otherwise
	 */
	public boolean hasComponent(String name, String prefix){
		return hasComponentRecursive(name, prefix, rootComponent);
	}
	
	public Timestamp getDateLastUpdate() {
		return dateLastUpdate;
	}

	public void setDateLastUpdate(Timestamp dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}




	private boolean hasComponentRecursive(String name, String prefix, XUIViewerDefinitionNode node){
		if (node.getName().equals(prefix + ":" + name))
			return true;
		List<XUIViewerDefinitionNode> children = node.getChildren();
		for (XUIViewerDefinitionNode child : children){
			return hasComponentRecursive(name, prefix, child);
		}
		return false;
	}
	
	/**
	 * 
	 * Checks if a given component within the component tree contains within its the name
	 * the given name passes as parameter, for instance, searching for form would find
	 * "xvw:form" and "xeo:formEdit"
	 * 
	 * @return True if any component has "name" as part of its name
	 */
	public boolean hasComponentContaningName(String name){
		return hasComponentContainingNameRecursive(name, rootComponent);
	}
	
	private boolean hasComponentContainingNameRecursive(String name, XUIViewerDefinitionNode node){
		if (node.getName().contains(name))
			return true;
		List<XUIViewerDefinitionNode> children = node.getChildren();
		for (XUIViewerDefinitionNode child : children){
			return hasComponentContainingNameRecursive(name, child);
		}
		return false;
	}




	/**
	 * @param newBean
	 * @param beanId
	 */
	public void replaceBean( String newBean, String beanId ) {
		this.viewerBeanList.clear();
		this.viewerBeanListId.clear();
		this.addViewerBean( newBean );
		this.addViewerBeanId( beanId );
	}
	
}
