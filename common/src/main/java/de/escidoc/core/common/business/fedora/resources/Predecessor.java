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
package de.escidoc.core.common.business.fedora.resources;

/**
 * Predecessor of Organizational Unit (OU).
 * 
 * The development of an OU could be reflected within the predecessor relation.
 * An OU links to it predecessors.
 * 
 * @author SWA
 * 
 */
public class Predecessor {

    private final String objid;

    private final PredecessorForm form;

    /**
     * Predecessor.
     * 
     * @param objid
     *            Objid of predecessor OU.
     * @param form
     *            Form/type of predecessor.
     */
    public Predecessor(final String objid, final PredecessorForm form) {

        this.objid = objid;
        this.form = form;
    }

    /**
     * @return the objid
     */
    public String getObjid() {
        return objid;
    }

    /**
     * @return the predecessor form
     */
    public PredecessorForm getForm() {
        return form;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Predecessor that = (Predecessor) o;

        if (form != that.form) {
            return false;
        }
        return !(objid != null ? !objid.equals(that.objid) : that.objid != null);

    }

    @Override
    public int hashCode() {
        int result = objid != null ? objid.hashCode() : 0;
        result = 31 * result + (form != null ? form.hashCode() : 0);
        return result;
    }
}
