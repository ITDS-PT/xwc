package netgest.bo.xwc.components.connectors.helper;

import java.util.ArrayList;
import java.util.List;

public class CardIDSearch {
	
	private String boqlExpression;
	private List<Object> parameters;
	
	public static final CardIDSearch NULL = new CardIDSearch( "" , new ArrayList< Object >() );
	
	public CardIDSearch(String boqlExpression, List< Object > parameters) {
		this.boqlExpression = boqlExpression;
		this.parameters = parameters;
	}
	
	public String getBoqlExpression() {
		return boqlExpression;
	}
	
	public void setBoqlExpression(String boqlExpression) {
		this.boqlExpression = boqlExpression;
	}
	
	public List< Object > getParameters() {
		return parameters;
	}
	
	public void setParameters(List< Object > parameters) {
		this.parameters = parameters;
	}
	
	

	
}
