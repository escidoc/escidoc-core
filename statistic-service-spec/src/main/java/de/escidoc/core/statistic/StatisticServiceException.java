package de.escidoc.core.statistic;

/**
 * Exception thrown by {@link StatisticService} and its components.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class StatisticServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public StatisticServiceException() {
    }

    /**
     * {@inheritDoc}
     */
    public StatisticServiceException(final String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public StatisticServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public StatisticServiceException(final Throwable cause) {
        super(cause);
    }
}
