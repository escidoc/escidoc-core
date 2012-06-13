package org.escidoc.core.domain.exception;

import java.io.IOException;
import java.lang.reflect.Constructor;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;

import javax.xml.bind.JAXBElement;

/**
 * JAXB Converter to support Exceptions.
 *
 * @author <a href="mailto:michael.hoppe@fiz-karlsruhe.de">Michael Hoppe</a>
 */
public final class ExceptionTypeTOFactory {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * private default Constructor to prevent instantiation.
     */
    private ExceptionTypeTOFactory() {
    }


    /**
     * Make ExceptionTypeTO JAXB-Object out of Exception-Object.
     *
     * @param e Throwable
     * @return ExceptionTypeTO JAX-B Exception Object according to exception.xsd.
     * @throws IOException e
     * @throws SystemException e
     */
    public static JAXBElement<ExceptionTypeTO> generateExceptionTO(final Throwable e) throws IOException, SystemException {
        ExceptionTypeTO exceptionTypeTO = FACTORY.createExceptionTypeTO();

        if (e.getCause() != null) {
            CauseTypeTO causeTypeTO = FACTORY.createCauseTypeTO();
            causeTypeTO.setException(generateExceptionTO(e.getCause()).getValue());
            exceptionTypeTO.setCause(causeTypeTO);
        }

        exceptionTypeTO.setClazz(e.getClass().getName());
        exceptionTypeTO.setMessage(e.getMessage());
        exceptionTypeTO.setStackTrace(getStackTraceString(e));

        if (e instanceof EscidocException) {
            exceptionTypeTO.setTitle(String.valueOf(((EscidocException) e).getHttpStatusCode()) + " "
                                     + ((EscidocException) e).getHttpStatusMsg());
        }
        else {
            exceptionTypeTO.setTitle(e.getClass().getName());
        }

        return FACTORY.createException(exceptionTypeTO);
    }

    /**
     * TODO: StackTrace
     * @param ExceptionTypeTO the {@link ExceptionTypeTO} to map to a {@link Throwable}
     * @return
     * @throws Exception
     */
    public static Throwable createThrowable(final ExceptionTypeTO ExceptionTypeTO) throws Exception {
        Throwable result;
        Class<? extends Throwable> throwableClass = null;

        // load class
        try {
            Class<?> clazz = Class.forName(ExceptionTypeTO.getClazz());
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
            if (ExceptionTypeTO.getCause() != null && ExceptionTypeTO.getCause().getException() != null) {
                result = constructor.newInstance(ExceptionTypeTO.getMessage(),
                        createThrowable(ExceptionTypeTO.getCause().getException()));
            } else {
                result = constructor.newInstance(ExceptionTypeTO.getMessage(), null);
            }
        } catch (NoSuchMethodException e) {
            try {
                // fallback to constructor with parameter: String message
                constructor = throwableClass.getDeclaredConstructor(String.class);
                result = constructor.newInstance(ExceptionTypeTO.getMessage());
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
