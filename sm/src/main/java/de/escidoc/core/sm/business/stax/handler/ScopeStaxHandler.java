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
package de.escidoc.core.sm.business.stax.handler;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Fills xml-data into hibernate object.
 *
 * @author Michael Hoppe
 */
public class ScopeStaxHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeStaxHandler.class);

    private Scope scope = new Scope();

    private final Map<String, Integer> charactersCounter = new HashMap<String, Integer>();

    private static final String MSG_INCONSISTENT_IDS = "id in xml is not the same as id provided in method.";

    /**
     * Handle startElement event.
     *
     * @param element startElement
     * @return StartElement startElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws IntegritySystemException {
        if ("scope".equals(element.getLocalName())) {
            try {
                final String scopeId = XmlUtility.getIdFromStartElement(element);
                if (scope.getId() != null && !scope.getId().equals(scopeId)) {
                    throw new IntegritySystemException(MSG_INCONSISTENT_IDS);
                }
            }
            catch (final MissingAttributeValueException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Missing attribute value.", e);
                }
            }
        }
        return element;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     * @throws Exception e
     */
    @Override
    public String characters(final String s, final StartElement element) throws Exception {
        if ("name".equals(element.getLocalName())) {
            if (scope.getName() != null && charactersCounter.get(element.getLocalName()) != null) {
                scope.setName(scope.getName() + s);
            }
            else {
                scope.setName(s);
            }
            charactersCounter.put(element.getLocalName(), 1);
        }
        else if ("type".equals(element.getLocalName())) {
            scope.setScopeType(s);
        }
        return s;
    }

    /**
     * @return the scope
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

}
