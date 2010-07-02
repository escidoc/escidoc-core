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
package de.escidoc.core.om.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.escidoc.core.common.exceptions.system.SystemException;

public final class CalendarUtility {

    private static Pattern XMLSCHEMA_DATE_TIME_LAX =
        Pattern
            .compile("^(\\-)?(\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}:\\d{2})(:\\d{0,2})?(\\.\\d{0,3})?([+-]\\d{2}:\\d{2}|Z)?$");

    public static Calendar TransformStringToCalendar(
        final String date, final SimpleDateFormat sdfInput) {
        Date transformedDate = null;

        try {
            transformedDate = sdfInput.parse(date);
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(transformedDate);
        return calendar;
    }


    public static String normalizeDate(final String date)
        throws SystemException {
        String result = "";

        Matcher xsDateTime = XMLSCHEMA_DATE_TIME_LAX.matcher(date);

        if (xsDateTime.find()) {

            String tmp = xsDateTime.group(1);
            if (tmp != null && tmp.length() > 0) {
                result += tmp;
            }

            // YYYY-MM-DDTHH:mm
            result += xsDateTime.group(2);

            tmp = xsDateTime.group(3);
            if (tmp != null && tmp.length() > 0) {
                // :ss
                while (tmp.length() < 3) {
                    tmp += 0;
                }
                result += tmp;
            }
            else {
                result += ":00";
            }

            tmp = xsDateTime.group(4);
            if (tmp != null && tmp.length() > 0) {
                // .uuu
                while (tmp.length() < 4) {
                    tmp += 0;
                }
                result += tmp;
            }
            else {
                result += ".000";
            }

            tmp = xsDateTime.group(5);
            if (tmp != null && tmp.length() == 6) {
                // Z|+zz:zz
                result += tmp;
            }
            else {
                result += "Z";
            }
        }
        else {
            throw new SystemException("Date is not conform to xs:dateTime. ("
                + date + ")");
        }

        return result;
    }
}
