package netgest.bo.xwc.framework.components;

import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.framework.XUIRequestContext;

public class ViewRootBeanFinder {
	
	private Object bean = null;
	
	public Object getBean(XUIViewRoot root, String sBeanName) {
		bean = XUIRequestContext.getCurrentContext().getSessionContext()
				.getAttribute(root.getBeanPrefix() + sBeanName);
		if (bean == null){
			//findBean(root,sBeanName);
		}
		return bean; 
	}
	

	private void findBean(UIComponent comp, String name){
		Iterator<UIComponent> lst = comp.getFacetsAndChildren();
		while (lst.hasNext()){
		UIComponent component = lst.next();
			if (component instanceof XUIViewRoot){
				bean = ((XUIViewRoot) component).getBean( name );
				if (bean == null){
					findBean( component , name );
				} else
					return;
			} else{ 
				findBean(component,name);
			}
		}
		
	}
	
}
