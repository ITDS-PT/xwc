package netgest.bo.xwc.framework;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.framework.jsf.XUIStaticField;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;
import netgest.utils.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XUIScriptContext {

    
    private boolean bInlineWritePending = false;
    
    public static final RenderType TYPE_INCLUDE = new RenderType( 1, "TYPE_INCLUDE" );
    public static final RenderType TYPE_TEXT = new RenderType( 2, "TYPE_TEXT" );
    
    public static final RenderPosition POSITION_HEADER = new RenderPosition( 1, "POSITION_HEADER" );
    public static final RenderPosition POSITION_INLINE = new RenderPosition( 2, "POSITION_INLINE" );
    public static final RenderPosition POSITION_FOOTER = new RenderPosition( 3, "POSITION_FOOTER" );

    protected Vector<Fragment> oFragmentsVector = new Vector<Fragment>();

    public void add( RenderPosition oPosition, String sId, CharSequence sCode ) {
        int iIdIdx;
        if( oPosition == null ) throw new IllegalArgumentException(ExceptionMessage.OPOSITION_CANNOT_BE_NULL.toString());
        if( sId == null ) throw new IllegalArgumentException(ExceptionMessage.SID_CANNOT_BE_NULL.toString());
        
        // Muda a flag para notificar que existem  inline para ser feito o render
        bInlineWritePending = bInlineWritePending || oPosition == POSITION_INLINE;
        
        iIdIdx = getIndexById( sId );
        if( iIdIdx != -1 )
        	oFragmentsVector.remove( iIdIdx );
        
    	oFragmentsVector.add( new Fragment( TYPE_TEXT, oPosition, sId , sCode ) );

        //oFragmentsVector.set( iIdIdx,new Fragment( TYPE_TEXT, oPosition, sId , sCode ) );
    }
    
    private void addInclude(RenderPosition oPosition, String sId, String sURL, String urlAppend){
    	int iIdIdx;
        assert oPosition != null : MessageLocalizer.getMessage("OPOSITION_CANNOT_BE_NULL");
        assert oPosition != POSITION_INLINE : "Cannot include scripts in POSTION_INLINE";
        assert sId != null: MessageLocalizer.getMessage("SID_CANNOT_BE_NULL");

        // Muda a flag para notificar que existem  inline para ser feito o render
        bInlineWritePending = bInlineWritePending || oPosition.getValue() == POSITION_INLINE.getValue();

        iIdIdx = getIndexById( sId );
        if (StringUtils.hasValue( urlAppend )){
        	sURL = sURL + "?id=" +  urlAppend;
        }
        if( iIdIdx == -1 )
            oFragmentsVector.add( new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );
        else
            oFragmentsVector.set( iIdIdx,new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );
    }
    
    
    public void addSystemInclude(RenderPosition oPosition, String sId, String sURL, String systemId){
    	addInclude( oPosition, sId, sURL , systemId );
    }

    public void addInclude( RenderPosition oPosition, String sId, String sURL ) {
    	addInclude( oPosition, sId, sURL, null );
    }

    public void addIncludeBefore( String sBeforeId, RenderPosition oPosition, String sId, String sURL ) {
        if( sBeforeId == null ) throw new IllegalArgumentException( ExceptionMessage.SBEFOREID_CANNOT_BE_NULL.toString());
        if( oPosition == null ) throw new IllegalArgumentException(ExceptionMessage.OPOSITION_CANNOT_BE_NULL.toString());
        if( sId != null ) throw new IllegalArgumentException(ExceptionMessage.SID_CANNOT_BE_NULL.toString());

        // Muda a flag para notificar que existem  inline para ser feito o render
        bInlineWritePending = bInlineWritePending || oPosition.getValue() == POSITION_INLINE.getValue();

        int iReferPos = getIndexById( sBeforeId );
        if( iReferPos != -1 ) {
            oFragmentsVector.add( iReferPos, new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );            
        }
        else {
            oFragmentsVector.add( new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );            
        }

    }
    
    public void addIncludeAfter( String sAfterId, RenderPosition oPosition, String sId, String sURL ) {
        if( sAfterId == null ) throw new IllegalArgumentException(ExceptionMessage.SAFTERID_CANNOT_BE_NULL.toString());
        if( oPosition == null ) throw new IllegalArgumentException( ExceptionMessage.OPOSITION_CANNOT_BE_NULL.toString());
        if( sId == null ) throw new IllegalArgumentException(ExceptionMessage.SID_CANNOT_BE_NULL.toString());

        // Muda a flag para notificar que existem  inline para ser feito o render
        bInlineWritePending = bInlineWritePending || oPosition.getValue() == POSITION_INLINE.getValue();

        int iReferPos = getIndexById( sAfterId );
        if( iReferPos != -1 && iReferPos+1 < oFragmentsVector.size() ) {
            oFragmentsVector.add( iReferPos + 1, new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );            
        }
        else {
            oFragmentsVector.add( new Fragment( TYPE_INCLUDE, oPosition, sId, sURL ) );            
        }
        
    }

    public Fragment[] getFragments( final RenderPosition oRenderPosition ) {

        Vector         oRetFragmentVector = new Vector();
        Fragment oFragment;
        
        Enumeration<Fragment> oFragmentsEnum = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();
            if( oRenderPosition == oFragment.getPosition() )
                oRetFragmentVector.add( oFragment );
        }
        return (Fragment[])oRetFragmentVector.toArray( new Fragment[ oRetFragmentVector.size() ]  );
        
    }
    
    public Fragment get( String sId ) {
        int iPos;
        Fragment oRetFragment;
        
        oRetFragment = null;
        
        iPos = getIndexById( sId );
        
        if( iPos > -1 ) {
            oRetFragment = oFragmentsVector.get( iPos );
        }
        
        return oRetFragment;
        
    }

    public int getIndexById( String sId ) {
        if( sId == null ) throw new IllegalArgumentException(ExceptionMessage.SID_CANNOT_BE_NULL.toString());
        
        Fragment oCurrentFragment;
        int iPos;

        iPos = 0;
        
        Enumeration<Fragment> oEnum = oFragmentsVector.elements();
        while( oEnum.hasMoreElements() ) {
            oCurrentFragment = oEnum.nextElement();
            if( sId.equals( oCurrentFragment.getScriptId() )  ) {
                break;
            }
            iPos++;
        }
        if( iPos >= oFragmentsVector.size() ) {
            iPos = -1;
        }
        return iPos;
    }
    
    protected void renderInLine( XUIResponseWriter w ) throws IOException {
        if( bInlineWritePending ) {
            Enumeration<Fragment> oFragmentsEnum;
            Fragment oFragment;
            
            oFragmentsEnum  = oFragmentsVector.elements();
            while( oFragmentsEnum.hasMoreElements() ) {
                oFragment = oFragmentsEnum.nextElement();    
                switch( oFragment.getPosition().getValue() ) {
                    case 1:
                        // Do not render this elements for now
                        break;
                    case 2:
                        // Render inline elements 
                        w.writeText("\n", null );
                        w.startElement("script", null );
                        w.writeAttribute("id", oFragment.getScriptId(), null);
                        w.writeAttribute("type", "text/javascript", null);
                        renderFragment( w, oFragment );
                        w.writeText("\n", null );
                        w.endElement("script" );
                        break;
                    case 3:
                        // Do not render this elements for now
                        break;
                }
            }
        }
    }

    public void render( XUIResponseWriter hW, XUIResponseWriter bW, XUIResponseWriter fW ) throws IOException {
        Enumeration<Fragment> oFragmentsEnum;
        Fragment oFragment;
        
        boolean bInLineTagOpen = false;
        
        oFragmentsEnum  = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();
            if( oFragment.getPosition() == POSITION_HEADER ) 
            {
                if( oFragment.getType() == TYPE_INCLUDE ) {
                    if( bInLineTagOpen ) {
                        hW.write('\n');
                        hW.endElement("script");   
                        bInLineTagOpen = false;
                    }
                }
                else if ( oFragment.getType() == TYPE_TEXT ) {
                    if( !bInLineTagOpen ) {
                        hW.write('\n');
                        hW.startElement("script", null );
                        hW.writeAttribute("type", "text/javascript", null);
                        bInLineTagOpen = true;
                    }
                }
                renderFragment( hW, oFragment );
            }
        }
        if( bInLineTagOpen ) {
            hW.write('\n');
            hW.endElement("script");    
        }
        
        bInLineTagOpen = false;
        oFragmentsEnum  = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();
            if( oFragment.getPosition() == POSITION_FOOTER ) 
            {
                if( oFragment.getType() == TYPE_INCLUDE ) {
                    if( bInLineTagOpen ) {
                        fW.endElement("script");   
                        bInLineTagOpen = false;
                    }
                }
                else if ( oFragment.getType() == TYPE_TEXT ) {
                    if( !bInLineTagOpen ) {
                        fW.startElement("script", null );
                        bInLineTagOpen = true;
                    }
                }
                renderFragment( fW, oFragment );
            }
        }
        if( bInLineTagOpen ) {
            hW.write('\n');
            fW.endElement("script");    
        }
        hW.write('\n');

    }

    protected void renderFragment( XUIResponseWriter w, Fragment oFragment ) throws IOException {
        // <script type="text/javascript" src="/en/us/shared/core/2/js/js.ashx?s=Csp;shared"></script>
        
        assert w!=null:new IllegalArgumentException( MessageLocalizer.getMessage("RESPONSE_WRITER_CANNOT_BE_NULL") );

        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        if( !oFragment.isRendered() ) {
            if( oFragment.getType().getValue() == TYPE_INCLUDE.getValue()  ) {
                w.write('\n');
                w.startElement("script", null);
                w.writeAttribute("type","text/javascript", null );
                w.writeAttribute("id", oFragment.getScriptId(), null );
                if ( oFragment.isExternal() )
                	w.writeAttribute("src", oFragment.getContent().toString() , null );
                else
                	w.writeAttribute("src", oRequestContext.getResourceUrl( oFragment.getContent().toString() ), null );
                w.endElement("script");
            }
            else if ( oFragment.getType().getValue() == TYPE_TEXT.getValue() ) {
                w.write('\n');
                w.write( String.valueOf( oFragment.getContent() ) );
            }
            else {
                throw new IllegalStateException(ExceptionMessage.SCRIPT_RENDER_TYPE_IS_INVALID.toString());
            }
            oFragment.markRenderered();
        }
    }
    
    public void renderForAjaxDom( Element oXvwAjaxResp ) {
        
        Element headElement;
        Element footerElement;
        NodeList list;
        
        Document oXmlDoc = oXvwAjaxResp.getOwnerDocument();
        
        list = oXvwAjaxResp.getElementsByTagName("headerScripts");
        if( list.getLength() == 0 ) {
            headElement = oXmlDoc.createElement("headerScripts");
            oXvwAjaxResp.appendChild( headElement );
        }
        else {
            headElement = (Element)list.item( 0 );
        }
        
        list = oXvwAjaxResp.getElementsByTagName("footerScripts");
        if( list.getLength() == 0 ) {
            footerElement = oXmlDoc.createElement("footerScripts");
            oXvwAjaxResp.appendChild( footerElement );
        }
        else {
            footerElement = (Element)list.item( 0 );
        }


        Enumeration<Fragment> oFragmentsEnum;
        Fragment oFragment;

        oFragmentsEnum  = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();    
            switch( oFragment.getPosition().getValue() ) {
                case 1:
            		renderFragmentForAjaxDom( headElement, oFragment );
                    break;
                case 2:
                    // Do not render inline 
                    break;
                case 3:
            		renderFragmentForAjaxDom( footerElement, oFragment );
                    break;
            }
        }


    }
    
    protected void renderFragmentForAjaxDom( Element Element, Fragment oFragment ) {
        Element headScriptElement;
        Document oXmlDoc;

        oXmlDoc = Element.getOwnerDocument();

        headScriptElement = oXmlDoc.createElement("script");
        headScriptElement.setAttribute( "id", oFragment.getScriptId() );
        
        if( oFragment.getType() == TYPE_TEXT )
        	headScriptElement.appendChild( oXmlDoc.createCDATASection( String.valueOf( oFragment.getContent() ) ) );
        else
        	headScriptElement.setAttribute("src", String.valueOf( oFragment.getContent() ) );

        Element.appendChild( headScriptElement );
    }
    

    public static class Fragment 
    {
        private RenderType iType;
        private RenderPosition iPosition;
        private CharSequence sContent;
        private String sScriptId;
        private boolean allreadyRendered;
        private boolean isExternal = false;
        
        public Fragment( RenderType iType, RenderPosition iPosition, String sScriptId, CharSequence sContent ) {
            this.sScriptId = sScriptId;
            this.iPosition = iPosition;
            this.iType = iType;
            this.sContent = sContent;
            if (this.iType == TYPE_INCLUDE && isExternalScript( sContent ))
            	this.isExternal = true; 
        }

		private boolean isExternalScript( CharSequence sContent ) {
			if (sContent == null)
				return false;
			return sContent.toString().startsWith( "http" ) || sContent.toString().startsWith( "//" );
		}

        public CharSequence getContent() {
            return sContent;
        }

        public String getScriptId() {
            return sScriptId;
        }

        public RenderType getType() {
            return iType;
        }

        public RenderPosition getPosition() {
            return iPosition;
        }
        
        public void markRenderered() {
            allreadyRendered = true;
        }
        
        public boolean isRendered() {
            return allreadyRendered;
        }
        
        public boolean isExternal(){
        	return isExternal;
        }

        @Override
        public String toString() {
        	return getScriptId() + " " + getPosition() + " " + getContent();
        }
    }
    
    
    protected static final class RenderPosition extends XUIStaticField {
        public RenderPosition( int iValue, String sName ) {
            super( iValue, sName );
        }
    }

    protected static final class RenderType extends XUIStaticField {
        public RenderType( int iValue, String sName ) {
            super( iValue, sName );
        }
    }

}
