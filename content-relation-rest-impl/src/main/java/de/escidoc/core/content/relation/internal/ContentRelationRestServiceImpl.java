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

import org.escidoc.core.domain.content.relation.ContentRelationTO;
import org.escidoc.core.service.ServiceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.content.relation.ContentRelationRestService;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;

@Service
public class ContentRelationRestServiceImpl implements ContentRelationRestService {

    @Autowired
    @Qualifier("service.ContentRelationHandler")
    private ContentRelationHandlerInterface contentRelationHandler;

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
        return ServiceUtility.fromXML(ContentRelationTO.class,
            this.contentRelationHandler.create(ServiceUtility.toXML(contentRelationTO)));

    }

    @Override
    public ContentRelationTO retrieve(final String id) throws SystemException, AuthorizationException,
        AuthenticationException, ContentRelationNotFoundException {
        return ServiceUtility.fromXML(ContentRelationTO.class, this.contentRelationHandler.retrieve(id));
    }

    @Override
    public ContentRelationTO update(final String id, final ContentRelationTO contentRelationTO) throws SystemException,
        InvalidContentException, OptimisticLockingException, MissingAttributeValueException,
        RelationPredicateNotFoundException, AuthorizationException, InvalidStatusException, AuthenticationException,
        ContentRelationNotFoundException, InvalidXmlException, ReferencedResourceNotFoundException, LockingException,
        MissingMethodParameterException {
        return ServiceUtility.fromXML(ContentRelationTO.class,
            this.contentRelationHandler.update(id, ServiceUtility.toXML(contentRelationTO)));
    }

    @Override
    public void delete(final String id) throws SystemException, AuthorizationException, AuthenticationException,
        ContentRelationNotFoundException, LockingException {
        contentRelationHandler.delete(id);
    }
}
