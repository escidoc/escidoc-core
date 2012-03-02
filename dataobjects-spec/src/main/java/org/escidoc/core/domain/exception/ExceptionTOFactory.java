package org.escidoc.core.domain.exception;

import java.io.IOException;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * JAXB Converter to support Exceptions.
 *
 * @author <a href="mailto:michael.hoppe@fiz-karlsruhe.de">Michael Hoppe</a>
 */
public final class ExceptionTOFactory {

    private static final ObjectFactory factory = new ObjectFactory();

    /**
     * private default Constructor to prevent instantiation.
     */
    private ExceptionTOFactory() {
    }

    /**
     * Make ExceptionTO JAXB-Object out of Exception-Object.
     *
     * @param e Throwable
     * @return ExceptionTO JAX-B Exception Object according to exception.xsd.
     * @throws IOException e
     * @throws SystemException e
     */
    public static ExceptionTO generateExceptionTO(final Throwable e) throws IOException, SystemException {
        ExceptionTO exceptionTo = factory.createExceptionTO();

        if (e.getCause() != null) {
            CauseTO causeTo = factory.createCauseTO();
            causeTo.setException(generateExceptionTO(e.getCause()));
            exceptionTo.setCause(causeTo);
        }

        exceptionTo.setClazz(e.getClass().getName());
        exceptionTo.setMessage(e.getMessage());
        exceptionTo.setStackTrace(getStackTraceString(e));

        if (e instanceof EscidocException) {
            exceptionTo.setTitle(String.valueOf(((EscidocException) e).getHttpStatusCode()) + " "
                + ((EscidocException) e).getHttpStatusMsg());
        }
        else {
            exceptionTo.setTitle(e.getClass().getName());
        }

        return exceptionTo;
    }

    /**
     * Gets the stack trace of the provided Exception as String.
     *
     * @param e The exception to get the stack trace from.
     * @return Returns the stack trace in the XML structure.
     */
    private static String getStackTraceString(final Throwable e) {

        final StringBuilder result = new StringBuilder();
        for (final StackTraceElement element : e.getStackTrace()) {
            result.append("    ");
            result.append(element);
            result.append('\n');
        }
        return result.toString();
    }

}
