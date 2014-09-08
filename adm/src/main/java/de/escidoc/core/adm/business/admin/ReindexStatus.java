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

import de.escidoc.core.common.business.fedora.resources.ResourceType;

/**
 * Singleton which contains all information about a running or finished reindexing process.
 *
 * @author Michael Hoppe
 */
public final class ReindexStatus extends AdminMethodStatus {

    /**
     * Singleton instance.
     */
    private static final ReindexStatus instance = new ReindexStatus();

    /**
     * Create a new ReindexStatus object.
     */
    private ReindexStatus() {
    }

    /**
     * Decrease the number of resources of the given type which still have to be processed.
     *
     * @param type resource type
     */
    public synchronized void dec(final ResourceType type) {
        final Integer oldValue = get(type);

        if (oldValue != null) {
            if (oldValue == 1) {
                remove(type);
            }
            else {
                treeMap.put(type, oldValue - 1);
            }
        }
        if (this.isFillingComplete() && size() == 0) {
            finishMethod();
        }
    }

    /**
     * Get the singleton instance.
     *
     * @return ReindexStatus singleton
     */
    public synchronized static ReindexStatus getInstance() {
        if (instance == null)
            return new ReindexStatus();
        else
            return instance;
    }

    /**
     * Increase the number of resources of the given type which still have to be processed.
     *
     * @param type resource type
     */
    public synchronized void inc(final ResourceType type) {
        final Integer oldValue = get(type);

        if (oldValue != null) {
            treeMap.put(type, oldValue + 1);
        }
        else {
            treeMap.put(type, 1);
        }
    }

    /**
     * Set a flag to signalize that the queue was completely filled. Now an empty queue would mean the whole process has
     * been finished.
     */
    public void setFillingComplete() {
        this.setFillingComplete(true);
        if (size() == 0) {
            finishMethod();
        }
    }

    /**
     * Store the total number of objects to reindex
     */
    public void setTotalNumberOfObjects(Integer number, ResourceType type) {
        this.startMap.put(type, number);
    }

    /**
     * Get the total number of objects to reindex for the specified ResourceType
     */
    public long getStartNumberOfObjects(ResourceType type) {
        return this.startMap.get(type).longValue();
    }

    /**
     * Get the total number of objects to reindex for the specified ResourceType
     */
    public long getTotalNumberOfObjects() {
        long l = 0;

        for (final Entry<ResourceType, Integer> e : entrySet()) {

            l = l + e.getValue();
        }

        return l;
    }

    /**
     * Return a string representation of the object.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (getCompletionDate() != null) {
            long timeUsed = (getCompletionDate().getTime() - getStartDate().getTime()) / 1000;

            result.append("<message>");
            result.append("reindexing finished at ").append(getCompletionDate());

            result.append(" ---- time used: "
                + String.format("%d h : %02d m : %02d s", timeUsed / 3600, (timeUsed % 3600) / 60, (timeUsed % 60)));

            /*if (entrySet().size() == 1) {
                for (final Entry<ResourceType, Integer> e : entrySet()) {
                    result.append(" ---- average: " + getStartNumberOfObjects(e.getKey()) / timeUsed * 3600
                        + " objects / h");
                }
            }*/
            result.append("</message>\n");
        }
        else {
            result.append("<message>reindexing currently running</message>\n");
            long timeUsed = (System.currentTimeMillis() - getStartDate().getTime()) / 1000;
            for (final Entry<ResourceType, Integer> e : entrySet()) {
                result.append("<message>\n");
                result.append(e.getValue());
                result.append(' ');
                result.append(e.getKey().getLabel());
                result.append("(s) still to be reindexed\n");

                /*if (entrySet().size() == 1) {
                    result.append(" --- running with "
                        + ((getStartNumberOfObjects(e.getKey()) - e.getValue().longValue()) * 60 * 60 / timeUsed)
                        + " objects / h ");
                    result.append(" --- expected end at "
                        + (new Date(System.currentTimeMillis() + timeUsed * 1000 * e.getValue().longValue()
                            / (getStartNumberOfObjects(e.getKey()) - e.getValue().longValue() + 1))).toString());

                }*/
                result.append("</message>\n");
            }
        }
        return result.toString();
    }

}
