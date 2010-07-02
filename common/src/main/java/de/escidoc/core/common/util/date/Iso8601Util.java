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
package de.escidoc.core.common.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.escidoc.core.common.util.string.StringUtility;

/**
 * Utility class to support date and time conversion to ISO86001 format.
 * 
 * @author TTE
 * @common
 */
@Deprecated
public final class Iso8601Util {

    /*
     * Use Joda time instead of this class.
     */
    private static final int SIX = 6;

    private static final int FOUR = 4;

    private static final String UTC_TIMEZONE_ID = "UTC";

    private static final String DATE_FORMAT_PATTERN =
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /** The date format for parsing date string. */
    private static DateFormat inputDateFormat;

    /** The date format to convert date objects to string. */
    private static DateFormat outputDateFormat;

    private static Calendar calendar;

    /**
     * Private Constructor to prevent instantiation.
     * 
     * @common
     */
    private Iso8601Util() {
    }

    /**
     * Creates a date format for the provided pattern.<br>
     * 
     * @param pattern
     *            The format pattern.
     * @return Returns an instance of <code>SimpleDateFormat</code> using the
     *         provided pattern and the gregorian calendar with UTC time zone.
     * @common
     */
    private static DateFormat createDateFormat(final String pattern) {

        if (calendar == null) {
            calendar =
                new GregorianCalendar(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
        }
        DateFormat df = new SimpleDateFormat(pattern);
        df.setCalendar(calendar);
        df.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
        return df;
    }

    /**
     * Gets the date and time in ISO8601 format with time zone formatted as Z
     * (UTC), +hh:mm, or -hh:mm.
     * 
     * @param date
     *            The date and time to convert to ISO8601 format.
     * @return Returns the value of date in ISO8601 format
     *         yyyy-MM-ddThh:mm:ss.sssZ, yyyy-MM-dd-Thh:mm:ss.sss+hh:mm, or
     *         yyyy-MM-dd-Thh:mm:ss.sss-hh:mm
     * @common
     * 
     */
    @Deprecated
    public static synchronized String getIso8601(final Date date) {

        if (outputDateFormat == null) {
            outputDateFormat = createDateFormat(DATE_FORMAT_PATTERN);
        }
        String preformatted = outputDateFormat.format(date);
        if (preformatted.endsWith("Z")) {
            return preformatted;
        }
        else if (preformatted.endsWith("+0000")) {
            final String ret =
                preformatted.substring(0, preformatted.length()
                    - "+0000".length())
                    + "Z";
            return ret;
        }
        else {
            final String ret =
                preformatted.substring(0, preformatted.length() - 2)
                    + ":"
                    + preformatted.substring(preformatted.length() - 2,
                        preformatted.length());
            return ret;
        }
    }

    /**
     * Parses the provided date and time in ISO8601 format
     * yyyy-MM-ddThh:mm:ss.sssZ, yyyy-MM-dd-Thh:mm:ss.sss+hh:mm, or
     * yyyy-MM-dd-Thh:mm:ss.sss-hh:mm.
     * 
     * @param dateText
     *            The date and time to parse.
     * @return Returns a <code>Date</code> object representing the provided date
     *         and time.
     * @throws ParseException
     *             Thrown if the parsing fails.
     * @common
     */
    @Deprecated
    public static synchronized Date parseIso8601(final String dateText)
        throws ParseException {

        if ((dateText == null) || (dateText.length() == 0)) {
            throw new ParseException("Could not parse text: [null]", 0);
        }

        String tmpDateText;
        if (dateText.endsWith("Z")) {
            // FIXME quick workaround (FRS)
            if (dateText.length() == 20) {
                tmpDateText =
                    dateText.substring(0, dateText.length() - 1) + ".0+0000";
            }
            else {
                tmpDateText =
                    dateText.substring(0, dateText.length() - 1) + "+0000";
            }
        }
        else {
            final int pos = dateText.length() - SIX;
            char c = dateText.charAt(pos);
            if (c == '+' || c == '-') {
                tmpDateText =
                    dateText.substring(0, dateText.length() - FOUR)
                        + dateText.substring(dateText.length() - 2);
            }
            else {
                throw new ParseException(StringUtility
                    .concatenateWithBracketsToString(
                        "Could not parse date text", dateText), pos);
            }
        }

        if (inputDateFormat == null) {
            inputDateFormat = createDateFormat(DATE_FORMAT_PATTERN);
            inputDateFormat.setLenient(false);
        }
        Date ret = null;
        try {
            ret = inputDateFormat.parse(tmpDateText);
        }
        catch (ParseException e) {
            throw e;
        }
        return ret;
    }

    /**
     * Checks if the dates specified by the two provided Strings are equal.
     * 
     * @param date1String
     *            The first date as a String.
     * @param date2String
     *            The second date as a String.
     * @return Returns <code>true</code> if both dates are equal.
     * @throws ParseException
     *             Thrown if parsing a provided date String fails.
     * @common
     */
    @Deprecated
    public static synchronized boolean equalDates(
        final String date1String, final String date2String)
        throws ParseException {
        Date date1 = parseIso8601(date1String);
        boolean result = equalDates(date1, date2String);
        return result;
    }

    /**
     * Checks if the dates specified by the two provided Date and String are
     * equal.
     * 
     * @param date1
     *            The first date.
     * @param date2String
     *            The second date as a String.
     * @return Returns <code>true</code> if both dates are equal.
     * @throws ParseException
     *             Thrown if parsing the provided date String fails.
     * @common
     */
    public static boolean equalDates(final Date date1, final String date2String)
        throws ParseException {

        final Date date2 = parseIso8601(date2String);
        final boolean result = date2.equals(date1);
        return result;
    }
}
