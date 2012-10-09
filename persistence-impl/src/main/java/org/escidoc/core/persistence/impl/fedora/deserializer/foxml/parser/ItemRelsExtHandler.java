package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.util.stax.StaxParser;

public class ItemRelsExtHandler extends RelsExtHandler {
    private static final String RELS_EXT_ID = "RELS-EXT";

    public ItemRelsExtHandler(final StaxParser parser, final String date)
        throws XMLStreamException {
        super(parser, RELS_EXT_ID, date);
    }
}
