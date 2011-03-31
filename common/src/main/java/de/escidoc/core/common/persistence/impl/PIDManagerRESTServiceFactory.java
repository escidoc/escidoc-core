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

package de.escidoc.core.common.persistence.impl;

import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.persistence.PIDSystemFactory;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * @author Frank Schwichtenberg
 */
public class PIDManagerRESTServiceFactory extends PIDSystemFactory {

    private PIDSystem pidGenerator;

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDGeneratorFactory#getPIDGenerator()
     */
    @Override
    public PIDSystem getPIDGenerator() throws PidSystemException {
        if (this.pidGenerator == null) {
            getNewInstance();
        }

        return this.pidGenerator;
    }

    /**
     * Create a new instance of PIDGeneratorRESTService and initialize it from escidoc-core.properties. <ul>
     * <li>escidoc-core.PidGenerator.globalPrefix (required)</li> </ul>
     *
     * @throws PidSystemException If the PIDGenerator could not be initialized.
     */
    private void getNewInstance() throws PidSystemException {
        final PIDManagerRESTService pidRestGenerator = new PIDManagerRESTService();
        try {
            final EscidocConfiguration conf = EscidocConfiguration.getInstance();
            String param = conf.get(EscidocConfiguration.ESCIDOC_CORE_PID_SERVICE_HOST);
            if (param != null) {
                pidRestGenerator.setPidGeneratorServer(param);
            }
            param = conf.get(EscidocConfiguration.ESCIDOC_CORE_PID_NAMESPACE);
            if (param != null) {
                pidRestGenerator.setPidNamespace(param);
            }
            param = conf.get(EscidocConfiguration.ESCIDOC_CORE_PID_GLOBALPREFIX);
            if (param != null) {
                pidRestGenerator.setGlobalPrefix(param);
            }
            pidRestGenerator.setLocalPrefix(conf.get(EscidocConfiguration.ESCIDOC_CORE_PID_LOCALPREFIX, ""));
            pidRestGenerator.setSeparator(conf.get(EscidocConfiguration.ESCIDOC_CORE_PID_SEPARATOR, "/"));
        }
        catch (final Exception e) {
            throw new PidSystemException("Can not initialise PID System.", e);
        }
        this.pidGenerator = pidRestGenerator;
    }

}
