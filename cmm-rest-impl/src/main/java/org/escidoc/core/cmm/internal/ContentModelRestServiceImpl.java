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
package org.escidoc.core.cmm.internal;

import javax.ws.rs.core.Response;

import net.sf.oval.guard.Guarded;

import org.escidoc.core.cmm.ContentModelRestService;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.content.model.ContentModelPropertiesTypeTO;
import org.escidoc.core.domain.content.model.ContentModelResourcesTypeTO;
import org.escidoc.core.domain.content.model.ContentModelTypeTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.version.history.VersionHistoryTypeTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
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

import javax.xml.bind.JAXBElement;

/**
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContentModelRestServiceImpl implements ContentModelRestService {

    @Autowired
    @Qualifier("service.ContentModelHandler")
    private ContentModelHandlerInterface contentModelHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    /**
     *
     */
    protected ContentModelRestServiceImpl() {

    }

    public JAXBElement<ContentModelTypeTO> create(ContentModelTypeTO contentModelTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, InvalidContentException, XmlCorruptedException, XmlSchemaValidationException {

        return factoryProvider.getContentModelFactory().createContentModel(serviceUtility
            .fromXML(ContentModelTypeTO.class, this.contentModelHandler.create(serviceUtility.toXML(contentModelTO))));
    }

    public void delete(String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException, LockingException, InvalidStatusException,
        ResourceInUseException {

        this.contentModelHandler.delete(id);
    }

    public JAXBElement<ContentModelTypeTO> retrieve(String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getContentModelFactory().createContentModel(
            serviceUtility.fromXML(ContentModelTypeTO.class, this.contentModelHandler.retrieve(id)));
    }

    public JAXBElement<ContentModelPropertiesTypeTO> retrieveProperties(String id)
        throws ContentModelNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getContentModelFactory().createProperties(
            serviceUtility
                .fromXML(ContentModelPropertiesTypeTO.class, this.contentModelHandler.retrieveProperties(id)));
    }

    public JAXBElement<ContentModelResourcesTypeTO> retrieveResources(String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getContentModelFactory().createResources(
            serviceUtility.fromXML(ContentModelResourcesTypeTO.class, this.contentModelHandler.retrieveResources(id)));
    }

    public JAXBElement<VersionHistoryTypeTO> retrieveVersionHistory(String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getVersionHistoryFactory().createVersionHistory(
            serviceUtility.fromXML(VersionHistoryTypeTO.class, this.contentModelHandler.retrieveVersionHistory(id)));
    }

    public JAXBElement<ContentModelTypeTO> update(String id, ContentModelTypeTO contentModelTO)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException, InvalidXmlException,
        MissingMethodParameterException, OptimisticLockingException, SystemException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidContentException {

        return factoryProvider.getContentModelFactory().createContentModel(serviceUtility
            .fromXML(ContentModelTypeTO.class,
                this.contentModelHandler.update(id, serviceUtility.toXML(contentModelTO))));
    }

    public Response retrieveMdRecordDefinitionSchemaContent(String id, String name) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ContentModelNotFoundException, SystemException {
        return serviceUtility.toResponse(this.contentModelHandler.retrieveMdRecordDefinitionSchemaContent(id, name));
    }

    public Response retrieveResourceDefinitionXsltContent(String id, String name) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, ResourceNotFoundException {
        return serviceUtility.toResponse(this.contentModelHandler.retrieveResourceDefinitionXsltContent(id, name));
    }

}
