/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package org.escidoc.core.services.fedora;

import net.sf.oval.guard.Guarded;
import org.esidoc.core.utils.io.MimeTypes;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetDatastreamProfileQueryParam {

    private String asOfDateTime;
    private String format = MimeTypes.TEXT_XML;
    private String validateChecksum;

    public String getAsOfDateTime() {
        return asOfDateTime;
    }

    public void setAsOfDateTime(final String asOfDateTime) {
        this.asOfDateTime = asOfDateTime;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getValidateChecksum() {
        return validateChecksum;
    }

    public void setValidateChecksum(final String validateChecksum) {
        this.validateChecksum = validateChecksum;
    }
}
