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

package de.escidoc.core.content.relation.internal;

import org.escidoc.core.domain.content.relation.ContentRelationPropertiesTO;
import org.escidoc.core.domain.content.relation.ContentRelationTO;
import org.escidoc.core.domain.content.relation.ContentRelationResourcesTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.predicate.list.PredicatesTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.taskparam.assignpid.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.optimisticlocking.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.content.relation.ContentRelationRestService;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;

/**
 * 
 * @author ?, SWA
 * 
 */
@Transactional
public class ContentRelationRestServiceImpl implements ContentRelationRestService {

    @Autowired
    @Qualifier("service.ContentRelationHandler")
    private ContentRelationHandlerInterface contentRelationHandler;
    
    @Autowired
    private ServiceUtility serviceUtility;

    /**
     * 
     */
    protected ContentRelationRestServiceImpl() {

    }

    @Override
    public ContentRelationTO create(final ContentRelationTO contentRelationTO) throws SystemException,
        InvalidContentException, MissingAttributeValueException, RelationPredicateNotFoundException,
        AuthorizationException, AuthenticationException, InvalidXmlException, ReferencedResourceNotFoundException,
        MissingMethodParameterException {

        return serviceUtility.fromXML(ContentRelationTO.class,
            this.contentRelationHandler.create(serviceUtility.toXML(contentRelationTO)));

    }

    @Override
    public ContentRelationTO retrieve(final String id) throws SystemException, AuthorizationException,
        AuthenticationException, ContentRelationNotFoundException {

        return serviceUtility.fromXML(ContentRelationTO.class, this.contentRelationHandler.retrieve(id));
    }

    @Override
    public ContentRelationTO update(final String id, final ContentRelationTO contentRelationTO) throws SystemException,
        InvalidContentException, OptimisticLockingException, MissingAttributeValueException,
        RelationPredicateNotFoundException, AuthorizationException, InvalidStatusException, AuthenticationException,
        ContentRelationNotFoundException, InvalidXmlException, ReferencedResourceNotFoundException, LockingException,
        MissingMethodParameterException {

        return serviceUtility.fromXML(ContentRelationTO.class,
            this.contentRelationHandler.update(id, serviceUtility.toXML(contentRelationTO)));
    }

    @Override
    public void delete(final String id) throws SystemException, AuthorizationException, AuthenticationException,
        ContentRelationNotFoundException, LockingException {

        this.contentRelationHandler.delete(id);
    }

    @Override
    public ContentRelationPropertiesTO retrieveProperties(String id) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException {

        return serviceUtility.fromXML(ContentRelationPropertiesTO.class,
            this.contentRelationHandler.retrieveProperties(id));
    }

    @Override
    public ResultTO lock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidContentException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.lock(id, serviceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO unlock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidContentException,
        InvalidStatusException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.unlock(id, serviceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO submit(String id, StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.submit(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO revise(String id, StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, XmlCorruptedException,
        InvalidContentException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.revise(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO release(String id, StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.release(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO assignObjectPid(String id, AssignPidTaskParamTO assignPidTaskParamTO) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, InvalidXmlException, SystemException, PidAlreadyAssignedException {

        return serviceUtility.fromXML(ResultTO.class,
            this.contentRelationHandler.assignObjectPid(id, serviceUtility.toXML(assignPidTaskParamTO)));
    }

    @Override
    public MdRecordsTO retrieveMdRecords(String id) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException {

        return serviceUtility.fromXML(MdRecordsTO.class, this.contentRelationHandler.retrieveMdRecords(id));
    }

    @Override
    public MdRecordTO retrieveMdRecord(String id, String name) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, MdRecordNotFoundException, SystemException {

        return serviceUtility.fromXML(MdRecordTO.class, this.contentRelationHandler.retrieveMdRecord(id, name));
    }

    @Override
    public PredicatesTO retrieveRegisteredPredicates() throws InvalidContentException, InvalidXmlException,
        SystemException {

        return serviceUtility.fromXML(PredicatesTO.class,
            this.contentRelationHandler.retrieveRegisteredPredicates());
    }

    @Override
    public ContentRelationResourcesTO retrieveResources(String id) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, MissingMethodParameterException, SystemException {
        return serviceUtility.fromXML(ContentRelationResourcesTO.class, this.contentRelationHandler.retrieveResources(id));
    }

}
