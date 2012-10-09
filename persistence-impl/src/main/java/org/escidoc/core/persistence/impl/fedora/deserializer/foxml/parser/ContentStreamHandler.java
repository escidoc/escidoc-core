package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;

import org.escidoc.core.persistence.impl.fedora.resource.ContentStream;
import org.escidoc.core.persistence.impl.fedora.resource.ContentStreams;


import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class ContentStreamHandler extends VersionedDatastreamHandler {
    private static final String CONTENT_STREAM_ID = "content-stream";

    private static final String CONTENT_LOCATION_PATH =
        DATASTREAM_VERSION_PATH + "/contentLocation";

    private ContentStreams contentStreams = new ContentStreams();

    public ContentStreamHandler(final StaxParser parser, final String date)
        throws XMLStreamException {
        super(parser, "", date);
    }

    @Override
    public String characters(final String s, StartElement element) {
        return s;
    }

    @Override
    public EndElement endElement(EndElement element) {
        if (insideLevel > 0) {
            insideLevel--;
            if (insideXmlContent) {
                if (insideLevel == 0) {
                    insideXmlContent = false;
                }
            }
        }
        return element;
    }

    public ContentStreams getContentStreams() {
        return contentStreams;
    }

    @Override
    protected void initWriter() {
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
            try {
                if (DATASTREAM_PATH.equals(parser.getCurPath())) {
                    datastreamName =
                        element.getAttribute(null, "ID").getValue();
                    controlGroup =
                        element.getAttribute(null, "CONTROL_GROUP").getValue();
                }
                else if (DATASTREAM_VERSION_PATH.equals(parser.getCurPath())) {
                    try {
                        final String[] altIds =
                            element
                                .getAttribute(null, "ALT_IDS").getValue()
                                .split(" ");

                        if (altIds.length > 0) {
                            if (CONTENT_STREAM_ID.equals(altIds[0])) {
                                final String created =
                                    element
                                        .getAttribute(null, "CREATED")
                                        .getValue();

                                mimeType =
                                    element
                                        .getAttribute(null, "MIMETYPE")
                                        .getValue();
                                if ((date == null)
                                    || (created.compareTo(date) <= 0)) {
                                    if (MIMETYPE_DELETED.equals(mimeType)) {
                                        contentStreams.remove(datastreamName);
                                    }
                                    else {
                                        ContentStream contentStream =
                                            new ContentStream();

                                        contentStream.setName(datastreamName);
                                        contentStream.setLabel(element
                                            .getAttribute(null, "LABEL")
                                            .getValue());
                                        contentStream.setMimeType(element
                                            .getAttribute(null, "MIMETYPE")
                                            .getValue());
                                        contentStream
                                            .setStorage(getStorageType());
                                        contentStreams.put(datastreamName,
                                            contentStream);
                                    }
                                    insideXmlContent = true;
                                }
                            }
                        }
                    }
                    catch (NoSuchAttributeException e) {
                    }
                }
                else if (CONTENT_LOCATION_PATH.equals(parser.getCurPath())) {
                    if (insideXmlContent) {
                        insideLevel++;

                        ContentStream contentStream =
                            contentStreams.get(datastreamName);

                        if (contentStream != null) {
                            contentStream.setReference(element.getAttribute(
                                null, "REF").getValue());
                        }
                    }
                }
            }
            catch (NoSuchAttributeException e) {
                System.out.println(e);
            }
        }
        return element;
    }
}