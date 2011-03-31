package de.escidoc.core.statistic;

import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test for {@link StatisticRecordBuilder} and its implementations.
 */
public class StatisticRecordBuilderTest {

    private static final long TEST_DECIMAL_VALUE = 10L;

    @Test
    public void testStatisticRecord() {
        final DateTime now = new DateTime();
        final StatisticRecord statisticRecord =
            StatisticRecordBuilder.createStatisticRecord().withParameter("string", "stringvalue") // NON-NLS
            .withParameter("date", now) // NON-NLS
                .withParameter("decimal", BigDecimal.valueOf(TEST_DECIMAL_VALUE)) // NON-NLS
                .withParameter("booleanTrue", true) // NON-NLS
                .withParameter("booleanFalse", false) // NON-NLS
                .build();
        assertEquals("wrong number of parameters", 5, statisticRecord.getParameter().size()); // NON-NLS
        final Map<String, Parameter> parametersMap = new HashMap<String, Parameter>(5);
        for (final Parameter parameter : statisticRecord.getParameter()) {
            parametersMap.put(parameter.getName(), parameter);
        }
        assertEquals("wrong string value", "stringvalue", parametersMap.get("string").getStringValue()); // NON-NLS
        assertEquals("wrong date value", now.getMillis(), parametersMap
            .get("date").getDateValue().toGregorianCalendar().getTimeInMillis()); // NON-NLS
        assertEquals("wrong decimal value", BigDecimal.valueOf(TEST_DECIMAL_VALUE), parametersMap
            .get("decimal").getDecimalValue()); // NON-NLS
        assertEquals("wrong boolean value", "1", parametersMap.get("booleanTrue").getStringValue()); // NON-NLS
        assertEquals("wrong boolean value", "0", parametersMap.get("booleanFalse").getStringValue()); // NON-NLS
    }

}
