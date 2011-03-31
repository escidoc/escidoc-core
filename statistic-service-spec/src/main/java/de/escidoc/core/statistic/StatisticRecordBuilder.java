package de.escidoc.core.statistic;

import de.escidoc.core.statistic.internal.StatisticRecordBuilderImpl;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Builder for {@link StatisticRecord}.
 */
public abstract class StatisticRecordBuilder {

    /**
     * Create a new {@link StatisticRecord}.
     *
     * @return a new StatisticRecordBuilder instance.
     */
    public static StatisticRecordBuilder createStatisticRecord() {
        return new StatisticRecordBuilderImpl();
    }

    /**
     * Create a new {@link StatisticRecord} with the given {@link String} parameter.
     *
     * @param name  the name of the parameter
     * @param value the {@link String} value of the parameter
     * @return a the current StatisticRecordBuilder instance.
     */
    public abstract StatisticRecordBuilder withParameter(String name, String value);

    /**
     * Create a new {@link StatisticRecord} with the given {@link BigDecimal} parameter.
     *
     * @param name  the name of the parameter
     * @param value the {@link BigDecimal} value of the parameter
     * @return a the current StatisticRecordBuilder instance.
     */
    public abstract StatisticRecordBuilder withParameter(String name, BigDecimal value);

    /**
     * Create a new {@link StatisticRecord} with the given {@link DateTime} parameter.
     *
     * @param name  the name of the parameter
     * @param value the {@link DateTime} value of the parameter
     * @return a the current StatisticRecordBuilder} instance.
     */
    public abstract StatisticRecordBuilder withParameter(String name, DateTime value);

    /**
     * Create a new {@link StatisticRecord} with the given boolean parameter.
     *
     * @param name  the name of the parameter
     * @param value the boolean value of the parameter
     * @return a the current StatisticRecordBuilder instance.
     */
    public abstract StatisticRecordBuilder withParameter(String name, boolean value);

    /**
     * Builds a new {@link StatisticRecord} instance.
     *
     * @return a new {@link StatisticRecord} instance
     */
    public abstract StatisticRecord build();

}
