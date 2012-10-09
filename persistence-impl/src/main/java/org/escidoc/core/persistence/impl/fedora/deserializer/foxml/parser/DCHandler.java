package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class DCHandler extends VersionedDatastreamHandler {
    private static final String DC_ID = "DC";

    private static final String DC_DESCRIPTION_PATH = XML_CONTENT_PATH
        + "/dc/description";

    private static final String DC_TITLE_PATH = XML_CONTENT_PATH + "/dc/title";

    private boolean insideDcDescription = false;

    private boolean insideDcTitle = false;

    private StringBuffer dcDescription = new StringBuffer();

    private StringBuffer dcTitle = new StringBuffer();

    public DCHandler(final StaxParser parser, final String date)
        throws XMLStreamException {
        super(parser, DC_ID, date);
    }

    @Override
    public String characters(final String s, StartElement element)
        throws IntegritySystemException, XmlParserSystemException {
        if (insideDcDescription) {
            dcDescription.append(s);
        }
        else if (insideDcTitle) {
            dcTitle.append(s);
        }
        return s;
    }

    @Override
    public EndElement endElement(EndElement element) {
        super.endElement(element);
        insideDcDescription = false;
        insideDcTitle = false;
        return element;
    }

    public String getDcDescription() {
        if (dcDescription.length() > 0) {
            return dcDescription.toString();
        }
        else {
            return null;
        }
    }

    public String getDcTitle() {
        if (dcTitle.length() > 0) {
            return dcTitle.toString();
        }
        else {
            return null;
        }
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
            if (insideLevel > 0) {
                insideLevel++;
            }
            if (DATASTREAM_PATH.equals(parser.getCurPath())) {
                try {
                    datastreamName =
                        element.getAttribute(null, "ID").getValue();
                }
                catch (NoSuchAttributeException e) {
                    System.out.println(e);
                }
            }
            else if (DC_ID.equals(datastreamName)) {
                if (DATASTREAM_VERSION_PATH.equals(parser.getCurPath())) {
                    try {
                        final String created =
                            element.getAttribute(null, "CREATED").getValue();

                        if (((datastreamDate == null) || ((created
                            .compareTo(datastreamDate) > 0)))
                            && ((date == null) || created.compareTo(date) <= 0)) {
                            datastreamDate = created;
                            insideLevel++;
                        }
                    }
                    catch (NoSuchAttributeException e) {
                        System.out.println(e);
                    }
                }
                else if (DC_DESCRIPTION_PATH.equals(parser.getCurPath())) {
                    if (insideLevel > 0) {
                        insideDcDescription = true;
                        dcDescription.setLength(0);
                    }
                }
                else if (DC_TITLE_PATH.equals(parser.getCurPath())) {
                    if (insideLevel > 0) {
                        insideDcTitle = true;
                        dcTitle.setLength(0);
                    }
                }
            }
        }
        return element;
    }
}