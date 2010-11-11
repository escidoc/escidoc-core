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
package de.escidoc.core.sm.service.interfaces;

import java.util.Map;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of an Statistic Scope Handler.
 * 
 * @author MIH
 * 
 */
public interface ScopeHandlerInterface {

    /**
     * Create a new Scope.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * The provided XML data in the body is only accepted if the size is less
     * than ESCIDOC_MAX_XML_SIZE.<br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The Scope is created.</li>
     * <li>The XML data is returned.</li>
     * </ul>
     * 
     * @param xmlData
     *            The XML representation of the Scope to be created
     *            corresponding to XML-schema "scope.xsd".
     * @return The XML representation of the created Scope corresponding to
     *         XML-schema "scope.xsd".
     * 
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException
     *             ex
     * @throws XmlCorruptedException
     *             ex
     * @throws MissingMethodParameterException
     *             ex
     * @throws SystemException
     *             ex
     * 
     */
    @Validate(param = 0, resolver = "getScopeSchemaLocation")
    String create(String xmlData) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, SystemException;

    /**
     * Delete the Scope with the given id.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * The Scope must exist<br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The Scope is accessed using the provided reference.</li>
     * <li>The Scope is deleted.</li>
     * <li>No data is returned.</li>
     * </ul>
     * 
     * @param scopeId
     *            The Scope ID to be deleted.
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     */
    void delete(String scopeId) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieve the Scope with the given id.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * The Scope must exist<br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The Scope is accessed using the provided reference.</li>
     * <li>The XML data is returned.</li>
     * </ul>
     * 
     * @param scopeId
     *            The Scope ID to be retrieved.
     * @return The XML representation of the retrieved scope corresponding to
     *         XML-schema "scope.xsd".
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * 
     */
    String retrieve(String scopeId) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieve all Scopes the user is allowed to see.<br/>
     * 
     * Returns list of all Scopes the user may see.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>All Scopes are accessed.</li>
     * <li>The XML data is returned.</li>
     * </ul>
     * 
     * @param filterXml
     *            filterXml
     * @return The XML representation of the Scopes corresponding to XML-schema
     *         "scope-list.xsd". The list only contains these scopes the user is
     *         allowed to see.
     * @throws MissingMethodParameterException
     *             If the parameter filter is not given.
     * @throws XmlCorruptedException
     *             If the given xml is not valid.
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws SystemException
     *             e.
     *
     * @deprecated replaced by {@link #retrieveScopes(java.util.Map)}
     * @escidoc_core.visible false
     */
    @Deprecated String retrieveScopes(String filterXml) throws XmlCorruptedException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    /**
     * Retrieve Scopes the user is allowed to see.<br/>
     * 
     * Returns list of Scopes the user may see.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>All Scopes are accessed.</li>
     * <li>The XML data is returned.</li>
     * </ul>
     * 
     * @param parameters
     *            filter as CQL query
     * 
     * @return The XML representation of the Scopes corresponding to SRW schema.
     *         The list only contains these Scopes the user is allowed to see.
     * @throws MissingMethodParameterException
     *             If the parameter filter is not given.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws SystemException
     *             e.
     */
    String retrieveScopes(Map<String, String[]> parameters)
        throws InvalidSearchQueryException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Updates the specified Scope with the provided data.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * The provided XML data in the body is only accepted if the size is less
     * than ESCIDOC_MAX_XML_SIZE.<br/>
     * 
     * The Scope must exist<br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The Scope is accessed using the provided reference.</li>
     * <li>The Scope is updated.</li>
     * <li>The XML data of the updated Scope is returned.</li>
     * </ul>
     * 
     * @param scopeId
     *            The Scope ID to be updated.
     * @param xmlData
     *            The XML representation of the Scope to be updated
     *            corresponding to XML-schema "scope.xsd".
     * @return The XML representation of the updated Scope corresponding to
     *         XML-schema "scope.xsd".
     * 
     * @throws AuthenticationException
     *             Thrown in case of failed authentication.
     * @throws AuthorizationException
     *             Thrown in case of failed authorization.
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws XmlSchemaValidationException
     *             e.
     * @throws XmlCorruptedException
     *             e.
     * @throws SystemException
     *             e.
     * 
     */
    @Validate(param = 1, resolver = "getScopeSchemaLocation")
    String update(String scopeId, String xmlData) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, XmlSchemaValidationException,
        XmlCorruptedException, SystemException;
}
