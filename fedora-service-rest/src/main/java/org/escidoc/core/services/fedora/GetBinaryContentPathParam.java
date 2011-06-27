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

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetBinaryContentPathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    @NotNull
    @NotEmpty
    private final String dsID;

    private final String versionDate;

    public GetBinaryContentPathParam(@AssertFieldConstraints final String pid,
                                     @AssertFieldConstraints final String dsID, final String versionDate) {
        this.pid = pid;
        this.dsID = dsID;
        if(versionDate != null) {
            this.versionDate = versionDate;
        } else {
            this.versionDate = "";
        }
    }

    public String getPid() {
        return pid;
    }

    public String getDsID() {
        return dsID;
    }

    public String getVersionDate() {
        return versionDate;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        final GetBinaryContentPathParam that = (GetBinaryContentPathParam) o;

        if(dsID != null ? ! dsID.equals(that.dsID) : that.dsID != null) {
            return false;
        }
        if(pid != null ? ! pid.equals(that.pid) : that.pid != null) {
            return false;
        }
        return ! (versionDate != null ? ! versionDate.equals(that.versionDate) : that.versionDate != null);

    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (dsID != null ? dsID.hashCode() : 0);
        result = 31 * result + (versionDate != null ? versionDate.hashCode() : 0);
        return result;
    }
}
