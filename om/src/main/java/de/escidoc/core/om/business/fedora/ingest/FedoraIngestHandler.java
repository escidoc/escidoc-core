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
import de.escidoc.core.om.business.interfaces.IngestFacade;
import de.escidoc.core.om.business.interfaces.IngestHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Fedora Ingest Handler. Takes any resource, automatically determines its type and tries to ingest.
 *
 * @author Steffen Wagner,KST
 */
@Service("business.FedoraIngestHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FedoraIngestHandler implements IngestHandlerInterface {

    @Autowired
    @Qualifier("business.IngestFacade")
    private IngestFacade ingestFacade;

    /**
     * Private constructor to prevent initialization.
     */
    protected FedoraIngestHandler() {
    }

    /**
     * Getter for the ingest facade.
     *
     * @return ingestFacade the ingestFacade that has been injected
     */
    public IngestFacade getIngestFacade() {
        return this.ingestFacade;
    }

    /**
     * Ingest a resource.
     *
     * @param xmlData The string that contains the resource
     * @return XML structure including objid and resource type.
     * @throws EscidocException Thrown if XML representation fulfills not all requirements or internal errors occur.
     */
    @Override
    public String ingest(final String xmlData) throws EscidocException {
        return ingestFacade.ingest(xmlData);
    }
}
