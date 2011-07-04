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
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Stax handler that manages the scope definition of a role.
 *
 * @author Torsten Tetteroo
 */
public class ScopeStaxHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeStaxHandler.class);

    private static final String ROLE_SCOPE_DEF_RESOURCE_TYPE_NAME = "resource-type";

    private static final String ROLE_SCOPE_DEF_RELATION_ATTRIBUTE_ID_NAME = "relation-attribute-id";

    private static final String ROLE_SCOPE_DEF_RELATION_ATTRIBUTE_OBJECT_TYPE_NAME = "relation-attribute-object-type";

    private static final String ROLE_SCOPE_DEF_NAME = "scope-def";

    private static final String BASE_PATH = "/role/scope";

    private static final String ROLE_SCOPE_DEF_PATH = BASE_PATH + '/' + ROLE_SCOPE_DEF_NAME;

    private boolean unlimited = true;

    private final List<String> scopeDefResourceTypes = new ArrayList<String>();

    private final List<String> scopeDefAttributeIds = new ArrayList<String>();

    private final List<String> scopeDefAttributeObjectTypes = new ArrayList<String>();

    private final EscidocRole role;

    /**
     * The constructor.
     *
     * @param role The role to handle.
     */
    public ScopeStaxHandler(final EscidocRole role) {
        this.role = role;
    }

    /**
     * See Interface for functional description.
     *
     * @throws SystemException Thrown in case of an internal system error.
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws SystemException {

        if (isNotReady()) {
            final String currentPath = element.getPath();
            if (ROLE_SCOPE_DEF_PATH.equals(currentPath)) {
                try {
                    scopeDefResourceTypes.add(element.getAttributeValue(null, ROLE_SCOPE_DEF_RESOURCE_TYPE_NAME));
                }
                catch (final NoSuchAttributeException e) {
                    throw createMandatoryAttributeNotFoundException(element, null, ROLE_SCOPE_DEF_RESOURCE_TYPE_NAME, e);
                }
                String scopeDefAttributeId = null;
                try {
                    scopeDefAttributeId = element.getAttributeValue(null, ROLE_SCOPE_DEF_RELATION_ATTRIBUTE_ID_NAME);
                    scopeDefAttributeIds.add(scopeDefAttributeId);
                }
                catch (final NoSuchAttributeException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error on getting attribute.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error on getting attribute.", e);
                    }
                    scopeDefAttributeIds.add(null);
                }
                try {
                    scopeDefAttributeObjectTypes.add(element.getAttributeValue(null,
                        ROLE_SCOPE_DEF_RELATION_ATTRIBUTE_OBJECT_TYPE_NAME));
                }
                catch (final NoSuchAttributeException e) {
                    if (scopeDefAttributeId != null) {
                        throw createMandatoryAttributeNotFoundException(element, null,
                            ROLE_SCOPE_DEF_RELATION_ATTRIBUTE_OBJECT_TYPE_NAME, e);
                    }
                    scopeDefAttributeObjectTypes.add(null);
                }
                this.unlimited = false;
            }
        }

        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) throws Exception {

        if (isNotReady()) {
            final String currentPath = element.getPath();
            if (BASE_PATH.equals(currentPath)) {

                final Collection<ScopeDef> scopeDefs = role.getScopeDefs();

                // remove old scope defs
                scopeDefs.clear();

                // add new scope defs
                final Iterator<String> resourceTypesIterator = scopeDefResourceTypes.iterator();
                final Iterator<String> attributeIdsIterator = scopeDefAttributeIds.iterator();
                final Iterator<String> attributeObjectTypesIterator = scopeDefAttributeObjectTypes.iterator();
                while (resourceTypesIterator.hasNext()) {
                    final String resourceType = resourceTypesIterator.next();
                    final String attributeId = attributeIdsIterator.next();
                    final String attributeObjectType = attributeObjectTypesIterator.next();
                    final ScopeDef scopeDef = new ScopeDef(resourceType, attributeId, attributeObjectType, this.role);
                    scopeDefs.add(scopeDef);
                }

                setReady();
            }
        }
        return element;
    }

    /**
     * Gets the unlimited flag.
     *
     * @return Returns {@code true}, if a unlimited role has been parsed, {@code false} else.
     */
    public boolean isUnlimited() {

        return this.unlimited;
    }

    /**
     * Gets the attribute id definitions of the scope defs.
     *
     * @return Returns the attribute id definitions of the scope defs. If no scope definitions have been found, an empty
     *         {@code List} is returned.
     */
    public List<String> getScopeDefAttributeIds() {
        return this.scopeDefAttributeIds;
    }

    /**
     * Gets the resource type definitions of the scope defs.
     *
     * @return Returns the resource type definitions of the scope defs. If no scope definitions have been found, an
     *         empty {@code List} is returned.
     */
    public List<String> getScopeDefResourceTypes() {
        return this.scopeDefResourceTypes;
    }
}
