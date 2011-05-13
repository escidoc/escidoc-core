package org.escidoc.core.util.xml.internal;

import org.esidoc.core.utils.xml.DatastreamHolder;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

/**
 * {@link XMLStreamWriter} that filters content elements in {@link org.esidoc.core.utils.xml.DatastreamHolder} objects.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class FilteringXMLStreamWriter implements XMLStreamWriter {

    private final XMLStreamWriter xmlStreamWriter;
    private boolean ignore = false;

    public FilteringXMLStreamWriter(final XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = checkNotNull(xmlStreamWriter, "XML stream writer can not be null.");
    }

    public void close() throws XMLStreamException {
        xmlStreamWriter.close();
    }

    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return xmlStreamWriter.getNamespaceContext();
    }

    public String getPrefix(String s) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(s);
    }

    public Object getProperty(String s) throws IllegalArgumentException {
        return xmlStreamWriter.getProperty(s);
    }

    public void setDefaultNamespace(String s) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(s);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(namespaceContext);
    }

    public void setPrefix(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.setPrefix(s, s1);
    }

    public void writeAttribute(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1);
    }

    public void writeAttribute(String s, String s1, String s2) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2);
    }

    public void writeAttribute(String s, String s1, String s2, String s3) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2, s3);
    }

    public void writeCData(String s) throws XMLStreamException {
        xmlStreamWriter.writeCData(s);
    }

    public void writeCharacters(String s) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(s);
    }

    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(chars, i, i1);
    }

    public void writeComment(String s) throws XMLStreamException {
        xmlStreamWriter.writeComment(s);
    }

    public void writeDTD(String s) throws XMLStreamException {
        xmlStreamWriter.writeDTD(s);
    }

    public void writeDefaultNamespace(String s) throws XMLStreamException {
        xmlStreamWriter.writeDefaultNamespace(s);
    }

    public void writeEmptyElement(String s) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s);
    }

    public void writeEmptyElement(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s, s1);
    }

    public void writeEmptyElement(String s, String s1, String s2) throws XMLStreamException {
        if (!(DatastreamHolder.NAMESPACE.equals(s2) && DatastreamHolder.ELEMENT_NAME.equals(s1))) {
            xmlStreamWriter.writeEmptyElement(s, s1, s2);
        }
    }

    public void writeEndDocument() throws XMLStreamException {
        xmlStreamWriter.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        if (this.ignore) {
            this.ignore = false;
        } else {
            xmlStreamWriter.writeEndElement();
        }
    }

    public void writeEntityRef(String s) throws XMLStreamException {
        xmlStreamWriter.writeEntityRef(s);
    }

    public void writeNamespace(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeNamespace(s, s1);
    }

    public void writeProcessingInstruction(String s) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(s);
    }

    public void writeProcessingInstruction(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(s, s1);
    }

    public void writeStartDocument() throws XMLStreamException {
        xmlStreamWriter.writeStartDocument();
    }

    public void writeStartDocument(String s) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(s);
    }

    public void writeStartDocument(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(s, s1);
    }

    public void writeStartElement(String s) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s);
    }

    public void writeStartElement(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s, s1);
    }

    public void writeStartElement(String s, String s1, String s2) throws XMLStreamException {
        if (DatastreamHolder.NAMESPACE.equals(s2) && DatastreamHolder.ELEMENT_NAME.equals(s1)) {
            this.ignore = true;
        } else {
            xmlStreamWriter.writeStartElement(s, s1, s2);
        }
    }
}
