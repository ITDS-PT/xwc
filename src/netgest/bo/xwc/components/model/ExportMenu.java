package netgest.bo.xwc.components.model;

import netgest.bo.xwc.framework.XUIBindProperty;

/**
 * 
 * The Export Menu is a component to place in a Toolbar to customize
 * the export action to PDF, HTML or Excel format (which Extends the {@link Menu}Menu Component)
 * 
 * It basically allows a user to define a customized style sheet to export
 * in PDF, HTML or Excel, so that the default style sheet is overridden
 * 
 * Usage is:
 * 
 * <xvw:exportMenu
 *  text="Export HTML"
 * 	serverAction="#{viewBean.EXPORT_FUNCTION}"
 * 	styleSheet = "path/to/stylesheet.xsl"
 * >
 * </xvw:exportMenu>
 * 
 * EXPORT_FUNCTION = "exportHTML" / "exportPDF" / "exportExcel"
 * 
 * @author Pedro Rio
 *
 */
public class ExportMenu extends Menu 
{
	
	/**
	 * This proper will hold the name of the XSLT file to override
	 * the default style sheet
	 */
	private XUIBindProperty<String> styleSheet = 
		new XUIBindProperty<String>("styleSheet", this, String.class);
	
	/**
	 * Set of parameters to pass to the XSLT
	 */
	private XUIBindProperty<String> parameters =
		new XUIBindProperty<String>("parameters", this, String.class);
	
	public ExportMenu()
	{
		super();
	}
	
	public void setStyleSheet(String templateStyleSheetExpr)
	{
		this.styleSheet.setExpressionText(templateStyleSheetExpr);
	}
	
	public String getStyleSheet()
	{
		return this.styleSheet.getEvaluatedValue();
	}
	
	public void setParameters(String parametersExpr)
	{
		this.parameters.setExpressionText(parametersExpr);
	}
	
	public String getParameters()
	{
		return this.parameters.getEvaluatedValue();
	}
	
}
