package de.escidoc.core.statistic.internal;

import de.escidoc.core.statistic.StatisticRecord;
import de.escidoc.core.statistic.StatisticRecordBuilder;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Default implementation of {@link StatisticRecordBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class StatisticRecordBuilderImpl extends StatisticRecordBuilder {

    private StatisticRecordImpl statisticRecord = new StatisticRecordImpl();
    private String scopeId = "escidoc:scope1"; // NON-NLS

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticRecordBuilder withParameter(final String name, final String value) {
        if(value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticRecordBuilder withParameter(final String name, final BigDecimal value) {
        if(value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticRecordBuilder withParameter(final String name, final DateTime value) {
        if(value != null) {
            final ParameterImpl parameter = new ParameterImpl(name, value);
            this.statisticRecord.addParameter(parameter);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticRecordBuilder withParameter(final String name, final boolean value) {
        final ParameterImpl parameter = new ParameterImpl(name, value);
        this.statisticRecord.addParameter(parameter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StatisticRecordBuilderImpl{" +
                "statisticRecord=" + statisticRecord +
                ", scopeId='" + scopeId + '\'' +
                '}';
    }
}
