package netgest.bo.xwc.xeo.beans;

import java.io.File;

public class XEOViewerResolver {
	
	public enum ViewerType {
		LIST,
		EDIT,
		LOOKUP
	}

	public String getViewer( String className, ViewerType type ) {
		String ret;
		switch( type ) {
			case LIST:
				//ret = "viewers/" + className + "/list.xvw";
				ret = className + "_list.xvw";
				break;
			case EDIT:
				ret = className + "_edit.xvw"; 
				//ret = "viewers/" + className + "/edit.xvw"; 
				break;
			case LOOKUP:
				ret = className + "_lookup.xvw"; 
				//ret = "viewers/" + className + "/lookup.xvw"; 
				break;
			default:
				ret = null;
		}
		return ret;
	}

}
