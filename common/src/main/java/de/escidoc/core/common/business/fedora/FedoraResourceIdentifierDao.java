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

package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.persistence.interfaces.ResourceIdentifierDao;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.PidListTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link ResourceIdentifierDao} implementation using the Fedora repository.
 *
 * @author Torsten Tetteroo
 */
@Service("escidoc.core.business.FedoraResourceIdentifierDao")
public class FedoraResourceIdentifierDao implements ResourceIdentifierDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FedoraResourceIdentifierDao.class);

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    /**
     * See Interface for functional description.
     */
    @Override
    public List<String> getNextPids(final int noOfPids) throws FedoraSystemException {
        List<String> returnValue = new ArrayList<String>(0);
        Future<PidListTO> futurePIDList = fedoraServiceClient.getNextPIDAsync("escidoc", noOfPids);
        PidListTO pidListTO = null;
        try {
            pidListTO = futurePIDList.get(10, TimeUnit.SECONDS);
            returnValue = pidListTO.getPid();
        }
        catch (InterruptedException e) {
            LOGGER.warn("Interrupted exception on requesting PIDs.", e);
        }
        catch (ExecutionException e) {
            LOGGER.warn("Execution exception on requesting PIDs.", e);
        }
        catch (TimeoutException e) {
            LOGGER.warn("Timeout on requesting PIDs.", e);
        }
        return returnValue;
    }

}
