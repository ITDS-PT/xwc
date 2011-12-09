package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;

/**
 * Componente Tree Shuttle para selecção de colunas
 * 
 * @author mlarsen
 * 
 */
public class TreeShuttle extends AttributeBase {
	/**
	 * Lista de colunas a mostrar
	 */	
	private XUIBindProperty<String> columnAvailableList = new XUIBindProperty<String>(
			"columnAvailableList", this, String.class,
			"#{viewBean.columnsAvailable}");

	private XUIBindProperty<String> columnSelectedList = new XUIBindProperty<String>(
			"columnSelectedList", this, String.class,
			"#{viewBean.columnSelected}");	
	
	private XUIBindProperty<String> parentWindowId = new XUIBindProperty<String>(
			"parentWindowId", this, String.class,
			"#{viewBean.parentWindowId}");

	private XUIBindProperty<String> parentComponentId = new XUIBindProperty<String>(
			"parentComponentId", this, String.class,
			"#{viewBean.parentComponentId}");
	
	private XUIBindProperty<String> submitActionHandler = new XUIBindProperty<String>(
			"submitActionHandler", this, String.class,
			null);
	
	private XUIBindProperty<String> cancelActionHandler = new XUIBindProperty<String>(
			"cancelActionHandler", this, String.class,
			null);
	
	public void setParentComponentId(String beanExpr) {
		this.parentComponentId.setExpressionText(beanExpr);
	}

	public String getParentComponentId() {
		return this.parentComponentId.getEvaluatedValue();
	}
	
	public void setParentWindowId(String beanExpr) {
		this.parentWindowId.setExpressionText(beanExpr);
	}

	public String getParentWindowId() {
		return this.parentWindowId.getEvaluatedValue();
	}
	
	public void setColumnAvailableList(String beanExpr) {
		this.columnAvailableList.setExpressionText(beanExpr);
	}

	public String getColumnAvailableList() {
		return this.columnAvailableList.getEvaluatedValue();
	}

	public void setColumnSelectedList(String beanExpr) {
		this.columnSelectedList.setExpressionText(beanExpr);
	}

	public String getColumnSelectedList() {
		return this.columnSelectedList.getEvaluatedValue();
	}
	
	public void setSubmitActionHandler(String beanExpr) {
		this.submitActionHandler.setExpressionText(beanExpr);
	}

	public String getSubmitActionHandler() {
		return this.submitActionHandler.getEvaluatedValue();
	}
	
	public void setCancelActionHandler(String beanExpr) {
		this.cancelActionHandler.setExpressionText(beanExpr);
	}

	public String getCancelActionHandler() {
		return this.cancelActionHandler.getEvaluatedValue();
	}
	
	@Override
	public void initComponent() {
	}

	public static final class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {

			TreeShuttle oAttr = (TreeShuttle) oComp;
			XUIResponseWriter w = getResponseWriter();
			
			w.startElement(TABLE, oComp);
			w.writeAttribute(CELLPADDING, "0", null);
			w.writeAttribute(CELLSPACING, "0", null);

			w.startElement("COLGROUP", oAttr);
			w.startElement(COL, oAttr);
			w.writeAttribute(HTMLAttr.WIDTH, "45%", null);
			w.endElement("COL");
			w.startElement(COL, oAttr);
			w.writeAttribute(HTMLAttr.WIDTH, "10%", null);
			w.endElement("COL");
			w.startElement(COL, oAttr);
			w.writeAttribute(HTMLAttr.WIDTH, "45%", null);
			w.endElement("COL");
			w.endElement("COLGROUP");
			w.startElement(TR, oAttr);
			w.startElement(TD, oAttr);	
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_tree1", null);
			w.writeAttribute(
							HTMLAttr.STYLE,
							"float:right;margin:10px;border:1px solid #c3daf9;overflow:auto",
							null);
			w.endElement(HTMLTag.DIV);				
			w.endElement(TD);	
			w.startElement(TD, oAttr);	
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-div1r", null);
			w
					.writeAttribute(
							HTMLAttr.STYLE,
							"float:middle;margin:10px;border:1px solid #c3daf9",
							null);
			w.endElement(HTMLTag.DIV);
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-div1l", null);
			w
					.writeAttribute(
							HTMLAttr.STYLE,
							"float:middle;margin:10px;border:1px solid #c3daf9",
							null);
			w.endElement(HTMLTag.DIV);			
			
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-divAr", null);
			w
					.writeAttribute(
							HTMLAttr.STYLE,
							"float:middle;margin:10px;border:1px solid #c3daf9",
							null);
			w.endElement(HTMLTag.DIV);			
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-divAl", null);
			w
					.writeAttribute(
							HTMLAttr.STYLE,
							"float:middle;margin:10px;border:1px solid #c3daf9",
							null);
			w.endElement(HTMLTag.DIV);			
			w.startElement(HTMLTag.DIV, oAttr);
			w.endElement(TD);	
			w.startElement(TD, oAttr);	
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "_tree2", null);
			w
					.writeAttribute(
							HTMLAttr.STYLE,
							"float:right;margin:10px;border:1px solid #c3daf9;overflow:auto",
							null);
			w.endElement(HTMLTag.DIV);
			w.endElement(TD);
			w.endElement(TR);
			w.endElement(TABLE);			
			w.startElement(TABLE, oComp);
			w.writeAttribute(CELLPADDING, "0", null);
			w.writeAttribute(CELLSPACING, "0", null);
			w.startElement("COLGROUP", oAttr);
			w.startElement(COL, oAttr);
			w.writeAttribute(HTMLAttr.WIDTH, "90%", null);
			w.endElement("COL");
			w.startElement(COL, oAttr);
			w.writeAttribute(HTMLAttr.WIDTH, "10%", null);
			w.endElement("COL");
			w.endElement("COLGROUP");
			w.startElement(TR, oAttr);
			w.startElement(TD, oAttr);	
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-ok", null);
			w
			.writeAttribute(
					HTMLAttr.STYLE,
					"float:right;margin:5px;border:1px solid #c3daf9;overflow:auto",
					null);
			w.endElement(HTMLTag.DIV);
			w.endElement(TD);
			w.startElement(TD, oAttr);
			w.startElement(HTMLTag.DIV, oAttr);
			w.writeAttribute(HTMLAttr.ID, oComp.getClientId() + "button-cancel", null);
			w
			.writeAttribute(
					HTMLAttr.STYLE,
					"float:right;margin:10px;border:1px solid #c3daf9;overflow:auto",
					null);
			w.endElement(HTMLTag.DIV);
			w.endElement(TD);
			w.endElement(TR);
			w.endElement(TABLE);
			
			
			// O Componente Ext-js
			StringBuffer extComp = new StringBuffer();
			
			extComp.append("function toggleCheck(node,isCheck)\n");
			extComp.append("{\n");
			extComp.append("	if(node)\n");
			extComp.append("	{\n");
			extComp.append("		node.expand();\n");
			extComp.append("		node.eachChild(function(n) {\n");
			extComp.append("		n.eachChild(function(n1) {\n");
			extComp.append("			toggleCheck(n1, isCheck);\n");
			extComp.append("		});\n");
			extComp.append("		n.getUI().toggleCheck(isCheck);\n");
			extComp.append("		this.attributes.checked = isCheck;\n");
			extComp.append("		});\n");
			extComp.append("	}\n");
			extComp.append("}\n");
			
			extComp.append("var Children = new Array(); ");
			
			extComp.append("function move(nodeSrc,nodeDest,rootSrc, rootDest,flag)\n");
			extComp.append("{\n");
			extComp.append("	if(flag == 'A') {\n");
			extComp.append("		toggleCheck(rootSrc, true);\n");
			extComp.append("	}\n");
			extComp.append("    Children = nodeSrc.getChecked(); \n");
			extComp.append("    var limit = Children.length; \n");
			extComp.append("    for (count =0; count < limit; count++){ \n");		
			extComp.append("    	var node = Children[count];");
			extComp.append("    	if(node != rootSrc)\n");
			extComp.append("    	{\n");
			extComp.append(" 			rootDest.appendChild(node); \n");
			extComp.append("    	}\n");
			extComp.append("    }\n");	
			extComp.append("}\n");
			
			extComp.append("Ext.onReady(function() {\n");
			extComp.append("     var Tree = Ext.tree;\n");

			// Dados Tree1
			extComp.append("	var v_local_tree1_data = [");
			extComp.append(oAttr.getColumnAvailableList());
			extComp.append("	];\n");

			// Dados Tree2
			extComp.append("	var v_local_tree2_data = [");
			extComp.append(oAttr.getColumnSelectedList());
			extComp.append("	];\n");

			// Primeira Tree
			extComp.append("	var tree = new Tree.TreePanel({\n");
			extComp.append("                rootVisible:false, \n");
			extComp.append("                animate:true, \n");
			extComp.append("                title:'"+XEOComponentMessages.TREE_SHUTLE_AVAILABLE.toString()+"', \n");
			extComp.append("                frame:true, \n");
			extComp.append("                bodyBorder:false, \n");
			extComp.append("                bodyStyle:'background-color:white;', \n");
			extComp.append("                autoScroll:true,\n");
			extComp.append("            	enableDD:true,\n");
			extComp.append("                containerScroll: false,\n");
			extComp.append("                border: false,\n");
			extComp.append("                width: 255,\n");
			extComp.append("                height: 300,\n");
			extComp.append("                dropConfig: {appendOnly:false},\n");					
			extComp.append("				listeners: {\n");
			extComp.append("    				'checkchange': function(node, checked){\n");
			extComp.append("						if(checked) {\n");
			extComp.append("        					toggleCheck(node, true); \n");
			extComp.append(" 						}\n");
			extComp.append("						else {\n");
			extComp.append("        					toggleCheck(node, false); \n");
			extComp.append(" 						}\n");
			extComp.append("    				}\n");
			extComp.append("				}\n");	
			extComp.append("            });\n");

			// Nó raiz
			extComp.append("	var root = new Tree.AsyncTreeNode({\n");
			extComp.append("	                text: '"+XEOComponentMessages.TREE_SHUTLE_AVAILABLE.toString()+"', \n");
			extComp.append("	                draggable:true,\n");
			extComp.append("                	children:v_local_tree1_data,\n");
			extComp.append("	                id:'src',\n");	
			extComp.append("                	checked:false\n");
			extComp.append("	            });\n");
			extComp.append("	tree.setRootNode(root);\n");

			// Renderiza a árvore
			extComp.append("    tree.render('" + oComp.getClientId()
					+ "_tree1');\n");
			extComp.append("	root.expand(false, /*no anim*/ false);\n");

			// Segunda Tree
			extComp.append("	var tree2 = new Tree.TreePanel({\n");
			extComp.append("                rootVisible:false,\n");
			extComp.append("                title:'"+XEOComponentMessages.TREE_SHUTLE_SELECTED.toString()+"', \n");
			extComp.append("                frame:true, \n");
			extComp.append("                bodyStyle:'background-color:white;', \n");
			extComp.append("                animate:true,\n");
			extComp.append("                autoScroll:true,\n");
			extComp.append("                containerScroll: true,\n");
			extComp.append("                border: false,\n");
			extComp.append("                width: 255,\n"); 
			extComp.append("                height: 300,\n");
			extComp.append("                enableDD:true,\n");
			extComp.append("                dropConfig: {appendOnly:false}, \n");				
			extComp.append("				listeners: {\n");
			extComp.append("    				'checkchange': function(node, checked){\n");
			extComp.append("						if(checked) {\n");
			extComp.append("        					toggleCheck(node, true); \n");
			extComp.append(" 						}\n");
			extComp.append("						else {\n");
			extComp.append("        					toggleCheck(node, false); \n");
			extComp.append(" 						}\n");
			extComp.append("    				}\n");
			extComp.append("				}\n");			
			extComp.append("            });\n");
			
			// Nó raiz
			extComp.append("	var root2 = new Tree.AsyncTreeNode({\n");
			extComp.append("                text: '"+XEOComponentMessages.TREE_SHUTLE_SELECTED.toString()+"', \n");
			extComp.append("                draggable:true, \n");
			extComp.append("                children:v_local_tree2_data,\n");
			extComp.append("                id:'ux',\n");	
			extComp.append("                checked:false\n");			
			extComp.append("            });\n");
			extComp.append("   tree2.setRootNode(root2);\n");
			extComp.append("   tree2.render('" + oComp.getClientId() + "_tree2');\n");
			extComp.append("   root2.expand(false, /*no anim*/ false);\n");
			
			// Buttons
			extComp.append("var btn1 = new Ext.Button({id: 'button-1r', minWidth:'40', text:'&gt;', handler : function() {move(tree,tree2,root,root2,'S'); } });"); 
			extComp.append("btn1.render('" + oComp.getClientId() + "button-div1r');");
			extComp.append("var btn4 = new Ext.Button({id: 'button-1l', minWidth:'40', text:'&lt;', handler : function() {move(tree2,tree,root2,root,'S'); }});"); 
			extComp.append("btn4.render('" + oComp.getClientId() + "button-div1l');");			
			extComp.append("var btn2 = new Ext.Button({id: 'button-Ar', minWidth:'40', text:'&gt;&gt;', handler : function() {move(tree,tree2,root,root2,'A');  }});"); 
			extComp.append("btn2.render('" + oComp.getClientId() + "button-divAr');");
			extComp.append("var btn3 = new Ext.Button({id: 'button-Al', minWidth:'40', text:'&lt;&lt;', handler : function() {move(tree2,tree,root2,root,'A');  }});"); 
			extComp.append("btn3.render('" + oComp.getClientId() + "button-divAl');");
			
			String okHandler = oAttr.getSubmitActionHandler();
			
			if(okHandler == null || okHandler.length() == 0)
			{
				okHandler = "function(){}";
			}
			
			String cancelHandler = oAttr.getCancelActionHandler();
			
			if(cancelHandler == null || cancelHandler.length() == 0)
			{
				cancelHandler = "function(){\n" +
								"	var w = Ext.getCmp('" + oAttr.getParentWindowId() + "');\n" +
								"	w.close();\n" +
								"	}";
			}
			
			extComp.append("var btnOk = new Ext.Button({id: 'button-ok', text:'&nbsp;&nbsp;&nbsp;&nbsp;OK&nbsp;&nbsp;&nbsp;&nbsp;', handler: " + okHandler + "});\n");			
			extComp.append("btnOk.render('" + oComp.getClientId() + "button-ok');");	
			
			
			extComp.append("var btnCancel = new Ext.Button({id: 'button-cancel', text:'"+XEOComponentMessages.TREE_SHUTLE_CANCEL.toString()+"', handler: " + cancelHandler + "});\n");			
			extComp.append("btnCancel.render('" + oComp.getClientId() + "button-cancel');");	
			extComp.append("});");
			
			


			w.getScriptContext()
					.add(XUIScriptContext.POSITION_FOOTER,
							oComp.getClientId() + "_extcomboscript",
							extComp.toString());

			// w.endElement(HTMLTag.DIV);
			oComp.setRendered(true);

		}

		@Override
		public boolean getRendersChildren() {
			return true;
		}
	}

}
