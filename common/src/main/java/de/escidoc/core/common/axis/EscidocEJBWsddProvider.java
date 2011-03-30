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

package de.escidoc.core.common.axis;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;

/**
 * WSDDProvider providing the EscidocEjbProvider.<br>
 * This implementation extends org.apache.axis.deployment.wsdd.WSDDProvider. The
 * name of the provider is <code>EscidocEJB</code>
 * 
 * @author Torsten Tetteroo
 *
 */
public class EscidocEJBWsddProvider extends WSDDProvider {

    private static final String NAME = "EscidocEJB";



    /**
     * See Interface for functional description.
     * 
     * @return
     */
    @Override
    public String getName() {

        return NAME;
    }

    /**
     * See Interface for functional description.
     * 
     * @param wsddservice
     * @param engineconfiguration
     * @return
     * @throws Exception
     */
    @Override
    public Handler newProviderInstance(
        final WSDDService wsddservice,
        final EngineConfiguration engineconfiguration) throws Exception {

        return new EscidocEjbProvider();
    }


}
