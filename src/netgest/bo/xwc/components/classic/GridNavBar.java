package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class GridNavBar extends XUIComponentBase {

    XUIStateBindProperty<Boolean> showFullTextSearch = new XUIStateBindProperty<Boolean>( "showFullTextSearch", this, "true", Boolean.class );
    XUIStateBindProperty<Boolean> showExportToExcel = new XUIStateBindProperty<Boolean>( "showExportToExcel", this, "true", Boolean.class );
    XUIStateBindProperty<Boolean> showExportToPDF = new XUIStateBindProperty<Boolean>( "showExportToPDF", this, "true", Boolean.class );


    public void setShowFullTextSearch( String showFullTextSearch) {
        this.showFullTextSearch.setExpressionText( showFullTextSearch );
    }

    public boolean getShowFullTextSearch() {
        return showFullTextSearch.getEvaluatedValue();
    }

    public void setShowExportToExcel(String showExportToExcel) {
        this.showExportToExcel.setExpressionText( showExportToExcel );
    }

    public boolean getShowExportToExcel() {
        return showExportToExcel.getEvaluatedValue();
    }

    public void setShowExportToPDF(String showExportToPDF) {
        this.showExportToPDF.setExpressionText( showExportToPDF );
    }

    public boolean getShowExportToPDF() {
        return showExportToPDF.getEvaluatedValue();
    }


    public Object saveState() {
        return super.saveState();
    }

    public void restoreState(Object oState) {
        super.restoreState(oState);
    }
}
