package netgest.bo.xwc.components.classic.jsf;

import javax.faces.component.html.HtmlOutputText;

public class OutputText extends HtmlOutputText
{

	public OutputText() {
		super();
	}
	
	@Override
	public String getRendererType() 
	{
		return "outputText";
	}

	@Override
	public String getFamily()
	{
		return "outputText";
	}

}
