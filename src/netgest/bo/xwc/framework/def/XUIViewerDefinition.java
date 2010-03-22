package netgest.bo.xwc.framework.def;



public class XUIViewerDefinition
{
    protected String                   viewerBean;
    protected String                   viewerBeanId;
    
    protected String[] 				   localizationClasses;
    
    protected boolean				   btransient;

	protected XUIViewerDefinitionNode  rootComponent;
	
	protected String				   beforeRestoreViewPhase;
	protected String				   afterRestoreViewPhase;
	
	protected String				   beforeApplyRequestValuesPhase;
	protected String				   afterApplyRequestValuesPhase;
	
	protected String				   beforeUpdateModelPhase;
	protected String				   afterUpdateModelPhase;
	
	protected String				   beforeRenderPhase;
	protected String				   afterRenderPhase;
	
    public XUIViewerDefinition()
    {
        
    }
    
    /**
	 * @return the beforeRestoreViewPhase
	 */
	public String getBeforeRestoreViewPhase() {
		return beforeRestoreViewPhase;
	}



	/**
	 * @param beforeRestoreViewPhase the beforeRestoreViewPhase to set
	 */
	public void setBeforeRestoreViewPhase(String beforeRestoreViewPhase) {
		this.beforeRestoreViewPhase = beforeRestoreViewPhase;
	}



	/**
	 * @return the afterRestoreViewPhase
	 */
	public String getAfterRestoreViewPhase() {
		return afterRestoreViewPhase;
	}



	/**
	 * @param afterRestoreViewPhase the afterRestoreViewPhase to set
	 */
	public void setAfterRestoreViewPhase(String afterRestoreViewPhase) {
		this.afterRestoreViewPhase = afterRestoreViewPhase;
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
    }

    public String getViewerBean()
    {
        return viewerBean;
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
    }
}
