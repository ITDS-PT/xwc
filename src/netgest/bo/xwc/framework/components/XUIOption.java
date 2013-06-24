package netgest.bo.xwc.framework.components;


public class XUIOption extends XUIComponentBase
{
    private String label;
    private String image;
    private String acessKey;
    private String toolTip;

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setAcessKey(String acessKey)
    {
        this.acessKey = acessKey;
    }

    public String getAcessKey()
    {
        return acessKey;
    }

    public void setToolTip(String toolTip)
    {
        this.toolTip = toolTip;
    }

    public String getToolTip()
    {
        return toolTip;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImage()
    {
        return image;
    }

    public String getFamily()
    {
        return null;
    }
}
