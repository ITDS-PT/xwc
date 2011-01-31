package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.io.PrintWriter;
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
		//Had to add this, as it was alwasy being ovewriten by the 
		//propagate method in the Attribute component
		super.setHeight("auto");
	}

	public AttributeHtmlEditor() {
		super.setHeight("auto");
	}

	public static class XEOHTMLRenderer extends XUIRenderer implements
			XUIRendererServlet {

		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {

			XUIResponseWriter w = getResponseWriter();
			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) oComp;

			if (!oComp.isRenderedOnClient()) {
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
		}

		public String renderExtJs(XUIComponentBase oComp) {

			StringBuilder sOut = new StringBuilder(200);

			AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor) oComp;

			ExtConfig oHtmlCfg = new ExtConfig("Ext.form.HtmlEditor");
			oHtmlCfg.addJSString("id", oComp.getClientId() + "_editor");
			oHtmlCfg.addJSString("name", oComp.getClientId());
			oHtmlCfg.addJSString("renderTo", oComp.getClientId());
			oHtmlCfg.addJSString("value", JavaScriptUtils.safeJavaScriptWrite(
					oHtmlComp.getDisplayValue(), '\''));
			// oHtmlCfg.add( "width" , oHtmlComp.getWidth() );
			// oHtmlCfg.add( "height" , "0" );
			oHtmlCfg.add("frame", false);
			// oHtmlCfg.addJSString( "layout" , "break" );
			oHtmlCfg.add("enableColors", true);
			oHtmlCfg.add("enableAlignments", true);
			oHtmlCfg.add("enableLinks", false);

			if (oHtmlComp.isDisabled())
				oHtmlCfg.add("disabled", true);

			if (oHtmlComp.isVisible())
				oHtmlCfg.add("visible", false);

			handleAdvancedEditorPlugins(oHtmlComp, oHtmlCfg, sOut);

			sOut.append("Ext.onReady( function() {");
			sOut.append("try { \n");
			oHtmlCfg.renderExtConfig(sOut);
			sOut.append("} catch(e){} \n");
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
						if (imageObj.length() > 0 && contentObjs.length() > 0 )
						{
							b.append(",new Ext.ux.form.HtmlEditor.XEO_IMAGE(),");
							b.append("new Ext.ux.form.HtmlEditor.XEO_CONTENTS()");
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

			String sHtml = XUIRequestContext.getCurrentContext()
					.getRequestParameterMap().get(component.getClientId());
			((AttributeHtmlEditor) component).setSubmittedValue(sHtml);

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
