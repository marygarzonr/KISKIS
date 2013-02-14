package de.tbuchloh.kiskis.testutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.persistence.IErrorHandler;
import de.tbuchloh.kiskis.util.KisKisRuntimeException;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 22.11.2010
 */
public final class TestErrorHandler implements IErrorHandler {
    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(TestErrorHandler.class);

    @Override
    public void warning(String message) {
        LOG.warn(message);
    }

    @Override
    public void error(String message) {
        LOG.error(message);
        throw new KisKisRuntimeException(message);
    }
}