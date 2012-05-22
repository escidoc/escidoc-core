package org.escidoc.core.utils.xml;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

/**
 * JAXB Converter to support {@link DateTime} for xsd:date and xsd:dateTime.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class DateJaxbConverter {

    private DateJaxbConverter() {
    }

    /**
     *
     * @param dateString
     * @return
     */
    public static DateTime parseDate(final String dateString) {
        if (dateString == null) {
            return null;
        }
        final Calendar calendar = DatatypeConverter.parseDate(dateString);
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forTimeZone(calendar.getTimeZone()));
    }

    /**
     *
     * @param date
     * @return
     */
    public static String printDate(final DateTime date) {
        if (date == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        calendar.setTimeZone(date.getZone().toTimeZone());
        return DatatypeConverter.printDate(calendar);
    }
}
