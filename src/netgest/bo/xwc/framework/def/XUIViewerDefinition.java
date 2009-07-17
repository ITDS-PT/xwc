package netgest.bo.xwc.framework.def;



public class XUIViewerDefinition
{
    protected String                   viewerBean;
    protected String                   viewerBeanId;
    
    protected String[] 				   localizationClasses;
    
    protected boolean				   btransient;

	protected XUIViewerDefinitionNode  rootComponent;
    
    public XUIViewerDefinition()
    {
        
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
