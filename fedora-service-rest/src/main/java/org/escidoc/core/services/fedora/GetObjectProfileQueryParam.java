package org.escidoc.core.services.fedora;

import org.esidoc.core.utils.io.MimeTypes;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetObjectProfileQueryParam {

    private String format = MimeTypes.TEXT_XML;

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }
}
