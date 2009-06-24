package netgest.bo.xwc.components.render.pdf;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

import com.sun.faces.renderkit.RenderKitImpl;

public class PdfRenderKit extends RenderKitImpl {

	@Override
	public void addRenderer(String arg0, String arg1, Renderer arg2) {
		// TODO Auto-generated method stub
		super.addRenderer(arg0, arg1, arg2);
	}

	@Override
	public ResponseStream createResponseStream(OutputStream out) {
		// TODO Auto-generated method stub
		return super.createResponseStream(out);
	}

	@Override
	public ResponseWriter createResponseWriter(Writer arg0, String arg1,
			String arg2) {
		// TODO Auto-generated method stub
		return super.createResponseWriter(arg0, arg1, arg2);
	}

	@Override
	public Renderer getRenderer(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return super.getRenderer(arg0, arg1);
	}

	@Override
	public synchronized ResponseStateManager getResponseStateManager() {
		// TODO Auto-generated method stub
		return super.getResponseStateManager();
	}


}
