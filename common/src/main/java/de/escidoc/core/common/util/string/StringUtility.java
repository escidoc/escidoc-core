/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.string;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Helper class for strings.
 *
 * @author Torsten Tetteroo
 */
public final class StringUtility {

    /**
     * Pattern used to detect '-'.
     */
    private static final Pattern PATTERN_MINUS = Pattern.compile("-");

    /**
     * Private constructor to prevent initialization.
     */
    private StringUtility() {
    }

    /**
     * Substitutes each {@code %s} in {@code template} with an argument. These are matched by position - the first
     * {@code %s} gets {@code args[0]}, etc. If there are more arguments than placeholders, the unmatched arguments will
     * be appended to the end of the formatted message in square braces.
     *
     * @param template a non-null string containing 0 or more {@code %s} placeholders.
     * @param args     the arguments to be substituted into the message template. Arguments are converted to strings
     *                 using {@link String#valueOf(Object)}. Arguments can be null.
     * @return
     */
    public static String format(final String template, final Object... args) { // visible for testing
        // start substituting the arguments into the '%s' placeholders
        final StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            final int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i]);
            i++;
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));
        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i]);
            i++;
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i]);
                i++;
            }
            builder.append(']');
        }
        return builder.toString();
    }

    /**
     * Concatenates the first value with a colon and then with the second value.
     *
     * @param firstPart  The first part of the key.
     * @param secondPart The second part of the key.
     * @return Returns the created StringBuffer object with content "firstPart:secondPart".
     */
    public static StringBuffer concatenateWithColon(final String firstPart, final Object secondPart) {
        final StringBuffer ret = new StringBuffer(firstPart);
        ret.append(':');
        ret.append(secondPart);
        return ret;
    }

    /**
     * @param firstPart
     * @param secondPart
     * @see StringUtility#concatenateWithColon(String, Object)
     * @return
     */
    public static String concatenateWithColonToString(final String firstPart, final Object secondPart) {
        return concatenateWithColon(firstPart, secondPart).toString();
    }

    /**
     * Gets the String of the object type like it is used in class names.
     *
     * @param objectType The object-type to be converted into ObjectType representation.
     * @return Returns the converted value.
     */
    public static StringBuffer convertToUpperCaseLetterFormat(final CharSequence objectType) {
        final StringBuffer ret = new StringBuffer();
        if (objectType != null) {
            final String[] splitted = PATTERN_MINUS.split(objectType);
            for (final String split : splitted) {
                ret.append(split.substring(0, 1).toUpperCase(Locale.ENGLISH));
                ret.append(split.substring(1));
            }
        }
        return ret;
    }
}
