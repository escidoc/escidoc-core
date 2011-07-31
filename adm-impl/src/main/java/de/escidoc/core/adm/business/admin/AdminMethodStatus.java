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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.business.admin;

import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Basic class for all singletons which contain all information about a running or finished process.
 *
 * @author André Schenk
 */
public abstract class AdminMethodStatus {

    private Date completionDate = new Date();

    private boolean fillingComplete;

    private final Semaphore semaphore = new Semaphore(1);

    protected boolean isFillingComplete() {
        return this.fillingComplete;
    }

    protected void setFillingComplete(final boolean fillingComplete) {
        this.fillingComplete = fillingComplete;
    }

    /**
     * This method must be called if the admin method has been finished.
     */
    public void finishMethod() {
        this.completionDate = new Date();
        semaphore.release();
    }

    /**
     * Get the completion date of this process or null, if the process is still running.
     *
     * @return completion date
     */
    public Date getCompletionDate() {
        return this.completionDate;
    }

    /**
     * Start a new admin method. The return value is true if the is no other process running the same method at the
     * moment.
     *
     * @return true if the method is allowed to be started
     */
    public boolean startMethod() {
        boolean result = false;

        if (semaphore.tryAcquire()) {
            this.completionDate = null;
            this.fillingComplete = false;
            result = true;
        }
        return result;
    }
}
