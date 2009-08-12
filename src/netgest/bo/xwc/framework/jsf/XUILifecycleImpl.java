package netgest.bo.xwc.framework.jsf;

import com.sun.faces.lifecycle.ApplyRequestValuesPhase;
import com.sun.faces.lifecycle.InvokeApplicationPhase;
import com.sun.faces.lifecycle.Phase;

import com.sun.faces.lifecycle.ProcessValidationsPhase;
import com.sun.faces.lifecycle.RenderResponsePhase;

import com.sun.faces.lifecycle.RestoreViewPhase;

import com.sun.faces.lifecycle.UpdateModelValuesPhase;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.MessageUtils;

import java.util.logging.Logger;

import javax.faces.lifecycle.Lifecycle;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XUILifecycleImpl extends Lifecycle {

    // -------------------------------------------------------- Static Variables


    // Log instance for this class
    private static Logger LOGGER = FacesLogger.LIFECYCLE.getLogger();


    // ------------------------------------------------------ Instance Variables

    // The Phase instance for the render() method
    private Phase response  = new RenderResponsePhase();
    private Phase preRender = new XUIPreRenderPhase();
    private Phase iniComponents = new XUIInitComponentsPhase();

    // The set of Phase instances that are executed by the execute() method
    // in order by the ordinal property of each phase
    private Phase[] phases = {
        null, // ANY_PHASE placeholder, not a real Phase
        new RestoreViewPhase(),
        iniComponents,  // Only if is a new view
        new ApplyRequestValuesPhase(),
        new ProcessValidationsPhase(),
        new UpdateModelValuesPhase(),
        new XUIValidateModelPhase(),
        new InvokeApplicationPhase(),
        preRender, // Pre render for current elements Tree
        iniComponents, // Only for new components add in the preRender Tree
        preRender, // Run pre render in the added components
        response // Render response
    };

    // List for registered PhaseListeners
    private List<PhaseListener> listeners =
          new CopyOnWriteArrayList<PhaseListener>();

    // ------------------------------------------------------- Lifecycle Methods


    // Execute the phases up to but not including Render Response
    public void execute(FacesContext context) throws FacesException {

        if (context == null) {
            throw new NullPointerException
                (MessageUtils.getExceptionMessageString
                 (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context"));
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("execute(" + context + ")");
        }
        
        int i = 1;
        int len = phases.length -1;
        
    	boolean runPhase;

    	for ( ; i < len; i++) { // Skip ANY_PHASE placeholder
            
            // Check if initComponents as been done
            // if not do phases to initcomponents phase
    		runPhase = true;
        	
            if ( context.getRenderResponse() || context.getResponseComplete()) 
            {
                break;
            }
            
            // Only run initcomponents at this phase if
            // the view is not a postback
            if( i == 2 && !XUIRequestContext.getCurrentContext().getViewRoot().isPostBack()  ) {
            	runPhase = false;
            }
        	
            if( runPhase ) {
            	phases[i].doPhase(context, this, listeners.listIterator());
            }

    	}
        
        // Check if required phases where performed
        if( !context.getResponseComplete() ) {
        	
        	XUIViewRoot viewToRender = XUIRequestContext.getCurrentContext().getViewRoot(); 
        	
        	runPhase = !viewToRender.isPostBack();
            // Check initComponents phase.
            if ( !viewToRender.wasInitComponentsProcessed() ) {
                iniComponents.execute( context );
            }
            
            // Check PreRender Phase
            if ( i < 8 ) {
                //
                preRender.execute( context );    
            }

            if ( i < 9 ) {
            	iniComponents.execute( context );
            }
            
            if ( i < 10 ) {
            	preRender.execute( context );
            }
            
        }

    }


    // Execute the Render Response phase
    public void render(FacesContext context) throws FacesException {

        if (context == null) {
            throw new NullPointerException
                (MessageUtils.getExceptionMessageString
                 (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context"));
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("render(" + context + ")");
        }

        if (!context.getResponseComplete()) {
            response.doPhase(context, this, listeners.listIterator());
        }

    }


    // Add a new PhaseListener to the set of registered listeners
    public void addPhaseListener(PhaseListener listener) {

        if (listener == null) {
            throw new NullPointerException
                  (MessageUtils.getExceptionMessageString
                        (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "listener"));
        }

        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<PhaseListener>();
        }

        if (listeners.contains(listener)) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           "jsf.lifecycle.duplicate_phase_listener_detected",
                           listener.getClass().getName());
            }
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           "addPhaseListener({0},{1})",
                           new Object[]{
                                 listener.getPhaseId().toString(),
                                 listener.getClass().getName()});
            }
            listeners.add(listener);
        }

    }


    // Return the set of PhaseListeners that have been registered
    public PhaseListener[] getPhaseListeners() {

        return listeners.toArray(new PhaseListener[listeners.size()]);

    }


    // Remove a registered PhaseListener from the set of registered listeners
    public void removePhaseListener(PhaseListener listener) {

        if (listener == null) {
            throw new NullPointerException
                  (MessageUtils.getExceptionMessageString
                        (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "listener"));
        }

        if (listeners.remove(listener) && LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE,
                       "removePhaseListener({0})",
                       new Object[]{listener.getClass().getName()});
        }

    }
        
}

