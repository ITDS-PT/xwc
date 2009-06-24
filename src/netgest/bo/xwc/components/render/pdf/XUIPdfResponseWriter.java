package netgest.bo.xwc.components.render.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.framework.XUIResponseWriter;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

import static com.lowagie.text.Element.*;

public class XUIPdfResponseWriter extends XUIResponseWriter  {

	private java.io.OutputStream out;
	
	private Document pdfDocument;
	
	private Stack elements = new Stack(); 
	
	public XUIPdfResponseWriter( OutputStream out ) {
		super( null,null,null );
		this.out = out;
	}
	
	@Override
	public ResponseWriter cloneWithWriter(Writer writer) {
		throw new RuntimeException("XUIPdfResponseWriter cannot be cloned with writer");
	}

	@Override
	public void endDocument() throws IOException {
		pdfDocument.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return "application/pdf";
	}

	@Override
	public void startDocument() throws IOException {
		try {
			Document document = new Document(PageSize.A4, 10, 10, 10, 10);
			PdfWriter.getInstance(document, out );
		} catch (DocumentException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public void startElement(String type, UIComponent component) throws IOException {

/*		
		// TODO Auto-generated method stub
		int iType = Integer.parseInt( type );
		
		Element oElement;
		
		switch( iType ) {
			case ANCHOR:
				oElement = new Anchor(  )
			case ANNOTATION:
			case AUTHOR:
			case CELL:
			case CHAPTER:
			case CHUNK:
			case CREATIONDATE: 
			case CREATOR:
			case HEADER:
			case IMGRAW:
			case IMGTEMPLATE:
			case JPEG:
			case KEYWORDS: 
			case LIST:
			case LISTITEM: 
			case MARKED:
			case MULTI_COLUMN_TEXT: 
			case PARAGRAPH:
			case PHRASE:
			case PRODUCER:
			case PTABLE:
			case RECTANGLE: 
			case ROW:
			case SECTION:
			case SUBJECT:
			case TABLE:
			case TITLE:
		}
*/		
	}

	public void startElement(Element oElement, UIComponent component)
			throws IOException {
		this.elements.add( oElement );
	}

	public void endElement(Element oElement) {
		
		if( this.elements.peek() == oElement )
			this.elements.pop();
		else
			throw new RuntimeException( "Close of this element is out of order. The childest element at this point is " + 
					((Element)this.elements.peek()).toString()
			);
		
	}

	@Override
	public void writeAttribute(String name, Object value, String property)
			throws IOException {
	}

	@Override
	public void writeComment(Object comment) throws IOException {
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {
	}

	@Override
	public void writeText(Object text, String property) throws IOException {
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property)
			throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
	}

	public void setHeader( HeaderFooter elem ) {
		pdfDocument.setHeader( elem );
	}

	public void setFooter( HeaderFooter elem ) {
		pdfDocument.setFooter( elem );
	}
}
