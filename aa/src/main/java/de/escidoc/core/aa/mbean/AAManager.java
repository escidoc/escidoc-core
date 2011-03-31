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
package de.escidoc.core.aa.mbean;

import de.escidoc.core.aa.business.UserHandleCleaner;
import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;

/**
 * Managed bean to access values and operations of the authentication and authorization component.
 *
 * @author Torsten Tetteroo
 */
@ManagedResource(objectName = "eSciDocCore:name=AAManager", description = "Manager of the authentication and authorization component.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class AAManager {

    /**
     * The worker to clean up the eSciDoc user handles.
     */
    private UserHandleCleaner userHandleCleaner;

    /**
     * Cleans up the eSciDoc user handles.<br> This delegates to {@link UserHandleCleaner}.cleanUp.
     */
    @ManagedOperation(description = "Clean up the eSciDoc user handles that have been expired.")
    public void cleanUpUserHandles() {

        getUserHandleCleaner().cleanUp();
    }

    /**
     * Removes all objects dstored in the PoliciesCache.<br>
     */
    @ManagedOperation(description = "remove everything from the PoliciesCache.")
    public void clearPoliciesCache() {

        PoliciesCache.clear();
    }

    /**
     * Exposes the clean up period.
     *
     * @return Returns the clean up period taken from the {@link EscidocConfiguration}.
     * @throws IOException Thrown if configuration properties are not available.
     */
    @ManagedAttribute(description = "The clean up period in milli seconds.", persistPeriod = 300)
    public long getCleanUpPeriod() throws IOException {

        return EscidocConfiguration.getInstance().getAsLong("escidoc-core.aa.cleanup.period");
    }

    /**
     * Gets the {@link UserHandleCleaner}.
     *
     * @return Returns the {@link UserHandleCleaner}.
     */
    private UserHandleCleaner getUserHandleCleaner() {
        return this.userHandleCleaner;
    }

    /**
     * Injects the {@link UserHandleCleaner}.
     *
     * @param userHandleCleaner The {@link UserHandleCleaner} to inject.
     */
    public void setUserHandleCleaner(final UserHandleCleaner userHandleCleaner) {
        this.userHandleCleaner = userHandleCleaner;
    }

}
