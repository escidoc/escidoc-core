package de.escidoc.core.common.exceptions.application.notfound;

public class OperationNotFoundException extends ResourceNotFoundException {

    /**
     * 
     */
    private static final long serialVersionUID = 7360676821283154190L;

    public static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_NOT_FOUND;

    public static final String HTTP_STATUS_MESSAGE = "Operation was not found";

    /**
     * 
     */
    public OperationNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param httpStatusCode
     * @param httpStatusMsg
     */
    public OperationNotFoundException(final int httpStatusCode, final String httpStatusMsg) {
        super(httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param httpStatusCode
     * @param httpStatusMsg
     */
    public OperationNotFoundException(final String message, final int httpStatusCode,
        final String httpStatusMsg) {
        super(message, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param httpStatusCode
     * @param httpStatusMsg
     */
    public OperationNotFoundException(final String message, final Throwable cause,
        final int httpStatusCode, final String httpStatusMsg) {
        super(message, cause, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public OperationNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public OperationNotFoundException(final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     * @param httpStatusCode
     * @param httpStatusMsg
     */
    public OperationNotFoundException(final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) {
        super(cause, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public OperationNotFoundException(final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
