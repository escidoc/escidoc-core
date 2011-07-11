package org.escidoc.core.services.fedora;


/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetDatastreamQueryParam {

    private String asOfDateTime;

    private String download;

    public String getAsOfDateTime() {
        return asOfDateTime;
    }

    public void setAsOfDateTime(final String asOfDateTime) {
        this.asOfDateTime = asOfDateTime;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(final String download) {
        this.download = download;
    }
}
