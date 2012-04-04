package org.escidoc.core.services.fedora;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetObjectProfileQueryParam {

    private String format = MediaType.TEXT_XML;

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }
}
