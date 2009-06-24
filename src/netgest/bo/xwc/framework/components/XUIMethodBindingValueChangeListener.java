package netgest.bo.xwc.framework.components;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;


/**
 * <p><strong>XUIMethodBindingValueChangeListener</strong> is an {@link
 * ValueChangeListenerListener} that wraps a {@link MethodBinding}. When it
 * receives a {@link ValueChangeEvent}, it executes a method on an
 * object identified by the {@link MethodBinding}.</p>
 */

class XUIMethodBindingValueChangeListener extends XUIMethodBindingAdapterBase implements ValueChangeListener, StateHolder {


    // ------------------------------------------------------ Instance Variables
    
    private MethodBinding methodBinding = null;

    public XUIMethodBindingValueChangeListener() {}

    
    /**
     * <p>Construct a {@link ValueChangeListener} that contains a {@link
     * MethodBinding}.</p>
     */
    public XUIMethodBindingValueChangeListener(MethodBinding methodBinding) {

        super();
        this.methodBinding = methodBinding;

    }

    public MethodBinding getWrapped() {
	return methodBinding;
    }


    // ------------------------------------------------------- Event Method

    /**
     * @throws NullPointerException {@inheritDoc}     
     * @throws AbortProcessingException {@inheritDoc}     
     */ 
    public void processValueChange(ValueChangeEvent actionEvent) throws AbortProcessingException {
                         
        if (actionEvent == null) {
            throw new NullPointerException();
        }
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            methodBinding.invoke(context, new Object[] {actionEvent});
        } 
	catch (EvaluationException ee) {
	    Throwable cause = 
		this.getExpectedCause(AbortProcessingException.class, ee);
	    if (cause instanceof AbortProcessingException) {
		throw  ((AbortProcessingException) cause);
	    }
	    if (cause instanceof RuntimeException) {
		throw ((RuntimeException) cause);
	    }
	    throw new IllegalStateException(ee.getMessage());
        }
    }

    // 
    // Methods from StateHolder
    //

    

    public Object saveState(FacesContext context) {
	Object result = null;
	if (!tranzient) {
	    if (methodBinding instanceof StateHolder) {
		Object [] stateStruct = new Object[2];
		
		// save the actual state of our wrapped methodBinding
		stateStruct[0] = ((StateHolder)methodBinding).saveState(context);
		// save the class name of the methodBinding impl
		stateStruct[1] = methodBinding.getClass().getName();

		result = stateStruct;
	    }
	    else {
		result = methodBinding;
	    }
	}

	return result;
    }

    public void restoreState(FacesContext context, Object state) {
	// if we have state
	if (null == state) {
	    return;
	}
	
	if (!(state instanceof MethodBinding)) {
	    Object [] stateStruct = (Object []) state;
	    Object savedState = stateStruct[0];
	    String className = stateStruct[1].toString();
	    MethodBinding result = null;
	    
	    Class toRestoreClass;
	    if (null != className) {
		try {
		    toRestoreClass = loadClass(className, this);
		}
		catch (ClassNotFoundException e) {
		    throw new IllegalStateException(e.getMessage());
		}
		
		if (null != toRestoreClass) {
		    try {
			result = 
			    (MethodBinding) toRestoreClass.newInstance();
		    }
		    catch (InstantiationException e) {
			throw new IllegalStateException(e.getMessage());
		    }
		    catch (IllegalAccessException a) {
			throw new IllegalStateException(a.getMessage());
		    }
		}
		
		if (null != result && null != savedState) {
		    // don't need to check transient, since that was
		    // done on the saving side.
		    ((StateHolder)result).restoreState(context, savedState);
		}
		methodBinding = result;
	    }
	}
	else {
	    methodBinding = (MethodBinding) state;
	}
    }

    private boolean tranzient = false;

    public boolean isTransient() {
	return tranzient;
    }

    public void setTransient(boolean newTransientValue) {
	tranzient = newTransientValue;
    }

    //
    // Helper methods for StateHolder
    //

    private static Class loadClass(String name, 
            Object fallbackClass) throws ClassNotFoundException {
        ClassLoader loader =
            Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }
        return Class.forName(name, false, loader);
    }
}
