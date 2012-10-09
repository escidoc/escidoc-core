package org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser;


import org.escidoc.core.persistence.impl.fedora.resource.VersionHistory;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public abstract class VersionHistoryHandler extends DefaultHandler {
    protected final StaxParser parser;

    protected final VersionHistory versionHistory = new VersionHistory();

    public VersionHistoryHandler(final StaxParser parser) {
        this.parser = parser;
    }

    public VersionHistory getVersionHistory() {
        return versionHistory;
    }
}