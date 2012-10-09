package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public abstract class AbstractHandler extends DefaultHandler {
    protected final StaxParser parser;

    protected final String path;

    public AbstractHandler(final StaxParser parser, final String path) {
        this.parser = parser;
        this.path = path;
    }

    protected String getObjId(final StartElement xlink)
        throws NoSuchAttributeException {
        String href =
            xlink.getAttribute(Constants.XLINK_NS_URI, "href").getValue();

        return href.substring(href.lastIndexOf('/') + 1);
    }

    protected String getTitle(final StartElement xlink)
        throws NoSuchAttributeException {
        return xlink.getAttribute(Constants.XLINK_NS_URI, "title").getValue();
    }
}