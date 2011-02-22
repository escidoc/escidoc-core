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

/**
 * Fedora Ingest Handler. Takes any resource, automatically determines its type
 * and tries to ingest.
 * 
 * @spring.bean id="business.FedoraIngestHandler" scope="prototype"
 * @spring.property name="ingestFacade" ref="business.IngestFacade"
 * 
 * @author SWA,KST
 * 
 * @om
 */

public class FedoraIngestHandler implements IngestHandlerInterface {

    private IngestFacade ingestFacade;

    /**
     * Setter for the ingest facade.
     * 
     * @param facade
     *            Facade for ingest.
     */
    public void setIngestFacade(final IngestFacade facade) {
        this.ingestFacade = facade;
    }

    /**
     * Getter for the ingest facade.
     * 
     * @return ingestFacade the ingestFacade that has been injected
     */
    public IngestFacade getIngestFacade() {
        return ingestFacade;
    }

    /**
     * Ingest a resource.
     * 
     * @param xmlData
     *            The string that contains the resource
     * @return XML structure including objid and resource type.
     * @throws EscidocException
     *             Thrown if XML representation fulfills not all requirements or
     *             internal errors occur.
     * @see de.escidoc.core.om.business.interfaces.IngestHandlerInterface#ingest(java.lang.String)
     */
    @Override
    public String ingest(final String xmlData) throws EscidocException {
        return ingestFacade.ingest(xmlData);
    }
}
