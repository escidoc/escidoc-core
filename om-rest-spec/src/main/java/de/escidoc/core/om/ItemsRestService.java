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

package de.escidoc.core.om;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * 
 * @author SWA
 *
 */
@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ItemsRestService {

    /**
     * Retrieves a list of complete Items applying filters.<br/>
     * <p/>
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>Check weather all filter names are valid.</li>
     * <li>The Items are accessed using the provided filters.</li>
     * <li>The XML representation to be returned for all Item will not contain any binary content but references to
     * them.</li>
     * <li>The XML representation of the list of all Items the current user is allowed to see is returned as output
     * corresponding to the SRU/SRW schema.</li>
     * </ul>
     * <br/>
     * See chapter "Filters" for detailed information about filter definitions.
     * 
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @param userId
     *            The custom SRU Get Parameter x-info5-userId
     * @param roleId
     *            The custom SRU Get Parameter x-info5-roleId
     * @param omitHighlighting
     *            The custom SRU Get Parameter x-info5-omitHighlighting
     * @return The XML representation of the the filtered list of items corresponding to SRW schema as JAXBElement.
     * @throws SystemException
     *             If an error occurs.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveItems(
        @QueryParam("") SruSearchRequestParametersBean parameters, 
        @QueryParam("x-info5-roleId") String roleId,
        @QueryParam("x-info5-userId") String userId, 
        @QueryParam("x-info5-omitHighlighting") String omitHighlighting) throws SystemException;

}
