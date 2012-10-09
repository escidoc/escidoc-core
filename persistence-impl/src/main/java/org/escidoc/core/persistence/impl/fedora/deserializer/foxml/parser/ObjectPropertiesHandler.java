package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ObjectPropertiesHandler extends DefaultHandler {
    private static final String PROPERTY_PATH =
        "/digitalObject/objectProperties/property";

    private static final String CREATED_DATE_ATTRIBUTE =
        "info:fedora/fedora-system:def/model#createdDate";

    private static final String LABEL_ATTRIBUTE =
        "info:fedora/fedora-system:def/model#label";

    private static final String LAST_MODIFIED_DATE_ATTRIBUTE =
        "info:fedora/fedora-system:def/view#lastModifiedDate";

    private final StaxParser parser;

    private String creationDate = null;

    private String label = null;

    private String lastModificationDate = null;

    public ObjectPropertiesHandler(final StaxParser parser) {
        this.parser = parser;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLabel() {
        return label;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
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
            if (PROPERTY_PATH.equals(parser.getCurPath())) {
                try {
                    final String name =
                        element.getAttribute(null, "NAME").getValue();

                    if (name.equals(CREATED_DATE_ATTRIBUTE)) {
                        creationDate =
                            element.getAttribute(null, "VALUE").getValue();
                    }
                    else if (name.equals(LABEL_ATTRIBUTE)) {
                        label = element.getAttribute(null, "VALUE").getValue();
                    }
                    else if (name.equals(LAST_MODIFIED_DATE_ATTRIBUTE)) {
                        lastModificationDate =
                            element.getAttribute(null, "VALUE").getValue();
                    }
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
        }
        return element;
    }
}