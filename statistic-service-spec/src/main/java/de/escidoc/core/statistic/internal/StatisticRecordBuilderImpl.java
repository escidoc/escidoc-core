package de.escidoc.core.statistic.internal;

import de.escidoc.core.statistic.StatisticRecord;
import de.escidoc.core.statistic.StatisticRecordBuilder;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Default implementation of {@link StatisticRecordBuilder}.
 */
public class StatisticRecordBuilderImpl extends StatisticRecordBuilder {

    private StatisticRecordImpl statisticRecord = new StatisticRecordImpl();

    private String scopeId = "escidoc:scope1"; // NON-NLS

    @Override
    public StatisticRecordBuilder withParameter(final String name, final String value) {
        if (value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    @Override
    public StatisticRecordBuilder withParameter(final String name, final BigDecimal value) {
        if (value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    @Override
    public StatisticRecordBuilder withParameter(final String name, final DateTime value) {
        if (value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    @Override
    public StatisticRecordBuilder withParameter(final String name, final boolean value) {
        final ParameterImpl parameter = new ParameterImpl(name, value);
        this.statisticRecord.addParameter(parameter);
        return this;
    }

    @Override
    public StatisticRecord build() {
        final StatisticRecordImpl returnValue = this.statisticRecord;
        final LinkRequiredImpl scope = new LinkRequiredImpl(this.scopeId);
        returnValue.setScope(scope);
        this.statisticRecord = new StatisticRecordImpl();
        return returnValue;
    }

    /**
     * Sets the scope id.
     *
     * @param scopeId the scope id.
     */
    public void setScopeId(final String scopeId) {
        // TODO: Set scope ID from Spring context.
        this.scopeId = scopeId;
    }

    @Override
    public String toString() {
        return "StatisticRecordBuilderImpl{" + "statisticRecord=" + this.statisticRecord + ", scopeId='" + this.scopeId
            + '\'' + '}';
    }
}
