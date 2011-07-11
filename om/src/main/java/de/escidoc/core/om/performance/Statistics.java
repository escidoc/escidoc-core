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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.om.performance;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Acts as collector for execution times for measured methods. Gets called by the advisor configured via spring aop.
 * <p/>
 * This class is exposed via JMX where statistics can be viewed and reinitialized.
 *
 * @author Kai Strnad
 */
@Service("performance.statistics")
@ManagedResource(objectName = "eSciDocCore:name=PerformanceStatistics", description = "Obtains and stores method execution times of desired operations.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class Statistics {

    private Map<String, SummaryStatistics> statisticsMap;

    // cutoff number for the amount of measurements per class allowed.
    private int maxValues = 5000;

    /**
     * Create new Statistics. Ensure that the Map holding all measurements is created.
     */
    public Statistics() {
        this.statisticsMap = new HashMap<String, SummaryStatistics>();
    }

    /**
     * @param key   the name of package.class.method
     * @param value the execution time of the method
     */
    public void addValueToStatistics(final String key, final long value) {
        final SummaryStatistics statistics = getStatistics(key);
        statistics.addValue((double) value);
    }

    /**
     * @param key the name of package.class.method
     * @return the Statistics of the method
     */
    private SummaryStatistics getStatistics(final String key) {
        SummaryStatistics statistics = statisticsMap.get(key);
        if (statistics == null || statistics.getN() >= (long) this.maxValues) {
            statistics = new SummaryStatistics();
            statisticsMap.put(key, statistics);
        }
        return statistics;
    }

    /**
     * Sets the maximum of values allowed per class.
     *
     * @param values max values to be stored
     */
    @ManagedAttribute(description = "Sets the maximum of values allowed per class (default: 5000)")
    public void setMaxValues(final int values) {
        if (values > 0) {
            this.maxValues = values;
        }
    }

    @ManagedAttribute(description = "Gets the maximum of values allowed per class")
    public int getMaxValues() {
        return this.maxValues;
    }

    /**
     * @return the statistics of all measured methods.
     */
    @ManagedAttribute(description = "Get all currently available statistics")
    public String getKeys() {
        final StringBuilder b = new StringBuilder();
        for (final String key : this.statisticsMap.keySet()) {
            final SummaryStatistics s = getStatistics(key);
            if (s != null) {
                b.append(key).append(", #:").append(s.getN()).append(", min (ms):").append((long) s.getMin()).append(
                    ", max (ms):").append((long) s.getMax()).append(", mean (ms):").append((long) s.getMean()).append(
                    ", stddev (ms):").append((long) s.getStandardDeviation()).append(", total (ms):").append(
                    (long) s.getSum()).append('\n');
            }
        }
        return b.toString();
    }

    /**
     * resets all statistics.
     */
    @ManagedOperation(description = "Delete all currently available statistics")
    public void resetStatistics() {
        this.statisticsMap = new HashMap<String, SummaryStatistics>();
    }
}
