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

package org.escidoc.core.st;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.escidoc.core.domain.st.StagingFileTO;
import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * 
 * @author SWA
 * 
 */
@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface StagingRestService {

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
    @PUT
    StagingFileTO create(final EscidocBinaryContent binaryContent) throws MissingMethodParameterException,
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
    @GET
    @Path("/{id}")
    Response retrieve(@PathParam("id") String stagingFileId) throws StagingFileNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException;

}
