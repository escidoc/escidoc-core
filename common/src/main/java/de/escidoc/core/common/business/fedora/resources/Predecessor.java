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

    private String objid = null;

    private PredecessorForm form = null;

    /**
     * Predecessor.
     */
    public Predecessor() {
    }

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
     * @param objid
     *            the objid to set
     */
    public void setObjid(final String objid) {
        this.objid = objid;
    }

    /**
     * @return the objid
     */
    public String getObjid() {
        return objid;
    }

    /**
     * @param form
     *            the form of predecessor to set
     */
    public void setForm(final PredecessorForm form) {
        this.form = form;
    }

    /**
     * @return the predecessor form
     */
    public PredecessorForm getForm() {
        return form;
    }
}
