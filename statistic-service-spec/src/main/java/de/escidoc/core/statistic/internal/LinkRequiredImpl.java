package de.escidoc.core.statistic.internal;

import de.escidoc.core.statistic.LinkRequired;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Default implementation of {@link LinkRequired}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkRequired", namespace = "http://www.escidoc.de/schemas/commontypes/0.3")
public class LinkRequiredImpl implements LinkRequired {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String objid;

    /**
     * Public constructor to allow JAXB serialisation.
     */
    public LinkRequiredImpl() {
    }

    /**
     * Instantiate a new {@link LinkRequired} instance using the given object identifier.
     *
     * @param objid the object identifier
     */
    public LinkRequiredImpl(final String objid) {
        this.objid = objid;
    }

    @Override
    public String getObjid() {
        return this.objid;
    }

    @Override
    public String toString() {
        return "LinkRequiredImpl{" + // NON-NLS
            "objid='" + this.objid + '\'' + // NON-NLS
            '}'; // NON-NLS
    }
}
