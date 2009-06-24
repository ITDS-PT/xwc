package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIOutput;

public class ProgressBar extends XUIOutput {

	private XUIStateBindProperty<Integer>  minValue 		= new XUIStateBindProperty<Integer>( "minValue", this, "0", Integer.class );
	private XUIStateBindProperty<Integer>  maxValue 		= new XUIStateBindProperty<Integer>( "maxValue", this, "100", Integer.class );

	private XUIStateBindProperty<String>   width 			= new XUIStateBindProperty<String>( "width", this, "auto", String.class );

	private XUIStateBindProperty<Integer>  updateInterval 	= new XUIStateBindProperty<Integer>( "updateInterval", this, "5000", Integer.class );

	public int getUpdateInterval() {
		return updateInterval.getEvaluatedValue();
	}

	public void setUpdateInterval(String updateIntervalExpr ) {
		this.updateInterval.setExpressionText( updateIntervalExpr );
	}

	public String getWidth() {
		return width.getEvaluatedValue();
	}

	public void setWidth(String widthExpr ) {
		this.width.setExpressionText( widthExpr  );
	}

	public Integer getMinValue() {
		return minValue.getEvaluatedValue();
	}

	public void setMinValue(String minValueExpr ) {
		this.minValue.setExpressionText( minValueExpr );
	}

	public int getMaxValue() {
		return maxValue.getEvaluatedValue();
	}

	public void setMaxValue(String maxValueExpr ) {
		this.maxValue.setExpressionText( maxValueExpr );
	}

	public void setValueExpression( String sValueExpression ) {
		super.setValueExpression( "value" ,  createValueExpression( sValueExpression, Integer.class ));
	}
	
	public static class XEOHTMLRenderer {
		
		
	}

}
