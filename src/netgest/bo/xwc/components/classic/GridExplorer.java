package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.grid.GridPanelExtJsRenderer;
import netgest.bo.xwc.components.classic.grid.GridPanelRenderer;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;

public class GridExplorer extends GridPanel {
	
	public enum PreviewPanelPosition {
		LEFT,
		RIGHT,
		BOTTOM
	}
	
	private XUIViewProperty<PreviewPanelPosition> previewPanelPosition = 
		new XUIViewProperty<PreviewPanelPosition>("previewPanelPosition", this, PreviewPanelPosition.BOTTOM );
	
	private XUIViewProperty<Boolean> enablePreviewPanel = 
		new XUIViewProperty<Boolean>("enablePreviewPanel", this, true );

	private XUIViewProperty<Integer> previewPanelHeight = 
		new XUIViewProperty<Integer>("previewPanelHeight", this, 250 );

	private XUIViewProperty<Integer> previewPanelWidth = 
		new XUIViewProperty<Integer>("previewPanelWidth", this, 350 );
	
	private XUIBaseProperty<String> previewCommand = 
		new XUIBaseProperty<String>("previewCommand", this, "#{viewBean.previewObject}");
	
	public void setPreviewPanelHeight( int height ) {
		this.previewPanelHeight.setValue( height );
	}

	public int getPreviewPanelHeight() {
		return this.previewPanelHeight.getValue();
	}

	public void setPreviewPanelWidth( int height ) {
		this.previewPanelWidth.setValue( height );
	}

	public int getPreviewPanelWidth() {
		return this.previewPanelWidth.getValue();
	}
	
	public void setEnablePreviewPanel( boolean previewPanel ) {
		this.enablePreviewPanel.setValue( previewPanel );
	}

	public void setPreviewPanelPosition( String position ) {
		this.previewPanelPosition.setValue( PreviewPanelPosition.valueOf( position.toUpperCase() ) );
	}

	public void setPreviewPanelPosition( PreviewPanelPosition position ) {
		this.previewPanelPosition.setValue( position );
	}

	public PreviewPanelPosition getPreviewPanelPosition() {
		return this.previewPanelPosition.getValue();
	}
	
	public boolean getEnablePreviewPanel() {
		return this.enablePreviewPanel.getValue();
	}
	
	public String getPreviewCommand() {
		return this.previewCommand.getValue();
	}
	
	public void setPreviewCommand( String previewCommand ) {
		this.previewCommand.setValue( previewCommand );
	}
	
	
	@Override
	public void initComponent() {
		super.initComponent();
		
		if ( super.getStateProperty("enableGroupBy").isDefaultValue() ) {
			super.setEnableGroupBy( Boolean.toString( true ) );
		}

		// Preview Command 
		XUICommand previewCmd = new XUICommand();
		previewCmd.setId( getId() + "_pvwcmd" );
		previewCmd.setActionExpression( createMethodBinding( previewCommand.getValue() ) );
		getChildren().add( previewCmd );
		
		createToolBar();
		
	}
	
	private ToolBar getToolBar() {
		return (ToolBar)findComponent( getId() + "_tb" );
	}
	
	private void createToolBar() {
		ToolBar t = new ToolBar();
		
		t.setId( getId() + "_tb" );
		
		// Menu Preview
		Menu pvw = new PreviewPositionMenu();
		pvw.setText("Painel de Leitura");
		pvw.setId( getId() + "_pvw" );
		
		PreviewPanelPosition p = getPreviewPanelPosition();
		
		PreviewPositionMenu pvwl = new PreviewPositionMenu();
		pvwl.setText( "Esquerda" );
		pvwl.setValue( PreviewPanelPosition.LEFT == p );
		pvwl.setGroup( "preview" );
		pvwl.setPosition( "LEFT" );
		pvwl.setId( getId() + "_mpvwl" );
		
		pvw.getChildren().add( pvwl );
		
		PreviewPositionMenu pvwr = new PreviewPositionMenu();
		pvwr.setText( "Direita" );
		pvwr.setValue( PreviewPanelPosition.RIGHT == p );
		pvwr.setGroup( "preview" );
		pvwr.setPosition( "RIGHT" );
		pvwr.setId( getId() + "_mpvwr" );
		pvw.getChildren().add( pvwr );
		
		PreviewPositionMenu pvwb = new PreviewPositionMenu();
		pvwb.setText( "Inferior" );
		pvwb.setValue( PreviewPanelPosition.BOTTOM == p );
		pvwb.setGroup( "preview" );
		pvwb.setPosition( "BOTTOM" );
		pvwb.setId( getId() + "_mpvwb" );
		pvw.getChildren().add( pvwb );
		
		t.getChildren().add( pvw );
		
		Menu search = new Menu();
		search.setText("Pesquisa");
		search.setId( getId() + "_srch" );
		t.getChildren().add( search );
		
		getChildren().add( t );
	}
	
	public static class PreviewPositionMenu extends Menu {
		
		private XUIBaseProperty<String> position = new XUIBaseProperty<String>("position", this );
		
		@Override
		public void actionPerformed(ActionEvent event) {
			GridExplorer gexp = (GridExplorer)findParentComponent( GridExplorer.class );
			
			String position = getPosition();
			
			if( position == null ) {
				// Toggle the position
				
				boolean nextSetTrue = false;
				for( UIComponent c : getChildren() ) {
					PreviewPositionMenu pm = (PreviewPositionMenu)c;
					
					if( (Boolean)pm.getValue() ) {
						nextSetTrue = true;
						pm.setValue( false );
					}
					else {
						if( nextSetTrue ) {
							pm.setValue( true );
							gexp.setPreviewPanelPosition( pm.getPosition() );
							nextSetTrue = false;
						}
					}
				}
				if( nextSetTrue ) {
					((Menu)getChild(0)).setValue( true );
					gexp.setPreviewPanelPosition( ((PreviewPositionMenu)getChild(0)).getPosition() );
				}				
			}
			else {
				gexp.setPreviewPanelPosition( position );
				// Update the menu flag
				Menu m = (Menu)getParent();
				for( UIComponent c : m.getChildren() ) {
					PreviewPositionMenu pm = (PreviewPositionMenu)c;
					if( pm != this ) {
						pm.setValue( false );
					}
					else {
						pm.setValue( true );
					}
				}
			}
		}
		
		public void setPosition( String position ) {
			this.position.setValue( position );
		}
		
		public String getPosition() {
			return this.position.getValue();
		}
	}
	
	
	protected XUICommand getPreviewCommandComponent() {
		return (XUICommand)findComponent( getId() + "_pvwcmd" );
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {
		
		GridPanelExtJsRenderer 	gridRenderer = new GridPanelExtJsRenderer();
		GridPanelRenderer 		render = new GridPanelRenderer();
		
		private static final String decodePositionToRegion( PreviewPanelPosition p ) {
			switch( p ) {
				case BOTTOM:
					return "south";
				case LEFT:
					return "west";
				case RIGHT:
					return "east";
			}
			return "south";
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
		}
		
		@Override
		public boolean getRendersChildren() {
			return true;
		}
		
		@Override
		public void encodeChildren(XUIComponentBase component) throws IOException {
		}
		
		
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {

			GridExplorer exp = ((GridExplorer)component);
			
			exp.setRegion("center");
			exp.setLayout("");
			
			if( exp.isRenderedOnClient() ) {
				getResponseWriter().getScriptContext().add(
						XUIScriptContext.POSITION_HEADER, 
						"destroy" + exp.getId(), 
						"Ext.getCmp('" + exp.getId() + "_vp').destroy();" );
			}
			
			ExtConfig config = new ExtConfig("Ext.Viewport");
			config.add( "layout" , "'border'");
			config.addJSString( "id", exp.getId() + "_vp" );
			
			ExtConfigArray items = config.addChildArray("items");
			items.add( gridRenderer.extEncodeAll(component) );
			
			ExtConfig previewPanel = new ExtConfig();
			previewPanel.addJSString( "id" , exp.getClientId() + "_panel" );
			previewPanel.add( "split" , true );
			previewPanel.add( "height" , exp.getPreviewPanelHeight() );
			previewPanel.add( "width" , exp.getPreviewPanelWidth() );
			
			previewPanel.add( "hidden" , false );
			previewPanel.addJSString( "region" , decodePositionToRegion( exp.getPreviewPanelPosition() ) );
			previewPanel.addJSString( "title" , "Preview" );
			previewPanel.addJSString( "bodyStyle" , "overflow:auto;" );
			previewPanel.addJSString( "html" , "<div id=\"" + exp.getClientId() + "_pvwdiv"  +  "\"/>" );
			
			// Default listeners
			ExtConfig listeners = new ExtConfig();
			listeners.add( "bodyresize" , "function(){ExtXeo.layoutMan.doLayout();}");
			previewPanel.add( "listeners" , listeners );
			
			items.add( previewPanel );
			
			
			
			
			getResponseWriter().getScriptContext()
				.add(XUIScriptContext.POSITION_FOOTER, "viewPort" + component.getClientId() , 
						config.renderExtConfig()
					);
			
			getResponseWriter().getScriptContext()
			.add(XUIScriptContext.POSITION_FOOTER, "previewRowClick" + component.getClientId() , 
					"var c = Ext.getCmp('" + component.getClientId() + "');" +
					"c.on('rowclick',function(){ " + 
						"ExtXeo.destroyComponents(document.getElementById('" + exp.getClientId() + "_pvwdiv'));" +
						"XVW.openViewOnElement(" +
						"'" + component.findParentComponent(XUIForm.class).getClientId() + "', '" + exp.getPreviewCommandComponent().getClientId() + "', '', '" + exp.getClientId() + "_pvwdiv');" + 
					" });"
				);
			
			
			// Overwrite the main functions
			if( !exp.isRenderedOnClient() ) {
				getResponseWriter().getScriptContext()
				.add(XUIScriptContext.POSITION_FOOTER, "explorerFunctions" + component.getClientId() ,
						"XVW.canCloseTabExplorer = XVW.canCloseTab;\n" +
						"XVW.canCloseTab = function( sForm, sCmd ) {\n" +
						"	XVW.explorerIsClosingTab=true;\n" +
						"	XVW.canCloseTabExplorer( sForm, sCmd );\n" +
						"}\n" +
						"XVW.setTitleExplorer = XVW.setTitle;\n" +
						"XVW.setTitle = function( sTitle ) {\n" +
						"	XVW.explorerIsClosingTab=false;\n" +
						"	Ext.getCmp('"+ exp.getClientId() + "_panel').setTitle( sTitle );\n" +
						"};" +
						"XVW.closeViewExplorer = XVW.closeView;\n" +
						"XVW.closeView = function( sViewId ) {\n" +
						"	var e = document.getElementById('" + exp.getClientId() + "_pvwdiv');\n" +
							"ExtXeo.destroyComponents( e );\n" +
							"Ext.getCmp('"+ exp.getClientId() + "_panel').setTitle( '' );\n" +
							"if( XVW.explorerIsClosingTab ) {\n" +
							"	XVW.closeViewExplorer('" + XUIRequestContext.getCurrentContext().getViewRoot().getViewId() +  "');\n" +
							"}\n" +
						"}\n"
				);
			}
			
			
//			getResponseWriter().getScriptContext()
//			.add(XUIScriptContext.POSITION_FOOTER, "previewDoLayout" + component.getClientId() , 
//					"window.setTimeout(\"alert(1);Ext.getCmp('" + component.getClientId() + "').doLayout( true, true );\",100);"
//				);
			
		}

		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp) throws IOException {
			this.render.service(oRequest, oResponse, oComp);
		}
		
		@Override
		public void decode(XUIComponentBase component) {
			super.decode(component);
			render.decode( component );
			gridRenderer.decode( component );
		}
		
	}
	
}
