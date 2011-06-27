package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.esidoc.core.utils.xml.DatastreamHolder;

import javax.validation.constraints.NotNull;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

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
    public String getPrefix(String s) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(s);
    }

    @Override
    public Object getProperty(String s) throws IllegalArgumentException {
        return xmlStreamWriter.getProperty(s);
    }

    @Override
    public void setDefaultNamespace(String s) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(s);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(namespaceContext);
    }

    @Override
    public void setPrefix(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.setPrefix(s, s1);
    }

    @Override
    public void writeAttribute(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2, String s3) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(s, s1, s2, s3);
    }

    @Override
    public void writeCData(String s) throws XMLStreamException {
        xmlStreamWriter.writeCData(s);
    }

    @Override
    public void writeCharacters(String s) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(s);
    }

    @Override
    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(chars, i, i1);
    }

    @Override
    public void writeComment(String s) throws XMLStreamException {
        xmlStreamWriter.writeComment(s);
    }

    @Override
    public void writeDTD(String s) throws XMLStreamException {
        xmlStreamWriter.writeDTD(s);
    }

    @Override
    public void writeDefaultNamespace(String s) throws XMLStreamException {
        xmlStreamWriter.writeDefaultNamespace(s);
    }

    @Override
    public void writeEmptyElement(String s) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s);
    }

    @Override
    public void writeEmptyElement(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(s, s1);
    }

    @Override
    public void writeEmptyElement(String s, String s1, String s2) throws XMLStreamException {
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
    public void writeEntityRef(String s) throws XMLStreamException {
        xmlStreamWriter.writeEntityRef(s);
    }

    @Override
    public void writeNamespace(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeNamespace(s, s1);
    }

    @Override
    public void writeProcessingInstruction(String s) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(s);
    }

    @Override
    public void writeProcessingInstruction(String s, String s1) throws XMLStreamException {
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
    public void writeStartDocument(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(s, s1);
    }

    @Override
    public void writeStartElement(String s) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s);
    }

    @Override
    public void writeStartElement(String s, String s1) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(s, s1);
    }

    @Override
    public void writeStartElement(String s, String s1, String s2) throws XMLStreamException {
        if(DatastreamHolder.NAMESPACE.equals(s2) && DatastreamHolder.ELEMENT_NAME.equals(s1)) {
            this.ignore = true;
        } else {
            xmlStreamWriter.writeStartElement(s, s1, s2);
        }
    }
}
