package netgest.bo.xwc.framework.jsf;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.FacesException;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;

import com.sun.faces.util.MessageUtils;

public class XUILifeCycleFactoryImpl extends LifecycleFactory {



    // Log instance for this class
    private static Logger LOGGER = netgest.bo.system.Logger.getLogger( XUILifeCycleFactoryImpl.class );

    protected ConcurrentHashMap<String,Lifecycle> lifecycleMap = null;


    // ------------------------------------------------------------ Constructors


    public XUILifeCycleFactoryImpl() {
        super();
        lifecycleMap = new ConcurrentHashMap<String,Lifecycle>();

        // We must have an implementation under this key.
        lifecycleMap.put(LifecycleFactory.DEFAULT_LIFECYCLE,
                         new XUILifecycleImpl());
        if (LOGGER.isFinerEnabled()) {
            LOGGER.finer(LoggerMessageLocalizer.getMessage("CREATED_DEFAULT_LIFECYCLE"));
        }
    }


    // -------------------------------------------------- Methods from Lifecycle


    public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
        if (lifecycleId == null) {
            throw new NullPointerException(
                MessageUtils.getExceptionMessageString(MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "lifecycleId"));
        }
        if (lifecycle == null) {
            throw new NullPointerException(
                MessageUtils.getExceptionMessageString(MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "lifecycle"));
        }
        if (null != lifecycleMap.get(lifecycleId)) {
            Object params[] = {lifecycleId};
            String message =
                MessageUtils.getExceptionMessageString(MessageUtils.LIFECYCLE_ID_ALREADY_ADDED_ID,
                                         params);
            if (LOGGER.isLoggable( LoggerLevels.WARNING )) {
                LOGGER.warn(MessageUtils.getExceptionMessageString(
                        MessageUtils.LIFECYCLE_ID_ALREADY_ADDED_ID,params));
            }
            throw new IllegalArgumentException(message);
        }
        lifecycleMap.put(lifecycleId, lifecycle);
        if (LOGGER.isFinerEnabled()) {
            LOGGER.finer("addedLifecycle: " + lifecycleId + " " + lifecycle);
        }
    }


    public Lifecycle getLifecycle(String lifecycleId) throws FacesException {

        if (null == lifecycleId) {
            throw new NullPointerException(
                MessageUtils.getExceptionMessageString(MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "lifecycleId"));
        }

        if (null == lifecycleMap.get(lifecycleId)) {
            Object[] params = {lifecycleId};
            String message =
                MessageUtils.getExceptionMessageString(
                    MessageUtils.CANT_CREATE_LIFECYCLE_ERROR_MESSAGE_ID,
                    params);
            if (LOGGER.isLoggable( LoggerLevels.WARNING )) {
                LOGGER.warn("LifecycleId " + lifecycleId + " "+LoggerMessageLocalizer.getMessage("DOES_NOT_EXIST"));
            }
            throw new IllegalArgumentException(message);
        }

        Lifecycle result = lifecycleMap.get(lifecycleId);

        if (LOGGER.isFinerEnabled()) {
            LOGGER.finer("getLifecycle: " + lifecycleId + " " + result);
        }
        return result;
    }


    public Iterator<String> getLifecycleIds() {
        return lifecycleMap.keySet().iterator();
    }



    // The testcase for this class is TestLifecycleFactoryImpl.java



}
