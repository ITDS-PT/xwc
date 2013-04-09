package netgest.bo.xwc.components.connectors;

public class LovValues {
	
	private Object[] values;
	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public String[] getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	private String[] descriptions;
	
	public LovValues( Object[] values, String[] descriptions ) {
		this.values 		= values;
		this.descriptions 	= descriptions;
	}
	

}
