package netgest.bo.xwc.components.classic;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Set the GridPanel navigation bar options
 * 
 * Example:
 * <code>
 * 	<xvw:gridPanel>
 * 		<xvw:gridNavBar howExportToPDF='false'>
 * 			
 * 		</xvw:gridNavBar>
 * </svw:gridPanel>
 * <!-- The code hiddens the button export to PDF -->
 * </code>
 * 
 * @author jcarreira
 *
 */
public class GridNavBar extends ToolBar {

    XUIStateBindProperty<Boolean> showFullTextSearch = 
    	new XUIStateBindProperty<Boolean>( "showFullTextSearch", this, "true", Boolean.class );
    XUIStateBindProperty<Boolean> showExportToExcel = 
    	new XUIStateBindProperty<Boolean>( "showExportToExcel", this, "true", Boolean.class );
    XUIStateBindProperty<Boolean> showExportToPDF = 
    	new XUIStateBindProperty<Boolean>( "showExportToPDF", this, "true", Boolean.class );

    /**
     * Set if the user can perform a fulltextsearch in the Grid
     * @param showFullTextSearch true/false or a {@link ValueExpression}
     */
    public void setShowFullTextSearch( String showFullTextSearch) {
        this.showFullTextSearch.setExpressionText( showFullTextSearch );
    }
    
    /**
     * Return the value of the showFullTextSearch property
     * @return true/false
     */
    public boolean getShowFullTextSearch() {
        return showFullTextSearch.getEvaluatedValue();
    }

    /**
     * Set if the user can export the results to Excel Worksheet
     * @param showExportToExcel true/false or a {@link ValueExpression}
     */
    public void setShowExportToExcel(String showExportToExcel) {
        this.showExportToExcel.setExpressionText( showExportToExcel );
    }

    /**
     * Return the value of the showExportToExcel property
     * @return true/false
     */
    public boolean getShowExportToExcel() {
        return showExportToExcel.getEvaluatedValue();
    }

    /**
     * Set if the user can export the results to PDF Document
     * @param showExportToPDF true/false or a {@link ValueExpression}
     */
    public void setShowExportToPDF(String showExportToPDF) {
        this.showExportToPDF.setExpressionText( showExportToPDF );
    }

    /**
     * Return the value of the showFullTextSearch property
     * @return true/false
     */
    public boolean getShowExportToPDF() {
        return showExportToPDF.getEvaluatedValue();
    }

}
