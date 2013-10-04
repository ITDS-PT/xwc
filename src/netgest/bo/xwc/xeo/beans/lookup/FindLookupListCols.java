package netgest.bo.xwc.xeo.beans.lookup;

import java.util.List;

import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;

class FindLookupListCols {
	
	private List<XUIViewerDefinitionNode> rootElements;
	
	public FindLookupListCols(List<XUIViewerDefinitionNode> node){
		this.rootElements = node;
	}
	
	public List<XUIViewerDefinitionNode> getColns(){
		return recursiveSearch(rootElements);
	}
	
	
	private List<XUIViewerDefinitionNode> recursiveSearch(List<XUIViewerDefinitionNode> nodeList){
		List<XUIViewerDefinitionNode> result = null;
		for (int i = 0, k = nodeList.size(); i < k ; i++){
			XUIViewerDefinitionNode curr = nodeList.get(i);
			if (curr.getName().equalsIgnoreCase("xvw:columns"))
				return curr.getChildren();
			else{
				result = recursiveSearch(curr.getChildren());
				if (result != null)
					return result;
			}
		}
		return null;
		
		
		
		
	}

}
