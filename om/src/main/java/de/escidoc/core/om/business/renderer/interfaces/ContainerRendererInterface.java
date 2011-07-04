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

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Interface of an container renderer.
 *
 * @author Rozita Friedman
 */
public interface ContainerRendererInterface {

    /**
     * Gets the representation of an organizational unit.
     *
     * @param container The Container.
     * @return Returns the XML representation of the organizational unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final Container container) throws SystemException;

    /**
     * Gets the representation of the sub resource {@code properties} of an organizational unit.
     *
     * @param container The Container to render.
     * @return Returns the XML representation of the sub resource {@code properties} of an organizational unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderProperties(final Container container) throws SystemException;

    /**
     * Gets the representation of the sub resource {@code resources} of an organizational unit.
     *
     * @param container The Container to render.
     * @return Returns the XML representation of the sub resource {@code resources} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderResources(final Container container) throws WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code organization-details} of an organizational unit.
     *
     * @param container The Container to render.
     * @return Returns the XML representation of the sub resource {@code data} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    String renderMetadataRecords(final Container container) throws EncodingSystemException, FedoraSystemException,
        WebserverSystemException, IntegritySystemException;

    /**
     * Gets the representation of the sub resource {@code organization-details} of an organizational unit.
     *
     * @param container The Container to render.
     * @param mdRecord
     * @param isRootMdRecord
     * @return Returns the XML representation of the sub resource {@code data} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    String renderMetadataRecord(final Container container, final Datastream mdRecord, final boolean isRootMdRecord)
        throws EncodingSystemException, FedoraSystemException, WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code ou-parents} of an organizational unit.
     *
     * @param container The Container to render.
     * @return Returns the XML representation of the sub resource {@code ou-parents} of an organizational unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderRelations(final Container container) throws SystemException;

    /**
     * Gets the representation of the virtual resource {@code parents} of an container.
     *
     * @param containerId The Container ID to render.
     * @return Returns the XML representation of the virtual resource {@code parents} of an Container.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderParents(final String containerId) throws SystemException;

    /**
     * Gets the representation of the virtual sub resource {@code struct-map} of an Container.
     *
     * @param container The Container handler.
     * @return Returns the XML representation of the virtual sub resource {@code struct-map} of an Container.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderStructMap(final Container container) throws SystemException;
}
