package de.escicore.cache;

/**
 * Exception thrown by {@link CacheService} and its components.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class CacheServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public CacheServiceException() {
    }

    /**
     * {@inheritDoc}
     */
    public CacheServiceException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public CacheServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public CacheServiceException(Throwable cause) {
        super(cause);
    }
}
