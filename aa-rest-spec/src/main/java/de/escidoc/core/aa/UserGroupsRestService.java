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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
/**
 * 
 */
package de.escidoc.core.aa;

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
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface UserGroupsRestService {

    /**
     * Retrieve a filtered list of User Groups applying filters.<br/>
     * <p/>
     * Default (and for now, the only) format is a list of full User Group XML representations.<br/> Access rights are
     * checked.<br/> <br/> NOTE: URI-Like Filters are deprecated and will be removed in the next version of the
     * core-framework. Please use the new PATH-like filters (eg /id instead of http://purl.org/dc/elements/1.1/identifier).
     * For further information about the filter-names, please see the explain-plan.<br/>
     * <p/>
     * <ul> <li>Check weather all filter names are valid.</li> <li>The User Groups are accessed using the provided
     * filters.</li> <li>The XML representation of the list of User Groups corresponding to XML schema is returned as
     * output.</li> </ul> <br/> See chapter "Filters" for detailed information about filter definitions.<br/>
     * <b>Additional filters valid for this method:</b><br/> <ul> <li>active<br/> retrieves all User Groups that are
     * activated (value=true) or deactivated (value=false).</li> <li>http://escidoc.de/core/01/properties/user with
     * value "id"<br/> retrieves all User Groups the User belongs to (hierarchically).</li> </ul> <br/>
     *
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @return The XML representation of the the filtered list of user-groups corresponding to SRW schema as JAXBElement.
     * @throws MissingMethodParameterException
     *                                     Thrown if no XML representation of filter parameters is provided.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                     Handle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             Thrown in case of an internal system error.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveUserGroups(
        @QueryParam("") SruSearchRequestParametersBean parameters) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

}
