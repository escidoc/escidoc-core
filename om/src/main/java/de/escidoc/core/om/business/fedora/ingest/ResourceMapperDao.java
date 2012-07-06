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

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidResourceException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the handlers of the respective resources. It is configured via Spring and contains a List of
 * ResourceMapperBean instances, each consisting of an enum and its handler instance.
 *
 * @author Kai Strnad
 */
public class ResourceMapperDao {

    private List<ResourceMapperBean> resourceMappers = new ArrayList<ResourceMapperBean>();

    /**
     * Getter for the ResourceMapper beans
     *
     * @return Returns the list of resourceMappers
     */
    public List<ResourceMapperBean> getResourceMappers() {
        return this.resourceMappers;
    }

    /**
     * Set the resourceMappers (see spring-beans.xml for configuration).
     * @param resourceMappers
     */
    public void setResourceMappers(final List<ResourceMapperBean> resourceMappers) {
        this.resourceMappers = resourceMappers;
    }

    /**
     * Returns the IngestableResource upon the first match.
     *
     * @param xmlData
     * @return the IngestableResource responsible for the given resource.
     * @throws de.escidoc.core.common.exceptions.EscidocException
     */
    public ResourceMapperBean getIngestableForResource(final String xmlData) throws EscidocException {
        final StringBuilder exceptions = new StringBuilder();
        for (final ResourceMapperBean bean : getResourceMappers()) {
            try {
                if (bean.getValidator().isResourceValid(xmlData, bean.getResourceType())) {
                    return bean;
                }
            }
            catch (final InvalidResourceException e) {
                // possible smell here. how to better communicate exceptions in
                // this case ?
                exceptions.append("Not a valid ").append(bean.getResourceType()).append(" : ").append(e).append('\n');
            }

        }
        // for valid resources this code should never be reached
        throw new InvalidResourceException("The given resource is invalid. It cannot be validated against any schema: "
            + exceptions);
    }

}
