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
package org.escidoc.core.om.internal;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBElement;

import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.business.om.interfaces.ItemHandlerInterface;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.item.ItemTypeTO;
import org.escidoc.core.domain.service.om.ItemTranslator;
import org.escidoc.core.om.NewItemRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * REST Service Implementation for Item.
 *
 * @author SWA
 */
@Service
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class NewItemRestServiceImpl implements NewItemRestService {

    private final static Logger LOG = LoggerFactory.getLogger(NewItemRestServiceImpl.class);

    @Autowired
    @Qualifier("business.NewItemHandler")
    private ItemHandlerInterface itemHandler;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    @Autowired
    @Qualifier("domain.ItemTranslator")
    private ItemTranslator itemTranslator;

    protected NewItemRestServiceImpl() {
    }

    @Override
    public JAXBElement<ItemTypeTO> create(final ItemTypeTO itemTO)
        throws MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, FileNotFoundException, SystemException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException, RemoteException {
        
        ItemDO itemDo = itemTranslator.To2Do(itemTO);
        ItemDO returnDo = itemHandler.create(itemDo);
        return factoryProvider.getItemFactory().createItem(itemTranslator.Do2To(returnDo));
    }

    @Override
    public JAXBElement<ItemTypeTO> retrieve(final String id)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException {
        ItemDO returnDo = itemHandler.retrieve(new ID(id));
        return factoryProvider.getItemFactory().createItem(itemTranslator.Do2To(returnDo));
    }

    @Override
    public JAXBElement<ItemTypeTO> update(final String id, final ItemTypeTO itemTO)
        throws ItemNotFoundException, FileNotFoundException, InvalidContextException, InvalidStatusException,
        LockingException, NotPublishedException, MissingLicenceException, ComponentNotFoundException,
        MissingContentException, AuthenticationException, AuthorizationException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        AlreadyExistsException, ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException, MissingAttributeValueException,
        MissingMdRecordException, RemoteException {

        ItemDO itemDo = itemTranslator.To2Do(itemTO);
        ItemDO returnDo = itemHandler.update(new ID(id), itemDo);
        return factoryProvider.getItemFactory().createItem(itemTranslator.Do2To(returnDo));
    }

}