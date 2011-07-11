package org.esidoc.core.utils.xml;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

/**
 * JAXB Converter to support {@link DateTime} for xsd:date and xsd:dateTime.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class DateTimeJaxbConverter {

    private DateTimeJaxbConverter() {
    }

    public static DateTime parseDate(final String dateString) {
        if (dateString == null) {
            return null;
        }
        final Calendar calendar = DatatypeConverter.parseDate(dateString);
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forTimeZone(calendar.getTimeZone()));
    }

    public static String printDate(final DateTime date) {
        if (date == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        calendar.setTimeZone(date.getZone().toTimeZone());
        return DatatypeConverter.printDateTime(calendar);
    }
}
