package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;

import org.escidoc.core.persistence.impl.fedora.resource.MdRecord;
import org.escidoc.core.persistence.impl.fedora.resource.MdRecords;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class MdRecordHandler extends VersionedDatastreamHandler {
    private static final String MD_RECORD_ID = "metadata";

    // mapping datastreamName->datastreamDate
    private final Map<String, String> datastreams =
        new HashMap<String, String>();

    private String mdRecordType = null;

    private String mdRecordSchema = null;

    private MdRecords mdRecords = new MdRecords();

    public MdRecordHandler(final StaxParser parser, final String date)
        throws XMLStreamException {
        super(parser, "", date);
    }

    @Override
    public EndElement endElement(EndElement element) {
        if (insideLevel > 0) {
            try {
                insideLevel--;
                if (insideLevel == 0) {
                    writer.flush();
                    if (MIMETYPE_DELETED.equals(mimeType)) {
                        mdRecords.remove(datastreamName);
                    }
                    else {
                        MdRecord mdRecord = new MdRecord();

                        mdRecord.setName(datastreamName);
                        if (!mdRecordSchema.equals("unknown")) {
                            mdRecord.setSchema(mdRecordSchema);
                        }
                        if (!mdRecordType.equals("unknown")) {
                            mdRecord.setType(mdRecordType);
                        }
                        mdRecord.setXmlContent(out.toString());
                        mdRecords.put(datastreamName, mdRecord);
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
        return element;
    }

    public MdRecords getMdRecords() {
        return mdRecords;
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
                if (!MIMETYPE_DELETED.equals(mimeType)) {
                    try {
                        writer.writeStartElement(element.getPrefix(),
                            element.getLocalName(), element.getNamespace());

                        List<Attribute> attributes = element.getAttributes();

                        if (attributes != null) {
                            for (Attribute attribute : attributes) {
                                final String prefix = attribute.getPrefix();
                                final String localName =
                                    attribute.getLocalName();
                                final String value = attribute.getValue();

                                if (("xsi".equals(prefix) && ("type"
                                    .equals(localName)))) {
                                    String[] valueParts = value.split(":");
                                    String nsURI =
                                        element
                                            .getNamespaceContext()
                                            .getNamespaceURI(valueParts[0]);

                                    if (nsURI != null) {
                                        writer.writeNamespace(valueParts[0],
                                            nsURI);
                                    }
                                }
                                writer.writeAttribute(prefix,
                                    attribute.getNamespace(), localName, value);
                            }
                        }
                    }
                    catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (DATASTREAM_PATH.equals(parser.getCurPath())) {
                try {
                    datastreamName =
                        element.getAttribute(null, "ID").getValue();
                }
                catch (NoSuchAttributeException e) {
                    e.printStackTrace();
                }
            }
            else if (DATASTREAM_VERSION_PATH.equals(parser.getCurPath())) {
                try {
                    final String[] altIds =
                        element
                            .getAttribute(null, "ALT_IDS").getValue()
                            .split(" ");

                    if (altIds.length > 0) {
                        if (MD_RECORD_ID.equals(altIds[0])) {
                            try {
                                final String created =
                                    element
                                        .getAttribute(null, "CREATED")
                                        .getValue();

                                mimeType =
                                    element
                                        .getAttribute(null, "MIMETYPE")
                                        .getValue();
                                datastreamDate =
                                    datastreams.get(datastreamName);
                                if (((datastreamDate == null) || ((created
                                    .compareTo(datastreamDate) > 0)))
                                    && ((date == null) || created
                                        .compareTo(date) <= 0)) {
                                    datastreamDate = created;
                                    insideXmlContent = true;
                                    if (altIds.length > 1) {
                                        mdRecordType = altIds[1];
                                    }
                                    if (altIds.length > 2) {
                                        mdRecordSchema = altIds[2];
                                    }
                                    datastreams.put(datastreamName, created);
                                }
                            }
                            catch (NoSuchAttributeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catch (NoSuchAttributeException e) {
                }
            }
            else if (XML_CONTENT_PATH.equals(parser.getCurPath())) {
                if (insideXmlContent) {
                    insideLevel++;
                }
            }
        }
        return element;
    }
}