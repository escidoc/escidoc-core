package org.escidoc.core.domain.exception;

import java.io.IOException;
import java.lang.reflect.Constructor;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * JAXB Converter to support Exceptions.
 *
 * @author <a href="mailto:michael.hoppe@fiz-karlsruhe.de">Michael Hoppe</a>
 */
public final class ExceptionTOFactory {

    private static final ObjectFactory FACTORY = new ObjectFactory();

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
        ExceptionTO exceptionTo = FACTORY.createExceptionTO();

        if (e.getCause() != null) {
            CauseTO causeTo = FACTORY.createCauseTO();
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
     * TODO: StackTrace
     * @param exceptionTO the {@link ExceptionTO} to map to a {@link Throwable}
     * @return
     * @throws Exception
     */
    public static Throwable createThrowable(final ExceptionTO exceptionTO) throws Exception {
        Throwable result;
        Class<? extends Throwable> throwableClass = null;

        // load class
        try {
            Class<?> clazz = Class.forName(exceptionTO.getClazz());
            if (Throwable.class.isAssignableFrom(clazz)) {
                throwableClass = (Class<? extends Throwable>) clazz;
            }
        } catch (ClassNotFoundException ignored) {}
        // fallback
        if (throwableClass == null) {
            throwableClass = Exception.class;
        }

        Constructor<? extends Throwable> constructor;
        try {
            // we expect a constructor with parameters: String message & Throwable cause
            constructor = throwableClass.getDeclaredConstructor(String.class, Throwable.class);
            if (exceptionTO.getCause() != null && exceptionTO.getCause().getException() != null) {
                result = constructor.newInstance(exceptionTO.getMessage(),
                        createThrowable(exceptionTO.getCause().getException()));
            } else {
                result = constructor.newInstance(exceptionTO.getMessage(), null);
            }
        } catch (NoSuchMethodException e) {
            try {
                // fallback to constructor with parameter: String message
                constructor = throwableClass.getDeclaredConstructor(String.class);
                result = constructor.newInstance(exceptionTO.getMessage());
            } catch (NoSuchMethodException e1) {
                try {
                    // fallback to default constructor
                    result = throwableClass.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException e2) {
                    // we give up
                    throw new InstantiationException("Unable to instantiate class: " + throwableClass.getName());
                }
            }
        }

        return result;
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
