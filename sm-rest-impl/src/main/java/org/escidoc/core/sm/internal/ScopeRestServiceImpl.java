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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.core.sm.internal;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sm.scope.ScopeTypeTO;
import org.escidoc.core.sm.ScopeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface;

import javax.xml.bind.JAXBElement;

/**
 * @author Michael Hoppe
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ScopeRestServiceImpl implements ScopeRestService {

    @Autowired
    @Qualifier("service.ScopeHandler")
    private ScopeHandlerInterface scopeHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    /**
     *
     */
    protected ScopeRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.ScopeRestService#create(org.escidoc.core.domain.sm.ScopeTO)
     */
    @Override
    public JAXBElement<ScopeTypeTO> create(final ScopeTypeTO scopeTO)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, SystemException {

        return factoryProvider.getScopeFactory().createScope(
            serviceUtility.fromXML(ScopeTypeTO.class, this.scopeHandler.create(serviceUtility.toXML(scopeTO))));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.ScopeRestService#delete(java.lang.String)
     */
    @Override
    public void delete(final String id)
        throws AuthenticationException, AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException {

        this.scopeHandler.delete(id);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.ScopeRestService#retrieve(java.lang.String)
     */
    @Override
    public JAXBElement<ScopeTypeTO> retrieve(final String id)
        throws AuthenticationException, AuthorizationException, ScopeNotFoundException, MissingMethodParameterException,
        SystemException {

        return factoryProvider.getScopeFactory().createScope(
            serviceUtility.fromXML(ScopeTypeTO.class, this.scopeHandler.retrieve(id)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.ScopeRestService#update(java.lang.String, org.escidoc.core.domain.sm.ScopeTO)
     */
    @Override
    public JAXBElement<ScopeTypeTO> update(final String id, final ScopeTypeTO scopeTO)
        throws AuthenticationException, AuthorizationException, ScopeNotFoundException, MissingMethodParameterException,
        XmlSchemaValidationException, XmlCorruptedException, SystemException {

        return factoryProvider.getScopeFactory().createScope(
            serviceUtility.fromXML(ScopeTypeTO.class, this.scopeHandler.update(id, serviceUtility.toXML(scopeTO))));
    }
}