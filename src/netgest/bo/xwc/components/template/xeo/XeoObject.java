package netgest.bo.xwc.components.template.xeo;

import java.util.Map;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.template.base.TemplateComponentBase;
import netgest.bo.xwc.components.template.wrappers.WrapperFactory;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;

public class XeoObject extends TemplateComponentBase {
	
	
	/**
	 * The boql expression used to retrieve the list of objects
	 */
	@RequiredAlways
	private XUIBindProperty<String> boql = new
			XUIBindProperty<String>( "boql", this, String.class );
	
	
	public String getBoql(){
		return boql.getEvaluatedValue();
	}
	
	public void setBoql(String boqlExpr){
		boql.setExpressionText( boqlExpr );
	}
	
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
	
	public EboContext getEboContext(){
		return boApplication.currentContext().getEboContext();
	}
	
	
	
	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> props = super.getProperties();
		boObjectList list = boObjectList.list( getEboContext(), getBoql() );
		if (list.next()){
			boObject objectToExport;
			try {
				objectToExport = list.getObject();
				props.put( getName(), WrapperFactory.wrapObject( objectToExport ) );
			} catch ( boRuntimeException e ) {
				e.printStackTrace();
			}
		}
		return props;
	}

}
