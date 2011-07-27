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

import org.springframework.stereotype.Service;

/**
 * Singleton which contains all information about a running or finished purging process.
 *
 * @author André Schenk
 */
@Service("admin.PurgeStatus")
public final class PurgeStatus extends AdminMethodStatus {

    private int count;

    /**
     * Private constructor to prevent initialization.
     */
    protected PurgeStatus() {
    }

    /**
     * Decrease the number of resources which still have to be processed.
     */
    public synchronized void dec() {
        this.count--;
        if (this.isFillingComplete() && this.count == 0) {
            finishMethod();
        }
    }

    /**
     * Increase the number of resources which still have to be processed.
     */
    public synchronized void inc() {
        this.count++;
    }

    /**
     * Set a flag to signalize that the queue was completely filled. Now an empty queue would mean the whole process has
     * been finished.
     */
    public void setFillingComplete() {
        this.setFillingComplete(true);
        if (this.count == 0) {
            finishMethod();
        }
    }

    /**
     * Return a string representation of the object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (getCompletionDate() != null) {
            result.append("<message>purging finished at ").append(getCompletionDate()).append("</message>\n");
        }
        else {
            result.append("<message>purging currently running</message>\n");
            result.append("<message>");
            result.append(this.count);
            result.append(" object(s) still to be purged\n");
            result.append("</message>\n");
        }
        return result.toString();
    }
}
