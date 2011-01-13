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

import java.util.Map;

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

/**
 * Implementation for the Service layer of the OUM component.
 * 
 * @spring.bean id="service.OrganizationalUnitHandler" scope="prototype"
 * @interface class=
 *            "de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface"
 * @author MSC
 * @service
 */
public class OrganizationalUnitHandler
    implements
    de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface {

    private OrganizationalUnitHandlerInterface business;

    /**
     * Setter for the business object.
     * 
     * @spring.property ref="business.FedoraOrganizationalUnitHandler"
     * @param business
     *            business object.
     * @service.exclude
     */
    public void setBusiness(final OrganizationalUnitHandlerInterface business) {

        this.business = business;
    }

    /**
     * Ingest a resource.
     * 
     * @param user
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidXmlException
     * @throws MissingAttributeValueException
     * @throws MissingElementValueException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see {@link de.escidoc.core.common.business.interfaces.IngestableResource#ingest(String)}
     * @axis.exclude
     */
    public String ingest(final String xmlData) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidXmlException, InvalidStatusException, EscidocException {

        return business.ingest(xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param xml
     *            The XML representation of the organizational unit.
     * @return The XML representation of the created organizational unit.
     * @throws AuthenticationException
     *             e
     * @throws AuthorizationException
     *             e
     * @throws InvalidStatusException
     *             e
     * @throws MissingAttributeValueException
     *             e
     * @throws MissingElementValueException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws OrganizationalUnitNotFoundException
     *             e
     * @throws XmlCorruptedException
     *             e
     * @throws XmlSchemaValidationException
     *             e
     * @throws SystemException
     *             e
     * @throws MissingMdRecordException
     *             If required md-record is missing
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#create(java.lang.String)
     */
    public String create(final String xml) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMdRecordException {

        return business.create(xml);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws InvalidStatusException
     * @throws OrganizationalUnitHasChildrenException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#delete(java.lang.String)
     */
    public void delete(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException {

        business.delete(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param user
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidXmlException
     * @throws MissingMethodParameterException
     * @throws OptimisticLockingException
     * @throws OrganizationalUnitHierarchyViolationException
     * @throws OrganizationalUnitNotFoundException
     * @throws MissingElementValueException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#update(java.lang.String,
     *      java.lang.String)
     */
    public String update(final String id, final String user)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException {

        return business.update(id, user);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xml
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidXmlException
     * @throws MissingMethodParameterException
     * @throws OptimisticLockingException
     * @throws OrganizationalUnitNotFoundException
     * @throws MissingElementValueException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface#updateMdRecords(java.lang.String,
     *      java.lang.String)
     */
    public String updateMdRecords(final String id, final String xml)
        throws AuthenticationException, AuthorizationException,
        InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitNotFoundException, MissingElementValueException,
        SystemException {

        return business.updateMdRecords(id, xml);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xml
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidXmlException
     * @throws MissingMethodParameterException
     * @throws OptimisticLockingException
     * @throws OrganizationalUnitHierarchyViolationException
     * @throws OrganizationalUnitNotFoundException
     * @throws MissingElementValueException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface#updateParents(java.lang.String,
     *      java.lang.String)
     */
    public String updateParents(final String id, final String xml)
        throws AuthenticationException, AuthorizationException,
        InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException,
        OrganizationalUnitNotFoundException, MissingElementValueException,
        SystemException, InvalidStatusException {

        return business.updateParents(id, xml);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieve(java.lang.String)
     */
    public String retrieve(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return business.retrieve(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveProperties(java.lang.String)
     */
    public String retrieveProperties(final String id)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {
        return business.retrieveProperties(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param resourceName
     * @return
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface
     *      #retrieveResource(java.lang.String, java.lang.String)
     * @axis.exclude
     */
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        OperationNotFoundException, SystemException {

        return business.retrieveResource(id, resourceName);
    }

    /**
     * See Interface for functional description.
     * 
     * @param ouId
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveResources(java.lang.String)
     * @axis.exclude
     */
    public String retrieveResources(final String ouId)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {

        return business.retrieveResources(ouId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveMdRecords(java.lang.String)
     */
    public String retrieveMdRecords(final String id)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {

        return business.retrieveMdRecords(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param name
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MdRecordNotFoundException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface#retrieveMdRecord(java.lang.String,
     *      java.lang.String)
     */
    public String retrieveMdRecord(final String id, final String name)
        throws AuthenticationException, AuthorizationException,
        MdRecordNotFoundException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        return business.retrieveMdRecord(id, name);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveParents(java.lang.String)
     */
    public String retrieveParents(final String id)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {
        return business.retrieveParents(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param ouId
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveParentObjects(java.lang.String)
     */
    public String retrieveParentObjects(final String ouId)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {

        return business.retrieveParentObjects(ouId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface
     * #retrieveSuccessors(java.lang.String)
     */
    public String retrieveSuccessors(final String id)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {

        return business.retrieveSuccessors(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param ouId
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveChildObjects(java.lang.String)
     */
    public String retrieveChildObjects(final String ouId)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException {

        return business.retrieveChildObjects(ouId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param ouId
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @throws MissingMethodParameterException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrievePathList(java.lang.String)
     */
    public String retrievePathList(final String ouId)
        throws AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException,
        MissingMethodParameterException {
        return business.retrievePathList(ouId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidXmlException
     * @see de.escidoc.core.oum.service.interfaces.
     *      OrganizationalUnitHandlerInterface#retrieveOrganizationalUnits(java.lang.String)
     */
    public String retrieveOrganizationalUnits(final Map<String, String[]> filter)
        throws MissingMethodParameterException, SystemException,
        InvalidSearchQueryException, InvalidXmlException {

        return business
            .retrieveOrganizationalUnits(new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface#close(java.lang.String,
     *      String)
     */
    public String close(final String id, final String taskParam)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {

        return business.close(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface#open(java.lang.String,
     *      String)
     */
    public String open(final String id, final String taskParam)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {

        return business.open(id, taskParam);
    }
}
