package netgest.bo.xwc.framework.components;


import java.util.Iterator;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource2;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;



import java.util.Iterator;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import netgest.bo.xwc.framework.XUIMethodBindProperty;


/**
 * <p><strong>XUIForm</strong> is a {@link UIComponent} that represents an
 * input form to be presented to the user, and whose child components represent
 * (among other things) the input fields to be included when the form is
 * submitted.</p>
 *
 * <p>By default, the <code>rendererType</code> property must be set to
 * "<code>javax.faces.Form</code>".  This value can be changed by calling the
 * <code>setRendererType()</code> method.</p>
 */

public class XUIForm extends XUICommand implements NamingContainer {


    // ------------------------------------------------------ Manifest Constants


    // ------------------------------------------------------------ Constructors


    // ------------------------------------------------------ Instance Variables


    // -------------------------------------------------------------- Properties


    /**
     * <p>The form submitted flag for this {@link XUIForm}.</p>
     */
    private boolean submitted = false;
    private boolean submittedAction = false;

    protected boolean isSubmittedAction() {
		return submittedAction;
	}

    protected void setSubmittedAction(boolean submittedAction) {
		this.submittedAction = submittedAction;
	}

	/**
     * <p>Returns the current value of the <code>submitted</code>
     * property.  The default value is <code>false</code>.  See {@link
     * #setSubmitted} for details.</p>
     *
     */
    public boolean isSubmitted() {

        return (this.submitted);

    }

    public MethodExpression getDefaultCommand() {
		return super.getActionExpression();
	}

	public void setDefaultCommand(String defaultCommand) {
		
		super.setActionExpression( createMethodBinding( defaultCommand ) );
		
	}


    /**
     * <p>If <strong>this</strong> <code>XUIForm</code> instance (as
     * opposed to other forms in the page) is experiencing a submit
     * during this request processing lifecycle, this method must be
     * called, with <code>true</code> as the argument, during the {@link
     * UIComponent#decode} for this <code>XUIForm</code> instance.  If
     * <strong>this</strong> <code>XUIForm</code> instance is
     * <strong>not</strong> experiencing a submit, this method must be
     * called, with <code>false</code> as the argument, during the
     * {@link UIComponent#decode} for this <code>XUIForm</code>
     * instance.</p>
     *
     * <p>The value of a <code>XUIForm</code>'s submitted property must
     * not be saved as part of its state.</p>
     */
    public void setSubmitted(boolean submitted) {

        this.submitted = submitted;

    }
    
    /**
     * <p>The prependId flag.</p>
     */
    private Boolean prependId;


    public boolean isPrependId() {

	if (this.prependId != null) {
	    return (this.prependId);
	}
	ValueExpression ve = getValueExpression("prependId");
	if (ve != null) {
	    try {
		return (Boolean.TRUE.equals(ve.getValue(getFacesContext().getELContext())));
	    }
	    catch (ELException e) {
		throw new FacesException(e);
	    }
	} else {
	    return (true);
	}

    }


    public void setPrependId(boolean prependId) {

        this.prependId = prependId;

    }

    // ----------------------------------------------------- UIComponent Methods


    /**
     * <p>Override {@link UIComponent#processDecodes} to ensure that the
     * form is decoded <strong>before</strong> its children.  This is
     * necessary to allow the <code>submitted</code> property to be
     * correctly set.</p>
     *
     * @throws NullPointerException {@inheritDoc}
     */
    public void processDecodes(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
	
        // Process this component itself
        decode();

		// if we're not the submitted form, don't process children.
		if (!isSubmitted()) {
		    return;
		}

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processDecodes(context);
        }
	
        if( !isSubmittedAction() ) {
        	super.queueEvent( new ActionEvent( this ) );
        }
        
    }

    /**
     * <p>Override {@link UIComponent#processValidators} to ensure that
     * the children of this <code>XUIForm</code> instance are only
     * processed if {@link #isSubmitted} returns <code>true</code>.</p>
     * 
     * @throws NullPointerException {@inheritDoc}
     */
    public void processValidators(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
	if (!isSubmitted()) {
	    return;
	}

        // Process all the facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processValidators(context);
        }

    }


    /**
     * <p>Override {@link UIComponent#processUpdates} to ensure that the
     * children of this <code>XUIForm</code> instance are only processed
     * if {@link #isSubmitted} returns <code>true</code>.</p>
     * 
     * @throws NullPointerException {@inheritDoc}
     */
    public void processUpdates(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
	if (!isSubmitted()) {
	    return;
	}

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processUpdates(context);
        }

    }

    
    /**
     * <p>Override the {@link UIComponent#getContainerClientId} to allow
     * users to disable this form from prepending its <code>clientId</code> to
     * its descendent's <code>clientIds</code> depending on the value of
     * this form's {@link #isPrependId} property.</p>
     */
    public String getContainerClientId(FacesContext context) {
        if (this.isPrependId()) {
            return super.getContainerClientId(context);
        } else {
            UIComponent parent = this.getParent();
            while (parent != null) {
                if (parent instanceof NamingContainer) {
                    return parent.getContainerClientId(context);
                }
                parent = parent.getParent();
            }
        }
        return null;
    }

    private Object[] values;

    @Override
    public Object saveState(FacesContext context) {

        if (values == null) {
             values = new Object[2];
        }
        values[0] = super.saveState(context);
        values[1] = prependId;

        return values;

    }

    @Override
    public void restoreState(FacesContext context, Object state) {

        values = (Object[]) state;
        super.restoreState(context, values[0]);
        prependId = (Boolean) values[1];
        
    }
}
