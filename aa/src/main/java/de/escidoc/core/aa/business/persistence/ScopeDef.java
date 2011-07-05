/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.persistence;

import net.sf.oval.guard.Guarded;

import javax.validation.constraints.NotNull;

/**
 * Class encapsulating the information stored about the scope definition of an {@link EscidocRole}.
 *
 * @author Torsten Tetteroo
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class ScopeDef extends ScopeDefBase implements Comparable<ScopeDef> {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @see ScopeDefBase()
     */
    public ScopeDef() {
    }

    /**
     * @param objectType
     * @param attributeId
     * @param attributeObjectType
     * @param escidocRole
     * @see ScopeDefBase(String, String, EscidocRole)
     */
    public ScopeDef(final String objectType, final String attributeId, final String attributeObjectType,
        final EscidocRole escidocRole) {
        super(objectType, attributeId, attributeObjectType, escidocRole);
    }

    /**
     * @param objectType
     * @see ScopeDefBase(String)
     */
    public ScopeDef(final String objectType) {
        super(objectType);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public int compareTo(@NotNull
    final ScopeDef o) {
        final String ownId = getId();
        final String oId = o.getId();
        if (ownId == null) {
            return oId == null ? 0 : -1;
        }
        else {
            if (oId == null) {
                return 1;
            }
            else {
                return ownId.length() == oId.length() ? ownId.compareTo(oId) : ownId.length() > oId.length() ? 1 : -1;
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(final Object obj) {

        boolean ret = false;
        if (obj instanceof ScopeDef) {
            ret = compareTo((ScopeDef) obj) == 0;
        }
        return ret;
    }

    /**
     * See Interface for functional description.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {

        return getId().hashCode();
    }

}
