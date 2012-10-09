package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class ContentHandler extends XmlContentHandler {
    private static final String CONTENT_PATH = "/content";

    private static final String STORAGE = "storage";

    private String name = null;

    private String storage = null;

    public ContentHandler(final StaxParser parser, final String path)
        throws XMLStreamException {
        super(parser, path + CONTENT_PATH);
    }

    public String getName() {
        return name;
    }

    public String getStorage() {
        return storage;
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
    @Override
    public StartElement startElement(final StartElement element) {
        if (element != null) {
            try {
                if (path.equals(parser.getCurPath()) || (insideLevel > 0)) {
                    insideLevel++;
                }
                if (insideLevel > 0) {
                    if (insideLevel == 1) {
                        name = getTitle(element);
                        storage =
                            element.getAttribute(null, STORAGE).getValue();
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        return element;
    }
}