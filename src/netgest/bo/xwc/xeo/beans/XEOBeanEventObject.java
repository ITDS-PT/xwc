package netgest.bo.xwc.xeo.beans;

public class XEOBeanEventObject extends java.util.EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8182710134387457477L;
	
	private boolean canceled = false;
	private XEOEditBeanEventType type;
	
	
	
	public XEOBeanEventObject( XEOBaseBean source, XEOEditBeanEventType type ) {
		super( source );
		this.type = type;
	}
	
	@Override
	public XEOBaseBean getSource() {
		return (XEOBaseBean)super.getSource();
	}
	
	public void cancelEvent() {
		this.canceled = true;
	}
	
	public XEOEditBeanEventType getEventType() {
		return this.type;
	}
	
	public boolean isCanceled() {
		return this.canceled;
	}
	
}
