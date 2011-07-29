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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.st.mbean;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.st.business.StagingCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

/**
 * Managed bean for the staging file area. This should be exposed (as a mbean) to a JMX server.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.st.mbean.StagingManager")
@ManagedResource(objectName = "eSciDocCore:name=StagingManager", description = "Manager of the staging file area.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class StagingManager {

    @Autowired
    @Qualifier("st.StagingCleaner")
    private StagingCleaner stagingCleaner;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected StagingManager() {
    }

    /**
     * Cleans up the staging file area.<br> This delegates to {@link StagingCleaner}.cleanUp.
     */
    @ManagedOperation(description = "Clean up the staging file area.")
    public void cleanUp() {
        stagingCleaner.cleanUp();
    }

    /**
     * Exposes the clean up period.
     *
     * @return Returns the clean up period taken from the {@link EscidocConfiguration}.
     * @throws IOException Thrown if configuration properties are not available.
     */
    @ManagedAttribute(description = "The clean up period in milli seconds.", persistPeriod = 300)
    public long getCleanUpPeriod() {
        return EscidocConfiguration.getInstance().getAsLong("escidoc-core.st.cleanup.period");
    }
}
