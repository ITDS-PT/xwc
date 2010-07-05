/**
 * 
 */
package netgest.bo.xwc.framework.jsf.renderers;

import java.io.IOException;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.framework.components.XUIForm;

import com.sun.faces.RIConstants;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.CommandLinkRenderer;
import com.sun.faces.util.MessageUtils;

/**
 * 
 * An override to
 * 
 * @author PedroRio
 * 
 */
public class XUICommandLinkRenderer extends CommandLinkRenderer {

	private static final String[] ATTRIBUTES = AttributeManager
			.getAttributes(AttributeManager.Key.COMMANDLINK);

	private static final String SCRIPT_STATE = RIConstants.FACES_PREFIX
			+ "scriptState";

	/**
	 * 
	 */
	public XUICommandLinkRenderer() {
		super();
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {

		rendererParamsNotNull(context, component);

		if (!shouldEncode(component)) {
			return;
		}

		boolean componentDisabled = Boolean.TRUE.equals(component
				.getAttributes().get("disabled"));

		String formClientId = getFormClientId(component, context);
		if (formClientId == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING,
						"Component {0} must be enclosed inside a form",
						component.getId());
			}
		}

		if (componentDisabled || formClientId == null) {
			renderAsDisabled(context, component);
		} else {
			if (!hasScriptBeenRendered(context)) {
				RenderKitUtils.renderFormInitScript(
						context.getResponseWriter(), context);
				setScriptAsRendered(context);
			}
			renderAsActive(context, component);
		}

	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {

		rendererParamsNotNull(context, component);

		if (!shouldEncode(component)) {
			return;
		}

		ResponseWriter writer = context.getResponseWriter();
		assert (writer != null);
		String formClientId = getFormClientId(component, context);
		if (formClientId == null) {
			writer
					.write(MessageUtils
							.getExceptionMessageString(MessageUtils.COMMAND_LINK_NO_FORM_MESSAGE_ID));
			writer.endElement("span");
			return;
		}

		if (Boolean.TRUE.equals(component.getAttributes().get("disabled"))) {
			writer.endElement("span");
		} else {
			writer.endElement("a");
		}

	}

	/**
	 * @param context
	 *            the <code>FacesContext</code> for the current request
	 * 
	 * @return <code>true</code> If the <code>add/remove</code> javascript has
	 *         been rendered, otherwise <code>false</code>
	 */
	private static boolean hasScriptBeenRendered(FacesContext context) {

		return (context.getExternalContext().getRequestMap().get(SCRIPT_STATE) != null);

	}

	/**
	 * <p>
	 * Set a flag to indicate that the <code>add/remove</code> javascript has
	 * been rendered for the current form.
	 * 
	 * @param context
	 *            the <code>FacesContext</code> of the current request
	 */
	private static void setScriptAsRendered(FacesContext context) {

		context.getExternalContext().getRequestMap().put(SCRIPT_STATE,
				Boolean.TRUE);

	}

	/**
	 * <p>
	 * Utility method to return the client ID of the parent form.
	 * </p>
	 * 
	 * @param component
	 *            typically a command component
	 * @param context
	 *            the <code>FacesContext</code> for the current request
	 * 
	 * @return the client ID of the parent form, if any
	 */
	private static String getFormClientId(UIComponent component,
			FacesContext context) {

		UIComponent form = getMyForm(component);
		if (form != null) {
			return form.getClientId(context);
		}

		return null;

	}

	@Override
	protected void renderAsActive(FacesContext context, UIComponent command)
			throws IOException {

		ResponseWriter writer = context.getResponseWriter();
		assert (writer != null);
		String formClientId = getFormClientId(command, context);
		if (formClientId == null) {
			return;
		}

		// make link act as if it's a button using javascript
		writer.startElement("a", command);
		writeIdAttributeIfNecessary(context, writer, command);
		writer.writeAttribute("href", "#", "href");
		RenderKitUtils.renderPassThruAttributes(writer, command, ATTRIBUTES);
		RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, command);

		// render onclick
		String userOnclick = (String) command.getAttributes().get("onclick");
		StringBuffer sb = new StringBuffer(128);
		boolean userSpecifiedOnclick = (userOnclick != null && !""
				.equals(userOnclick));

		// if user specified their own onclick value, we are going to
		// wrap their js and the injected js each in a function and
		// execute them in a choose statement, if the user didn't specify
		// an onclick, the original logic executes unaffected
		if (userSpecifiedOnclick) {
			sb.append("var a=function(){");
			userOnclick = userOnclick.trim();
			sb.append(userOnclick);
			if (userOnclick.charAt(userOnclick.length() - 1) != ';') {
				sb.append(';');
			}
			sb.append("};var b=function(){");
		}

		Param[] params = getParamList(command);
		String commandClientId = command.getClientId(context);
		String target = (String) command.getAttributes().get("target");
		if (target != null) {
			target = target.trim();
		} else {
			target = "";
		}

		sb.append(getOnClickScript(formClientId, commandClientId, target,
				params));

		// we need to finish wrapping the injected js then
		if (userSpecifiedOnclick) {
			sb.append("};return (a()==false) ? false : b();");
		}

		writer.writeAttribute("onclick", sb.toString(), "onclick");

		writeCommonLinkAttributes(writer, command);

		// render the current value as link text.
		writeValue(command, writer);
		writer.flush();

	}

	private static UIComponent getMyForm(UIComponent component) {

		UIComponent parent = component.getParent();
		while (parent != null) {
			if (parent instanceof UIForm || parent instanceof XUIForm) {
				break;
			}
			parent = parent.getParent();
		}

		return parent;

	}

}
