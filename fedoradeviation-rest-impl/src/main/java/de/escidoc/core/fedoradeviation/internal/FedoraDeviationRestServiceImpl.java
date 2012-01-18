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
package de.escidoc.core.fedoradeviation.internal;

import java.util.HashMap;
import java.util.Map;

import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.fedoradeviation.FedoraDeviationRestService;
import de.escidoc.core.om.service.interfaces.FedoraDescribeDeviationHandlerInterface;
import de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface;

/**
 * @author Michael Hoppe
 *
 */
public class FedoraDeviationRestServiceImpl implements FedoraDeviationRestService {

    @Autowired
    @Qualifier("service.FedoraDescribeDeviationHandler")
    private FedoraDescribeDeviationHandlerInterface fedoraDescribeDeviationHandler;

    @Autowired
    @Qualifier("service.FedoraRestDeviationHandler")
    private FedoraRestDeviationHandlerInterface fedoraRestDeviationHandler;

    /**
     * 
     */
    public FedoraDeviationRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.fedoradeviation.FedoraDeviationRestService#export(java.lang.String, java.util.Map)
     */
    @Override
    public String export(final String id) throws SystemException {
        return fedoraRestDeviationHandler.export(id, new HashMap<String, String[]>());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.fedoradeviation.FedoraDeviationRestService#getDatastreamDissemination(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public EscidocBinaryContent getDatastreamDissemination(final String id, final String dsId)
        throws SystemException {
        return fedoraRestDeviationHandler.getDatastreamDissemination(id, dsId, new HashMap<String, String[]>());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.fedoradeviation.FedoraDeviationRestService#getFedoraDescription(java.lang.String)
     */
    @Override
    public String getFedoraDescription(final String xml) throws Exception {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("xml", new String[] {xml});
        return fedoraDescribeDeviationHandler.getFedoraDescription(parameters);
    }

}
