package netgest.bo.xwc.components.template.xeo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.template.base.TemplateComponentBase;
import netgest.bo.xwc.components.template.wrappers.LovItemWrapper;
import netgest.bo.xwc.framework.XUIBaseProperty;

public class XeoLov extends TemplateComponentBase {

	/**
	 * The name of the property to export the list. i.e. If name = 'list'
	 * inside the template you can do ${list}
	 */
	@RequiredAlways
	private XUIBaseProperty<String> name = new
			XUIBaseProperty<String>( "name", this );
	
	public String getName(){
		return name.getValue();
	}
	
	public void setName( String value ){
		this.name.setValue( value );
	}
	
	/**
	 * The name of the property to export the list. i.e. If name = 'list'
	 * inside the template you can do ${list}
	 */
	@RequiredAlways
	private XUIBaseProperty<String> lovName = new
			XUIBaseProperty<String>( "lovName", this );
	
	public String getLovName(){
		return lovName.getValue();
	}
	
	public void setLovName( String value ){
		this.lovName.setValue( value );
	}
	
	public EboContext getEboContext(){
		return boApplication.currentContext().getEboContext();
	}
	
	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> props = super.getProperties();
		String lovName = getLovName();
		try {
			lovObject obj = LovManager.getLovObject( getEboContext(), lovName );
			obj.beforeFirst();
			List<LovItemWrapper> list = new LinkedList<LovItemWrapper>();
			while (obj.next()){
				list.add( new LovItemWrapper( obj.getCode(), obj.getDescription() ) );
			}
			props.put( getName(), list );
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
		return props;
	}
	
}
