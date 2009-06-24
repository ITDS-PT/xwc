package netgest.bo.xwc.framework;


public interface XUITheme {

    public String getResourceBaseUri();
    public void addStyle( XUIStyleContext styleContext );
    public void addScripts( XUIScriptContext styleContext );
    public String getBodyStyle();
    public String getHtmlStyle();
    
}
