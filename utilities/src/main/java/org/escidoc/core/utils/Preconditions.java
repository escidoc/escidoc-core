package org.esidoc.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Simple static methods to be called at the start of your own methods to verify correct arguments and state. This
 * allows constructs such as
 * <pre>
 *     if (count <= 0) {
 *       throw new IllegalArgumentException("must be positive: " + count);
 *     }</pre>
 *
 * to be replaced with the more compact
 * <pre>
 *     checkArgument(count > 0, "must be positive: %s", count);</pre>
 *
 * Note that the sense of the expression is inverted; with {@code Preconditions} you declare what you expect to be
 * <i>true</i>, just as you do with an <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/assert.html"> {@code
 * assert}</a> or a JUnit {@code assertTrue()} call.
 *
 * <p>Take care not to confuse precondition checking with other similar types of checks! Precondition exceptions --
 * including those provided here, but also {@link IndexOutOfBoundsException}, {@link UnsupportedOperationException} and
 * others -- are used to signal that the <i>calling method</i> has made an error. This tells the caller that it should
 * not have invoked the method when it did, with the arguments it did, or perhaps <i>ever</i>. Postcondition or other
 * invariant failures should not throw these types of exceptions.
 *
 * @author Kevin Bourrillion
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@SuppressWarnings({"JavaDoc"})
public final class Preconditions {

    private static final Logger LOG = LoggerFactory.getLogger(Preconditions.class);

    private Preconditions() {
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
     *                     String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(final boolean expression, final Object errorMessage) {
        if(! expression) {
            final String errorMessageString = String.valueOf(errorMessage);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IllegalArgumentException(errorMessageString);
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The message is formed by
     *                             replacing each {@code %s} placeholder in the template with an argument. These are
     *                             matched by position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *                             Unmatched arguments will be appended to the formatted message in square braces.
     *                             Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments are converted to
     *                             strings using {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     * @throws NullPointerException     if the check fails and either {@code errorMessageTemplate} or {@code
     *                                  errorMessageArgs} is null (don't let this happen)
     */
    public static void checkArgument(final boolean expression, final String errorMessageTemplate,
                                     final Object... errorMessageArgs) {
        if(! expression) {
            final String errorMessageString = format(errorMessageTemplate, errorMessageArgs);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IllegalArgumentException(errorMessageString);
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not involving any parameters
     * to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
     *                     String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(final boolean expression, final Object errorMessage) {
        if(! expression) {
            final String errorMessageString = String.valueOf(errorMessage);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IllegalStateException(errorMessageString);
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not involving any parameters
     * to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The message is formed by
     *                             replacing each {@code %s} placeholder in the template with an argument. These are
     *                             matched by position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *                             Unmatched arguments will be appended to the formatted message in square braces.
     *                             Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments are converted to
     *                             strings using {@link String#valueOf(Object)}.
     * @throws IllegalStateException if {@code expression} is false
     * @throws NullPointerException  if the check fails and either {@code errorMessageTemplate} or {@code
     *                               errorMessageArgs} is null (don't let this happen)
     */
    public static void checkState(final boolean expression, final String errorMessageTemplate,
                                  final Object... errorMessageArgs) {
        if(! expression) {
            final String errorMessageString = format(errorMessageTemplate, errorMessageArgs);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IllegalStateException(errorMessageString);
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
     *                     String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(final T reference, final Object errorMessage) {
        if(reference == null) {
            final String errorMessageString = String.valueOf(errorMessage);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new NullPointerException(errorMessageString);
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference            an object reference
     * @param errorMessageTemplate a template for the exception message should the check fail. The message is formed by
     *                             replacing each {@code %s} placeholder in the template with an argument. These are
     *                             matched by position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *                             Unmatched arguments will be appended to the formatted message in square braces.
     *                             Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments are converted to
     *                             strings using {@link String#valueOf(Object)}.
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(final T reference, final String errorMessageTemplate,
                                     final Object... errorMessageArgs) {
        if(reference == null) {
            final String errorMessageString = format(errorMessageTemplate, errorMessageArgs);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new NullPointerException(errorMessageString);
        }
        return reference;
    }

    /**
     * Ensures that an {@code Iterable} object passed as a parameter to the calling method is not null and contains no
     * null elements.
     *
     * @param iterable     the iterable to check the contents of
     * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
     *                     String#valueOf(Object)}
     * @return the non-null {@code iterable} reference just validated
     * @throws NullPointerException if {@code iterable} is null or contains at least one null element
     */
    public static <T extends Iterable<?>> T checkContentsNotNull(final T iterable, final Object errorMessage) {
        if(containsOrIsNull(iterable)) {
            final String errorMessageString = String.valueOf(errorMessage);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new NullPointerException(errorMessageString);
        }
        return iterable;
    }

    /**
     * Ensures that an {@code Iterable} object passed as a parameter to the calling method is not null and contains no
     * null elements.
     *
     * @param iterable             the iterable to check the contents of
     * @param errorMessageTemplate a template for the exception message should the check fail. The message is formed by
     *                             replacing each {@code %s} placeholder in the template with an argument. These are
     *                             matched by position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *                             Unmatched arguments will be appended to the formatted message in square braces.
     *                             Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments are converted to
     *                             strings using {@link String#valueOf(Object)}.
     * @return the non-null {@code iterable} reference just validated
     * @throws NullPointerException if {@code iterable} is null or contains at least one null element
     */
    public static <T extends Iterable<?>> T checkContentsNotNull(final T iterable, final String errorMessageTemplate,
                                                                 final Object... errorMessageArgs) {
        if(containsOrIsNull(iterable)) {
            final String errorMessageString = format(errorMessageTemplate, errorMessageArgs);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new NullPointerException(errorMessageString);
        }
        return iterable;
    }

    private static boolean containsOrIsNull(final Iterable<?> iterable) {
        if(iterable == null) {
            return true;
        }
        if(iterable instanceof Collection) {
            final Collection<?> collection = (Collection<?>) iterable;
            try {
                return collection.contains(null);
            } catch(NullPointerException e) {
                // A NPE implies that the collection doesn't contain null.
                return false;
            }
        } else {
            for(final Object element : iterable) {
                if(element == null) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size {@code size}. An
     * element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size  the size of that array, list or string
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkElementIndex(final int index, final int size) {
        checkElementIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size {@code size}. An
     * element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size  the size of that array, list or string
     * @param desc  the text to use to describe this index in an error message
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkElementIndex(final int index, final int size, final String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if(index < 0) {
            final String errorMessageString = format("%s (%s) must not be negative", desc, index);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IndexOutOfBoundsException(errorMessageString);
        }
        if(index >= size) {
            final String errorMessageString = format("%s (%s) must be less than size (%s)", desc, index, size);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IndexOutOfBoundsException(errorMessageString);
        }
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of size {@code size}. A
     * position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size  the size of that array, list or string
     * @throws IndexOutOfBoundsException if {@code index} is negative or is greater than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkPositionIndex(final int index, final int size) {
        checkPositionIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of size {@code size}. A
     * position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size  the size of that array, list or string
     * @param desc  the text to use to describe this index in an error message
     * @throws IndexOutOfBoundsException if {@code index} is negative or is greater than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkPositionIndex(final int index, final int size, final String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if(index < 0) {
            final String errorMessageString = format("%s (%s) must not be negative", desc, index);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IndexOutOfBoundsException(errorMessageString);
        }
        if(index > size) {
            final String errorMessageString = format("%s (%s) must not be greater than size (%s)", desc, index, size);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IndexOutOfBoundsException(errorMessageString);
        }
    }

    /**
     * Ensures that {@code start} and {@code end} specify a valid <i>positions</i> in an array, list or string of size
     * {@code size}, and are in order. A position index may range from zero to {@code size}, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end   a user-supplied index identifying a ending position in an array, list or string
     * @param size  the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is greater than {@code size}, or if {@code end}
     *                                   is less than {@code start}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkPositionIndexes(final int start, final int end, final int size) {
        checkPositionIndex(start, size, "start index");
        checkPositionIndex(end, size, "end index");
        if(end < start) {
            final String errorMessageString =
                    format("end index (%s) must not be less than start index (%s)", end, start);
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessageString);
            }
            throw new IndexOutOfBoundsException(errorMessageString);
        }
    }

    /**
     * Substitutes each {@code %s} in {@code template} with an argument. These are matched by position - the first
     * {@code %s} gets {@code args[0]}, etc. If there are more arguments than placeholders, the unmatched arguments will
     * be appended to the end of the formatted message in square braces.
     *
     * @param template a non-null string containing 0 or more {@code %s} placeholders.
     * @param args     the arguments to be substituted into the message template. Arguments are converted to strings
     *                 using {@link String#valueOf(Object)}. Arguments can be null.
     */
    static String format(final String template, final Object... args) { // visible for testing
        // start substituting the arguments into the '%s' placeholders
        final StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while(i < args.length) {
            final int placeholderStart = template.indexOf("%s", templateStart);
            if(placeholderStart == - 1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));
        // if we run out of placeholders, append the extra args in square braces
        if(i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while(i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }
}
