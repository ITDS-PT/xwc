package netgest.bo.xwc.components.classic.grid;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class EditableColumnAttribute extends Attribute implements Column {
	
	
	private final ColumnAttribute columnAttribute = new ColumnAttribute();
	
	
	
	/**
	 * @param value
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#applyRenderTemplate(java.lang.Object)
	 */
	public String applyRenderTemplate(Object value) {
		return columnAttribute.applyRenderTemplate(value);
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getAlign()
	 */
	public String getAlign() {
		return columnAttribute.getAlign();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getDataField()
	 */
	public String getDataField() {
		return columnAttribute.getDataField();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getLabel()
	 */
	public String getLabel() {
		return columnAttribute.getLabel();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getLookupViewer()
	 */
	public String getLookupViewer() {
		return columnAttribute.getLookupViewer();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getSqlExpression()
	 */
	public String getSqlExpression() {
		return columnAttribute.getSqlExpression();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#getWidth()
	 */
	public String getWidth() {
		return columnAttribute.getWidth();
	}

	//TODO: Corrigir isto
	public void initComponent() {
		
	}
	
	public void initColumnAttribute() {
		if( getChildCount() == 0 ) {
			super.initComponent();
			super.processInitComponents();
		}
	}
	
	@Override
	public void createChildComponents() {
		// TODO Auto-generated method stub
		super.createChildComponents();
	}
	
	//TODO: Corrigir isto
	@Override
	public String[] getDependences() {
		GridPanel grid = (GridPanel)findParentComponent(GridPanel.class);
		if( grid!=null && grid.isRenderedOnClient() ) {
			return super.getDependences();
		}
		return null;
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isContentHtml()
	 */
	public boolean isContentHtml() {
		return columnAttribute.isContentHtml();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isEnableAggregate()
	 */
	public boolean isEnableAggregate() {
		return columnAttribute.isEnableAggregate();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isGroupable()
	 */
	public boolean isGroupable() {
		return columnAttribute.isGroupable();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isHidden()
	 */
	public boolean isHidden() {
		return columnAttribute.isHidden();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isHideable()
	 */
	public boolean isHideable() {
		return columnAttribute.isHideable();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isResizable()
	 */
	public boolean isResizable() {
		return columnAttribute.isResizable();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isSearchable()
	 */
	public boolean isSearchable() {
		return columnAttribute.isSearchable();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#isSortable()
	 */
	public boolean isSortable() {
		return columnAttribute.isSortable();
	}

	/**
	 * @return
	 * @see netgest.bo.xwc.framework.components.XUIComponentBase#saveState()
	 */
	public Object saveState() {
		GridPanel panel = (GridPanel)findParentComponent(GridPanel.class);
		
		DataRecordConnector c = panel.getDataSource().iterator().next();
		HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();
		request.setAttribute("currentData", c );
		
		
		Object[] state = {
				columnAttribute.saveState(),
				super.saveState()
		};
		return state;
	}
	
	@Override
	public void restoreState(Object oState) {
		Object[] state = (Object[])oState;
		columnAttribute.restoreState(state[0] );
		super.restoreState(state[1]);
		
	}

	/**
	 * @param align
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setAlign(java.lang.String)
	 */
	public void setAlign(String align) {
		columnAttribute.setAlign(align);
	}

	/**
	 * @param sBooleanContentHtml
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setContentHtml(boolean)
	 */
	public void setContentHtml(boolean sBooleanContentHtml) {
		columnAttribute.setContentHtml(sBooleanContentHtml);
	}

	/**
	 * @param sBooleanContentHtml
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setContentHtml(java.lang.String)
	 */
	public void setContentHtml(String sBooleanContentHtml) {
		columnAttribute.setContentHtml(sBooleanContentHtml);
	}

	/**
	 * @param dataField
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setDataField(java.lang.String)
	 */
	public void setDataField(String dataField) {
		columnAttribute.setDataField(dataField);
	}

	/**
	 * @param groupable
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setGroupable(java.lang.String)
	 */
	public void setGroupable(String groupable) {
		columnAttribute.setGroupable(groupable);
	}

	/**
	 * @param sBooleanText
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setHidden(java.lang.String)
	 */
	public void setHidden(String sBooleanText) {
		columnAttribute.setHidden(sBooleanText);
	}

	/**
	 * @param sBooleanText
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setHideable(java.lang.String)
	 */
	public void setHideable(String sBooleanText) {
		columnAttribute.setHideable(sBooleanText);
	}

	/**
	 * @param label
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		columnAttribute.setLabel(label);
	}

	/**
	 * @param lookupViewerExpr
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setLookupViewer(java.lang.String)
	 */
	public void setLookupViewer(String lookupViewerExpr) {
		columnAttribute.setLookupViewer(lookupViewerExpr);
	}

	/**
	 * @param sBooleanText
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setResizable(java.lang.String)
	 */
	public void setResizable(String sBooleanText) {
		columnAttribute.setResizable(sBooleanText);
	}

	/**
	 * @param searchable
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setSearchable(java.lang.String)
	 */
	public void setSearchable(String searchable) {
		columnAttribute.setSearchable(searchable);
	}

	/**
	 * @param sortable
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setSortable(java.lang.String)
	 */
	public void setSortable(String sortable) {
		columnAttribute.setSortable(sortable);
	}

	/**
	 * @param sqlexpressionEl
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setSqlExpression(java.lang.String)
	 */
	public void setSqlExpression(String sqlexpressionEl) {
		columnAttribute.setSqlExpression(sqlexpressionEl);
	}

	/**
	 * @param sWidth
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setWidth(java.lang.String)
	 */
	public void setWidth(String sWidth) {
		columnAttribute.setWidth(sWidth);
	}

	/**
	 * @param wrap
	 * @see netgest.bo.xwc.components.classic.ColumnAttribute#setWrapText(java.lang.String)
	 */
	public void setWrapText(String wrap) {
		columnAttribute.setWrapText(wrap);
	}
	
	
	public AttributeBase getAttributeComponent() {
		return (AttributeBase)findComponent( AttributeBase.class );
	}
	
	@Override
	public boolean wrapText() {
		// TODO Auto-generated method stub
		return false;
	}
		
	public static class XEOHTMLRenderer extends Attribute.XEOHTMLRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {

            Attribute oAttr = (Attribute)oComp;
            XUIResponseWriter w = getResponseWriter();
            String labelPos 	= "left";
            int	   labelWidth   = 100;
            
            w.startElement( TABLE, oComp );
            w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null ); 
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 

            w.startElement("COLGROUP", oComp);
            w.startElement(COL, oComp );
            w.endElement("COL");
            w.endElement("COLGROUP");
            w.startElement( TR, oComp );

            if( "1".equals( oAttr.getRenderInput() ) )
            {
                // Write Control
                w.startElement( TD, oComp );
                
                AttributeBase inpComp = (AttributeBase)oAttr.getInputComponent();
                
                if( inpComp != null ) {
                	//inpComp.encodeAll();
                	w.writeText( inpComp.getDisplayValue(), null );
                } else {
                	w.writeText( "- Invalid [" + oAttr.getObjectAttribute() + "] -", null );
                }
                w.endElement( TD );
            }
            w.endElement( TR );
            w.endElement( TABLE );
        }
	}

	@Override
	public boolean useValueOnLov() {
		return false;
	}
	
	
/*
	public class EditableColumnRenderer implements GridColumnRenderer {

		@Override
		public String render(GridPanel grid, DataRecordConnector record, DataFieldConnector field) {
			String uniqueIdentifier = grid.getRowUniqueIdentifier();
			Attribute att;
			
			FacesContext context = FacesContext.getCurrentInstance();
			
	        RenderKitFactory renderFactory = (RenderKitFactory)
	            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
			
	        RenderKit renderKit =
	                renderFactory.getRenderKit(context, getRequestContext().getViewRoot().getRenderKitId());
			
			CharArrayWriter w = new CharArrayWriter();
		
		    XUIWriteBehindStateWriter headWriter =
		          new XUIWriteBehindStateWriter(w,
		                                     context,
		                                     WRITER_BUFFER_SIZE);
	
		    XUIWriteBehindStateWriter bodyWriter =
		          new XUIWriteBehindStateWriter(w,
		                                     context,
		                                     WRITER_BUFFER_SIZE);
	
		    XUIWriteBehindStateWriter footerWriter =
		          new XUIWriteBehindStateWriter(w,
		                                     context, 
		                                     WRITER_BUFFER_SIZE);
	
		    ResponseWriter newWriter;
		    newWriter = renderKit.createResponseWriter(bodyWriter,
		                                                   null,
		                                                   ((HttpServletRequest)getRequestContext().getRequest()).getCharacterEncoding());
	
		    context.setResponseWriter(newWriter);
	    
		    // Sets the header and footer writer
		    if( newWriter instanceof XUIResponseWriter )
		    	PackageIAcessor.setHeaderAndFooterToWriter( (XUIResponseWriter)newWriter, headWriter, footerWriter );
	
		    newWriter.startDocument();
		    
		    getAttributeComponent().encodeAll( context );
		    newWriter.endDocument();
	
		    // Write header part of document
		    headWriter.flushToWriter( false );
		    headWriter.release();
	    
		    bodyWriter.flushToWriter(false);
		    // clear the ThreadLocal reference.
		    bodyWriter.release();
	    
		    // Write footer (Before tag </body> ) part.
		    footerWriter.flushToWriter( false );
		    footerWriter.release();
	    
		    String temp = w.toString();
		    
		    XMLDocument doc = ngtXMLUtils.loadXML( temp );
		    String xmlContent =ngtXMLUtils.getXML(doc);			
			
		}
	}
*/
}
