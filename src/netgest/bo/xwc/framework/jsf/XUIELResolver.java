package netgest.bo.xwc.framework.jsf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import netgest.bo.xwc.framework.XUIRequestContext;

public class XUIELResolver extends ELResolver {
    @Override
    public Class<?> getCommonPropertyType(ELContext eLContext, Object object) {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext eLContext, Object object) {
        return null;
    }

    @Override
    public Class<?> getType(ELContext eLContext, Object object, Object object2) {
        return null;
    }

    @Override
    public Object getValue(ELContext eLContext, Object base, Object property ) 
    {
        Object oResult;
        String sProperty = String.valueOf( property );
        
        XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
        
        // Resolve Viewer Beans
        oResult = oRequestContext.getViewRoot().getBean( sProperty );
        if( oResult != null ) {
            eLContext.setPropertyResolved( true );
            return oResult;
        }
        
        // Resolve Bean in Session
        oResult = oRequestContext.getSessionContext().getBean( sProperty );
        if( oResult != null ) {
            eLContext.setPropertyResolved( true );
            return oResult;
        }
        
        // Resolve Request Attributes
        oResult = oRequestContext.getAttribute( sProperty );
        if( oResult != null ) {
            eLContext.setPropertyResolved( true );
            return oResult;
        }

        // Resolve Request Attributes
        oResult = oRequestContext.getSessionContext().getAttribute( sProperty );
        if( oResult != null ) {
            eLContext.setPropertyResolved( true );
            return oResult;
        }
        eLContext.setPropertyResolved( false );
        return null;

    }

    @Override
    public boolean isReadOnly(ELContext eLContext, Object object, Object object2) {
        return true;
    }

    @Override
    public void setValue(ELContext eLContext, Object object, Object object2, Object object3) {
    }
}
