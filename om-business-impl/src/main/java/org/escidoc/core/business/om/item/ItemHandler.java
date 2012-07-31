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
package org.escidoc.core.business.om.item;

import java.io.IOException;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.business.om.interfaces.ItemHandlerInterface;
import org.escidoc.core.persistence.PersistenceImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
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
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * The retrieve, update, create and delete methods implement the {@link ItemHandlerInterface ItemHandlerInterface}.
 * These methods handle strings of xmlData and use the private (get,) set and render methods to set xmlData in the
 * system or get xmlData from the system.
 * <p/>
 * The private set methods take strings of xmlData as parameter and handling objects of type {@link Datastream Stream}
 * that hold the xmlData in an Item or Component object.
 * <p/>
 * To split incoming xmlData into the datastreams it consists of, the {@link StaxParser StaxParser} is used. In order to
 * modify datastreams or handle values provided in datastreams more than one Handler (implementations of DefaultHandler
 * can be added to the StaxParser. The {@link MultipleExtractor MultipleExtractor} have to be the last Handler in the
 * HandlerChain of a StaxParser.
 *
 * @author Frank Schwichtenberg
 */
@Service("business.NewItemHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ItemHandler implements ItemHandlerInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandler.class);

    @Autowired
    @Qualifier("persistence.FedoraImplementor")
    private PersistenceImplementor persistenceImplementor;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected ItemHandler() {
    }

    @Override
    public ItemDO retrieve(final ID id) throws ItemNotFoundException, MissingMethodParameterException, SystemException,
        ComponentNotFoundException, AuthorizationException {
        try {
            return persistenceImplementor.load(id, ItemDO.class);
        }
        catch (IOException e) {
            throw new FedoraSystemException(e.getMessage());
        }
    }

    /**
     * @return new XML representation of updated Item.
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#update(String, String)
     */
    @Override
    public ItemDO update(final ID id, final ItemDO itemDo) throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException, NotPublishedException,
        MissingLicenceException, ComponentNotFoundException, MissingAttributeValueException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, ReadonlyVersionException,
        MissingMdRecordException, AuthorizationException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException {
        return null;
    }

    /**
     * Create an Item.
     */
    @Override
    public ItemDO create(final ItemDO itemDo) throws MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, XmlCorruptedException,
        MissingMethodParameterException, FileNotFoundException, SystemException, ReferencedResourceNotFoundException,
        InvalidContentException, RelationPredicateNotFoundException, MissingMdRecordException, InvalidStatusException,
        AuthorizationException {

        return null;
    }

}
