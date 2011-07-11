package de.escidoc.core.test.tme;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.tme.JhoveClient;

public class TmeTestBase extends EscidocAbstractTest {

    private final JhoveClient jhoveClient;

    public TmeTestBase() {
        jhoveClient = new JhoveClient();
    }

    /**
     * @return the jhoveClient
     */
    public JhoveClient getJhoveClient() {
        return jhoveClient;
    }
}
