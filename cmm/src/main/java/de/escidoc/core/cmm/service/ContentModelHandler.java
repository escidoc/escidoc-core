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
package de.escidoc.core.cmm.service;

import de.escidoc.core.cmm.business.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.filter.LuceneRequestParameters;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation for the Service layer of the ctm component.
 *
 * @author Michael Schneider
 */
@Service("service.ContentModelHandler")
public class ContentModelHandler implements de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraContentModelHandler")
    private ContentModelHandlerInterface business;

    /**
     * Setter for the business object.
     *
     * @param business business object.
     */
    public void setBusiness(final ContentModelHandlerInterface business) {
        this.business = business;
    }

    @Override
    public String ingest(final String xmlData) throws EscidocException {

        return business.ingest(xmlData);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String create(final String xmlData) throws InvalidContentException, MissingAttributeValueException,
        SystemException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        XmlCorruptedException, XmlSchemaValidationException {
        return business.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see package de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws SystemException, ContentModelNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, LockingException, InvalidStatusException,
        ResourceInUseException {
        business.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see package de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws ContentModelNotFoundException, SystemException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException {
        return business.retrieve(id);
    }

    @Override
    public String retrieveResources(final String id) throws ContentModelNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {
        return business.retrieveResources(id);
    }

    @Override
    public String retrieveVersionHistory(final String id) throws ContentModelNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {
        return business.retrieveVersionHistory(id);
    }

    /**
     * Retrieves a filtered list of Content Models.
     *
     * @param parameterMap map of key - value pairs describing the filter
     * @return Returns XML representation of the list of Content Model objects.
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws SystemException             Thrown in case of an internal error.
     */
    @Override
    public String retrieveContentModels(final Map<String, String[]> parameterMap) throws InvalidSearchQueryException,
        SystemException {
        return business.retrieveContentModels(new LuceneRequestParameters(parameterMap));
    }

    /**
     * See Interface for functional description.
     *
     * @see package de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface #update(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws InvalidXmlException,
        ContentModelNotFoundException, OptimisticLockingException, SystemException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidContentException {
        return business.update(id, xmlData);
    }

    @Override
    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(final String id, final String name)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ContentModelNotFoundException, SystemException {
        return business.retrieveMdRecordDefinitionSchemaContent(id, name);
    }

    @Override
    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(final String id, final String name)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException {
        return business.retrieveResourceDefinitionXsltContent(id, name);
    }

}
