package de.escidoc.core.index;

/**
 * Exception thrown by {@link IndexService} and its components.
 */
public class IndexServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public IndexServiceException() {
    }

    /**
     * {@inheritDoc}
     */
    public IndexServiceException(final String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public IndexServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public IndexServiceException(final Throwable cause) {
        super(cause);
    }
}
