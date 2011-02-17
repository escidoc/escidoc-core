package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.util.logger.AppLogger;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * Velocity Logger with no output.
 * 
 * @author SWA
 * 
 */
class VelocityOutputLogger implements LogChute {

    private static final AppLogger LOG =
        new AppLogger(VelocityOutputLogger.class.getName());

    /**
     * Init Velocity logger.
     * 
     * @param arg0
     *            RuntimeServices
     * @throws Exception
     *             Shouldn't happen.
     */
    public void init(final RuntimeServices arg0) throws Exception {
    }

    /**
     * Check if log level is enabled.
     * 
     * @param arg0
     *            log level which is to check
     * @return true if log level is enabled, false otherwise
     */
    public boolean isLevelEnabled(final int arg0) {
        return true;
    }

    /**
     * Log.
     * 
     * @param arg0
     *            log level
     * @param arg1
     *            log message
     * @param arg2
     *            Exception
     */
    public void log(final int arg0, final String arg1, final Throwable arg2) {

        LOG.debug(arg1);
    }

    /**
     * Log.
     * 
     * @param arg0
     *            log level
     * @param arg1
     *            log message
     */
    public void log(final int arg0, final String arg1) {
        LOG.debug(arg1);
    }
}