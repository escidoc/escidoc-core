package de.escidoc.core.common.util.jaxb;

import java.io.IOException;

import org.escidoc.core.domain.exception.CauseTO;
import org.escidoc.core.domain.exception.ClassTO;
import org.escidoc.core.domain.exception.ExceptionTO;
import org.escidoc.core.domain.exception.MessageTO;
import org.escidoc.core.domain.exception.StackTraceTO;
import org.escidoc.core.domain.exception.TitleTO;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * JAXB Converter to support Exceptions.
 *
 * @author <a href="mailto:michael.hoppe@fiz-karlsruhe.de">Michael Hoppe</a>
 */
public final class ExceptionTOFactory {

    /**
     * private default Constructor to prevent initialization.
     */
    private ExceptionTOFactory() {
    }

    /**
     * Make Exception xml out of ExceptionTO.
     *
     * @param exceptionTo JAX-B Exception Object 
     * @return exceptionXml exception-xml according to exception.xsd.
     * @throws SystemException e
     */
    public static ExceptionTO generateExceptionTO(final Throwable e) throws IOException, SystemException {
        ExceptionTO exceptionTo = new ExceptionTO();

        if (e.getCause() != null) {
            CauseTO causeTo = new CauseTO();
            causeTo.setException(generateExceptionTO(e.getCause()));
            exceptionTo.setCause(causeTo);
        }

        ClassTO classTo = new ClassTO();
        classTo.setP(e.getClass().getName());
        exceptionTo.setClazz(classTo);

        MessageTO messageTo = new MessageTO();
        messageTo.setP(e.getMessage());
        exceptionTo.setMessage(messageTo);

        StackTraceTO stackTraceTo = new StackTraceTO();
        stackTraceTo.setP(getStackTraceString(e));
        exceptionTo.setStackTrace(stackTraceTo);

        TitleTO titleTo = new TitleTO();
        if (e instanceof EscidocException) {
            titleTo.setH1(String.valueOf(((EscidocException) e).getHttpStatusCode()) + " "
                + ((EscidocException) e).getHttpStatusMsg());
        }
        else {
            titleTo.setH1(e.getClass().getName());
        }
        exceptionTo.setTitle(titleTo);

        return exceptionTo;
    }

    /**
     * Gets the stack trace of the provided Exception as String.
     *
     * @param e The exception to get the stack trace from.
     * @return Returns the stack trace in the XML structure.
     */
    private static String getStackTraceString(final Throwable e) {

        final StringBuilder result = new StringBuilder("");
        final StackTraceElement[] elements = e.getStackTrace();
        for (final StackTraceElement element : elements) {
            result.append("    ");
            result.append(element);
            result.append('\n');
        }
        return result.toString();
    }

}
