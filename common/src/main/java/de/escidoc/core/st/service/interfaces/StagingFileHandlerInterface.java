/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.st.service.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service interface of an StagingFile handler.
 *
 * @author Torsten Tetteroo
 */
public interface StagingFileHandlerInterface {

    /**
     * Create a StagingFile.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The binary content is stored in the staging area.</li> <li>Create the XML
     * representation of the staging file corresponding to XML-schema "stagingfile.xsd". This contains an link to the
     * file stored in the staging area.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param binaryContent The binary content that shall be uploaded to the staging area.
     * @return The XML representation of the staging file corresponding to XML-schema "stagingfile.xsd".
     * @throws MissingMethodParameterException
     *                                 TODO
     * @throws AuthenticationException TODO
     * @throws AuthorizationException  TODO
     * @throws SystemException         TODO
     */
    String create(final EscidocBinaryContent binaryContent) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve a StagingFile<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The StagingFile must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The StagingFile is accessed using the provided reference.</li> <li>The file with
     * provided id is returned.</li> </ul>
     *
     * @param stagingFileId The id of the staging file to be retrieved.
     * @return The binary content of the staging file, filename and content type.
     * @throws StagingFileNotFoundException Thrown if the StagingFGile cannot be found.
     * @throws AuthenticationException      TODO
     * @throws AuthorizationException       TODO
     * @throws MissingMethodParameterException
     *                                      TODO
     * @throws SystemException              TODO
     */
    EscidocBinaryContent retrieve(final String stagingFileId) throws StagingFileNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException;

}
