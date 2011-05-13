package org.escidoc.core.services.fedora;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class IngestQueryParam {

    private String label;
    private String format;
    private String encoding;
    private String namespace;
    private String ownerId;
    private String logMessage;
    private Boolean ignoreMime;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }

    public Boolean isIgnoreMime() {
        return ignoreMime;
    }

    public void setIgnoreMime(final Boolean ignoreMime) {
        this.ignoreMime = ignoreMime;
    }
}
