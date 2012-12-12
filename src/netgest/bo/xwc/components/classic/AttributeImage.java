package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.io.InputStream;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.io.iFile;
/**
 * This component bind to a AttributeBinnaryData from the XEO Model a instead of render the value
 * he generates a image based on the AttributeBinnaryData 
 * @author jcarreira
 *
 */
public class AttributeImage extends ViewerOutputSecurityBase {
	
    private XUIBaseProperty<String> beanProperty         		= new XUIBaseProperty<String>( "beanProperty", this, "currentData" );
    
    private XUIBaseProperty<ValueExpression> dataType       	= new XUIBaseProperty<ValueExpression>( "dataType", this );
    private XUIBaseProperty<ValueExpression> dataFieldConnector = new XUIBaseProperty<ValueExpression>( "dataFieldConnector", this );
    
    private XUIViewStateBindProperty<String> width = new XUIViewStateBindProperty<String>( "width", this, String.class );
	private XUIViewStateBindProperty<String> height = new XUIViewStateBindProperty<String>( "height", this, String.class );
	private XUIStateBindProperty<String> objectAttribute = new XUIStateBindProperty<String>( "objectAttribute", this, String.class );
	private XUIViewStateBindProperty<String> className = new XUIViewStateBindProperty<String>( "className", this, String.class );
	private XUIViewStateBindProperty<String> align = new XUIViewStateBindProperty<String>( "align", this, String.class );
	private XUIStateBindProperty<String> emptyImage = new XUIStateBindProperty<String>( "emptyImage", this, String.class );
    private XUIViewStateBindProperty<String> label  = new XUIViewStateBindProperty<String>( "label", this, String.class );

    private XUIViewStateBindProperty<Boolean> 	renderLabel = new XUIViewStateBindProperty<Boolean>( "renderLabel", this, "true", Boolean.class );

    private XUIBaseProperty<Object>     renderedValue = new XUIBaseProperty<Object>( "renderedValue", this );

	
    private AttributeLabel      oLabel = null;


    @Override
	public void initComponent() {
    	
    	if( beanProperty.isDefaultValue() )
    		this.setBeanProperty(getBeanId() + ".currentData");
    	
        createChildComponents();
        
    }
    
    public void createChildComponents() {
        
        if( oLabel == null ) {

            this.oLabel = new AttributeLabel();
            this.oLabel.setId( getId() +  "_l" );
            this.oLabel.setText( getLabel() );

            if( getValueExpression("visible") != null )
                this.oLabel.setVisible( getValueExpression("visible").getExpressionString() );

            if( getValueExpression( "securityPermissions" )!=null )
            	oLabel.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

            if( getValueExpression( "viewerSecurityPermissions" )!=null )
            	oLabel.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );

            oLabel.setInstanceId( getInstanceId() );
            
            if (!getRenderLabel()) //Fix for Ajax Requests, get renders children does not work for ajax requests 
            	oLabel.setRenderComponent( false );

            this.getChildren().add( this.oLabel );
        }
    }
    
    public String getWidth() {
		return width.getEvaluatedValue();
	}

	public void setWidth(String width) {
		this.width.setExpressionText( width );
	}

	public String getHeight() {
		return height.getEvaluatedValue();
	}

	public void setHeight(String height) {
		this.height.setExpressionText( height );
	}

	public String getObjectAttribute() {
		return objectAttribute.getEvaluatedValue();
	}

	public String getClassName() {
		return className.getEvaluatedValue();
	}

	public void setClassName(String className) {
		this.className.setExpressionText( className );
	}

	public String getAlign() {
		return align.getEvaluatedValue();
	}

	public void setAlign(String align) {
		this.align.setExpressionText( align ); 
	}

	public String getLabel() {
		return label.getEvaluatedValue();
	}

	public void setLabel(String label) {
		this.label.setExpressionText( label ); 
	}
	
	public String getEmptyImage() {
		return emptyImage.getEvaluatedValue();
	}

	public void setEmptyImage(String emptyImage) {
		this.emptyImage.setExpressionText( emptyImage );
	}

	public boolean getRenderLabel() {
		return renderLabel.getEvaluatedValue();
	}

	public void setRenderLabel(String renderLabel) {
		this.renderLabel.setExpressionText( renderLabel ); 
	}
	
	
	public void setObjectAttribute( String sObjectAttribute ) {
        

        String sBeanExpression = "#{" + getBeanId()+ "." + getBeanProperty() + "." + sObjectAttribute;

        this.objectAttribute.setExpressionText( sObjectAttribute );

        // Value
        this.setValueExpression(
            "value", createValueExpression( sBeanExpression +  ".value}", Object.class ) 
        );

        // Label
        if (label.isDefaultValue())
        	setLabel( sBeanExpression +  ".label}" );

        // Config
        this.dataType.setValue( 
                createValueExpression( sBeanExpression + ".dataType}", Byte.class ) 
            );

        this.dataFieldConnector.setValue( 
                createValueExpression( sBeanExpression + "}", DataFieldConnector.class ) 
            );
        
	}
	

    public void setBeanProperty(String beanProperty) {
        this.beanProperty.setValue( beanProperty ); 
        
        if( getObjectAttribute() != null )
            setObjectAttribute( getObjectAttribute() );
        
    }
    
    public void setDataType(String dataType) {
        this.dataType.setValue( createValueExpression( dataType, String.class ) );
    }

	public byte getDataType() {
        if( dataType.getValue() != null && dataType.getValue().isLiteralText() ) {
            return Byte.valueOf( dataType.getValue().getExpressionString() );
        }
        else if ( dataType.getValue() != null ) {
             return (Byte)dataType.getValue().getValue( getELContext() );
        }
        return Byte.MIN_VALUE;
    }
    
    public String getBeanProperty() {
        return beanProperty.getValue();
    }
	
    public DataFieldConnector getDataFieldConnector() {
        if( this.dataFieldConnector.getValue() != null ) {
             return (DataFieldConnector)this.dataFieldConnector.getValue().getValue( getELContext() );
        }
        return null;
    }

    @Override
    public StateChanged wasStateChanged2() {
        if( super.wasStateChanged2() == StateChanged.NONE ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return StateChanged.FOR_RENDER;
            }
        }
        else {
            return StateChanged.FOR_RENDER;
        }
        return StateChanged.NONE;
    }

    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }
    
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		super.restoreState(context, state);
        if( this.getChildCount() > 0 ) {
            this.oLabel = (AttributeLabel)getChild( 0 );
        }
	}


	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {
    	
    	
        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            

            AttributeImage	oImg = (AttributeImage)oComp;
            
            XUIResponseWriter w = getResponseWriter();
            
            String labelPos = "";
            int labelWidth = 0;
            Rows r = (Rows)oImg.findParentComponent( Rows.class );

            
            if( r!=null ) {
            	labelPos 	= r.getLabelPosition();
            	labelWidth	= r.getLabelWidth();
            }
            
            w.startElement( DIV, oComp );	
            w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null );
            
            w.startElement( TABLE, oComp );
            //w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null );
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null );
            

            if( !"Top".equalsIgnoreCase( labelPos ) ) 
            {
	            w.startElement("COLGROUP", oComp);
	            if( oImg.getRenderLabel() ){
		            w.startElement(COL, oComp );
		            w.writeAttribute( HTMLAttr.WIDTH, labelWidth + "px", null );
		            w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
		            w.writeAttribute(HTMLAttr.ALIGN, labelPos, null); 
		            w.endElement("COL");
	            }

	            w.startElement(COL, oComp );
	            w.endElement("COL");
	            w.endElement("COLGROUP");
            } else {
	            w.startElement("COLGROUP", oComp);
	            w.startElement(COL, oComp );
	            w.writeAttribute( HTMLAttr.WIDTH, "100%", null );
	            w.endElement("COL");
	            w.endElement("COLGROUP");
            }
            
            
            w.startElement( TR, oComp );
            
            if( oImg.getRenderLabel() )
            {
            	w.startElement( TD, oComp );
            	w.writeAttribute( HTMLAttr.HEIGHT , "20px", null );
            	w.writeAttribute(HTMLAttr.ALIGN, labelPos, null);
            	w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
                if( oImg.oLabel != null ) {
                	oImg.oLabel.encodeAll();    
                }
                w.endElement( TD );
            }

            if( "Top".equalsIgnoreCase( labelPos ) ) 
            {
                w.endElement( TR );
                w.startElement( TR, oComp );
            }
            
            w.startElement( TD, oComp );

            Object oValue = oImg.getValue();
            String sEmptyImg = oImg.getEmptyImage();
            if( (sEmptyImg != null && sEmptyImg.length() > 0) || oValue != null ) {
	            w.startElement( HTMLTag.IMG , oComp);
	            if( oImg.getWidth() != null )
	            	w.writeAttribute("width", oImg.getWidth(), null);
	            if( oImg.getHeight() != null )
	            	w.writeAttribute("height", oImg.getHeight(), null);
	            if( oImg.getClassName() != null )
	            	w.writeAttribute("class", oImg.getClassName(), null);
	            if( oImg.getClassName() != null )
	            	w.writeAttribute("align", oImg.getAlign(), null);
	            
	            //'javax.faces.ViewState'
	            String sViewState = getRequestContext().getViewRoot().getViewState();
	            //xvw.servlet
	            String sServletId = oComp.getClientId();
	            
	            String sActionUrl = getRequestContext().getAjaxURL();
	            
	            if( sActionUrl.indexOf('?') != -1 ) {
	            	sActionUrl += "&";
	            }
	            else {
	            	sActionUrl += "?";
	            }
	            sActionUrl += "javax.faces.ViewState=" + sViewState;
	            sActionUrl += "&xvw.servlet=" + sServletId;
	
	            if( oValue != null )
	            	w.writeAttribute( "src" , sActionUrl, null );
	            else
	            	w.writeAttribute( "src" , sEmptyImg, null );
	            
	            w.endElement( HTMLTag.IMG );
            }
            w.endElement( TD );
            
            w.endElement( TR ); 
            w.endElement( TABLE );
            w.endElement( DIV );
        	
        }

		public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	AttributeImage oFile = (AttributeImage)comp;
        	DataFieldConnector oConnector = oFile.getDataFieldConnector();
        	
        	//TODO: Criar no AttributeConnector suporte para ficheiros
        	if( oConnector instanceof XEOObjectAttributeConnector ) {
        		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
        		try {
					iFile file = oXeoConnector.getAttributeHandler().getValueiFile();
					if( file != null ) {
						String sName = file.getName();
						
						ServletContext oCtx = (ServletContext)getFacesContext().getExternalContext().getContext();
						
			            String mimetype = oCtx.getMimeType(sName.toLowerCase());

			            resp.setHeader("Cache-Control","private");               
			            ServletOutputStream so = response.getOutputStream(); 

			            Long FileSize = new Long(file.length()); 
			            int xfsize = FileSize.intValue(); 

			            response.setContentType(mimetype); 
			            response.setContentLength(xfsize); 

			            int rb=0; 
			            InputStream is= null;
			            try { 
			                is = file.getInputStream();
			                byte[] a=new byte[4*1024];
			                while ((rb=is.read(a)) > 0) { 
			                    so.write(a,0,rb); 
			                } 
			                is.close();
			            } 
			            catch (Exception e) 
			            {
			            }
			            finally
			            {
			                if( is != null ) is.close();
			            }
			            so.close(); 
					}
				} catch (boRuntimeException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}
        	}
		}
	    @Override
	    public boolean getRendersChildren() {
	    	return true;
	    }

	    @Override
	    public void encodeChildren(XUIComponentBase component) throws IOException {
	    	
	    }
    }


}
