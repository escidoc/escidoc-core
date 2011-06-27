package org.escidoc.core.services.fedora;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class UpdateObjectQueryParam {

    private String label;
    private String logMessage;
    private String ownerId;
    private String state;
    private String lastModifiedDate;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
