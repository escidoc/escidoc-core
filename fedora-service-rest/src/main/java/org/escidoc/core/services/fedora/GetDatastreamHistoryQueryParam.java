package org.escidoc.core.services.fedora;

import net.sf.oval.guard.Guarded;

import org.esidoc.core.utils.io.MimeTypes;

/**
 * 
 * @author Marko Voß
 * 
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
    assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetDatastreamHistoryQueryParam {

    private String format = MimeTypes.TEXT_XML;

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public void setFormat(final String format) {
        this.format = format;
    }
}