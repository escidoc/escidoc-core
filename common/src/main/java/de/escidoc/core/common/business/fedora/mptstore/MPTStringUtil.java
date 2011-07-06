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

package de.escidoc.core.common.business.fedora.mptstore;

import java.util.Locale;

/**
 * Utility for parsing, validating, and printing strings in N-Triples format.
 *
 * @author cwilper@cs.cornell.edu
 */
public final class MPTStringUtil {

    private static final int SHORT_ESCAPE_LENGTH = 5;

    private static final int LONG_ESCAPE_LENGTH = 10;

    private static final int UC_LOW1 = 0x0;

    private static final int UC_HIGH1 = 0x8;

    private static final int UC_LOW2 = 0xB;

    private static final int UC_HIGH2 = 0xC;

    private static final int UC_LOW3 = 0xE;

    private static final int UC_HIGH3 = 0x1F;

    private static final int UC_LOW4 = 0x7F;

    private static final int UC_HIGH4 = 0xFFFF;

    private static final int UC_LOW5 = 0x10000;

    private static final int UC_HIGH5 = 0x10FFFF;

    private MPTStringUtil() {
    }

    public static String escapeLiteralValueForSql(final CharSequence s) {

        final int len = s.length();
        final StringBuilder out = new StringBuilder(len * 2);

        for (int i = 0; i < len; i++) {
            final char c = s.charAt(i);
            if ((int) c == (int) '\'') {
                out.append("\\\'");
            }
            else if ((int) c == (int) '\\') {
                out.append("\\\\\\\\");
            }
            else if ((int) c == (int) '"') {
                out.append("\\\\\"");
            }
            else if ((int) c == (int) '\n') {
                out.append("\\\\n");
            }
            else if ((int) c == (int) '\r') {
                out.append("\\\\r");
            }
            else if ((int) c == (int) '\t') {
                out.append("\\\\t");
            }
            else if (isLowUnicode((int) c)) {
                out.append("\\\\u");
                out.append(hexString((int) c, SHORT_ESCAPE_LENGTH - 1));
            }
            else if (isHighUnicode((int) c)) {
                out.append("\\\\U");
                out.append(hexString((int) c, LONG_ESCAPE_LENGTH - 2));
            }
            else {
                out.append(c);
            }
        }

        return out.toString();
    }

    /**
     * Tell whether the given character is in the "low unicode" (two-byte) range.
     *
     * @param cNum the character.
     * @return true if it's a low unicode character.
     */
    private static boolean isLowUnicode(final int cNum) {
        return cNum >= UC_LOW1 && cNum <= UC_HIGH1 || cNum == UC_LOW2 || cNum == UC_HIGH2 || cNum >= UC_LOW3
            && cNum <= UC_HIGH3 || cNum >= UC_LOW4 && cNum <= UC_HIGH4;
    }

    /**
     * Tell whether the given character is in the "high unicode" (four-byte) range.
     *
     * @param cNum the character.
     * @return true if it's a low unicode character.
     */
    private static boolean isHighUnicode(final int cNum) {
        return cNum >= UC_LOW5 && cNum <= UC_HIGH5;
    }

    /**
     * Get an uppercase hex string of the specified length, representing the given number.
     *
     * @param num The number to represent.
     * @param len The desired length of the output.
     * @return The uppercase hex string.
     */
    private static String hexString(final int num, final int len) {
        final StringBuilder out = new StringBuilder(len);
        final String hex = Integer.toHexString(num).toUpperCase(Locale.ENGLISH);
        final int n = len - hex.length();
        for (int i = 0; i < n; i++) {
            out.append('0');
        }
        out.append(hex);
        return out.toString();
    }

}
