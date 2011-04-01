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

package de.escidoc.core.om.business.fedora.ingest;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.om.business.interfaces.IngestValidator;

/**
 * This bean encapsulates resources necessary for the mapping of Resource Types, their respective handlers and
 * validators.
 *
 * @author Kai Strnad
 */
public class ResourceMapperBean {

    private ResourceType resourceType;

    private IngestableResource resource;

    private IngestValidator validator;

    /**
     * Constructor for the ResourceMapperBean.
     * @param resourceType
     * @param resource
     * @param validator
     */
    public ResourceMapperBean(final ResourceType resourceType, final IngestableResource resource,
        final IngestValidator validator) {
        this.resourceType = resourceType;
        this.resource = resource;
        this.validator = validator;
    }

    /**
     * Default Constructor.
     */
    public ResourceMapperBean() {

    }

    /**
     * Getter for the IngestValidator.
     *
     * @return the validator
     */
    public IngestValidator getValidator() {
        return this.validator;
    }

    /**
     * Setter for the IngestValidator.
     * @param validator
     */
    public void setValidator(final IngestValidator validator) {
        this.validator = validator;
    }

    /**
     * Getter for the ResourceType.
     *
     * @return ResourceType
     */
    public ResourceType getResourceType() {
        return this.resourceType;
    }

    /**
     * Setter for the ResourceType.
     * @param resourceType
     */
    public void setResourceType(final ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Getter for the concrete instance ingesting the resource.
     *
     * @return the instance.
     */
    public IngestableResource getResource() {
        return this.resource;
    }

    /**
     * Setter for the concrete instance ingesting the resource.
     * @param resource
     */
    public void setResource(final IngestableResource resource) {
        this.resource = resource;
    }

}
