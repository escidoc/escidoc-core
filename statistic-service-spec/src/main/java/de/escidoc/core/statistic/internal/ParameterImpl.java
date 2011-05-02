package de.escidoc.core.statistic.internal;

import de.escidoc.core.statistic.Parameter;
import org.joda.time.ReadableInstant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

/**
 * Default implementation of {@link Parameter}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parameter", namespace = "http://www.escidoc.de/schemas/commontypes/0.3", propOrder = { "datevalue",
    "stringvalue", "decimalvalue" })
public class ParameterImpl implements Parameter {

    private static final String VALUE_FALSE = "0"; // NON-NLS

    private static final String VALUE_TRUE = "1"; // NON-NLS

    @XmlAttribute(required = true)
    private String name;

    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar datevalue;

    private String stringvalue;

    private BigDecimal decimalvalue;

    /**
     * Public constructor to allow JAXB serialisation.
     */
    public ParameterImpl() {
    }

    /**
     * Instantiate a new {@link Parameter} instance using the given name and date value.
     *
     * @param name the name of the parameter
     * @param value
     */
    public ParameterImpl(final String name, final ReadableInstant value) {
        this.name = name;
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(value.getMillis());
        try {
            this.datevalue = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        }
        catch (final DatatypeConfigurationException e) {
            throw new IllegalStateException("Error on instantiating datatype factory.", e);
        }
    }

    /**
     * Instantiate a new {@link Parameter} instance using the given name and string value.
     *
     * @param name the name of the parameter
     * @param value
     */
    public ParameterImpl(final String name, final String value) {
        this.name = name;
        this.stringvalue = value;
    }

    /**
     * Instantiate a new {@link Parameter} instance using the given name and decimal value.
     *
     * @param name the name of the parameter
     * @param value
     */
    public ParameterImpl(final String name, final BigDecimal value) {
        this.name = name;
        this.decimalvalue = value;
    }

    /**
     * Instantiate a new {@link Parameter} instance using the given name and boolean value.
     *
     * @param name the name of the parameter
     * @param value
     */
    public ParameterImpl(final String name, final boolean value) {
        this.name = name;
        this.stringvalue = value ? VALUE_TRUE : VALUE_FALSE;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public XMLGregorianCalendar getDateValue() {
        return this.datevalue;
    }

    @Override
    public String getStringValue() {
        return this.stringvalue;
    }

    @Override
    public BigDecimal getDecimalValue() {
        return this.decimalvalue;
    }

    @Override
    public String toString() {
        return "ParameterImpl{" + "name='" + this.name + '\'' + ", datevalue=" + this.datevalue + ", stringvalue='"
            + this.stringvalue + '\'' + ", decimalvalue=" + this.decimalvalue + '}';
    }
}
