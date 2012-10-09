package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class VersionedDatastreamHandler extends DefaultHandler {
    protected static final String DATASTREAM_PATH = "/digitalObject/datastream";

    protected static final String DATASTREAM_VERSION_PATH = DATASTREAM_PATH
        + "/datastreamVersion";

    // mark a data stream as deleted
    protected static final String MIMETYPE_DELETED = "deleted";

    protected static final String XML_CONTENT_PATH = DATASTREAM_VERSION_PATH
        + "/xmlContent";

    protected final StaxParser parser;

    protected final String name;

    protected final String date;

    private final XMLOutputFactory f = XMLOutputFactory.newInstance();

    protected XMLStreamWriter writer;

    protected Writer out = new StringWriter();

    protected int insideLevel = 0;

    protected boolean insideXmlContent = false;

    protected String datastreamDate = null;

    protected String datastreamName = null;

    protected String controlGroup = null;

    protected String mimeType = null;

    private String xmlContent = null;

    public VersionedDatastreamHandler(final StaxParser parser,
        final String name, final String date) throws XMLStreamException {
        this.parser = parser;
        this.name = name;
        this.date = date;
        f.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        initWriter();
    }

    @Override
    public String characters(final String s, StartElement element)
        throws IntegritySystemException, XmlParserSystemException {
        if (insideXmlContent && (!MIMETYPE_DELETED.equals(mimeType))) {
            try {
                writer.writeCharacters(s);
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    @Override
    public EndElement endElement(EndElement element) {
        if (insideLevel > 0) {
            insideLevel--;
            if (insideXmlContent) {
                try {
                    if (insideLevel == 0) {
                        writer.flush();
                        if (!MIMETYPE_DELETED.equals(mimeType)) {
                            xmlContent = out.toString();
                        }
                        initWriter();
                        insideXmlContent = false;
                    }
                    else {
                        if (!MIMETYPE_DELETED.equals(mimeType)) {
                            writer.writeEndElement();
                        }
                    }
                }
                catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
        return element;
    }

    public String getName() {
        return datastreamName;
    }

    protected String getStorageType() {
        String result = null;

        if (controlGroup != null) {
            if (controlGroup.equals("M")) {
                result = "internal-managed";
            }
            else if (controlGroup.equals("E")) {
                result = "external-managed";
            }
            else if (controlGroup.equals("R")) {
                result = "external-url";
            }
        }
        return result;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    protected void initWriter() throws XMLStreamException {
        out = new StringWriter();
        writer = f.createXMLStreamWriter(out);
        // don't call startDocument because the written XML is used as snippet
        // and MUST NOT have a XML header
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
            try {
                if (insideLevel > 0) {
                    insideLevel++;
                    if (!MIMETYPE_DELETED.equals(mimeType)) {
                        writer.writeStartElement(element.getPrefix(),
                            element.getLocalName(), element.getNamespace());

                        List<Attribute> attributes = element.getAttributes();

                        if (attributes != null) {
                            for (Attribute attribute : attributes) {
                                writer.writeAttribute(attribute.getPrefix(),
                                    attribute.getNamespace(),
                                    attribute.getLocalName(),
                                    attribute.getValue());
                            }
                        }
                    }
                }
                if (DATASTREAM_PATH.equals(parser.getCurPath())) {
                    datastreamName =
                        element.getAttribute(null, "ID").getValue();
                }
                else if (DATASTREAM_VERSION_PATH.equals(parser.getCurPath())) {
                    if (name.equals(datastreamName)) {
                        final String created =
                            element.getAttribute(null, "CREATED").getValue();

                        mimeType =
                            element.getAttribute(null, "MIMETYPE").getValue();
                        if (((datastreamDate == null) || ((created
                            .compareTo(datastreamDate) > 0)))
                            && ((date == null) || created.compareTo(date) <= 0)) {
                            datastreamDate = created;
                            insideXmlContent = true;
                            xmlContent = null;
                        }
                    }
                }
                else if (XML_CONTENT_PATH.equals(parser.getCurPath())) {
                    if (insideXmlContent) {
                        insideLevel++;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return element;
    }
}