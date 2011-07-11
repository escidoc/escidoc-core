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
package de.escidoc.core.aa.business.renderer.interfaces;

import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.List;

/**
 * Interface of a role renderer.
 *
 * @author Torsten Tetteroo
 */
public interface RoleRendererInterface {

    String ROLE_URL_BASE = "/aa/role/";

    String RESOURCES_URL_PART = "/resources";

    /**
     * Gets the representation of a role.
     *
     * @param role The role to render.
     * @return Returns the XML representation of the role.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String render(final EscidocRole role) throws WebserverSystemException;

    /**
     * Gets the representation of the "resources" sub resource of a role.
     *
     * @param role The role to render.
     * @return Returns the XML representation of the "resources" sub resource of a role.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderResources(final EscidocRole role) throws WebserverSystemException;

    /**
     * Gets the representation of the list of roles.
     *
     * @param roles         The roles to render.
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the list of roles.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderRoles(final List<EscidocRole> roles, final RecordPacking recordPacking)
        throws WebserverSystemException;

}
