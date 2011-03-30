package de.escidoc.core.statistic;

/**
 * Exception thrown by {@link StatisticService} and its components.
 */
public class StatisticServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public StatisticServiceException() {
    }

    public StatisticServiceException(final String message) {
        super(message);
    }

    public StatisticServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StatisticServiceException(final Throwable cause) {
        super(cause);
    }
}
