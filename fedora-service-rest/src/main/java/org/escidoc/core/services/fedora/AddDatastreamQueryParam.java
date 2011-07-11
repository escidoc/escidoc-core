package org.escidoc.core.services.fedora;

import org.esidoc.core.utils.io.MimeTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class AddDatastreamQueryParam {

    private ControlGroup controlGroup;
    private String dsLocation;
    private List<String> altIDs = new ArrayList<String>();
    private String dsLabel;
    private Boolean versionable;
    private DatastreamState dsState;
    private String formatURI;
    private ChecksumType checksumType;
    private String checksum;
    private String mimeType = MimeTypes.TEXT_XML;
    private String logMessage;

    public ControlGroup getControlGroup() {
        return controlGroup;
    }

    public void setControlGroup(final ControlGroup controlGroup) {
        this.controlGroup = controlGroup;
    }

    public String getDsLocation() {
        return dsLocation;
    }

    public void setDsLocation(final String dsLocation) {
        this.dsLocation = dsLocation;
    }

    public List<String> getAltIDs() {
        return altIDs;
    }

    public void setAltIDs(final List<String> altIDs) {
        if(altIDs == null) {
            this.altIDs = new ArrayList<String>();
        } else {
            this.altIDs = new ArrayList<String>(altIDs);
        }
    }

    public String getDsLabel() {
        return dsLabel;
    }

    public void setDsLabel(final String dsLabel) {
        this.dsLabel = dsLabel;
    }

    public Boolean getVersionable() {
        return versionable;
    }

    public void setVersionable(final Boolean versionable) {
        this.versionable = versionable;
    }

    public DatastreamState getDsState() {
        return dsState;
    }

    public void setDsState(final DatastreamState dsState) {
        this.dsState = dsState;
    }

    public String getFormatURI() {
        return formatURI;
    }

    public void setFormatURI(final String formatURI) {
        this.formatURI = formatURI;
    }

    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(final ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(final String checksum) {
        this.checksum = checksum;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }
}
