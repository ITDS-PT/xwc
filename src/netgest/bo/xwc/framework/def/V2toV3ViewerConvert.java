package netgest.bo.xwc.framework.def;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Element;

public class V2toV3ViewerConvert {
	
	private StringBuffer log = null;

	public V2toV3ViewerConvert() {
		super();
		log = new StringBuffer();
	}

	public String getLog() {
		return log.toString();
	}

	private void createLogMessage(String newMessage) {
		this.log.append(newMessage+"\n");
	}

	public void convertPackage(String packageName, String basePath, String destPath) throws Exception {
		try {
			this.createLogMessage("PACKAGE : "+packageName);
			File packageFiles = new File(basePath+File.separator+packageName);
			for (int i = 0 ;i < packageFiles.listFiles().length;i++) {
				File currFile = packageFiles.listFiles()[i];
				if (currFile !=null && currFile.getName().toLowerCase().endsWith("model")) {
					this.convertBOModel(currFile,destPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CONVERTING_PACKAGE")+" : "+packageName);
		}
	}
	
	public void convertBOModel(File boModelFile, String destPath) throws Exception {
		try {
			this.createLogMessage("BO MODEL : "+boModelFile.getName());
			ngtXMLHandler boModel = new ngtXMLHandler(ngtXMLUtils.loadXMLFile(boModelFile.getAbsolutePath()));
			String boName = boModel.getChildNode("xeoModel").getChildNode("general").getAttribute("name");
			// get model from deployment dir 
			boModel = (new ngtXMLHandler( boDefHandler.getBoDefinition(boName).getNode() )).getParentNode();
			ngtXMLHandler[] forms = boModel.getChildNode("xeoModel").getChildNode("viewers").getChildNode("viewer").getChildNode("forms").getChildNodes();
			XMLDocument newViewerXML = null;
			
			File boFolder = new File(destPath+File.separator+boName);
			if (!boFolder.exists())
				boFolder.mkdir();
			for (int j=0;j<forms.length;j++){
				this.createLogMessage("VIEWER name = "+forms[j].getAttribute("name") + " type = " +forms[j].getAttribute("formtype"));

				if ( forms[j].getAttribute("forBridge") != null || forms[j].getChildNode("treeView") != null ) {
					this.createLogMessage("SKIPPED VIEWER REASON : "+(forms[j].getAttribute("forBridge")!=null?"forBridge":"") + " " + (forms[j].getChildNode("treeView")!=null?"treeView":""));
					continue;
				}
				String formName = forms[j].getAttribute("name");
				if(forms[j].getChildNode("explorer") != null){
					newViewerXML = this.createV3ViewerXML(this.createV3ViewerList(forms[j]));
				} else if(forms[j].getChildNode("grid") != null) {
					newViewerXML = this.createV3ViewerXML(this.createV3ViewerLookup(forms[j]));
				}else{
					newViewerXML = this.createV3ViewerXML(this.createV3ViewerEdit(forms[j]));
				}
				this.createNewViewerFile(newViewerXML,boFolder.getAbsolutePath(),this.getNewXVWViewerName(boName,formName));
			}
			if (boFolder.list().length == 0)
				boFolder.delete();
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CONVERTING_BO_MODEL")+" : "+boModelFile.getName());
		}
	}

	public void createNewViewerFile(XMLDocument newViewerXML, String destPath, String newViewerFileName) throws Exception {
		try{
			ngtXMLUtils.saveXML(newViewerXML,destPath+File.separator+newViewerFileName);
			this.createLogMessage(MessageLocalizer.getMessage("CREATE_VIEWER")+" : "+newViewerFileName); 
		}catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CREATING_VIEWER")+" : "+newViewerFileName); 
		}
	}
	
	public XUIViewerDefinition createV3ViewerEdit(ngtXMLHandler v2EditForm) throws Exception{
		XUIViewerDefinition viewerDef = new XUIViewerDefinition();
		XUIViewerDefinitionNode nodeForm = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodePanel = (new XUIViewerDefinitionNode());

		viewerDef.setViewerBean("netgest.bo.xwc.xeo.beans.XEOEditBean");
		viewerDef.setViewerBeanId("viewBean");

		nodeForm.setName("xeo:formEdit");
		nodePanel.setName("xvw:panel");

		viewerDef.setRootComponent(nodeForm);
		nodeForm.addChild(nodePanel);

		this.createV3ViewerEditFields(nodePanel,v2EditForm);
		
		return viewerDef;
	}

	public void createV3ViewerEditFields(XUIViewerDefinitionNode parentNode, ngtXMLHandler v2EditForm) throws Exception {
		try {
			// populate table
			Hashtable<String, ngtXMLHandler> objCollectionAttributes = new Hashtable<String, ngtXMLHandler>();
			ngtXMLHandler[] attributes = (new ngtXMLHandler(v2EditForm.getDocument())).getChildNode("xeoModel").getChildNode("attributes").getChildNodes();
			for (int i = 0; i < attributes.length; i++) {
				if (attributes[i].getNodeName().trim().equalsIgnoreCase("attributeObjectCollection"))
					objCollectionAttributes.put(attributes[i].getAttribute("name"), attributes[i]);
			}
			// populate table
			Hashtable<String, ngtXMLHandler> v2ListFormForBridges = new Hashtable<String, ngtXMLHandler>();
			ngtXMLHandler[] forms = (new ngtXMLHandler(v2EditForm.getDocument())).getChildNode("xeoModel").getChildNode("viewers").getChildNode("viewer").getChildNode("forms").getChildNodes();
			for (int j=0;j<forms.length;j++) {
				if(forms[j].getChildNode("grid") != null && forms[j].getAttribute("forBridge") != null) 
					v2ListFormForBridges.put(forms[j].getAttribute("forBridge"), forms[j]);
			}

			this.createV3ViewerEditNode(parentNode,v2EditForm,objCollectionAttributes,v2ListFormForBridges);
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CREATING_V3_EDIT_VIEWER")); 
		}
	}


	public void createV3ViewerEditNode(XUIViewerDefinitionNode parentNode, ngtXMLHandler v2FormNode, Hashtable<String,ngtXMLHandler> objCollectionAttributes, Hashtable<String, ngtXMLHandler> v2ListFormForBridges) throws Exception{
		// iterate children of XML node
		for (int i = 0; i < v2FormNode.getChildNodes().length; i++) {
			XUIViewerDefinitionNode newNode = new XUIViewerDefinitionNode();
			ngtXMLHandler v2FormNodeChild =  v2FormNode.getChildNodes()[i];

			// found bridge
			if (v2FormNodeChild.getText() != null 
					&& v2FormNodeChild.getText().indexOf(".") != -1
					&& objCollectionAttributes.get(v2FormNodeChild.getText().subSequence(0, v2FormNodeChild.getText().indexOf("."))) != null) {
				newNode = createV3ViewerEditBridgeNode(v2FormNodeChild, objCollectionAttributes.get(v2FormNodeChild.getText().substring(0, v2FormNodeChild.getText().indexOf("."))),v2ListFormForBridges);
			} else { // check for known tags
				if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("tab")) {
					//number of child tabs for this nodeXML //TODO think of better way			
					int nodeXMLTabsNum = 0;
					for (int j = 0; j < v2FormNode.getChildNodes().length; j++) {
						if (v2FormNode.getChildNodes()[j].getNodeName().trim().equalsIgnoreCase("tab"))
							nodeXMLTabsNum++;
					}
					// only create if  v2FormNode has more than 1 child tab
					if (nodeXMLTabsNum > 1) {
						//  must have "tags" as parent if not create parent before
						if (!parentNode.getName().equalsIgnoreCase("xvw:tabs")){
							newNode.setName("xvw:tabs");
							parentNode.addChild( newNode );
							parentNode = newNode;
							newNode = new XUIViewerDefinitionNode();
						} 
						newNode.setName("xvw:tab");
					}
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("section") 
						|| v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("div") ) {
					newNode.setName("xvw:section");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("areas") ) {
					newNode.setName("xvw:tabs");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("area") ) {
					newNode.setName("xvw:tab");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("rows") 
						|| v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("table")) {
					newNode.setName("xvw:rows");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("row") 
						|| v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("tr")) {
					newNode.setName("xvw:row");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("cell") 
						|| v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("td")) {
					newNode.setName("xvw:cell");
				} else if (v2FormNodeChild.getNodeName().trim().equalsIgnoreCase("attribute") ) {
					newNode.setName("xvw:attribute");
					newNode.setProperty("objectAttribute", v2FormNodeChild.getText());
				} 
			}// END check for known tags

			// new node was found
			if (newNode.getName() != null) {
				
				// interface found
				if (v2FormNodeChild.getAttribute("constraint") != null 
					&& v2FormNodeChild.getAttribute("constraint").toUpperCase().contains("INTERFACE:")) {
					String interfaceBOName = v2FormNodeChild.getAttribute("constraint").trim().substring(v2FormNodeChild.getAttribute("constraint").indexOf(":")+1);
					this.createV3ViewerEditFields(newNode,this.getBOForm(interfaceBOName,"edit"));
				}
				
				// add label if applicable 
				if (v2FormNodeChild.getAttribute("showlabel") == null || v2FormNodeChild.getAttribute("showlabel").equalsIgnoreCase("yes")) {
					
					if (v2FormNodeChild.getAttribute("bo_node") != null && objCollectionAttributes.get(v2FormNodeChild.getAttribute("bo_node")) != null) {
						//use label of bridge
						newNode.setProperty("label", objCollectionAttributes.get(v2FormNodeChild.getAttribute("bo_node")).getChildNode("label").getText());
						
					} else if (v2FormNodeChild.getAttribute("bo_node") != null && 
						(v2FormNodeChild.getAttribute("label") == null || v2FormNodeChild.getAttribute("label").trim().equalsIgnoreCase("node.label"))) {

						ngtXMLHandler[] categories = (new ngtXMLHandler(v2FormNode.getDocument())).getChildNode("xeoModel").getChildNode("viewers").getChildNode("viewer").getChildNode("categories").getChildNodes();
						newNode.setProperty("label", this.getNoNodeLabel(v2FormNodeChild.getAttribute("bo_node"),categories));
						
					} else if (v2FormNodeChild.getAttribute("label") != null) {
					
						newNode.setProperty("label", v2FormNodeChild.getAttribute("label"));
					}
				}
				if (newNode.getName().equalsIgnoreCase("xvw:tab") && newNode.getProperty("label") == null ) 
					newNode.setProperty("label", "");//TODO
				
				// add new node to viewer
				parentNode.addChild(newNode);
				createV3ViewerEditNode(newNode,v2FormNodeChild,objCollectionAttributes,v2ListFormForBridges);
			} else {
				//no node was created
				if (v2FormNodeChild.getNodeName().equalsIgnoreCase("include-frame"))
					this.createLogMessage("SKIPPED TAG : "+v2FormNodeChild.getNodeName());
				createV3ViewerEditNode(parentNode,v2FormNodeChild,objCollectionAttributes,v2ListFormForBridges);
			}
		}// END iterate children of XML node
	}
	
	private String getNoNodeLabel(String boNode, ngtXMLHandler[] categories) {
		String label = null;
		for (int j = 0; j < categories.length; j++) {
			if (boNode.trim().equalsIgnoreCase(categories[j].getAttribute("name").trim())) {
				label =  categories[j].getAttribute("label");
				break;
			} else if (boNode.contains(".") 
					&& boNode.toLowerCase().contains(categories[j].getAttribute("name").toLowerCase()) 
					&& categories[j].getChildNode("categories") != null){
				label = this.getNoNodeLabel(boNode.substring(boNode.indexOf(".")+1, boNode.length()),categories[j].getChildNode("categories").getChildNodes());
			}
		}
		return label;
	}

	public XUIViewerDefinitionNode createV3ViewerEditBridgeNode(ngtXMLHandler v2FormNode, ngtXMLHandler v2FormBridgeNode, Hashtable<String, ngtXMLHandler> v2ListFormForBridges) throws Exception{
		XUIViewerDefinitionNode bridgeNode = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode columnsNode = new XUIViewerDefinitionNode();
		String bridgeName = v2FormBridgeNode.getAttribute("name");
		String objName = v2FormBridgeNode.getChildNode("type").getText().substring(v2FormBridgeNode.getChildNode("type").getText().indexOf(".")+1, v2FormBridgeNode.getChildNode("type").getText().length());
		String listFormName = v2FormNode.getText().substring(v2FormNode.getText().indexOf(".")+1, v2FormNode.getText().length());

		//this.createLogMessage("FOUND BRIDGE : "+bridgeName+ " REFERENCING OBJECT : "+objName+" FOR BRIDGE FORM : "+(v2ListFormForBridges.get(bridgeName) !=null ? true : false)+" SKIPPED TAG : "+v2FormNode.getNodeName());

		bridgeNode.setName("xeo:bridge");
		bridgeNode.setProperty("bridgeName", bridgeName);
		columnsNode.setName("xvw:columns");

		bridgeNode.addChild(columnsNode);

		ngtXMLHandler v2ListForm = v2ListFormForBridges.get(bridgeName);

		// no list form forBridge, find form in referenced object
		if (v2ListForm == null) {
			v2ListForm = this.getBOForm(objName, listFormName);
		}

		// create bridge attributes
		ngtXMLHandler[] v2ListFormCols = v2ListForm.getChildNode("grid").getChildNode("cols").getChildNodes();
		for (int i = 0; i < v2ListFormCols.length; i++) {
			XUIViewerDefinitionNode nodeAttribute = new XUIViewerDefinitionNode();
			nodeAttribute.setName("xvw:columnAttribute");
			if (v2ListFormCols[i].getChildNode("attribute").getText().toLowerCase().contains("childobject"))
				nodeAttribute.setProperty("dataField", "SYS_CARDID");
			else
				nodeAttribute.setProperty("dataField", v2ListFormCols[i].getChildNode("attribute").getText());
			nodeAttribute.setProperty("width", v2ListFormCols[i].getAttribute("width"));

			columnsNode.addChild(nodeAttribute);
		}
		return bridgeNode;
	}

	public XUIViewerDefinition createV3ViewerLookup(ngtXMLHandler v2ListForm) throws Exception {
		XUIViewerDefinition viewerDef = new XUIViewerDefinition();
		XUIViewerDefinitionNode nodeForm = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodelookup = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeColumns = new XUIViewerDefinitionNode();

		viewerDef.setViewerBean("netgest.bo.xwc.components.beans.XEOBaseLookupList");
		viewerDef.setViewerBeanId("viewBean");

		nodeForm.setName("xeo:formLookupList");
		nodelookup.setName("xeo:lookupList");
		nodeColumns.setName("xvw:columns");

		viewerDef.setRootComponent(nodeForm);
		nodeForm.addChild(nodelookup);
		nodelookup.addChild(nodeColumns);

		try {
			ngtXMLHandler[] listFormCols = v2ListForm.getChildNode("grid").getChildNode("cols").getChildNodes();

			for (int i = 0; i < listFormCols.length; i++) {
				XUIViewerDefinitionNode nodeAttribute = new XUIViewerDefinitionNode();
				nodeAttribute.setName("xvw:columnAttribute");
				nodeAttribute.setProperty("dataField", listFormCols[i].getChildNode("attribute").getText());
				nodeAttribute.setProperty("width", listFormCols[i].getAttribute("width"));
				nodeColumns.addChild(nodeAttribute);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CREATING_V3_LOOKUP_VIEWER")); 
		}
		return viewerDef;
	}

	public XUIViewerDefinition createV3ViewerList(ngtXMLHandler v2ExplorerForm) throws Exception {
		XUIViewerDefinition viewerDef = new XUIViewerDefinition();
		XUIViewerDefinitionNode nodeForm = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodelist = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeColumns = new XUIViewerDefinitionNode();

		viewerDef.setViewerBean("netgest.bo.xwc.xeo.beans.XEOBaseList");
		viewerDef.setViewerBeanId("viewBean");

		nodeForm.setName("xeo:formList");
		nodelist.setName("xeo:list");
		nodeColumns.setName("xvw:columns");

		viewerDef.setRootComponent(nodeForm);
		nodeForm.addChild(nodelist);
		nodelist.addChild(nodeColumns);

		try {
			ngtXMLHandler[] explorerFormAttributes = v2ExplorerForm.getChildNode("explorer").getChildNode("attributes").getChildNodes();
			ngtXMLHandler[] explorerFormCols = v2ExplorerForm.getChildNode("explorer").getChildNode("cols").getChildNodes();
			Hashtable<String, ngtXMLHandler> v2FormAttributes = new Hashtable<String, ngtXMLHandler>();

			// populate table
			for (int i = 0; i < explorerFormAttributes.length; i++) {
				v2FormAttributes.put(explorerFormAttributes[i].getText(), explorerFormAttributes[i]);
			}

			for (int i = 0; i < explorerFormCols.length; i++) {
				// only fields in attributes (visible) go to new viewer
				if (v2FormAttributes.get(explorerFormCols[i].getChildNode("attribute").getText()) != null){
					XUIViewerDefinitionNode attributeNode = new XUIViewerDefinitionNode();
					attributeNode.setName("xvw:columnAttribute");
					attributeNode.setProperty("dataField", explorerFormCols[i].getChildNode("attribute").getText());
					attributeNode.setProperty("width", (v2FormAttributes.get(explorerFormCols[i].getChildNode("attribute").getText())).getAttribute("width"));
					nodeColumns.addChild(attributeNode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CREATING_V3_LIST_VIEWER")); 
		}
		return viewerDef;
	}

	public XMLDocument createV3ViewerXML(XUIViewerDefinition viewerDef) throws Exception {
		XMLDocument xmlDoc = new XMLDocument();
		Element rootNode = xmlDoc.createElement("xvw:root");
		Element viewerNode = xmlDoc.createElement("xvw:viewer");

		rootNode.setAttribute("xmlns:xvw", "http://www.netgest.net/xeo/xvw");
		rootNode.setAttribute("xmlns:xeo", "http://www.netgest.net/xeo/xeo");
		viewerNode.setAttribute("beanClass", viewerDef.getViewerBean());
		viewerNode.setAttribute("beanId", viewerDef.getViewerBeanId());
		
		xmlDoc.appendChild(rootNode);
		rootNode.appendChild(viewerNode);

		try {
			createV3ViewerXMLNode(viewerDef.getRootComponent(),viewerNode);
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CREATING_XML_FOR_V3_VIEWER")); 
		}
		return xmlDoc;
	}

	public void createV3ViewerXMLNode(XUIViewerDefinitionNode parentViewerNode,Element parentXMLnode) throws Exception {		
		Element newXMLNode = parentXMLnode.getOwnerDocument().createElement(parentViewerNode.getName());
		for (Iterator<Map.Entry<String, String>> iterator = parentViewerNode.getProperties().entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, String> pairs = (Map.Entry<String,String>)iterator.next();
			newXMLNode.setAttribute(pairs.getKey(), pairs.getValue()); 
		}
		if (parentViewerNode.getTextContent() != null )
			newXMLNode.appendChild(newXMLNode.getOwnerDocument().createTextNode(parentViewerNode.getTextContent()));
		
		parentXMLnode.appendChild(newXMLNode);

		for (Iterator<XUIViewerDefinitionNode> iterator = parentViewerNode.getChildren().iterator(); iterator.hasNext();) {
			XUIViewerDefinitionNode childViewerNode = (XUIViewerDefinitionNode) iterator.next();
			createV3ViewerXMLNode(childViewerNode,newXMLNode);
		}
	}
	
	public ngtXMLHandler getBOForm(String boName, String formName) throws Exception {
		ngtXMLHandler foundForm = null;
		boDefHandler def = null;
		ngtXMLHandler boXML = null;
		ngtXMLHandler[] forms = null;
		
		def = boDefHandler.getBoDefinition(boName);   
		boXML = (new ngtXMLHandler( def.getNode() )).getParentNode();

		forms = boXML.getChildNode("xeoModel").getChildNode("viewers").getChildNode("viewer").getChildNode("forms").getChildNodes();
		for (int k = 0; k < forms.length; k++) {
			if (forms[k].getAttribute("name").equalsIgnoreCase(formName)){
				foundForm = forms[k];
				//break; get last with name if more than one
			}
		}			
		return foundForm;
	}
	
	public String getNewXVWViewerName(String boName, String formName) throws Exception {
		String newViewerFileName = null;
		String newViewerType = null;
		String foundFormType = null;
		ngtXMLHandler boXML = null;
		ngtXMLHandler[] forms = null;
		ngtXMLHandler foundForm = null;
		int numListForms = 0;
		int numEditForms = 0;
		boolean mutipleFormsSameType = false;
		
		boXML = (new ngtXMLHandler( boDefHandler.getBoDefinition(boName).getNode() )).getParentNode();
		forms = boXML.getChildNode("xeoModel").getChildNode("viewers").getChildNode("viewer").getChildNode("forms").getChildNodes();
		
		//count forms of same type TODO think of better way
		for (int j=0;j<forms.length;j++){
			if (forms[j].getAttribute("name").trim().equalsIgnoreCase(formName))
				foundForm = forms[j];
		
			if ( forms[j].getAttribute("forBridge") != null 
					|| forms[j].getChildNode("treeView") != null
					|| forms[j].getChildNode("explorer") != null ) {
				continue;
			}
			if(forms[j].getChildNode("grid") != null) {
				numListForms++;
			}else{
				numEditForms++;
			} 
		}
		
		// explorer forms always have their name appended to the new file
		if(foundForm.getChildNode("explorer") != null){
			newViewerFileName = "list_"+formName+".xvw"; 
		} else {
			if(foundForm.getChildNode("grid") != null) {
				foundFormType = "list";
				newViewerType = "lookup";
				mutipleFormsSameType = numListForms > 1 ? true : false;
			}else{
				foundFormType = "edit";
				newViewerType = "edit";
				mutipleFormsSameType = numEditForms > 1 ? true : false;
			}

			// don't append form name if only one form of type or form has standard name (edit or list)
			if (!mutipleFormsSameType || formName.trim().equalsIgnoreCase(foundFormType))
				newViewerFileName = newViewerType+".xvw"; 
			else
				newViewerFileName = newViewerType+"_"+formName+".xvw"; 
		}

		return newViewerFileName;
	}
	
	public XUIViewerDefinition createTreeViewer(ngtXMLHandler treeXML) throws Exception {
		XUIViewerDefinition viewerDef = new XUIViewerDefinition();
		XUIViewerDefinitionNode nodeForm = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeMainLayout = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeTreePanel = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeScript = new XUIViewerDefinitionNode();
		XUIViewerDefinitionNode nodeStyle = new XUIViewerDefinitionNode();

		viewerDef.setViewerBean("netgest.bo.xwc.components.beans.XEOMainBean");
		viewerDef.setViewerBeanId("viewBean");

		nodeForm.setName("xvw:form");
		nodeForm.setProperty("id","formMain");
		nodeMainLayout.setName("xvw:mainLayout");
		nodeTreePanel.setName("xvw:treePanel");
		nodeTreePanel.setProperty("renderComponent","false");
		nodeTreePanel.setProperty("id","tree");
		nodeScript.setName("script");
		nodeScript.setTextContent("var treeName = 'Default ';");
		nodeStyle.setName("style");
		nodeStyle.setTextContent(".api-title{-x-system-font:none;color:#fff;font-family:tahoma,arial,sans-serif;font-size:12px;font-size-adjust:none;font-stretch:normal;font-style:normal;font-variant:normal;font-weight:normal;line-height:normal;margin-left:10px}#header{background:#1E4176 url(ext-xeo/images/hd-bg.gif) repeat-x scroll 0 0;border:0 none;padding-left:3px;padding-top:3px;padding-left:5px}");
		
		nodeForm.addChild(nodeMainLayout);
		nodeForm.addChild(nodeStyle);
		nodeForm.addChild(nodeScript);
		nodeForm.addChild(nodeTreePanel);
		
		viewerDef.setRootComponent(nodeForm);
	
		Hashtable<String, ngtXMLHandler> barsTable = new Hashtable<String, ngtXMLHandler>();
		Hashtable<String, ngtXMLHandler> uiTreeTable = new Hashtable<String, ngtXMLHandler>();

		try {
			for (int i = 0; i < treeXML.getChildNode("uiObjects").getChildNodes().length; i++) {
				ngtXMLHandler obj = treeXML.getChildNode("uiObjects").getChildNodes()[i];
				if (obj.getNodeName().equalsIgnoreCase("bar")){
					barsTable.put(obj.getAttribute("name"), obj);
				} else if (obj.getNodeName().equalsIgnoreCase("uiTree")) {
					uiTreeTable.put(obj.getAttribute("name"), obj);
				}
			}

			for (Iterator<ngtXMLHandler> iterator = barsTable.values().iterator(); iterator.hasNext();) {
				ngtXMLHandler bar = iterator.next();
				ngtXMLHandler tree = uiTreeTable.get(bar.getChildNode("content").getChildNode("tree").getAttribute("name")).getChildNode("tree");
				XUIViewerDefinitionNode newBarMenu = new XUIViewerDefinitionNode();

				newBarMenu.setName("xvw:menu");
				newBarMenu.setProperty("text", bar.getAttribute("description"));
				newBarMenu.setProperty("expanded", tree.getAttribute("gui_open"));

				for (int i = 0; i < tree.getChildNodes().length; i++) {
					if (tree.getChildNodes()[i].getNodeName().equalsIgnoreCase("optionLink")
							|| tree.getChildNodes()[i].getNodeName().equalsIgnoreCase("optionObject")
							|| tree.getChildNodes()[i].getNodeName().equalsIgnoreCase("optionFolder") )
						newBarMenu.addChild(this.createTreeViewerNode(tree.getChildNodes()[i]));
				}
				nodeTreePanel.addChild(newBarMenu);
			}
		} catch (Exception e) {
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CONVERTING_TREE"));
			e.printStackTrace();
		}
		return viewerDef;
	}

	public XUIViewerDefinitionNode createTreeViewerNode(ngtXMLHandler treeNode) throws Exception {
		XUIViewerDefinitionNode newMenuItem = new XUIViewerDefinitionNode();
		try {
			newMenuItem.setName("xvw:menu");
			newMenuItem.setProperty("text", treeNode.getChildNode("label").getCDataText());
			if (treeNode.getAttribute("img") != null && !treeNode.getAttribute("img").trim().equals(""))
				newMenuItem.setProperty("icon", treeNode.getAttribute("img"));

			if (treeNode.getNodeName().equalsIgnoreCase("optionLink")){
				newMenuItem.setProperty("target", "tab");
				newMenuItem.setProperty("serverAction", "#{viewBean.openLink}");
				newMenuItem.setProperty("value", treeNode.getChildNode("url").getCDataText());

			}else if (treeNode.getNodeName().equalsIgnoreCase("optionObject")){
				String boName = treeNode.getAttribute("object");
				String treeNodeMode = treeNode.getAttribute("mode");
				String formName = treeNode.getAttribute("form");
				String boql = null;
				String newViewerFileName = null;
				
				newMenuItem.setProperty("target", "tab");
				if (treeNodeMode.trim().equalsIgnoreCase("explorer")) {
					ngtXMLHandler form = getBOForm(boName,formName);
					boql = form.getChildNode("explorer").getChildNode("boql").getText().replace("'", "\\'");
					newViewerFileName = (formName==null||formName.trim().equals(""))? boName+"_list.xvw" : boName+"/"+this.getNewXVWViewerName(boName,formName);
					newMenuItem.setProperty("serverAction", "#{viewBean.listObject}");
					newMenuItem.setProperty("value", "{viewerName:'"+newViewerFileName+"', boql:'"+boql+"'}");
				} else if (treeNodeMode.trim().equalsIgnoreCase("list")  ) {
					boql = "select "+ boName + " where 1=1";
					newViewerFileName = (formName==null||formName.trim().equals(""))? boName+"_lookup.xvw" : boName+"/"+this.getNewXVWViewerName(boName,formName);
					newMenuItem.setProperty("serverAction", "#{viewBean.listObject}");
					newMenuItem.setProperty("value", "{viewerName:'"+newViewerFileName+"', boql:'"+boql+"'}");
				} else if (treeNodeMode.trim().equalsIgnoreCase("new")) { 
					newViewerFileName = (formName==null||formName.trim().equals(""))? boName+"_edit.xvw" : boName+"/"+this.getNewXVWViewerName(boName,formName);
					newMenuItem.setProperty("serverAction", "#{viewBean.createObject}");
					newMenuItem.setProperty("value", "{viewerName:'"+newViewerFileName+"', objectName:'"+boName+"'}");
				}
			} else if (treeNode.getNodeName().equalsIgnoreCase("optionFolder")) {
				newMenuItem.setProperty("expanded", treeNode.getAttribute("gui_open"));
				for (int j = 0; j < treeNode.getChildNode("childs").getChildNodes().length; j++) {
					ngtXMLHandler treeNodeChild = treeNode.getChildNode("childs").getChildNodes()[j];
					newMenuItem.addChild(this.createTreeViewerNode(treeNodeChild));
				}
			}
		} catch (Exception e) {
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CONVERTING_TREE_NODE")+": "+newMenuItem.getProperty("text"));
			e.printStackTrace();
		}
		return newMenuItem;
	}
	
	public void convertTrees(String basePath, String destPath) throws Exception {
		File currFile = null;
		try {
			File packageFiles = new File(basePath+File.separator);
			for (int i = 0 ;i < packageFiles.listFiles().length;i++) {
				currFile = packageFiles.listFiles()[i];
				if (currFile != null && currFile.getName().endsWith(".xml")) {
					this.createLogMessage("TREE FILE : "+currFile.getName());
					XUIViewerDefinition newViewer = this.createTreeViewer(new ngtXMLHandler(ngtXMLUtils.loadXMLFile(currFile.getAbsolutePath())));
					this.createNewViewerFile(this.createV3ViewerXML(newViewer),destPath,"Main_"+currFile.getName().replaceAll("[$]workplace.xml", ".xvw"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.createLogMessage(MessageLocalizer.getMessage("ERROR_CONVERTING_TREE_FILE")+" : "+currFile.getName());
		}
	}
	

}
