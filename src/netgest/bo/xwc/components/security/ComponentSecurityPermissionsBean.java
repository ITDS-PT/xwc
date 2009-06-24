package netgest.bo.xwc.components.security;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.beans.XEOBaseOrphanEdit;

public class ComponentSecurityPermissionsBean extends XEOBaseOrphanEdit {

	private String read;
	private String write;
	private String add;
	private String remove;
	private String execute;
	private String fullControl;
	
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	public String getWrite() {
		return write;
	}
	public void setWrite(String write) {
		this.write = write;
	}
	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	public String getRemove() {
		return remove;
	}
	public void setRemove(String remove) {
		this.remove = remove;
	}
	public String getExecute() {
		return execute;
	}
	public void setExecute(String execute) {
		this.execute = execute;
	}
	public String getFullControl() {
		return fullControl;
	}
	public void setFullControl(String fullControl) {
		this.fullControl = fullControl;
	}
	
	@Override
	public void confirm() throws boRuntimeException {
		updatePermissions();
		super.confirm();
	}
	
	public void load() {
		loadPermissions();
	}
	
	public void loadPermissions() {
		try {
			boObject obj = getXEOObject();
			long permissions = obj.getAttribute("accessLevel").getValueLong();
			this.read = (SecurityPermissions.READ & permissions) > 0?"1":"0";
			this.write = (SecurityPermissions.WRITE & permissions) > 0?"1":"0";
			this.add = (SecurityPermissions.ADD & permissions) > 0?"1":"0";
			this.remove = (SecurityPermissions.DELETE & permissions) > 0?"1":"0";
			this.execute = (SecurityPermissions.EXECUTE & permissions) > 0?"1":"0";
			this.fullControl = permissions == SecurityPermissions.FULL_CONTROL?"1":"0";
		} catch (boRuntimeException e) {
			
			throw new RuntimeException(e);
			
		}
	}
	
	public void updatePermissions() {
		try {
			boObject obj = getXEOObject();
			long value = 0;
			if( "1".equals( this.fullControl ) ) {
				value = SecurityPermissions.FULL_CONTROL;
			}
			else {
				value += "1".equals( this.read )?SecurityPermissions.READ:0;
				value += "1".equals( this.write )?SecurityPermissions.WRITE:0;
				value += "1".equals( this.add )?SecurityPermissions.ADD:0;
				value += "1".equals( this.remove )?SecurityPermissions.DELETE:0;
				value += "1".equals( this.execute )?SecurityPermissions.EXECUTE:0;
			}
			obj.getAttribute("accessLevel").setValueLong( value );
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
}
