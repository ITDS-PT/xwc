package netgest.bo.xwc.components;

import netgest.bo.xwc.framework.jsf.XUIWriterElementConst;

public class HTMLTag {
    
    public static final Tag A = new Tag( "a" );
    public static final Tag ABBR = new Tag( "abbr" );
    public static final Tag ACRONYM = new Tag( "acronym" );
    public static final Tag ADDRESS = new Tag( "address" );
    @Deprecated
    public static final Tag APPLET = new Tag( "applet" );
    public static final Tag AREA = new Tag( "area" );
    public static final Tag B = new Tag( "b" );
    public static final Tag BASE = new Tag( "base" );
    @Deprecated
    public static final Tag BASEFONT = new Tag( "basefont" );
    public static final Tag BDO = new Tag( "bdo" );
    public static final Tag BIG = new Tag( "big" );
    public static final Tag BLOCKQUOTE = new Tag( "blockquote" );
    public static final Tag BODY = new Tag( "body" );
    public static final Tag BR = new Tag( "br" );
    public static final Tag BUTTON = new Tag( "button" );
    public static final Tag CAPTION = new Tag( "caption" );
    @Deprecated
    public static final Tag CENTER = new Tag( "center" );
    public static final Tag CITE = new Tag( "cite" );
    public static final Tag CODE = new Tag( "code" );
    public static final Tag COL = new Tag( "col" );
    public static final Tag COLGROUP = new Tag( "colgroup" );
    public static final Tag DD = new Tag( "dd" );
    public static final Tag DEl = new Tag( "del" );
    public static final Tag DIV = new Tag( "div" );
    public static final Tag DFN = new Tag( "dfn" );
    @Deprecated
    public static final Tag DIR = new Tag( "dir" );
    public static final Tag DL = new Tag( "dl" );
    public static final Tag DT = new Tag( "dt" );
    public static final Tag EM = new Tag( "em" );
    public static final Tag FIELDSET = new Tag( "fieldset" );
    @Deprecated
    public static final Tag FONT = new Tag( "font" );
    public static final Tag FORM = new Tag( "form" );
    public static final Tag FRAME = new Tag( "frame" );
    public static final Tag FRAMESET = new Tag( "frameset" );
    public static final Tag H1 = new Tag( "h1" );
    public static final Tag H2 = new Tag( "h2" );
    public static final Tag H3 = new Tag( "h3" );
    public static final Tag H4 = new Tag( "h4" );
    public static final Tag H5 = new Tag( "h5" );
    public static final Tag H6 = new Tag( "h6" );
    public static final Tag HEAD = new Tag( "head" );
    public static final Tag HR = new Tag( "hr" );
    public static final Tag HTML = new Tag( "html" );
    public static final Tag I = new Tag( "i" );
    public static final Tag IFRAME = new Tag( "iframe" );
    public static final Tag IMG = new Tag( "img" );
    public static final Tag INPUT = new Tag( "input" );
    public static final Tag INS = new Tag( "ins" );
    @Deprecated
    public static final Tag ISINDEX = new Tag( "isindex" );
    public static final Tag KBD = new Tag( "kbd" );
    public static final Tag LABEL = new Tag( "label" );
    public static final Tag LEGEND = new Tag( "legend" );
    public static final Tag LI = new Tag( "li" );
    public static final Tag LINK = new Tag( "link" );
    public static final Tag MAP = new Tag( "map" );
    @Deprecated
    public static final Tag MENU = new Tag( "menu" );
    public static final Tag META = new Tag( "meta" );
    public static final Tag NOFRAMES = new Tag( "noframes" );
    public static final Tag NOSCRIPT = new Tag( "noscript" );
    public static final Tag OBJECT = new Tag( "object" );
    public static final Tag OL = new Tag( "ol" );
    public static final Tag OPTGROUP = new Tag( "optgroup" );
    public static final Tag OPTION = new Tag( "option" );
    public static final Tag P = new Tag( "p" );
    public static final Tag PARAM = new Tag( "param" );
    public static final Tag PRE = new Tag( "pre" );
    public static final Tag Q = new Tag( "q" );
    @Deprecated
    public static final Tag S = new Tag( "s" );
    public static final Tag SAMP = new Tag( "samp" );
    public static final Tag SCRIPT = new Tag( "script" );
    public static final Tag SELECT = new Tag( "select" );
    public static final Tag SMALL = new Tag( "small" );
    public static final Tag SPAN = new Tag( "span" );
    @Deprecated
    public static final Tag STRIKE = new Tag( "strike" );
    public static final Tag STRONG = new Tag( "strong" );
    public static final Tag STYLE = new Tag( "style" );
    public static final Tag SUB = new Tag( "sub" );
    public static final Tag SUP = new Tag( "sup" );
    public static final Tag TABLE = new Tag( "table" );
    public static final Tag TBODY = new Tag( "tbody" );
    public static final Tag TD = new Tag( "td" );
    public static final Tag TEXTAREA = new Tag( "textarea" );
    public static final Tag TFOOT = new Tag( "tfoot" );
    public static final Tag TH = new Tag( "th" );
    public static final Tag THEAD = new Tag( "thead" );
    public static final Tag TITLE = new Tag( "title" );
    public static final Tag TR = new Tag( "tr" );
    public static final Tag TT = new Tag( "tt" );
    public static final Tag UL = new Tag( "ul" );
    public static final Tag VAR = new Tag( "var" );

    private static final class Tag extends XUIWriterElementConst {
            public Tag( String sAttrName ) {
                super( sAttrName );
            }
    }

}
