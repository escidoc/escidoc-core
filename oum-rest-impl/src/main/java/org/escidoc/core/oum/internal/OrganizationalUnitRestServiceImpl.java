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
package org.escidoc.core.oum.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitPropertiesTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitResourcesTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitTypeTO;
import org.escidoc.core.domain.ou.ParentsTypeTO;
import org.escidoc.core.domain.ou.path.list.OrganizationalUnitPathListTypeTO;
import org.escidoc.core.domain.ou.successors.SuccessorsTypeTO;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.oum.OrganizationalUnitRestService;
import org.escidoc.core.utils.io.IOUtils;
import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;

/**
 * REST Service Implementation for Organizational Unit.
 *
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Service
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class OrganizationalUnitRestServiceImpl implements OrganizationalUnitRestService {

    private final static Logger LOG = LoggerFactory.getLogger(OrganizationalUnitRestServiceImpl.class);

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface organizationalUnitHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    protected OrganizationalUnitRestServiceImpl() {
    }

    @Override
    public JAXBElement<OrganizationalUnitTypeTO> create(final OrganizationalUnitTypeTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException {

        return factoryProvider.getOuFactory().createOrganizationalUnit(serviceUtility
            .fromXML(OrganizationalUnitTypeTO.class,
                this.organizationalUnitHandler.create((serviceUtility.toXML(organizationalUnitTO)))));
    }

    @Override
    public JAXBElement<OrganizationalUnitTypeTO> retrieve(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getOuFactory().createOrganizationalUnit(
            serviceUtility.fromXML(OrganizationalUnitTypeTO.class, this.organizationalUnitHandler.retrieve(id)));
    }

    @Override
    public JAXBElement<OrganizationalUnitTypeTO> update(final String id,
        final OrganizationalUnitTypeTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException {

        return factoryProvider.getOuFactory().createOrganizationalUnit(serviceUtility
            .fromXML(OrganizationalUnitTypeTO.class,
                this.organizationalUnitHandler.update(id, serviceUtility.toXML(organizationalUnitTO))));
    }

    @Override
    public void delete(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, OrganizationalUnitHasChildrenException,
        SystemException {

        this.organizationalUnitHandler.delete(id);
    }

    @Override
    public JAXBElement<MdRecordsTypeTO> updateMdRecords(final String id, final MdRecordsTypeTO mdRecordsTO)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException {

        return factoryProvider.getMdRecordsFactory().createMdRecords(serviceUtility.fromXML(MdRecordsTypeTO.class,
            this.organizationalUnitHandler.updateMdRecords(id, serviceUtility.toXML(mdRecordsTO))));
    }

    @Override
    public JAXBElement<ParentsTypeTO> updateParents(final String id, final ParentsTypeTO parentsTO)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException {

        return factoryProvider.getOuFactory().createParents(serviceUtility.fromXML(ParentsTypeTO.class,
            this.organizationalUnitHandler.updateParents(id, serviceUtility.toXML(parentsTO))));
    }

    @Override
    public JAXBElement<OrganizationalUnitPropertiesTypeTO> retrieveProperties(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getOuFactory().createProperties(serviceUtility
            .fromXML(OrganizationalUnitPropertiesTypeTO.class, this.organizationalUnitHandler.retrieveProperties(id)));
    }

    @Override
    public Stream retrieveResource(final String id, final String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException {

        Stream stream = new Stream();
        try {
            InputStream ins = this.organizationalUnitHandler.retrieveResource(id, resourceName).getContent();
            IOUtils.copyAndCloseInput(ins, stream);
        } catch (IOException e) {
            LOG.error("Stream copy error", e);
            throw new SystemException(e);
        }

        return stream;
    }

    @Override
    public JAXBElement<OrganizationalUnitResourcesTypeTO> retrieveResources(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getOuFactory().createResources(serviceUtility.fromXML(
            OrganizationalUnitResourcesTypeTO.class, this.organizationalUnitHandler.retrieveResources(id)));
    }

    @Override
    public JAXBElement<MdRecordsTypeTO> retrieveMdRecords(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getMdRecordsFactory().createMdRecords(
            serviceUtility.fromXML(MdRecordsTypeTO.class, this.organizationalUnitHandler.retrieveMdRecords(id)));
    }

    @Override
    public JAXBElement<MdRecordTypeTO> retrieveMdRecord(final String id, final String name)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getMdRecordsFactory().createMdRecord(
            serviceUtility.fromXML(MdRecordTypeTO.class, this.organizationalUnitHandler.retrieveMdRecord(id, name)));
    }

    @Override
    public JAXBElement<ParentsTypeTO> retrieveParents(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getOuFactory().createParents(
            serviceUtility.fromXML(ParentsTypeTO.class, this.organizationalUnitHandler.retrieveParents(id)));
    }

    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveParentObjects(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility
            .fromXML(this.organizationalUnitHandler.retrieveParentObjects(id));
    }

    @Override
    public JAXBElement<SuccessorsTypeTO> retrieveSuccessors(final String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return factoryProvider.getOuSuccessorsFactory().createSuccessors(
            serviceUtility.fromXML(SuccessorsTypeTO.class, this.organizationalUnitHandler.retrieveSuccessors(id)));
    }

    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveChildObjects(final String id)
        throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility
            .fromXML(this.organizationalUnitHandler.retrieveChildObjects(id));
    }

    @Override
    public JAXBElement<OrganizationalUnitPathListTypeTO> retrievePathList(final String id)
        throws AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        MissingMethodParameterException {

        return factoryProvider.getOuPathListFactory().createOrganizationalUnitPathList(serviceUtility.fromXML(
            OrganizationalUnitPathListTypeTO.class, this.organizationalUnitHandler.retrievePathList(id)));
    }

    @Override
    public JAXBElement<ResultTypeTO> close(final String id, final StatusTaskParamTO statusTaskParamTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.organizationalUnitHandler.close(id, serviceUtility.toXML(statusTaskParamTO))));
    }

    @Override
    public JAXBElement<ResultTypeTO> open(final String id, final StatusTaskParamTO statusTaskParamTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {

        return factoryProvider.getResultFactory().createResult(serviceUtility.fromXML(ResultTypeTO.class,
            this.organizationalUnitHandler.open(id, serviceUtility.toXML(statusTaskParamTO))));
    }
}