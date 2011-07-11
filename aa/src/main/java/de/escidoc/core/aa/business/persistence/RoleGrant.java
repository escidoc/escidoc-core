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

import java.util.Date;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * A grant.
 *
 * @author Torsten Tetteroo
 */
public class RoleGrant extends RoleGrantBase {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor.
     */
    public RoleGrant() {

    }

    /**
     * Gets the title of this role grant.
     *
     * @return Returns the title.
     */
    public String getTitle() {

        final String objectTitle = getObjectTitle();
        return objectTitle != null ? getEscidocRole().getRoleName() + " of " + objectTitle : getEscidocRole()
            .getRoleName();
    }

    /**
     * Gets the Href of this role grant.
     *
     * @return Returns the Href
     */
    public String getHref() {
        return this.getUserAccountByUserId() != null ? XmlUtility.getUserAccountGrantHref(this
            .getUserAccountByUserId().getId(), this.getId()) : XmlUtility.getUserGroupGrantHref(this
            .getUserGroupByGroupId().getId(), this.getId());
    }

    /**
     * Gets the creation date in ISO8601 format.
     *
     * @return Returns the creation date in ISO8601 format.
     */
    public String getIso8601CreationDate() {

        if (getCreationDate() == null) {
            return null;
        }
        return XmlUtility.normalizeDate(getCreationDate());
    }

    /**
     * Gets the revocation date in ISO8601 format.
     *
     * @return Returns the creation date in ISO8601 format.
     */
    public String getIso8601RevocationDate() {

        if (getRevocationDate() == null) {
            return null;
        }
        return XmlUtility.normalizeDate(getRevocationDate());
    }

    /**
     * Gets the date of last modification in ISO8601 format. <br> The maximum of creation date and revocation date is
     * returned for this.
     *
     * @return Returns the creation date in ISO8601 format.
     */
    public String getIso8601LastModificationDate() {

        final Date lastModificationDate = getLastModificationDate();
        if (lastModificationDate == null) {
            return null;
        }
        return XmlUtility.normalizeDate(lastModificationDate);
    }

    /**
     * Gets date of last modification. <br> The maximum of creation date and revocation date is returned for this.
     *
     * @return Returns the date of last modification.
     */
    public Date getLastModificationDate() {

        return getRevocationDate() != null ? getRevocationDate() : getCreationDate();
    }

}
