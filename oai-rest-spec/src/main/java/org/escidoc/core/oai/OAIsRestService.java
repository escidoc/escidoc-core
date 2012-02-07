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

package org.escidoc.core.oai;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
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
public interface OAIsRestService {

     /**
     * Retrieves a list of completes set-definitions applying filters.<br/> <br/> NOTE: URI-Like Filters are
     deprecated
     * and will be removed in the next version of the core-framework. Please use the new PATH-like filters (eg /id
     * instead of http://purl.org/dc/elements/1.1/identifier). For further information about the filter-names, please
     * see the explain-plan.<br/> <b>Tasks:</b><br/> <ul> <li>Check weather all filter names are valid.</li> <li>The
     * set-definitions are accessed using the provided filters.</li> <li>The XML representation of the list of all
     * set-definitions corresponding to SRW schema is returned as output.</li> </ul> <br/> See chapter "Filters" for
     * detailed information about filter definitions.
     *
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @return Returns the XML representation of found set-definitions as JAXBElement.
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided
     * eSciDocUserHandle.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     * If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException If an error occurs.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveSetDefinitions(
        @QueryParam("") SruSearchRequestParametersBean parameters) throws AuthenticationException,
            AuthorizationException, MissingMethodParameterException, InvalidSearchQueryException, SystemException;

}
