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

package de.escidoc.core.oum;

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
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface OrganizationalUnitsRestService {

    /**
     * Retrieve a list of complete Organizational Units applying filters. <br /> <br /> <b>Tasks:</b> <ul> <li>Check
     * whether all filter names are valid.</li> <li>All Organizational Units matching the given filter criteria are
     * retrieved.</li> <li>The XML representation of the list of Organizational Units corresponding to SRW schema is
     * returned as output.</li> </ul> See chapter "Filters" for detailed information about filter definitions.
     * <p/>
     * Special filters for this method are: <ul> <li><br /> top-level-organizational-units<br />
     * <p/>
     * If this filter is defined only Organizational Unit objects that have no associated parent are returned.</li>
     * </ul>
     *
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @return The XML representation of the created list of Organizational Units corresponding to the SRW schema as JAXBElement.
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws InvalidXmlException         Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                     Thrown if the XML data is not provided.
     * @throws SystemException             Thrown in case of an internal error.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveOrganizationalUnits(
        @QueryParam("") SruSearchRequestParametersBean parameters, 
        @QueryParam("x-info5-roleId") String roleId,
        @QueryParam("x-info5-userId") String userId, 
        @QueryParam("x-info5-omitHighlighting") String omitHighlighting) throws InvalidSearchQueryException,
            InvalidXmlException, MissingMethodParameterException, SystemException;

}
