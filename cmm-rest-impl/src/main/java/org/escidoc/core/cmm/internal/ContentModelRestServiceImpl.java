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

import org.escidoc.core.cmm.ContentModelRestService;
import org.escidoc.core.domain.content.model.ContentModelPropertiesTO;
import org.escidoc.core.domain.content.model.ContentModelResourcesTO;
import org.escidoc.core.domain.content.model.ContentModelTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.version.VersionHistoryTO;
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
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * 
 * @author SWA
 * 
 */
public class ContentModelRestServiceImpl implements ContentModelRestService {

    @Autowired
    @Qualifier("service.ContentModelHandler")
    private ContentModelHandlerInterface contentModelHandler;

    /**
     * 
     */
    protected ContentModelRestServiceImpl() {

    }

    public ContentModelTO create(ContentModelTO contentModelTO) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, InvalidContentException,
        XmlCorruptedException, XmlSchemaValidationException {

        return ServiceUtility.fromXML(ContentModelTO.class,
            this.contentModelHandler.create(ServiceUtility.toXML(contentModelTO)));

    }

    public void delete(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException, LockingException,
        InvalidStatusException, ResourceInUseException {

        this.contentModelHandler.delete(id);
    }

    public ContentModelTO retrieve(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException {

        return ServiceUtility.fromXML(ContentModelTO.class, this.contentModelHandler.retrieve(id));

    }

    public ContentModelPropertiesTO retrieveProperties(String id) throws ContentModelNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException {

        return ServiceUtility.fromXML(ContentModelPropertiesTO.class,
            this.contentModelHandler.retrieveProperties(id));

    }

    public ContentModelResourcesTO retrieveResources(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException {

        return ServiceUtility.fromXML(ContentModelResourcesTO.class,
            this.contentModelHandler.retrieveResources(id));

    }

    public VersionHistoryTO retrieveVersionHistory(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException {

        return ServiceUtility.fromXML(VersionHistoryTO.class, this.contentModelHandler.retrieveVersionHistory(id));

    }

    public ContentModelTO update(String id, ContentModelTO contentModelTO) throws AuthenticationException,
        AuthorizationException, ContentModelNotFoundException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, ReadonlyVersionException, MissingAttributeValueException,
        InvalidContentException {

        return ServiceUtility.fromXML(ContentModelTO.class,
            this.contentModelHandler.update(id, ServiceUtility.toXML(contentModelTO)));

    }

    // FIXME
    // public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(String id, String name)
    // throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
    // ContentModelNotFoundException, SystemException {
    // return ServiceUtility.fromXML(ContentModelTO.class,
    // this.contentModelHandler.create(ServiceUtility.toXML(contentModelTO)));
    // }

    // FIXME
    // public EscidocBinaryContent retrieveResourceDefinitionXsltContent(String id, String name) throws
    // AuthenticationException,
    // AuthorizationException, MissingMethodParameterException, SystemException, ResourceNotFoundException {
    // return ServiceUtility.fromXML(ContentModelTO.class,
    // this.contentModelHandler.create(ServiceUtility.toXML(contentModelTO)));
    // }

}
