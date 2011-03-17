package netgest.bo.xwc.framework.jsf;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
import netgest.bo.system.LoggerLevels.LoggerLevel;
import netgest.bo.xwc.framework.components.XUIViewRoot;

import com.sun.faces.lifecycle.Phase;

public class XUIInitComponentsPhase extends Phase {

    // Log instance for this class
    private static Logger LOGGER = Logger.getLogger( XUIInitComponentsPhase.class );


    // ---------------------------------------------------------- Public Methods


    public void execute(FacesContext facesContext) throws FacesException {

        if ( LOGGER.isFinerEnabled() ) {
            LOGGER.finer(LoggerMessageLocalizer.getMessage("ENTERING_PROCESSINITCOMPONENTSPHASE"));
        }
        UIComponent component = facesContext.getViewRoot();
        assert (null != component);

        try {
            if( component instanceof XUIViewRoot ) {
                ((XUIViewRoot)component).processInitComponents();    
            }
            
        } catch (RuntimeException re) {
            String exceptionMessage = re.getMessage();
            if (null != exceptionMessage) {
                if ( LOGGER.isLoggable( LoggerLevels.WARNING ) ) {
                    LOGGER.warn( exceptionMessage, re);
                }
            }
            throw new FacesException(exceptionMessage, re);
        }
        if (LOGGER.isFinerEnabled()) {
            LOGGER.finer(LoggerMessageLocalizer.getMessage("EXITING_PROCESSINITCOMPONENTSPHASE"));
        }

    }


    public PhaseId getId() {

        return PhaseId.ANY_PHASE;

    }


}
