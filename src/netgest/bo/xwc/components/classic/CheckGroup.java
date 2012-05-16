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
public class CheckGroup extends XUIInput {
	
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
	
	
	public static class XEOHTMLRenderer extends XUIRenderer{
		
		
		@Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            
            CheckGroup    lovComponent = (CheckGroup) component;
            XUIResponseWriter  w = getResponseWriter();
            w.startElement( DIV, lovComponent );
            w.writeAttribute( ID, lovComponent.getClientId(), "id" );
            writeHiddenInput(w,lovComponent);
            
        }

        private void writeHiddenInput( XUIResponseWriter w, CheckGroup lovComponent ) throws IOException {
			w.startElement( HTMLTag.INPUT, lovComponent );
				w.writeAttribute( HTMLAttr.TYPE, "hidden", null );
				w.writeAttribute( HTMLAttr.NAME, lovComponent.getId() +"_hidden", null );
				w.writeAttribute( HTMLAttr.ID, lovComponent.getId() +"_hidden", null );
				w.writeAttribute( HTMLAttr.VALUE, "", null );
			w.endElement( HTMLTag.INPUT );
		}
        
        private String getListenerFunction(){
        	StringBuilder b = new StringBuilder();
        	
        	b.append( " function (node,checked ){ ");
        	b.append( " var idDestiny = this.id; " );
        	b.append( " var idInput  = this.xvwInputId; " );
        	b.append( " var result = ''; " );
        	b.append( " var choices = Ext.getCmp(idDestiny).getChecked(); " );
			b.append( " var append = ''; " );
			b.append( " for (var k = 0 ; k < choices.length; k++){ " );
			b.append( "	result += append; " );
			b.append( " result += choices[k].id; " );
			b.append( " append = ','; " );
			b.append( "} " ); 
        	b.append(" Ext.get(idInput).set({'value' : result}); " );
        	b.append( "} " );
        	
        	
        	return b.toString();
        	
        }

		@Override
        public void encodeEnd(XUIComponentBase component ) throws IOException {
            CheckGroup    lovComponent = (CheckGroup)component;
            XUIResponseWriter  w = getResponseWriter();
            
            createComponentPlaceHolders(w, lovComponent);
            
            ExtConfig sourceTreePanel = createSourceTree(component);
            
            getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
            		"lov_sourceTreePanel", sourceTreePanel.renderExtConfig().toString() );
            
            w.endElement( DIV );
            
        }
		
		
		private ExtConfig createSourceTree( XUIComponentBase component ) {
			
			CheckGroup comp = (CheckGroup) component;
			
			ExtConfig config = new ExtConfig( "Ext.tree.TreePanel" );
			config.add( "loader", "new Ext.tree.TreeLoader" );
			config.addJSString( "id", component.getId() + "_source" );
			config.addJSString( "xvwInputId", component.getId() + "_hidden");
			config.add( "width", 100 );
			config.add("lines", false);
			config.addJSString( "bodyStyle", "{border:0}" );
			config.addJSString( "renderTo", component.getId() + "_sourceDiv" );
			config.add("rootVisible", false);
			config.add( "listeners", " {'checkchange' : "+ getListenerFunction() + " } " );
			
			ExtConfig rootNode = config.addChild( "root" );
			
			rootNode.setComponentType( "Ext.tree.AsyncTreeNode" );
			rootNode.add( "expanded", false );
			rootNode.add( "leaf", false );
			rootNode.addJSString( "text", "" );
			rootNode.add( "children", comp.convertChoicesToExtArray() );
			
			
			return config;
		}

		private void createComponentPlaceHolders( XUIResponseWriter w, CheckGroup component ) throws IOException {
			
			w.startElement( TABLE );
				
				w.startElement( HTMLTag.TD );
					w.startElement( HTMLTag.DIV );
						w.writeAttribute( HTMLAttr.ID, component.getId() + "_sourceDiv");
						w.writeAttribute( HTMLAttr.STYLE, "display:inline; margin-top:5px;");
					w.endElement( HTMLTag.DIV );
				w.endElement( HTMLTag.TD);
				
				w.endElement( TR );
			w.endElement( TABLE );
			
		}

		@Override
		public void decode(XUIComponentBase component) {
			CheckGroup checkGroup = ( CheckGroup ) component;
			String value = getFacesContext().getExternalContext().getRequestParameterMap().get( checkGroup.getId() + "_hidden" );
			checkGroup.setSubmittedValue( value );
			checkGroup.setValue( value );
		}
		
	}
	
}
