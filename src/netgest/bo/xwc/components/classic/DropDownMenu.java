package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.context.ResponseWriter;
import netgest.bo.xwc.framework.components.XUIOption;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Drop down menu
 * @author jcarreira
 *
 */
public class DropDownMenu extends XUIComponentBase
{
    public DropDownMenu()
    {
    }

    public void renderBeginTag( ResponseWriter w ) throws IOException
    {
/*
        w.startElement("table", null );
        w.writeAttribute( "class","mnubarFlat", null );
        w.writeAttribute( "cellspacing","0", null );
        w.writeAttribute( "cellpadding","0", null );
        
        w.startElement("tbody", null );

        w.startElement("tr", null );
        w.writeAttribute( "height","48", null );
        w.startElement("td", null );
        w.writeAttribute( "width","9", null );
        w.startElement("img", null );
        w.writeAttribute( "src","/xwc/templates/menu/std/mnu_vSpacer.gif", null );
        w.endElement( "img" );
        w.endElement( "td" );

        w.startElement("td", null );
        w.writeAttribute( "class","icMenu", null );
        w.writeAttribute( "nowrap","1", null );
        
        renderOptions(w);
        
        w.endElement( "td" );
        w.endElement( "tr" );
        w.endElement( "tbody" );
        w.endElement( "table" );
*/        
    }
    
    public void renderOptions( ResponseWriter w ) throws IOException
    {
        for (int i = 0; i < getChildCount(); i++) 
        {
            renderTopOption( w, (XUIOption)getChild(i), i );
        }
        
    }
    
    public void renderTopOption( ResponseWriter w, XUIOption o, int idx ) throws IOException
    {
        w.startElement("span",null);
        w.writeAttribute( "class","menu", null );
        w.writeAttribute( "menu",getId()+":"+idx+":1", null );
        w.writeText( o.getLabel(), null );
        if( o.getChildCount() > 0 )
        {
            w.startElement("table", null);
            w.writeAttribute( "class","icMenu", null );
            w.writeAttribute( "menu",getId()+":"+idx+":1", null );

            w.startElement("colgroup", null);

            w.startElement("col", null);
            w.writeAttribute( "class","mnuLeft", null );
            w.endElement("col");

            w.startElement("col", null);
            w.endElement("col");
            
            w.endElement("colgroup");

            w.startElement("tbody", null);

            renderMenuOptions( w, o, idx, 1 );
            
            w.endElement("tbody");
            w.endElement("table");
            
        }   
        w.endElement("span");
    }
    
    private void renderMenuOptions( ResponseWriter w, XUIOption o,  int idx, int level ) throws IOException
    {
        for (int i = 0; i < o.getChildCount(); i++) 
        {
        
            XUIOption soption = (XUIOption)o.getChild(i);
            if( soption.getLabel() != null && soption.getLabel().length() > 0 )
            {
                renderOption( w, soption, idx, level++ );    
            }
            else
            {
                renderSpacer( w, soption, idx, level++ );
            }
        }
    }
    
    public void renderOption( ResponseWriter w, XUIOption o,  int idx, int level ) throws IOException
    {
        w.startElement("tr", null);
        w.writeAttribute( "menu",getId()+":"+idx+":"+level, null );
        w.startElement("td", null);
        w.write("&nbsp;");
        w.endElement( "td" );
        
        w.startElement("td", null);
        w.writeAttribute( "class","mnuItm", null );
        w.writeAttribute( "nowrap","1", null );
        w.startElement("span", null);
        w.writeAttribute( "class","mnuSubItem", null );
        w.writeText( o.getLabel(), null );
        w.endElement( "span" );
        w.endElement( "td" );
        w.endElement( "tr" );
    }
    
    public void renderSpacer( ResponseWriter w, XUIOption o,  int idx, int level ) throws IOException
    {
        
        w.startElement( "tr", null );
        w.writeAttribute( "class","mnuSpacer", null );
        w.startElement( "td", null );
        w.write( "&nbsp;" );
        w.endElement( "td" );
        w.startElement( "td", null );
        w.startElement("hr",null);
        w.writeAttribute( "class","mnuSpacer", null );
        w.endElement( "hr" );
        w.endElement( "td" );
        w.endElement( "tr" );
    }
    
    

    public void renderEndTag() throws IOException
    {
    }

    @Override
	public boolean getRendersChildren()
    {
        return false;
    }
}
