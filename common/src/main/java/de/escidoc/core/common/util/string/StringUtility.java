/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.string;

import java.util.regex.Pattern;

/**
 * Helper class for strings.
 * 
 * @author TTE
 * 
 */
public final class StringUtility {

    /**
     * Pattern used to detect '-'.
     */
    static final Pattern PATTERN_MINUS = Pattern.compile("-");

    /**
     * Private constructor to prevent initialization.
     * 
     * @common
     */
    private StringUtility() {
    }

    /**
     * Concatenates the first value with a colon and then with the second value.
     * 
     * @param firstPart
     *            The first part of the key.
     * @param secondPart
     *            The second part of the key.
     * @return Returns the created StringBuffer object with content
     *         "firstPart:secondPart".
     * @common
     */
    public static StringBuffer concatenateWithColon(
        final String firstPart, final Object secondPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(":");
        ret.append(secondPart);
        return ret;
    }

    /**
     * Concatenates the provided strings with putting the second string between
     * brackets.
     * 
     * @param firstPart
     *            The first value.
     * @param secondPart
     *            The second value.
     * @return Returns the created StringBuffer object with content "firstPart
     *         [secondPart]".
     * @common
     */
    public static StringBuffer concatenateWithBrackets(
        final String firstPart, final int secondPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(" [");
        ret.append(secondPart);
        ret.append("]");
        return ret;
    }

    /**
     * Concatenates the provided strings with putting the second string between
     * brackets.
     * 
     * @param firstPart
     *            The first value.
     * @param secondPart
     *            The second value.
     * @return Returns the created StringBuffer object with content "firstPart
     *         [secondPart]".
     * @common
     */
    public static StringBuffer concatenateWithBrackets(
        final String firstPart, final Object secondPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(" [");
        ret.append(secondPart);
        ret.append("]");
        return ret;
    }

    /**
     * Concatenates the provided strings with putting the second and third
     * string between brackets, and a comma between second and third value.
     * 
     * @param firstPart
     *            The first value.
     * @param secondPart
     *            The second value.
     * @param thirdPart
     *            The third value.
     * @return Returns the created StringBuffer object with content "firstPart
     *         [secondPart, thirdPart]".
     * @common
     */
    public static StringBuffer concatenateWithBrackets(
        final String firstPart, final Object secondPart, final Object thirdPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(" [");
        ret.append(secondPart);
        ret.append(", ");
        ret.append(thirdPart);
        ret.append("]");
        return ret;
    }

    /**
     * Concatenates the provided strings with putting the second to fourth
     * string between brackets, and a comma between second, third, and fourth
     * value.
     * 
     * @param firstPart
     *            The first value.
     * @param secondPart
     *            The second value.
     * @param thirdPart
     *            The third value.
     * @param fourthPart
     *            The fourth value.
     * @return Returns the created StringBuffer object with content "firstPart
     *         [secondPart, thirdPart, fourthPart]".
     * @common
     */
    public static StringBuffer concatenateWithBrackets(
        final String firstPart, final Object secondPart,
        final Object thirdPart, final Object fourthPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(" [");
        ret.append(secondPart);
        ret.append(", ");
        ret.append(thirdPart);
        ret.append(", ");
        ret.append(fourthPart);
        ret.append("]");
        return ret;
    }

    /**
     * 
     * @param firstPart firstPart
     * @param secondPart secondPart
     * @param thirdPart thirdPart
     * @param fourthPart fourthPart
     * @param fifthPart fifthPart
     * @param sixthPart sixthPart
     * @param seventhPart seventhPart
     * @return StringBuffer
     */
    public static StringBuffer concatenateWithBrackets(
        final String firstPart, final Object secondPart,
        final Object thirdPart, final Object fourthPart,
        final Object fifthPart, final Object sixthPart, final Object seventhPart) {

        StringBuffer ret = new StringBuffer(firstPart);
        ret.append(" [");
        ret.append(secondPart);
        ret.append(", ");
        ret.append(thirdPart);
        ret.append(", ");
        ret.append(fourthPart);
        ret.append(", ");
        ret.append(fifthPart);
        ret.append(", ");
        ret.append(sixthPart);
        ret.append(", ");
        ret.append(seventhPart);
        ret.append("]");
        return ret;
    }

    /**
     * Concatenates the provided values.
     * 
     * @param parts
     *            The values to concatenate.
     * @return Returns the created StringBuffer object with content
     *         "firstPartsecondPartthirdPartfourthPart".
     */
    @Deprecated
    public static StringBuffer concatenate(final Object... parts) {

        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < parts.length; i++) {
            ret.append(parts[i]);
        }
        return ret;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * @see StringUtility#concatenateWithBrackets(String, int)
     * @common
     */
    public static String concatenateWithBracketsToString(
        final String firstPart, final int secondPart) {

        return concatenateWithBrackets(firstPart, secondPart).toString();
    }

    /**
     * @see StringUtility#concatenateWithBrackets(String, Object)
     * 
     * @param firstPart
     *            The first value.
     * @param secondPart
     *            The second value.
     * @common
     */
    public static String concatenateWithBracketsToString(
        final String firstPart, final Object secondPart) {

        return concatenateWithBrackets(firstPart, secondPart).toString();
    }

    /**
     * @see StringUtility#concatenateWithColon(String, Object)
     * @common
     */
    public static String concatenateWithColonToString(
        final String firstPart, final Object secondPart) {

        return concatenateWithColon(firstPart, secondPart).toString();
    }

    /**
     * @see StringUtility#concatenateWithBrackets(String, Object, Object)
     * @common
     */
    public static String concatenateWithBracketsToString(
        final String firstPart, final Object secondPart, final Object thirdPart) {

        return concatenateWithBrackets(firstPart, secondPart, thirdPart)
            .toString();
    }

    /**
     * @see StringUtility#concatenateWithBrackets(String, Object, Object,
     *      Object)
     * @common
     */
    public static String concatenateWithBracketsToString(
        final String firstPart, final Object secondPart,
        final Object thirdPart, final Object fourthPart) {

        return concatenateWithBrackets(firstPart, secondPart, thirdPart,
            fourthPart).toString();
    }

    /**
     * 
     * @param firstPart firstPart
     * @param secondPart secondPart
     * @param thirdPart thirdPart
     * @param fourthPart fourthPart
     * @param fifthPart fifthPart
     * @param sixthPart sixthPart
     * @param seventhPart seventhPart
     * @return Object
     */
    public static Object concatenateWithBracketsToString(
        final String firstPart, final String secondPart, final String thirdPart,
        final String fourthPart, final String fifthPart, final String sixthPart,
        final String seventhPart) {

        return concatenateWithBrackets(firstPart, secondPart, thirdPart,
            fourthPart, fifthPart, sixthPart, seventhPart).toString();
    }

    /**
     * Gets the String of the object type like it is used in class names.
     * 
     * @param objectType
     *            The object-type to be converted into ObjectType
     *            representation.
     * @return Returns the converted value.
     */
    public static StringBuffer convertToUpperCaseLetterFormat(
        final String objectType) {

        String[] splitted = PATTERN_MINUS.split(objectType);
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < splitted.length; i++) {
            String split = splitted[i];
            ret.append(split.substring(0, 1).toUpperCase());
            ret.append(split.substring(1));
        }

        return ret;
    }
}
