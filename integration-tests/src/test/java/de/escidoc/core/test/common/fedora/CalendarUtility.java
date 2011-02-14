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
package de.escidoc.core.test.common.fedora;

import de.escidoc.core.test.common.logger.AppLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class CalendarUtility {

    protected static AppLogger log =
        new AppLogger(CalendarUtility.class.getName());

    public static Calendar TransformStringToCalendar(
        final String date, final SimpleDateFormat sdfInput) {
        Date transformedDate = null;

        try {
            transformedDate = sdfInput.parse(date);
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            log.error(e);
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(transformedDate);
        return calendar;
    }
}
