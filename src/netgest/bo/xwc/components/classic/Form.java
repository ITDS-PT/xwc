package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.PropertyNotFoundException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;

/**
 * A form with dependences control. This form controls the dependence between attributes a 
 * force a data submit when a dependent depends of another component
 * 
 * @author jcarreira
 *
 */
public class Form extends XUIForm
{
	
	private XUIViewStateProperty<String> 	encType 			= 
		new XUIViewStateProperty<String>("encType", this, null );
	
	private XUIBindProperty<Byte> 		securityPermissions = 
		new XUIBindProperty<Byte>("securityPermissions", this, Byte.class, "#{viewBean.securityPermissions}" );

	
	public void setSecurityPermissions( String sExpressionString ) {
		this.securityPermissions.setExpressionText( sExpressionString );
	}
	
	public byte getSecurityPermissions() {
		// Quando n�o existe bean associada ao viewer ignora seguran�as.
		// N�o existe outra maneira j� que o propriedade � sempre resolvida atrav�s da class
		// ScopedAttributeELResolver 
		
		if( getRequestContext().getViewRoot().getBean( getBeanId() ) != null ) {
			try {
				byte ret =  this.securityPermissions.getEvaluatedValue();
				return ret;
			} catch (PropertyNotFoundException e) {
				// viewBean doesn't have securityPermissions property... return full control;
			}
		}
		return SecurityPermissions.FULL_CONTROL;
	}
	
	public String getEncType() {
		return encType.getValue();
	}

	public void setEncType(String encType) {
		this.encType.setValue( encType );
	}

	private HashMap<String, Boolean> oComponentDependeces;

    @Override
	public void preRender() {
        
        super.preRender();
        
        // Scan dependeces on Attributes 
        oComponentDependeces = new HashMap<String, Boolean>();
        buildComponentDependences( this );

    }
    
    
    @Override
    public void initComponent(){
    	super.initComponent();
    }
    
    public void buildComponentDependences( UIComponent oComponent ) {
        Iterator<UIComponent> oChildsIterator;
        UIComponent           oKid;
        AttributeBase         oAttributeBase;
        String[]              oComponentDependences; 
        
        
        
        //return;
    
        oChildsIterator = oComponent.getFacetsAndChildren();
        while( oChildsIterator.hasNext() ) {
            
            oKid = oChildsIterator.next();
            if( oKid instanceof AttributeBase ) {
                
                oAttributeBase = (AttributeBase)oKid;
                try {
                	oComponentDependences = oAttributeBase.getDependences();
	                for (int i = 0;oComponentDependences != null && i < oComponentDependences.length; i++) {
	                    if( !oComponentDependeces.containsKey( oComponentDependences[i] ) )
	                    {
	                        oComponentDependeces.put( oComponentDependences[i], Boolean.TRUE );
	                    }
	                }
                } catch (Exception e) {};
	               
            }
            buildComponentDependences( oKid );
        }

    }

    @Override
	public void processDecodes(FacesContext context) {
		// TODO Auto-generated method stub
		super.processDecodes(context);
    }

	public boolean haveDependents( String objectAttrbuteName ) {
        
        return oComponentDependeces != null && oComponentDependeces.containsKey( objectAttrbuteName );
        
    }

    public static class XEOHTMLRenderer extends XUIRenderer {
    

        protected static final Logger logger = Logger.getLogger( Form.class );

        private static final String[] ATTRIBUTES =
              AttributeManager.getAttributes(AttributeManager.Key.FORMFORM);
    
        private boolean writeStateAtEnd;
    
    
        // ------------------------------------------------------------ Constructors
    
    
        public XEOHTMLRenderer() {
            WebConfiguration webConfig = WebConfiguration.getInstance();
            writeStateAtEnd =
                 webConfig.isOptionEnabled(
                        com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.WriteStateAtFormEnd
                      );
    
        }
    
        // ---------------------------------------------------------- Public Methods
    
    
        @Override
        public void decode( XUIComponentBase component ) {
                
            FacesContext context = getFacesContext();
            rendererParamsNotNull(getFacesContext(), component);
                    
            // Was our form the one that was submitted?  If so, we need to set
            // the indicator accordingly..
            String clientId = component.getClientId();
            Map<String, String> requestParameterMap = context.getExternalContext()
                  .getRequestParameterMap();
            if (requestParameterMap.containsKey(clientId)) {
                if (logger.isFinestEnabled()) {
                    logger.log(LoggerLevels.FINEST,
                               LoggerMessageLocalizer.getMessage("UIFORM_WITH_CLIENT_ID_SUBMITED"),
                               clientId);
                }
                ((XUIForm) component).setSubmitted(true);
            } else {
                ((XUIForm) component).setSubmitted(false);
            }
    
        }

        @Override
        public void encodeBegin(XUIComponentBase component)
              throws IOException {
    
            FacesContext context = getFacesContext();
            rendererParamsNotNull(getFacesContext(), component);
            
            Form oForm;
            
            oForm = (Form)component;
    
            if (!shouldEncode(component)) {
                return;
            }
    
            XUIResponseWriter writer = (XUIResponseWriter)context.getResponseWriter();
            
            assert(writer != null);
            String clientId = component.getClientId();
            // since method and action are rendered here they are not added
            // to the pass through attributes in Util class.
            writer.write('\n');
            writer.startElement("form", component);

            writer.writeAttribute("id", clientId, "clientId");
            writer.writeAttribute("name", clientId, "name");
            writer.writeAttribute("method", "post", null);
            
            if( oForm.getEncType() != null ) {
                writer.writeAttribute("enctype", oForm.getEncType(), "encType");
            }
            
            writer.writeAttribute("action", getActionStr(context), null);
            String styleClass =
                  (String) component.getAttributes().get("styleClass");
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, "styleClass");
            }
            String acceptcharset = (String)
                  component.getAttributes().get("acceptcharset");
            if (acceptcharset != null) {
                writer.writeAttribute("accept-charset", acceptcharset,
                                      "acceptcharset");
            }
        }
    
    
        @Override
        public void encodeEnd( XUIComponentBase component)
              throws IOException {
    
            FacesContext context = getFacesContext();
            ResponseWriter writer = context.getResponseWriter();
            XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();

            String clientId = component.getClientId();

            RenderKitUtils.renderPassThruAttributes(writer,
                    component,
                    ATTRIBUTES);
			// this hidden field will be checked in the decode method to
			// determine if this form has been submitted.         
			writer.startElement("input", component);
			writer.writeAttribute("type", "hidden", "type"); 
			writer.writeAttribute("name", clientId,
			  "clientId");
			writer.writeAttribute("value", clientId, "value");
			writer.endElement("input");
			
			writer.startElement("input", component);
			writer.writeAttribute("type", "hidden", "type"); 
			writer.writeAttribute("name", "xvw.ajax.submitUrl",
			  null );
			writer.writeAttribute("value", oRequestContext.getAjaxURL(), "value");
			writer.endElement("input");

			writer.startElement("input", component);
			writer.writeAttribute("type", "hidden", "type"); 
			writer.writeAttribute("name", "xvw.ajax.resourceUrl",
			  null );
			writer.writeAttribute("value", oRequestContext.getResourceUrl(""), "value");
			writer.endElement("input");

			
			if (!writeStateAtEnd) {
				context.getApplication().getViewHandler().writeState(context);
			}
            
            
            rendererParamsNotNull(context, component);
            if (!shouldEncode(component)) {
                return;
            }
    
            // Render the end tag for form
            assert(writer != null);
            if (writeStateAtEnd) {
                context.getApplication().getViewHandler().writeState(context);
            }
            writer.writeText("\n", component, null);
            writer.endElement("form");
    
        }
    
        // --------------------------------------------------------- Private Methods
    
    
        /**
         * @param context FacesContext for the response we are creating
         *
         * @return Return the value to be rendered as the <code>action</code> attribute
         *  of the form generated for this component.
         */
        private static String getActionStr(FacesContext context) {
    
            String viewId = context.getViewRoot().getViewId();
            String actionURL =
                  context.getApplication().getViewHandler().
                        getActionURL(context, viewId);
            return (context.getExternalContext().encodeActionURL(actionURL));
    
        }
        
        protected void rendererParamsNotNull(FacesContext context,
                                             UIComponent component) {

            com.sun.faces.util.Util.notNull("context", context);
            com.sun.faces.util.Util.notNull("component", component);
            
        }

        protected boolean shouldEncode(UIComponent component) {

            // suppress rendering if "rendered" property on the component is
            // false.
            if (!component.isRendered()) {
                if (logger.isFinestEnabled()) {
                    logger.log(LoggerLevels.FINEST,MessageLocalizer.getMessage("END_ENCODING_COMPONENT")+
                               "{0}"+MessageLocalizer.getMessage("SINCE_REDERED_ATTRIBUTE_IS_SET_TO_FALSE"),
                               component.getId());
                }
                return false;
            }
            return true;

        }

        protected boolean shouldDecode(UIComponent component) {

            if (com.sun.faces.util.Util.componentIsDisabledOrReadonly(component)) {
                if (logger.isFinestEnabled()) {
                    logger.log(LoggerLevels.FINEST,MessageLocalizer.getMessage("NO_DECODING_NECESSARY_SINCE_THE_COMPONENT")+
                               " {0} "+MessageLocalizer.getMessage("IS_DISABLED_OR_READ_ONLY"),
                               component.getId());
                }
                return false;
            }
            return true;

        }

		@Override
		public boolean getRendersChildren() {
			return true;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void encodeChildren(FacesContext context, UIComponent component)
				throws IOException {
			
			if( ( ((Form)component).getSecurityPermissions()&SecurityPermissions.READ) == SecurityPermissions.READ ) {
				super.encodeChildren(context, component);
			}
			else {
//				if ( !getRequestContext().isPortletRequest() ) {
//					XUIResponseWriter w = getResponseWriter();
//					w.startElement( HTMLTag.H1, component);
//					w.writeAttribute( HTMLAttr.STYLE , "color:red", null);
//					w.writeText( "N�o t�m permiss�es para realizar a opera��o!", null );
//					w.endElement( HTMLTag.H1);
//				}
			}
		}

		@Override
		public void encodeChildren(XUIComponentBase component)
				throws IOException {
			
			if( ( ((Form)component).getSecurityPermissions()&SecurityPermissions.READ)  == SecurityPermissions.READ ) {
				super.encodeChildren( component );
			}
			else {
				XUIResponseWriter w = getResponseWriter();
				w.startElement( HTMLTag.H1, component);
				w.writeAttribute( HTMLAttr.STYLE , "color:red", null);
				w.writeText( ComponentMessages.FORM_NOT_ENOUGH_PERMISSION.toString(), null );
				w.endElement( HTMLTag.H1);
			}
		}

    } // end of class FormRenderer

}
