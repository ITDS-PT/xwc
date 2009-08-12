package netgest.bo.xwc.xeo.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewerConfig {
    
    private long     boui;
    private String   objectName;
    private String   viewerName;
    private String   boql;
    
    public ViewerConfig(JSONObject jsonObject) {
        try {
            
            if( jsonObject.has("boui") )
                this.boui = jsonObject.getLong("boui");
            if( jsonObject.has("objectName") )
                this.objectName = jsonObject.getString("objectName");
            if( jsonObject.has("viewerName") )
                this.viewerName = jsonObject.getString("viewerName");
            if( jsonObject.has("boql") )
                this.boql = jsonObject.getString("boql");
            
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBoui(long boui) {
        this.boui = boui;
    }

    public long getBoui() {
        return boui;
    }

    public void setViewerName(String viewerName) {
        this.viewerName = viewerName;
    }

    public String getViewerName() {
        return viewerName;
    }

    public void setBoql(String boql) {
        this.boql = boql;
    }

    public String getBoql() {
        return boql;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }
}
