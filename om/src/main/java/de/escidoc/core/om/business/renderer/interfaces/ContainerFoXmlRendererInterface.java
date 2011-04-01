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

package de.escidoc.core.om.business.renderer.interfaces;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.List;
import java.util.Map;

/**
 * Interface of an container foxml renderer.
 *
 * @author Rozita Friedman
 */
public interface ContainerFoXmlRendererInterface {

    /**
     * Gets the foxml representation of a container.
     *
     * @param values The values of the container.
     * @param properties
     * @param members
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @return Returns the foxml representation of the container.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(
        Map<String, Object> values, final Map<String, String> properties, final List<String> members,
        final String containerId, final String lastModificationDate, final List<Map<String, String>> contentRelations,
        final String comment, final Map<String, String> propertiesAsReferences) throws SystemException;

    /**
     * Render RELS-EXT.
     *
     * @param properties
     * @param members
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @return XML representation of RELS-EXT.
     * @throws WebserverSystemException Thrown in case of internal failure.
     */
    String renderRelsExt(
        final Map<String, String> properties, final List<String> members, final String containerId,
        final String lastModificationDate, final List<Map<String, String>> contentRelations, final String comment,
        final Map<String, String> propertiesAsReferences) throws WebserverSystemException;

    /**
     * Render WOV to XML.
     *
     * @param id                   Objid of Container.
     * @param title                Title of Container.
     * @param versionNo            Number of Container version.
     * @param lastModificationDate Last Modification Date of Container.
     * @param versionStatus        Status of Version.
     * @param comment              Comment.
     * @return XML representation of WOV.
     * @throws WebserverSystemException Thrown in case of internal failure.
     */
    String renderWov(
        final String id, final String title, final String versionNo, final String lastModificationDate,
        final String versionStatus, final String comment) throws WebserverSystemException;
}
