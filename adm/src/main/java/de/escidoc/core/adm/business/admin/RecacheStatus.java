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

import java.util.Map;

import de.escidoc.core.common.business.fedora.resources.ResourceType;

public final class RecacheStatus extends AdminMethodStatus {

    private static final RecacheStatus instance = new RecacheStatus();

    private RecacheStatus() {
    }

    public static RecacheStatus getInstance() {
        return instance;
    }

    public synchronized void inc(final ResourceType type) {
        Integer oldValue = get(type);

        if (oldValue != null) {
            put(type, oldValue + 1);
        }
        else {
            put(type, 1);
        }
    }

    public synchronized void dec(final ResourceType type) {
        Integer oldValue = get(type);

        if (oldValue != null) {
            if (oldValue == 1) {
                remove(type);
            }
            else {
                put(type, oldValue - 1);
            }
        }
        if (this.isFillingComplete() && (size() == 0)) {
            finishMethod();
        }
    }

    /**
     * Return a string representation of the object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (getCompletionDate() != null) {
            result.append("<message>recaching finished at ").append(getCompletionDate()).append("</message>\n");
        }
        else {
            result.append("<message>recaching currently running</message>\n");
            for (Map.Entry<ResourceType, Integer> e : entrySet()) {
                result.append("<message>\n");
                result.append(e.getValue());
                result.append(' ');
                result.append(e.getKey().getLabel());
                result.append("(s) still to be recached\n");
                result.append("</message>\n");
            }
        }
        return result.toString();
    }
}
