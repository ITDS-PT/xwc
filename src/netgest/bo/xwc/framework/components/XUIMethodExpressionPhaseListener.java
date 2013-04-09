package netgest.bo.xwc.framework.components;

import javax.el.MethodExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.event.ValueChangeListener;

public class XUIMethodExpressionPhaseListener implements PhaseListener, StateHolder {

	// ------------------------------------------------------ Instance Variables
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MethodExpression methodExpression = null;
    private PhaseId phaseId = null;
    private boolean isBefore = false;
    private boolean tranzient = false;

    
    public XUIMethodExpressionPhaseListener() {}
    
    /**
     * <p>Construct a {@link ValueChangeListener} that contains a {@link
     * MethodBinding}.</p>
     */
    public XUIMethodExpressionPhaseListener(MethodExpression methodExpression, PhaseId phaseId,  boolean isBefore ) {

        super();
        this.methodExpression = methodExpression;
        this.phaseId = phaseId;
        this.isBefore = isBefore;

    }

    public MethodExpression getWrapped() {
    	return methodExpression;
    }

    public PhaseId getPhaseId() {
    	return phaseId;
    }
    
    public Object saveState(FacesContext context) {
    	Object result = null;
		if (!tranzient) {
	    	result = new Object[] { methodExpression, phaseId.getOrdinal(), isBefore} ;
		}
		return result;
    }

    public void restoreState(FacesContext context, Object state) {
		// if we have state
		if (null == state) {
		    return;
		}
		
		Object[] localState = (Object[])state;
		methodExpression = (MethodExpression) localState[0];
		phaseId = (PhaseId)PhaseId.VALUES.get( (Integer)localState[1] );
		isBefore = (Boolean) localState[2];
    }

    public boolean isTransient() {
    	return tranzient;
    }

    public void setTransient(boolean newTransientValue) {
    	tranzient = newTransientValue;
    }

	@Override
	public void afterPhase(PhaseEvent event) {
		if( !isBefore ) {
			try {
				methodExpression.invoke( 
					FacesContext.getCurrentInstance().getELContext() , 
					new Object[] { event } 
				);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		if( isBefore ) {
			try {
				methodExpression.invoke( 
					FacesContext.getCurrentInstance().getELContext() , 
					new Object[] { event } 
				);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
