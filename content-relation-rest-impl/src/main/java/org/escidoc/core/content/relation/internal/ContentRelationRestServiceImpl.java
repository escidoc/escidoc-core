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
package org.escidoc.core.content.relation.internal;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.content.relation.ContentRelationPropertiesTypeTO;
import org.escidoc.core.domain.content.relation.ContentRelationResourcesTypeTO;
import org.escidoc.core.domain.content.relation.ContentRelationTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.predicate.list.PredicatesTypeTO;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.taskparam.assignpid.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.optimisticlocking.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
import org.escidoc.core.content.relation.ContentRelationRestService;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;

import javax.xml.bind.JAXBElement;

/**
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContentRelationRestServiceImpl implements ContentRelationRestService {

    @Autowired
    @Qualifier("service.ContentRelationHandler")
    private ContentRelationHandlerInterface contentRelationHandler;
    
    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    /**
     * 
     */
    protected ContentRelationRestServiceImpl() {}

    @Override
    public JAXBElement<ContentRelationTypeTO> create(final ContentRelationTypeTO contentRelationTO)
        throws SystemException, InvalidContentException, MissingAttributeValueException,
        RelationPredicateNotFoundException, AuthorizationException, AuthenticationException, InvalidXmlException,
        ReferencedResourceNotFoundException, MissingMethodParameterException {

        return factoryProvider.getContentRelationFactory().createContentRelation(serviceUtility.fromXML(
            ContentRelationTypeTO.class, this.contentRelationHandler.create(serviceUtility.toXML(contentRelationTO))));
    }

    @Override
    public JAXBElement<ContentRelationTypeTO> retrieve(final String id)
        throws SystemException, AuthorizationException, AuthenticationException, ContentRelationNotFoundException {

        return factoryProvider.getContentRelationFactory().createContentRelation(serviceUtility.fromXML(
            ContentRelationTypeTO.class, this.contentRelationHandler.retrieve(id)));
    }

    @Override
    public JAXBElement<ContentRelationTypeTO> update(final String id, final ContentRelationTypeTO contentRelationTO)
        throws SystemException, InvalidContentException, OptimisticLockingException, MissingAttributeValueException,
        RelationPredicateNotFoundException, AuthorizationException, InvalidStatusException, AuthenticationException,
        ContentRelationNotFoundException, InvalidXmlException, ReferencedResourceNotFoundException, LockingException,
        MissingMethodParameterException {

        return factoryProvider.getContentRelationFactory().createContentRelation(
            serviceUtility.fromXML(ContentRelationTypeTO.class, this.contentRelationHandler.update(
                id, serviceUtility.toXML(contentRelationTO))));
    }

    @Override
    public void delete(final String id)
        throws SystemException, AuthorizationException, AuthenticationException, ContentRelationNotFoundException,
        LockingException {

        this.contentRelationHandler.delete(id);
    }

    @Override
    public JAXBElement<ContentRelationPropertiesTypeTO> retrieveProperties(String id)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {

        return factoryProvider.getContentRelationFactory().createProperties(serviceUtility.fromXML(
            ContentRelationPropertiesTypeTO.class, this.contentRelationHandler.retrieveProperties(id)));
    }

    @Override
    public JAXBElement<ResultTypeTO> lock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.lock(id, serviceUtility.toXML(optimisticLockingTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> unlock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, InvalidStatusException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.unlock(id, serviceUtility.toXML(optimisticLockingTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> submit(String id, StatusTaskParamTO statusTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.submit(id, serviceUtility.toXML(statusTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> revise(String id, StatusTaskParamTO statusTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        XmlCorruptedException, InvalidContentException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.revise(id, serviceUtility.toXML(statusTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> release(String id, StatusTaskParamTO statusTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.release(id, serviceUtility.toXML(statusTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> assignObjectPid(String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException, InvalidXmlException, SystemException,
        PidAlreadyAssignedException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.contentRelationHandler.assignObjectPid(id, serviceUtility.toXML(assignPidTaskParamTO))));
    }

    @Override
    public JAXBElement<MdRecordsTypeTO> retrieveMdRecords(String id)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {

        return factoryProvider.getMdRecordsFactory().createMdRecords(serviceUtility.fromXML(
            MdRecordsTypeTO.class, this.contentRelationHandler.retrieveMdRecords(id)));
    }

    @Override
    public JAXBElement<MdRecordTypeTO> retrieveMdRecord(String id, String name)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        MdRecordNotFoundException,
        SystemException {

        return factoryProvider.getMdRecordsFactory().createMdRecord(serviceUtility.fromXML(
            MdRecordTypeTO.class, this.contentRelationHandler.retrieveMdRecord(id, name)));
    }

    @Override
    public JAXBElement<ContentRelationResourcesTypeTO> retrieveResources(String id)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getContentRelationFactory().createResources(serviceUtility.fromXML(
            ContentRelationResourcesTypeTO.class, this.contentRelationHandler.retrieveResources(id)));
    }
}