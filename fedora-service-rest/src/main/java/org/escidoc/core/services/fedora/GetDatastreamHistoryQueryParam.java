package org.escidoc.core.services.fedora;

import net.sf.oval.guard.Guarded;

import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Marko Voß
 * 
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
    assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetDatastreamHistoryQueryParam {

    private String format = MediaType.TEXT_XML;

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