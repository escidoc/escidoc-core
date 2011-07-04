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
package de.escidoc.core.oum.service;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.filter.LuceneRequestParameters;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.oum.business.interfaces.OrganizationalUnitHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation for the Service layer of the OUM component.
 *
 * @author Michael Schneider
 */
@Service("service.OrganizationalUnitHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OrganizationalUnitHandler
    implements de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraOrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface business;

    @Override
    public String ingest(final String xmlData) throws EscidocException {
        return business.ingest(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @param xml The XML representation of the organizational unit.
     * @return The XML representation of the created organizational unit.
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws InvalidStatusException         e
     * @throws MissingAttributeValueException e
     * @throws MissingElementValueException   e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws OrganizationalUnitNotFoundException
     *                                        e
     * @throws XmlCorruptedException          e
     * @throws XmlSchemaValidationException   e
     * @throws SystemException                e
     * @throws MissingMdRecordException       If required md-record is missing
     */
    @Override
    public String create(final String xml) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, MissingElementValueException,
        OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMdRecordException {

        return business.create(xml);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException {

        business.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String update(final String id, final String user) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException {

        return business.update(id, user);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String updateMdRecords(final String id, final String xml) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, InvalidStatusException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, MissingElementValueException, SystemException {

        return business.updateMdRecords(id, xml);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String updateParents(final String id, final String xml) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException {

        return business.updateParents(id, xml);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieve(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieve(id);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveProperties(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {
        return business.retrieveProperties(id);
    }

    @Override
    public EscidocBinaryContent retrieveResource(final String id, final String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException {

        return business.retrieveResource(id, resourceName);
    }

    @Override
    public String retrieveResources(final String ouId) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieveResources(ouId);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveMdRecords(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieveMdRecords(id);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveMdRecord(final String id, final String name) throws AuthenticationException,
        AuthorizationException, MdRecordNotFoundException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        return business.retrieveMdRecord(id, name);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveParents(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {
        return business.retrieveParents(id);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveParentObjects(final String ouId) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieveParentObjects(ouId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface
     * #retrieveSuccessors(java.lang.String)
     */
    @Override
    public String retrieveSuccessors(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieveSuccessors(id);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveChildObjects(final String ouId) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return business.retrieveChildObjects(ouId);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrievePathList(final String ouId) throws AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException {
        return business.retrievePathList(ouId);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieveOrganizationalUnits(final Map<String, String[]> filter)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException {

        return business.retrieveOrganizationalUnits(new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String close(final String id, final String taskParam) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException {

        return business.close(id, taskParam);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String open(final String id, final String taskParam) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException, SystemException,
        OptimisticLockingException, InvalidXmlException {

        return business.open(id, taskParam);
    }
}
