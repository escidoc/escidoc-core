package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.esidoc.core.utils.xml.DatastreamHolder;

import javax.validation.constraints.NotNull;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * {@link XMLStreamWriter} that filters content elements in {@link org.esidoc.core.utils.xml.DatastreamHolder} objects.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class FilteringXMLStreamWriter implements XMLStreamWriter {

    private final XMLStreamWriter xmlStreamWriter;
    private boolean ignore = false;

    public FilteringXMLStreamWriter(@NotNull final XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    @Override
    public void close() throws XMLStreamException {
        xmlStreamWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return xmlStreamWriter.getNamespaceContext();
    }

    @Override
    public String getPrefix(final String s) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(s);
    }

    @Override
    public Object getProperty(final String s) {
        return xmlStreamWriter.getProperty(s);
    }

    @Override
    public void setDefaultNamespace(final String s) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(s);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(namespaceContext);
    }

    @Override
    public void setPrefix(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.setPrefix(s, s1);
    }

    @Override
    public void writeAttribute(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1);
    }

    @Override
    public void writeAttribute(final String s, final String s1, final String s2) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2);
    }

    @Override
    public void writeAttribute(final String s, final String s1, final String s2, final String s3) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2, s3);
    }

    @Override
    public void writeCData(final String s) throws XMLStreamException {
        xmlStreamWriter.writeCData(s);
    }

    @Override
    public void writeCharacters(final String s) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(s);
    }

    @Override
    public void writeCharacters(final char[] chars, final int i, final int i1) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(chars, i, i1);
    }

    @Override
    public void writeComment(final String s) throws XMLStreamException {
        xmlStreamWriter.writeComment(s);
    }

    @Override
    public void writeDTD(final String s) throws XMLStreamException {
        xmlStreamWriter.writeDTD(s);
    }

    @Override
    public void writeDefaultNamespace(final String s) throws XMLStreamException {
        xmlStreamWriter.writeDefaultNamespace(s);
    }

    @Override
    public void writeEmptyElement(final String s) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s);
    }

    @Override
    public void writeEmptyElement(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s, s1);
    }

    @Override
    public void writeEmptyElement(final String s, final String s1, final String s2) throws XMLStreamException {
        if(! (DatastreamHolder.NAMESPACE.equals(s2) && DatastreamHolder.ELEMENT_NAME.equals(s1))) {
            xmlStreamWriter.writeEmptyElement(s, s1, s2);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        xmlStreamWriter.writeEndDocument();
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if(this.ignore) {
            this.ignore = false;
        } else {
            xmlStreamWriter.writeEndElement();
        }
    }

    @Override
    public void writeEntityRef(final String s) throws XMLStreamException {
        xmlStreamWriter.writeEntityRef(s);
    }

    @Override
    public void writeNamespace(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeNamespace(s, s1);
    }

    @Override
    public void writeProcessingInstruction(final String s) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(s);
    }

    @Override
    public void writeProcessingInstruction(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(s, s1);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        xmlStreamWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String s) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(s);
    }

    @Override
    public void writeStartDocument(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(s, s1);
    }

    @Override
    public void writeStartElement(final String s) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s);
    }

    @Override
    public void writeStartElement(final String s, final String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s, s1);
    }

    @Override
    public void writeStartElement(final String s, final String s1, final String s2) throws XMLStreamException {
        if(DatastreamHolder.NAMESPACE.equals(s2) && DatastreamHolder.ELEMENT_NAME.equals(s1)) {
            this.ignore = true;
        } else {
            xmlStreamWriter.writeStartElement(s, s1, s2);
        }
    }
}
