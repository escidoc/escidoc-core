/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.common.business.fedora.resources;

import de.escidoc.core.common.business.Constants;

/**
 * Enumeration to describe all types of resources.
 *
 * @author André Schenk
 */
public enum ResourceType {
    CONTAINER("container", Constants.CONTAINER_OBJECT_TYPE), CONTEXT("context", Constants.CONTEXT_OBJECT_TYPE), ITEM(
        "item", Constants.ITEM_OBJECT_TYPE), OU("organizational-unit", Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE), COMPONENT(
        "component", Constants.COMPONENT_OBJECT_TYPE), CONTENT_MODEL("content-model",
        Constants.CONTENT_MODEL_OBJECT_TYPE), CONTENT_RELATION("content-relation",
        Constants.CONTENT_RELATION2_OBJECT_TYPE);

    private final String label;

    private final String uri;

    /**
     * Create a new object.
     *
     * @param label object label
     * @param uri   object type URI
     */
    ResourceType(final String label, final String uri) {
        this.label = label;
        this.uri = uri;
    }

    /**
     * Get the corresponding ResourceType object from the given object type URI.
     *
     * @param uri object type URI
     * @return corresponding ResourceType object
     */
    public static ResourceType getResourceTypeFromUri(final String uri) {
        ResourceType result = null;

        for (final ResourceType resourceType : ResourceType.values()) {
            if (resourceType.uri.equals(uri)) {
                result = resourceType;
                break;
            }
        }
        return result;
    }

    /**
     * Get the label of the object.
     *
     * @return object label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Get the URI of the object.
     *
     * @return object URI
     */
    public String getUri() {
        return this.uri;
    }
}
