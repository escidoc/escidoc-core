package de.escidoc.core.test.tme.jhove;

import de.escidoc.core.test.tme.TmeTestBase;

public class JhoveTestBase extends TmeTestBase {

    public String extract(final String requests) throws Exception {

        return handleXmlResult(getJhoveClient().identify(requests));
    }
}
