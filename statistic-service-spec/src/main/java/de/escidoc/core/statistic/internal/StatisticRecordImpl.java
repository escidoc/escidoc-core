package de.escidoc.core.statistic.internal;

import de.escidoc.core.statistic.LinkRequired;
import de.escidoc.core.statistic.Parameter;
import de.escidoc.core.statistic.StatisticRecord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link StatisticRecord}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statistic-record", propOrder = { "scope", "parameter" })
@XmlRootElement(name = "statistic-record")
public class StatisticRecordImpl implements StatisticRecord {

    @XmlElement(required = true)
    private LinkRequiredImpl scope;

    private final List<ParameterImpl> parameter = new ArrayList<ParameterImpl>();

    @Override
    public LinkRequired getScope() {
        return this.scope;
    }

    /**
     * Sets the scope.
     *
     * @param value the scope.
     */
    public void setScope(final LinkRequiredImpl value) {
        this.scope = value;
    }

    @Override
    public List<Parameter> getParameter() {
        return new ArrayList<Parameter>(this.parameter);
    }

    /**
     * Adds an new parameter to the record.
     *
     * @param parameter the parameter
     */
    public void addParameter(final ParameterImpl parameter) {
        this.parameter.add(parameter);
    }

    @Override
    public String toString() {
        return "StatisticRecordImpl{" + "scope=" + this.scope + ", parameter=" + this.parameter + '}';
    }
}
