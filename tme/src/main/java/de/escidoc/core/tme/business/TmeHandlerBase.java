package de.escidoc.core.tme.business;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.tme.business.stax.handler.TmeRequestsStaxHandler;

public class TmeHandlerBase {

    protected TmeHandlerBase() {
    }

    protected final String[] parseRequests(final String requests)
        throws TmeException, XmlParserSystemException {

        StaxParser sp = new StaxParser();
        TmeRequestsStaxHandler requestsStaxHandler =
            new TmeRequestsStaxHandler();
        sp.addHandler(requestsStaxHandler);
        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(requests));
        }
        catch (TmeException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        return requestsStaxHandler.getFiles();
    }
}
