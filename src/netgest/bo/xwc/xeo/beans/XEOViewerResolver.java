package netgest.bo.xwc.xeo.beans;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;


public class XEOViewerResolver {
	
	public enum ViewerType {
		LIST,
		EDIT,
		LOOKUP,
		PREVIEW
	}
	
	public String getViewer( boObject forObject, ViewerType type ) {
		return getViewer( forObject.getName(), type  );
	}

	public String getViewer( boObject forObject, ViewerType type, boDefAttribute source ) {
		return getViewer( forObject.getName(), type, source );
	}
	
	public String getViewer( boObject forObject, ViewerType type, String source ) {
		return getViewer( forObject.getName(), type, source );
	}
	
	
	public String getViewer( String className, ViewerType type){
		return getViewer( className , type , "" );
	}
	
	public String getViewer( String className, ViewerType type, boDefAttribute source){
		return getViewer( className , type , source.getName() );
	}

	public String getViewer( String className, ViewerType type, String source ) {
		String ret;
		switch( type ) {
			case LIST:
				ret = "viewers/" + className + "/list.xvw";
				//ret = className + "_list.xvw";
				break;
			case EDIT:
				//ret = className + "_edit.xvw"; 
				ret = "viewers/" + className + "/edit.xvw"; 
				break;
			case LOOKUP:
				//ret = className + "_lookup.xvw"; 
				ret = "viewers/" + className + "/lookup.xvw"; 
				break;
			case PREVIEW:
				//ret = className + "_edit.xvw"; 
				ret = "viewers/" + className + "/edit.xvw"; 
				break;
			default:
				ret = null;
		}
		return ret;
	}

}
