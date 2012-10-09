package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;

import org.escidoc.core.persistence.impl.fedora.resource.Predicate;
import org.escidoc.core.persistence.impl.fedora.resource.Relation;
import org.escidoc.core.persistence.impl.fedora.resource.Relations;
import org.escidoc.core.persistence.impl.fedora.resource.Target;


import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class RelsExtHandler extends VersionedDatastreamHandler {
    private final RelsExt relsExt = new RelsExt();

    private RelsExtValues values = new RelsExtValues();

    private int insideLevel = 0;

    public RelsExtHandler(final StaxParser parser, final String id,
        final String date) throws XMLStreamException {
        super(parser, id, date);
    }

    public static String getEscidocId(final String id) {
        return id.substring(12);
    }

    public String geFirstVersionTimestamp() {
        return relsExt.firstEntry().getKey();
    }

    public RelsExtValues getLatestVersionValues() {
        return relsExt.lastEntry().getValue();
    }

    public RelsExtValues getValues() {
        RelsExtValues result = null;

        if (date != null) {
            // get the newest RELS-EXT <= date
            Entry<String, RelsExtValues> entry = relsExt.floorEntry(date);

            if (entry == null) {
                // bug in container Foxml?
                entry = relsExt.lastEntry();
            }
            result = entry.getValue();
        }
        else {
            result = relsExt.lastEntry().getValue();
        }
        return result;
    }

    @Override
    public String characters(final String s, StartElement element)
        throws IntegritySystemException, XmlParserSystemException {
        if (insideXmlContent) {
            final String prefix = element.getPrefix();
            final String localName = element.getLocalName();
            String key = "";

            if (prefix != null) {
                key += prefix;
                key += ":";
            }
            key += localName;

            if ("nsCR".equals(prefix)) {
                // content relation
                try {
                    Predicate predicate = new Predicate();
                    Target target = new Target();
                    Relation relation = new Relation();

                    predicate.setLocalName(element.getLocalName());
                    predicate.setNamespace(element.getNamespace());
                    relation.setPredicate(predicate);
                    target.setId(getEscidocId(element.getAttribute(
                        Constants.RDF_NAMESPACE_URI, "resource").getValue()));
                    relation.setTarget(target);
                    values.addRelation(relation);
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
            else if ("rdf".equals(prefix)) {
                if (("predicate".equals(localName))
                    || ("type".equals(localName))) {
                    try {
                        values.put(
                            key,
                            element.getAttribute(Constants.RDF_NAMESPACE_URI,
                                "resource").getValue());
                    }
                    catch (NoSuchAttributeException e) {
                        System.out.println(e);
                    }
                }
                else if (("object".equals(localName))
                    || ("subject".equals(localName))) {
                    try {
                        values.put(
                            key,
                            getEscidocId(element
                                .getAttribute(Constants.RDF_NAMESPACE_URI,
                                    "resource").getValue()));
                    }
                    catch (NoSuchAttributeException e) {
                        System.out.println(e);
                    }
                }
            }
            else if ("srel".equals(prefix)) {
                // structural relation
                try {
                    values
                        .put(
                            key,
                            getEscidocId(element
                                .getAttribute(Constants.RDF_NAMESPACE_URI,
                                    "resource").getValue()));
                }
                catch (NoSuchAttributeException e) {
                    values.put(key, s);
                }
            }
            else if ("hasService".equals(localName)) {
                // BDef object of a content model
                try {
                    values
                        .put(
                            key,
                            getEscidocId(element
                                .getAttribute(Constants.RDF_NAMESPACE_URI,
                                    "resource").getValue()));
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
            else {
                values.put(key, s);
            }
        }
        return s;
    }

    @Override
    public EndElement endElement(EndElement element) {
        if (insideLevel > 0) {
            insideLevel--;
            if (insideLevel == 0) {
                insideXmlContent = false;
                relsExt.put(datastreamDate, values);
                values = new RelsExtValues();
            }
        }
        return element;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * 
     * @see de.escidoc.core.om.business.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.om.business.stax.events.StartElement)
     */
    public StartElement startElement(final StartElement element) {
        if (element != null) {
            if (insideLevel > 0) {
                insideLevel++;
            }
            if (DATASTREAM_PATH.equals(parser.getCurPath())) {
                try {
                    datastreamName =
                        element.getAttribute(null, "ID").getValue();
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
            else if (name.equals(datastreamName)
                && DATASTREAM_VERSION_PATH.equals(parser.getCurPath())) {
                try {
                    datastreamDate =
                        element.getAttribute(null, "CREATED").getValue();
                    insideLevel++;
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
            else if (XML_CONTENT_PATH.equals(parser.getCurPath())) {
                if (insideLevel > 0) {
                    insideXmlContent = true;
                }
            }
        }
        return element;
    }

    // list of values
    public static class RelsExtValue extends Vector<String> {
        private static final long serialVersionUID = -1287196328679967895L;
    }

    // mapping from name to list of values
    public static class RelsExtValues extends HashMap<String, RelsExtValue> {
        private static final long serialVersionUID = 3262383469412361475L;

        private final Relations relations = new Relations();

        public void addRelation(final Relation relation) {
            relations.add(relation);
        }

        public String getFirst(String key) {
            RelsExtValue currentValue = get(key);

            if (currentValue != null) {
                return currentValue.iterator().next();
            }
            else {
                return null;
            }
        }

        public Relations getRelations() {
            return relations;
        }

        public void put(String key, String value) {
            RelsExtValue currentValue = get(key);

            if (currentValue != null) {
                currentValue.add(value);
            }
            else {
                currentValue = new RelsExtValue();
                currentValue.add(value);
                put(key, currentValue);
            }
        }
    }

    // mapping from time stamp to RELS-EXT values
    public static class RelsExt extends TreeMap<String, RelsExtValues> {
        private static final long serialVersionUID = -1617373515386086393L;
    }
}