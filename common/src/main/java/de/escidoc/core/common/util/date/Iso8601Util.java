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

package de.escidoc.core.common.util.date;

import de.escidoc.core.common.util.string.StringUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility class to support date and time conversion to ISO86001 format.
 * 
 * @author TTE
 *
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

    private static final Calendar CALENDAR = new GregorianCalendar(TimeZone.getTimeZone(UTC_TIMEZONE_ID));

    /**
     * Private Constructor to prevent instantiation.
     * 
     *
     */
    private Iso8601Util() {
    }

    /**
     * Creates a date format for the provided pattern.<br>
     * 
     * @param pattern
     *            The format pattern.
     * @return Returns an instance of <code>SimpleDateFormat</code> using the
     *         provided pattern and the gregorian CALENDAR with UTC time zone.
     *
     */
    private static DateFormat createDateFormat(final String pattern) {
        final DateFormat df = new SimpleDateFormat(pattern);
        df.setCalendar(CALENDAR);
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
     *
     * 
     */
    @Deprecated
    public static String getIso8601(final Date date) {
        final DateFormat outputDateFormat = createDateFormat(DATE_FORMAT_PATTERN);
        final String preformatted = outputDateFormat.format(date);
        return preformatted.endsWith("Z") ? preformatted : preformatted.endsWith("+0000") ?
                preformatted.substring(0, preformatted.length() - "+0000".length()) + 'Z' :
                preformatted.substring(0, preformatted.length() - 2) + ':' +
                        preformatted.substring(preformatted.length() - 2, preformatted.length());
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
     *
     */
    @Deprecated
    public static Date parseIso8601(final String dateText)
        throws ParseException {

        if (dateText == null || dateText.length() == 0) {
            throw new ParseException("Could not parse text: [null]", 0);
        }

        final String tmpDateText;
        if (dateText.endsWith("Z")) {
            // FIXME quick workaround (FRS)
            tmpDateText = dateText.length() == 20 ? dateText.substring(0, dateText.length() - 1) + ".0+0000" : dateText.substring(0, dateText.length() - 1) + "+0000";
        }
        else {
            final int pos = dateText.length() - SIX;
            final char c = dateText.charAt(pos);
            if ((int) c == (int) '+' || (int) c == (int) '-') {
                tmpDateText =
                    dateText.substring(0, dateText.length() - FOUR)
                        + dateText.substring(dateText.length() - 2);
            }
            else {
                throw new ParseException(StringUtility
                    .format(
                        "Could not parse date text", dateText), pos);
            }
        }
        final DateFormat inputDateFormat = createDateFormat(DATE_FORMAT_PATTERN);
        inputDateFormat.setLenient(false);
        return inputDateFormat.parse(tmpDateText);
    }

}
