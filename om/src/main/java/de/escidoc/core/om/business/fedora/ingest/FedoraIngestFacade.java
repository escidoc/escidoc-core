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

import java.util.HashMap;
import java.util.Map;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.business.interfaces.IngestFacade;
import de.escidoc.core.om.business.interfaces.ValueFormatter;
import static de.escidoc.core.common.business.Constants.INGEST_OBJ_ID;
import static de.escidoc.core.common.business.Constants.INGEST_RESOURCE_TYPE;

/**
 * Thin facade on top of the FedoraIngestHandler. This class separates the
 * Handler from the individual classes used for the ingest and the determination
 * process of the resource type.
 *
 *
 *
 * @spring.bean id="business.IngestFacade" scope="singleton"
 * @spring.property name="resourceMapperDao" ref="business.resourceMapperDao"
 * @spring.property name="formatter" ref="business.ingestReturnValueFormatter"
 *
 * @author KST
 *
 */
public class FedoraIngestFacade implements IngestFacade {

    private ResourceMapperDao resourceMapperDao;

    private ValueFormatter formatter;

    /**
     * @param formatter
     *            the formatter to set
     */
    public void setFormatter(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * @return the formatter
     */
    public ValueFormatter getFormatter() {
        return formatter;
    }

    /**
     * Getter for the ResourceMapperDao
     *
     * @return the resourceMapperDao
     */
    public ResourceMapperDao getResourceMapperDao() {
        return resourceMapperDao;
    }

    /**
     * Setter for the ResourceMapperDao
     *
     * @param resourceMapperDao
     */
    public void setResourceMapperDao(ResourceMapperDao resourceMapperDao) {
        this.resourceMapperDao = resourceMapperDao;
    }

    /**
     * Ingest a given resource and return the id which has been assigned by the
     * framework. Format the return value according to the rule implemented in
     * the injected Formatter instance.
     *
     * @throws EscidocException
     */
    public String ingest(final String xmlData) throws EscidocException {
        ResourceMapperBean bean =
            getResourceMapperDao().getIngestableForResource(xmlData);
        String objectId = bean.getResource().ingest(xmlData);

        Map<String, String> values = new HashMap<String, String>();
        values.put(INGEST_OBJ_ID, objectId);
        values.put(INGEST_RESOURCE_TYPE, bean.getResourceType().toString());

        return getFormatter().format(values);
    }
}
