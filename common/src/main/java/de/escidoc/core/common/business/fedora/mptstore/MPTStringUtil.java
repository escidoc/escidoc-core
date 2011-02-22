package de.escidoc.core.common.business.fedora.mptstore;


/**
 * Utility for parsing, validating, and printing strings in N-Triples format.
 * 
 * @author cwilper@cs.cornell.edu
 */
public abstract class MPTStringUtil {

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


    public static String escapeLiteralValueForSql(final String s) {

        int len = s.length();
        StringBuilder out = new StringBuilder(len * 2);

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            int cNum = c;
            if (c == '\'') {
                out.append("\\\'");
            }
            else if (c == '\\') {
                out.append("\\\\\\\\");
            }
            else if (c == '"') {
                out.append("\\\\\"");
            }
            else if (c == '\n') {
                out.append("\\\\n");
            }
            else if (c == '\r') {
                out.append("\\\\r");
            }
            else if (c == '\t') {
                out.append("\\\\t");
            }
            else if (isLowUnicode(cNum)) {
                out.append("\\\\u");
                out.append(hexString(cNum, SHORT_ESCAPE_LENGTH - 1));
            }
            else if (isHighUnicode(cNum)) {
                out.append("\\\\U");
                out.append(hexString(cNum, LONG_ESCAPE_LENGTH - 2));
            }
            else {
                out.append(c);
            }
        }

        return out.toString();
    }

    /**
     * Tell whether the given character is in the "low unicode" (two-byte)
     * range.
     * 
     * @param cNum
     *            the character.
     * @return true if it's a low unicode character.
     */
    private static boolean isLowUnicode(final int cNum) {
        return (cNum >= UC_LOW1 && cNum <= UC_HIGH1)
            || (cNum == UC_LOW2 || cNum == UC_HIGH2)
            || (cNum >= UC_LOW3 && cNum <= UC_HIGH3)
            || (cNum >= UC_LOW4 && cNum <= UC_HIGH4);
    }

    /**
     * Tell whether the given character is in the "high unicode" (four-byte)
     * range.
     * 
     * @param cNum
     *            the character.
     * @return true if it's a low unicode character.
     */
    private static boolean isHighUnicode(final int cNum) {
        return cNum >= UC_LOW5 && cNum <= UC_HIGH5;
    }

    /**
     * Get an uppercase hex string of the specified length, representing the
     * given number.
     * 
     * @param num
     *            The number to represent.
     * @param len
     *            The desired length of the output.
     * @return The uppercase hex string.
     */
    private static String hexString(final int num, final int len) {
        StringBuilder out = new StringBuilder(len);
        String hex = Integer.toHexString(num).toUpperCase();
        int n = len - hex.length();
        for (int i = 0; i < n; i++) {
            out.append('0');
        }
        out.append(hex);
        return out.toString();
    }

}
