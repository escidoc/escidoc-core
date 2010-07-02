package de.escidoc.core.common.exceptions.application.invalid;

/**
 * The InvalidResourceException indicates that the given resource could not be
 * ingested because the ingest interface could not detect its type. Hence it is
 * not a valid resource. Due to the similarity to the
 * XmlSchemaValidationException the same httpStatusCode(412) is returned.
 * 
 * @author KST
 * @common
 */
public class InvalidResourceException extends ValidationException {
    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -7101624412851271099L;

    public static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_INVALID;

    public static final String HTTP_STATUS_MESSAGE = "The resource is invalid.";

    /**
     * Default constructor.
     * 
     * @common
     */
    public InvalidResourceException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            - the detail message.
     * @common
     */
    public InvalidResourceException(final String message) {
        super(message);
    }

    /**
     * Constructor used to map an initial exception.
     * 
     * @param error
     *            Throwable
     */
    public InvalidResourceException(final Throwable error) {
        super(error);
    }

    /**
     * Constructor used to create a new Exception with the specified detail
     * message and a mapping to an initial exception.
     * 
     * @param message
     *            - the detail message.
     * @param error
     *            Throwable
     * @common
     */
    public InvalidResourceException(final String message, final Throwable error) {
        super(message);
    }

}
