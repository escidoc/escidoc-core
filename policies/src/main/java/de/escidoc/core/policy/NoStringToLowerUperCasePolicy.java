package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoStringToLowerUperCasePolicy {

    private final static String DESCRIPTION = " This method is locale sensitive, and may produce unexpected " +
            "results if used for strings that are intended to be interpreted locale independently. " +
            "Examples are programming language identifiers, protocol keys, and HTML tags. " +
            "For instance, 'TITLE'.toLowerCase()' in a Turkish locale returns 't\\u0131tle', " +
            "where '\\u0131' is the LATIN SMALL LETTER DOTLESS I character. " +
            "To obtain correct results for locale insensitive strings, use toLowerCase(Locale.ENGLISH).";

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.toLowerCase())")
    private static final String NO_STRING_TO_LOWER_CASE =
            "Using java.lang.String.toLowerCase() is not allowed." + DESCRIPTION;

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.toUpperCase())")
    private static final String NO_STRING_TO_UPPER_CASE =
            "Using java.lang.String.toUpperCase() is not allowed." + DESCRIPTION;

    private NoStringToLowerUperCasePolicy() {
    }

}
