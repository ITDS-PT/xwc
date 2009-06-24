package netgest.bo.xwc.framework.jsf;

import com.sun.faces.lifecycle.Phase;
import com.sun.faces.util.FacesLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XUIPreRenderPhase extends Phase {

    // Log instance for this class
    private static Logger LOGGER = FacesLogger.LIFECYCLE.getLogger();


    // ---------------------------------------------------------- Public Methods


    public void execute(FacesContext facesContext) throws FacesException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Entering ProcessInitComponentsPhase");
        }
        UIComponent component = facesContext.getViewRoot();
        assert (null != component);

        try {
            if( component instanceof XUIViewRoot ) {
                ((XUIViewRoot)component).processPreRender();    
            }
            
        } catch (RuntimeException re) {
            String exceptionMessage = re.getMessage();
            if (null != exceptionMessage) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, exceptionMessage, re);
                }
            }
            throw new FacesException(exceptionMessage, re);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Exiting ProcessInitComponentsPhase");
        }

    }


    public PhaseId getId() {

        return PhaseId.ANY_PHASE;

    }


}
