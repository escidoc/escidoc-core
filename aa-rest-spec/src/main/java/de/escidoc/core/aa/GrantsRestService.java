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
public interface GrantsRestService {

    /**
     * Retrieve Grants by providing filter-criteria.<br/>
     * <p/>
     * Retrieve the information about the Roles that currently have been granted to a User Account or User Group
     * including the information for which objects the limited roles are granted. Grants are selected depending on the
     * provided filter-criteria. Group Grants are selected hierarchically, that means if only a userId is provided as
     * filter-criterion, not only the Grants that are directly assigned to the User Account are returned but also the
     * Grants that are attached to the User Groups the User Account belongs to. Same with User Groups. Not only the
     * Grants that are directly assigned to the User Group are returned, but also the Grants that are attached to the
     * User Groups the User Group belongs to. If more than one userId is provided, the User Group Grants of all User
     * Accounts are returned. These User Group Grants do not have any information about the User Account they are
     * attached to! <br/> NOTE: URI-Like Filters are deprecated and will be removed in the next version of the
     * core-framework. Please use the new PATH-like filters (eg /id instead of http://purl.org/dc/elements/1.1/identifier).
     * For further information about the filter-names, please see the explain-plan.<br/> <b>Additional filters valid for
     * this method:</b><br/>
     * <p/>
     * <b>Prerequisites:</b><br/> At least one filter containing a value should be specified.<br/> If no filter is
     * specified, all grants are returned.<br/> <b>Tasks:</b><br/> <ul> <li>Check weather all filter names are
     * valid.</li> <li>The grants are accessed using the provided filters.</li> <li>The XML representation of the list
     * of all grants corresponding to XML-schema grants.xsd, element grant-list is returned as output.</li> </ul> <br/>
     * Valid filter-names for filtering grants are:<br/> <ul> <li>http://escidoc.de/core/01/properties/user and
     * /properties/user/id: filter for grants of specific users</li> <li>http://escidoc.de/core/01/properties/group and
     * /properties/group/id: filter for grants of specific groups</li> <li>http://escidoc.de/core/01/properties/role and
     * /properties/role/id: filter for grants of specific roles</li> <li>http://escidoc.de/core/01/properties/assigned-on
     * and /properties/assigned-on: filter for grants on specific objects (scopes)</li>
     * <li>http://escidoc.de/core/01/properties/created-by and /properties/created-by/id: filter for grants created by
     * specific users</li> <li>http://escidoc.de/core/01/properties/revoked-by and /properties/revoked-by/id: filter for
     * grants revoked by specific users</li> <li>http://escidoc.de/core/01/properties/revocation-date and
     * /properties/revocation-date: filter for grants revoked before/after a specific date</li>
     * <li>http://escidoc.de/core/01/properties/creation-date and /properties/creation-date: filter for grants created
     * before/after a specific date</li> </ul>
     * <p/>
     * It is not allowed to provide group-filter and user-filter at the same time. All filters except user and group may
     * have empty-value, this delivers all grants that have the field specified by the filter null<br/> eg providing a
     * filter with revoked-by=empty only returns grants that are not revoked<br/>
     * <p/>
     * See chapter "Filters" for detailed information about filter definitions.
     *
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @return The XML representation of the grants matching the provided filter-criteria corresponding to XML-schema
     *         "grants.xsd", element grant-list as JAXBElement.
     * @throws MissingMethodParameterException
     *                                     Thrown if no user id is provided.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws SystemException             Thrown in case of an internal system error.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveGrants(
        @QueryParam("") SruSearchRequestParametersBean parameters) 
        throws MissingMethodParameterException, InvalidSearchQueryException, AuthenticationException,
        AuthorizationException, SystemException;

}
