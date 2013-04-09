package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIInput;

/**
 * A component that supports selecting values from a List Of Values
 *
 */
public class LovChooser extends XUIInput {
	
	private XUIBindProperty<Map<String,String>> choices = new XUIBindProperty<Map<String,String>>( "choices", this, Map.class );
	
	public void setChoices( String choicesExpr ){
		this.choices.setExpressionText( choicesExpr );
	}
	
	public Map<String,String> getChoices(){
		return this.choices.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Generate a JSON array with the choices
	 * 
	 * @return
	 */
	public ExtConfigArray convertChoicesToExtArray(){
		ExtConfigArray result = new ExtConfigArray();
		Map<String,String> currChoices = getChoices();
		Iterator<Entry<String,String>> it = currChoices.entrySet().iterator();
		while (it.hasNext()){
			Entry<String,String> currEntry = it.next();
			ExtConfig newEntry = result.addChild();
			newEntry.addJSString( "text", currEntry.getValue() );
			newEntry.addJSString( "id", currEntry.getKey() );
			newEntry.addJSString( "cls", "x-tree-noicon" );
			newEntry.add( "checked" , false );
			newEntry.add( "leaf" , true );
		}
		return result;
	}
	
	@Override
	public void preRender(){
		getRequestContext().getScriptContext().addInclude( XUIScriptContext.POSITION_HEADER, "lovChooser", "ext-xeo/js/lovChooser.js" );
	}

	public static class XEOHTMLRenderer extends XUIRenderer{
		
		
		@Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            
            LovChooser    lovComponent = (LovChooser) component;
            XUIResponseWriter  w = getResponseWriter();
            w.startElement( DIV, lovComponent );
            w.writeAttribute( ID, lovComponent.getClientId(), "id" );
            //w.writeAttribute( HTMLAttr.STYLE, " overflow-y: scroll" );
            writeHiddenInput(w,lovComponent);
            
        }

        private void writeHiddenInput( XUIResponseWriter w, LovChooser lovComponent ) throws IOException {
			w.startElement( HTMLTag.INPUT, lovComponent );
				w.writeAttribute( HTMLAttr.TYPE, "hidden", null );
				w.writeAttribute( HTMLAttr.NAME, lovComponent.getId() +"_hidden", null );
				w.writeAttribute( HTMLAttr.ID, lovComponent.getId() +"_hidden", null );
				w.writeAttribute( HTMLAttr.VALUE, "", null );
			w.endElement( HTMLTag.INPUT );
		}

		@Override
        public void encodeEnd(XUIComponentBase component ) throws IOException {
            LovChooser    lovComponent = (LovChooser)component;
            XUIResponseWriter  w = getResponseWriter();
            
            createComponentPlaceHolders(w, lovComponent);
            
            ExtConfig sourceTreePanel = createSourceTree(component);
            ExtConfig destinyTreePanel  = createDestinyTree(component);
            ExtConfig addButton = createAddButton(component);
            ExtConfig removeButton = createRemoveButton(component);
            
            getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
            		"lov_sourceTreePanel", sourceTreePanel.renderExtConfig().toString() );
            
            getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
            		"lov_destinyTreePanel", destinyTreePanel.renderExtConfig().toString() );
            
            getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
            		"lov_addBtn", addButton.renderExtConfig().toString() );
            
            getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
            		"lov_removeBtn", removeButton.renderExtConfig().toString() );
            
            w.endElement( DIV );
            
        }
		
		private ExtConfig createDestinyTree( XUIComponentBase component ) {
			
			ExtConfig config = new ExtConfig( "Ext.tree.TreePanel" );
			config.add( "loader", "new Ext.tree.TreeLoader" );
			config.addJSString( "id", component.getId() + "_destiny" );
			config.add( "width", 100 );
			config.add("lines", false);
			config.addJSString( "renderTo", component.getId() + "_targetDiv" );
			config.add("rootVisible", false);
			config.addJSString( "bodyStyle", "{border:0}" );
			
			ExtConfig rootNode = config.addChild( "root" );
			
			rootNode.setComponentType( "Ext.tree.TreeNode" );
			rootNode.add( "expanded", false );
			rootNode.add( "leaf", false );
			rootNode.addJSString( "text", "" );
			rootNode.addChildArray( "children" );
			
			return config;
		}

		private ExtConfig createSourceTree( XUIComponentBase component ) {
			
			LovChooser comp = (LovChooser) component;
			
			ExtConfig config = new ExtConfig( "Ext.tree.TreePanel" );
			config.add( "loader", "new Ext.tree.TreeLoader" );
			config.addJSString( "id", component.getId() + "_source" );
			config.add( "width", 100 );
			config.add("lines", false);
			config.addJSString( "bodyStyle", "{border:0}" );
			config.addJSString( "renderTo", component.getId() + "_sourceDiv" );
			config.add("rootVisible", false);
			
			
			ExtConfig rootNode = config.addChild( "root" );
			
			rootNode.setComponentType( "Ext.tree.AsyncTreeNode" );
			rootNode.add( "expanded", false );
			rootNode.add( "leaf", false );
			rootNode.addJSString( "text", "" );
			rootNode.add( "children", comp.convertChoicesToExtArray() );
			
			
			return config;
		}

		private ExtConfig createRemoveButton( XUIComponentBase component ) {
			ExtConfig config = new ExtConfig( "Ext.Button" );
			config.addJSString( "renderTo", component.getId() + "_remove" );
			config.addJSString( "text", "<<" );
			config.add( "handler", "function(){XVW.changeToSourceTree(this);}" );
			associateTreeIdentifiersWithButton( component, config );
			return config;
		}

		private void associateTreeIdentifiersWithButton( XUIComponentBase component, ExtConfig config ) {
			config.addJSString( "sourceTree", component.getId() + "_source" );
			config.addJSString( "destinyTree", component.getId() + "_destiny" );
			config.addJSString( "idInput", component.getId() + "_hidden" );
		}

		private ExtConfig createAddButton( XUIComponentBase component ) {
			ExtConfig config = new ExtConfig( "Ext.Button" );
			config.addJSString( "renderTo", component.getId() + "_add" );
			config.addJSString( "text", ">>" );
			config.add( "handler", "function(){XVW.changeToTargetTree(this);}" );
			associateTreeIdentifiersWithButton( component, config );
			return config;
		}

		private void createComponentPlaceHolders( XUIResponseWriter w, LovChooser component ) throws IOException {
			
			w.startElement( TABLE );
				w.startElement( TR );
					w.startElement( HTMLTag.TH );
						w.write( ComponentMessages.LOV_OPTIONS.toString() );
					w.endElement( HTMLTag.TH);
					w.startElement( HTMLTag.TH );
						w.write( "" );
					w.endElement( HTMLTag.TH);
					w.startElement( HTMLTag.TH );
						w.write( ComponentMessages.LOV_CHOSEN.toString() );
					w.endElement( HTMLTag.TH);
				w.endElement( TR );
				w.startElement( TR );
				
				w.startElement( HTMLTag.TD );
					w.startElement( HTMLTag.DIV );
						w.writeAttribute( HTMLAttr.ID, component.getId() + "_sourceDiv");
						w.writeAttribute( HTMLAttr.STYLE, "display:inline; margin-top:5px;");
					w.endElement( HTMLTag.DIV );
				w.endElement( HTMLTag.TD);
				
				w.startElement( HTMLTag.TD );
					w.startElement( HTMLTag.DIV );
						w.writeAttribute( HTMLAttr.ID, component.getId() + "_buttons");
						w.writeAttribute( HTMLAttr.STYLE, "margin-top:5px;display:inline");
					
					
						w.startElement( HTMLTag.DIV ); 
							w.writeAttribute( HTMLAttr.ID, component.getId() + "_add");
							w.writeAttribute( HTMLAttr.STYLE, "");
						w.endElement( HTMLTag.DIV);
					
					
						w.startElement( HTMLTag.DIV ); 
							w.writeAttribute( HTMLAttr.ID, component.getId() + "_remove");
							w.writeAttribute( HTMLAttr.STYLE, "margin-top:5px;");
						w.endElement( HTMLTag.DIV);
					w.endElement( HTMLTag.DIV );
				w.endElement( HTMLTag.TD);
				
				w.startElement( HTMLTag.TD );
					w.startElement( HTMLTag.DIV );
						w.writeAttribute( HTMLAttr.ID, component.getId() + "_targetDiv");
						w.writeAttribute( HTMLAttr.STYLE, "margin-top:5px;");
					w.endElement( HTMLTag.DIV );	
				w.endElement( HTMLTag.TD );
				
				w.endElement( TR );
			w.endElement( TABLE );
			
		}

		@Override
		public void decode(XUIComponentBase component) {
			LovChooser lovChoover = ( LovChooser ) component;
			String value = getFacesContext().getExternalContext().getRequestParameterMap().get( lovChoover.getId() + "_hidden" );
			lovChoover.setSubmittedValue( value );
			lovChoover.setValue( value );
		}
		
	}
	
}
