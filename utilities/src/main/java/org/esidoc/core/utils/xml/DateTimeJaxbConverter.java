package org.esidoc.core.utils.xml;

import org.joda.time.DateTime;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

/**
 * JAXB Converter to support {@link DateTime} for xsd:date and xsd:dateTime.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class DateTimeJaxbConverter {

    public static DateTime parseDate(final String dateString) {
        if (dateString == null) {
            return null;
        }
        return new DateTime(DatatypeConverter.parseDate(dateString).getTime());
    }

    public static String printDate(final DateTime date) {
        if (date == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        return DatatypeConverter.printDate(calendar);
    }
}
