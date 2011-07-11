package org.escidoc.core.services.fedora;

import org.esidoc.core.utils.io.MimeTypes;

import static org.esidoc.core.utils.Preconditions.checkState;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class NextPIDQueryParam {

    public static final int DEFAULT_NUMBER_OF_PIDS = 1;

    private String namespace;
    private int numPIDs = DEFAULT_NUMBER_OF_PIDS;
    private String format = MimeTypes.TEXT_XML;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public int getNumPIDs() {
        return numPIDs;
    }

    public void setNumPIDs(final int numPIDs) {
        checkState(numPIDs > 0, "Number of PIDs must be positive.");
        this.numPIDs = numPIDs;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }
}
