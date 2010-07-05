/**
 * 
 */
package netgest.bo.xwc.framework.def;

/**
 * 
 * Definition of a Renderer (the renderer is specific to a given family
 * and a given rendererType of a component)
 * 
 * @author Pedro Rio
 *
 */
public class XUIRendererDefinition {
	
	/**
	 * The family (group) of the component
	 */
	private String familyName;
	
	/**
	 * The target for the renderer (its type)
	 */
	private String rendererType;
	
	/**
	 * The name of the java class that will render the component
	 */
	private String className;


	public XUIRendererDefinition()
	{}


	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}


	/**
	 * @param familyName the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}


	/**
	 * @return the rendererType
	 */
	public String getRendererType() {
		return rendererType;
	}


	/**
	 * @param rendererType the rendererType to set
	 */
	public void setRendererType(String rendererType) {
		this.rendererType = rendererType;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}


	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	

}
