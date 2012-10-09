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

public class XmlContentHandler extends AbstractHandler {
    private final XMLOutputFactory f = XMLOutputFactory.newInstance();

    protected XMLStreamWriter writer = null;

    protected int insideLevel = 0;

    protected boolean insideXmlContent = false;

    protected Writer out = new StringWriter();

    private String xmlContent = null;

    public XmlContentHandler(final StaxParser parser, final String path)
        throws XMLStreamException {
        super(parser, path);
        f.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        initWriter();
    }

    @Override
    public String characters(final String s, StartElement element)
        throws IntegritySystemException, XmlParserSystemException {
        if (insideXmlContent) {
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
            try {
                if (insideLevel == 0) {
                    insideXmlContent = false;
                    writer.flush();
                    xmlContent = out.toString();
                    initWriter();
                }
                else {
                    writer.writeEndElement();
                }
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        return element;
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
                if (path.equals(parser.getCurPath())) {
                    insideLevel++;
                    insideXmlContent = false;
                }
                if (insideLevel > 0) {
                    insideLevel++;
                    writer.writeStartElement(element.getPrefix(),
                        element.getLocalName(), element.getNamespace());

                    List<Attribute> attributes = element.getAttributes();

                    if (attributes != null) {
                        for (Attribute attribute : attributes) {
                            writer.writeAttribute(attribute.getPrefix(),
                                attribute.getNamespace(),
                                attribute.getLocalName(), attribute.getValue());
                        }
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
