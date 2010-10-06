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
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="statistic-record", namespace="http://www.escidoc.de/schemas/commontypes/0.3",
        propOrder = {"scope",
                "parameter"
})
@XmlRootElement(name="statistic-record", namespace="http://www.escidoc.de/schemas/commontypes/0.3")
public class StatisticRecordImpl implements StatisticRecord {

    @XmlElement(required = true)
    private LinkRequiredImpl scope;
    private final List<ParameterImpl> parameter = new ArrayList<ParameterImpl>();

    /**
     * Public constructor to allow JAXB serialisation.
     */
    public StatisticRecordImpl() {
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StatisticRecordImpl{" +
                "scope=" + scope +
                ", parameter=" + parameter +
                '}';
    }
}

