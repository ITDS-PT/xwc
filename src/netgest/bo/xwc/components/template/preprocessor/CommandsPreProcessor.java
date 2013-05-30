package netgest.bo.xwc.components.template.preprocessor;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import netgest.bo.xwc.components.template.Template;
import netgest.bo.xwc.components.template.TemplateCommand;
import netgest.bo.xwc.components.template.TemplateInput;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIInput;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;

public class CommandsPreProcessor {

	private freemarker.template.Template template = null;
	private Template component;
	private DocumentBuilder docBuilder;
	
	public CommandsPreProcessor(freemarker.template.Template template, Template component) {
		this.template = template;
		this.component = component;
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch ( ParserConfigurationException e ) {
			e.printStackTrace();
		}
	}
	
	Element parseElement(String source){
		try {
			org.w3c.dom.Document doc = docBuilder.parse(new InputSource(new StringReader(source)));
			return doc.getDocumentElement( ) ;
		} catch ( SAXException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} return null;
	}
	
	public List<UIComponent> createComponents(){
		List<UIComponent> result =  new LinkedList< UIComponent >( );
		try{
			freemarker.template.Template t = template;
			TemplateElement template = t.getRootTreeNode( );
			int count = t.getRootTreeNode().getChildCount( );
			for (int k = 0 ; k < count ; k++){
				TreeNode current = template.getChildAt( k );
				if (current instanceof TemplateObject && current.getClass( ).getName( ).equalsIgnoreCase( "freemarker.core.UnifiedCall" )){
					TemplateObject o = (TemplateObject) current;
					String source = o.getSource();
					if (source.contains( "XUICommand" )){
						source = source.replace( "@" , "" );
				        Element m = parseElement( source );
				        String id = m.getAttribute( "id" );
				        String commandExpression = m.getAttribute("serverAction");
				        commandExpression =commandExpression.replace( "!" , "#" );
				        XUICommand cmd = new TemplateCommand( );
				        cmd.setId( id );
				        cmd.setActionExpression( component.createMethodBinding(commandExpression) );
				        result.add( cmd );
					} else if (source.contains("XUIInput")){
						source = source.replace( "@" , "" );
				        Element m = parseElement( source );
				        String name = m.getAttribute( "name" );
				        String commandExpression = m.getAttribute("valueExpression");
				        commandExpression =commandExpression.replace( "!" , "#" );
				        XUIInput in = new TemplateInput();
				        in.setId(name);
				        in.setValueExpression( "value" , component.createValueExpression( commandExpression , String.class ) );
				        result.add( in );
					}
				}
				
			}
		} catch (Exception e){
			e.printStackTrace( );
		}
		return result; 
	}
	
	public String toString() {
		return super.toString( );
	}

	
}
