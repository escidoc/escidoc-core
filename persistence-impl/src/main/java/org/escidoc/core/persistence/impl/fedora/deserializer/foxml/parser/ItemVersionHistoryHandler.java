package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;

import javax.naming.directory.NoSuchAttributeException;

import org.escidoc.core.persistence.impl.fedora.resource.Version;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

public class ItemVersionHistoryHandler extends VersionHistoryHandler {
    private static final String VERSION_PATH =
        "/digitalObject/datastream/datastreamVersion/xmlContent/version-history/version";

    private static final String VERSION_COMMENT_PATH =
        VERSION_PATH + "/comment";

    private static final String VERSION_DATE_PATH = VERSION_PATH + "/timestamp";

    private static final String VERSION_NUMBER_PATH =
        VERSION_PATH + "/version-number";

    private static final String VERSION_PID_PATH = VERSION_PATH + "/pid";

    private static final String VERSION_STATUS_PATH =
        VERSION_PATH + "/version-status";

    private Version version = null;

    public ItemVersionHistoryHandler(StaxParser parser) {
        super(parser);
    }

    @Override
    public String characters(final String s, StartElement element) {
        if (element != null) {
            if (VERSION_COMMENT_PATH.equals(parser.getCurPath())) {
                version.setComment(s);
            }
            else if (VERSION_DATE_PATH.equals(parser.getCurPath())) {
                version.setDate(s);
            }
            else if (VERSION_NUMBER_PATH.equals(parser.getCurPath())) {
                version.setNumber(s);
            }
            else if (VERSION_PID_PATH.equals(parser.getCurPath())) {
                version.setPid(s);
            }
            else if (VERSION_STATUS_PATH.equals(parser.getCurPath())) {
                version.setStatus(s);
            }
        }
        return s;
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
            if (VERSION_PATH.equals(parser.getCurPath())) {
                try {
                    version = new Version();
                    version.setTimestamp(element
                        .getAttribute(null, "timestamp").getValue());
                    versionHistory.put(element
                        .getAttribute(null, "objid").getValue(), version);
                }
                catch (NoSuchAttributeException e) {
                    e.printStackTrace();
                }
            }
        }
        return element;
    }
}
