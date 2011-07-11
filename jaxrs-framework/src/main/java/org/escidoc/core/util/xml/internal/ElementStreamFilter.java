package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link StreamFilter} that filters specific elements in a XML file.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class ElementStreamFilter implements StreamFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ElementStreamFilter.class);

    private final XMLStreamReader filteredXmlStreamReader;
    private PrintStream output;
    private boolean active = false;
    private final List<QName> ignoredElements = new ArrayList<QName>();

    public ElementStreamFilter(@NotNull final InputStream inputStream) {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            this.filteredXmlStreamReader = xmlInputFactory.createFilteredReader(xmlStreamReader, this);
        } catch(XMLStreamException e) {
            final String errorMessage = "Error on creating XML reader.";
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessage, e);
            }
            throw new IllegalStateException(errorMessage, e);
        }
    }

    public ElementStreamFilter(@NotNull final XMLStreamReader reader) {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            this.filteredXmlStreamReader = xmlInputFactory.createFilteredReader(reader, this);
        } catch(XMLStreamException e) {
            final String errorMessage = "Error on creating XML reader.";
            if(LOG.isDebugEnabled()) {
                LOG.debug(errorMessage, e);
            }
            throw new IllegalStateException(errorMessage, e);
        }
    }

    public XMLStreamReader getFilteredXmlStreamReader() {
        return filteredXmlStreamReader;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setOutputStream(@NotNull final OutputStream outputStream) {
        this.output = new PrintStream(outputStream);
    }

    public void addIgnoredElemenet(@NotNull final QName elementName) {
        this.ignoredElements.add(elementName);
    }

    public void removeIgnoredElemenet(@NotNull final QName elementName) {
        this.ignoredElements.remove(elementName);
    }


    @Override
    public boolean accept(@NotNull final XMLStreamReader reader) {
        if(this.active) {
            switch(reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    onStartElement(reader);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    onEndElement(reader);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    onElementText(reader);
                    break;
                // TODO: implement other events!
                default:
                    if(LOG.isWarnEnabled()) {
                        LOG.warn("Implement handling for XML event " + reader.getEventType() + '.');
                    }
            }
        }
        return true;
    }

    private void onStartElement(@NotNull final XMLStreamReader reader) {
        final QName elementName = reader.getName();
        this.output.print("<");
        if(elementName.getPrefix() != null && elementName.getPrefix().trim().length() > 0) {
            this.output.print(elementName.getPrefix());
            this.output.print(":");
        }
        this.output.print(elementName.getLocalPart());
        printNamespaces(reader);
        printAttributes(reader);
        this.output.print(">");
    }

    private void onElementText(final XMLStreamReader reader) {
        final String text = reader.getText();
        if(text != null && text.trim().length() > 0) {
            this.output.print(text);
        }
    }

    private void printNamespaces(final XMLStreamReader reader) {
        int count = reader.getNamespaceCount();
        while(count > 0) {
            final String prefix = reader.getNamespacePrefix(count - 1);
            this.output.print(" ");
            if(prefix != null && prefix.trim().length() > 0) {
                this.output.print("xmlns:");
                this.output.print(prefix);
                this.output.print("=\"");
            }
            this.output.print(reader.getNamespaceURI(count - 1));
            this.output.print("\"");
            count--;
        }
    }

    private void printAttributes(final XMLStreamReader reader) {
        int count = reader.getAttributeCount();
        while(count > 0) {
            final String prefix = reader.getAttributePrefix(count - 1);
            this.output.print(" ");
            if(prefix != null && prefix.trim().length() > 0) {
                this.output.print(prefix);
                this.output.print(":");
            }
            this.output.print(reader.getAttributeLocalName(count - 1));
            this.output.print("=\"");
            this.output.print(reader.getAttributeValue(count - 1));
            this.output.print("\"");
            count--;
        }
    }

    private boolean isIgnoredElement(final QName elementName) {
        boolean returnValue = false;
        for(final QName myIgnoredElementName : this.ignoredElements) {
            if(myIgnoredElementName.equals(elementName)) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }

    private void onEndElement(final XMLStreamReader reader) {
        if(! isIgnoredElement(reader.getName())) {
            this.printEndElement(reader.getName());
        }
    }

    private void printEndElement(final QName elementName) {
        this.output.print("</");
        if(elementName.getPrefix() != null && elementName.getPrefix().trim().length() > 0) {
            this.output.print(elementName.getPrefix());
            this.output.print(":");
        }
        this.output.print(elementName.getLocalPart());
        this.output.print(">");
    }

}
