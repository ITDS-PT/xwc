package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.theme.ExtJsTheme;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * This component represents a HtmlEditor.
 * 
 * @author jcarreira
 * 
 */
public class AttributeHtmlEditor extends AttributeBase {

	public XUIBindProperty<Boolean> enableFont = 
		new XUIBindProperty<Boolean>("enableFont", this, Boolean.TRUE, Boolean.class );

	public XUIBindProperty<String> fontFamilies = 
		new XUIBindProperty<String>("fontFamilies", this, String.class );
	
	public void setEnableFont( String exprEnableFont ) {
		this.enableFont.setExpressionText( exprEnableFont );
	}

	public boolean getEnableFont() {
		return this.enableFont.getEvaluatedValue();
	}
	
	public void setFontFamilies( String exprEnableFont ) {
		this.fontFamilies.setExpressionText( exprEnableFont );
	}

	public String getFontFamilies() {
		return this.fontFamilies.getEvaluatedValue();
	}
	
	@Override
	public void initComponent() {

		XUIRequestContext req = XUIRequestContext.getCurrentContext();

		req.getStyleContext().addInclude(XUIScriptContext.POSITION_HEADER,
				"ext-xeo-nohtmleditor", "ext-xeo/css/ext-xeo-htmleditor.css");

		req.getStyleContext().addInclude(XUIScriptContext.POSITION_HEADER,
				"xeo-htmleditoradvancedcss",
				".xeodeploy/ext-xeo/htmlAdvanced/css/htmlAdvanced.css");

		XUIScriptContext scriptCtx = req.getScriptContext();

		scriptCtx
				.addInclude(
						XUIScriptContext.POSITION_HEADER,
						"xeo-htmleditoradvancedjs",
						ExtJsTheme
								.composeUrl(".xeodeploy/ext-xeo/htmlAdvanced/js/htmlAdvanced.js"));
	}

	public AttributeHtmlEditor() {
		super.setHeight("auto");
	}

	public static class XEOHTMLRenderer extends XUIRenderer implements
			XUIRendererServlet {

		@Override
		public StateChanged wasStateChanged( XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties ) {
			updateProperties.add( component.getStateProperty( "disabled" ) );
			updateProperties.add( component.getStateProperty( "visible" ) );
			updateProperties.add( component.getStateProperty( "readOnly" ) );
			return super.wasStateChanged( component, updateProperties );
		}
		
		
		@Override
		public void encodeComponentChanges( XUIComponentBase component,
				List<XUIBaseProperty<?>> propertiesWithChangedState ) throws IOException {
			
			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) component;
			
			StringBuilder script = new StringBuilder(200);
			script.append("var c=Ext.getCmp('");
			script.append(component.getClientId());
			script.append("_editor');\n");
			script.append("if(c) { c.setValue('");
			script.append(JavaScriptUtils.safeJavaScriptWrite( oHtmlComp.getDisplayValue(), '\''));
			script.append("');\n");
			
			for (XUIBaseProperty<?> prop : propertiesWithChangedState){
				if ("disabled".equalsIgnoreCase( prop.getName() ) ){
					script.append( "c.setReadOnly(" );
					script.append( prop.getValue() );
					script.append( ");" );
				}
				else if ("readOnly".equalsIgnoreCase( prop.getName() ) ){
					script.append( "c.setReadOnly(" );
					script.append( prop.getValue() );
					script.append( ");" );
				}
				else if ("visible".equalsIgnoreCase( prop.getName() ) ){
					script.append( "c.setVisible(" );
					script.append( prop.getValue() );
					script.append( ");" );
				}
			}
			
			script.append("}\n");
			
			getRequestContext().getScriptContext()
				.add(XUIScriptContext.POSITION_FOOTER , 
					component.getClientId() + "_updHtml",
					script.toString() 
			);
			
		}
		
		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {

			XUIResponseWriter w = getResponseWriter();
			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) oComp;

			
			w.getStyleContext().addInclude(
					XUIScriptContext.POSITION_HEADER,
					"ext-xeo-nohtmleditor",
					"ext-xeo/css/ext-xeo-htmleditor.css");

			// Place holder for the component
			if (oHtmlComp.isDisabled() || oHtmlComp.isReadOnly()) {

				String sActionUrl = getRequestContext().getActionUrl();
				// 'javax.faces.ViewState'
				String sViewState = getRequestContext().getViewRoot()
						.getViewState();
				// xvw.servlet
				String sServletId = oComp.getClientId();

				if (sActionUrl.indexOf('?') != -1) {
					sActionUrl += "&";
				} else {
					sActionUrl += "?";
				}
				sActionUrl += "javax.faces.ViewState=" + sViewState;
				sActionUrl += "&xvw.servlet=" + sServletId;

				w.startElement(HTMLTag.IFRAME, oComp);
				w.writeAttribute(ID, oComp.getClientId(), null);
				w.writeAttribute(HTMLAttr.CLASS,
						"x-form-text x-form-field", null);
				w.writeAttribute(HTMLAttr.STYLE, "width:99%;height:"
						+ oHtmlComp.getHeight() + "px", null);
				w.writeAttribute(HTMLAttr.FRAMEBORDER, "0", null);
				w.writeAttribute(HTMLAttr.SCROLLING, "1", null);

				w.writeAttribute(HTMLAttr.SRC, sActionUrl, null);

				w.endElement(HTMLTag.IFRAME);
			} else {
				w.startElement(DIV, oComp);
				w.writeAttribute(ID, oComp.getClientId(), null);

				w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
						oComp.getId(), renderExtJs(oComp));
				w.endElement(DIV);
			}
			if ("auto".equals(oHtmlComp.getHeight())) {
				Layouts.registerComponent(w, oComp,
						Layouts.LAYOUT_FIT_PARENT);
			}
			
			
		}

		public String renderExtJs(XUIComponentBase oComp) {

			StringBuilder sOut = new StringBuilder(200);

			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) oComp;
			
            ExtConfig oHtmlCfg = new ExtConfig( "Ext.form.HtmlEditor" );
            oHtmlCfg.addJSString("id", oComp.getClientId() );
            oHtmlCfg.addJSString("name", oComp.getClientId() );
            oHtmlCfg.addJSString("renderTo", oComp.getClientId() );
            String value = JavaScriptUtils.safeJavaScriptWrite(oHtmlComp.getDisplayValue(), '\'');
            oHtmlCfg.addJSString("value", value );
//            oHtmlCfg.add( "width" ,  oHtmlComp.getWidth() );
            
            if( !"auto".equals( oHtmlComp.getHeight() ) ) {
            	oHtmlCfg.add( "height" , oHtmlComp.getHeight()  );
            }
            oHtmlCfg.add( "frame" ,  false );
//            oHtmlCfg.addJSString( "layout" ,  "fit" );
            oHtmlCfg.add( "enableColors" ,  true );
            oHtmlCfg.add( "enableAlignments" ,  true );
            oHtmlCfg.add( "enableLinks" ,  false );
            oHtmlCfg.add( "statefull" ,  true );
            oHtmlCfg.add("enableFont", oHtmlComp.getEnableFont() );
            
            ExtConfig listeners = oHtmlCfg.addChild("listeners");

            // ExtJs Workaround, Prevent lock of the editable content
            // When chaging tab, the content can't be editable any more. 
            // Bug in ExtJs must call toggleSourceEdit twice to the function work.
            listeners.add("render", "function(editor) {\n" +
            		"editor.toggleSourceEdit(true);" +
            		"editor.toggleSourceEdit(true);" +
            		"editor.toggleSourceEdit(false);" +
            		"editor.toggleSourceEdit(false);" +
            		"}\n");
            //Really weird ExtJS or IE (probably) Bug? 
            //In IE, if we swtich to a tab while having
            //focus on the editor, fields become "non-editable"
            //Cant understand why, I couldn't find any javascript/css
            //that makes them this way. Anyway, workaround for Bug 502
            listeners.add("beforeDestroy", "function(editor) {\n" +
            		" editor.toggleSourceEdit(true);" +
            		" editor.toggleSourceEdit(true); " +
            		"}\n");
            
            String fontFamilies = oHtmlComp.getFontFamilies();
            if( fontFamilies != null ) {
                oHtmlCfg.add("fontFamilies", oHtmlComp.getFontFamilies() );
            }
            
            if( oHtmlComp.isDisabled() ) 
                oHtmlCfg.add( "disabled" ,  true );
            
            oHtmlCfg.add( "hidden" ,  !oHtmlComp.isVisible() );
            
			
			handleAdvancedEditorPlugins(oHtmlComp, oHtmlCfg, sOut);

			sOut.append("Ext.onReady( function() {");
			sOut.append("try { \n");
				oHtmlCfg.renderExtConfig(sOut);
			sOut.append("} catch(e){ debugger; alert(e); } \n");
			if (!oHtmlComp.isVisible())
				sOut.append("Ext.get('"+oComp.getClientId()+"').hide();");
			sOut.append("});\n");

			return sOut.toString();
		}

		private void handleAdvancedEditorPlugins(
				final AttributeHtmlEditor iHtmlComp, final ExtConfig iHtmlCfg,
				final StringBuilder sOut) {

			DataFieldConnector fConn = iHtmlComp.getDataFieldConnector();

			if (fConn instanceof XEOObjectAttributeConnector) {

				XEOObjectAttributeConnector attrConn = (XEOObjectAttributeConnector) fConn;

				try {
					boObject docObj = attrConn.getAttributeHandler()
							.getObject();

					if (docObj == null) {
						docObj = attrConn.getAttributeHandler().getParent();
					}

					boDefAttribute attrHandler = docObj.getAttribute(
							iHtmlComp.getObjectAttribute()).getDefAttribute();

					if ("htmladvanced".equalsIgnoreCase(attrHandler
							.getEditorType())) {

						String imageObj = "";
						String contentObjs = "";

						Properties props = docObj.getEboContext()
								.getApplication().getApplicationConfig()
								.getContentMngmConfig();

						if (props != null) {
							imageObj = props.get("Images_Type") != null ? props
									.get("Images_Type").toString() : imageObj;
							contentObjs = props.get("Contents_Type") != null ? props
									.get("Contents_Type").toString()
									: contentObjs;
						}

						iHtmlCfg.add("imageObjs", "'" + imageObj + "'");
						iHtmlCfg.add("contentObjs", "'" + contentObjs + "'");

						if (!"auto".equals(iHtmlComp.getHeight())) {
							iHtmlCfg.add("height", iHtmlComp.getHeight());
						}

						StringBuilder b = new StringBuilder();
						b.append("new Ext.ux.form.HtmlEditor.Divider(),");
						b.append("new Ext.ux.form.HtmlEditor.Anchor(),");
						b.append("new Ext.ux.form.HtmlEditor.SpecialCharacters(),");
						b.append("new Ext.ux.form.HtmlEditor.Table(),");
						b.append("new Ext.ux.form.HtmlEditor.HR(),");
						b.append("new Ext.ux.form.HtmlEditor.Smileys(),");
						b.append("new Ext.ux.form.HtmlEditor.Image(),");
						b.append("new Ext.ux.form.HtmlEditor.Link(),");
						b.append("new Ext.ux.form.HtmlEditor.IndentOutdent(),");
						b.append("new Ext.ux.form.HtmlEditor.SubSuperScript(),");
						b.append("new Ext.ux.form.HtmlEditor.YOUTUBE(),");
						b.append("new Ext.ux.form.HtmlEditor.GMAPS(),");
						b.append("new Ext.ux.form.HtmlEditor.FLASH()");
						if (imageObj.length() > 0)
						{
							b.append(",new Ext.ux.form.HtmlEditor.XEO_IMAGE()");
						}
						if (contentObjs.length() > 0 )
						{
							b.append(",new Ext.ux.form.HtmlEditor.XEO_CONTENTS()");
						}
						iHtmlCfg
								.add(
										"plugins",
										"[" + b.toString() + "]");
					}

				} catch (boRuntimeException e) {

					e.printStackTrace();
				}
			}

		}

		@Override
		public void decode(XUIComponentBase component) {

			String sHtml = XUIRequestContext.getCurrentContext().getRequestParameterMap().get( component.getClientId() );
			if( sHtml != null ) {
				sHtml = sHtml.replace('\n',' ');
				((AttributeHtmlEditor)component).setSubmittedValue( sHtml );
			}
		}

		public void service(ServletRequest request, ServletResponse response,
				XUIComponentBase oComp) throws IOException {
			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) oComp;
			String sDisplayValue = oHtmlComp.getDisplayValue();
			PrintWriter w = response.getWriter();
			final Pattern p = Pattern.compile(
					"<([A-Z][A-Z0-9]*)\\b[^>]*>(.*?)</\\1>",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sDisplayValue);
			if (m.find()) {
				// Content is HTML
				response.setContentType("text/html");
				w.print(sDisplayValue);
			} else {
				response.setContentType("text/html");
				w.write("<pre>");
				w.print(sDisplayValue);
				w.write("</pre>");
			}
		}
	}

}
