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

import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.ou.OrganizationalUnitPropertiesTO;
import org.escidoc.core.domain.ou.OrganizationalUnitResourcesTO;
import org.escidoc.core.domain.ou.OrganizationalUnitTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.ou.path.list.OrganizationalUnitPathListTO;
import org.escidoc.core.domain.ou.successors.SuccessorsTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.oum.OrganizationalUnitRestService;
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
 * 
 */
@Service
public class OrganizationalUnitRestServiceImpl implements OrganizationalUnitRestService {

    private final static Logger LOG = LoggerFactory.getLogger(OrganizationalUnitRestServiceImpl.class);

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface organizationalUnitHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    protected OrganizationalUnitRestServiceImpl() {}

    @Override
    public OrganizationalUnitTO create(final OrganizationalUnitTO organizationalUnitTO) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException {

        return serviceUtility.fromXML(OrganizationalUnitTO.class,
            this.organizationalUnitHandler.create((serviceUtility.toXML(organizationalUnitTO))));

    }

    @Override
    public OrganizationalUnitTO retrieve(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(OrganizationalUnitTO.class, this.organizationalUnitHandler.retrieve(id));
    }

    @Override
    public OrganizationalUnitTO update(final String id, final OrganizationalUnitTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException {

        return serviceUtility.fromXML(OrganizationalUnitTO.class,
            this.organizationalUnitHandler.update(id, serviceUtility.toXML(organizationalUnitTO)));
    }

    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException {

        this.organizationalUnitHandler.delete(id);
    }

    @Override
    public MdRecordsTO updateMdRecords(final String id, final MdRecordsTO mdRecordsTO) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, InvalidStatusException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, MissingElementValueException, SystemException {

        return serviceUtility.fromXML(MdRecordsTO.class,
            this.organizationalUnitHandler.updateMdRecords(id, serviceUtility.toXML(mdRecordsTO)));
    }

    @Override
    public ParentsTO updateParents(final String id, final ParentsTO parentsTO) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException {

        return serviceUtility.fromXML(ParentsTO.class,
            this.organizationalUnitHandler.updateParents(id, serviceUtility.toXML(parentsTO)));
    }

    @Override
    public OrganizationalUnitPropertiesTO retrieveProperties(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(OrganizationalUnitPropertiesTO.class,
            this.organizationalUnitHandler.retrieveProperties(id));
    }

    @Override
    public Stream retrieveResource(final String id, final String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException {

        Stream stream = new Stream();
        byte[] buffer = new byte[1024];
        try {
            InputStream ins = this.organizationalUnitHandler.retrieveResource(id, resourceName).getContent();
        int len;
        while ((len = ins.read(buffer)) > 0) {
            stream.write(buffer, 0, len);
        }
        } catch (IOException e) {
            LOG.error("Stream copy error", e);
            throw new SystemException(e);
        }

        return stream;
    }

    @Override
    public OrganizationalUnitResourcesTO retrieveResources(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(OrganizationalUnitResourcesTO.class,
            this.organizationalUnitHandler.retrieveResources(id));
    }

    @Override
    public MdRecordsTO retrieveMdRecords(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(MdRecordsTO.class, this.organizationalUnitHandler.retrieveMdRecords(id));
    }

    @Override
    public MdRecordTO retrieveMdRecord(final String id, final String name) throws AuthenticationException,
        AuthorizationException, MdRecordNotFoundException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(MdRecordTO.class, this.organizationalUnitHandler.retrieveMdRecord(id, name));
    }

    @Override
    public ParentsTO retrieveParents(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(ParentsTO.class, this.organizationalUnitHandler.retrieveParents(id));
    }

    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveParentObjects(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
            this.organizationalUnitHandler.retrieveParentObjects(id));
    }

    @Override
    public SuccessorsTO retrieveSuccessors(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return serviceUtility.fromXML(SuccessorsTO.class, this.organizationalUnitHandler.retrieveSuccessors(id));
    }

    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveChildObjects(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
            this.organizationalUnitHandler.retrieveChildObjects(id));
    }

    @Override
    public OrganizationalUnitPathListTO retrievePathList(final String id) throws AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException {

        return serviceUtility.fromXML(OrganizationalUnitPathListTO.class, this.organizationalUnitHandler.retrievePathList(id));
    }

    @Override
    public ResultTO close(final String id, final StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.organizationalUnitHandler.close(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO open(final String id, final StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.organizationalUnitHandler.open(id, serviceUtility.toXML(statusTaskParamTO)));
    }

}
